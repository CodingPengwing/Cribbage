package cribbage.Log;

import ch.aplu.jcardgame.Hand;
import java.util.ArrayList;

class SetUpLogger extends Logger {
    @Override
    void update() {
        // Check that the current gamePhase is a SETUP phase
        switch (cribbage.getGameInfo().getGamePhase()) {
            case SETUP: break;
            default: return;
        }
        // Get the seed info and print
        String logString = "seed," + cribbage.getSEED();
        printlnLog(logString);
        // Get the play types and print
        ArrayList<String> playerTypes = cribbage.getPlayerTypes();
        for (int i = 0; i < playerTypes.size(); i++) {
            logString = playerTypes.get(i) + ",P" + i;
            printlnLog(logString);
        }
        // Get the cards initially dealt and print
        Hand[] dealtHands = cribbage.getHands();
        for (int i = 0; i < dealtHands.length; i++) {
            Hand hand = dealtHands[i];
            if (hand == null) {
                System.err.println("Error in SetUpLogger.update(): null pointer in dealtHands.");
                System.exit(1);
            }
            logString = "deal,P" + i + ",";
            // turn the hand into a printable string
            logString += cribbage.canonical(hand.getCardList());
            printlnLog(logString);
        }
    }
}
