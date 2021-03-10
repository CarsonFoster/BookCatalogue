package com.fostecar000.gui;

import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.*;
import com.fostecar000.backend.Database;

public class Test {
    private String label;
    private Runnable code;

    public static void main(String args[]) {
        guiTests();
    }

    private static void guiTests() {
        ArrayList<Test> tests = new ArrayList<>();

        Test insertionTest = new Test("insertionTest", () -> {
            try (Database db = new Database()) {
                TestGui.doInGuiThread(() -> {
                    Insertion.call(db);
                });
                TestGui.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Test basicSearchCall = new Test("basicSearchCall", () -> {
            Search.main(null);
        });

        Test searchTest = new Test("searchTest", () -> {
            try (Database db = new Database()) {
                TestGui.doInGuiThread(() -> {
                    Search.call(db);
                });
                TestGui.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //tests.add(insertionTest);
        //tests.add(basicSearchCall);
        tests.add(searchTest);
        
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