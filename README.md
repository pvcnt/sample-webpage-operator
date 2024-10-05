# WebPage Operator

*This operator is a simplified version of [the sample WebPage operator provided by Java Operator SDK](https://github.com/operator-framework/java-operator-sdk/tree/main/sample-operators/webpage).* 

This is a simple example of how a Custom Resource backed by an Operator can serve as an abstraction layer.
This operator will use a WebPage resource, which mainly contains a static webpage definition and creates an NGINX Deployment backed by a ConfigMap which holds the HTML.

This is an example input:
```yaml
apiVersion: "sample.javaoperatorsdk/v1"
kind: WebPage
metadata:
  name: mynginx-hello
spec:
  html: |
    <html>
      <head>
        <title>Webserver Operator</title>
      </head>
      <body>
        Hello World!
      </body>
    </html>
```

## Test locally

1. Build the operator: `mvn compile package`
2. Deploy the CRD: `kubectl apply -f target/classes/META-INF/fabric8/webpages.sample.javaoperatorsdk-v1.yml`
3. Deploy the operator: `kubectl apply -f k8s/operator.yaml`
3. Deploy the operator: `kubectl apply -f k8s/webpage-hello.yaml`
