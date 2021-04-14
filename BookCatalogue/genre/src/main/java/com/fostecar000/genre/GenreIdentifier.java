package com.fostecar000.genre;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRegexRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;

import java.util.List;
import java.io.File;
import java.io.IOException;

public class GenreIdentifier {
    public static void main(String[] args) throws IOException, InterruptedException {
        
        String trainingData = "ai_data\\train.txt";
        String validatingData = "ai_data\\validation.txt";
        String testingData = "ai_data\\test.txt";

        int numLinesToSkip = 0;
        String delimiter = "####";
        String quote = null; // do not strip quotes
        String[] regex = null; // no further parsing for now

        RecordReader recordReader = new CSVRegexRecordReader(numLinesToSkip, delimiter, quote, regex);
        recordReader.initialize(new FileSplit(new File(trainingData)));

        List<Writable> tmp = recordReader.next();
        for (Writable w : tmp) System.out.println(w);
    }    
}