package org.hyperfit;

import org.hyperfit.exception.NoClientRegisteredForSchemeException;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static test.TestUtils.uniqueString;

//TODO: switch this back to builder in v2
public class SchemeBasedHyperClientSelectionStrategyTest {

    @Mock
    HyperClient mockClient1;


    @Mock
    HyperClient mockClient2;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetAcceptedContentTypesCalledOncePerClient(){

        HashMap<String,HyperClient> clientMap = new HashMap<String, HyperClient>();
        //registered mockClient1 for 2 different schemes
        clientMap.put(
            uniqueString(),
            mockClient1
        );

        clientMap.put(
            uniqueString(),
            mockClient1
        );

        clientMap.put(
            uniqueString(),
            mockClient2
        );


        HashSet<String> acceptedContentTypes = new HashSet<String>(Arrays.asList(
            "application/hal+json"
        ));

        SchemeBasedHyperClientSelectionStrategy subject = new SchemeBasedHyperClientSelectionStrategy(
            clientMap,
            acceptedContentTypes
        );

        verify(mockClient1, times(1)).setAcceptedContentTypes(acceptedContentTypes);
        verifyNoMoreInteractions(mockClient1);

        verify(mockClient2, times(1)).setAcceptedContentTypes(acceptedContentTypes);
        verifyNoMoreInteractions(mockClient2);

    }


    @Test
    public void testClientListParamReferenceNotHeld(){
        HashMap<String,HyperClient> clientMap = new HashMap<String, HyperClient>();
        String scheme = uniqueString();

        clientMap.put(
            scheme,
            mockClient1
        );


        Request mockRequest = mock(Request.class);
        when(mockRequest.getUrl())
            .thenReturn(
                scheme + "://host"
            );


        HashSet<String> acceptedContentTypes = new HashSet<String>(Arrays.asList(
            "application/hal+json"
        ));

        SchemeBasedHyperClientSelectionStrategy subject = new SchemeBasedHyperClientSelectionStrategy(
            clientMap,
            acceptedContentTypes
        );



        assertSame(
            "choosen client must be the one passed in constructor",
            mockClient1,
            subject.chooseClient(mockRequest)
        );


        //now replaced the client with the new client
        clientMap.put(
            scheme,
            mockClient2
        );


        assertSame(
            "choosen client must be the one passed in constructor",
            mockClient1,
            subject.chooseClient(mockRequest)
        );


        SchemeBasedHyperClientSelectionStrategy subject2 = new SchemeBasedHyperClientSelectionStrategy(
            clientMap,
            acceptedContentTypes
        );

        assertSame(
            "choosen client must be the one passed in 2nd constructor",
            mockClient2,
            subject2.chooseClient(mockRequest)
        );

    }




    @Test
    public void testChooseClient() {

        HashMap<String, HyperClient> clientMap = new HashMap<String, HyperClient>();
        String scheme1 = uniqueString();
        String scheme2 = uniqueString();

        clientMap.put(
            scheme1,
            mockClient1
        );

        clientMap.put(
            scheme2,
            mockClient2
        );


        Request mockRequest1 = mock(Request.class);
        when(mockRequest1.getUrl())
            .thenReturn(
                scheme1 + "://host"
            );


        Request mockRequest2 = mock(Request.class);
        when(mockRequest2.getUrl())
            .thenReturn(
                scheme2 + "://host"
            );


        HashSet<String> acceptedContentTypes = new HashSet<String>(Arrays.asList(
            "application/hal+json"
        ));

        SchemeBasedHyperClientSelectionStrategy subject = new SchemeBasedHyperClientSelectionStrategy(
            clientMap,
            acceptedContentTypes
        );


        assertSame(
            "choosen client must be the one passed in constructor",
            mockClient1,
            subject.chooseClient(mockRequest1)
        );


        assertSame(
            "choosen client must be the one passed in constructor",
            mockClient2,
            subject.chooseClient(mockRequest2)
        );

    }





    @Test
    public void testChooseClientThrowsIllegalArgumentExceptionWhenRequestHasNoScheme(){
        SchemeBasedHyperClientSelectionStrategy subject = new SchemeBasedHyperClientSelectionStrategy(
            Collections.<String, HyperClient>emptyMap(),
            Collections.<String>emptySet()
        );

        Request mockRequest = mock(Request.class);
        when(mockRequest.getUrl())
            .thenReturn(
                ""
            );

        try{
            subject.chooseClient(mockRequest);
            fail("expected exception not thrown");
        } catch (IllegalArgumentException e){
            assertEquals(
                "The request url does not have a scheme",
                e.getMessage()
            );
        }


    }





    @Test
    public void testChooseClientThrowsNoClientRegisteredForSchemeExceptionWhenNoClientFound(){
        SchemeBasedHyperClientSelectionStrategy subject = new SchemeBasedHyperClientSelectionStrategy(
            Collections.<String, HyperClient>emptyMap(),
            Collections.<String>emptySet()
        );

        String scheme = uniqueString();

        Request mockRequest = mock(Request.class);
        when(mockRequest.getUrl())
            .thenReturn(
                scheme + "://host"
            );

        try{
            subject.chooseClient(mockRequest);
            fail("expected exception not thrown");
        } catch (NoClientRegisteredForSchemeException e){
            assertEquals(
                "No HyperClient has been registered for the " + scheme + " scheme",
                e.getMessage()
            );
        }

    }


}