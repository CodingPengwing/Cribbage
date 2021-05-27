package cribbage.Score;

import ch.aplu.jcardgame.Hand;

/** Scores a "go" */
public class GoScorer extends Scorer {
    private static final int GO_SCORE = 1;

    /**
     * Returns the reward for a "go"
     * @param hand The hand of cards to be evaluated
     * @return Score for a "go"
     */
    @Override
    public int evaluate(Hand hand) {
        return GO_SCORE;
    }
}
