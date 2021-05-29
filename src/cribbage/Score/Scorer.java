/**
 * Created by Hoang Dang (1080344) and Ehsan Soltani Abhari (1003877)
 * Workshop 16 Team 06.
 */

package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Strategy pattern. This class defines the behaviour of all Scorers. Scorers are
 * to be given a Hand and are to return an integer value representing the worth
 * of that hand.
 */
public abstract class Scorer {
    private final ArrayList<ScorerCache> cache = new ArrayList<>();
    /**
     * Evaluates the state of the game and returns a score accordingly
     * @param hand The hand of cards to be evaluated
     * @return An integer score depending on the evaluation
     */
    public abstract int evaluate(Hand hand);

    public ArrayList<ScorerCache> getCache() {
        return new ArrayList<>(cache);
    }

    void addToCache(int score, String scoreType, ArrayList<Card> cardList) {
        if (cardList != null) {
            ArrayList<Card> sortedCardList = Cribbage.getInstance().sortCardList(cardList, Hand.SortType.POINTPRIORITY);
            this.cache.add(new ScorerCache(score, scoreType, sortedCardList));
        } else {
            this.cache.add(new ScorerCache(score, scoreType, null));
        }
    }

    void addToCache(int score, String scoreType, ArrayList<Card> cardList, Comparator<ScorerCache> comparator) {
        if (comparator == null) {
            System.err.println("Error in addAllToCache(): null comparator");
            System.exit(1);
        }
        addToCache(score, scoreType, cardList);
        cache.sort(comparator);
    }

    void addAllToCache(ArrayList<ScorerCache> cacheList) {
        this.cache.addAll(cacheList);
    }

    void addAllToCache(ArrayList<ScorerCache> cacheList, Comparator<ScorerCache> comparator) {
        if (comparator == null) {
            System.err.println("Error in addAllToCache(): null comparator");
            System.exit(1);
        }
        this.cache.addAll(cacheList);
        cache.sort(comparator);
    }

    public void clearCache() {
        this.cache.clear();
    }
}
