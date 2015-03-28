## 1.0.2-SNAPSHOT - 2015-02-09

## 1.0.1-SNAPSHOT - 2015-01-25
* Initial release version

* HyperLink type added withe following fields
  * href:String
  * rel:String
  * templated:boolean
  * type:String
  * deprecation:String
  * name:String
  * profile:String
  * title:String
  * hrefLang:String
* href, rel and templated are never null, the other fields are optional so they can be null.

* HyperResource::getLink, HyperResource::getLinks and  HyperResource::hasLink have been updated with new behaviours for retrieving HyperLink instances:
  * getLink(String relationship) updated such that it succeeds whenever a unique link can be determined.  It throws otherwise.
  * An overload getLink(String relationship, String name) was added to retrieve a link by both it's relationship and name.  It follows the same behaviour as getLink(String relationship) using the extended matching
  * getLinks(String relationship) updated such that it returns an array with all links that have the given relationship.  This may possibly be an empty array.
  * An overload getLinks(String relationship, String name) was added that returns an array with all links that have the given relationship and name.  This may possibly be an empty array.
* The following table provides some examples of the new behaviours compare to the old results 

HA+JSON _links | Content	Method Called	| New Outcome	| Old outcome
---------------|-----------------------|-------------|--------------------
getLink(relationship) |
```"x:rel" : { "href" : "url" }``` |  ```getLink("x:rel")``` |	returns HyperLink	 | returns HyperLink
```"x:rel" : [ { "href" : "url" } ]``` | 	```getLink("x:rel")``` | 	returns HyperLink	| throws
```"x:rel" : [ { "href" : "url" }, { "href" : "url" } ]``` | 	```getLink("x:rel")``` | 	throws |	throws
```"x:some-other-rel" : { "href" : "url" }``` | 	```getLink("x:rel")```  | throws |  throws
```"x:rel" : { "href" : "url", "name": "a name" }``` | 	```getLink("x:rel")``` |	returns HyperLink |	returns HyperLink
getLink(relationship, name) |
```"x:rel" : { "href" : "url", "name": "a name" }``` | ```getLink("x:rel", "a name")``` | 	returns HyperLink |	n/a
```"x:rel" : { "href" : "url", "name": "a different name" }``` |	```getLink("x:rel", "a name")``` |	throws |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" } ]``` | ```getLink("x:rel", "a name")``` | returns HyperLink |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" },  { "href" : "url", "name": "a different name" } ]``` |	```getLink("x:rel", "a name")``` |	returns HyperLink |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" }, { "href" : "url", "name": "a name" } ]``` | 	```getLink("x:rel", "a name")``` |	returns HyperLink	| n/a
```"x:rel" : [ { "href" : "url", "name": "a different name" }, { "href" : "url", "name": "a different name" } ]``` |  	```getLink("x:rel", "a name")``` |	throws |	n/a
```"x:some-other-rel" : { "href" : "url", "name" : "a name" }``` |	```getLink("x:rel", "a name")``` |	throws |	n/a
getLinks(relationship) |
```"x:rel" : { "href" : "url" }``` |	```getLinks("x:rel")``` |	returns HyperLink[1] |	throws
```"x:rel" : [ { "href" : "url" } ]``` |	```getLinks("x:rel")``` |	returns HyperLink[1] |	returns HyperLink[1]
```"x:rel" : [   { "href" : "url" },   { "href" : "url" } ]``` | ```getLinks("x:rel")``` |	returns HyperLink[2] |	returns HyperLink[2]
```"x:some-other-rel" : { "href" : "url" }```	| ```getLinks("x:rel")``` |	returns HyperLink[0] |	throws
```"x:rel" : { "href" : "url", "name": "a name" }``` | ```getLinks("x:rel")``` |	returns HyperLink[1] |	throws
getLinks(String relationship, String name) |
```"x:rel" : { "href" : "url", "name": "a name" }``` |	```getLinks("x:rel", "a name")``` |	returns HyperLink[1] |	n/a
```"x:rel" : { "href" : "url", "name": "a different name" }``` |	```getLinks("x:rel", "a name")``` |	returns HyperLink[0] |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" } ]``` |	```getLinks("x:rel", "a name")``` |	returns HyperLink[1] |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" },  { "href" : "url", "name": "a different name" } ]``` |	```getLinks("x:rel", "a name")``` |	returns HyperLink[1] |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" }, { "href" : "url", "name": "a name" } ]``` |	```getLinks("x:rel", "a name")``` |	returns HyperLink[2] |	n/a
```"x:rel" : [ { "href" : "url", "name": "a different name" }, { "href" : "url", "name": "a different name" } ]``` |	```getLinks("x:rel", "a name")``` |	returns HyperLink[0] |	n/a
```"x:some-other-rel" : { "href" : "url" }```	| ```getLinks("x:rel", "a name")``` |	returns | HyperLink[0] |	n/a
hasLink(String relationship, String name) (see notes about embedded affecting hasLink below) |
```"x:rel" : { "href" : "url", "name": "a name" }``` |	```hasLink("x:rel", "a name")```	| true |	n/a
```"x:rel" : { "href" : "url", "name": "a different name" }``` |	```hasLink("x:rel", "a name")```	| false |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" } ]``` |	```hasLink("x:rel", "a name")``` |	true |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" },  { "href" : "url", "name": "a different name" } ]``` |	```hasLink("x:rel", "a name")``` |	true |	n/a
```"x:rel" : [ { "href" : "url", "name": "a name" }, { "href" : "url", "name": "a name" } ]``` |	```hasLink("x:rel", "a name")```	| true |	n/a
```"x:rel" : [ { "href" : "url", "name": "a different name" }, { "href" : "url", "name": "a different name" } ]``` |	hasLink("x:rel", "a name")	| false |	n/a
```"x:some-other-rel" : { "href" : "url" }``` | ```hasLink("x:rel", "a name")``` |	false |	n/a
```"x:some-other-rel" : { "href" : "url" }``` with ```_embedded : { "x:rel" : {} }``` |	hasLink("x:rel", "a name")	| false (see notes below)	| n/a

* Note to developers: getLinks is very safe and calls to hasLink are not needed before calling it.  
* getLink is less safe and hasLink should be called prior to calling getLink if you are not sure the link is present (consult link relationship docs to know about conditional presence of links). Note that partial representation of resources means that hasLink should almost always be called prior to getLink
* getLink should only be used in cases where the link is known to be a single link by contract.

 

* Embedded resources are now retrieved via the Links annotation.  If the link is not in the embedded collection then the request to the remote server is made.
* The link annotation now takes just 1 parameter, the link relationship value.  It's also the value of the annotation so no need to specify the param.  IE @Link("bb:promotions") 
* Any method annotated with a Link that returns a boolean is treated as a has Link request.  When true the link is present (either as a link or as embedded) when false the link is not present (either as a link or as embedded)
* The Data annotation now uses value() to set the path, explicitly passing the param as path is not needed.
* any parameter that is provided as null is ignored.
* All resources interfaces now must extend the HyperResource base interface, this exposes the useful hasLink method to check for arbitrary link presence as well as some other lower level stuff to retrieve as needed.

 * HyperLink's have the following fields (which match the HAL spec's Link Object currently, but new fields outside HAL may be added):


* HyperLink getLink(relationship) method throws an exception if the relationship is a multi link relationship.
* HyperLink[] getLinks(relationship) added that returns all links of a multi link relationship.
* hasLink(relationship) returns true if one or more link objects exists for the given relationship.
 * If relationship is a multi link relationship with no links present (ala an empty array) this method returns false.
 * When following a relationship in the client (usually via the a Link annotated method) the client will throw an exception if it detects the relationship was a multi link relationship and that relationship was not embedded.
 * Multi-link relationship navigation may be supported in future
 * Embedded multi link relationships will be successfully resolved.
* HyperLink has a new follow(TypeRef<T>, HyperLink) method that can be used to follow arbitrary hyperlinks retrieved from the getLink or getLinks methods.  This technique is very useful for cases where the return type of a link relationship is not consistent between resources.  For example the primary and secondary targets of Promotions.  The TypeRef is a super type token as explained at http://gafter.blogspot.com/2006/12/super-type-tokens.html.  Example usage:
HyperLink shopByLink = root.getLink("bb:shopby");
Page<Shopby> shopbyPage = root.follow(new TypeRef<Page<Shopby>>(){}, shopByLink);
* the follow method has been moved to the HyperLink type, as it is the link you're following anyways.  It now works like:
HyperLink shopByLink = root.getLink("bb:shopby");
Page<Shopby> shopbyPage = shopByLink.follow(new TypeRef<Page<Shopby>>(){});
* Lombak dependency was switched to provided scope so that it need not be included by projects depending on this package (like if you were to import this with gradle)
* commons-lang3 dependency was removed
* If you do not want to suffer the overhead of JSON parsing  prefer to get the entire Response you can do that as well
//On interface
@Link("bb:reviews")
HyperResponse reviews();
 
//In calling code
HyperResponse reviewsResponse = product.reviews();
 
//Using HyperLink's follow
HyperResponse reviewsResponse = product.getLink("bb:reviews").follow(new TypeRef<HyperResponse>{});
* A new interface RequestInterceptor has been added that allows arbitrary modifications of outgoing Requests prior to sending them
Interceptors are registered during build phase of the hyper client.  See Interceptors in Code Examples below.
* New interfaces ResourceMethodInfoCache and MethodInfoCache added to allow caching method metadata, such as annotations, instead of retrieving it every time. 
A default implementation based on a ConcurrentHashMap has been included
Clients may override the default by implementing their own and settings it during build phase.  See Configuring the client.
* Resource interface methods that return HyperLink or HyperLink[] and are annotated with the @Link annotation are now supported.  These are functionally equivalent shortcuts for the HyperResource#getLink(String rel) and HyperResource#getLinks(String rel) methods and can be followed.  Given the following resource interface:
Class code
public class Foo {
    ...
    @Link("bb:single")
    HyperLink getSingleLink();
     
    @Link("bb:multi")
    HyperLink[] getMultiLinks();   
}

The CHA specifically guarantees that links present as single link relationship (no array in the JSON) are contractually obligated to remain as single link relationships unless backwards compatibility is broken during a major version change.  (Note: versioning with the CHA is still an outstanding issue).
Resource interface methods annotated with @Link that return a HyperResource, String, or HyperResponse use getLink to find the link relationship currently.
When working with HAL hasLink(String relationship, String name) will never return true based on the presence of a link embedded using the Hyper Text Cache Pattern.  This is because this pattern does not allow specifying the name field of the link.
In various places the CHA uses a convention of appending -[name] to the link relationship when embedding...but this is not a standard and the commerce hyper client does not work with this behaviour.  This may change in the future once the client allows convention extensions.
hasLink(String relationship) does take into consideration present of a link embedded using the Hyper Text Cache Pattern.
The HyperClient class has been removed and HyperClientBuilder has been replaced with RootResourceBuilder, whose #build takes a Class<T extends HyperResource> to return the right resource interface (equivalent to the old HyperClient#fetchRoot). See  Configuring the client.  You can still jump directly to a deeper resource as needed...but this is effectively considered the root resource of a different API.
TypeInfo class added, which caches method return type information by wrapping the previous Map<String, Type> typeParamsLookup, which is used by HyperResourceInvokeHandler.
Complex properties of Resource Interfaces are now implemented as simple POJOs deserialized from the response.  Previously complex properties were proxified and treated much like full HyperResource implementations.  Examples of complex properties in the Commerce Hyper API include: Flag, GiftInfo, Inventory, PriceInfo, Rating and Violator
The Rating complex property method getRating which returns the value of the rating has been renamed getValue
The LinkedHashSet<String> HyperResoruce::getProfiles method was added to return an ordered set of profiles the resource claims to implement per the RFC6909 spec
* "Hyper" prefix removed from most class names and variables.  Some examples:
HyperResponse is now Response
HyperRequest is now Request
HyperMediaTypeHandler is now MediaTypeHandler
There are many other examples
HyperClientException was moved to the exception namespace
The HyperClient class now has a setCookieHandler method that takes a cookie handler to be used when working with requests and responses
HyperResourcePart has been removed.
HyperResource::getPathAs added to return data casted to types.  This is the engine behind @Data annotations now.
HyperResourceType was no longer needed and removed
The ability to return a List<? extends HyperResource> for @Link annotated methods was removed as it was not being used and not considered core functionality.  A plugin architecture will be introduced to allows this to be added back in a future version.
HyperResource:resolveLinkLocal now returns a HyperResource
HyperResource:resolveLinksLocal added.  It is the equivalent of Hyperresource:resolveLinkLocal for multi link relationships.  It returns a HyperResource[]
HyperResource:isMultiLink(relationship) was updated to return true if the relationship is embedded AND is multilink
BaseHyperResource added which has implementations of hasLink(relationship), hasLink(relationship, name), getLink(relationship), getLink(relationship, name), & getLinks(relationship, name) based upon an extender implementing getLinks(relationship).
HalJsonResource now extends BaseHyperResource
HalJsonResource now lazy non-blocking cache's calls to getLinks(relationship) (which many other methods from BaseHyperResource now use) in a simple hashmap.  If you notice issues with this, please report them.
An optional "name" field was added to the @Link annotation. E.g.:
public interface Store extends CHAHyperResource {
...
    @Link(value=REL_IMAGE_FLAG, name="icon")
    HyperLink getFlagIconLink();
...
}
The sample above is equivalent to store.getLink(REL_IMAGE_FLAG, "icon"). Applies also for #getLinks and #hasLink
New Profiles annotation used to annotated Resource interfaces to identify what profiles the resource supports
Takes an array of profile URIs
A new concept of a resource registry has been added to the client which contains information about all the profiles that registered resources implement.
The RootResourceBuilder has a resourceRegistry method for supplying a custom resource registry
Resources must be registered with the registry, this is most easily accomplished with the RootResourceBulder's registerResource method
The HyperResource interface has a new method getLinks() that returns an array of all hyper links within the resource.
The HyperResource interface has a new method getFirstLink(String relationship, String...names) that gathers all the links with the given relationship and finds the first name for which a link is present.
Use the * string to match any name.  Very useful as a fallback to try to retrieve a link by name, but resort to using any link with the relationship.
Use null to match a link with no name
Use the empty string to match a link with the empty string
There is no way to match a link with name * with this method, instead use the hasLink(rel, "*") followed by a getLink(rel, "*") in the rare case that you are looking for a link with the * name.
The Content annotation was removed.  Register media type handlers to send the accept headers you require.  This was mostly broken anyways as we don't send request bodies currently.
The Link.Param & Link.Header annotations have been lifted into their own types out of the Link annotation class.
The Link annotation was split up into more explicit parameters:
The Method annotation controls the request method to use when following links.  The methodType field is no longer present on the Link annotation.
The default it Method.GET
All instances that were not using GET have been updated
The Link annotation now identifies links regardless of their names.  The name field has been removed from the Link parameter to indicate this.
The NamedLink annotation now identifies a link with a given name
Use the NamedLink.NULL constant to identify links with no name field
The FirstLink annotation now identifies links with a given relationship matching the given set of names
The names are iterated in order and the first name that matches a link is returned
Use the FirstLink.NULL constant to identify links with no name field
Use the FirstLink.MATCH_ANY_NAME constant to identify any link with the given relationship
This is useful in fallback scenarios where you may want links with a given name, but can accept any link with the given relationship in the worst case
In all cases using a name of "" no longer matches any link regardless of name.  Use the Link annotation for this instead.
The HyperResource interface has a new method hasPath(String... path) that returns true if the path results in data that exists
the org.hyperfit namespace was moved into it's own project.  It will eventually contain different release notes.  For now:
the org.hyperfit.http namespace was moved to the org.hyperfit.net namespace.  Hyperfit is built to be protocol agnostic with protocol implementation plugins (like OkHttp).
The type HyperClientException was renamed HyperfitException
The RootResourceBuilder::build() method now has two parameters, the class interface to return and the url for the root resource
the endpoint() method has been removed.
The commerce hyper client project now has a transitive dependency on the hyperfit project.

