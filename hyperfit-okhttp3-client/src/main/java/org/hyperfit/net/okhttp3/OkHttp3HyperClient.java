package org.hyperfit.net.okhttp3;

import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.BaseHyperClient;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.utils.StringUtils;

import java.net.CookieHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * Implementation of hypermedia provider, making http request using OkHttp 3.x Client
 */
public class OkHttp3HyperClient extends BaseHyperClient {

    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private final OkHttpClient client;

    /**
     * Set OkHttp Client for the Hyper provider
     *
     * @param okHttpClient {@link okhttp3.OkHttpClient}
     */
    public OkHttp3HyperClient(OkHttpClient okHttpClient) {
        if (okHttpClient == null) throw new NullPointerException("Error while creating http client. Client cannot be null.");
        this.client = okHttpClient;
    }

    /**
     * @param request {@link org.hyperfit.net.Request}
     * @return {@inheritDoc}
     */
    public Response execute(Request request) {
        if (request == null) throw new NullPointerException("Error while generating client request. Request cannot be null.");
        if (request.getMethod() == null)
            throw new NullPointerException("Error while generating client request. Request method cannot be null.");
        if (StringUtils.isEmpty(request.getUrl()))
            throw new IllegalArgumentException("Error while generating client request. Request url cannot be empty.");
        return doResponse(doRequest(prepareRequest(request)), request);
    }

    public HyperClient setCookieHandler(CookieHandler handler) {
        throw new UnsupportedOperationException();
    }

    public String[] getSchemes() {
        return new String[]{"http", "https"};
    }

    //at some point after OkHttp 2.1 they added the requirement of a request body for certain http methods
    //this can happen a lot..so let's make one dummy one and reuse it.
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(null, "");

    /**
     * Use the request builder to build the request to be executed in the future
     *
     * @param request {@link org.hyperfit.net.Request} includes url,method, headers information
     * @return {@link okhttp3.Request}
     */
    private okhttp3.Request prepareRequest(Request request) {
        RequestBody requestBody = null;
        if (request.getContentType() != null && request.getContent() != null) {
            requestBody = RequestBody.create(MediaType.parse(request.getContentType()), request.getContent());
        } else if (HttpMethod.requiresRequestBody(request.getMethod().name())) {
            requestBody = EMPTY_REQUEST_BODY;
        }
        return new okhttp3.Request.Builder()
                .url(request.getUrl())
                .method(request.getMethod().name(), requestBody)
                .headers(extractHeadersFromRequest(request))
                .addHeader(ACCEPT, buildAcceptHeaderValue(request.getAcceptedContentTypes()))
                .build();
    }

    /**
     * execute OkHttp call
     *
     * @return {@link okhttp3.Response}
     */
    private okhttp3.Response doRequest(okhttp3.Request request) {
        try {
            return client.newCall(request).execute();
        } catch (Exception ex) {
            throw new HyperfitException("The request [{}] could not be executed.", ex);
        }
    }

    /**
     * Build HyperMedia Response based on {@link okhttp3.Response}
     *
     * @return {@link org.hyperfit.net.Response}
     */
    private Response doResponse(okhttp3.Response response, Request request) {
        Response.ResponseBuilder b = Response.builder()
                .addCode(response.code())
                .addRequest(request);
        for (Entry<String, java.util.List<String>> h : response.headers().toMultimap().entrySet())
            for (String val : h.getValue())
                b.addHeader(h.getKey(), val);
        //Set the content type explicitly, even though it comes from the headers.  Hyperfit needs to know this
        //abstracted from the headers
        b.addContentType(response.header(CONTENT_TYPE));
        try {
            b.addBody(response.body().string());
        } catch (Exception ex) {
            throw new HyperfitException("The response [{}] could not be generated correctly.", ex);
        }
        return b.build();
    }

    /**
     * Maps headers added to the Request object into OkHttp request headers.
     *
     * @param request hypermedia request object
     * @return headers as used by OKHTTP
     */
    private Headers extractHeadersFromRequest(Request request) {
        Headers.Builder b = new Headers.Builder();
        Iterable<Entry<String, String>> headers = request.getHeaders();
        if (null != headers)
            for (Entry<String, String> h : headers)
                b.add(h.getKey(), h.getValue());
        return b.build();
    }

    /**
     * Builds the HTTP accept header using the configured media types for the client.
     *
     * @return comma separated media type values. (e.g. "application/hal+json,application/atom+xml"
     */
    private String buildAcceptHeaderValue(Set<String> requestAcceptedContentTypes) {
        HashSet<String> t = new LinkedHashSet<String>(requestAcceptedContentTypes);
        t.addAll(this.getAcceptedContentTypes());
        Iterator<String> contentTypes = t.iterator();
        StringBuilder builder = new StringBuilder();
        while (contentTypes.hasNext()) {
            builder.append(contentTypes.next());
            if (contentTypes.hasNext()) builder.append(",");
        }
        return builder.toString();
    }
}
