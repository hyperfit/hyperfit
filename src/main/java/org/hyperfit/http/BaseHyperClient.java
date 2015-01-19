package org.hyperfit.http;

import java.util.Iterator;
import java.util.Set;

import org.hyperfit.utils.StringUtils;

/**
 * Partial implementation of client contract
 *
 */
public abstract class BaseHyperClient implements HyperClient {

    protected static final String ACCEPT_HEADER = "Accept"; 

    private Set<String> acceptedMediaTypes;   
    private String acceptHeader;

    /**
     * Set accept Media Type
     * @param acceptedMediaTypes {@link Set} of {@link String} to set
     */
    public BaseHyperClient setAcceptedMediaTypes(Set<String> acceptedMediaTypes) {
        this.acceptedMediaTypes = acceptedMediaTypes;
        return this;
    }

    /**
     * get accept header based on accept media types
     * @return {@link String} return comma delimited list of accepted headers
     */
    protected String getAcceptHeader() {
        if(!StringUtils.isEmpty(acceptHeader)) {
            return acceptHeader;
        } else {
            return buildAcceptHeader();
        }
    }
    
    /**
     * Builds the accept header using the configured media types for the client.
     * @return comma separated media type values. (e.g. "application/hal+json,application/atom+xml"
     */
    private String buildAcceptHeader() {
                
        if(acceptedMediaTypes != null && acceptedMediaTypes.size() > 0) {
            StringBuilder builder =  new StringBuilder();
            Iterator<String> iterator = acceptedMediaTypes.iterator();
            
            for(int i=0; i < acceptedMediaTypes.size(); i++){
                builder.append(iterator.next());
                
                if((i+1) < acceptedMediaTypes.size()) {
                    builder.append(",");
                }
            }
            acceptHeader = builder.toString();
        }        
        return acceptHeader;
    }
}
