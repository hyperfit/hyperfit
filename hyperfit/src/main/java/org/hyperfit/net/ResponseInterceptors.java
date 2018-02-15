package org.hyperfit.net;

import java.util.ArrayList;
import java.util.List;

public class ResponseInterceptors {

    private final List<ResponseInterceptor> interceptors = new ArrayList<ResponseInterceptor>();

    public ResponseInterceptors(){

    }

    public void intercept(Response response) {

        for (ResponseInterceptor interceptor : interceptors) {

            interceptor.intercept(response);

        }
    }

    public void add(ResponseInterceptor requestInterceptor) {
        interceptors.add(requestInterceptor);
    }
}
