package cribbage.Log;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class ShowLogger extends Logger {
    @Override
    public void update() {
        switch (cribbage.getGamePhase()) {
            case SHOW: break;
            default: return;
        }
        // get the current player
        int currentPlayer = cribbage.getGameInfo().getCurrentPlayer();
        String logString = "show,P" + currentPlayer + ",";
        Card starter = cribbage.getStarter().getFirst();
        ArrayList<Card> cardList = new ArrayList<>(cribbage.getGameInfo().getCurrentHand().getCardList());
        cardList.remove(starter);
        logString += cribbage.canonical(starter) + "+";
        logString += cribbage.canonical(cardList);
        printlnLog(logString);
    }
}
