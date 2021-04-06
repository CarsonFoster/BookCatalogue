package com.fostecar000.genre;

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
        genreTests();
    }

    private static void genreTests() {
        ArrayList<Test> tests = new ArrayList<>();
        
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