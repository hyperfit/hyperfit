package org.hyperfit.net;

import java.util.Collections;
import java.util.Set;


/**
 * Partial implementation of client contract
 */
public abstract class BaseHyperClient implements HyperClient {

    private Set<String> acceptedContentTypes;

    /**
     * Set accept Media Type
     * @param acceptedContentTypes {@link Set} of {@link String} to set
     */
    public BaseHyperClient setAcceptedContentTypes(Set<String> acceptedContentTypes) {
        this.acceptedContentTypes = Collections.unmodifiableSet(acceptedContentTypes);
        return this;
    }

    /**
     * get accept header based on accept media types
     * @return {@link String} return comma delimited list of accepted headers
     */
    protected Set<String> getAcceptedContentTypes() {
        return this.acceptedContentTypes;
    }
    

}
