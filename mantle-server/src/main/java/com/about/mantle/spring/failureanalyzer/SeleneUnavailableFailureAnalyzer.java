package com.about.mantle.spring.failureanalyzer;

import com.about.mantle.exception.SeleneUnavailableException;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class SeleneUnavailableFailureAnalyzer extends MantleAbstractFailureAnalyzer<SeleneUnavailableException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, SeleneUnavailableException cause) {
        return new FailureAnalysis(
                // The SeleneUnavailableException's cause contains the root information we want to display
                getDescription(cause.getCause()),
                "This error likely occurred because Selene is either hibernating or still waking from hibernation in " + getEnvironment() +
                        ". See " + DEVELOPER_SQUADRON_VIEW_ENV_URL + getEnvironment(),
                cause);
    }
}
