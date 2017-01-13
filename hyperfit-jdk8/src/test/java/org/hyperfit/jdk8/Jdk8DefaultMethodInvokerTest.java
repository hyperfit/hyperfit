package org.hyperfit.jdk8;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by btilford on 1/13/17.
 */
public class Jdk8DefaultMethodInvokerTest {

    Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod> invoker;

    public interface InterfaceWithDefaultMethod {
        default public String someString() {
            return "imastring";
        }
    }


    @Test
    public void invoke() throws Exception {
        Object val = new Jdk8DefaultMethodInvoker<InterfaceWithDefaultMethod>().invoke(
                InterfaceWithDefaultMethod.class,
                InterfaceWithDefaultMethod.class.getMethod("someString"),
                null
        );
        assertThat(val, notNullValue());
        assertThat(val, equalTo("imastring"));

    }

}
