package cribbage.Score;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

/** For evaluating the score of the game of Cribbage in a particular situation */
public abstract class Scorer {
    private final ArrayList<ScoreCache> cache = new ArrayList<>();
    /**
     * Evaluates the state of the game and returns a score accordingly
     * @param hand The hand of cards to be evaluated
     * @return An integer score depending on the evaluation
     */
    public abstract int evaluate(Hand hand);

    protected ArrayList<ScoreCache> getCache() {
        return cache;
    }

    protected void addToCache(int score, String scoreType, ArrayList<Card> cardList) {
        this.cache.add(new ScoreCache(score, scoreType, cardList));
    }

    protected void clearCache() {
        this.cache.clear();
    }
}
