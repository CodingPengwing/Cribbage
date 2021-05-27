package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.Collections;

import java.util.ArrayList;

public class PairScorerPlay extends PairScorer {


    // Returns the number of cards which are of the same rank in a row starting from the back of the hand
    private ArrayList<Card> getMostRecentPairs(Hand hand) {
        ArrayList<Card> cards = hand.getCardList();
        ArrayList<Card> pairs = new ArrayList<>();
        if (cards.size() < 2) return pairs;

        // Reverse the order of cards so that the back of the hand is now at the front
        Collections.reverse(cards);
        int count = 0;
        // Get the last card's rank
        Card lastCard = cards.get(0);
        Cribbage.Rank lastRank = (Cribbage.Rank) lastCard.getRank();
        // Check how many cards in a row have the same rank as that
        for (Card card : cards) {
            if (card.getRank() == lastRank) count++;
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
