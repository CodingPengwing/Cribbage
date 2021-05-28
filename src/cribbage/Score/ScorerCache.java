package cribbage.Score;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;

import java.util.ArrayList;

public class ScorerCache implements Comparable<ScorerCache> {
    private int score;
    private String scoreType;
    private ArrayList<Card> cards;

    public ScorerCache(int score, String scoreType, ArrayList<Card> cards) {
        this.score = score;
        this.scoreType = scoreType;
        this.cards = cards;
    }

    public int getScore() {
        return score;
    }

    public String getScoreType() {
        return scoreType;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    /** compares 2 cache objects by size and then by alphabetical order.
     * @param o other cache object to compare to
     * @return integer representing comparison
     */
    @Override
    public int compareTo(ScorerCache o) {
        if (this.cards.size() < o.cards.size()) return -1;
        if (this.cards.size() > o.cards.size()) return 1;
        // Now the sizes are equal
        return this.cards.toString().compareTo(o.cards.toString());
    }
}
