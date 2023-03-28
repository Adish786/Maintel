package com.about.mantle.monitoring.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.about.mantle.exception.JmxMonitoringException;
import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JolokiaServerConfig;

public class JolokiaMonitoringServer {
    public JolokiaServer server = null;

    private JolokiaMonitoringServer(Builder builder) {
        try {
            JolokiaServerConfig jsc = new JolokiaServerConfig(builder.jolokiaConfigs);
            server = new JolokiaServer(jsc , false);
            server.start();
        } catch (IOException e) {
            throw new JmxMonitoringException("Failure to start Jolokia", e);
        }
    }

    /**
     * Stops the JolokiaServer. Required to avoid errors when performing hot reload using Spring Boot Devtools.
     */
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new JmxMonitoringException("Failure to stop Jolokia", e);
        }
    }

    public static class Builder {

        private final Map<String, String> jolokiaConfigs = new HashMap<>();

        public Builder(){
            jolokiaConfigs.put("autoStart", "true");
            jolokiaConfigs.put("host", "0.0.0.0");
            jolokiaConfigs.put("port","8778");
        }

        public Builder withPort(int port) {
            jolokiaConfigs.put("port", String.valueOf(port));
            return this;
        }

        public Builder withHost(String host) {
            jolokiaConfigs.put("host", String.valueOf(host));
            return this;
        }

        public Builder withAutoStart(Boolean autoStart) {
            jolokiaConfigs.put("port", String.valueOf(autoStart));
            return this;
        }

        /**
         * Start Jolokia.
         * @throws JmxMonitoringException if Jolokia server can't start
         */
        public JolokiaMonitoringServer start(){
            return new JolokiaMonitoringServer(this);
        }
    }
}
