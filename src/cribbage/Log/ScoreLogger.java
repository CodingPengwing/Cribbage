package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;

import java.util.ArrayList;

class ScoreLogger extends Logger {

    @Override
    public void update() {
        // Check that the current gamePhase is a scoring phase
        switch (cribbage.getGamePhase()) {
            case PLAY_SCORE: case SHOW_SCORE: break;
            // Not a scoring phase, return
            default: return;
        }

        Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
        ArrayList<ScorerCache> cacheList = scorer.getCache();
        int currentScore = cribbage.getGameInfo().getCurrentPlayerScore();
        Cribbage.GamePhase currentGamePhase = cribbage.getGameInfo().getGamePhase();

        for (ScorerCache cache : cacheList) {
            String logString = "score,";

            logString += "P" + cribbage.getGameInfo().getCurrentPlayer() + ",";
            logString += currentScore + ",";
            currentScore += cache.getScore();
            logString += cache.getScore() + ",";
            if (cribbage.getGameInfo().getGamePhase() == Cribbage.GamePhase.SHOW) {
                logString += cardArrayListToString(cache.getCards());
            }
        }
        // TODO: print(logString) into log file
    }
}

