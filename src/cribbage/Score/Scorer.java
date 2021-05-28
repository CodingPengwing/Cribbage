package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import cribbage.Cribbage;

import java.lang.reflect.Array;
import java.util.ArrayList;

/** For evaluating the score of the game of Cribbage in a particular situation */
public abstract class Scorer {
    private int cacheTotal;
    private final ArrayList<ScorerCache> cache = new ArrayList<>();
    /**
     * Evaluates the state of the game and returns a score accordingly
     * @param hand The hand of cards to be evaluated
     * @return An integer score depending on the evaluation
     */
    public abstract int evaluate(Hand hand);

    public ArrayList<ScorerCache> getCache() {
        return cache;
    }

    protected void addToCache(int score, String scoreType, ArrayList<Card> cardList) {
        if (cardList != null) {
            ArrayList<Card> sortedCardList = Cribbage.getInstance().sortCardList(cardList, Hand.SortType.POINTPRIORITY);
            this.cache.add(new ScorerCache(score, scoreType, sortedCardList));
        } else {
            this.cache.add(new ScorerCache(score, scoreType, null));
        }
    }


    protected void addAllToCache(ArrayList<ScorerCache> cacheList) {
        this.cache.addAll(cacheList);
    }

    public void clearCache() {
        this.cache.clear();
    }

    void updateCacheTotal() {
        cacheTotal = 0;
        for (ScorerCache c : cache) {
            cacheTotal += c.getScore();
        }
    }

    public int getCacheTotal() {
        updateCacheTotal();
        return cacheTotal;
    }

    void setCacheTotal(int total) {
        cacheTotal = total;
    }
}
