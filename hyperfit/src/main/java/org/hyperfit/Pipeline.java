package org.hyperfit;

public interface Pipeline<I,O> {

    interface Step<I,O> {
        O run(I input, Pipeline<I,O> pipeline);
    }

    O run(I input);

    HyperfitProcessor getProcessor();
}
