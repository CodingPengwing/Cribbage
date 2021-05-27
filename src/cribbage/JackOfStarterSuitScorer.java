package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

/** Scorer for jack of the same suit as the starter card */
public class JackOfStarterSuitScorer extends Scorer {
    private static final int SCORE = 1;

    /**
     * Returns the score for the jack of the same suit as the starter card if it is present in the given hand, and 0 if
     * it's not
     * @param hand The hand of cards to be evaluated
     * @return The score for having a jack of the same suit as the starter card, or 0 if not
     */
    @Override
    public int evaluate(Hand hand) {
        for (Card c: hand.getCardsWithSuit((Cribbage.Suit)Cribbage.cribbage.getStarter().getFirst().getSuit())) {
            if ((Cribbage.Rank)c.getRank() == Cribbage.Rank.JACK) {
                return SCORE;
            }
        }
        return 0;
    }
}
