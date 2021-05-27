package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

// TODO: Change the inheritance structure
public class PairScorer extends Scorer {
    protected final static int QUAD = 4;
    protected final static int TRIPLET = 3;
    protected final static int PAIR = 2;
    // TODO: TAKE THIS CONSTANTS FROM A PROPERTY FILE
    protected static final int PAIR_SCORE = 2;
    protected static final int TRIPLET_SCORE = 6;
    protected static final int QUAD_SCORE = 12;

    @Override
    public int evaluate(Hand hand) {
        int total = 0;
        total += QUAD_SCORE * hand.getQuads().size();
        total += TRIPLET_SCORE * hand.getTrips().size();
        total += PAIR_SCORE * hand.getPairs().size();
        return total;
    }
}
