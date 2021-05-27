package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/** Concrete Strategy class for checking if the current segment has reached an exact milestone value and scoring it
 * correctly if so */
public class MilestoneScorer extends Scorer{

    protected static final int THIRTY_ONE = 31;
    protected static final int FIFTEEN = 15;
    // TODO: GET THIS FROM PROPERTIES
    protected static final int POINTS = 2;
    private static final int MAX_COMB_SIZE = 5;
    private static final int MIN_COMB_SIZE = 2;
    protected static final String FIFTEEN_STR = "fifteen";

    private ArrayList<ArrayList<Card>> getAllCardCombinations(ArrayList<Card> cardList) {
        // Create a new clone of the card so that we can sort it without changing the original list
        ArrayList<Card> sortedCardList = new ArrayList<>(cardList);
        // Now sort it, so the the resulting combinations are in order
        Collections.sort(sortedCardList);
        // Now get all card combinations from minimum to maximum size allowed
        ArrayList<ArrayList<Card>> combs = new ArrayList<>();
        for (int i = MIN_COMB_SIZE; i <= MAX_COMB_SIZE; i++) {
            combs.addAll(getCardCombinationsOfSize(i, 0, sortedCardList));
        }
        return combs;
    }

    // Returns a list of all possible unique sum combinations of the rank values of the given card list, of the given
    // combination size, and from the given starting index
    private ArrayList<ArrayList<Card>> getCardCombinationsOfSize(int combSize, int startIdx, ArrayList<Card> cardList) {
        ArrayList<ArrayList<Card>> combs = new ArrayList<>();
        // The base case. Just return lists containing a single card
        if (combSize == 1) {
            for (Card c: cardList.subList(startIdx, cardList.size())) {
                ArrayList<Card> comb = new ArrayList<>();
                comb.add(c);
                combs.add(comb);
            }
            return combs;
        }

        // The recursive case
        // To get all sum combinations of size "n" for a list of length "l", we need to sum each element "x" of the list
        // up to element l - n + 1, to each sum combination of size "n-1", of the list from element "x+1" onwards
        // Example: To get all sums of size 3, from the list [4,3,1,5]
        // List size = 4 --> So we need to sum the first 4 - 3 + 1 = 2 elements
        // --> Thus we need to sum 4 to every possible sum combination of size 2 of the list [3,1,5]
        // --> And we need to sum 3 to every possible sum combination of size 2 of the list [1,5]
        int limit = cardList.size() - combSize + 1;
        List<Card> subList = cardList.subList(startIdx, limit);
        for (int i = 0; i < subList.size(); i++) {
            Card card = subList.get(i);
            ArrayList<ArrayList<Card>> smallerCombs = getCardCombinationsOfSize(combSize - 1, startIdx + i + 1, cardList);
            for (ArrayList<Card> smallComb: smallerCombs) {
                smallComb.add(0, card);
                combs.add(smallComb);
            }
        }

        return combs;
    }

    // Returns the sum of the given card combination
    private int getCardCombinationSum(ArrayList<Card> cardList) {
        int total = 0;
        for (Card c: cardList) {
            total += Cribbage.cardValue(c);
        }

        return total;
    }

    // Returns the number of unique combination of cards that add up to 15
    private ArrayList<ArrayList<Card>> getFifteenCombinations(Hand hand) {
        // Get all card combinations
        ArrayList<ArrayList<Card>> combs = getAllCardCombinations(hand.getCardList());
        // Look through our combinations to find ones that sum up to 15
        ArrayList<ArrayList<Card>> fifteenCombs = new ArrayList<>();
        for (ArrayList<Card> cardComb: combs) {
            if (getCardCombinationSum(cardComb) == FIFTEEN) {
                fifteenCombs.add(cardComb);
            }
        }
        return fifteenCombs;
    }

    /**
     * Returns the score for the total number of fifteens in the given hand, or 0 if none are present
     * @param hand The hand of cards to be evaluated
     * @return The score for the total number of fifteens in the given hand, or 0 if none are present
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        // During show we want to find all unique combinations adding up to 15
        // Get all card combinations that add up to 15
        ArrayList<ArrayList<Card>> fifteenCombs = getFifteenCombinations(hand);

        for (ArrayList<Card> comb: fifteenCombs) {
            addToCache(POINTS, FIFTEEN_STR, comb);
        }
        return POINTS * fifteenCombs.size();
    }
}
