package cribbage.Score;

import ch.aplu.jcardgame.Hand;

/** For evaluating the score of the game of Cribbage in a particular situation */
public abstract class Scorer {
    /**
     * Evaluates the state of the game and returns a score accordingly
     * @param hand The hand of cards to be evaluated
     * @return An integer score depending on the evaluation
     */
    public abstract int evaluate(Hand hand);
}
