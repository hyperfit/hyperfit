package org.hyperfit;

import org.hyperfit.java8.Java8DefaultMethodHandler;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(ConsoleSpammingMockitoJUnitRunner.class)
public class Java8HyperResourceInvokeHandlerTest {

    @Mock
    protected Java8Resource mockHyperResource;


    protected Java8Resource concretehyperResource;


    @Mock
    private HyperfitProcessor mockHyperfitProcessor;

    private ResourceMethodInfoCache resourceMethodInfoCache;

    <T> T getHyperResourceProxy(Class<T> clazz, HyperResource resource) {
        return clazz.cast(
            Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new HyperResourceInvokeHandler(
                    resource,
                    mockHyperfitProcessor,
                    resourceMethodInfoCache.get(clazz),
                    null,
                    new Java8DefaultMethodHandler()
                )
            )
        );
    }

    @After
    public void after() {
        Mockito.reset(mockHyperfitProcessor, mockHyperResource);
    }

    @Before
    public void before() {
        // Mockito cannot call through to default methods without a concrete implementation.
        concretehyperResource = new Java8Resource() {
            @Override
            public Integer getSomeData() {
                throw new NotImplementedException();
            }

            @Override
            public HyperLink[] getLinks() {
                return new HyperLink[0];
            }

            @Override
            public HyperLink[] getLinks(String relationship) {
                return new HyperLink[0];
            }

            @Override
            public HyperLink[] getLinks(String relationship, String name) {
                return new HyperLink[0];
            }

            @Override
            public HyperLink getLink(String relationship) {
                return null;
            }

            @Override
            public HyperLink getLink(String relationship, String name) {
                return null;
            }

            @Override
            public <T> T getPathAs(Class<T> classToReturn, String... path) {
                return null;
            }

            @Override
            public <T> T getPathAs(Class<T> classToReturn, boolean nullWhenMissing, String... path) {
                return null;
            }

            @Override
            public boolean hasPath(String... path) {
                return false;
            }

            @Override
            public boolean canResolveLinkLocal(String relationship) {
                return false;
            }

            @Override
            public HyperResource resolveLinkLocal(String relationship) {
                return null;
            }

            @Override
            public HyperResource[] resolveLinksLocal(String relationship) {
                return new HyperResource[0];
            }

            @Override
            public boolean hasLink(String relationship) {
                return false;
            }

            @Override
            public boolean hasLink(String relationship, String name) {
                return false;
            }

            @Override
            public boolean isMultiLink(String relationship) {
                return false;
            }

            @Override
            public LinkedHashSet<String> getProfiles() {
                return null;
            }

            @Override
            public Form getForm(String formName) {
                return null;
            }

            @Override
            public boolean hasForm(String formName) {
                return false;
            }

            @Override
            public Form[] getForms() {
                return new Form[0];
            }
        };
        resourceMethodInfoCache = new ConcurrentHashMapResourceMethodInfoCache();
    }

    @Test
    public void defaultMethodsInvoked() {
        Java8Resource resource = getHyperResourceProxy(Java8Resource.class, concretehyperResource);

        assertThat(resource.imADefaultMethod(), equalTo("ok sure"));
    }

    @Test
    public void getPathAsStillWorks() {
        when(mockHyperResource.getPathAs(Integer.class, false, "someData"))
                .thenReturn(100);
        Java8Resource resource = getHyperResourceProxy(Java8Resource.class, mockHyperResource);
        Integer result = resource.getSomeData();
        verify(mockHyperResource).getPathAs(Integer.class, false, "someData");
        assertThat(result, equalTo(100));
    }
}
