package org.aksw.palmetto.classification;

import org.junit.Test;

import junit.framework.Assert;

public abstract class AbstractClassificationTest {

    protected static CoherenceClassifier classifier = new CoherenceClassifier();

    private String coherenceName;
    private double coherence;
    private int numberOfWords;
    private boolean isGood;

    public AbstractClassificationTest(String coherenceName, double coherence, int numberOfWords, boolean isGood) {
        this.coherenceName = coherenceName;
        this.coherence = coherence;
        this.numberOfWords = numberOfWords;
        this.isGood = isGood;
    }

    @Test
    public void test() {
        Assert.assertEquals(isGood, classifier.isCoherenceGood(coherenceName, coherence, numberOfWords));
    }
}
