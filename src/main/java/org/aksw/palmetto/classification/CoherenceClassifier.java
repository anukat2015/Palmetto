package org.aksw.palmetto.classification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class CoherenceClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoherenceClassifier.class);

    private static final double MAX_CLASS_FOR_BEING_GOOD = 0.5;

    private ClassificationModelLoader loader = new ClassificationModelLoader();

    public boolean isCoherenceGood(String coherenceName, double coherence, int numberOfWords) {
        CoherenceType type = CoherenceType.valueOf(coherenceName.toUpperCase());
        if (type == null) {
            LOGGER.error("Got unknown coherence type \"{}\". Returning false.", coherenceName);
            return false;
        }
        Classifier classifier = loader.loadClassifier(type);
        if (classifier == null) {
            return false;
        }
        Instance instance = createInstance(type, coherence, numberOfWords);
        double classification;
        boolean result;
        try {
            classification = classifier.classifyInstance(instance);
            result = classification <= MAX_CLASS_FOR_BEING_GOOD;
            LOGGER.debug("Classified coherenceType={}, coherenceValue={}, numberOfWords={} to value={}, isGood={}.",
                    type, coherence, numberOfWords, classification, result);
            return result;
        } catch (Exception e) {
            LOGGER.error("Got an exception while classifying coherenceType=" + type + ", coherenceValue=" + coherence
                    + ", numberOfWords=" + numberOfWords + ".", e);
            return false;
        }
    }

    private Instance createInstance(CoherenceType type, double coherence, int numberOfWords) {
        FastVector attInfo = new FastVector();
        attInfo.addElement(new Attribute(type.getCoherenceName()));
        attInfo.addElement(new Attribute("words"));
        Attribute classAttribute = new Attribute("rating");
        attInfo.addElement(classAttribute);
        Instances dataset = new Instances("", attInfo, 1);
        dataset.setClass(classAttribute);
        Instance instance = new Instance(1, new double[] { coherence, numberOfWords });
        instance.setDataset(dataset);
        return instance;
    }
}
