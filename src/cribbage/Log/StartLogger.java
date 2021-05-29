/**
 * Created by Hoang Dang (1080344) and Ehsan Soltani Abhari (1003877)
 * Workshop 16 Team 06.
 */

package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;
import java.util.ArrayList;

/**
 * StartLogger is used for logging Start events (discarding 2 cards and picking starter cards).
 */
class StartLogger extends Logger {
    @Override
    void update() {
        // Check that the current gamePhase is a START phase
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
            // The dealer has to start with score of 0
            int dealerScore = 0;
            // This will be the dealer's score after the change
            dealerScore += cache.getScore();

            // Log the score of dealer
            logString = "score,P1," + dealerScore + ",";
            logString += cache.getScore() + "," + cache.getScoreType() + ",";
            logString += "[" + cribbage.canonical(starter) + "]";
            printlnLog(logString);
        }
    }
}
