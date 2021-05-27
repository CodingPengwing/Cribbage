package cribbage;

import ch.aplu.jcardgame.Hand;

/** Scorer for the starter card being a Jack */
public class JackStarterScorer extends Scorer {
    private final static int SCORE = 2;

    /**
     * Returns the score for the starter card being a Jack if that is the case, and 0 if it's not
     * @param hand The hand of cards to be evaluated
     * @return The score for the starter card being a Jack if that is the case, and 0 if it's not
     */
    @Override
    public int evaluate(Hand hand) {
        if ((Cribbage.Rank)Cribbage.cribbage.getStarter().getFirst().getRank() == Cribbage.Rank.JACK) {
            return SCORE;
        }
        return 0;
    }
}
