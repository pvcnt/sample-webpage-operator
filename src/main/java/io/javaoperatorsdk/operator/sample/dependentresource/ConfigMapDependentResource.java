package io.javaoperatorsdk.operator.sample.dependentresource;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.sample.customresource.WebPage;

import java.util.Map;

import static io.javaoperatorsdk.operator.sample.WebPageReconciler.MANAGED_LABEL;

public class ConfigMapDependentResource extends CRUDKubernetesDependentResource<ConfigMap, WebPage> {

    public static String configMapName(WebPage webPage) {
        return webPage.getMetadata().getName() + "-html";
    }

    public ConfigMapDependentResource() {
        super(ConfigMap.class);
    }

    @Override
    protected ConfigMap desired(WebPage webPage, Context<WebPage> context) {
        return new ConfigMapBuilder()
                .withMetadata(
                        new ObjectMetaBuilder()
                                .withName(configMapName(webPage))
                                .withNamespace(webPage.getMetadata().getNamespace())
                                .withLabels(Map.of(MANAGED_LABEL, "true"))
                                .build())
                .withData(Map.of("index.html", webPage.getSpec().getHtml()))
                .build();
    }
}
