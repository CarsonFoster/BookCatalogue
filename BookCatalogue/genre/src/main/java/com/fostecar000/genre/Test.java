package com.fostecar000.genre;

import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

public class Test {
    private String label;
    private Runnable code;

    public static void main(String args[]) {
        genreTests();
    }

    private static void genreTests() {
        ArrayList<Test> tests = new ArrayList<>();

        Test testDL4J = new Test("testDL4J", () -> {
            try {
                TestDL4J.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Test runGenreIdentifier = new Test("runGenreIdentifier", () -> {
            try {
                GenreIdentifier.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Test testApostropheReplace = new Test("testApostropheReplace", () -> {
            try {
                Scanner fin = new Scanner(new File("ai_data\\train.txt"), "UTF-8");
                while (fin.hasNextLine()) {
                    String s = fin.nextLine();
                    System.out.println(s);
                    System.out.println(s.replace("\u2019", "'"));
                    System.out.println();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        Test testGLSP = new Test("testGLSP", () -> {
            try {
                GenreLabeledSentenceProvider glsp = new GenreLabeledSentenceProvider("C:\\users\\cwf\\documents\\bookcatalogue\\git\\bookcatalogue\\ai_data\\train.txt");
                for (int i = 0; i < 30; i++) {
                    System.out.println(glsp.nextSentence());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //tests.add(testDL4J)
        //tests.add(testBert);
        tests.add(runGenreIdentifier);
        //tests.add(testApostropheReplace);
        //tests.add(testGLSP);

        runTests(tests);
    }

    private Test(String label, Runnable code) {
        this.label = label;
        this.code = code;
    }

    protected String getLabel() {
        return label;
    }

    protected Runnable getCode() {
        return code;
    }

    private static void runTests(List<Test> tests) {
        for (Test t : tests)
            t.getCode().run();
    }
}