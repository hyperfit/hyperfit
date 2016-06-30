package org.hyperfit.net;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class RequestInterceptorsTest {

    class InterceptorA implements RequestInterceptor {

        public void intercept(RequestBuilder requestBuilder) {
            requestBuilder.setParam("A", "A");
        }
    }

    class InterceptorB implements RequestInterceptor {
        public void intercept(RequestBuilder requestBuilder) {
            requestBuilder.setParam("B", "B");
        }
    }

    class InterceptorC extends InterceptorA {
        public void intercept(RequestBuilder requestBuilder) {
            requestBuilder.setParam("C", "C");
        }
    }


    @Test
    public void testRemoveByType(){
        RequestInterceptors subject = new RequestInterceptors();

        subject.add(new InterceptorA());
        subject.add(new InterceptorB());
        subject.add(new InterceptorA());
        subject.add(new InterceptorC());

        RequestBuilder mockRequestBuilder = mock(RequestBuilder.class);

        subject.intercept(mockRequestBuilder);

        verify(mockRequestBuilder, times(2)).setParam("A", "A");
        verify(mockRequestBuilder, times(1)).setParam("B", "B");
        verify(mockRequestBuilder, times(1)).setParam("C", "C");
        verifyNoMoreInteractions(mockRequestBuilder);

        subject.remove(InterceptorA.class);

        mockRequestBuilder = mock(RequestBuilder.class);

        subject.intercept(mockRequestBuilder);

        //A and C should be removed
        verify(mockRequestBuilder, times(1)).setParam("B", "B");
        verifyNoMoreInteractions(mockRequestBuilder);

    }



}
