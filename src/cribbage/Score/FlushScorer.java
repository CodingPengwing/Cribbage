package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;

/** Performs scoring for flush */
public class FlushScorer extends Scorer {
    private static final int FLUSH4 = 4;
    private static final int FLUSH5 = 5;
    // TODO: GET THESE FROM PROPERTY FILE
    private static final int FLUSH4_SCORE = 4;
    private static final int FLUSH5_SCORE = 5;


    /**
     * Returns the score for the largest flush in the given hand if it exists (must be at least of minimum flush size),
     * and if not returns 0
     * @param hand The hand of cards to be evaluated
     * @return The score for the largest flush, or 0 if none exists
     */
    @Override
    public int evaluate(Hand hand) {
        ArrayList<Card> cards = new ArrayList<>(hand.getCardList());
        Cribbage cribbage = Cribbage.getInstance();
        cards.add(cribbage.getStarter().getFirst());

        // Find the suit with the highest count
        int maxSuitCount = 0;
        for (Cribbage.Suit suit: Cribbage.Suit.values()) {
            int count = hand.getCardsWithSuit(suit).size();
            if (count > maxSuitCount) maxSuitCount = count;
        }

        switch (maxSuitCount) {
            case FLUSH4: return FLUSH4_SCORE;
            case FLUSH5: return FLUSH5_SCORE;
            default: return 0;
        }
    }
}
