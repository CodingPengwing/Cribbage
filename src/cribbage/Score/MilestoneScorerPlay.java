/**
 * Created by Hoang Dang (1080344) and Ehsan Soltani Abhari (1003877)
 * Workshop 16 Team 06.
 */

package cribbage.Score;

import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

/**
 * Concrete Strategy class. Checks if the current segment (during Play) has reached an exact milestone
 * value and scoring it if so (ie. segment reaching a sum of 15 or 31).
 */
class MilestoneScorerPlay extends MilestoneScorer {
    private static final String THIRTYONE_STR = "thirtyone";

    /**
     * During the play phase checks if fifteen or thirty-one have been reached, and if so, returns an appropriate score
     * @param hand The hand of cards to be evaluated
     * @return The score reward for reaching fifteen or thirty-one during play if either is reached, or 0 if not. During
     * show, the score for each combination of cards adding up to fifteen
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        switch (Cribbage.total(hand)) {
            case FIFTEEN:
                addToCache(FIFTEEN_SCORE, FIFTEEN_STR, null);
                return FIFTEEN_SCORE;
            case THIRTY_ONE:
                addToCache(THIRTYONE_SCORE, THIRTYONE_STR, null);
                return THIRTYONE_SCORE;
            default:
                return 0;
        }
    }
}
