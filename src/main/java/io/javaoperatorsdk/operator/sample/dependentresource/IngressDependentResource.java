package io.javaoperatorsdk.operator.sample.dependentresource;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.sample.Utils;
import io.javaoperatorsdk.operator.sample.customresource.WebPage;

import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;
import static io.javaoperatorsdk.operator.sample.Utils.serviceName;

public class IngressDependentResource extends CRUDKubernetesDependentResource<Ingress, WebPage> {
    public IngressDependentResource() {
        super(Ingress.class);
    }

    @Override
    protected Ingress desired(WebPage webPage, Context<WebPage> context) {
        var ingress = loadYaml(Ingress.class, Utils.class, "ingress.yaml");
        ingress.getMetadata().setName(webPage.getMetadata().getName());
        ingress.getMetadata().setNamespace(webPage.getMetadata().getNamespace());
        ingress.getSpec().getRules().get(0).getHttp().getPaths().get(0)
                .getBackend().getService().setName(serviceName(webPage));
        return ingress;
    }

}
