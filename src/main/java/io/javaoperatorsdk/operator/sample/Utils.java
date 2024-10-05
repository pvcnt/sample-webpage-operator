package io.javaoperatorsdk.operator.sample;

import io.javaoperatorsdk.operator.sample.customresource.WebPage;

public class Utils {

    public static String configMapName(WebPage webPage) {
        return webPage.getMetadata().getName() + "-html";
    }

    public static String deploymentName(WebPage webPage) {
        return webPage.getMetadata().getName();
    }

    public static String serviceName(WebPage webPage) {
        return webPage.getMetadata().getName();
    }

    private Utils() {
        // Do not instantiate.
    }
}
