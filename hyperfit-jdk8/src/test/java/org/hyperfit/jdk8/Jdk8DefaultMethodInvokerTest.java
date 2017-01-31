package org.hyperfit.jdk8;

import org.hyperfit.DefaultMethodInvoker;
import org.hyperfit.HyperResourceInvokeHandler;
import org.hyperfit.HyperfitProcessor;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.methodinfo.ConcurrentHashMapMethodInfoCache;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by btilford on 1/13/17.
 */
public class Jdk8DefaultMethodInvokerTest {

    @Rule
    public ExpectedException ee = ExpectedException.none();

    Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod> invoker;

    public interface InterfaceWithDefaultMethod extends HyperResource {
        Integer imAnAbstractMethod();

        default public String someString() {
            return "imastring";
        }

        default Integer iCallAnAbstractMethod() {
            return this.imAnAbstractMethod() + 1;
        }
    }

    public class MockHyperResource implements InterfaceWithDefaultMethod {

        @Override
        public Integer imAnAbstractMethod() {
            return 0;
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
    }

    @Test
    public void fallbackToHyperProxyWhenCallingAbstrctMethodsFromInsideADefaultMethod() throws Exception {
        Method method = InterfaceWithDefaultMethod.class.getMethod("iCallAnAbstractMethod");
        MockHyperResource mock = new MockHyperResource();
        HyperResourceInvokeHandler handler = Mockito.mock(HyperResourceInvokeHandler.class);
        when(
                handler.invoke(
                        eq(mock),
                        eq(InterfaceWithDefaultMethod.class.getMethod("imAnAbstractMethod")),
                        anyVararg())
        ).thenReturn(mock.imAnAbstractMethod());

        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                new DefaultMethodInvoker.DefaultMethodContext<InterfaceWithDefaultMethod>(
                        handler,
                        mock,
                        method
                ), null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo(1));
        verify(handler).invoke(
                eq(mock),
                eq(InterfaceWithDefaultMethod.class.getMethod("imAnAbstractMethod")),
                anyVararg()
        );
    }

    @Test
    public void invoke() throws Exception {
        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                new DefaultMethodInvoker.DefaultMethodContext<InterfaceWithDefaultMethod>(
                        null,
                        new MockHyperResource(),
                        InterfaceWithDefaultMethod.class.getMethod("someString")
                ), null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("imastring"));

    }

    @Test
    public void invoke_withNoValidInterfaces() throws Exception {
        ee.expect(HyperfitException.class);
        ee.expectMessage("No interface in [interface org.hyperfit.resource.HyperResource] has default method public default java.lang.String org.hyperfit.jdk8.Jdk8DefaultMethodInvokerTest$InterfaceWithDefaultMethod.someString()!");
        Object val = new Jdk8DefaultMethodInvoker<>().invoke(
                new DefaultMethodInvoker.DefaultMethodContext<>(
                        null,
                        new HyperResource() {
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
                        },
                        InterfaceWithDefaultMethod.class.getMethod("someString")
                ), null
        );
    }

    @Test
    public void invoke_withEmptyInterfaceArray() throws Exception {
        ee.expect(HyperfitException.class);

        Object val = new Jdk8DefaultMethodInvoker<>().invoke(
                new DefaultMethodInvoker.DefaultMethodContext<>(
                        null,
                        new HyperResource() {
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
                        },
                        InterfaceWithDefaultMethod.class.getMethod("someString")
                ), null
        );
    }


    @Test
    public void choosesTheCorrectInterface() throws NoSuchMethodException {

        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                new DefaultMethodInvoker.DefaultMethodContext<InterfaceWithDefaultMethod>(
                        null,
                        new MockHyperResource(),
                        InterfaceWithDefaultMethod.class.getMethod("someString")
                ),
                null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("imastring"));

    }
}
