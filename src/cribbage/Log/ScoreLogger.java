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
            case PLAY_SCORE: case SHOW_SCORE: case PLAY_SCORE_GO: break;
            // Not a scoring phase, return
            default: return;
        }
        System.out.println("HELLo");
        Scorer scorer = ScorerCompositeFactory.getInstance().getScorerComposite();
        ArrayList<ScorerCache> cacheList = scorer.getCache();
        int currentPlayerScore = cribbage.getGameInfo().getCurrentPlayerScore();

        for (ScorerCache cache : cacheList) {
            // start string with 'score'
            String logString = "score,";
            // add the player
            logString += "P" + cribbage.getGameInfo().getCurrentPlayer() + ",";
            // add the player's score
            logString += currentPlayerScore + ",";
            currentPlayerScore += cache.getScore();
            // add the new awarded score
            logString += cache.getScore() + ",";
            // add the new awarded score's cardList
            logString += cache.getScoreType();
            if (cribbage.getGameInfo().getGamePhase() == Cribbage.GamePhase.SHOW) {
                logString += "," + cardArrayListToString(cache.getCards());
            }
            printlnLog(logString);
        }
    }
}

