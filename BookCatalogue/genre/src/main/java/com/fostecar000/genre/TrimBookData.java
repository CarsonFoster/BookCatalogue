package com.fostecar000.genre;

import java.io.*;
import java.util.*;

public class TrimBookData {
    public static void main(String[] args) throws IOException {
        final String path = "C:\\users\\cwf\\documents\\bookcatalogue\\blurbgenrecollectionen\\";
        final String fileIn = "blurbgenrecollection_en_dev.txt";
        final String fullPathIn = path + fileIn;
        final String fileOut = "D:\\validation.txt";
        Scanner fin = new Scanner(new File(fullPathIn));
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
                int t = 0;
                boolean record = false;
                for (int i = 0; i < topics.length(); i++) {
                    char c = topics.charAt(i);
                    if (c == '>')
                        record = true;
                    else if (c == '<') {
                        record = false;
                    } else if (c == '/') {
                        lineToOutput.append(',');
                        t++;
                        if (t == 5) break;
                    } else if (record) {
                        lineToOutput.append(c);
                    }
                }
                fin.nextLine();
                lineToOutput.deleteCharAt(lineToOutput.length() - 1); // delete last comma
                fout.println(lineToOutput.toString());
                lineToOutput = new StringBuilder();
                books++;
                if (books % 1000 == 0) System.out.println("Processed " + books);
            }
        }
    }
}
