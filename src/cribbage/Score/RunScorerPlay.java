package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;
import java.util.Collections;

class RunScorerPlay extends RunScorer {
    // Returns the largest run from the end of the given hand that is not interrupted by a pair. If no such runs exist,
    // the empty array is returned
    private Card[] getLongestRunFromEnd(Hand hand) {
        // Get all the cards in the hand
        ArrayList<Card> cardList = hand.getCardList();
        // Get all possible runs in the given hand
        ArrayList<Card[]> allRuns = getAllRuns(hand);
        // We will use this list to keep track of cards we have seen in the run, for duplicate and pair checking
        ArrayList<Cribbage.Rank> visitedRanks = new ArrayList<>();
        // Use this list to convert the card arrays into an ArrayList for easy containment checking
        ArrayList<Card> runList = new ArrayList<>();
        outerLoop:
        for (Card[] run: allRuns) {
            // Clear all list from the previous round
            visitedRanks.clear();
            runList.clear();
            // Starting from the end of the hand's cards, check that every card appears in the sequence, and that there are
            // no duplicates, otherwise move on to the next run sequence
            Collections.addAll(runList, run);
            for (int i = cardList.size() - 1; i >= cardList.size() - run.length; i--) {
                if (!runList.contains(cardList.get(i)) || visitedRanks.contains((Cribbage.Rank)cardList.get(i).getRank())) {
                    continue outerLoop;
                }

                visitedRanks.add((Cribbage.Rank) cardList.get(i).getRank());
            }

            // This is the largest run from the end of the had's cards, which does not span a pair
            return run;
        }

        // Did not find any runs that qualify. Return empty list
        return new Card[0];
    }

    /**
     * Returns the score for the longest run that has been completed by the last card in the given hand, such that the
     * run does not span any pairs, triplets or quads. If no such run exists, a score of 0 is returned
     * @param hand The hand of cards to be evaluated
     * @return The score of the longest run, or 0 if no such runexists
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        Card[] longestRun = getLongestRunFromEnd(hand);
        // No such run exists
        if (longestRun.length == 0) {
            return 0;
        }
        // Else add to cache and return
        int score = getRunScore(longestRun.length);
        addToCache(score, RUN_STR + longestRun.length, null);
        return score;
    }
}
