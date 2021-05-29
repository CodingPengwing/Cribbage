package cribbage.Score;

import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;

import java.util.ArrayList;

/** Scorer for the starter card being a Jack */
class StarterScorer extends Scorer {
    private final static int STARTER_SCORE = Cribbage.getPropertyInt("starterScore");
    private static final String STARTER_STR = "starter";

    /**
     * Returns the score for the starter card being a Jack if that is the case, and 0 if it's not
     * @param hand The hand of cards to be evaluated
     * @return The score for the starter card being a Jack if that is the case, and 0 if it's not
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        Cribbage cribbage = Cribbage.getInstance();
        Card starter = cribbage.getStarter().getFirst();
        Cribbage.Rank starterRank = (Cribbage.Rank)starter.getRank();
        // Check that the rank of the first card is a Jack
        if (starterRank == Cribbage.Rank.JACK) {
            ArrayList<Card> cardList = new ArrayList<>();
            cardList.add(starter);
            addToCache(STARTER_SCORE, STARTER_STR, cardList);
            return STARTER_SCORE;
        }
        return 0;
    }
}
