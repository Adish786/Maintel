package com.about.mantle.spring.failureanalyzer;

import com.about.globe.core.exception.GlobeMissingComponentRefsException;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class MissingComponentRefsExceptionFailureAnalyzer extends MantleAbstractFailureAnalyzer<GlobeMissingComponentRefsException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, GlobeMissingComponentRefsException cause) {
        return new FailureAnalysis(
                getDescription(cause),
                "Check the components listed in the above description and verify that you haven't " +
                        "tried to ref a component that doesn't exist.",
                cause);
    }
}
