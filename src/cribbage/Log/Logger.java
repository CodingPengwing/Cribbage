package cribbage.Log;

import ch.aplu.jcardgame.Card;
import cribbage.Cribbage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

abstract class Logger {
    protected static Cribbage cribbage;
    protected static String LOG_FILE;

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
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

