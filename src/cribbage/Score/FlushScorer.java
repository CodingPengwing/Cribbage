package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;

/**
 * Concrete Strategy class. Scorer for Flush hands.
 */
class FlushScorer extends Scorer {
    private static final int FLUSH4 = 4;
    private static final int FLUSH5 = 5;
    private static final int FLUSH4_SCORE = Cribbage.getPropertyInt("flush4Score");
    private static final int FLUSH5_SCORE = Cribbage.getPropertyInt("flush5Score");
    private static final String FLUSH4_STR = "flush4";
    private static final String FLUSH5_STR = "flush5";

    /**
     * Returns the score for the largest flush in the given hand if it exists (must be at least of minimum flush size),
     * and if not returns 0
     * @param hand The hand of cards to be evaluated
     * @return The score for the largest flush, or 0 if none exists
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        // Find the suit with the highest count
        int maxSuitCount = 0;
        ArrayList<Card> maxSuitList = new ArrayList<>();
        for (Cribbage.Suit suit: Cribbage.Suit.values()) {
            ArrayList<Card> suitList = hand.getCardsWithSuit(suit);
            int count = suitList.size();
            if (count > maxSuitCount) {
                maxSuitCount = count;
                maxSuitList = suitList;
            }
        }

        switch (maxSuitCount) {
            case FLUSH4:
                addToCache(FLUSH4_SCORE, FLUSH4_STR, maxSuitList);
                return FLUSH4_SCORE;
            case FLUSH5:
                addToCache(FLUSH5_SCORE, FLUSH5_STR, maxSuitList);
                return FLUSH5_SCORE;
            default:
                return 0;
        }
    }
}
