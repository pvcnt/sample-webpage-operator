package io.javaoperatorsdk.operator.sample.dependentresource;

import io.fabric8.kubernetes.api.model.Service;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.sample.WebPageOperator;
import io.javaoperatorsdk.operator.sample.customresource.WebPage;

import java.util.Map;

import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;
import static io.javaoperatorsdk.operator.sample.WebPageReconciler.MANAGED_LABEL;
import static io.javaoperatorsdk.operator.sample.dependentresource.DeploymentDependentResource.deploymentName;

public class ServiceDependentResource extends CRUDKubernetesDependentResource<Service, WebPage> {
    public static String serviceName(WebPage webPage) {
        return webPage.getMetadata().getName();
    }

    public ServiceDependentResource() {
        super(Service.class);
    }

    @Override
    protected Service desired(WebPage webPage, Context<WebPage> context) {
        var service = loadYaml(Service.class, WebPageOperator.class, "service.yaml");
        service.getMetadata().setName(serviceName(webPage));
        service.getMetadata().setNamespace(webPage.getMetadata().getNamespace());
        service.getMetadata().setLabels(Map.of(MANAGED_LABEL, "true"));
        service.getSpec().setSelector(Map.of("app", deploymentName(webPage)));
        return service;
    }
}
