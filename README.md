[![Build Status](http://bitwise-shields.dev/jenkins/s/http/jenkins.body.prod/Hyperfit%20-%20Publish%20to%20Artifactory.svg)](http://jenkins/job/Hyperfit%20-%20Publish%20to%20Artifactory/)
[![Tests](http://bitwise-shields.dev/jenkins/t/http/jenkins.body.prod/Hyperfit%20-%20Publish%20to%20Artifactory.svg)](http://jenkins/job/Hyperfit%20-%20Publish%20to%20Artifactory/)
[![Sonarqube Coverage](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod:9000/org.hyperfit:hyperfit-root/coverage.svg)](http://sonarqube.body.prod:9000/dashboard/index/org.hyperfit:hyperfit-root)


hyperfit
========

Hyperfit is a client library for RESTful Applications &amp; Hypermedia APIs inspired by [Retrofit](http://square.github.io/retrofit/)

If you design your client as a [RESTful Reactive client]() such that the basic design is ```ViewFactory(ResourceFactory(URL))```
then you can use hyperfit to completely fullfill the ```ResourceFactory(URL)``` portion of your client.

## releases
Releases are published to artifactory.  For maven projects you can add the dependency as
```
<dependency>
  <groupId>org.hyperfit</groupId>
  <artifactId>hyperfit</artifactId>
  <version>1.8.0-SNAPSHOT</version>
</dependency>
```
See latest [snaps](http://artifactory/simple/libs-snapshot-local/org/hyperfit/hyperfit/) & [releases](http://artifactory/simple/libs-release-local/org/hyperfit/hyperfit/)

## release history
See [history](history.md)



## Best Practices
* When defining resource interface methods some best practices are recommended:
 * Data Properties (which are generally annotated with @Data should be defined as bean properties, prefixed with get ala getPropertyName().  
 * Methods that follow links to other resources, even if they are expected to be embedded, should not be prefixed with get to distinguish them from data that is guaranteed to be locally available.  Prefixing with fetch or follow is   IE Prefer naming a method that follows a x:product link relationship as product() or fetchProduct() over getProduct().
 * multi word links like main-goal should have camelCase method names, EG mainGoal()
 * A method used for the checking for the presence of a link should be prefixed with has EG hasLinkname
