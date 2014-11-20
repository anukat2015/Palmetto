package org.aksw.palmetto.classification;

import java.io.PrintStream;
import java.util.Arrays;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.DoubleObjectOpenHashMap;

public class SimpleThreshouldFinder {

    private static final int TRUE_POSITIVE_INDEX = 0;
    private static final int FALSE_POSITIVE_INDEX = 1;
    private static final int PRECISION_INDEX = 0;
    private static final int RECALL_INDEX = 0;
    private static final int F1_INDEX = 0;

    public double findThreshould(double goldStd[], double data[], double humanRatingThreshould, PrintStream debugStream) {
        BitSet isTruePositive = new BitSet(goldStd.length);
        for (int i = 0; i < goldStd.length; i++) {
            if (goldStd[i] >= humanRatingThreshould) {
                isTruePositive.set(i);
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
        double bestF1 = result[F1_INDEX];
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
            if (bestF1 < result[F1_INDEX]) {
                bestF1 = result[F1_INDEX];
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
        result[F1_INDEX] = (2 * result[PRECISION_INDEX] * result[RECALL_INDEX])
                / (result[PRECISION_INDEX] + result[RECALL_INDEX]);
        return result;
    }
}
