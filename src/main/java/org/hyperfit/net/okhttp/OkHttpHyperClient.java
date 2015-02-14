package org.hyperfit.net.okhttp;

import java.net.CookieHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.BaseHyperClient;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Response;
import org.hyperfit.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hyperfit.message.Messages;
import org.hyperfit.net.Request;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

/**
 *Implementation of hypermedia provider, making http request using OKHTTP Client
 *
 */
public class OkHttpHyperClient extends BaseHyperClient {

    private static final Logger LOG = LoggerFactory.getLogger(HyperClient.class);

    private final OkHttpClientShim okHttpClient;

    public OkHttpHyperClient() {
        this.okHttpClient = new OkHttpClientShim(new OkHttpClient());
    }

    /**
     * Set OKHTTP Client for the Hyper provider
     * @param okHttpClient {@link com.squareup.okhttp.OkHttpClient}
     */
    public OkHttpHyperClient(OkHttpClientShim okHttpClient) {
        if (okHttpClient == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CLIENT_NULL);
        }

        this.okHttpClient = okHttpClient;
    }

    /**
     * Set OKHTTP Client for the Hyper provider
     * @param okHttpClient {@link com.squareup.okhttp.OkHttpClient}
     */
    public OkHttpHyperClient(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CLIENT_NULL);
        }

        this.okHttpClient = new OkHttpClientShim(okHttpClient);
    }

    /**
     *
     * @param request {@link org.hyperfit.net.Request}
     * @return {@inheritDoc}
     */
    public Response execute(Request request) {
        // Validate required elements
        if (request == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CLIENT_REQUEST_NULL);
        }

        if (request.getMethod() == null) {
            throw new NullPointerException(Messages.MSG_ERROR_CLIENT_REQUEST_METHOD_NULL);
        }

        if (StringUtils.isEmpty(request.getUrl())) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_CLIENT_REQUEST_URL_NULL);
        }

        return doResponse(doRequest(prepareRequest(request)));
    }
   
    public OkHttpHyperClient setAcceptedContentTypes(Set<String> acceptedContentTypes) {
        return (OkHttpHyperClient)super.setAcceptedContentTypes(acceptedContentTypes);
    }

    public HyperClient setCookieHandler(CookieHandler handler) {
        this.okHttpClient.setCookieHandler(handler);
        return this;
    }

    /**
     * Use the request builder to build the request to be executed in the future
     * @param request {@link org.hyperfit.net.Request} includes url,method, headers information
     * @return {@link com.squareup.okhttp.Request}
     */
    protected com.squareup.okhttp.Request prepareRequest(Request request) {

        RequestBody requestBody = null;

        if (request.getContentType() != null && request.getContentBody() != null) {
            requestBody = RequestBody.create(MediaType.parse(request.getContentType()), request.getContentBody());
        }
      
        return new com.squareup.okhttp.Request.Builder()
                .url(request.getUrl())
                .method(request.getMethod().name(), requestBody)
                .headers(extractHeadersFromRequest(request))
                .addHeader(HttpHeader.ACCEPT, buildAcceptHeaderValue(request.getAcceptedContentTypes()))
                .build();
    }

    /**
     * execute OKHTTP call
     * @param request
     * @return {@link com.squareup.okhttp.Response}
     */
    protected com.squareup.okhttp.Response doRequest(com.squareup.okhttp.Request request) {

        LOG.trace(Messages.MSG_DEBUG_CLIENT_REQUEST, request.url(), request.method(), request.headers(), request.body());
        
        try {
            return okHttpClient.newCall(request).execute();
        } catch (Exception ex) {
            throw new HyperfitException(ex, Messages.MSG_ERROR_CLIENT_REQUEST_FAILURE, request);
        }
    }

    /**
     * Build HyperMedia Response based on {@link com.squareup.okhttp.Response}
     * @param response
     * @return {@link org.hyperfit.net.Response}
     */
    protected Response doResponse(com.squareup.okhttp.Response response) {
        Response.ResponseBuilder responseBuilder = Response.builder().addCode(response.code());

        for (String headerName : response.headers().names()) {
            responseBuilder.addHeader(headerName, response.header(headerName));
        }

        //Set the content type explicitly, even though it comes from the headers.  Hyperfit needs to know this
        //abstracted from the headers
        responseBuilder.addContentType(response.header(HttpHeader.CONTENT_TYPE));

        try {
            responseBuilder.addBody(response.body().string());
        } catch (Exception ex) {
            throw new HyperfitException(ex, Messages.MSG_ERROR_CLIENT_REQUEST_RESPONSE_FAILURE, response);
        }

        LOG.trace(Messages.MSG_DEBUG_CLIENT_RESPONSE, response);
        return responseBuilder.build();
    }

    /**
     * Maps headers added to the Request object into OKHTTP request headers.
     *
     * @param request hypermedia request object
     * @return headers as used by OKHTTP
     */
    protected Headers extractHeadersFromRequest(Request request) {
        Headers.Builder headersBuilder = new Headers.Builder();
        Iterator<Entry<String, String>> headersIterator = request.getHeaders();

        if (null != headersIterator) {
            while (headersIterator.hasNext()) {
                Entry<String, String> headerEntry = headersIterator.next();
                headersBuilder.add(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        return headersBuilder.build();
    }

    /**
     * Builds the HTTP accept header using the configured media types for the client.
     * @return comma separated media type values. (e.g. "application/hal+json,application/atom+xml"
     */
    private String buildAcceptHeaderValue(Set<String> requestAcceptedContentTypes) {
        HashSet<String> allContentTypes =  new HashSet<String>(this.getAcceptedContentTypes());
        allContentTypes.addAll(requestAcceptedContentTypes);

        Iterator<String> contentTypes = allContentTypes.iterator();

        StringBuilder builder =  new StringBuilder();
        while(contentTypes.hasNext()){
            builder.append(contentTypes.next());

            if(contentTypes.hasNext()){
                builder.append(",");
            }
        }

        return builder.toString();

    }
}
