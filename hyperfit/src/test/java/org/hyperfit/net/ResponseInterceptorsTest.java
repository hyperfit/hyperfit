package org.hyperfit.net;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ResponseInterceptorsTest {

    public static class CheckHeaderInterceptor implements ResponseInterceptor{

        List<String> HEADERS_TO_CHECK = new ArrayList<String>();

        public CheckHeaderInterceptor(String... headers){
            HEADERS_TO_CHECK.addAll(Arrays.asList(headers));
        }


        public void intercept(Response response) {

            for (String header: HEADERS_TO_CHECK){
                assertTrue(response.getHeader(header) == null);
            }

        }
    }


    public static class StatusResponseInterceptor implements ResponseInterceptor{

        public void intercept(Response response) {

                if(response.getCode() == 200){
                    ////
                }
        }
    }

    @Test
    public void testCheckHeaderInterceptor(){

        ResponseInterceptors subject = new ResponseInterceptors();

        subject.add(new ResponseInterceptorsTest.CheckHeaderInterceptor("bb-app", "bb-version"));
        subject.add(new ResponseInterceptorsTest.StatusResponseInterceptor());

        Response response = mock(Response.class);

        subject.intercept(response);

        verify(response, times(1)).getHeader("bb-app");
        verify(response, times(1)).getHeader("bb-version");
        verify(response, times(1)).getCode();
        verifyNoMoreInteractions(response);

    }


}
