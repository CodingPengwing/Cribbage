/**
 * Created by Hoang Dang (1080344) and Ehsan Soltani Abhari (1003877)
 * Workshop 16 Team 06.
 */

package cribbage.Score;

import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

/**
 * Composite Strategy pattern. This class defines the behaviour of all ScorerComposite.
 * ScorerComposites work like Scorers by taking in a Hand and returning a value.
 * However, they contain a list of Scorers that they use to sum a score.
 */
class ScorerComposite extends Scorer {
    private final ArrayList<Scorer> scorers = new ArrayList<>();

    /** Constructor for ScorerComposite, only usable inside the Score package.
     */
    ScorerComposite() {}

    final void addScorer(Scorer scorer) {
        scorers.add(scorer);
    }

    final void removeScorer(Scorer scorer) {
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
        updateCache();
        return score;
    }

    // Updates the cache of the composite by going through each Scorer in its
    // list and adding their caches to its own cache.
    private void updateCache() {
        clearCache();
        for (Scorer scorer : scorers) {
            addAllToCache(scorer.getCache());
        }
    }

    /** Updates the cache of the composite from all of its Scorers in the list.
     * Then return that cache.
     * @return cache - the list of all ScorerCaches contained in the composite.
     */
    @Override
    public ArrayList<ScorerCache> getCache() {
        updateCache();
        return super.getCache();
    }
}
