package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class PairScorer extends Scorer {
    // The size of each pair type
    final static int PAIR = 2;
    final static int TRIPLET = 3;
    final static int QUAD = 4;
    // The score for each pair type
    final static int PAIR_SCORE = Cribbage.getPropertyInt("pair2Score");
    final static int TRIPLET_SCORE = Cribbage.getPropertyInt("pair3Score");
    final static int QUAD_SCORE = Cribbage.getPropertyInt("pair4Score");
    // Strings representing the the various pair types
    final static String PAIR_STR = "pair2";
    final static String TRIPLET_STR = "pair3";
    final static String QUAD_STR = "pair4";

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
            addToCache(score, scoreType, cardList, new ScorerCache.CacheComparator());
        }
        return total;
    }
}