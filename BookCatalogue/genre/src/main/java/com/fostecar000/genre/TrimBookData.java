package com.fostecar000.genre;

import java.io.*;
import java.util.*;

public class TrimBookData {
    
    private static HashMap<String, Integer> createGenreHashMap() {
        HashMap<String, Integer> genres = new HashMap<>();
        genres.put("Nonfiction", 0);
        genres.put("Fiction", 1);
        genres.put("Children’s Books", 2);
        genres.put("Mystery & Suspense", 3);
        genres.put("Religion & Philosophy", 4);
        genres.put("Romance", 5);
        genres.put("Biography & Memoir", 6);
        genres.put("Suspense & Thriller", 7);
        genres.put("Graphic Novels & Manga", 8);
        genres.put("Historical Fiction", 9);
        genres.put("Classics", 10);
        genres.put("Teen & Young Adult", 11);
        genres.put("Personal Growth", 12);
        genres.put("Arts & Entertainment", 13);
        genres.put("Health & Fitness", 14);
        genres.put("Cooking", 15);
        genres.put("Fantasy", 16);
        genres.put("History", 17);
        genres.put("Politics", 18);
        genres.put("Reference", 19);
        genres.put("Science", 20);
        genres.put("Science Fiction", 21);
        genres.put("Art", 22);
        genres.put("Humor", 23);
        genres.put("Paranormal Fiction", 24);
        genres.put("Crafts, Home & Garden", 25);
        genres.put("Diet & Nutrition", 26);
        genres.put("Parenting", 27);
        genres.put("Poetry", 28);
        genres.put("Sports", 29);
        genres.put("Crafts & Hobbies", 30);
        genres.put("Travel", 31);
        genres.put("Business", 32);
        genres.put("Technology", 33);
        genres.put("Gothic & Horror", 34);
        genres.put("Photography", 35);
        genres.put("Music", 36);
        genres.put("Games", 37);
        genres.put("Language", 38);
        genres.put("Film", 39);
        genres.put("Design", 40);
        genres.put("Economics", 41);
        return genres;
    }
    
    private static HashSet<String> createGenreHashSet() {
        HashSet<String> genres = new HashSet<>();
        genres.add("Nonfiction");
        genres.add("Fiction");
        genres.add("Children’s Books");
        genres.add("Mystery & Suspense");
        genres.add("Religion & Philosophy");
        genres.add("Romance");
        genres.add("Biography & Memoir");
        genres.add("Suspense & Thriller");
        genres.add("Graphic Novels & Manga");
        genres.add("Historical Fiction");
        genres.add("Classics");
        genres.add("Teen & Young Adult");
        genres.add("Personal Growth");
        genres.add("Arts & Entertainment");
        genres.add("Health & Fitness");
        genres.add("Cooking");
        genres.add("Fantasy");
        genres.add("History");
        genres.add("Politics");
        genres.add("Reference");
        genres.add("Science");
        genres.add("Science Fiction");
        genres.add("Art");
        genres.add("Humor");
        genres.add("Paranormal Fiction");
        genres.add("Crafts, Home & Garden");
        genres.add("Diet & Nutrition");
        genres.add("Parenting");
        genres.add("Poetry");
        genres.add("Sports");
        genres.add("Crafts & Hobbies");
        genres.add("Travel");
        genres.add("Business");
        genres.add("Technology");
        genres.add("Gothic & Horror");
        genres.add("Photography");
        genres.add("Music");
        genres.add("Games");
        genres.add("Language");
        genres.add("Film");
        genres.add("Design");
        genres.add("Economics");
        return genres;
    }
    
    public static void main(String[] args) throws IOException {
        final String pathIn = "C:\\users\\cwf\\documents\\bookcatalogue\\blurbgenrecollectionen\\blurbgenrecollection_en_";
        final String pathOut = "C:\\users\\cwf\\documents\\bookcatalogue\\git\\bookcatalogue\\ai_data\\";
        String[][] files = new String[][] {new String[] {"train.txt", "train.txt"},
                                           new String[] {"test.txt", "test.txt"},
                                           new String[] {"dev.txt", "validation.txt"}};
        HashSet<String> genres = createGenreHashSet();
        for (String[] pair : files)
            trimBookData(pathIn + pair[0], pathOut + pair[1], genres);
    }
    
    private static void trimBookData(String fileIn, String fileOut, HashSet<String> genres) throws IOException {
        System.out.println("Trimming file " + fileIn + " ...");
        Scanner fin = new Scanner(new File(fileIn));
        StringBuilder lineToOutput = new StringBuilder();
        PrintWriter fout = new PrintWriter(fileOut);
        
        int books = 0;
        while (fin.hasNextLine()) {
            String line = fin.nextLine().trim();
            if (line.startsWith("<body>")) {
                lineToOutput.append(line.substring(6, line.length() - 7));
                lineToOutput.append("####");
            } else if (line.startsWith("<topics>")) {
                String topics = fin.nextLine().trim();
                boolean record = false;
                StringBuilder topicToChoose = new StringBuilder();
                String topicToWrite = "Fiction";
                
                for (int i = 0; i < topics.length(); i++) {
                    char c = topics.charAt(i);
                    if (c == '>')
                        record = true;
                    else if (c == '<') {
                        record = false;
                    } else if (c == '/') {
                        String topicStr = topicToChoose.toString();
                        if (genres.contains(topicStr)) topicToWrite = topicStr;
                        topicToChoose = new StringBuilder();
                    } else if (record) {
                        topicToChoose.append(c);
                    }
                }
                lineToOutput.append(topicToWrite);
                fin.nextLine();
                fout.println(lineToOutput.toString());
                lineToOutput = new StringBuilder();
                books++;
                if (books % 1000 == 0) System.out.println("Processed " + books);
            }
        }
        fout.close();
        System.out.println("Done with file " + fileIn);
    }
}
