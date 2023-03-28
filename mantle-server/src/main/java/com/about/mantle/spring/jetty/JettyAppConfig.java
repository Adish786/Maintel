package com.about.mantle.spring.jetty;

public class JettyAppConfig {

    private final int maxThreads;
    private final int minThreads;
    private final int idleTimeout;
    private final int requestHeaderSize;
    private final int outputBufferSize;
    private final long connectionIdleTimeout;

    public JettyAppConfig(int maxThreads, int minThreads, int idleTimeout, int requestHeaderSize, int outputBufferSize,
                          long connectionIdleTimeout) {
        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
        this.idleTimeout = idleTimeout;
        this.requestHeaderSize = requestHeaderSize;
        this.outputBufferSize = outputBufferSize;
        this.connectionIdleTimeout = connectionIdleTimeout;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public int getRequestHeaderSize() {
        return requestHeaderSize;
    }

    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    public long getConnectionIdleTimeout() {
        return connectionIdleTimeout;
    }
}
