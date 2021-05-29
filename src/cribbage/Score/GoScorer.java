/**
 * Created by Hoang Dang (1080344) and Ehsan Soltani Abhari (1003877)
 * Workshop 16 Team 06.
 */

package cribbage.Score;

import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

/**
 * Concrete Strategy class. Scores a "go".
 */
class GoScorer extends Scorer {
    private static final int GO_SCORE = Cribbage.getPropertyInt("goScore");
    private static final String GO_STR = "go";

    /**
     * Returns the reward for a "go"
     * @param hand The hand of cards to be evaluated
     * @return Score for a "go"
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        addToCache(GO_SCORE, GO_STR, null);
        return GO_SCORE;
    }
}
