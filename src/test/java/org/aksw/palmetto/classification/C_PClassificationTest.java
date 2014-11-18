package org.aksw.palmetto.classification;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class C_PClassificationTest extends AbstractClassificationTest {

    private static final String COHERENCE_NAME = "C_V";

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { COHERENCE_NAME, 0.6885897805, 5, Boolean.TRUE },
                { COHERENCE_NAME, 0.5002712215, 5, Boolean.FALSE },
                { COHERENCE_NAME, 0.7764745971, 5, Boolean.TRUE },
        });
    }

    public C_PClassificationTest(String coherenceName, double coherence, int numberOfWords, boolean isGood) {
        super(coherenceName, coherence, numberOfWords, isGood);
    }
}
