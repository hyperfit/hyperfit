package org.hyperfit.resource;


import java.util.LinkedHashSet;

/**
 * Define basic access points of HyperMedia Resource
 */
public interface HyperResource {

    HyperLink getLink(String relationship);

    HyperLink getLink(String relationship, String name);

    HyperLink[] getLinks(String relationship);

    HyperLink[] getLinks(String relationship, String name);

    <T> T getPathAs(Class<T> classToReturn, String... path);


    boolean canResolveLinkLocal(String relationship);

    //TODO: these actually should take an array of relationship URL template parameters
    //hal doesn't support embedded template links, but other formats may
    HyperResource resolveLinkLocal(String relationship);

    HyperResource[] resolveLinksLocal(String relationship);

    boolean hasLink(String relationship);

    boolean hasLink(String relationship, String name);

    boolean isMultiLink(String relationship);

    /**
     *
     * @return LinkedHasSet of all profiles the resource implements.  Order from response is maintained.
     */
    LinkedHashSet<String> getProfiles();

}
