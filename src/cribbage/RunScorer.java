package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.HashMap;

/** Assesses the runs in the given hand in accordance with the rules of the show phase */
public class RunScorer extends Scorer {
    // A map of each run (key) and its respective reward score (value)
    private static final HashMap<Integer, Integer> RUN_SCORES = new HashMap<>();

    // Returns the maximum run size allowed. Defined as a method so that it can be overridden by child classes
    protected int getMaxRun() {
        return 5;
    }

    // The minimum run size allowed. The jcardgame framework used only supports sequences of size 3 or larger for their
    // sequence methods, which happens to align with the minimum run size in Cribbage
    protected int getMinRun() {
        return 3;
    }

    // Returns the map of runs and their respective scores
    protected HashMap<Integer, Integer> getRunScores() {
        return RUN_SCORES;
    }

    /**
     * Creates a new instance of RunScorerShow
     */
    public RunScorer() {
        // The scores for respective runs, Change these value to change the reward score for a particular run
        RUN_SCORES.put(3, 3);
        RUN_SCORES.put(4,4);
        RUN_SCORES.put(5,5);
        RUN_SCORES.put(6,6);
        RUN_SCORES.put(7,7);
    }

    // Returns a list of all possible runs in the given hand
    protected ArrayList<Card[]> getAllRuns(Hand hand) {
        ArrayList<Card[]> allRuns = new ArrayList<>();
        for (int i = getMaxRun(); i > getMinRun(); i--) {
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
        int totalScore = 0;
        ArrayList<Card[]> allRuns = getAllRuns(hand);
        for (Card[] run: allRuns) {
            totalScore += RUN_SCORES.get(run.length);
        }

        return totalScore;
    }
}
