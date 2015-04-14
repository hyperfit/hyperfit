package org.hyperfit.resource.html5.controls.link;

import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.utils.StringUtils;

public class Html5HyperLink extends HyperLink {

    public Html5HyperLink(String href, String rel, boolean templated, String type, String deprecation, String name, String profile, String title, String hrefLang) {
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
        BoringRequestBuilder builder =  new BoringRequestBuilder().setUrl(this.getHref());


        if(!StringUtils.isEmpty(this.getType())){
            builder.addAcceptedContentType(this.getType());
        }

        return builder;
    }


}
