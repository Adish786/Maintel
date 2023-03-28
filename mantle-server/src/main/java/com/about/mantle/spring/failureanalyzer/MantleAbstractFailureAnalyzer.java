package com.about.mantle.spring.failureanalyzer;

import com.about.hippodrome.config.CommonProperty;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;

import java.util.Objects;

public abstract class MantleAbstractFailureAnalyzer<T extends Throwable> extends AbstractFailureAnalyzer<T> {

    protected static final String DEVELOPER_SQUADRON_VIEW_ENV_URL = "https://squadron.prod.aws.dotdash.com/ui/deploy/environments/view/";
    private final String environment;

    public MantleAbstractFailureAnalyzer() {
        environment = Objects.requireNonNull(System.getProperty(CommonProperty.ENVIRONMENT.getPropertyName()),
                "The value of the \"environment\" environment variable could not be determined");
    }

    protected String getDescription(Throwable cause) {
        return "An exception of the following type occurred: " + cause.getClass().getName() + "\n\n" + cause.getLocalizedMessage();
    }

    protected String getEnvironment() {
        return environment;
    }
}
