package com.about.mantle.exception;

public class JmxMonitoringException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JmxMonitoringException(String message, Throwable cause) {
        super(message, cause);
    }
}
