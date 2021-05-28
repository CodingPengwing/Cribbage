package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PairScorer extends Scorer {
    // The size of each pair type
    protected final static int PAIR = 2;
    protected final static int TRIPLET = 3;
    protected final static int QUAD = 4;
    // The score for each pair type
    // TODO: TAKE THIS CONSTANTS FROM A PROPERTY FILE
    protected static final int PAIR_SCORE = 2;
    protected static final int TRIPLET_SCORE = 6;
    protected static final int QUAD_SCORE = 12;
    // Strings representing the the various pair types
    protected final static String PAIR_STR = "pair2";
    protected final static String TRIPLET_STR = "pair3";
    protected final static String QUAD_STR = "pair4";

    @Override
    protected void addToCache(int score, String scoreType, ArrayList<Card> cardList) {
        super.addToCache(score, scoreType, cardList);
        Collections.sort(getCache());
    }

    @Override
    protected void addAllToCache(ArrayList<ScorerCache> cacheList) {
        super.addAllToCache(cacheList);
        Collections.sort(getCache());
    }

    /**
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        int total = 0;
        // TODO: CHECK THE ORDERING OF SUITS WHEN THERE ARE PAIRS OF SAME RANK
        ArrayList<Card[]> pairArray = hand.getPairs();
        pairArray.addAll(hand.getTrips());
        pairArray.addAll(hand.getQuads());

        for (Card[] array : pairArray) {
            ArrayList<Card> cardList = new ArrayList<>(Arrays.asList(array));
            String scoreType = "";
            int score = 0;
            switch (cardList.size()) {
                case PAIR:
                    score = PAIR_SCORE;
                    scoreType = PAIR_STR;
                    break;
                case TRIPLET:
                    score = TRIPLET_SCORE;
                    scoreType = TRIPLET_STR;
                    break;
                case QUAD:
                    score = QUAD_SCORE;
                    scoreType = QUAD_STR;
                    break;
            }
            total += score;
            addToCache(score, scoreType, cardList);
        }
        return total;
    }
}