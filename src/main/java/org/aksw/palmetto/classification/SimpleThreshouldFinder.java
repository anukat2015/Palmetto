package org.aksw.palmetto.classification;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.DoubleObjectOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;

public class SimpleThreshouldFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleThreshouldFinder.class);

    private static final int TRUE_POSITIVE_INDEX = 0;
    private static final int FALSE_POSITIVE_INDEX = 1;
    private static final int PRECISION_INDEX = 0;
    private static final int RECALL_INDEX = 1;
    private static final int F1_INDEX = 2;

    private static final double HUMAN_RATING_THRESHOULD_FOR_BEING_GOOD = 2.500000001;
    private static final double HUMAN_RATING_THRESHOULD_FOR_BEING_BAD = 1.499999;

    private static final String GOLD_STD_COL_NAME = "gold";
    private static final String WORD_COUNT_COL_NAME = "words";
    private static final String COL_NAME_BLACKLIST[] = new String[] { "rating" };

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        InputStream is = SimpleThreshouldFinder.class.getClassLoader().getResourceAsStream("important_coherences.csv");
        if (is == null) {
            LOGGER.error("Couldn't load important_coherences.csv from resources. Aborting.");
            return;
        }
        InputStreamReader isReader = new InputStreamReader(is);
        CSVReader reader = new CSVReader(isReader);

        IntObjectOpenHashMap<IntObjectOpenHashMap<DoubleArrayList>> data = new IntObjectOpenHashMap<IntObjectOpenHashMap<DoubleArrayList>>();
        IntObjectOpenHashMap<DoubleArrayList> goldStd = new IntObjectOpenHashMap<DoubleArrayList>();
        IntObjectOpenHashMap<DoubleArrayList> wordCountValuesMapping;
        DoubleArrayList values;
        int wordCount;
        String columnNames[];
        try {
            columnNames = reader.readNext();
            int goldStdId = -1, wordCountId = -1;
            BitSet measureColumn = new BitSet(columnNames.length);
            Set<String> colNameBlacklist = new HashSet<String>(Arrays.asList(COL_NAME_BLACKLIST));
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i].equals(GOLD_STD_COL_NAME)) {
                    goldStdId = i;
                } else if (columnNames[i].equals(WORD_COUNT_COL_NAME)) {
                    wordCountId = i;
                } else if (!colNameBlacklist.contains(columnNames[i])) {
                    measureColumn.set(i);
                    data.put(i, new IntObjectOpenHashMap<DoubleArrayList>());
                }
            }
            if ((goldStdId == -1) || (wordCountId == -1)) {
                LOGGER.error("Couldn't find god std column or word counts column. Aborting.");
                return;
            }

            String line[] = reader.readNext();
            double value;
            while (line != null) {
                wordCount = Integer.parseInt(line[wordCountId]);
                value = Double.parseDouble(line[goldStdId]);
                if (goldStd.containsKey(wordCount)) {
                    values = goldStd.lget();
                } else {
                    values = new DoubleArrayList();
                    goldStd.put(wordCount, values);
                }
                values.add(value);

                for (int i = 0; i < data.allocated.length; i++) {
                    if (data.allocated[i]) {
                        value = Double.parseDouble(line[data.keys[i]]);
                        wordCountValuesMapping = (IntObjectOpenHashMap<DoubleArrayList>) ((Object[]) data.values)[i];
                        if (wordCountValuesMapping.containsKey(wordCount)) {
                            values = wordCountValuesMapping.lget();
                        } else {
                            values = new DoubleArrayList();
                            wordCountValuesMapping.put(wordCount, values);
                        }
                        values.add(value);
                    }
                }

                line = reader.readNext();
            }
        } catch (Exception e) {
            LOGGER.error("Exception while reading input data. Aborting.", e);
            return;
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(isReader);
            IOUtils.closeQuietly(is);
        }

        double goldValues[];
        PrintStream debugStream = null;
        double threshould;
        SimpleThreshouldFinder finder = new SimpleThreshouldFinder();
        for (int i = 0; i < goldStd.allocated.length; i++) {
            if (goldStd.allocated[i]) {
                wordCount = goldStd.keys[i];
                goldValues = ((DoubleArrayList) ((Object[]) goldStd.values)[i]).toArray();
                for (int j = 0; j < data.allocated.length; j++) {
                    if (data.allocated[j]) {
                        wordCountValuesMapping = (IntObjectOpenHashMap<DoubleArrayList>) ((Object[]) data.values)[j];
                        if (wordCountValuesMapping.containsKey(wordCount)) {
                            System.out.print(columnNames[data.keys[j]] + "\t" + wordCount);
                            try {
                                debugStream = new PrintStream(columnNames[data.keys[j]] + "_" + wordCount
                                        + "words_good.csv");
                                threshould = finder.findThreshould(goldValues, wordCountValuesMapping.get(wordCount)
                                        .toArray(), HUMAN_RATING_THRESHOULD_FOR_BEING_GOOD, true, debugStream);
                            } catch (Exception e) {
                                LOGGER.error("Exception while searching threshould.", e);
                            } finally {
                                IOUtils.closeQuietly(debugStream);
                            }
                            try {
                                debugStream = new PrintStream(columnNames[data.keys[j]] + "_" + wordCount
                                        + "words_bad.csv");
                                threshould = finder.findThreshould(goldValues, wordCountValuesMapping.get(wordCount)
                                        .toArray(), HUMAN_RATING_THRESHOULD_FOR_BEING_BAD, false, debugStream);
                            } catch (Exception e) {
                                LOGGER.error("Exception while searching threshould.", e);
                            } finally {
                                IOUtils.closeQuietly(debugStream);
                            }
                            System.out.println();
                        }
                    }
                }
            }
        }
    }

    public double findThreshould(double goldStd[], double data[], double humanRatingThreshould,
            boolean posIsLargerOrEqualToThresh, PrintStream debugStream) {
        BitSet isTruePositive = new BitSet(goldStd.length);
        if (posIsLargerOrEqualToThresh) {
            for (int i = 0; i < goldStd.length; i++) {
                if (goldStd[i] >= humanRatingThreshould) {
                    isTruePositive.set(i);
                }
            }
        } else {
            for (int i = 0; i < goldStd.length; i++) {
                if (goldStd[i] <= humanRatingThreshould) {
                    isTruePositive.set(i);
                }
            }
        }
        return findThreshould(data, isTruePositive, debugStream);
    }

    public double findThreshould(double data[], BitSet isTruePositive, PrintStream debugStream) {
        // go through the data and create a mapping double --> number of
        // positive, negatives which are not classified as
        // good anymore
        DoubleObjectOpenHashMap<int[]> mapping = createMapping(data, isTruePositive);
        // create a sorted array of values
        double values[] = mapping.keys().toArray();
        Arrays.sort(values);
        // count the true positives (= all that are assigned to the ) and false
        // positives
        int positivesInGoldStd = (int) isTruePositive.cardinality();
        int truePositives = positivesInGoldStd;
        int falsePositives = data.length - positivesInGoldStd;
        // go through the mapping according to the sorted array (make sure that
        // the key is the threshould, but a
        // threshould is used for value >= threshould, thus, we have to deal
        // with threshoulds pointing to values that
        // should be used of the threshould is larger than this number) and
        // decrease the number of true positives and
        // false positives according to the numbers inside
        // the mapping.
        double result[] = calculateF1(truePositives, falsePositives, positivesInGoldStd);
        int counts[];
        double bestResult[] = Arrays.copyOf(result, result.length);
        int bestThreshouldId = 0;
        if (debugStream != null) {
            debugStream.println("threshould\ttruePositives\tfalsePositives\tprecision\trecall\tf1");
            debugStream.print(values[0]);
            debugStream.print('\t');
            debugStream.print(truePositives);
            debugStream.print('\t');
            debugStream.print(falsePositives);
            debugStream.print('\t');
            debugStream.print(result[PRECISION_INDEX]);
            debugStream.print('\t');
            debugStream.print(result[RECALL_INDEX]);
            debugStream.print('\t');
            debugStream.println(result[F1_INDEX]);
        }
        for (int i = 0; i < (values.length - 1); i++) {
            counts = mapping.get(values[i]);
            truePositives -= counts[TRUE_POSITIVE_INDEX];
            falsePositives -= counts[FALSE_POSITIVE_INDEX];
            // For every step update precision, recall and F1
            result = calculateF1(truePositives, falsePositives, positivesInGoldStd);
            if (bestResult[F1_INDEX] < result[F1_INDEX]) {
                System.arraycopy(result, 0, bestResult, 0, result.length);
                bestThreshouldId = i + 1;
            }
            if (debugStream != null) {
                debugStream.print(values[i + 1]);
                debugStream.print('\t');
                debugStream.print(truePositives);
                debugStream.print('\t');
                debugStream.print(falsePositives);
                debugStream.print('\t');
                debugStream.print(result[PRECISION_INDEX]);
                debugStream.print('\t');
                debugStream.print(result[RECALL_INDEX]);
                debugStream.print('\t');
                debugStream.println(result[F1_INDEX]);
            }
        }
        System.out.print("\t" + values[bestThreshouldId] + "\t" + bestResult[PRECISION_INDEX] + "\t" + bestResult[RECALL_INDEX] + "\t"
                + bestResult[F1_INDEX]);
        return values[bestThreshouldId];
    }

    private DoubleObjectOpenHashMap<int[]> createMapping(double[] data, BitSet isTruePositive) {
        DoubleObjectOpenHashMap<int[]> mapping = new DoubleObjectOpenHashMap<int[]>();
        int counts[];
        for (int i = 0; i < data.length; i++) {
            if (mapping.containsKey(data[i])) {
                counts = mapping.lget();
            } else {
                counts = new int[] { 0, 0 };
                mapping.put(data[i], counts);
            }
            ++counts[(isTruePositive.get(i) ? TRUE_POSITIVE_INDEX : FALSE_POSITIVE_INDEX)];
        }
        return mapping;
    }

    private double[] calculateF1(int truePos, int falsePos, int posInGoldStd) {
        double result[] = new double[3];
        // precision
        if ((truePos + falsePos) > 0) {
            result[PRECISION_INDEX] = ((double) truePos) / (double) (truePos + falsePos);
        } else {
            result[PRECISION_INDEX] = 1;
        }
        // recall
        result[RECALL_INDEX] = ((double) truePos) / ((double) (posInGoldStd));
        // F1
        if ((result[PRECISION_INDEX] == 0) || (result[RECALL_INDEX] == 0)) {
            result[F1_INDEX] = 0;
        } else {
            result[F1_INDEX] = (2 * result[PRECISION_INDEX] * result[RECALL_INDEX])
                    / (result[PRECISION_INDEX] + result[RECALL_INDEX]);
        }
        return result;
    }
}
