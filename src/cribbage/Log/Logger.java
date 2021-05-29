package cribbage.Log;

import cribbage.Cribbage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Parent Logger class that dictates how Loggers are supposed to operate
 */
abstract class Logger {
    static Cribbage cribbage;
    static String LOG_FILE;

    /** Abstract method to be implemented by all children logger classes.
     * Upon calling, the child logger needs to check whether it is their
     * turn to log something, and carry out the logic to log it.
     * */
    abstract void update();

    // Resets the log file that the loggers are going to write in
    final void resetLog() {
        try {
            // this just resets the file to be empty
            new FileWriter(LOG_FILE);
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    // Prints a string as a line into the log file
    final void printlnLog(String logString) {
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

