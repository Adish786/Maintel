package com.about.mantle.spring.failureanalyzer;

import com.about.hippodrome.config.common.PropertyDecryptionException;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class PropertyDecryptionExceptionFailureAnalyzer extends MantleAbstractFailureAnalyzer<PropertyDecryptionException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, PropertyDecryptionException cause) {
        String action = "If running locally, this error often occurs when your oktaaws utility isn't running. " +
                        "Check that you're running the oktaaws utility using the correct configuration. Sometimes " +
                        "you may need to quit and re-run oktaaws locally if it's already been running for some time.\n\n" +
                        "Additionally, check if the property value that failed to decrypt in the above description " +
                        "is supposed to be for an encrypted property. Some config properties need to be encrypted and if " +
                        "you’re using a plaintext value in a property that’s supposed to be encrypted, startup " +
                        "will fail.";
        return new FailureAnalysis(
                getDescription(cause),
                action,
                cause);
    }
}
