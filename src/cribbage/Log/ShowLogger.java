/**
 * Created by Hoang Dang (1080344) and Ehsan Soltani Abhari (1003877)
 * Workshop 16 Team 06.
 */

package cribbage.Log;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;

/**
 * ShowLogger is used for logging Show events (when the hands are showed at the end of the game).
 */
class ShowLogger extends Logger {
    @Override
    void update() {
        // Check that the current gamePhase is a SHOW phase
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
