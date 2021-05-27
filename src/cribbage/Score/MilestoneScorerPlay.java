package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;
import java.util.Collections;

public class MilestoneScorerPlay extends MilestoneScorer {

    /**
     * During the play phase checks if fifteen or thirty-one have been reached, and if so, returns an appropriate score
     * @param hand The hand of cards to be evaluated
     * @return The score reward for reaching fifteen or thirty-one during play if either is reached, or 0 if not. During
     * show, the score for each combination of cards adding up to fifteen
     */
    @Override
    public int evaluate(Hand hand) {
        switch (Cribbage.total(hand)) {
            case FIFTEEN: case THIRTY_ONE: return POINTS;
            default: return 0;
        }
    }
}
