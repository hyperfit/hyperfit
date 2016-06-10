package org.hyperfit.net;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

public class RequestTest {

    @Test
    public void testURLIsRequired(){

        RequestBuilder mockBuilder = mock(RequestBuilder.class);

        try {
            new Request(mockBuilder);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("builder's url can not be null or empty"));
        }

    }
}
