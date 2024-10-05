package io.javaoperatorsdk.operator.sample.dependentresource;

import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.sample.Utils;
import io.javaoperatorsdk.operator.sample.customresource.WebPage;

import java.util.Map;

import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;
import static io.javaoperatorsdk.operator.sample.Utils.configMapName;
import static io.javaoperatorsdk.operator.sample.Utils.deploymentName;
import static io.javaoperatorsdk.operator.sample.WebPageReconciler.MANAGED_LABEL;

public class DeploymentDependentResource extends CRUDKubernetesDependentResource<Deployment, WebPage> {
    public DeploymentDependentResource() {
        super(Deployment.class);
    }

    @Override
    protected Deployment desired(WebPage webPage, Context<WebPage> context) {
        var deploymentName = deploymentName(webPage);
        var deployment = loadYaml(Deployment.class, Utils.class, "deployment.yaml");
        deployment.getMetadata().setName(deploymentName);
        deployment.getMetadata().setNamespace(webPage.getMetadata().getNamespace());
        deployment.getMetadata().setLabels(Map.of(MANAGED_LABEL, "true"));
        deployment.getSpec().getSelector().getMatchLabels().put("app", deploymentName);
        deployment
                .getSpec()
                .getTemplate()
                .getMetadata()
                .getLabels()
                .put("app", deploymentName);
        deployment
                .getSpec()
                .getTemplate()
                .getSpec()
                .getVolumes()
                .get(0)
                .setConfigMap(new ConfigMapVolumeSourceBuilder().withName(configMapName(webPage)).build());
        return deployment;
    }
}
