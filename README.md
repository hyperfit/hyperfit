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
See latest [snaps] & [releases]

## release history
See [history](history.md)


## Best Practices
* When defining resource interface methods some best practices are recommended:
 * Data Properties (which are generally annotated with @Data should be defined as bean properties, prefixed with get ala getPropertyName().  
 * Methods that follow links to other resources, even if they are expected to be embedded, should not be prefixed with get to distinguish them from data that is guaranteed to be locally available.  Prefixing with fetch or follow is   IE Prefer naming a method that follows a x:product link relationship as product() or fetchProduct() over getProduct().
 * multi word links like main-goal should have camelCase method names, EG mainGoal()
 * A method used for the checking for the presence of a link should be prefixed with has EG hasLinkname

## Known Issues

### Type Erasure When Comparing to Null on Android
On October 9th, 2014 an interesting issue was reported by the android developers.   What follows is information regarding the investigation of the issue.  As of now the cause of the issue is still unknown and therefore a solution has not been identified.

#### Symptom
A null pointer exception is thrown when attempting to retrieve an array of items from a Page<IngredientLabel> interface's items() method.  The code at the execution point looked something like:

```
if(page != null && page.items() != null){
  updateUIWithData(Array.asList(page.items()));
}
```

It was the items() != null check that threw the exception.

Important Notes:
* This only manifested itself on the android operating system
* Similar code executing in the integration tests of the Hyper Client 
* Similar code that only had the page as a Page<Skugroup> functioned without issue in the same method blocks
* The signature of updateUIWithData is updateUIWithData(List<IngredientLabel>)

#### Analysis
The null pointer exception was traced back to the routine in the proxy method handler that determines the type T of the current Page<T> resource.   There was no information about what the T parameter should be resolved to as the type lookup dictionary was empty.  Tracing the cause of the lookup dictionary being empty revealed that the method.getGenericReturnType() call when constructing the Page proxy object returned a java.lang.class instead of the expected implementation of ParameterizedType.  No type info can be looked up on a straight Class.  Specifically the Class was the just a Page, which looked like it has suffered from Type erasure.

We checked, and for methods returning a Page<Skugroup> the getGenericReturnType() does return an implementation of ParameterizedType which is why retrieving the items() of that page does not cause the issue.  Thus Type erasure did not occur on a page of skugroup.
It's been assumed that the JIT compiler must be optimizing away the type information for a Page<IngredientLabel>.   What causes the JIT to do this is unknown.  We noticed a few differences between Skugroup and IngredientLabel:
* Skugroup has a self() method that returns a Skugroup, while IngredientLabel does not. (note, after this issue, IngredientLabel had a self() added but we have not yet tested to verify this would change behaviour)
* Skugroup had methods that returned generic types, specifically skus() returns a Page<Sku> and getPropertyBag returns Map<String, Object>.  IngredientLabel does not have any methods returning generic types

#### Workaround
It was recommended that two calls to items() not be made as this will result in generating the array of proxified IngredientLabel instances twice, which is less than optimal.  The code was changed to:

```
if(page != null){
  IngredientLabel[] labels = page.items();
  if(labels != null){
    updateUIWithData(Array.asList(labels));
  }
}
```
And the issue no longer presented itself.

It is unclear why this change functions as a work around, but it is better to only call items() once.  A possible recommendation is to always assign the items() method to an typed array...however this doesn't full explain why similar code worked for Page<T> of other types of T.
