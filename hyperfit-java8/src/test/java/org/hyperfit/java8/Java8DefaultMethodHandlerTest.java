package org.hyperfit.java8;


import org.hyperfit.HyperResourceInvokeHandler;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.methodinfo.ConcurrentHashMapMethodInfoCache;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;


public class Java8DefaultMethodHandlerTest {

    @Rule
    public ExpectedException ee = ExpectedException.none();

    Java8DefaultMethodHandler invoker;

    //We put the abstract method here to test teh more advanced inheritance scenario
    public interface InterfaceWithMethod extends HyperResource {
        Integer imAnAbstractMethod();
    }

    public interface InterfaceWithDefaultMethod extends InterfaceWithMethod {


        default String someString() {
            return "imastring";
        }

        default String iCallADefaultMethod() {
            return "called " + someString();
        }

        default Integer iCallAnAbstractMethod() {
            return this.imAnAbstractMethod() + 1;
        }

        default Integer iCallADefaultThatCallsAnAbstractMethod() {
            return this.iCallAnAbstractMethod() + 1;
        }
    }

    public class FakeHyperResourceWithDefaultMethods implements InterfaceWithDefaultMethod {

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
        public String[] getDataFieldNames() {
            return new String[0];
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
    public void fallbackToResourceWhenCallingAbstractMethodsFromInsideADefaultMethod() throws Exception {
        FakeHyperResourceWithDefaultMethods fake = new FakeHyperResourceWithDefaultMethods();

        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                fake,
                InterfaceWithDefaultMethod.class.getMethod("iCallAnAbstractMethod")
            ),
            null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo(1));

    }


    @Test
    public void fallbackToResourceWhenCallingAbstractMethodsFromInsideADefaultMethodInsideADefaultMethod() throws Exception {

        FakeHyperResourceWithDefaultMethods fake = new FakeHyperResourceWithDefaultMethods();

        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                fake,
                InterfaceWithDefaultMethod.class.getMethod("iCallADefaultThatCallsAnAbstractMethod")
            ),
            null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo(2));

    }


    @Test
    public void fallbackToProxyResourceWhenCallingAbstractMethodsFromInsideADefaultMethod() throws Exception {
        InterfaceWithDefaultMethod fake = (InterfaceWithDefaultMethod)Proxy.newProxyInstance(
            InterfaceWithDefaultMethod.class.getClassLoader(),
            new Class[]{InterfaceWithDefaultMethod.class},
            (proxy, method, params)->{

                if(InterfaceWithDefaultMethod.class.getMethod("imAnAbstractMethod").equals(method)){
                    return 0;
                }

                throw new RuntimeException("expected call not made");

            }
        );



        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                fake,
                InterfaceWithDefaultMethod.class.getMethod("iCallAnAbstractMethod")
            ),
            null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo(1));

    }


    @Test
    public void fallbackToProxyResourceWhenCallingAbstractMethodsFromInsideADefaultMethodInsideADefaultMethod() throws Exception {

        InterfaceWithDefaultMethod fake = (InterfaceWithDefaultMethod)Proxy.newProxyInstance(
            InterfaceWithDefaultMethod.class.getClassLoader(),
            new Class[]{InterfaceWithDefaultMethod.class},
            new HyperResourceInvokeHandler(
                new FakeHyperResourceWithDefaultMethods(),
                null,
                new ConcurrentHashMapMethodInfoCache(),
                null,
                new Java8DefaultMethodHandler()
            ){

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                    if(method.equals(InterfaceWithDefaultMethod.class.getMethod("imAnAbstractMethod"))){

                        return 0;
                    }

                    return super.invoke(proxy, method, args);
                }
            }

        );

        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                fake,
                InterfaceWithDefaultMethod.class.getMethod("iCallADefaultThatCallsAnAbstractMethod")
            ),
            null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo(2));

    }


    @Test
    public void testDefaultToDefault() throws Exception {

        FakeHyperResourceWithDefaultMethods fake = new FakeHyperResourceWithDefaultMethods();

        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                fake,
                InterfaceWithDefaultMethod.class.getMethod("iCallADefaultMethod")
            ),
            null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("called imastring"));

    }

    @Test
    public void invoke() throws Exception {
        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                new FakeHyperResourceWithDefaultMethods(),
                InterfaceWithDefaultMethod.class.getMethod("someString")
            ),
        null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("imastring"));

    }

    @Test
    public void invoke_withNoValidInterfaces() throws Exception {
        ee.expect(HyperfitException.class);
        ee.expectMessage("No interface in [interface org.hyperfit.resource.HyperResource] has default method public default java.lang.String org.hyperfit.java8.Java8DefaultMethodHandlerTest$InterfaceWithDefaultMethod.someString()");
        new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
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
                    public String[] getDataFieldNames() {
                        return new String[0];
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
            ),
            null
        );
    }



    @Test
    public void choosesTheCorrectInterface() throws NoSuchMethodException {

        Object val = new Java8DefaultMethodHandler().invoke(
            new org.hyperfit.handlers.Java8DefaultMethodHandler.DefaultMethodContext(
                new FakeHyperResourceWithDefaultMethods(),
                InterfaceWithDefaultMethod.class.getMethod("someString")
            ),
            null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("imastring"));

    }
}
