package org.hyperfit;

import org.hyperfit.annotation.Data;
import org.hyperfit.resource.HyperResource;

/**
 * Created by btilford on 1/13/17.
 */
public interface Jdk8Resource extends HyperResource {


    @Data("someData")
    Integer getSomeData();

    default String imADefaultMethod() {
        return "ok sure";
    }
}
