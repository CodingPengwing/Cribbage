package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.*;

/** Assesses the runs in the given hand in accordance with the rules of the show phase */
class RunScorer extends Scorer {
    final static String RUN_STR = "run";
    // A map of each run (key) and its respective reward score (value)
    final static HashMap<Integer, Integer> RUN_SCORES = new HashMap<>();
    final static int MAX_RUN = 7;
    final static int MIN_RUN = 3;

    // Returns the map of runs and their respective scores
    final static int getRunScore(int runLength) {
        return RUN_SCORES.get(runLength);
    }

    /**
     * Creates a new instance of RunScorerShow
     */
    public RunScorer() {
        // The scores for respective runs, Change these value to change the reward score for a particular run
        for (int i = MIN_RUN; i <= MAX_RUN; i++)
            RUN_SCORES.put(i, Cribbage.getPropertyInt("run"+i+"Score"));
    }

    // Returns a list of all possible runs in the given hand
    protected ArrayList<Card[]> getAllRuns(Hand hand) {
        ArrayList<Card[]> allRuns = new ArrayList<>();
        for (int i = MAX_RUN; i >= MIN_RUN; i--) {
            allRuns.addAll(hand.getSequences(i));
        }

        return allRuns;
    }

    /**
     * Finds all runs of between 3 and 5 (inclusive) and returns an appropriate total score for them
     * @param hand The hand of cards to be evaluated
     * @return The total score of all runs, and 0 if no runs are present in the given hand
     */
    @Override
    public int evaluate(Hand hand) {
        clearCache();
        int totalScore = 0;
        ArrayList<Card[]> allRuns = getAllRuns(hand);

        for (Card[] run: allRuns) {
            ArrayList<Card> cardList = new ArrayList<>(Arrays.asList(run));
            int score = RUN_SCORES.get(run.length);
            addToCache(score, RUN_STR + run.length, cardList, new ScorerCache.CacheComparator());
            totalScore += score;
        }

        return totalScore;
    }
}
