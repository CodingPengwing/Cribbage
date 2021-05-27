package cribbage;

// Cribbage.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import cribbage.Log.Logger;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerComposite;
import cribbage.Score.ScorerCompositeFactory;
import cribbage.Score.ScorerFactory;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cribbage extends CardGame {
	static Cribbage cribbage;	// Provide access to singleton

	/** An enum for representing the phase of the game */
	public enum GamePhase { DEAL, DISCARD, STARTER, PLAY, PLAY_GO, SHOW, SCORE_PLAY, SCORE_SHOW }

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

		public int getCurrentPlayerScore() {
			return scores[currentPlayer];
		}

		public int getCurrentPlayer() {
			return currentPlayer;
		}
	}

	public static int cardValue(Card c) { return ((Cribbage.Rank) c.getRank()).value; }

	/**
	 * Returns the order (where it fits in the ordering of cards) of the given card
	 * @param c The card to find the order of
	 * @return The order of the card
	 */
	public static int cardOrder(Card c) {
		return ((Cribbage.Rank) c.getRank()).order;
	}

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

	//	TODO: (MAYBE) GET THESE FROM PROPERTY FILE
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
	private final Hand[] initialHands = new Hand[nPlayers];
	private Hand starter;
	private Hand crib;

	public static void setStatus(String string) { cribbage.setStatusText(string); }

	static private final IPlayer[] players = new IPlayer[nPlayers];
	private final int[] scores = new int[nPlayers];

	final Font normalFont = new Font("Serif", Font.BOLD, 24);
	final Font bigFont = new Font("Serif", Font.BOLD, 36);

	private void initScore() {
		for (int i = 0; i < nPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	private void updateScore(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}








	private void deal(Hand pack, Hand[] hands) {
		gameInfo.currentGamePhase = GamePhase.DEAL;
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
		gameInfo.currentGamePhase = GamePhase.DISCARD;
		crib = new Hand(deck);
		RowLayout layout = new RowLayout(cribLocation, cribWidth);
		layout.setRotationAngle(0);
		crib.setView(this, layout);
		// crib.setTargetArea(cribTarget);
		crib.draw();
		for (IPlayer player: players) {
			for (int i = 0; i < nDiscards; i++) {
				transfer(player.discard(), crib);
			}
			crib.sort(Hand.SortType.POINTPRIORITY, true);
		}
	}

	private void starter(Hand pack) {
		gameInfo.currentGamePhase = GamePhase.STARTER;
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
			initialHands[i] = copyHand(hands[i]);
			initialHands[i].insert(starter.getFirst().getSuit(), starter.getFirst().getRank(), false);
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

	private void play() {
		final int thirtyone = 31;
		List<Hand> segments = new ArrayList<>();
		gameInfo.currentPlayer = 0; // Player 1 is dealer
		Segment s = new Segment();
		s.reset(segments);
		while (!(players[0].emptyHand() && players[1].emptyHand())) {
			gameInfo.currentGamePhase = GamePhase.PLAY;
			// System.out.println("segments.size() = " + segments.size());
			Card nextCard = players[gameInfo.currentPlayer].lay(thirtyone-total(s.segment));
			if (nextCard == null) {
				if (s.go) {
					// Another "go" after previous one with no intervening cards
					// lastPlayer gets 1 point for a "go"
					s.newSegment = true;

					// Scoring
					// Since neither player can play another card without going over the limit, the last player who
					// Played a card gets a point(s) for "go"
					gameInfo.currentGamePhase = GamePhase.PLAY_GO;
					addToPlayerScore(s.lastPlayer, ScorerCompositeFactory.getInstance().getScorerComposite().evaluate(hands[s.lastPlayer]));
				} else {
					// currentPlayer says "go"
					s.go = true;
				}
				gameInfo.currentPlayer = (gameInfo.currentPlayer+1) % 2;
			} else {
				s.lastPlayer = gameInfo.currentPlayer; // last Player to play a card in this segment
				transfer(nextCard, s.segment);

				// Scoring
				// Score and potentially rewards the last player who played card based on play-phase scoring
				gameInfo.currentGamePhase = GamePhase.SCORE_PLAY;
				addToPlayerScore(s.lastPlayer, ScorerCompositeFactory.getInstance().getScorerComposite().evaluate(s.segment));

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
		gameInfo.currentGamePhase = GamePhase.SHOW;

		// Scoring
		// Reward players based on their hands and show-phase scoring rules
		Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
		int nonDealer = 0;
		int dealer = 1;
		// First score the non-dealer's hand
		gameInfo.currentGamePhase = GamePhase.SCORE_SHOW;
		addToPlayerScore(nonDealer, scorer.evaluate(initialHands[nonDealer]));
		gameInfo.currentGamePhase = GamePhase.SHOW;
		// Then the dealer's hand
		gameInfo.currentGamePhase = GamePhase.SCORE_SHOW;
		addToPlayerScore(dealer, scorer.evaluate(initialHands[dealer]));
		gameInfo.currentGamePhase = GamePhase.SHOW;
		// Finally the crib gets scored for the dealer
		Hand cribAndStarter = copyHand(crib);
		cribAndStarter.insert(starter.getFirst().getSuit(), starter.getFirst().getRank(), false);
		gameInfo.currentGamePhase = GamePhase.SCORE_SHOW;
		addToPlayerScore(dealer, scorer.evaluate(cribAndStarter));
		gameInfo.currentGamePhase = GamePhase.SHOW;
	}

	public Cribbage()
	{
		super(850, 700, 30);
		cribbage = this;
		setTitle("Cribbage (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
		initScore();

		Hand pack = deck.toHand(false);
		RowLayout layout = new RowLayout(starterLocation, 0);
		layout.setRotationAngle(0);
		pack.setView(this, layout);
		pack.setVerso(true);
		pack.draw();
		addActor(new TextActor("Seed: " + SEED, Color.BLACK, bgColor, normalFont), seedLocation);

		/* Play the round */
		deal(pack, hands);
		discardToCrib();
		starter(pack);
		play();
		showHandsCrib();

		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over.");
		refresh();
	}

	public static void main(String[] args)
			throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		/* Handle Properties */
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		Properties cribbageProperties = new Properties();
		// Default properties
		cribbageProperties.setProperty("Animate", "true");
		cribbageProperties.setProperty("Player0", "cribbage.RandomPlayer");
		cribbageProperties.setProperty("Player1", "cribbage.HumanPlayer");

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
		clazz = Class.forName(cribbageProperties.getProperty("Player0"));
		players[0] = (IPlayer) clazz.getConstructor().newInstance();
		clazz = Class.forName(cribbageProperties.getProperty("Player1"));
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

//	private void notifyLoggers() {
//		for (Logger l: loggers) {
//			l.update();
//		}
//	}

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
		updateScore(playerID);
	}
}