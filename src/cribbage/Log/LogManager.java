package cribbage.Log;

import cribbage.Cribbage;
import java.util.ArrayList;

/**
 * LogManager provides an interface for external classes to access the log functionality
 * through indirection. It follows a Composite Strategy pattern and includes a list of Loggers
 * to call upon. It is a Singleton because the current Cribbage implementation only allows for
 * one Cribbage at a time, thus there should only be one instance of Logger corresponding to
 * that instance of cribbage.
 */
public class LogManager extends Logger {
    private static final ArrayList<Logger> loggers = new ArrayList<>();
    private static LogManager instance = null;

    /**
     * Constructor for a LogManager
     */
    private LogManager(Cribbage cribbage) {
        super.cribbage = cribbage;
        super.LOG_FILE = Cribbage.getProperty("logFile");
        resetLog();
        loggers.add(new SetUpLogger());
        loggers.add(new StartLogger());
        loggers.add(new PlayLogger());
        loggers.add(new ScoreLogger());
        loggers.add(new ShowLogger());
    }

    final static public LogManager getInstance(Cribbage cribbage) {
        if (instance == null) instance = new LogManager(cribbage);
        return instance;
    }

    /** Notifies all Loggers that the game has been updated and needs to be logged. */
    @Override
    public void update() {
        for (Logger logger : loggers) logger.update();
    }
}
