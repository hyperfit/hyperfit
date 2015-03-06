package org.hyperfit;

import org.hyperfit.annotation.Profiles;
import org.hyperfit.exception.ResponseException;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.HttpHeader;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.registry.ProfileResourceRegistryIndexStrategy;
import org.hyperfit.resource.registry.ResourceRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HyperRequestProcessorTest {

    private ResourceRegistry resourceRegistry = new ResourceRegistry(new ProfileResourceRegistryIndexStrategy())
        .add(
            Arrays.<Class<? extends HyperResource>>asList(
                ProfileResource1.class,
                ProfileResource2.class,
                MultipleProfileResource.class
            )
        );

    public interface BaseProfileResource extends HyperResource {

    }

    @Profiles("/a/b/c/profile-resource-1")
    public interface ProfileResource1 extends BaseProfileResource {
    }

    @Profiles("/a/b/c/profile-resource-2")
    public interface ProfileResource2 extends BaseProfileResource {
    }

    @Profiles({"/a/b/c/multiple-profile-resource-a", "/a/b/c/multiple-profile-resource-b"})
    public interface MultipleProfileResource extends BaseProfileResource {
    }

    @Profiles("/a/b/c/not-in-registry-resource")
    public interface NotInRegistryResource extends BaseProfileResource {

    }

    @Mock
    private HyperResource mockHyperResource;

    @Mock
    private HyperClient mockHyperClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ResponseException.class)
    public void testBuildResourceNoContentTypeException() {
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder(mockHyperClient));
        Response response = new Response.ResponseBuilder().build();
        Request request = Request.builder().setUrlTemplate("http://here.com").build();

        hyperRequestProcessor.buildHyperResource(request, response, HyperResource.class);
    }

    @Test(expected = ResponseException.class)
    public void testBuildResourceNoHyperMediaTypeHandlerException() {
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder(mockHyperClient));
        Response response = new Response.ResponseBuilder().
            addHeader(HttpHeader.CONTENT_TYPE, "someType").
            build();
        Request request = Request.builder().setUrlTemplate("http://here.com").build();

        hyperRequestProcessor.buildHyperResource(request, response, HyperResource.class);
    }

    @Test
    public void testInvokeSingleProfileResourceTest() {

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add("/a/b/c/profile-resource-1");
        when(mockHyperResource.getProfiles()).thenReturn(profiles);

        HyperRequestProcessor processor = new HyperRequestProcessor(new RootResourceBuilder(mockHyperClient).resourceRegistry(this.resourceRegistry));

        BaseProfileResource result = processor.processResource(BaseProfileResource.class, mockHyperResource, null);

        assertTrue(result instanceof ProfileResource1);


    }



    @Test
    public void testProcessResourceWithArrayOfRegisteredProfiles() {


        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add("/a/b/c/profile-resource-1");
        profiles.add("/a/b/c/profile-resource-2");
        profiles.add("/a/b/c/multiple-profile-resource-b");
        when(mockHyperResource.getProfiles()).thenReturn(profiles);

        HyperRequestProcessor processor = new HyperRequestProcessor(new RootResourceBuilder(mockHyperClient).resourceRegistry(this.resourceRegistry));
        BaseProfileResource result = processor.processResource(BaseProfileResource.class, mockHyperResource, null);

        assertFalse(result instanceof ProfileResource1);
        assertFalse(result instanceof ProfileResource2);
        assertTrue(result instanceof MultipleProfileResource);

    }


    @Test
    public void testProcessResourceWithUnregisteredProfile() {

        //Build up the Hyper resource with embedded item
        String profileNotInRegistry =  "not-in-registry-resource";
        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add(profileNotInRegistry);
        when(mockHyperResource.getProfiles()).thenReturn(profiles);

        HyperRequestProcessor processor = new HyperRequestProcessor(new RootResourceBuilder(mockHyperClient).resourceRegistry(this.resourceRegistry));

        BaseProfileResource result = processor.processResource(BaseProfileResource.class, mockHyperResource, null);

        assertFalse("because the profile is not in the registry, the method return type is respected", result instanceof NotInRegistryResource);
    }
}
