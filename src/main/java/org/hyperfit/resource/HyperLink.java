package org.hyperfit.resource;


import org.hyperfit.http.Request;
import org.hyperfit.utils.TypeRef;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a hyper link for a HyperResource
 * Contains fields for all properties of a HAL Hyper Link currently, but in future other specs Hyperlink params will be added
 */
@ToString
@EqualsAndHashCode
public class HyperLink {

    private final String href;
    private final String rel;
    private final boolean templated;
    private final String type;
    private final String deprecation;
    private final String name;
    private final String profile;
    private final String title;
    private final String hrefLang;

    public HyperLink(String href, String rel, boolean templated, String type, String deprecation, String name, String profile, String title, String hrefLang) {
        this.href = href;
        this.rel = rel;
        this.templated = templated;
        this.type = type;
        this.deprecation = deprecation;
        this.name = name;
        this.profile = profile;
        this.title = title;
        this.hrefLang = hrefLang;
    }

    public HyperLink(HyperLink hyperlink) {
        this.href = hyperlink.href;
        this.rel = hyperlink.rel;
        this.templated = hyperlink.templated;
        this.type = hyperlink.type;
        this.deprecation = hyperlink.deprecation;
        this.name = hyperlink.name;
        this.profile = hyperlink.profile;
        this.title = hyperlink.title;
        this.hrefLang = hyperlink.hrefLang;
    }

    public String getHrefLang() {
        return hrefLang;
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public boolean isTemplated() {
        return templated;
    }

    public String getType() {
        return type;
    }

    public String getDeprecation() {
        return deprecation;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public String getTitle() {
        return title;
    }

    public Request.RequestBuilder toRequestBuilder() {
        return Request.builder().setUrlTemplate(this.getHref());
    }

    public <R> R follow(TypeRef<R> typeRef){
        //TODO: really don't like this..but since HyperResource has the HyperLink getLink()
        //and thus ever resource type (HalJsonResource, etc) must be able to return a HyperLink
        //but they can't actually perform a follow..it needs to go through the proxy for that..
        throw new NotImplementedException();
    }



}
