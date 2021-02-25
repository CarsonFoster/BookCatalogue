package com.fostecar000.backend;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.net.URISyntaxException;

public class Database implements AutoCloseable {
    private Process db;
    private Properties p;
    private boolean userHandling; // is the user handling the start and stop of the database?
    protected static final String NON_WINDOWS_MSG = "I didn't write an installer for non-Windows for a reason. You're going to have to start "
                                                    + "the database yourself if you want the program to run. You can find more information "
                                                    + "about the database in the database.properties file."; // for use when the client is not running Windows (just in case)

    private static class StreamGobbler extends Thread {
        private InputStream is;
        StreamGobbler(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                int character = -1;
                while ((character = br.read()) != -1) System.out.print((char)character);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Database() throws IOException, FileNotFoundException, NullPointerException, URISyntaxException {
        userHandling = false;
        p = new Properties();
        p.load(getClass().getClassLoader().getResourceAsStream("database.properties"));
        String executable = p.getProperty("executableLocation");
        String args = p.getProperty("arguments");
        String replaced = replaceArgs(args);
        String command = executable + (replaced != null ? " " + replaced : "");                // I am aware of the unsafeness of this.
                                                                                             // However, this program is not intended to be used as a service, but by a user
                                                                                             // with user permissions, and should not have any elevated privileges.
        Runtime rt = Runtime.getRuntime();
        db = rt.exec(command);
        new StreamGobbler(db.getInputStream()).start();
        new StreamGobbler(db.getErrorStream()).start();

        if (!isRunning()) {
            throw new RuntimeException("Failed to start the specified database. See database.properties for information on specified database");
        }
    }

    public Database(boolean startedAlready) {
        userHandling = true;
    }

    public boolean isRunning() {
        return userHandling || (db != null && db.isAlive());
    }

    public boolean isUserHandling() {
        return userHandling;
    }

    private String getAbsolutePath(String resource) throws IOException, NullPointerException, URISyntaxException {
        return new File(getClass().getClassLoader().getResource(resource).toURI()).getAbsolutePath();
    }

    private String replaceArgs(String args) throws IOException, NullPointerException, URISyntaxException {
        if (args == null) return null;
        String replaced = new String(args);
        for (int i = 0; i < args.length(); i++) {
            if (args.charAt(i) == '{') {
                StringBuilder sb = new StringBuilder();
                boolean foundMatch = false;
                while (++i < args.length()) {
                    if (args.charAt(i) != '}') sb.append(args.charAt(i));
                    else {
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) throw new RuntimeException("did not find matching curly bracket in String \"" + args + "\"");
                String resource = sb.toString();
                replaced = replaced.replace("{" + resource + "}", getAbsolutePath(resource));
            }
        }
        return replaced;
    }

    @Override
    public void close() throws Exception, IOException, InterruptedException, NullPointerException, URISyntaxException {
        if (userHandling) return;
        String closeExecutable = p.getProperty("closeExecutableLocation");
        String args = p.getProperty("closeArguments");
        String replaced = replaceArgs(args);
        String command = closeExecutable + (replaced != null ? " " + replaced : "");
        //System.out.println(command);

        Runtime rt = Runtime.getRuntime();
        Process close = rt.exec(command);
        new StreamGobbler(close.getInputStream()).start();
        new StreamGobbler(close.getErrorStream()).start();

        close.waitFor(5L, TimeUnit.SECONDS);
    }
}