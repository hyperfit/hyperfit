package org.hyperfit;


import org.hyperfit.net.*;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.InterfaceSelectionStrategy;

import org.hyperfit.utils.TypeRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static test.TestUtils.*;

public class HyperfitProcessorTest {

    public interface BaseProfileResource extends HyperResource {

    }

    public interface ProfileResource1 extends BaseProfileResource {
    }

    public interface ProfileResource2 extends BaseProfileResource {
    }


    @Mock
    private HyperResource mockHyperResource;

    @Mock
    private HyperClient mockHyperClient;

    @Mock
    protected InterfaceSelectionStrategy mockSelectionStrategy;

    HyperfitProcessor.Builder builder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});

        builder = HyperfitProcessor.builder()
            .hyperClient(mockHyperClient)
            .interfaceSelectionStrategy(mockSelectionStrategy)
            ;
    }



    @Test
    public void testInvokeSingleProfileResourceTest() {

        HyperfitProcessor processor = builder.build();

        when(mockSelectionStrategy.determineInterfaces(BaseProfileResource.class, mockHyperResource))
            .thenReturn(new Class[]{ProfileResource1.class, ProfileResource2.class});

        BaseProfileResource result = processor.processResource(BaseProfileResource.class, mockHyperResource, null);

        assertTrue(result instanceof ProfileResource1);
        assertTrue(result instanceof ProfileResource2);

    }



    @Test
    public void testProcessResourceWithArrayOfRegisteredProfiles() {


        HyperfitProcessor processor = builder.build();

        when(mockSelectionStrategy.determineInterfaces(BaseProfileResource.class, mockHyperResource))
            .thenReturn(new Class[]{ProfileResource1.class, ProfileResource2.class});


        BaseProfileResource result = processor.processResource(BaseProfileResource.class, mockHyperResource, null);

        assertTrue(result instanceof ProfileResource1);
        assertTrue(result instanceof ProfileResource2);

    }


    @Test
    public void testProcessResourceWithUnregisteredProfile() {
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});

        HyperfitProcessor processor = builder.build();

        when(mockSelectionStrategy.determineInterfaces(BaseProfileResource.class, mockHyperResource))
            .thenReturn(new Class[]{BaseProfileResource.class});


        BaseProfileResource result = processor.processResource(BaseProfileResource.class, mockHyperResource, null);

        assertTrue(result instanceof BaseProfileResource);

    }


    @Test
    public void testProcessRequestThrowsForBadArgs(){
        HyperfitProcessor processor = builder.build();

        try{
            processor.processRequest((Class<?>)null, (String)null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("entryPointURL can not be null or empty"));
        }


        try{
            //it's a bit odd to have to set the 2nd param to get the first to error, but it's cause
            //of the polymorphic chain and wanting the return type to be the first param always
            processor.processRequest((Class<?>)null, "xyz://localhost");
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("classToReturn can not be null"));
        }


        try{
            processor.processRequest((Class<?>)null, (RequestBuilder) null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("classToReturn can not be null"));
        }


        try{
            processor.processRequest(String.class, (RequestBuilder) null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("requestBuilder can not be null"));
        }


        try{
            processor.processRequest(null, null, null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("classToReturn can not be null"));
        }


        try{
            processor.processRequest(String.class, null, null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("requestBuilder can not be null"));
        }



        try{
            processor.processRequest(new TypeRef<Object>() {}, (String)null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("entryPointURL can not be null or empty"));
        }

        try{
            processor.processRequest((TypeRef<? extends Object>) null, "xyz://local");
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("typeToReturn can not be null"));
        }


        try{
            processor.processRequest((TypeRef<? extends Object>) null, (RequestBuilder) null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("typeToReturn can not be null"));
        }

        try{
            processor.processRequest(new TypeRef<Object>() {}, (RequestBuilder)null);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("requestBuilder can not be null"));
        }

    }



    @Test
    public void testProcessRequestRunsInterceptors(){
        RequestInterceptor mockInterceptor1 = mock(RequestInterceptor.class);
        RequestInterceptor mockInterceptor2 = mock(RequestInterceptor.class);

        HyperClient mockClient = mock(HyperClient.class);




        HyperfitProcessor processor = builder
            .addRequestInterceptor(mockInterceptor1)
            .addRequestInterceptor(mockInterceptor2)
            .hyperClient(mockClient, "xyz")
            .build();

        BoringRequestBuilder request = new BoringRequestBuilder()
            .setUrl("xyz://local");

        String fakeResponseBody = uniqueString();
        when(mockClient.execute(request.build()))
            .thenReturn(
                Response.builder()
                    .addRequest(request.build())
                    .addBody(fakeResponseBody)
                    .build()
            );

        String actual = processor.processRequest(String.class, request, null);


        assertEquals(fakeResponseBody, actual);

        verify(mockInterceptor1, times(1)).intercept(Matchers.any(RequestBuilder.class));
        verify(mockInterceptor1, times(1)).intercept(request);

        verify(mockInterceptor2, times(1)).intercept(Matchers.any(RequestBuilder.class));
        verify(mockInterceptor2, times(1)).intercept(request);

    }

    @Test
    public void testResponseInterceptor(){

        ResponseInterceptor responseInterceptor = mock(ResponseInterceptor.class);

        HyperClient mockClient = mock(HyperClient.class);


        HyperfitProcessor processor = builder
                .addResponseInterceptor(responseInterceptor)
                .hyperClient(mockClient, "xyz")
                .build();

        BoringRequestBuilder request = new BoringRequestBuilder()
                .setUrl("xyz://local");

        String fakeResponseBody = uniqueString();

        Response response = Response.builder()
                .addRequest(request.build())
                .addBody(fakeResponseBody)
                .build();

        when(mockClient.execute(request.build()))
                .thenReturn(response);

        String actual = processor.processRequest(String.class, request, null);


        assertEquals(fakeResponseBody, actual);

        verify(responseInterceptor, times(1)).intercept(Matchers.any(Response.class));
        verify(responseInterceptor, times(1)).intercept(response);

    }

}
