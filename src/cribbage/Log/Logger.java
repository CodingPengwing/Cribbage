package cribbage.Log;

import cribbage.Cribbage;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;

import java.util.ArrayList;

public abstract class Logger {
    protected Cribbage cribbage;

    public abstract void update();

//    protected void print

    // TODO: print(logString) into log file

}

