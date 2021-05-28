package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;
import cribbage.Score.Scorer;
import cribbage.Score.ScorerCache;
import cribbage.Score.ScorerCompositeFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

abstract class Logger {
    protected static Cribbage cribbage;
    protected static final String LOG_FILE = "cribbage.log";
//    private ArrayList<Logger> loggers = new ArrayList<>();

//    public Logger(Cribbage cribbage) {
//        this.cribbage = cribbage;
//    }


    public abstract void update();

    protected final void resetLog() {
        try {
            FileWriter fileWriter = new FileWriter(LOG_FILE);
            // BufferedWriter writer = new BufferedWriter(fileWriter);
            // writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    protected final void printlnLog(String logString) {
        try {
            FileWriter fileWriter = new FileWriter(LOG_FILE, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(logString + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final String cardArrayListToString(ArrayList<Card> cardList) {
        String str = "[";
        for (int i = 0; i < cardList.size(); i++) {
            str += Cribbage.getInstance().canonical(cardList.get(i));
            if (i < cardList.size()-1) str += ",";
        }
        str += "]";
        return str;
    }

}

