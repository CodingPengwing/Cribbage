package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

abstract class Logger {
    protected static Cribbage cribbage;
    protected static final String LOG_FILE = "cribbage.log";


    public abstract void update();

    protected final void resetLog() {
        try {
            // this just resets the file to be empty
            new FileWriter(LOG_FILE);
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    protected final void printlnLog(String logString) {
        try {
            // open the file and write into it
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

