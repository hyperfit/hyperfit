package org.hyperfit;

import org.hyperfit.annotation.Data;
import org.hyperfit.resource.HyperResource;


public interface Java8Resource extends HyperResource {


    @Data("someData")
    Integer getSomeData();

    default String imADefaultMethod() {
        return "ok sure";
    }
}
