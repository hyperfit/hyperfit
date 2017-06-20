package org.hyperfit.resource;

import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.utils.StringUtils;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Provides some basic method implementations.  Most of these build on top of the getLinks(String relationship) implementation
 * so it is suggested you make that fast if possible.  Override these if you can make more efficient implementations
 * but a simple cache layer for getLinks(String relationship) probably be rather sufficient
 */
public abstract class BaseHyperResource implements HyperResource {
    /**
     * Get the the link identified by the given relationship
     * If more than one link is present for this relationship a HyperResourceException will be thrown.
     *
     * @param relationship
     * @return
     */
    public HyperLink getLink(String relationship) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException("Link relationship is required");
        }

        HyperLink[] links = this.getLinks(relationship);
        if (links.length == 0) {
            throw new HyperResourceException("Could not find a link with relationship " + relationship);
        }

        if (links.length > 1) {
            throw new HyperResourceException("Found more than one link with relationship " + relationship);
        }

        return links[0];
    }


    public HyperLink getLink(String relationship, String name) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException("Link relationship is required");
        }

        HyperLink[] links = this.getLinks(relationship, name);
        if (links.length == 0) {
            throw new HyperResourceException("Could not find a link with relationship [" + relationship + "] and name [" +  name + "]");
        }

        if (links.length > 1) {
            throw new HyperResourceException("Found more than one link with relationship [" + relationship + "] and name [" + name + "]");
        }

        return links[0];
    }


    public HyperLink[] getLinks(String relationship, String name) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException("Link relationship is required");
        }

        HyperLink[] links = this.getLinks(relationship);

        if (links.length == 0){
            return new HyperLink[0];
        }

        List<HyperLink> namedLinks = new ArrayList<HyperLink>(links.length);
        for(HyperLink link : links){
            if(StringUtils.safeEquals(link.getName(), name)){
                namedLinks.add(link);
            }
        }

        HyperLink[] matchedLinks = new HyperLink[namedLinks.size()];
        return namedLinks.toArray(matchedLinks);

    }

    public <T> T getPathAs(Class<T> classToReturn, String... path) {
        return this.getPathAs(classToReturn, false, path);
    }

    public LinkedHashSet<String> getProfiles(){
        HyperLink[] profileLinks = this.getLinks("profile");
        LinkedHashSet<String> profiles = new LinkedHashSet<String>(profileLinks.length);
        for(HyperLink l : profileLinks){
            profiles.add(l.getHref());
        }

        return profiles;

    }

    public boolean hasLink(String relationship, String name) {
        return this.getLinks(relationship, name).length > 0;
    }

    public boolean hasLink(String relationship) {
        return this.getLinks(relationship).length > 0;
    }
}
