package io.javaoperatorsdk.operator.sample.dependentresource;

import com.google.common.hash.Hashing;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.sample.WebPageOperator;
import io.javaoperatorsdk.operator.sample.customresource.WebPage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;
import static io.javaoperatorsdk.operator.sample.WebPageReconciler.MANAGED_LABEL;
import static io.javaoperatorsdk.operator.sample.dependentresource.ConfigMapDependentResource.configMapName;

public class DeploymentDependentResource extends CRUDKubernetesDependentResource<Deployment, WebPage> {

    public static String deploymentName(WebPage webPage) {
        return webPage.getMetadata().getName();
    }

    public DeploymentDependentResource() {
        super(Deployment.class);
    }

    @Override
    protected Deployment desired(WebPage webPage, Context<WebPage> context) {
        // Inject a hash of HTML content as a pod label to force deployment restart whenever
        // the HTML changes.
        var htmlHash = Hashing.sha256()
                .newHasher()
                .putString(webPage.getSpec().getHtml(), StandardCharsets.UTF_8)
                .hash()
                .toString()
                .substring(0, 40);

        var deploymentName = deploymentName(webPage);
        var deployment = loadYaml(Deployment.class, WebPageOperator.class, "deployment.yaml");
        deployment.getMetadata().setName(deploymentName);
        deployment.getMetadata().setNamespace(webPage.getMetadata().getNamespace());
        deployment.getMetadata().setLabels(Map.of(MANAGED_LABEL, "true"));
        deployment.getSpec().getSelector().getMatchLabels().put("app", deploymentName);
        deployment
                .getSpec()
                .getTemplate()
                .getMetadata()
                .getLabels()
                .putAll(Map.of("app", deploymentName, "html-hash", htmlHash));
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
