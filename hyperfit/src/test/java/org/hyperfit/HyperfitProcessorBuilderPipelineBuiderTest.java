package org.hyperfit;


import org.hamcrest.Matchers;
import org.hyperfit.net.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

public class HyperfitProcessorBuilderPipelineBuiderTest {


    class FakeStep implements Pipeline.Step<Request, Response> {

        public Response run(Request input, Pipeline<Request, Response> pipeline) {
            return null;
        }
    }


    @Mock
    HyperfitProcessor.Builder mockBuilder;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAdd(){
        HyperfitProcessor.Builder.PipelineBuilder<Request, Response> subject = new HyperfitProcessor.Builder.PipelineBuilder<Request, Response>(mockBuilder);

        FakeStep step1 = new FakeStep();
        FakeStep step2 = new FakeStep();

        subject.addSteps(
            step1,
            step2
        );

        assertThat(
            subject.steps,
            Matchers.<Pipeline.Step<Request, Response>>contains(
                step1,
                step2
            )
        );
    }


    @Test
    public void testRemoveInstance(){
        HyperfitProcessor.Builder.PipelineBuilder<Request, Response> subject = new HyperfitProcessor.Builder.PipelineBuilder<Request, Response>(mockBuilder);

        FakeStep step1 = new FakeStep();
        FakeStep step2 = new FakeStep();
        Pipeline.Step<Request, Response> step3 = new Pipeline.Step<Request, Response>() {
            public Response run(Request input, Pipeline<Request, Response> pipeline) {
                return null;
            }
        };

        subject.addSteps(
            step1,
            step2,
            step3
        );

        assertThat(
            subject.steps,
            Matchers.<Pipeline.Step<Request, Response>>contains(
                step1,
                step2,
                step3
            )
        );


        subject.removeSteps(
            FakeStep.class
        );

        assertThat(
            subject.steps,
            Matchers.<Pipeline.Step<Request, Response>>contains(
                step3
            )
        );
    }


    @Test
    public void testRemoveClass(){
        HyperfitProcessor.Builder.PipelineBuilder<Request, Response> subject = new HyperfitProcessor.Builder.PipelineBuilder<Request, Response>(mockBuilder);

        FakeStep step1 = new FakeStep();
        FakeStep step2 = new FakeStep();
        FakeStep step3 = new FakeStep();

        subject.addSteps(
            step1,
            step2,
            step3
        );

        assertThat(
            subject.steps,
            Matchers.<Pipeline.Step<Request, Response>>contains(
                step1,
                step2,
                step3
            )
        );


        subject.removeSteps(
            step3,
            step1
        );

        assertThat(
            subject.steps,
            Matchers.<Pipeline.Step<Request, Response>>contains(
                step2
            )
        );

    }


    @Test
    public void testDone(){
        HyperfitProcessor.Builder.PipelineBuilder<Request, Response> subject = new HyperfitProcessor.Builder.PipelineBuilder<Request, Response>(mockBuilder);

        assertSame(
            mockBuilder,
            subject.done()
        );
    }


}
