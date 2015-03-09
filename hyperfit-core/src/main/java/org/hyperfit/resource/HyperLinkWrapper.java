package org.hyperfit.resource;

import org.hyperfit.net.RequestBuilder;


/**
 * Used by the HyperResourceInvokeHandler to wrap an existing hyper link objects
 * functionality...except for the follow() methods...which is really
 * weird and seems not quite right...
 */
public abstract class HyperLinkWrapper extends HyperLink {


    private final HyperLink wrappedLink;

    public HyperLinkWrapper(HyperLink hyperlink) {
        //TODO Not a fan of having to call this..maybe we need a HyperLink interface?
        super(hyperlink);
        this.wrappedLink = hyperlink;
    }

    public String getHrefLang() {
        return wrappedLink.getHrefLang();
    }

    public String getHref() {
        return wrappedLink.getHref();
    }

    public String getRel() {
        return wrappedLink.getRel();
    }

    public boolean isTemplated() {
        return wrappedLink.isTemplated();
    }

    public String getType() {
        return wrappedLink.getType();
    }

    public String getDeprecation() {
        return wrappedLink.getDeprecation();
    }

    public String getName() {
        return wrappedLink.getName();
    }

    public String getProfile() {
        return wrappedLink.getProfile();
    }

    public String getTitle() {
        return wrappedLink.getTitle();
    }

    public RequestBuilder toRequestBuilder() {

        return wrappedLink.toRequestBuilder();
    }

}

