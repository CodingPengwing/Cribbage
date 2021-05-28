package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCompositeFactory;

import java.util.ArrayList;

public class StartLogger extends Logger {
    @Override
    public void update() {
        switch (cribbage.getGameInfo().getGamePhase()) {
            case START: break;
            default: return;
        }
        String logString;
        ArrayList<ArrayList<Card>> playerDiscards = cribbage.getPlayerDiscards();
        for (int i = 0; i < playerDiscards.size(); i++) {
            logString = "discard,P" + i + ",";
            logString += cardArrayListToString(playerDiscards.get(i));
            printlnLog(logString);
        }
        Card starter = cribbage.getStarter().get(0);
        logString = "starter," + cribbage.canonical(starter);
        printlnLog(logString);

        // TODO: IS IS ALWAYS P1? CAN DEALER BE P0???
        Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
        if (scorer.getCache().size() > 0) {
            logString = "score,P1,2,2" + cribbage.canonical(starter);
            printlnLog(logString);
        }
    }
}
