package cribbage.Score;

import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

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
        Cribbage cribbage = Cribbage.getInstance();
        Cribbage.Rank starterRank = (Cribbage.Rank)cribbage.getStarter().getFirst().getRank();
        if (starterRank == Cribbage.Rank.JACK) return SCORE;
        return 0;
    }
}
