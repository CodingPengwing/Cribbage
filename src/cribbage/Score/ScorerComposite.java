package cribbage.Score;

import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

/** A Composite for Scorers */
public class ScorerComposite extends Scorer {
    private final ArrayList<Scorer> scorers = new ArrayList<>();

    public ScorerComposite() {}

    public void addScorer(Scorer scorer) {
        scorers.add(scorer);
    }

    public void removeScorer(Scorer scorer) {
        scorers.remove(scorer);
    }

    /**
     * Returns the total of the evaluation of all Scorers from this Composite
     * @param hand The hand of cards to be evaluated
     * @return The total of all Scorer evaluations
     */
    @Override
    public int evaluate(Hand hand) {
        int score = 0;
        for (Scorer scorer : scorers) score += scorer.evaluate(hand);
        return score;
    }

    @Override
    public ArrayList<ScorerCache> getCache() {
        for (Scorer scorer : scorers) {
            addAllToCache(scorer.getCache());
        }
        return getCache();
    }
}
