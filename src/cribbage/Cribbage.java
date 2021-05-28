package cribbage;

// Cribbage.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import cribbage.Log.LogManager;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cribbage extends CardGame {
	static Cribbage cribbage;	// Provide access to singleton

	/** An enum for representing the phase of the game */
	public enum GamePhase { SETUP, START, PLAY, PLAY_SCORE, PLAY_SCORE_GO, SHOW, SHOW_SCORE}
	public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
	public enum Rank {
		// Order of cards is tied to card images
		ACE(1,1), KING(13,10), QUEEN(12,10), JACK(11,10), TEN(10,10), NINE(9,9), EIGHT(8,8), SEVEN(7,7), SIX(6,6), FIVE(5,5), FOUR(4,4), THREE(3,3), TWO(2,2);
		public final int order;
		public final int value;
		Rank(int order, int value) {
			this.order = order;
			this.value = value;
		}
	}

	/** A class for keeping track of game information */
	public class GameInformation {
		int currentPlayer = 0;
		GamePhase currentGamePhase = GamePhase.PLAY;
		Hand currentHand = null;

		public int getCurrentPlayerScore() {
			return scores[currentPlayer];
		}
		public int getCurrentPlayer() {
			return currentPlayer;
		}

		/** @return The current phase of the game */
		public GamePhase getGamePhase() {
			return currentGamePhase;
		}
		void updateGamePhase(GamePhase phase) {
			currentGamePhase = phase;
		}

		public Hand getCurrentHand() {
			return currentHand;
		}

		void setCurrentHand(Hand currentHand) {
			this.currentHand = currentHand;
		}
	}

	public static int cardValue(Card c) { return ((Cribbage.Rank) c.getRank()).value; }


	/*
	Canonical String representations of Suit, Rank, Card, and Hand
	*/
	public String canonical(Suit s) { return s.toString().substring(0, 1); }

	public String canonical(Rank r) {
		switch (r) {
			case ACE:case KING:case QUEEN:case JACK:case TEN:
				return r.toString().substring(0, 1);
			default:
				return String.valueOf(r.value);
		}
	}

	public String canonical(Card c) { return canonical((Rank) c.getRank()) + canonical((Suit) c.getSuit()); }

	public String canonical(ArrayList<Card> cardList) {
		String str = "[";
		for (int i = 0; i < cardList.size(); i++) {
			str += Cribbage.getInstance().canonical(cardList.get(i));
			if (i < cardList.size()-1) str += ",";
		}
		str += "]";
		return str;
	}

	public String canonical(Hand h) {
		Hand h1 = new Hand(deck); // Clone to sort without changing the original hand
		for (Card C: h.getCardList()) h1.insert(C.getSuit(), C.getRank(), false);
		h1.sort(Hand.SortType.POINTPRIORITY, false);
		return "[" + h1.getCardList().stream().map(this::canonical).collect(Collectors.joining(",")) + "]";
	}

	class MyCardValues implements Deck.CardValues { // Need to generate a unique value for every card
		public int[] values(Enum suit) {	// Returns the value for each card in the suit
			return Stream.of(Rank.values()).mapToInt(r -> (((Rank) r).order-1)*(Suit.values().length)+suit.ordinal()).toArray();
		}
	}

	static Random random;

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
		int x = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	static boolean ANIMATE;

	void transfer(Card c, Hand h) {
		if (ANIMATE) {
			c.transfer(h, true);
		} else {
			c.removeFromHand(true);
			h.insert(c, true);
		}
	}

	private void dealingOut(Hand pack, Hand[] hands) {
		for (int i = 0; i < nStartCards; i++) {
			for (int j=0; j < nPlayers; j++) {
				Card dealt = randomCard(pack);
				dealt.setVerso(false);	// Show the face
				transfer(dealt, hands[j]);
			}
		}
	}

	static int SEED;

	public static Card randomCard(Hand hand){
		int x = random.nextInt(hand.getNumberOfCards());
		return hand.get(x);
	}

	private GameInformation gameInfo = new GameInformation();
	private final String version = "0.1";

	static public final int nPlayers = 2;
	public final int nStartCards = 6;
	public final int nDiscards = 2;
	private final int handWidth = 400;
	private final int cribWidth = 150;
	private final int segmentWidth = 180;

	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover", new MyCardValues());
	private final Location[] handLocations = {
			new Location(360, 75),
			new Location(360, 625)
	};
	private final Location[] scoreLocations = {
			new Location(590, 25),
			new Location(590, 675)
	};
	private final Location[] segmentLocations = {	// need at most three as 3x31=93 > 2x4x10=80
			new Location(150, 350),
			new Location(400, 350),
			new Location(650, 350)
	};

	private final Location starterLocation = new Location(50, 625);
	private final Location cribLocation = new Location(700, 625);
	private final Location seedLocation = new Location(5, 25);
	// private final TargetArea cribTarget = new TargetArea(cribLocation, CardOrientation.NORTH, 1, true);
	private final Actor[] scoreActors = {null, null}; //, null, null };
	private final Location textLocation = new Location(350, 450);
	private final Hand[] hands = new Hand[nPlayers];
	private final Hand[] startHands = new Hand[nPlayers];
	private Hand starter;
	private Hand crib;

	public static void setStatus(String string) { cribbage.setStatusText(string); }

	private final LogManager logManager = new LogManager(this);
	private static final IPlayer[] players = new IPlayer[nPlayers];
	private final int[] scores = new int[nPlayers];
	private static final String[] playerTypes = new String[nPlayers];
	private final ArrayList<ArrayList<Card>> playerDiscards = new ArrayList<>();


	final Font normalFont = new Font("Serif", Font.BOLD, 24);
	final Font bigFont = new Font("Serif", Font.BOLD, 36);

	private void initScoreDisplay() {
		for (int i = 0; i < nPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	private void updateScoreDisplay(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}





	private void deal(Hand pack, Hand[] hands) {
		for (int i = 0; i < nPlayers; i++) {
			hands[i] = new Hand(deck);
			// players[i] = (1 == i ? new HumanPlayer() : new RandomPlayer());
			players[i].setId(i);
			players[i].startSegment(deck, hands[i]);
		}
		RowLayout[] layouts = new RowLayout[nPlayers];
		for (int i = 0; i < nPlayers; i++)
		{
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(0);
			// layouts[i].setStepDelay(10);
			hands[i].setView(this, layouts[i]);
			hands[i].draw();
		}
		layouts[0].setStepDelay(0);

		dealingOut(pack, hands);
		for (int i = 0; i < nPlayers; i++) {
			hands[i].sort(Hand.SortType.POINTPRIORITY, true);
		}
		layouts[0].setStepDelay(0);
	}

	private void discardToCrib() {
		crib = new Hand(deck);
		RowLayout layout = new RowLayout(cribLocation, cribWidth);
		layout.setRotationAngle(0);
		crib.setView(this, layout);
		// crib.setTargetArea(cribTarget);
		crib.draw();

		for (IPlayer player : players) {
			// process the discards for each player
			ArrayList<Card> discards = new ArrayList<>();
			for (int j = 0; j < nDiscards; j++) {
				Card discard = player.discard();
				transfer(discard, crib);
				discards.add(discard);
			}
			// Add the discards to the playDiscards array list
			playerDiscards.add(sortCardList(discards, Hand.SortType.POINTPRIORITY));
			crib.sort(Hand.SortType.POINTPRIORITY, true);
		}
	}

	private void starter(Hand pack) {
		starter = new Hand(deck);	// if starter is a Jack, the dealer gets 2 points
		RowLayout layout = new RowLayout(starterLocation, 0);
		layout.setRotationAngle(0);
		starter.setView(this, layout);
		starter.draw();
		Card dealt = randomCard(pack);
		dealt.setVerso(false);
		transfer(dealt, starter);

		// Copy the initial hands of players so that scoring can be done during show, and add the starter card to them
		for (int i = 0; i < hands.length; i++) {
			startHands[i] = copyHand(hands[i]);
			startHands[i].insert(starter.getFirst().getSuit(), starter.getFirst().getRank(), false);
		}

		// Scoring
		// If the starter card is a Jack, the dealer gets points
		addToPlayerScore(1, ScorerCompositeFactory.getInstance().getScorerComposite().evaluate(starter));
	}

	// This method was changed to a static method, since it does not rely on any instance variables, and other classes can
	// make use of it
	public static int total(Hand hand) {
		int total = 0;
		for (Card c: hand.getCardList()) total += cardValue(c);
		return total;
	}

	class Segment {
		Hand segment;
		boolean go;
		int lastPlayer;
		boolean newSegment;

		void reset(final List<Hand> segments) {
			segment = new Hand(deck);
			segment.setView(Cribbage.this, new RowLayout(segmentLocations[segments.size()], segmentWidth));
			segment.draw();
			go = false;				// No-one has said "go" yet
			lastPlayer = -1;	 // No-one has played a card yet in this segment
			newSegment = false;	// Not ready for new segment yet
		}
	}

	private Segment s = new Segment();

	public int getCurrentSegmentTotal() {
		return total(s.segment);
	}

	public Card getLastPlayedCard() {
		ArrayList<Card> cards = s.segment.getCardList();
		return cards.get(cards.size()-1);
	}

	private void play() {
		final int thirtyone = 31;
		List<Hand> segments = new ArrayList<>();
		gameInfo.currentPlayer = 0; // Player 1 is dealer
		s.reset(segments);
		Scorer scorer;
		while (!(players[0].emptyHand() && players[1].emptyHand())) {
			Card nextCard = players[gameInfo.currentPlayer].lay(thirtyone-total(s.segment));
			if (nextCard == null) {
				if (s.go) {
					// Another "go" after previous one with no intervening cards
					// lastPlayer gets 1 point for a "go"
					s.newSegment = true;

					// ----> we're in GO phase
					gameInfo.updateGamePhase(GamePhase.PLAY_SCORE_GO);
					// Get scorer and calculate score for current player
					scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
					int score = scorer.evaluate(hands[s.lastPlayer]);
					// log the go score event
					logManager.update();
					// add to current player's score
					// Since neither player can play another card without going over the limit, the last player who
					// played a card gets a point(s) for "go"
					addToPlayerScore(s.lastPlayer, score);
					scorer.clearCache();
				} else {
					// currentPlayer says "go"
					s.go = true;
				}
				gameInfo.currentPlayer = (gameInfo.currentPlayer+1) % 2;
			} else {
				// nextCard not null
				// ----> we're in PLAY phase
				gameInfo.updateGamePhase(GamePhase.PLAY);
				// get the last player to play a card in this segment
				s.lastPlayer = gameInfo.currentPlayer;
				// take their chosen card from their hand
				transfer(nextCard, s.segment);
				// log the play event
				logManager.update();

				// Scoring
				// ----> we're in PLAY_SCORE phase
				gameInfo.updateGamePhase(GamePhase.PLAY_SCORE);
				// Get the scorer
				scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
				// score the player's action above
				int score = scorer.evaluate(s.segment);
				// log any scores awarded to the player
				logManager.update();
				// add the total scores awarded to the player in this PLAY_SCORE phase
				addToPlayerScore(s.lastPlayer, score);
				scorer.clearCache();

				if (players[0].emptyHand() && players[1].emptyHand()) {
					// ----> we're in GO phase due to the last card being played
					gameInfo.updateGamePhase(GamePhase.PLAY_SCORE_GO);
					// Get scorer and calculate score for current player
					scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
					score = scorer.evaluate(hands[s.lastPlayer]);
					// log the go score event
					logManager.update();
					// add to current player's score
					addToPlayerScore(s.lastPlayer, score);
					scorer.clearCache();
				}

				if (total(s.segment) == thirtyone) {
					// lastPlayer gets 2 points for a 31
					s.newSegment = true;
					gameInfo.currentPlayer = (gameInfo.currentPlayer+1) % 2;
				} else {
					// if total(segment) == 15, lastPlayer gets 2 points for a 15
					if (!s.go) { // if it is "go" then same player gets another turn
						gameInfo.currentPlayer = (gameInfo.currentPlayer+1) % 2;
					}
				}
			}

			if (s.newSegment) {
				segments.add(s.segment);
				s.reset(segments);
			}
		}
	}

	void showHandsCrib() {
		// Scoring
		// Reward players based on their hands and show-phase scoring rules
		Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
		int nonDealer = 0;
		int dealer = 1;
		// First show and score the non-dealer's hand
		showAndScore(nonDealer, startHands[nonDealer]);
		// Then the dealer's hand
		showAndScore(dealer, startHands[dealer]);
		// Finally the crib gets scored for the dealer
		Hand cribAndStarter = copyHand(crib);
		cribAndStarter.insert(starter.getFirst().getSuit(), starter.getFirst().getRank(), false);
		showAndScore(dealer, cribAndStarter);
	}

	private void showAndScore(int playerNo, Hand hand) {
		// Assess this player's start hand and score it
		gameInfo.currentPlayer = playerNo;
		gameInfo.setCurrentHand(hand);
		gameInfo.updateGamePhase(GamePhase.SHOW);
		// log the show event
		logManager.update();
		// ----> we are in SHOW_SCORE phase
		gameInfo.updateGamePhase(GamePhase.SHOW_SCORE);
		// calculate a score for their starter hand
		Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
		int score = scorer.evaluate(hand);
		// log the score for their starter hand
		logManager.update();
		// update the player's score
		addToPlayerScore(playerNo, score);
		scorer.clearCache();
	}

	public Cribbage()
	{
		super(850, 700, 30);
		cribbage = this;
		setTitle("Cribbage (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
		initScoreDisplay();

		Hand pack = deck.toHand(false);
		RowLayout layout = new RowLayout(starterLocation, 0);
		layout.setRotationAngle(0);
		pack.setView(this, layout);
		pack.setVerso(true);
		pack.draw();
		addActor(new TextActor("Seed: " + SEED, Color.BLACK, bgColor, normalFont), seedLocation);

		/* Play the round */
		gameInfo.updateGamePhase(GamePhase.SETUP);
		deal(pack, hands);
		logManager.update();
		gameInfo.updateGamePhase(GamePhase.START);
		discardToCrib();
		starter(pack);
		logManager.update();
		gameInfo.updateGamePhase(GamePhase.PLAY);
		play();
		gameInfo.updateGamePhase(GamePhase.SHOW);
		showHandsCrib();

		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over.");
		refresh();
	}

	public static Properties cribbageProperties = new Properties();

	public static void main(String[] args)
			throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		/* Handle Properties */
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));

		// Default properties
		cribbageProperties.setProperty("Animate", "true");
		cribbageProperties.setProperty("Player0", "cribbage.RandomPlayer");
		cribbageProperties.setProperty("Player1", "cribbage.HumanPlayer");
		// Default advanced settings
		cribbageProperties.setProperty("nPlayers", "2");
		cribbageProperties.setProperty("nStartCards", "6");
		cribbageProperties.setProperty("nDiscards", "2");

		// Read properties
		try (FileReader inStream = new FileReader("cribbage.properties")) {
			cribbageProperties.load(inStream);
		}

		// Control Graphics
		ANIMATE = Boolean.parseBoolean(cribbageProperties.getProperty("Animate"));

		// Control Randomisation
		/* Read the first argument and save it as a seed if it exists */
		if (args.length > 0 ) { // Use arg seed - overrides property
			SEED = Integer.parseInt(args[0]);
		} else { // No arg
			String seedProp = cribbageProperties.getProperty("Seed");	//Seed property
			if (seedProp != null) { // Use property seed
				SEED = Integer.parseInt(seedProp);
			} else { // and no property
				SEED = new Random().nextInt(); // so randomise
			}
		}
		random = new Random(SEED);

		// Control Player Types
		Class<?> clazz;
		playerTypes[0] = cribbageProperties.getProperty("Player0");
		clazz = Class.forName(playerTypes[0]);
		players[0] = (IPlayer) clazz.getConstructor().newInstance();
		playerTypes[1] = cribbageProperties.getProperty("Player1");
		clazz = Class.forName(playerTypes[1]);
		players[1] = (IPlayer) clazz.getConstructor().newInstance();
		// End properties

		new Cribbage();
	}

	// New methods -----------------------------------------------------------------------------------------------------
	/**
	 * Returns a copy of the given Hand based on the Deck used by the Cribbage game instance
	 * @param hand The Hand to copy
	 * @return A copy of the Hand based on the same Deck
	 */
	public static Hand copyHand(Hand hand) {
		Hand newHand = new Hand(Cribbage.getInstance().deck);
		for (Card c: hand.getCardList()) {
			newHand.insert(c.getSuit(), c.getRank(), false);
		}

		return newHand;
	}

	/** @return cribbage - the instance of Cribbage to play on */
	public static Cribbage getInstance() {
		return cribbage;
	}

	/** @return The hand containing the starter card */
	public Hand getStarter() {
		return starter;
	}

	/** @return The current phase of the game */
	public GamePhase getGamePhase() {
		return gameInfo.currentGamePhase;
	}

	/** @return The list of players in the game */
	public static IPlayer[] getPlayers() {
		return players;
	}


	public GameInformation getGameInfo() {
		return gameInfo;
	}

	/**
	 * Adds given score to the given player's total score, and updates the score on screen
	 * @param playerID The integer ID of the player to add score to
	 * @param score The score to add
	 */
	public void addToPlayerScore(int playerID, int score) {
//		notifyLoggers();
		scores[playerID] += score;
		updateScoreDisplay(playerID);
	}

	public static int getSEED() {
		return SEED;
	}

	public ArrayList<String> getPlayerTypes() {
		return new ArrayList<>(Arrays.asList(playerTypes));
	}

	public Hand[] getHands() {
		return hands;
	}

	public Hand[] getStartHands() {
		return startHands;
	}


	public ArrayList<ArrayList<Card>> getPlayerDiscards() {
		return new ArrayList<>(playerDiscards);
	}

	/**
	 * Returns a sorted list of cards based on the given card list and sort type
	 * @param cardList List of cards to sort
	 * @param sortType The ordering to sort by
	 * @return Sorted card list
	 */
	public ArrayList<Card> sortCardList(ArrayList<Card> cardList, Hand.SortType sortType) {
		// Create a new hand from the given card list
		Hand tempHand = new Hand(deck);
		for (Card c: cardList) {
			tempHand.insert(c.getSuit(), c.getRank(), false);
		}

		// Now sort the hand based on the given sort type
		tempHand.sort(sortType, false);
		// Finally, return the sorted card list
		return tempHand.getCardList();
	}

}