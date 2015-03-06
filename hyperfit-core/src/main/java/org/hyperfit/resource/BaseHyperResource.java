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
     * Get the the link identified by the given relationship
     * If more than one link is present for this relationship a HyperResourceException will be thrown.
     *
     * @param relationship
     * @return
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

        HyperLink[] links = this.getLinks(relationship);

        if (links.length == 0){
            return new HyperLink[0];
        }

        List<HyperLink> namedLinks = new ArrayList<HyperLink>(links.length);
        for(HyperLink link : links){
            if(StringUtils.equals(link.getName(), name)){
                namedLinks.add(link);
            }
        }

        HyperLink[] matchedLinks = new HyperLink[namedLinks.size()];
        return namedLinks.toArray(matchedLinks);

    }


    public boolean hasLink(String relationship, String name) {
        return this.getLinks(relationship, name).length > 0;
    }

    public boolean hasLink(String relationship) {
        return this.getLinks(relationship).length > 0;
    }
}