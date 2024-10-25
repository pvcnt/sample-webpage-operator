# WebPage Operator

This is an example of a [Kubernetes operator](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/), implemented using the [Java Operator SDK](https://javaoperatorsdk.io/), that deploys a simple Web page using Nginx.
This operator is a simplified version of [the sample WebPage operator provided by Java Operator SDK](https://github.com/operator-framework/java-operator-sdk/tree/main/sample-operators/webpage).
It showcases how an operator can simplify the amount of configuration needed to deploy an application.

**This operator is only for educational purposes, it should not be used in production!**

## Build and test locally

Build the operator (requires Java 17):
```
mvn compile package
```

Deploy the CRD:
```
kubectl apply -f target/classes/META-INF/fabric8/webpages.sample.javaoperatorsdk-v1.yml
```

Deploy the operator:
```
kubectl apply -f k8s/operator.yaml
```

Deploy the web page:
```
kubectl apply -f k8s/webpage.yaml
```