# Docker : Kubernetes and Helm EventFlow

This sample builds on the [main Kubernetes sample](../../../../../ef-kubernetes/ef-kubernetes-app/src/site/markdown/index.md) and adds  Helm packaging.

* [Prerequisites](#prerequisites)
* [Quick runthrough](#quick-runthrough)
* [Packaging with Helm](#packaging-with-helm)
* [Deployment](#deployment)

## Prerequisites

In addition to Docker and Kubernetes ( see [main Kubernetes sample](../../../../../ef-kubernetes/ef-kubernetes-app/src/site/markdown/index.md) ), 
Helm 3 is also required to be installed and configured - see https://helm.sh/docs/using_helm/ .

Note that the helm binary must be locatable via PATH - both from the command line and inside
studio.  This may require setting PATH in studio ( see the Environment tab of the run configurations ).


## Quick runthrough

On **docker-desktop** :

1. Install Docker, Kubernetes and Helm
  See [Prerequisites](#prerequisites)
2. Build this project to create Docker images and Helm chart
  See [Packaging with Helm](#packaging-with-helm)
3. Use *helm --set dockerRegistry= install ef-helm-app ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz* to start the Streaming Nodes in the Kubernetes cluster
4. Locate the cluster monitor assigned NodePort  with *kubectl describe service clustermonitor*
5. Access the cluster monitor GUI at http://localhost:NodePort

```
$ helm --set dockerRegistry= install ef-helm-app ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz
NAME: ef-helm-app
LAST DEPLOYED: Mon Nov 18 11:01:44 2019
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
Thank you for installing ef-helm-app - Docker: Helm EventFlow

How to deploy an EventFlow application in Docker with Kubernetes and Helm

Your release is named ef-helm-app.

To learn more about the release, try:

  $ helm status ef-helm-app
  $ helm get ef-helm-app

$ kubectl describe service clustermonitor
...
NodePort:                 lvweb  31243/TCP
...

$ open http://localhost:31243

```

## Packaging with Helm

The the project is built with *mvn install* a Helm chart is created :

```shell
$ mvn install
...
[INFO] --- helm-maven-plugin:4.12:package (create helm package) @ ef-helm-app ---
[INFO] Packaging chart /Users/guest/workspace/tibco-streaming-samples-guest/docker/ef-helm/ef-helm-app/src/main/helm/ef-helm-app...
[INFO] Setting chart version to 1.0.0
[INFO] Successfully packaged chart and saved it to: /Users/guest/workspace/tibco-streaming-samples-guest/docker/ef-helm/ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz
```

The Helm chart can be installed with the *helm install* command - this will start the application up in Kubernetes :

```shell
$ helm --set dockerRegistry= install ef-helm-app ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz
NAME: ef-helm-app
LAST DEPLOYED: Mon Nov 18 11:01:44 2019
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
Thank you for installing ef-helm-app - Docker: Helm EventFlow

How to deploy an EventFlow application in Docker with Kubernetes and Helm

Your release is named ef-helm-app.

To learn more about the release, try:

  $ helm status ef-helm-app
  $ helm get ef-helm-app
```

If the Docker image has been pushed to a remote repository, the *dockerRegistry*
value can be overridden :

```shell
$ helm --set dockerRegistry=registry.com/ install ef-helm-app ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz
```

Overriding dockerRegistry can be especially important if the image is fetched from a docker
registry running in Kubernetes itself ( such as **Kind** ).

Values can be set via the *--set* argument :

```shell
$ helm --set replicaCount=3 install ef-helm-app ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz
```

Substitution parameters can be passed into the start-node script in the same way :

```shell
$ helm --set substitutions="a=b\,c=d" install ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz
```

The *helm uninstall* command will stop the application and delete the chart :

```shell
$ helm uninstall ef-helm-app
release "ef-helm-app" uninstalled
```

## Deployment

The Helm chart can be deployed to a repository using the *mvn deploy* command.  Standard parameter *-Dmaven.deploy.skip=true* 
is useful to skip deploying the maven artifacts.

```shell
$ mvn -Dmaven.deploy.skip=true deploy
...
[INFO] --- helm-maven-plugin:4.12:upload (deploy helm package) @ ef-helm-app ---
[INFO] Uploading to http://server.example.com/artifactory/helm/

[INFO] Uploading /Users/guest/workspace/tibco-streaming-samples-guest/docker/ef-helm/ef-helm-app/target/helm/repo/ef-helm-app-1.0.0.tgz...
[INFO] 201 - {
  "repo" : "helm",
  "path" : "/ef-helm-app-1.0.0.tgz",
  "created" : "2019-10-31T11:23:17.332-04:00",
  "createdBy" : "deployment",
  "downloadUri" : "http://server.example.com/artifactory/helm/ef-helm-app-1.0.0.tgz",
  "mimeType" : "application/x-gzip",
  "size" : "2215",
  "checksums" : {
    "sha1" : "329d5dd4766219daa30b47df59bbc8e830e7b8a3",
    "md5" : "8deccfad3fbafd02667b9a497f1d2d71"
  },
  "originalChecksums" : {
  },
  "uri" : "http://server.example.com/artifactory/helm/ef-helm-app-1.0.0.tgz"
}
```

The repository details can be specified in the pom.xml, or more typically set in continuous integration builds in maven's settings.xml :

```xml
<settings>
    <servers>
        <server>
            <id>server.example.com:2001</id>
            <username>username</username>
            <password>password</password>
        </server>
        <server>
            <id>helm.registry</id>
            <username>username</username>
            <password>password</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>cloud</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <docker.push.registry>server.example.com:2001</docker.push.registry>
                <helm.registry>http://server.example.com/artifactory/helm/</helm.registry>
                <helm.registry.type>ARTIFACTORY</helm.registry.type>
            </properties>
        </profile>
    </profiles>
</settings>
```

Once deployed, the application can be installed on any Kubernetes node :

```shell
$ helm install ef-helm-app http://server.example.com/artifactory/helm/ef-helm-app-1.0.0.tgz
NAME: ef-helm-app
LAST DEPLOYED: Mon Nov 18 11:01:44 2019
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
Thank you for installing ef-helm-app - Docker: Helm EventFlow

How to deploy an EventFlow application in Docker with Kubernetes and Helm

Your release is named ef-helm-app.

To learn more about the release, try:

  $ helm status ef-helm-app
  $ helm get ef-helm-app

```

Note that in the above example the Helm variable dockerRegistry is set to the location of the
docker images.

