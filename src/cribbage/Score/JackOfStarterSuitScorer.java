package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;

/** Scorer for jack of the same suit as the starter card */
public class JackOfStarterSuitScorer extends Scorer {
    private static final int SCORE = Cribbage.getPropertyInt("jackStarterSuitScore");;
    private static final String JACK_STR = "jack";

    /**
     * Returns the score for the jack of the same suit as the starter card if it is present in the given hand, and 0 if
     * it's not
     * @param hand The hand of cards to be evaluated
     * @return The score for having a jack of the same suit as the starter card, or 0 if not
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        Cribbage cribbage = Cribbage.getInstance();
        Card starter = cribbage.getStarter().getFirst();
        // If the starter card is a Jack, there cannot be another Jack
        // of the same suit
        if (starter.getRank() == Cribbage.Rank.JACK) return 0;

        // Otherwise, check for a Jack of the same suit as starter
        Cribbage.Suit starterSuit = (Cribbage.Suit)starter.getSuit();
        for (Card c: hand.getCardsWithSuit(starterSuit)) {
            if (c.getRank() == Cribbage.Rank.JACK) {
                ArrayList<Card> jack = new ArrayList<>();
                jack.add(c);
                addToCache(SCORE, JACK_STR, jack);
                return SCORE;
            }
        }
        return 0;
    }
}
