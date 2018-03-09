package org.hyperfit.net.okhttp1;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.BaseHyperClient;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class OkHttp1HyperClient extends BaseHyperClient {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static Logger LOG = LoggerFactory.getLogger(OkHttp1HyperClient.class);
    private final OkHttpClient okHttpClient;

    public OkHttp1HyperClient() {
        this(new OkHttpClient());
    }

    public OkHttp1HyperClient(OkHttpClient okHttpClient){
        if (okHttpClient == null) {
            throw new IllegalArgumentException("okHttpClient cannot be null");
        }
        this.okHttpClient = okHttpClient;
    }


    @Override
    public Response execute(Request request) {
        // Validate required elements
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null.");
        }

        // in practice this should never happen, but just in case
        if (request.getMethod() == null) {
            throw new IllegalArgumentException("request's method cannot be null.");
        }

        // in practice this should never happen, but just in case
        if (Strings.isNullOrEmpty(request.getUrl())) {
            throw new IllegalArgumentException("request's url cannot be empty.");
        }

        try {
            HttpURLConnection connection = openConnection(request);
            prepareRequest(connection, request);
            return readResponse(connection, request);
        } catch (IOException ex) {
            LOG.error("Unable to Execute Request", ex);
            throw new HyperfitException("The request [" + request + "] could not be executed.", ex);
        }
    }

    public HyperClient setCookieHandler(CookieHandler handler) {
        this.okHttpClient.setCookieHandler(handler);
        return this;
    }

    @Override
    public String[] getSchemes() {
        return new String[]{"http", "https"};
    }


    @SuppressWarnings("deprecation")
    protected HttpURLConnection openConnection(Request request) throws IOException {
        return okHttpClient.open(new URL(request.getUrl()));
    }

    protected void prepareRequest(HttpURLConnection connection, Request request) throws IOException{
        connection.setRequestMethod(request.getMethod().name());
        connection.setDoInput(true);

        prepareHeaders(connection, request);

        if (!Strings.isNullOrEmpty(request.getContentType()) && !Strings.isNullOrEmpty(request.getContent())){
            byte [] body = request.getContent().getBytes(charset(request.getContentType()));
            long length = body.length;

            connection.setFixedLengthStreamingMode((int) length);

            connection.getOutputStream().write(body);
        }
    }

    protected void prepareHeaders(HttpURLConnection connection, Request request) {
        if (request.getHeaders() == null){
            return;
        }


        for (Map.Entry<String, String> header: request.getHeaders()){
            connection.addRequestProperty(header.getKey(), header.getValue());
        }

    }

    protected Response readResponse(HttpURLConnection connection, Request request) throws IOException {
        Response.ResponseBuilder responseBuilder = Response.builder();

        addHeadersToResponse(connection, responseBuilder);
        String body = convertResponseBodyToString(connection);

        Response response = responseBuilder
            .addRequest(request)
            .addCode(connection.getResponseCode())
            .addContentType(connection.getContentType())
            .addBody(body)
            .build();

        LOG.trace("Provider generating response [{}].", response);
        return response;
    }

    @VisibleForTesting
    protected void addHeadersToResponse(HttpURLConnection connection, Response.ResponseBuilder responseBuilder) {
        if (connection.getHeaderFields() == null || connection.getHeaderFields().isEmpty()){
            return;
        }

        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            String name = field.getKey();
            if (!Strings.isNullOrEmpty(name)) {
                for (String value : field.getValue()) {

                    // TODO - Need to Review this design with CHRIS D.
                    //          I think there's a bug here, since a we can get multiple headers
                    //          with the same name.  In this code, last one wins!
                    responseBuilder.addHeader(name, value);
                }
            }
        }

    }

    @VisibleForTesting
    protected String convertResponseBodyToString(HttpURLConnection connection) throws IOException {
        InputStream stream = getResponseInputStream(connection);

        InputStreamReader isr = null;

        try {
            Charset charset = charset(connection.getContentType());
            isr = new InputStreamReader(stream, charset);

            return CharStreams.toString(isr);
        } finally {
            Closeables.closeQuietly(isr);
        }
    }

    @VisibleForTesting
    protected InputStream getResponseInputStream(HttpURLConnection connection) throws IOException {
        InputStream stream;
        int status = connection.getResponseCode();
        if (status >= 400){
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
        }
        return stream;
    }


    @VisibleForTesting
    protected Charset charset(String contentType){
        MediaType type = MediaType.parse(contentType);
        return type != null ? type.charset(UTF_8): UTF_8;
    }
}
