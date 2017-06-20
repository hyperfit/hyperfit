package org.hyperfit.net.okhttp2;

import java.net.CookieHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.squareup.okhttp.internal.http.HttpMethod;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.*;
import org.hyperfit.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

/**
 *Implementation of hypermedia provider, making http request using OKHTTP Client
 *
 */
public class OkHttp2HyperClient extends BaseHyperClient {

    private static final Logger LOG = LoggerFactory.getLogger(HyperClient.class);

    private final OkHttp2ClientShim okHttpClient;

    public OkHttp2HyperClient() {
        this.okHttpClient = new OkHttp2ClientShim(new OkHttpClient());
    }

    /**
     * Set OKHTTP Client for the Hyper provider
     * @param okHttpClient {@link com.squareup.okhttp.OkHttpClient}
     */
    public OkHttp2HyperClient(OkHttp2ClientShim okHttpClient) {
        if (okHttpClient == null) {
            throw new IllegalArgumentException("okHttpClient cannot be null");
        }

        this.okHttpClient = okHttpClient;
    }

    /**
     * Set OKHTTP Client for the Hyper provider
     * @param okHttpClient {@link com.squareup.okhttp.OkHttpClient}
     */
    public OkHttp2HyperClient(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            throw new IllegalArgumentException("okHttpClient cannot be null");
        }

        this.okHttpClient = new OkHttp2ClientShim(okHttpClient);
    }

    /**
     *
     * @param request {@link org.hyperfit.net.Request}
     * @return {@inheritDoc}
     */
    public Response execute(Request request) {
        // Validate required elements
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null.");
        }

        if (request.getMethod() == null) {
            throw new IllegalArgumentException("request's method cannot be null.");
        }

        if (StringUtils.isEmpty(request.getUrl())) {
            throw new IllegalArgumentException("request's url cannot be empty.");
        }

        return doResponse(doRequest(prepareRequest(request)), request);
    }

    public OkHttp2HyperClient setAcceptedContentTypes(Set<String> acceptedContentTypes) {
        return (OkHttp2HyperClient)super.setAcceptedContentTypes(acceptedContentTypes);
    }

    public HyperClient setCookieHandler(CookieHandler handler) {
        this.okHttpClient.setCookieHandler(handler);
        return this;
    }

    public String[] getSchemes() {
        return new String[]{"http", "https"};
    }

    //at some point after okhttp 2.1 they added the requirement of a request body for certain http methods
    //this can happen a lot..so let's make one dummy one and reuse it.
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(null, "");

    /**
     * Use the request builder to build the request to be executed in the future
     * @param request {@link org.hyperfit.net.Request} includes url,method, headers information
     * @return {@link com.squareup.okhttp.Request}
     */
    protected com.squareup.okhttp.Request prepareRequest(Request request) {

        RequestBody requestBody = null;

        if (request.getContentType() != null && request.getContent() != null) {
            requestBody = RequestBody.create(MediaType.parse(request.getContentType()), request.getContent());
        } else if(HttpMethod.requiresRequestBody(request.getMethod().name())){
            requestBody = EMPTY_REQUEST_BODY;
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

        LOG.trace("Provider executing request url=[{}], method={}, headers={}, body=[{}].", request.url(), request.method(), request.headers(), request.body());

        try {
            return okHttpClient.newCall(request).execute();
        } catch (Exception ex) {
            LOG.error("Unable to Execute Request", ex);
            throw new HyperfitException("The request [" + request + "] could not be executed.", ex);
        }
    }

    /**
     * Build HyperMedia Response based on {@link com.squareup.okhttp.Response}
     * @param response
     * @return {@link org.hyperfit.net.Response}
     */
    protected Response doResponse(com.squareup.okhttp.Response response, Request request) {
        Response.ResponseBuilder responseBuilder = Response.builder()
            .addCode(response.code())
            //TODO: decide if this is ok..or should we build a request from the request
            //available at okhttp.Response.getRequest()?
            .addRequest(request)
        ;

        for( Entry<String,java.util.List<String>> h  :response.headers().toMultimap().entrySet()){
            for(String val : h.getValue()){
                responseBuilder.addHeader(h.getKey(), val);
            }
        }


        //Set the content type explicitly, even though it comes from the headers.  Hyperfit needs to know this
        //abstracted from the headers
        responseBuilder.addContentType(response.header(HttpHeader.CONTENT_TYPE));

        try {
            responseBuilder.addBody(response.body().string());
        } catch (Exception ex) {
            throw new HyperfitException("The response [" + response + "] could not be generated correctly.", ex);
        }

        LOG.trace("Response is {}", response);
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
        Iterable<Entry<String,String>> headers = request.getHeaders();


        if (null != headers) {
            for(Entry<String,String> h : headers){
                headersBuilder.add(h.getKey(), h.getValue());
            }
        }

        return headersBuilder.build();
    }

    /**
     * Builds the HTTP accept header using the configured media types for the client.
     * @return comma separated media type values. (e.g. "application/hal+json,application/atom+xml"
     */
    private String buildAcceptHeaderValue(Set<String> requestAcceptedContentTypes) {
        HashSet<String> allContentTypes =  new LinkedHashSet<String>(requestAcceptedContentTypes);
        allContentTypes.addAll(this.getAcceptedContentTypes());

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
