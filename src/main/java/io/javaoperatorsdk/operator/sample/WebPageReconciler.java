package io.javaoperatorsdk.operator.sample;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.sample.customresource.WebPage;
import io.javaoperatorsdk.operator.sample.customresource.WebPageStatus;
import io.javaoperatorsdk.operator.sample.dependentresource.ConfigMapDependentResource;
import io.javaoperatorsdk.operator.sample.dependentresource.DeploymentDependentResource;
import io.javaoperatorsdk.operator.sample.dependentresource.IngressDependentResource;
import io.javaoperatorsdk.operator.sample.dependentresource.ServiceDependentResource;

import java.util.Map;

@ControllerConfiguration
public class WebPageReconciler implements Reconciler<WebPage>, ErrorStatusHandler<WebPage>, Cleaner<WebPage>, EventSourceInitializer<WebPage> {
    public static final String MANAGED_LABEL = "webpage-operator.io/managed";

    private final KubernetesDependentResource<ConfigMap, WebPage> configMapDR = new ConfigMapDependentResource();
    private final KubernetesDependentResource<Deployment, WebPage> deploymentDR = new DeploymentDependentResource();
    private final KubernetesDependentResource<Service, WebPage> serviceDR = new ServiceDependentResource();
    private final KubernetesDependentResource<Ingress, WebPage> ingressDR = new IngressDependentResource();

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<WebPage> context) {
        return EventSourceInitializer.nameEventSourcesFromDependentResource(context, configMapDR, deploymentDR, serviceDR, ingressDR);
    }

    @Override
    public ErrorStatusUpdateControl<WebPage> updateErrorStatus(WebPage resource, Context<WebPage> context, Exception e) {
        resource.getStatus().setErrorMessage("Error: " + e.getMessage());
        return ErrorStatusUpdateControl.updateStatus(resource);
    }

    @Override
    public UpdateControl<WebPage> reconcile(WebPage webPage, Context<WebPage> context)
            throws Exception {
        if (webPage.getSpec().getHtml().contains("error")) {
            // special case just to showcase error if doing a demo
            throw new Exception("Simulating error");
        }

        configMapDR.reconcile(webPage, context);
        deploymentDR.reconcile(webPage, context);
        serviceDR.reconcile(webPage, context);

        if (webPage.getSpec().getExposed()) {
            ingressDR.reconcile(webPage, context);
        }

        var configMapName = context.getSecondaryResource(ConfigMap.class).orElseThrow().getMetadata().getName();
        var status = new WebPageStatus();
        status.setHtmlConfigMap(configMapName);
        status.setAreWeGood(true);
        status.setErrorMessage(null);
        webPage.setStatus(status);
        return UpdateControl.patchStatus(webPage);
    }

    @Override
    public DeleteControl cleanup(WebPage webPage, Context<WebPage> context) {
        return DeleteControl.defaultDelete();
    }
}
