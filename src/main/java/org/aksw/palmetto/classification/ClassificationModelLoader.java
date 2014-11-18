package org.aksw.palmetto.classification;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Classifier;

public class ClassificationModelLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationModelLoader.class);
    
    private static final String MODELS_FOLDER_PATH = "models/"; 

    private Classifier cache[] = new Classifier[CoherenceType.values().length];

    public Classifier loadClassifier(CoherenceType type) {
        int id = type.ordinal();
        if (cache[id] == null) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(MODELS_FOLDER_PATH + type.getModelFileName());
            if (is != null) {
                try {
                    cache[id] = (Classifier) weka.core.SerializationHelper.read(is);
                } catch (Exception e) {
                    LOGGER.error("Couldn't load classifier for coherence type " + type + ". Returning null.", e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            } else {
                LOGGER.error(
                        "Couldn't find a resource containing the serialized classifier for coherence type {}. Returning null.",
                        type);
            }
        }
        return cache[id];
    }
}
