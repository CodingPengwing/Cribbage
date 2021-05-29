package cribbage.Log;

/**
 * PlayLogger is used for logging Play events (when a player plays a card).
 */
class PlayLogger extends Logger {
    @Override
    void update() {
        // Check that the current gamePhase is a PLAY phase
        switch (cribbage.getGamePhase()) {
            case PLAY: break;
            default: return;
        }
        // get the current player
        String logString = "play,P" + cribbage.getGameInfo().getCurrentPlayer();
        // get the current total of the cards (in current segment)
        logString += "," + cribbage.getCurrentSegmentTotal();
        // get the current card (most recently played)
        logString += "," + cribbage.canonical(cribbage.getLastPlayedCard());
        printlnLog(logString);
    }
}
