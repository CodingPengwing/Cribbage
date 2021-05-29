package cribbage;

import ch.aplu.jcardgame.Card;

// All Player related functionality has been made package-private

/**
 * A random computer player that picks a random card for every action.
 */
public class RandomPlayer extends IPlayer {
	@Override
	Card discard() {
		return Cribbage.randomCard(hand);
	}

	@Override
	Card selectToLay() {
		return hand.isEmpty() ? null : Cribbage.randomCard(hand);
	}
}
