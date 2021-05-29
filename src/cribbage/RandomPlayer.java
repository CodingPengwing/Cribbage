package cribbage;

import ch.aplu.jcardgame.Card;

// All Player related functionality has been made package-private

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
