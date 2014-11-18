package org.aksw.palmetto.classification;

public enum CoherenceType {

    C_A("C_A"),
    C_P("C_P"),
    C_V("C_V"),
    NPMI("NPMI"),
    UCI("UCI"),
    UMASS("UMass");

    private static final String MODEL_FILE_ENDING = ".model";

    private String coherenceName;

    private CoherenceType(String coherenceName) {
        this.coherenceName = coherenceName;
    }

    public String getModelFileName() {
        return coherenceName + MODEL_FILE_ENDING;
    }

    public String getCoherenceName() {
        return coherenceName;
    }
}
