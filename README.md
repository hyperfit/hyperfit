[![Latest Release](http://bitwise-bot.dev:8080/badge/artifactory/latest-release/org.hyperfit/hyperfit/)](http://artifactory/webapp/#/artifacts/browse/tree/General/libs-release-local/org/hyperfit/hyperfit)
[![Build Status](http://bitwise-shields.dev/jenkins/s/http/jenkins.body.prod/Hyperfit%20-%20Publish%20to%20Artifactory.svg)](http://jenkins/job/Hyperfit%20-%20Publish%20to%20Artifactory/)
[![Sonarqube Lines of Code](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod/org.hyperfit:hyperfit:origin%252Fmaster/ncloc.svg?label=lines)](http://sonarqube.body.prod/drilldown/measures?id=org.hyperfit:hyperfit:origin/master&metric=ncloc)
[![Tests](http://bitwise-shields.dev/jenkins/t/http/jenkins.body.prod/Hyperfit%20-%20Publish%20to%20Artifactory.svg)](http://jenkins/job/Hyperfit%20-%20Publish%20to%20Artifactory/)
[![Sonarqube Coverage](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod/org.hyperfit:hyperfit:origin%252Fmaster/coverage.svg)](http://sonarqube.body.prod/drilldown/measures?id=org.hyperfit:hyperfit:origin/master&metric=coverage)
[![Sonarqube Issues](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod/org.hyperfit:hyperfit:origin%252Fmaster/violations.svg)](http://sonarqube.body.prod/component_issues?id=org.hyperfit:hyperfit:origin/master)
[![Sonarqube Debt Ratio](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod/org.hyperfit:hyperfit:origin%252Fmaster/tech_debt.svg?label=Debt Ratio)](http://sonarqube.body.prod/drilldown/measures?id=org.hyperfit:hyperfit:origin/master&metric=sqale_debt_ratio)
[![Sonarqube Debt Minutes](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod/org.hyperfit:hyperfit:origin%252Fmaster/sqale_index.svg?label=Debt Minutes)](http://sonarqube.body.prod/drilldown/measures?id=org.hyperfit:hyperfit:origin/master&metric=sqale_index)
[![Sonarqube Docs](http://bitwise-shields.dev/sonar/http/sonarqube.body.prod/org.hyperfit:hyperfit:origin%252Fmaster/public_documented_api_density.svg?label=Doc%)](http://sonarqube.body.prod/drilldown/measures?id=org.hyperfit:hyperfit:origin/master&metric=public_documented_api_density)


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
  <version>1.9.0</version>
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
