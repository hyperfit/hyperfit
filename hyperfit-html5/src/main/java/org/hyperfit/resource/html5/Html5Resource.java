package org.hyperfit.resource.html5;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.message.Messages;
import org.hyperfit.net.Response;
import org.hyperfit.resource.BaseHyperResource;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.resource.html5.controls.link.Html5HyperLink;
import org.hyperfit.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Hal Json Implementation of a HyperMedia Resource
 */
@ToString(exclude = {"linkCache","htmlResource"})
@EqualsAndHashCode(exclude = {"linkCache","htmlResource"})
public class Html5Resource extends BaseHyperResource {


    private final HashMap<String, HyperLink[]> linkCache = new HashMap<String, HyperLink[]>(5);
    private final Document htmlResource;

    public Html5Resource(Response response) {
        try {
            this.htmlResource = Jsoup.parse(response.getBody());
        } catch (Exception ex) {
            throw new HyperfitException(
                ex,
                Messages.MSG_ERROR_MEDIATYPE_CANNOT_CREATE_RESOURCE,
                response, Html5Resource.class
            );
        }
    }

    public Html5Resource(Document htmlDoc) {
        if (htmlDoc == null) {
            throw new NullPointerException(Messages.MSG_ERROR_RESOURCE_DATA_SOURCE_NULL);
        }

        this.htmlResource = htmlDoc;
    }



    public HyperLink[] getLinks() {
        //TODO: this doesn't use the rel cache...but since html can have empty rels..that doesn't really make a lot of sense. Maybe there's a better way
        return doLinkParsing(htmlResource.select("a,link"));
    }

    private static final String anchorByRelSelector = "a[rel=%s]";
    private static final String linkByRelSelector = "link[rel=%s]";
    public HyperLink[] getLinks(String relationship) {
        if (StringUtils.isEmpty(relationship)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_LINK_WITHOUT_REL);
        }

        String anchor = String.format(anchorByRelSelector, relationship);
        String link = String.format(linkByRelSelector, relationship);

        String fullQuery = anchor + ", " + link;

        if(!linkCache.containsKey(relationship)){
            linkCache.put(relationship, doLinkParsing(htmlResource.select(fullQuery)));
        }

        return linkCache.get(relationship);
    }


    private static HyperLink[] doLinkParsing(Elements matchingLinks){
        if(matchingLinks.isEmpty()){
            return new HyperLink[0];
        }



        HyperLink[] results = new HyperLink[matchingLinks.size()];
        int i = 0;
        for(Element link : matchingLinks){
            results[i] = linkFromElement(link);
            i++;
        }

        return results;

    }



    public boolean canResolveLinkLocal(String relationship) {
        return false;
    }

    public HyperResource resolveLinkLocal(String relationship) {
        throw new HyperResourceException(Messages.MSG_ERROR_RESOURCE_LINK_NOT_FOUND, relationship, this.htmlResource);
    }

    public HyperResource[] resolveLinksLocal(String relationship) {
        throw new HyperResourceException(Messages.MSG_ERROR_RESOURCE_LINK_NOT_FOUND, relationship, htmlResource);
    }



    public boolean isMultiLink(String relationship) {
        //TODO: decide about this...this is aroudn so we know not to try to fetch multi links in HAL..but does same apply to HTML?
        return getLinks(relationship).length > 1;
    }




    public boolean hasLink(String relationship){
        //HAL can embed links, so let's check if it's embedded first, otherwise use default implementation
        return this.canResolveLinkLocal(relationship) || super.hasLink(relationship);
    }


    public boolean hasPath(String... path) {
        return false;
    }

    public <T> T getPathAs(Class<T> classToReturn, String... path) {
        throw new HyperResourceException(Messages.MSG_ERROR_RESOURCE_DATA_PATH_NOT_FOUND, path, htmlResource);
    }


    @Override
    public Form getForm(String formName) {
        throw new IllegalArgumentException();
    }

    @Override
    public boolean hasForm(String formName) {
        return false;
    }

    @Override
    public Form[] getForms() {
        return new Form[0];
    }

    //This is here for Equals to work well with lombak. Two JSoup docs aren't equal even if there contents are.  This means equals for this resource is possibly slow
    //I don't really want to store the full html always..it's already in the resource
    private String rawHtml = "";
    public String getRawHtml(){
        return this.htmlResource.html();
    }


    public static HyperLink linkFromElement(Element node){

        String href = node.attr("href");
        if (StringUtils.isEmpty(href)) {
            throw new IllegalArgumentException("Malformed link node. href field empty: " + node.html());
        }

        return new Html5HyperLink(
            href,
            //note in html rel isn't required (as opposed to say hal)
            node.hasAttr("rel") ? node.attr("rel") : null,
            //html links are never templated
            false,
            node.hasAttr("type") ? node.attr("type") : null,
            //
            null,
            node.hasAttr("name") ? node.attr("name") : null,
            node.hasAttr("profile") ? node.attr("profile") : null,
            node.hasAttr("title") ? node.attr("title") : node.text(),
            node.hasAttr("hrefLang") ? node.attr("hrefLang") : null
        );
    }

}
