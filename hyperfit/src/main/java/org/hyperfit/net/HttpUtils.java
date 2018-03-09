package org.hyperfit.net;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utility class for working with http requests and responses
 */
public final class HttpUtils {

    private HttpUtils(){

    }

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     *
     * Builds an HTTP accept header using the .
     *
     * @param acceptedContentTypes collection of sets of acceptable content types used to construct the result.  Order is preserved.
     * @return comma separated media type values. (e.g. "application/hal+json,application/atom+xml"
     */
    public static String buildAcceptHeaderValue(
        //TODO: make this a LinkedHashSet?  or maybe a list? want to imply order but also needs to be
        // compatible with Request's interface which currently uses Set<String>
        Set<String>... acceptedContentTypes
    ) {
        HashSet<String> t = new LinkedHashSet<String>();
        for(Set<String> set: acceptedContentTypes){
            t.addAll(set);
        }

        Iterator<String> contentTypes = t.iterator();
        StringBuilder builder = new StringBuilder();
        while (contentTypes.hasNext()) {
            builder.append(contentTypes.next());
            if (contentTypes.hasNext()){
                builder.append(",");
            }
        }
        return builder.toString();
    }



}
