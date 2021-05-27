package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;

/** Concrete Strategy class for checking if the current segment has reached an exact milestone value and scoring it
 * correctly if so */
public class MilestoneScorer extends Scorer{

    protected static final int THIRTY_ONE = 31;
    protected static final int FIFTEEN = 15;
    // TODO: GET THIS FROM PROPERTIES
    protected static final int POINTS = 2;
    private static final int MAX_COMB_SIZE = 5;
    private static final int MIN_COMB_SIzE = 2;

    private ArrayList<Integer> getAllSumCombsAndBelow(int maxCombSize, ArrayList<Card> cardList) {
        ArrayList<Integer> combs = new ArrayList<>();
        for (int i = MIN_COMB_SIzE; i <= maxCombSize; i++) {
            combs.addAll(getAllSumCombs(i, 0, cardList));
        }

        return combs;
    }

    // Returns a list of all possible unique sum combinations of the rank values of the given card list, of the given
    // combination size, and from the given starting index
    private ArrayList<Integer> getAllSumCombs(int combSize, int startIdx, ArrayList<Card> cardList) {
        ArrayList<Integer> combs = new ArrayList<>();
        // The base case. Just return the value of the cards from the start index
        if (combSize <= 1) {
            for (Card c: cardList.subList(startIdx, cardList.size())) {
                combs.add(Cribbage.cardValue(c));
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
        for (Card c: cardList.subList(startIdx, limit)) {
            int cardVal = Cribbage.cardValue(c);
            ArrayList<Integer> smallerCombs = getAllSumCombs(combSize - 1, startIdx + 1, cardList);
            for (int smallComb: smallerCombs) {
                combs.add(cardVal + smallComb);
            }
        }

        return combs;
    }

    // Returns the number of unique combination of cards that add up to 15
    private int getNumFifteens(Hand hand) {
        ArrayList<Integer> combs = getAllSumCombsAndBelow(MAX_COMB_SIZE, hand.getCardList());
        System.out.println(hand.getCardList());
        System.out.println(combs);
        int nFifteens = 0;
        for (int cardSum: combs) {
            if (cardSum == FIFTEEN) {
                nFifteens++;
            }
        }

        return nFifteens;
    }

    @Override
    public int evaluate(Hand hand) {
        // During show we want to find all unique combinations adding up to 15
        return POINTS * getNumFifteens(hand);
    }
}
