package com.about.mantle.spring.failureanalyzer;

import com.about.globe.core.exception.GlobeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class GlobeExceptionFailureAnalyzer extends MantleAbstractFailureAnalyzer<GlobeException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, GlobeException cause) {
        return new FailureAnalysis(
                getDescription(cause) + "\n\nStack Trace:\n\n" + ExceptionUtils.getStackTrace(cause),
                "Consult the error message and the stack trace to address the cause of the exception. " +
                        "Reach out to the Globe or Axis teams if you have questions.",
                cause);
    }
}
