package org.hyperfit.resource.hal.json.controls.link;

import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.utils.StringUtils;

public class HalHyperLink extends HyperLink {

    public HalHyperLink(String href, String rel, boolean templated, String type, String deprecation, String name, String profile, String title, String hrefLang) {
        super(
            href,
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


}
