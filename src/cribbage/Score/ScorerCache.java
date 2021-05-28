package cribbage.Score;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.Comparator;

public class ScorerCache {
    private int score;
    private String scoreType;
    private ArrayList<Card> cards;

    /** Comparator class to compare ScoreCaches by size and then alphabetically
     */
    public static class CacheComparator implements Comparator<ScorerCache> {
        /** compares 2 cache objects by size and then by alphabetical order
         * @param o1 cache object 1 to compare
         * @param o2 cache object 2 to compare
         * @return integer value representing the ordering of objects
         */
        @Override
        public int compare(ScorerCache o1, ScorerCache o2) {
            if (o1.cards.size() < o2.cards.size()) return -1;
            if (o1.cards.size() > o2.cards.size()) return 1;
            // Now the sizes are equal
            Cribbage cribbage = Cribbage.getInstance();
            return cribbage.canonical(o1.cards).compareTo(cribbage.canonical(o2.cards));
        }
    }

    /** Comparator class to compare ScoreCaches only alphabetically
     */
    public static class CacheComparatorAlphabetical implements Comparator<ScorerCache> {
        /** compares 2 cache objects by alphabetical order
         * @param o1 cache object 1 to compare
         * @param o2 cache object 2 to compare
         * @return integer value representing the ordering of objects
         */
        @Override
        public int compare(ScorerCache o1, ScorerCache o2) {
            Cribbage cribbage = Cribbage.getInstance();
            return cribbage.canonical(o1.cards).compareTo(cribbage.canonical(o2.cards));
        }
    }


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

}
