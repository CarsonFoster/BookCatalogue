package com.fostecar000.genre;

import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.nd4j.common.primitives.Pair;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

public class GenreLabeledSentenceProvider implements LabeledSentenceProvider {
    private static List<String> labels;
    private static final int numClasses = 42;
    private Scanner fin;
    private String filePath;

    public GenreLabeledSentenceProvider(String filePath) throws IOException {
        this.filePath = filePath;
        fin = new Scanner(new File(filePath));
    }

    private static void createLabels() {
        if (labels == null) {
            labels = new ArrayList<String>();
            labels.add("Nonfiction");
            labels.add("Fiction");
            labels.add("Children’s Books");
            labels.add("Mystery & Suspense");
            labels.add("Religion & Philosophy");
            labels.add("Romance");
            labels.add("Biography & Memoir");
            labels.add("Suspense & Thriller");
            labels.add("Graphic Novels & Manga");
            labels.add("Historical Fiction");
            labels.add("Classics");
            labels.add("Teen & Young Adult");
            labels.add("Personal Growth");
            labels.add("Arts & Entertainment");
            labels.add("Health & Fitness");
            labels.add("Cooking");
            labels.add("Fantasy");
            labels.add("History");
            labels.add("Politics");
            labels.add("Reference");
            labels.add("Science");
            labels.add("Science Fiction");
            labels.add("Art");
            labels.add("Humor");
            labels.add("Paranormal Fiction");
            labels.add("Crafts, Home & Garden");
            labels.add("Diet & Nutrition");
            labels.add("Parenting");
            labels.add("Poetry");
            labels.add("Sports");
            labels.add("Crafts & Hobbies");
            labels.add("Travel");
            labels.add("Business");
            labels.add("Technology");
            labels.add("Gothic & Horror");
            labels.add("Photography");
            labels.add("Music");
            labels.add("Games");
            labels.add("Language");
            labels.add("Film");
            labels.add("Design");
            labels.add("Economics");
        }
    }

    public List<String> allLabels() {
        createLabels();
        return labels;
    }

    public boolean hasNext() {
        return fin.hasNextLine();
    }

    public Pair<String, String> nextSentence() {
        String line = fin.nextLine().trim();
        int indexOfDelimiter = line.indexOf("####");
        String text = line.substring(0, indexOfDelimiter);
        String label = line.substring(indexOfDelimiter + 4);
        return Pair.create(text, label);
    }

    public int numLabelClasses() {
        return numClasses;
    }

    public void reset() {
        try {
            fin = new Scanner(new File(filePath));
        } catch (IOException e) {
            fin = new Scanner(""); // if error, set scanner to read from empty string; hasNext will then return false
        }
    }

    public int totalNumSentences() {
        return -1; // we don't know how many there are
    }
}