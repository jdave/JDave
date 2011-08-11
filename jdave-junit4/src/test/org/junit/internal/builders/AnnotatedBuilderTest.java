/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.junit.internal.builders;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class AnnotatedBuilderTest {
    @Test
    public void aClassWithARunWithAnnotationHasRunnerBuilt() throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(ClassWithRunWithAnnotation.class);
        assertThat(runner, is(FakeRunner.class));
    }

    @Test
    public void aClassWithARunWithAnnotationThatRequiresASuiteBuilderHasRunnerBuilt()
            throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(ClassWithRunWithAnnotationWhereTheRunnerRequiresASuiteBuilder.class);
        assertThat(runner, is(FakeRunnerThatRequiresSuiteBuilder.class));

    }

    @Test
    public void aClassWithoutARunWithAnnotationReturnsNull() throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(ClassWithoutRunWithAnnotation.class);
        assertThat(runner, is(IsNull.<Runner> nullValue()));

    }

    public void aNonStaticInnerClassWithARunWithAnnotationOnTheDeclaringClassHasRunnerBuilt()
            throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(DeclaringClassWithRunWithAnnotation.NonStaticInnerClass.class);
        assertThat(runner, is(FakeRunner.class));
    }

    @Test
    public void aStaticInnerClassWithARunWithAnnotationHasRunnerBuilt() throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(DeclaringClassWithRunWithAnnotation.StaticInnerClassWithRunWithAnnotation.class);
        assertThat(runner, is(FakeRunner.class));
    }

    @Test
    public void aStaticInnerClassWithARunWithAnnotationOnTheDeclaringClassReturnsNull()
            throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(DeclaringClassWithRunWithAnnotation.StaticInnerClass.class);
        assertThat(runner, is(IsNull.<Runner> nullValue()));
    }

    @Test(expected = InitializationError.class)
    public void aClassWithARunWithAnnotationThatSpecifiesAnInvalidRunnerThrowsException()
            throws Exception {
        final Runner runner = new AnnotatedBuilder(null)
                .runnerForClass(ClassWithRunWithAnnotationThatSpecifiesAndInvalidRunner.class);
        assertThat(runner, is(IsNull.<Runner> nullValue()));
    }

    @RunWith(FakeRunner.class)
    private static class ClassWithRunWithAnnotation {
    }

    @RunWith(InvalidRunner.class)
    private static class ClassWithRunWithAnnotationThatSpecifiesAndInvalidRunner {
    }

    @RunWith(FakeRunnerThatRequiresSuiteBuilder.class)
    private static class ClassWithRunWithAnnotationWhereTheRunnerRequiresASuiteBuilder {
    }

    private static class ClassWithoutRunWithAnnotation {
    }

    @RunWith(FakeRunner.class)
    private static class DeclaringClassWithRunWithAnnotation {

        public class NonStaticInnerClass {
        }

        public static class StaticInnerClass {
        }

        @RunWith(FakeRunner.class)
        public static class StaticInnerClassWithRunWithAnnotation {
        }

    }

    public static class InvalidRunner extends Runner {
        @Override
        public Description getDescription() {
            return Description.EMPTY;
        }

        @Override
        public void run(final RunNotifier notifier) {
        }
    }

    public static class FakeRunner extends Runner {
        public FakeRunner(
        final Class<?> testClass) {
        }

        @Override
        public Description getDescription() {
            return Description.EMPTY;
        }

        @Override
        public void run(final RunNotifier notifier) {
        }
    }

    public static class FakeRunnerThatRequiresSuiteBuilder extends Runner {
        public FakeRunnerThatRequiresSuiteBuilder(final Class<?> testClass, final RunnerBuilder suiteBilder) {
        }

        @Override
        public Description getDescription() {
            return Description.EMPTY;
        }

        @Override
        public void run(final RunNotifier notifier) {
        }
    }
}
