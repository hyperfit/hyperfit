## 1.10.0 - TBD
* HyperfitProcessor has a new processRequest overloads that takes a super type token TypeRef argument.  This allows you to return Generics from an RESTful service entry point
```Page<Dog> result = processor.processRequest(new TypeRef<Page<Dog>>(){}, "url-to-entry-point-that-returns-generic-like-page-of-dog");```
* When working with missing generic arguments for embedded resources it assumed that all embedded resource are at least HyperResource interfaces, beyond that they also will implement any interfaces as defined by the resource interface selection strategy.
 * In other words supposed you have a Page<T> with a T[] items() method.  You can now have code like ```Page menu = (Page)resource.thatHasSomethingReturningHyperResource();  Object[] items = menu.items();  if(items[0] instanceof Resourcetype)``` to get out of the generic Object and back into specialized interfaces.
 * Very useful when you can't be sure what is being returned and send it through a view bindinging routing based on the resource you have

## 1.9.0 - 2015-12-16
* Fixed a bug where the Response object of okhttp2 client only included the last header with a given name
* Response::getHeaders now returns an unmodifiable collection instead of an Iterable.

## 1.8.0 - 2015-11-11
* Added the ability for a HyperfitProcessor to have multiple network clients, identified by the schemes they service, e.g. bbcomstore://account
 * reference http://confluence/display/commerce/Commerce+Mobile+Deep+Link+URL+Specification for detail about deep link
 * reference http://confluence/display/commerce/Commerce+Hyper+Client#CommerceHyperClient-Deeplinkresource&Routing for detail design for custom scheme routing
* Added some tests around support maps as params values for templated links.  Note: use a LinkedHashMap if parameter order is important.
* Moved to Damn Handy URI Templates 2.1.0
 * Better android support for var exploding
* ContentType now has a withQ method that creates a new Content type identicle to the previous with a different Q value.  Very useful when registering content type handlers with different q ratings.

## 1.7.0 - 2015-06-05
*  

## 1.6.3 - 2015-05-07
* Form::hasField(String fieldName) added to detect presence of fields

## 1.6.1 - 2015-05-05
* The jackson mapper in hyperfit-hal has been changed to ignore missing fields on deserialization
* Lots more support around form fields
 * Fields share some common properties: required, label, hasError, errorMessage, maxLength, name
 * Fields are read only immutable objects that describe the fields of a resource's form.  They are meant to help build UIs and create requests.
 * You do not modify fields or values, instead you generate a RequestBuilder from a form.  This request builder uses the fields of the form to perform semantics and validation.
 * This is exactly how params for links work, think of fields just as a much richer link parameter with richer semantics
 * Forms and Links are the two supported hypermedia controls in hyperfit
* the following fields types are supported.  Any unique aspects are summarized
 * TextField - very general
 * HiddenField - not intended to be shown in the UI, although it still can have a label
 * SubmitField - may not have a name
 * TelephoneNumberField - similar to text field, except also conveys meta info that the value should be validated as a telephone number
 * EmailField - similar to text field, except also conveys meta info that the value should be validated as an email address
 * CheckboxField - Has a CheckedStage property.  The value for the field is only submitted if the state is checked.
 * ChoiceField - Offers a set of options.  The value of the field is the value of the selected option.
* hyperfit-html5 library has implementations of all form types based upon specs at http://confluence/display/commerce/Forms+in+the+Commerce+Hyper+API
* Resource interface methods annotated with new NamedForm do interesting things now
 * If the return type is boolean, it returns true if the resource has a form with the name in the annotation, false otherwise
 * If the return type is assignable to Form, then if the given Form exists that form will be returned.  Otherwise it will throw an exception
 * Otherwise the normal hyperfit processing takes place.  All parameters annotated with @Param are set as values for the field that matches their name
 * Note: Checkbox is interesting in that you don't pass in a value but instead pass in a checked state which causes hyperfit to include the value of the checkbox field
* Form now has a toRequestBuilder method that creates a request from the form.  All fields of the form have their values set in the request params per the rules of the field
* New type FieldSet which eventually will contain a collection of fields, but for now lets you get a label for a field set
* Form has method getFieldSet(String name) that gets the field set for the given name or throws if not exactly one is found

## 1.5.0-SNAPSHOT - 2015-04-21
* HyperLink was move to resource.controls.link package as a hyperlink is a hypermedia control
  * other types moved there
