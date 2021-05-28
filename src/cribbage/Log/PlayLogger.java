package cribbage.Log;

public class PlayLogger extends Logger {
    @Override
    public void update() {
        String logString = "play,P" + cribbage.getGameInfo().getCurrentPlayer();
        logString += ",";
        // TODO:
        // logString +=

    }
}
