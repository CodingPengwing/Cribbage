package cribbage.Log;

public class PlayLogger extends Logger {
    @Override
    public void update() {
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
