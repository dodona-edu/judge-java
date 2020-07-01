package dodona.feedback;

import static dodona.junit.JUnitJSON.PROPERTY_OUTPUT_CUTOFF;

public class CloseTest extends AcceptedClose {

    // must be passed to the judge
    private static final int outputCutoff = Integer.getInteger(PROPERTY_OUTPUT_CUTOFF, 1000);

    private String generated;
    private StatusPair status;

    public CloseTest(String generated, StatusPair status, boolean accepted) {
        super("close-test", accepted);
        if(generated.length() > outputCutoff) {
            this.generated = generated.substring(0, outputCutoff) + "...";
        } else {
            this.generated = generated;
        }
        this.status = status;
    }

    public boolean wasTruncated() {
        return this.generated.length() > outputCutoff;
    }

}
