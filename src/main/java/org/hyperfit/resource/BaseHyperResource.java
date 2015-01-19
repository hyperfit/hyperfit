package org.hyperfit.resource;

import org.hyperfit.message.Messages;
import org.hyperfit.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides some basic method implementations.  Most of these build on top of the getLinks(String relationship) implementation
 * so it is suggested you make that fast if possible.  Override these if you can make more efficient implementations
 * but a simple cache layer for getLinks(String relationship) probably be rather sufficient
 */
public abstract class BaseHyperResource implements HyperResource {
    /**
     * Get the hyper link information for the given relationship
     *
     * @param relationship
     * @return {@link String} href
     */
    public HyperLink getLink(String relationship) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_LINK_WITHOUT_REL);
        }

        HyperLink[] links = this.getLinks(relationship);
        if (links.length == 0) {
            throw new HyperResourceException(Messages.MSG_ERROR_LINK_NOT_FOUND, relationship);
        }

        if (links.length > 1) {
            throw new HyperResourceException(Messages.MSG_ERROR_LINK_FOUND_MORE_THAN_ONE, relationship);
        }

        return links[0];
    }


    public HyperLink getLink(String relationship, String name) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_LINK_WITHOUT_REL);
        }

        if (StringUtils.isEmpty(name)) {
            return getLink(relationship);
        }

        HyperLink[] links = this.getLinks(relationship, name);
        if (links.length == 0) {
            throw new HyperResourceException(Messages.MSG_ERROR_LINK_WITH_NAME_NOT_FOUND, relationship, name);
        }

        if (links.length > 1) {
            throw new HyperResourceException(Messages.MSG_ERROR_LINK_WITH_NAME_FOUND_MORE_THAN_ONE, relationship, name);
        }

        return links[0];
    }


    public HyperLink[] getLinks(String relationship, String name) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_LINK_WITHOUT_REL);
        }

        if (StringUtils.isEmpty(name)) {
            return getLinks(relationship);
        }

        HyperLink[] links = this.getLinks(relationship);

        if (links.length == 0){
            return new HyperLink[0];
        }

        List<HyperLink> namedLinks = new ArrayList<HyperLink>(links.length);
        for(HyperLink link : links){
            if(link.getName().equals(name)){
                namedLinks.add(link);
            }
        }

        HyperLink[] matchedLinks = new HyperLink[namedLinks.size()];
        return namedLinks.toArray(matchedLinks);

    }

    public HyperLink getFirstMatchingLink(String relationship, String...names){
        HyperLink[] relLinks = this.getLinks(relationship);

        if(relLinks.length == 0){
            throw new HyperResourceException(Messages.MSG_ERROR_LINK_NOT_FOUND, relationship);
        }

        for(String name : names){
            if(StringUtils.equals(name, "*")){
                //If it's the wildcard, just return the first one
                return relLinks[0];
            }

            for(HyperLink link : relLinks){
                if(StringUtils.equals(name, link.getName())){
                    return link;
                }
            }
        }

        //If it was never found indicate that.
        throw new HyperResourceException(Messages.MSG_ERROR_LINK_WITH_NAME_NOT_FOUND, relationship, Arrays.toString(names));
    }

    public boolean hasLink(String relationship, String name) {
        if (StringUtils.isEmpty(name)) {
            return hasLink(relationship);
        }

        return this.getLinks(relationship, name).length > 0;
    }

    public boolean hasLink(String relationship) {
        return this.getLinks(relationship).length > 0;
    }
}