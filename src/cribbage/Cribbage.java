package cribbage;

// Cribbage.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import cribbage.Log.LogManager;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCompositeFactory;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The implementation of Cribbage, which conforms to a CardGame.
 */
public class Cribbage extends CardGame {

	// ------------------------------------ Overarching logic of Cribbage -------------------------------------//

	// Provide access to singleton
	static Cribbage cribbage;
	/** @return cribbage - the instance of Cribbage to play on */
	public static Cribbage getInstance() {
		return cribbage;
	}

	// Properties for the cribbage game
	final static Properties cribbageProperties = new Properties();
	// Set default properties for the game
	// These values can be overwritten by changing the default values in the cribbage.properties file
	private static void setDefaults(){
		// Default properties
		cribbageProperties.setProperty("Animate", "true");
		cribbageProperties.setProperty("Player0", "cribbage.RandomPlayer");
		cribbageProperties.setProperty("Player1", "cribbage.HumanPlayer");
		cribbageProperties.setProperty("logFile", "cribbage.log");
		// Default advanced settings (scoring system)
		cribbageProperties.setProperty("flush4Score", "4");
		cribbageProperties.setProperty("flush5Score", "5");
		cribbageProperties.setProperty("starter", "1");
		cribbageProperties.setProperty("goScore", "1");
		cribbageProperties.setProperty("jackStarterSuitScore", "1");
		cribbageProperties.setProperty("fifteenScore", "2");
		cribbageProperties.setProperty("thirtyoneScore", "2");
		cribbageProperties.setProperty("pair2Score", "2");
		cribbageProperties.setProperty("pair3Score", "6");
		cribbageProperties.setProperty("pair4Score", "12");
		cribbageProperties.setProperty("run3Score", "3");
		cribbageProperties.setProperty("run4Score", "4");
		cribbageProperties.setProperty("run5Score", "5");
		cribbageProperties.setProperty("run6Score", "6");
		cribbageProperties.setProperty("run7Score", "7");
	}

	/** Returns the value for the property requested in String form
	 * @param property - String of property requested
	 * @return the string value of that property
	 */
	public static String getProperty(String property) {
		return cribbageProperties.getProperty(property);
	}

	/** Returns the value for the property requested in integer form
	 * @param property - String of property requested
	 * @return the integer value of that property
	 */
	public static int getPropertyInt(String property) {
		return Integer.parseInt(cribbageProperties.getProperty(property));
	}

	public static void setStatus(String string) { cribbage.setStatusText(string); }

