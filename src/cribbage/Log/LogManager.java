package cribbage.Log;

import cribbage.Cribbage;

import java.util.ArrayList;

public class LogManager extends Logger {
    ArrayList<Logger> loggers = new ArrayList<>();

    public LogManager(Cribbage cribbage) {
        super.cribbage = cribbage;
        resetLog();
        loggers.add(new SetUpLogger());
        loggers.add(new StartLogger());
        loggers.add(new ScoreLogger());
    }

    @Override
    public void update() {
        for (Logger logger : loggers) logger.update();
    }
}
