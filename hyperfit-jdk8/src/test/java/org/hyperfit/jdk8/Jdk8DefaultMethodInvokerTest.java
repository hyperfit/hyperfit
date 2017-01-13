package org.hyperfit.jdk8;

import org.hyperfit.exception.HyperfitException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by btilford on 1/13/17.
 */
public class Jdk8DefaultMethodInvokerTest {

    @Rule
    public ExpectedException ee = ExpectedException.none();

    Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod> invoker;

    public interface InterfaceWithDefaultMethod {
        default public String someString() {
            return "imastring";
        }
    }


    @Test
    public void invoke() throws Exception {
        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                new Class[]{InterfaceWithDefaultMethod.class},
                InterfaceWithDefaultMethod.class.getMethod("someString"),
                null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("imastring"));

    }

    @Test
    public void invoke_withNoValidInterfaces() throws Exception {
        ee.expect(HyperfitException.class);
        ee.expectMessage("No interface in [interface java.lang.Iterable, interface java.util.List] has default method public default java.lang.String org.hyperfit.jdk8.Jdk8DefaultMethodInvokerTest$InterfaceWithDefaultMethod.someString()!");
        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                new Class[]{Iterable.class, List.class},
                InterfaceWithDefaultMethod.class.getMethod("someString"),
                null
        );
    }

    @Test
    public void invoke_withEmptyInterfaceArray() throws Exception {
        ee.expect(HyperfitException.class);

        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                new Class[]{},
                InterfaceWithDefaultMethod.class.getMethod("someString"),
                null
        );
    }


}