* resource.controls.form package was added to contain interfaces and types for working with form controls
  * Form interface added for working with a form
  * Field interface for working with the fields of a form
  * TextField interface added for working with text fields of a form
  * The best thing you can do is replace across project any occurrence of ```import org.hyperfit.resource.HyperLink;``` with ```import org.hyperfit.resource.controls.link.HyperLink;```
* HyperResource interface has new getForm(String formName) and getForms() methods for retrieving forms to work with.
* New content type plugin hyperfit-Html5
* The getProfiles implementation on HalJsonResource was moved to the lower level BaseHyperResource class

## 1.4.0-SNAPSHOT - 2015-04-01
* The concept of the ResourceRegistry was replaced with the much more general ResourceInterfaceSelectionStrategy plugin architecture.
* A ResourceInterfaceSelectionStrategy plugin must implement the determineInterfaces method that takes the expected return interface and the HyperResource for which interfaces should be selected and returns an array of all the HyperResource interfaces that have been selected
 * The resulting array may not include the expected resource interface!
* The SimpleInterfaceSelectionStrategy is an implementation of the ResourceInterfaceSelectionStrategy that returns an array containing only the expected resource interface passed to determineInterfaces regardless if that interface is applicable or not.
* The ProfileBasedInterfaceSelectionStrategy is meant to replace the removed ResourceRegistry.
 * It's constructor takes a collection of HyperResource extending classes that it looks for Profile annotations on and build up a registry of profiles to interfaces.  When determineInterfaces is invoked it returns the expected return class passed in combined with all interfaces that the have claimed, via their @Profile annotation, to support the profiles of the underlying HyperResource.
