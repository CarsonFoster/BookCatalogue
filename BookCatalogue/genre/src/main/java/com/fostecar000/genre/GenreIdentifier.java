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
//import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.factory.Nd4j;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.saver.LocalFileGraphSaver;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingGraphTrainer;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;

public class GenreIdentifier {
    private static final String vectorPath = "C:\\Users\\cwf\\Documents\\BookCatalogue\\glove.6b\\glove.6b.100d.bin";
    private static Word2Vec wordVectors;
    private static ComputationGraph neuralNet;
    private static int truncateBlurbsToLength = 256; // truncate blurbs to have at most 256 words
    private static int batchSize = 32; // original: 32; 10, 48, 64 decreased accuracy at 10 min

    private static void loadWordVectors(String wordVectorLocation) {
        if (wordVectors == null)
            wordVectors = WordVectorSerializer.readWord2VecModel(new File(wordVectorLocation));
    }

    private static void loadNeuralNet(String modelLocation) throws IOException {
        if (neuralNet == null)
            neuralNet = ComputationGraph.load(new File(modelLocation), true);
    }

    public static String predictGenre(String wordVectorLocation, String modelLocation, String blurb) throws IOException {
        loadWordVectors(wordVectorLocation);
        loadNeuralNet(modelLocation);
        CnnSentenceDataSetIterator iterator = fromGLSP(new GenreLabeledSentenceProvider(), wordVectors, batchSize, truncateBlurbsToLength);
        INDArray featuresBlurb = iterator.loadSingleSentence(blurb);
        INDArray results = neuralNet.outputSingle(featuresBlurb);
        int numClasses = iterator.totalOutcomes();
        double maxProbability = 0;
        int maxLabelIndex = 0;

        for (int i = 0; i < numClasses; i++) {
            double probability = results.getDouble(i);
            if (probability > maxProbability) {
                maxProbability = probability;
                maxLabelIndex = i;
            }
        }

        return iterator.getLabels().get(maxLabelIndex);
    }

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

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);*/
        
        int vectorSize = 100;
        int featureMaps = 100;
        final int EPOCHS = 1;
        final int MINUTES = 120;

        Nd4j.getMemoryManager().setAutoGcWindow(10000);
        // Nd4j.getEnvironment().allowHelpers(false); // uncommenting allows you to read one word2vec txt file, otherwise, can only read binary files in a timely manner

        System.out.println("[*] Loading data...");
        loadWordVectors(vectorPath);

        DataSetIterator trainIter = getDataSetIterator(trainingData, wordVectors, batchSize, truncateBlurbsToLength);
        DataSetIterator validationIter = getDataSetIterator(validatingData, wordVectors, batchSize, truncateBlurbsToLength);
        System.out.println("[+] Done loading data.");

        // things to try:
        // - change learning rate
        // - lower l2 coefficient
        // - change minibatch size (10, 32, 16-128)
        // - test other updaters/optimizers

        // Full Disclosure: I am only passingly familiar with AI, this turned out to be more complicated than I thought it would be,
        //                  and I didn't have enough time to learn everything, so this is using other people's work
        // this configuration is based on Kim (2014) and the dl4j example below
        // https://github.com/eclipse/deeplearning4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/advanced/modelling/textclassification/pretrainedword2vec/ImdbReviewClassificationCNN.java
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.LEAKYRELU)
                .updater(new Adam(0.0005)) // 0.001 high maybe?, 0.0001 low
                .convolutionMode(ConvolutionMode.Same)
                .l2(0.0001) // original: 0.0001; 0.00001 55%; 0.001: 54%, 0.0005: 55%
                .graphBuilder()
                .addInputs("inputLayer")
                .setInputTypes(InputType.convolutional(truncateBlurbsToLength, vectorSize, 1))
                .addLayer("cnn_h=3", new ConvolutionLayer.Builder()
                    .kernelSize(3, vectorSize)
                    .stride(1, vectorSize)
                    .nOut(featureMaps)
                    .build(), "inputLayer")
                .addLayer("cnn_h=4", new ConvolutionLayer.Builder()
                    .kernelSize(4, vectorSize)
                    .stride(1, vectorSize)
                    .nOut(featureMaps)
                    .build(), "inputLayer")
                .addLayer("cnn_h=5", new ConvolutionLayer.Builder()
                    .kernelSize(5, vectorSize)
                    .stride(1, vectorSize)
                    .nOut(featureMaps)
                    .build(), "inputLayer")
                .addVertex("mergeVertex", new MergeVertex(), "cnn_h=3", "cnn_h=4", "cnn_h=5")
                .addLayer("maxOverTimePooling", new GlobalPoolingLayer.Builder()
                    .poolingType(PoolingType.MAX)
                    .dropOut(0.5)
                    .build(), "mergeVertex")
                .addLayer("outputLayer", new OutputLayer.Builder()
                    .lossFunction(LossFunctions.LossFunction.MCXENT)
                    .activation(Activation.SOFTMAX)
                    .nOut(GenreLabeledSentenceProvider.NUM_CLASSES)
                    .build(), "maxOverTimePooling")
                .setOutputs("outputLayer")
                .build();
        
        ComputationGraph neuralNet = new ComputationGraph(conf);
        neuralNet.init();

        System.out.println("[*] Starting training...");
        // print loss function value every 100 iterations and evaluate model at the end of each epoch
        neuralNet.setListeners(new ScoreIterationListener(100), new EvaluativeListener(validationIter, 1, InvocationType.EPOCH_END));
        //neuralNet.fit(trainIter, epochs);

        EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>()
            .epochTerminationConditions(new MaxEpochsTerminationCondition(EPOCHS))
            .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(MINUTES, TimeUnit.MINUTES))
            .scoreCalculator(new DataSetLossCalculator(validationIter, true))
            .evaluateEveryNEpochs(1)
            .modelSaver(new LocalFileGraphSaver("ai_data"))
            .build();

        EarlyStoppingGraphTrainer trainer = new EarlyStoppingGraphTrainer(esConf, neuralNet, trainIter);

        //Conduct early stopping training:
        EarlyStoppingResult<ComputationGraph> result = trainer.fit();

        //Print out the results:
        System.out.println("Termination reason: " + result.getTerminationReason());
        System.out.println("Termination details: " + result.getTerminationDetails());
        System.out.println("Total epochs: " + result.getTotalEpochs());
        System.out.println("Best epoch number: " + result.getBestModelEpoch());
        System.out.println("Score at best epoch: " + result.getBestModelScore());

        System.out.println("[+] Finished.");

    }

    private static DataSetIterator getDataSetIterator(String filePath, Word2Vec vectors, int minibatchSize, int maxSentenceLength) throws IOException {
        GenreLabeledSentenceProvider glsp = new GenreLabeledSentenceProvider(filePath);
        return fromGLSP(glsp, vectors, minibatchSize, maxSentenceLength);
    }

    private static CnnSentenceDataSetIterator fromGLSP(GenreLabeledSentenceProvider glsp, Word2Vec vectors, int minibatchSize, int maxSentenceLength) {
        return new CnnSentenceDataSetIterator.Builder(Format.CNN2D)
                                             .sentenceProvider(glsp)
                                             .wordVectors(vectors)
                                             .minibatchSize(minibatchSize)
                                             .maxSentenceLength(maxSentenceLength)
                                             .useNormalizedWordVectors(false)
                                             .build();
    }
}