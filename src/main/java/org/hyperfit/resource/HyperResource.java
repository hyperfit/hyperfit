package org.hyperfit.resource;


import java.util.LinkedHashSet;

/**
 * Represents a Hypermedia Resource.  Provides a set of functions for working with Hypermedia Resources
 * namely the retrieval of data and hypermedia controls like links and forms.
 *
 * It is expected that different hypermedia formats have an implementation of this interface to add support
 * for that format within hyperfit.  Implementors can make use of the BaseHyperResource abstract class to
 * simplify implementation of this interface.
 */
public interface HyperResource {

    /**
     * retrieves all links present within this resource.
     * This may return an empty array, but should never return null.
     * @return a collection of all links present within this resource
     */
    HyperLink[] getLinks();

    /**
     * retrieves all links with the given relationship name present within this resource
     * This may return an empty array, but should never return null.
     * @param relationship the link relationship name used to limit the results
     * @return a collection of all links present within this resource that have the given relationship
     */
    HyperLink[] getLinks(String relationship);

    /**
     *
     * retrieves all links with the given relationship name present within this resource
     * This may return an empty array, but should never return null.
     * @param relationship the link relationship name used to limit the results
     * @param name the link name used to limit the results
     * @return a collection of all links present within this resource that have the given relationship and name
     */
    HyperLink[] getLinks(String relationship, String name);


    HyperLink getLink(String relationship);

    HyperLink getLink(String relationship, String name);


    <T> T getPathAs(Class<T> classToReturn, String... path);

    boolean hasPath(String... path);

    boolean canResolveLinkLocal(String relationship);

    //TODO: these actually should take an array of relationship URL template parameters
    //hal doesn't support embedded template links, but other formats may
    HyperResource resolveLinkLocal(String relationship);

    HyperResource[] resolveLinksLocal(String relationship);


    boolean hasLink(String relationship);


    boolean hasLink(String relationship, String name);

    /**
     * Determines if a given relationship in the resource is defined as a multi-link relationship or not
     * @param relationship the link relationship name to check
     * @return true if the relationship contains multiple links, false otherwise
     */
    boolean isMultiLink(String relationship);

    /**
     * Returns the set of IANA profile link relationships (https://tools.ietf.org/html/rfc6906) within this resource.
     * The order returned reflects the order as they are defined in the resource.
     * @return LinkedHasSet of all profiles the resource implements.  Order from response is maintained.
     */
    LinkedHashSet<String> getProfiles();

}