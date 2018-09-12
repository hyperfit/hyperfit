package org.hyperfit;

public interface Pipeline<I,O> {

    interface Step<I,O> {
        O run(I request, Pipeline<I,O> pipeline);
    }

    O run(I request);
}
