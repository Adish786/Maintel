package com.about.mantle.spring.failureanalyzer;

import com.about.mantle.exception.MissingServiceException;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class MissingServiceExceptionFailureAnalyzer extends MantleAbstractFailureAnalyzer<MissingServiceException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, MissingServiceException cause) {

        String action = "Make sure that " + cause.getServiceName() + " is deployed to and awake in " + getEnvironment() +
                ". See " + DEVELOPER_SQUADRON_VIEW_ENV_URL + getEnvironment();
        return new FailureAnalysis(
                getDescription(cause),
                action,
                cause);
    }
}
