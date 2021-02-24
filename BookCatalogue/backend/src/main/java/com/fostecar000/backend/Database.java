package com.fostecar000.backend;

import java.util.Properties;
import java.io.FileReader;

public class Database implements AutoCloseable {
    private Process db;
    protected static final String NON_WINDOWS_MSG = "I didn't write an installer for non-Windows for a reason. You're going to have to start "
                                                    + "the database yourself if you want the program to run. You can find more information "
                                                    + "about the database in the database.properties file."; // for use when the client is not running Windows (just in case)

    private static class StreamGobbler extends Thread {
        
    }

    public Database() {
        Properties p = new Properties();
        p.load(new FileReader("database.properties"));
        String command = p.getProperty("executableLocation") + p.getProperty("arguments"); // I am aware of the unsafeness of this.
                                                                                           // However, this program is not intended to be used as a service, but by a user
                                                                                           // with user permissions, and should not have any elevated privileges.
        Runtime rt = Runtime.getRuntime();
        db = rt.exec(command);

        if (!isRunning()) {
            throw new RuntimeException("Failed to start the specified database. See database.properties for information on specified database");
        }
    }

    public Database(boolean startedAlready) {
        // I'm trusting you here
    }

    public boolean isRunning() {

    }

    @Override
    void close() {

    }
}