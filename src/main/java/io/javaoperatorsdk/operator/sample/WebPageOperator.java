package io.javaoperatorsdk.operator.sample;

import com.sun.net.httpserver.HttpServer;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.sample.probes.LivenessHandler;
import io.javaoperatorsdk.operator.sample.probes.StartupHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebPageOperator {
    private static final Logger log = LoggerFactory.getLogger(WebPageOperator.class);

    public static void main(String[] args) throws IOException {
        log.info("WebPage Operator starting!");

        var operator = new Operator();
        operator.register(new WebPageReconciler());
        operator.start();

        var server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/startup", new StartupHandler(operator));
        server.createContext("/healthz", new LivenessHandler(operator));
        server.setExecutor(null);
        server.start();
    }
}