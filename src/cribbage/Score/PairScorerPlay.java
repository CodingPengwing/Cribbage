package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.Collections;

import java.util.ArrayList;

public class PairScorerPlay extends PairScorer {


    // Returns the number of cards which are of the same rank in a row, starting from the index startIdx and working
    // backwards in the given cardList, until the streak is broken
    // Note that if startIdx is out of bounds, will result in an index out of bounds error
    private ArrayList<Card> getMostRecentPairs(Hand hand) {
        ArrayList<Card> cards = hand.getCardList();
        ArrayList<Card> pairs = new ArrayList<>();
        if (cards.size() < 2) return pairs;

        Collections.reverse(cards);
        int count = 0;
        Card lastCard = cards.get(0);
        Cribbage.Suit lastSuit = (Cribbage.Suit) lastCard.getSuit();
        for (Card card : cards) {
            if (card.getSuit() == lastSuit) count++;
            else break;
        }
        if (count < 2) return pairs;
        for (int i = 0; i < count; i++) pairs.add(cards.get(i));
        return pairs;
    }

    @Override
    public int evaluate(Hand hand) {
        // During play, we only look at the longest streak from the end of the play segment
        ArrayList<Card> cards = getMostRecentPairs(hand);
        switch (cards.size()) {
            case QUAD: return QUAD_SCORE;
            case TRIPLET: return TRIPLET_SCORE;
            case PAIR: return PAIR_SCORE;
            default: return 0;
        }
    }
}
