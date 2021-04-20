package com.fostecar000.genre;

/*import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRegexRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;*/
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator.Format;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;
import java.io.File;
import java.io.IOException;

public class GenreIdentifier {
    private static final String vectorPath = "C:\\Users\\cwf\\Documents\\BookCatalogue\\glove.6b\\glove.6b.100d.bin";

    public static void main(String[] args) throws IOException, InterruptedException {
        
        String trainingData = "ai_data\\train.txt";
        String validatingData = "ai_data\\validation.txt";
        String testingData = "ai_data\\test.txt";

        /*int numLinesToSkip = 0;
        String delimiter = "####";
        String quote = null; // do not strip quotes
        String[] regex = null; // no further parsing

        RecordReader recordReader = new CSVRegexRecordReader(numLinesToSkip, delimiter, quote, regex);
        recordReader.initialize(new FileSplit(new File(trainingData)));

        int batchSize = 1000;
        int labelIndex = 1; // index 0 = blurb, index 1 = genre label
        int numClasses = 42; // I'm using 42 different genre identifiers

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);*/
        
        int batchSize = 32;
        int vectorSize = 100;
        int truncateBlurbsToLength = 256; // truncate blurbs to have at most 256 words

        Nd4j.getMemoryManager().setAutoGcWindow(10000);
        // Nd4j.getEnvironment().allowHelpers(false); // uncommenting allows you to read one word2vec txt file, otherwise, can only read binary files in a timely manner

        Word2Vec wordVectors = WordVectorSerializer.readWord2VecModel(new File(vectorPath));

        DataSetIterator trainIter = getDataSetIterator(trainingData, wordVectors, batchSize, truncateBlurbsToLength);

    }

    private static DataSetIterator getDataSetIterator(String filePath, WordVectors wordVectors, int minibatchSize, int maxSentenceLength) throws IOException {
        GenreLabeledSentenceProvider glsp = new GenreLabeledSentenceProvider(filePath);

        return new CnnSentenceDataSetIterator.Builder(Format.CNN2D)
                                             .sentenceProvider(glsp)
                                             .wordVectors(wordVectors)
                                             .minibatchSize(minibatchSize)
                                             .maxSentenceLength(maxSentenceLength)
                                             .useNormalizedWordVectors(false)
                                             .build();
    }
}