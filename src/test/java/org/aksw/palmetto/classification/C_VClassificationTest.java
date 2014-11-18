package org.aksw.palmetto.classification;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class C_VClassificationTest extends AbstractClassificationTest {

    private static final String COHERENCE_NAME = "C_V";

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { COHERENCE_NAME, 0.4263997811, 5, Boolean.TRUE },
                { COHERENCE_NAME, 0.4319214399, 5, Boolean.TRUE },
                { COHERENCE_NAME, 0.2306940972, 5, Boolean.FALSE },
                { COHERENCE_NAME, 0.198670227, 5, Boolean.TRUE },
                { COHERENCE_NAME, 0.5072050764, 5, Boolean.TRUE },
                { COHERENCE_NAME, 0.1961327092, 5, Boolean.FALSE },
                { COHERENCE_NAME, 0.0744240316, 5, Boolean.FALSE },
                { COHERENCE_NAME, 0.095061456, 5, Boolean.FALSE },
                { COHERENCE_NAME, -0.1068088511, 10, Boolean.FALSE },
                { COHERENCE_NAME, 0.1745578069, 10, Boolean.TRUE },
                { COHERENCE_NAME, -0.1754455383, 10, Boolean.FALSE },
                { COHERENCE_NAME, 0.161690101, 10, Boolean.FALSE },
                { COHERENCE_NAME, 0.1360748654, 10, Boolean.FALSE },
                { COHERENCE_NAME, 0.3124152609, 10, Boolean.TRUE } });
    }

    public C_VClassificationTest(String coherenceName, double coherence, int numberOfWords, boolean isGood) {
        super(coherenceName, coherence, numberOfWords, isGood);
    }
}
