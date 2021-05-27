package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;

import java.util.ArrayList;

public class ScoreLogger extends Logger {

    @Override
    public void update() {
        Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
        ArrayList<ScorerCache> cacheList = scorer.getCache();
        int currentScore = cribbage.getGameInfo().getCurrentPlayerScore();
        Cribbage.GamePhase currentGamePhase = cribbage.getGamePhase();

        for (ScorerCache cache : cacheList) {
            String logString = "score,";

            logString += "P" + cribbage.getGameInfo().getCurrentPlayer() + ",";
            logString += currentScore + ",";
            currentScore += cache.getScore();
            logString += cache.getScore() + ",";
            if (cribbage.getGamePhase() == Cribbage.GamePhase.SHOW) {
                logString += cache.cardsToString();
            }
        }
        // TODO: print(logString) into log file
    }
}