	/**
	 * Constructs and sets up the cribbage game, then play it
	 */
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
		// Execute the Set Up phase
		gameInfo.updateGamePhase(GamePhase.SETUP);
		deal(pack, hands);
		logManager.update();
		// Execute the game Start phase
		gameInfo.updateGamePhase(GamePhase.START);
		discardToCrib();
		starter(pack);
		// Execute the Play phase
		gameInfo.updateGamePhase(GamePhase.PLAY);
		play();
		// Execute the Show phase
		gameInfo.updateGamePhase(GamePhase.SHOW);
		showHandsCrib();
		// Notify players it's game over
		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over.");
		refresh();
	}

	/**
	 * Sets the default properties for the game, then creates an instance of Cribbage to play.
	 * @param args
	 */
	public static void main(String[] args)
			throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			InstantiationException, IllegalAccessException {
		/* Handle Properties */
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));

		// Set default properties
		setDefaults();
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



	// ------------------------- Definitions and methods related to Card and Hand ----------------------------//

	/** Suits for the cards in the game */
	public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

	/** Ranks for the cards in the game */
	public enum Rank {
		// Order of cards is tied to card images
		ACE(1,1), KING(13,10), QUEEN(12,10),
		JACK(11,10), TEN(10,10), NINE(9,9),
		EIGHT(8,8), SEVEN(7,7), SIX(6,6),
		FIVE(5,5), FOUR(4,4), THREE(3,3),
		TWO(2,2);

		public final int order;
		public final int value;
		Rank(int order, int value) {
			this.order = order;
			this.value = value;
		}
	}

	/** Returns the integer value of a card */
	public static int cardValue(Card c) { return ((Cribbage.Rank) c.getRank()).value; }

	/** Canonical String representation of a Suit */
	public String canonical(Suit s) { return s.toString().substring(0, 1); }

	/** Canonical String representation of a Rank */
	public String canonical(Rank r) {
		switch (r) {
			case ACE:case KING:case QUEEN:case JACK:case TEN:
				return r.toString().substring(0, 1);
			default:
				return String.valueOf(r.value);
		}
	}

	/** Canonical String representation of a Card */
	public String canonical(Card c) { return canonical((Rank) c.getRank()) + canonical((Suit) c.getSuit()); }

	/** Canonical String representation of an ArrayList of Cards */
	public String canonical(ArrayList<Card> cardList) {
		String str = "[";
		for (int i = 0; i < cardList.size(); i++) {
			str += Cribbage.getInstance().canonical(cardList.get(i));
			if (i < cardList.size()-1) str += ",";
		}
		str += "]";
		return str;
	}

	/** Canonical String representation of a Hand */
	public String canonical(Hand h) {
		Hand h1 = new Hand(deck); // Clone to sort without changing the original hand
		for (Card C: h.getCardList()) h1.insert(C.getSuit(), C.getRank(), false);
		h1.sort(Hand.SortType.POINTPRIORITY, false);
		return "[" + h1.getCardList().stream().map(this::canonical).collect(Collectors.joining(",")) + "]";
	}

	// Create Card values
	class MyCardValues implements Deck.CardValues { // Need to generate a unique value for every card
		public int[] values(Enum suit) {	// Returns the value for each card in the suit
			return Stream.of(Rank.values()).mapToInt(r -> (((Rank) r).order-1)*(Suit.values().length)+suit.ordinal()).toArray();
		}
	}

	// Used for randomizing
	static Random random;
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
		int x = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	// Generates a random Card
	public static Card randomCard(Hand hand){
		int x = random.nextInt(hand.getNumberOfCards());
		return hand.get(x);
	}

	/** Calculates the integer total of a hand.
	 * @param hand
	 * @return the total value of the hand (int)
	 */
	public static int total(Hand hand) {
		int total = 0;
		for (Card c: hand.getCardList()) total += cardValue(c);
		return total;
	}

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

	/** @return The hand containing the starter card */
	public Hand getStarter() {
		return starter;
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

	// Defines whether the actions in game should be animated on the screen
	static boolean ANIMATE;

	// Transfers a card into a hand
	void transfer(Card c, Hand h) {
		if (ANIMATE) {
			c.transfer(h, true);
		} else {
			c.removeFromHand(true);
			h.insert(c, true);
		}
	}

	// Deals the specified number of cards to the specified number of players.
	// nPlayers - number of players
	// nStartCards - number of starting cards per player
	private void dealingOut(Hand pack, Hand[] hands) {
		for (int i = 0; i < nStartCards; i++) {
			for (int j=0; j < nPlayers; j++) {
				Card dealt = randomCard(pack);
				dealt.setVerso(false);	// Show the face
				transfer(dealt, hands[j]);
			}
		}
	}



	//-------------------------- Definitions and methods related to game representation ---------------------------//

	static int SEED;
	/** @return the game's seed */
	public static int getSEED() {
		return SEED;
	}

	private final String version = "0.1";

	// Changed nPlayers, nStartCards and nDiscards to private
	private static final int nPlayers = 2;
	private final int nStartCards = 6;
	private final int nDiscards = 2;
	private final int handWidth = 400;
	private final int cribWidth = 150;
	private final int segmentWidth = 180;

	// Create the deck
	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover", new MyCardValues());

	// Locations on the display screen
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

	final Font normalFont = new Font("Serif", Font.BOLD, 24);
	final Font bigFont = new Font("Serif", Font.BOLD, 36);

	// Initialise the display of scores
	private void initScoreDisplay() {
		for (int i = 0; i < nPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	// Update the display of scores
	private void updateScoreDisplay(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}


	// ** Hands in the game **
	private final Hand[] hands = new Hand[nPlayers];
	private final Hand[] startHands = new Hand[nPlayers];
	private Hand starter;
	private Hand crib;

	/** @return the current hands in the game */
	public Hand[] getHands() {
		return hands;
	}

	private final LogManager logManager = LogManager.getInstance(this);
	private static final IPlayer[] players = new IPlayer[nPlayers];
	private final int[] scores = new int[nPlayers];
	private static final String[] playerTypes = new String[nPlayers];
	private final ArrayList<ArrayList<Card>> playerDiscards = new ArrayList<>();

	// Adds given score to the given player's total score, and updates the score on screen
	private void addToPlayerScore(int playerID, int score) {
		scores[playerID] += score;
		updateScoreDisplay(playerID);
	}

	/** @return the list of player types in the game */
	public ArrayList<String> getPlayerTypes() {
		return new ArrayList<>(Arrays.asList(playerTypes));
	}

	/** @return the player's discard piles */
	public ArrayList<ArrayList<Card>> getPlayerDiscards() {
		return new ArrayList<>(playerDiscards);
	}



	//----------------- Definitions and method related to the current phase/stage of the game --------------------//

	/** An enum for representing the phase of the game */
	public enum GamePhase { SETUP, START, PLAY, PLAY_SCORE, PLAY_SCORE_GO, SHOW, SHOW_SCORE}

	/** @return The current phase of the game */
	public GamePhase getGamePhase() {
		return gameInfo.currentGamePhase;
	}

	// Hold the current game information needed by other classes
	private GameInformation gameInfo = new GameInformation();

	/** A class for keeping track of game information */
	public class GameInformation {
		int currentPlayer = 0;
		GamePhase currentGamePhase = GamePhase.PLAY;
		Hand currentHand = null;

		/** @return An integer representing the current player in the game. */
		public int getCurrentPlayer() {
			return currentPlayer;
		}

		/** @return An integer representing the current player's score in the game. */
		public int getCurrentPlayerScore() {
			return scores[currentPlayer];
		}

		/** @return The current phase of the game */
		public GamePhase getGamePhase() {
			return currentGamePhase;
		}

		// Updates the current game phase, only callable by Cribbage
		void updateGamePhase(GamePhase phase) {
			currentGamePhase = phase;
		}

		/** Gets the current hand that is being played in the game */
		public Hand getCurrentHand() {
			return currentHand;
		}

		// Sets the current hand, only callable by Cribbage
		void setCurrentHand(Hand currentHand) {
			this.currentHand = currentHand;
		}
	}
	/** @return the object holding game information needed by external sources */
	public GameInformation getGameInfo() {
		return gameInfo;
	}


	// The current segment of the game
	private Segment s = new Segment();

	// The Segment object representing a particular segment of Play in the game
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

	/** @return the current total value of the segment */
	public int getCurrentSegmentTotal() {
		return total(s.segment);
	}

	/** @return the last played card in the current segment */
	public Card getLastPlayedCard() {
		ArrayList<Card> cards = s.segment.getCardList();
		return cards.get(cards.size()-1);
	}



	//------------------------------------ Methods for executing the game -------------------------------------//

	// Dealing cards to players
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

	// Asking players to discard cards to the crib
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

	// Choosing a random card for starter
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

		// Scoring the start
		Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
		int score = scorer.evaluate(starter);
		// Log the changes
		logManager.update();
		// If the starter card is a Jack, the dealer gets points
		addToPlayerScore(1, score);
		scorer.clearCache();
	}

	// Initiate play for the players
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

				// If players' hands are both empty, check who played last and award that player a score.
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

				// If the segment needs to end
				if (total(s.segment) == thirtyone) {
					s.newSegment = true;
					gameInfo.currentPlayer = (gameInfo.currentPlayer+1) % 2;
				}
				// Else continue and switch players if no go has been called
				else {
					if (!s.go) {
						gameInfo.currentPlayer = (gameInfo.currentPlayer+1) % 2;
					}
				}
			}
			// Reset the current segment -> start a new segment
			if (s.newSegment) {
				segments.add(s.segment);
				s.reset(segments);
			}
		}
	}

	// Start the Show part of the game
	private void showHandsCrib() {
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

	// This will show the cards and log the scores of given a player and hand
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
}