* Developers can use their own interface selection strategy to choose the resources the dynamic proxy should implement.  They could be based on media type, URL requested, headers in the response, or even based on the presence or values of fields in the response.
* org.hyperfit.HyperRequestProcessor was renamed org.hyperfit.HyperfitProcessor to identify it as the core processing logic of the Hyperfit library.
* Hyperfit processor has been formalized to have 3 processing methods that make up the processing pipeline within Hyperfit
 * A developer can define how far in the pipeline the processing should proceed by specifying the the return class.  See explinations of the 3 process methods for more detail.
 * processRequest takes a RequestBuilder and processes it into the requested return type.  If the requested return type is a Request then all that happens during processing is the request interceptors registered with the HyperfitProcessor are applied and the Request is returned.  If the requested type is not a request then the request is executed and the response is sent to processResponse.
 * There are various overload of processRequest to make it easy to start processing with just a URL.  This functionally replaces the concept of a RootResourceBuilder.
 * processResponse takes a response and processes it into the requested return type.  If the requested return type is a Response than that is returned.  This is most useful when processRequest is called with Response as the return type and is how a Resource Interface method that has Response as it's return type is processed. 
 * If the return type is not a Response then an attempt to convert the response to a HyperResource using the the appropriate ContentTypeHandler registered in the ContentTypeRegistry 
 * Upon success (either directly or with the ErrorHandler's intervention) the processResource method is called
 * processResource takes a HyperResource and processes it into the request return type.  It wraps the given hyper resource with a dynamic proxy backed by a HyperResourceInvokeHandler instance. The proxy is defined to implement all the interfaces returned by the ResourceInterfaceSelectionStrategy configured with the HyperfitProcessor.
* The Response type now includes a getRequest method to get a handle to the Request that was processed into the Response
* The ErrorHandler's signature was changed to take a reference to the HyperfitProcessor in use when the error was encountered.

## 1.3.0-SNAPSHOT - 2015-03-18
* Extracted OkHttp 2.x Client library as a hyperfit plugin - artifact org.hyperfit:hyperfit-okhttp2-client
 * This now depends on OkHttp 2.1.0 and okhttp-urlconnection 2.1.0 if you are explicitly overriding the transitive dependencies consider upgrading because of the cache reasons listed at https://github.com/square/okhttp/blob/master/CHANGELOG.md#version-210-rc1
* Added OkHttp 1.x Client library - artifact org.hyperfit:hyperfit-okhttp1-client 
 * OKHttp 1.x should only be used for Java 1.6 compatibility.
* Extracted the HAL specifics into a plugin library hyperfit-hal
 * This removes the core dependency on jackson and allows for a gson (or some other JSON library) based HAL implementation if desired.
* Migrated org.hyperfit.RootResourceBuilder to org.hyperfit.HyperRequestProcessor.Builder
 * HyperRequestProcessor is now the home of the core processing functionality of Hyperfit, more to come on this
* RequestBuilder was changed to be an interface and move out of the Request class
* BoringRequestBuilder implements RequestBuilder and is useful when you just have a url you want to make a request with.
 * Calling setParam on a BoringRequest builder will always result in a runtime exception being thrown as the BoringRequestBuilder cannot handle parameters in templates
* RFC6570RequestBuilder implements RequestBuilder and works with URI templates as defined in the RFC6570 specification.
* Both RequestBuilder implementations have static methods for building the most common types of requests like get post put delete, etc.
* A new HyperLink type HalHyperLink has been added (in the hyperfit-hal plugin) that is returned by the HalJsonResource whenever getLink() is called.
 * HalHyperLink's toRequestBuilder() method uses the isTemplated() method to determine if a BoringRequestBuilder or RFC6570RequestBuilder is returned
 * This roughly means that a link with {template} like syntax will not treat those as template/macros if it is being used within a BoringRequestBuilder, which will happen whenever a links templated field explicitly is not set to true (in the case of HAL links)
* This also allows for other link templating specifications to be used for other formats.
* Instead of Request.builder() use new BoringRequestBuilder() or new RFC6570RequestBuilder()
* The Request type no longer has any concept of parameters, it functions as an immutable POJO representing a full formed request.


## 1.1.0-SNAPSHOT - 2015-03-04
* Hyperfit now allows you to specify the content of requests (like post bodies). Various changes here:
* The org.hyperfit.mediatype package was renamed org.hyperfit.content
* New class ContentType for negotiating and matching content types (EG: application/hal+json;charset=UTF-8 matching application/hal+json)
 * You can use the parse static method to construct from content type strings (like "application/hal+json;charset=UTF-8;q=.8")
* New class ContentRegistry for managing available ContentTypeHander's
 * This is the interface passed to the various ErrorHandler methods replacing the Map<String, MediaTypeHandler> "registry" of content type handler
 * The RootResourceBuilder's method changed to work with this more closely
* The MediaTypeHandler was renamed ContentTypeHandler
 * getDefaultHandledMediaType is now called getDefaultContentType() and it now returns a ContentType
 * parseHyperResponse was renamed parseResponse
 * New method prepareRequest that takes a request and content (a POJO) and encodes it according to the content type and sets the reqest contentType to the appropriate content type.
 * New methods canParseResponse() and canPrepareRequest() to indicate capabilities of the ContentTypeHandler
* HalJsonMediaTypeHandler was renamed HalJsonContentTypeHandler
 * It does NOT currently support the prepareRequest functionality
* New content type handler class org.hyperfit.content.form.FormURLEncodedContentTypeHandler that works with the "application/x-www-form-urlencoded" content type.
 * It does NOT currently support the parseResponse functionality
 * YOU WILL WANT TO REGISTER THIS IN YOUR BUILDER!!!
 * It performs form url encoded by reflecting on fields (private or not) and encoding them as key=value pairs in the response content
* New annotation @Content that takes a content type string indicating how the parameter should be serialized.  For example:
```
@Method(org.hyperfit.net.Method.POST)
@Link(REL_LOGIN)
CheckoutStep login(
    @Param("CSRF") String CSRF,
    @Content("application/x-www-form-urlencoded") LoginCredentials credentials
);
```
Makes a POST request to the URL identified by the REL_LOGIN link relationship filling in the URL CSRF parameter and serializing the LoginCredentials instance use the ContentTypeHandler implementation registered as compatible with "application/x-www-form-urlencoded".  The serialized content is used as the content of the request, in the case of HTTP this is the request body.

## 1.0.2-SNAPSHOT - 2015-02-09
* The HyperLink type has two new follow method signatures()
 * R follow(Class<R>) - useful when you aren't trying to return a generic type.  For example the Response.class to get a raw Response object from a follow.
 * R follow(Class<R> class, Type genericInfo) - If you already have a generic type reference you can use it with this method.  This is actually what the follow(TypeRef<R>) calls, just using the TypeRef as an intermediate step.
* The Request object has a getAcceptedContentType() method that is used when creating the underlying network request by prefixing to the list of accepted content types of the client request specific values.  These are used in the request to identify what the it claims to accept.  In the case of HTTP this translates to the Accept header.
 * The Request.Builder has methods to add to the request specific accepted content types.
* If a link has a type parameter, then when it is followed (by any method including follow() and or an annotated method on a resource interface) that type is set as a request specific accepted content type.  This type gets a higher priority than any accept content types registered with the underlying Hyperfit client.

## 1.0.1-SNAPSHOT - 2015-01-25
* Initial release version.  These notes are here to help understand the history of pre-release versions.

* All resources interfaces now must extend the HyperResource base interface, this exposes the useful hasLink method to check for arbitrary link presence as well as some other lower level stuff to retrieve as needed.

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
* HyperLink getLink(relationship) method throws an exception if the relationship is a multi link relationship.
* When working with HAL hasLink(String relationship, String name) will never return true based on the presence of a link embedded using the Hyper Text Cache Pattern.  This is because HAL's implemenation of embedded links does not allow specifying the name field of the link.
* getLinks() returns an array of all hyper links of the resource.
* getFirstLink(String relationship, String...names) gathers all the links with the given relationship and finds the first name for which a link is present.
 * Use the * string to match any name.  Very useful as a fallback to try to retrieve a link by name, but resort to using any link with the relationship.
 * Use null to match a link with no name
 * Use the empty string to match a link with the empty string
 * There is no way to match a link with name * with this method, instead use the hasLink(rel, "*") followed by a getLink(rel, "*") in the rare case that you are looking for a link with the * name.


* HyperLink has a follow(TypeRef<T>) method that follows the link and returns the resulting resource with the given interface.    * The TypeRef is a super type token as explained at http://gafter.blogspot.com/2006/12/super-type-tokens.html.  Example usage:
 * This works the same an @Link annotated method on a resource interface
* A code example using a super type token:
```
HyperLink shopByLink = root.getLink("bb:shopby");
Page<Shopby> shopbyPage = shopByLink.follow(new TypeRef<Page<Shopby>>(){});
```

* Resource interface methods that return HyperLink or HyperLink[] and are annotated with the @Link annotation are now supported.  These are functionally equivalent shortcuts for the HyperResource#getLink(String rel) and HyperResource#getLinks(String rel) methods.  An example resource interface:
```
public class Foo {
    ...
    @Link("bb:single")
    HyperLink getSingleLink();
     
    @Link("bb:multi")
    HyperLink[] getMultiLinks();   
}
```
* A @Link annotated resource interface method can be considered short hand for a call to getLink(relationship) with a subsequent call to follow underneath the covers.
* The @Link annotation now takes just 1 parameter, the link relationship value.  It's also the value of the annotation so no need to specify the param.  IE @Link("bb:promotions") 
* Any method annotated with a @Link(relationship) that returns a boolean is executed as a hasLink(relationship) invokcation.  When true the link is present (either as a link or as embedded) when false the link is not present (either as a link or as embedded)
* A @NamedLink annotated resource interface method can be considered short hand for a call to getLink(relationship, name) with a subsequent call to follow underneat the convers.
public interface Store extends CHAHyperResource {
...
    @NamedLink(value=REL_IMAGE_FLAG, name="icon")
    HyperLink getFlagIconLink();
...
}
* A @NamedLink annotated resource interface method can be considered short hand for a call to getFirstLink(relationship, names...)
 * Use the FirstLink.NULL constant to identify links with no name field
 * Use the FirstLink.MATCH_ANY_NAME constant to identify any link with the given relationship



* The @Data annotation now uses value() to set the path, explicitly passing the param as path is not needed.
* any parameter that is provided as null is ignored.
* Complex properties (properties that have sub-properites, but no hypermedia controls) of resources are now retrievable as simple POJOs deserialized from the response.  Previously complex properties were considered a Resource with no hypermedia controls and required all the overhead of resources interfaces.
* HyperResource::getPathAs added to return data casted to types.  This is the engine behind @Data annotations now.
* HyperResource::hasPath(String... path) returns true if the path used results in a located piece of data


* Embedded resource links, like HAL's _embedded or Siren's entities, are now supported when using a HyperLink's follow method along with @Link annotated methods.
 * A new method on the HyperResource canResolveLocal(String relationhip) powers this logic
 * When requesting the following of a link, either by a HyperLink's follow method or a @Link annotated method on a resource interface, a check to see if the link is resolvable locally using the canResolveLocal is made.  If the link cannot be resolved locally, than a request to the remote server is made.
 * HyperResource:resolveLinkLocal now returns a HyperResource
 * HyperResource:resolveLinksLocal added.  It is the equivalent of Hyperresource:resolveLinkLocal for multi link relationships.  It returns a HyperResource[]
 * HyperResource:isMultiLink(relationship) was updated to return true if the relationship is embedded AND is multilink


* When calling an @Link annotated resource interface method an exception is thrown if it detects the relationship was a multi link relationship and that relationship was not embedded.
 * This is because making a request for every matching link object sequentially could require a lot of I/O wait time and doing it in parallel is overly complicated in Java 6
 * Multi-link relationship navigation may be supported in future
 * Embedded multi link relationships will be successfully resolved.
 *

* Dependency Cleanup to make Hyperfit a smaller footprint in your project
** Lombak dependency was switched to provided scope so that it need not be included by projects depending on this package (like if you were to import this with gradle)
** commons-lang3 dependency was removed

* If you do not want to suffer the overhead of JSON parsing and prefer to work with the raw Reponse you can now do that by specifying Response as the expected return type either as the return of a resource interface method or as the type in a call to HyperLink's follow().  For example
```
//On interface
@Link("bb:reviews")
Response reviews();
 
//In calling code
Response reviewsResponse = product.reviews();
 
//Using HyperLink's follow
Response reviewsResponse = product.getLink("bb:reviews").follow(new TypeRef<Response>{});
```

* A new interface RequestInterceptor has been added that allows arbitrary modifications of outgoing Requests prior to sending them
 * Interceptors are registered during build phase.
* New interfaces ResourceMethodInfoCache and MethodInfoCache added to allow caching method metadata, such as annotations, instead of retrieving it every time. 
 * A default implementation based on a ConcurrentHashMap has been included
 * Developers may override the default by implementing their own and settings it during build phase.  See Configuring the client.
* TypeInfo class added, which caches method return type information by wrapping all the generic information from the previous context.
* HyperClientException was moved to the exception namespace and renamed HyperfitException

* The "Hyper" prefix is no longer present in most class names and variables.  Some examples:
 * The HyperClient class has been removed and HyperClientBuilder has been replaced with RootResourceBuilder, whose #build takes a Class<T extends HyperResource> to return the right resource interface (equivalent to the old HyperClient#fetchRoot) and a String URL to retrieve the resource. See  Configuring the client.  You can still jump directly to a deeper resource as needed...but this is effectively considered the root resource of a different API.
 * HyperResponse is now Response
 * HyperRequest is now Request
 * HyperMediaTypeHandler is now MediaTypeHandler
 * HyperResourcePart has been removed.
 * HyperResourceType was no longer needed and removed

* The HyperClient class now has a setCookieHandler method that takes a cookie handler to be used when working with requests and responses
* The @Link.@Param & @Link.@Header annotations have been lifted into their own types out of the Link annotation class.
* The @Link annotation was split up into more explicit parameters:
* The @Method annotation controls the request method to use when following links.  The methodType field is no longer present on the Link annotation.
 * The default it Method.GET
 

* The ability to return a List<? extends HyperResource> for @Link annotated methods was removed as it was not being used and not considered core functionality.  A plugin architecture will be introduced to allows this to be added back in a future version.

* BaseHyperResource added which has implementations of hasLink(relationship), hasLink(relationship, name), getLink(relationship), getLink(relationship, name), & getLinks(relationship, name) based upon an extender implementing getLinks(relationship).
* HalJsonResource now extends BaseHyperResource
* HalJsonResource now lazy non-blocking cache's calls to getLinks(relationship) (which many other methods from BaseHyperResource now use) in a simple hashmap.  If you notice issues with this, please report them.

* The LinkedHashSet<String> HyperResoruce::getProfiles method was added to return an ordered set of profiles the resource claims to implement per the RFC6909 spec
* A new concept of a resource registry has been added
 * New Profiles annotation used to annotated Resource interfaces to identify what profiles the resource supports
 * The registry contains all information about which resource interfaces map to which RFC6909 profiles
 * The RootResourceBuilder has a resourceRegistry method for supplying a custom resource registry

* The Content annotation was removed.  Register media type handlers to send the accept headers you require.  This was mostly broken anyways as we don't send request bodies currently.

