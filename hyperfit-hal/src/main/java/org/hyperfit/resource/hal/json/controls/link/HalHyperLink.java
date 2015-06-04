package org.hyperfit.resource.hal.json.controls.link;

import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.utils.StringUtils;

import java.net.URI;


public class HalHyperLink extends HyperLink {

    public HalHyperLink(String href, String rel, boolean templated, String type, String deprecation, String name, String profile, String title, String hrefLang, String baseURI) {
        super(
            resolveRelativeURL(baseURI, href),
            rel,
            templated,
            type,
            deprecation,
            name,
            profile,
            title,
            hrefLang
        );

    }

    @Override
    public RequestBuilder toRequestBuilder() {
        RequestBuilder builder =  this.isTemplated() ?
            new RFC6570RequestBuilder().setUrlTemplate(this.getHref())
            :
            new BoringRequestBuilder().setUrl(this.getHref())
        ;

        if(!StringUtils.isEmpty(this.getType())){
            builder.addAcceptedContentType(this.getType());
        }

        return builder;
    }

    private static String resolveRelativeURL(String baseURI, String href){
        //TODO: support all types of relative links. Can't use URI.resolve because templated links are valid URI
        if(baseURI == null || !href.startsWith("/")){
            return href;
        }

        URI base = URI.create(baseURI);

        return base.getScheme() + "://" + base.getAuthority() + href;
    }


}
