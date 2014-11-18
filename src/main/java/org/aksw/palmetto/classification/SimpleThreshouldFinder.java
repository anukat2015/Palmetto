package org.aksw.palmetto.classification;

import java.io.PrintStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.LongOpenHashSet;

public class SimpleThreshouldFinder {

    public double findThreshould(double data[], BitSet isTruePositive, PrintStream debugStream) {
        // go through the data and create a mapping double --> number of positive, negatives which are not classified as
        // good anymore
        // create a sorted array of values
        // count the true positives (= all that are assigned to the ) and false positives
        // go through the mapping according to the sorted array (make sure that the key is the threshould, but a
        // threshould is used for value >= threshould, thus, we have to deal with threshoulds pointing to values that
        // should be used of the threshould is larger than this number) and decrease the number of true positives and
        // false positives according to the numbers inside
        // the mapping.
        // For every step update precision, recall and F1
        return 0;
    }

    private double[] findBestThreshould(LongOpenHashSet goldStandard, double[][] similarities, double[] similaritySteps) {
        PrintStream resultOut = null;
        try {
            resultOut = new PrintStream("");
            resultOut.println("Threshould,Precision,Recall,F1-measure");

            Arrays.sort(similaritySteps);
            double result[], bestResult[] = new double[] { 0, 0, 0 };

            result = calculateF1(goldStandard, similarities, similaritySteps[0]);
            resultOut.print("0,");
            resultOut.print(result[0]);
            resultOut.print(',');
            resultOut.print(result[1]);
            resultOut.print(',');
            resultOut.println(result[2]);
            resultOut.print(similaritySteps[0]);
            resultOut.print(',');
            resultOut.print(result[0]);
            resultOut.print(',');
            resultOut.print(result[1]);
            resultOut.print(',');
            resultOut.println(result[2]);

            for (int i = 1; i < similaritySteps.length; ++i) {
                result = calculateF1(goldStandard, similarities, similaritySteps[i]);
                if (result[2] > bestResult[2]) {
                    bestResult = result;
                }
                resultOut.print(similaritySteps[i]);
                resultOut.print(',');
                resultOut.print(result[0]);
                resultOut.print(',');
                resultOut.print(result[1]);
                resultOut.print(',');
                resultOut.println(result[2]);
            }
            if (similaritySteps[similaritySteps.length - 1] < 1.0) {
                resultOut.println("1,0,0");
            }
            return bestResult;
        } catch (Exception e) {
            LOGGER.error("Error while printing results to file. Returning null.", e);
            return null;
        } finally {
            IOUtils.closeQuietly(resultOut);
        }
    }

    private double[] calculateF1(LongOpenHashSet goldStandard, double[][] similarities, double threshould) {
        int truePos = 0, falsePos = 0;
        long pair;
        for (int i = 0; i < similarities.length; ++i) {
            for (int j = 0; j < i; ++j) {
                if (similarities[i][j] >= threshould) {
                    pair = (((long) j) << 32) | (long) i;
                    if (goldStandard.contains(pair)) {
                        ++truePos;
                    } else {
                        ++falsePos;
                    }
                }
            }
        }
        double result[] = new double[3];
        // precision
        if ((truePos + falsePos) > 0) {
            result[0] = ((double) truePos) / (double) (truePos + falsePos);
        } else {
            result[0] = 1;
        }
        // recall
        result[1] = ((double) truePos) / ((double) goldStandard.size());
        // F1
        result[2] = (2 * result[0] * result[1]) / (result[0] + result[1]);
        return result;
    }
}
