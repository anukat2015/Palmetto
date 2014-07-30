/**
 * Copyright (C) 2014 Michael Röder (michael.roeder@unister.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.palmetto.calculations.direct;

import org.aksw.palmetto.data.SubsetProbabilities;

/**
 * This confirmation measure calculates Shogenjis coherence.
 * 
 * @author Michael Röder
 * 
 */
public class ShogenjisConfirmationMeasure extends AbstractUndefinedResultHandlingConfirmationMeasure {

    public ShogenjisConfirmationMeasure() {
        super();
    }

    public ShogenjisConfirmationMeasure(double resultIfCalcUndefined) {
        super(resultIfCalcUndefined);
    }

    @Override
    public double[] calculateConfirmationValues(SubsetProbabilities subsetProbabilities) {
        int numberOfPairs = 0;
        for (int i = 0; i < subsetProbabilities.segments.length; ++i) {
            numberOfPairs += subsetProbabilities.conditions[i].length;
        }
        double values[] = new double[numberOfPairs];

        double conditionProbability, intersectionProbability;
        int pos = 0;
        for (int i = 0; i < subsetProbabilities.segments.length; ++i) {
            // if (subsetProbabilities.probabilities[subsetProbabilities.segments[i]] > 0) {
            for (int j = 0; j < subsetProbabilities.conditions[i].length; ++j) {
                conditionProbability = subsetProbabilities.probabilities[subsetProbabilities.conditions[i][j]];
                intersectionProbability = subsetProbabilities.probabilities[subsetProbabilities.segments[i]
                        | subsetProbabilities.conditions[i][j]];
                // if (conditionProbability > 0) {
                values[pos] = Math.log(intersectionProbability + LogBasedCalculation.EPSILON) - numberOfPairs
                        * Math.log(conditionProbability + LogBasedCalculation.EPSILON);
                // } else {
                // values[pos] = resultIfCalcUndefined;
                // }
                ++pos;
            }
            // } else {
            // for (int j = 0; j < subsetProbabilities.conditions[i].length; ++j) {
            // values[pos] = resultIfCalcUndefined;
            // ++pos;
            // }
            // }
        }
        return values;
    }

    @Override
    public String getName() {
        return "m_ls";
    }
}