package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;

import java.util.ArrayList;

public class StartLogger extends Logger {
    @Override
    public void update() {
        switch (cribbage.getGameInfo().getGamePhase()) {
            case START: break;
            default: return;
        }
        // Log the discarded cards
        String logString;
        ArrayList<ArrayList<Card>> playerDiscards = cribbage.getPlayerDiscards();
        for (int i = 0; i < playerDiscards.size(); i++) {
            logString = "discard,P" + i + ",";
            logString += cribbage.canonical(playerDiscards.get(i));
            printlnLog(logString);
        }
        // Log the starter card
        Card starter = cribbage.getStarter().get(0);
        logString = "starter," + cribbage.canonical(starter);
        printlnLog(logString);

        // Log the score for dealer starter card is a jack
        Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
        if (scorer.getCache().size() > 0) {
            // Dealer is always P1, starting scores have to be 2, 2
            ScorerCache cache = scorer.getCache().get(0);
            logString = "score,P1,2," + cache.getScore() + "," + cache.getScoreType();
            logString += ",["+ cribbage.canonical(starter) +"]";
            printlnLog(logString);
        }
    }
}
