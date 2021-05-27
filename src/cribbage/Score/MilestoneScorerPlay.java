package cribbage.Score;

import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

public class MilestoneScorerPlay extends MilestoneScorer {
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
                addToCache(POINTS, FIFTEEN_STR, null);
                return POINTS;
            case THIRTY_ONE:
                addToCache(POINTS, THIRTYONE_STR, null);
                return POINTS;
            default:
                return 0;
        }
    }
}
