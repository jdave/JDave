/*
 * Copyright 2006 the original author or authors.
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
package jdave.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import jdave.Parallel;
import jdave.ResultsAdapter;
import jdave.SpecVisitorAdapter;
import jdave.Specification;
import jdave.support.Reflection;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Pekka Enberg
 * @author Lasse Koskela
 */
public class SpecRunnerTest {
    private List<String> methods = new ArrayList<String>();
    private SpecRunner runner = new SpecRunner();

    @Test
    public void testShouldNotifyResultsOfAllPublicMethods() throws Exception {
        assertTrue(methods.isEmpty());
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter() {
            @Override
            public void expected(Method method) {
                methods.add(method.getName());
            }
        }));
        Collections.sort(methods);
        assertEquals(5, methods.size());
        assertEquals("anyBehavior", methods.get(0));
        assertEquals("inheritedBehavior", methods.get(1));
        assertEquals("shouldBeEqualToTrue", methods.get(2));
        assertEquals("shouldEqualToFalse", methods.get(3));
        assertEquals("shouldNotBeEqualToTrue", methods.get(4));
    }

    @Test
    public void testShouldInvokeAllSpecificationMethods() throws Exception {
        BooleanSpec.actualCalls.clear();
        BaseSpec.actualCalls.clear();
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()));
        Collections.sort(BooleanSpec.actualCalls);
        assertEquals(Arrays.asList("anyBehavior", "inheritedBehavior"), BaseSpec.actualCalls);
        assertEquals(Arrays.asList("shouldBeEqualToTrue", "shouldEqualToFalse",
                "shouldNotBeEqualToTrue"), BooleanSpec.actualCalls);
    }

    @Test
    public void testShouldNotifyCallbackWhenContextIsStarted() throws Exception {
        SpecVisitorAdapter adapter = new SpecVisitorAdapter(new ResultsAdapter());
        runner.run(BooleanSpec.class, adapter);
        assertEquals(Arrays.asList("CommonContext", "FalseBoolean", "TrueBoolean"), adapter
                .getContextNames());
    }

    @Test
    public void testShouldSkipIgnoredContexts() throws Exception {
        SpecVisitorAdapter adapter = new SpecVisitorAdapter(new ResultsAdapter());
        runner.run(SpecWithIgnoredContexts.class, adapter);
        assertFalse(adapter.getContextNames().contains("ContextWithDefaultVisibility"));
        assertFalse(adapter.getContextNames().contains("ContextWithJUnit4IgnoreAnnotation"));
        assertTrue(adapter.getContextNames().contains("TheOnlyProperContext"));
    }

    @Test
    public void testShouldNotifyCallbackWhenContextHasFinished() throws Exception {
        SpecVisitorAdapter adapter = new SpecVisitorAdapter(new ResultsAdapter());
        runner.run(BooleanSpec.class, adapter);
        assertEquals(Arrays.asList("CommonContext", "FalseBoolean", "TrueBoolean"), adapter
                .getFinishedContextNames());
    }

    @Test
    public void testShouldCallDestroyForEachMethod() throws Exception {
        BooleanSpec.destroyCalled = 0;
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(4, BooleanSpec.destroyCalled);
    }

    @Test
    public void testShouldCallSpecCreateForEachMethod() throws Exception {
        BooleanSpec.specCreateCalled = 0;
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(5, BooleanSpec.specCreateCalled);
    }

    @Test
    public void testShouldCallSpecDestroyForEachMethod() throws Exception {
        BooleanSpec.specDestroyCalled = 0;
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(5, BooleanSpec.specDestroyCalled);
    }

    @Test
    public void testShouldCallSpecDestroyEvenIfContextDestroyCrashes() {
        SpecWithCrashingContextDestroy.specDestroyCalled = 0;
        runner.run(SpecWithCrashingContextDestroy.class, new SpecVisitorAdapter(
                new ResultsAdapter()));
        assertEquals(1, SpecWithCrashingContextDestroy.specDestroyCalled);
    }

    @Test
    public void testShouldCallOnceBeforeExactlyOnce() throws Exception {
        BooleanSpec.onceBeforeCalled = 0;
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(1, BooleanSpec.onceBeforeCalled);
    }

    @Test
    public void testShouldCallOnceAfterExactlyOnce() throws Exception {
        BooleanSpec.onceAfterCalled = 0;
        runner.run(BooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(1, BooleanSpec.onceAfterCalled);
    }

    @Test
    public void testShouldCallOnceAfterEvenIfContextDestroyCrashes() {
        SpecWithCrashingContextDestroy.onceAfterCalled = 0;
        runner.run(SpecWithCrashingContextDestroy.class, new SpecVisitorAdapter(
                new ResultsAdapter()));
        assertEquals(1, SpecWithCrashingContextDestroy.onceAfterCalled);
    }

    @Test
    public void testReportsErrorIfSpecificationConstructionFails() {
        final List<Method> errors = new ArrayList<Method>();
        runner.run(FailingSpec.class, new SpecVisitorAdapter(new ResultsAdapter() {
            @Override
            public void error(Method method, Throwable t) {
                errors.add(method);
            }
        }));
        assertEquals(1, errors.size());
        assertEquals("someBehavior", errors.get(0).getName());
    }

    @Test
    public void testAllowsContextHavingCreateMethodWithVoidReturnValue() throws Exception {
        SpecForOtherCreations.whenCreateIsVoid = 0;
        runner.run(SpecForOtherCreations.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(1, SpecForOtherCreations.whenCreateIsVoid);
    }

    @Test
    public void testAllowsContextWithoutCreateMethod() throws Exception {
        SpecForOtherCreations.whenCreateDoesNotExist = 0;
        runner.run(SpecForOtherCreations.class, new SpecVisitorAdapter(new ResultsAdapter()));
        assertEquals(1, SpecForOtherCreations.whenCreateDoesNotExist);
    }

    @Test
    public void testShouldSupportParallelExecutor() {
        BooleanSpec.actualCalls.clear();
        BaseSpec.actualCalls.clear();
        runner.run(ParallelBooleanSpec.class, new SpecVisitorAdapter(new ResultsAdapter()) {
            @Override
            public void onContext(Context context) {
                Exception stack = new Exception();
                List<StackTraceElement> stackList = Arrays.asList(stack.getStackTrace());
                if (!stackList.toString().contains("java.util.concurrent.FutureTask")) {
                    Assert.fail("Not executed by ParallelExecutor (ie. as FutureTask): "
                            + stackList.toString());
                }
            }
        });
        Collections.sort(BaseSpec.actualCalls);
        Collections.sort(BooleanSpec.actualCalls);
        assertEquals(Arrays.asList("anyBehavior", "inheritedBehavior"), BaseSpec.actualCalls);
        assertEquals(Arrays.asList("shouldBeEqualToTrue", "shouldEqualToFalse",
                "shouldNotBeEqualToTrue"), BooleanSpec.actualCalls);
    }

    @Test
    public void testParallelExecutionsReallyRunsBehavioursInParallel() throws Exception {
        Parallel ann = Reflection.getAnnotation(ParallelBehavioursSpec.class, Parallel.class);
        assertSpecHasParallelization(ParallelBehavioursSpec.class, ann.behaviourThreads());
    }

    @Test
    public void testParallelExecutionsReallyRunsContextsInParallel() throws Exception {
        Parallel ann = Reflection.getAnnotation(ParallelBehavioursSpec.class, Parallel.class);
        assertSpecHasParallelization(ParallelContextsSpec.class, ann.contextThreads());
    }

    protected void assertSpecHasParallelization(Class<? extends Specification<?>> specType, int cycleCount) 
            throws Exception {
        final CyclicBarrier behaviourBarrier = new CyclicBarrier(cycleCount);
        final AtomicReference<Exception> parallelizationProblem = new AtomicReference<Exception>();
        runner.runContexts(specType, new ISpecVisitor() {
            public void onContext(Context context) { }
            public void onBehavior(Behavior behavior) {
                try {
                    behaviourBarrier.await(200, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    parallelizationProblem.set(e);
                }
            }
            public void afterContext(Context context) { }
        });
        if (parallelizationProblem.get()!=null) {
            throw parallelizationProblem.get();
        }
    }

    public static class BaseSpec extends Specification<Boolean> {
        public static List<String> actualCalls = new ArrayList<String>();

        public class CommonContext {
            public void anyBehavior() {
                actualCalls.add("anyBehavior");
            }
        }

        public abstract class BaseContext {
            public void inheritedBehavior() {
                actualCalls.add("inheritedBehavior");
            }
        }
    }

    public static class BooleanSpec extends BaseSpec {
        public static List<String> actualCalls = new ArrayList<String>();
        public static int destroyCalled;
        public static int specCreateCalled;
        public static int specDestroyCalled;
        public static int onceBeforeCalled;
        public static int onceAfterCalled;

        public static void onceBefore() {
            onceBeforeCalled++;
        }

        public static void onceAfter() {
            onceAfterCalled++;
        }

        public class FalseBoolean extends BaseContext {
            public Boolean create() {
                return false;
            }

            public void shouldEqualToFalse() {
                actualCalls.add("shouldEqualToFalse");
            }

            public void shouldNotBeEqualToTrue() {
                actualCalls.add("shouldNotBeEqualToTrue");
            }

            protected void protectedMethod() {
                actualCalls.add("protectedMethod");
            }

            @SuppressWarnings("unused")
            private void privateMethod() {
                actualCalls.add("privateMethod");
            }

            void packageProtectedMethod() {
                actualCalls.add("packageProtectedMethod");
            }

            public void methodWhichTakesParameters(Object obj) {
                actualCalls.add("methodWhichTakesParameters");
            }

            @Ignore(value = "this should not be called")
            public void ignoredMethod() {
                actualCalls.add("ignoredMethod");
            }

            public void destroy() {
                destroyCalled++;
            }
        }

        public class TrueBoolean {
            public Boolean create() {
                return true;
            }

            public void shouldBeEqualToTrue() {
                actualCalls.add("shouldBeEqualToTrue");
            }

            public void destroy() {
                destroyCalled++;
            }
        }

        public class ContextWithoutBehaviors {
        }

        public abstract class SomeAbstractClass {
        }

        public static class SomeHelperClass {
            public void thisIsNotBehavior() {
            }
        }

        @SuppressWarnings("unused")
        private class SomePrivateClass {
        }

        class SomePackageProtectedClass {
        }

        protected class SomeProtectedClass {
        }

        @Override
        public void create() {
            specCreateCalled++;
        }

        @Override
        public void destroy() {
            specDestroyCalled++;
        }
    }

    public static class SpecForOtherCreations extends Specification<Void> {
        public static int whenCreateIsVoid = 0;
        public static int whenCreateDoesNotExist = 0;

        public class ContextWithVoidCreateMethod {
            public void create() {
            }

            public void whenCreateIsVoid() {
                whenCreateIsVoid++;
            }
        }

        public class ContextWithoutCreateMethod {
            public void whenCreateDoesNotExist() {
                whenCreateDoesNotExist++;
            }
        }
    }

    public class FailingSpec extends Specification<Void> {
        public FailingSpec() {
            throw new RuntimeException();
        }

        public class Context {
            public void create() {
            }

            public void someBehavior() {
            }
        }
    }

    public static class SpecWithCrashingContextDestroy extends Specification<Void> {
        public static int onceAfterCalled;
        private static int specDestroyCalled;

        public static void onceAfter() {
            onceAfterCalled++;
        }

        public class Context {
            public void destroy() {
                throw new RuntimeException("Exception from context destroy");
            }

            public void someBehavior() {
            }
        }

        @Override
        public void destroy() throws Exception {
            specDestroyCalled++;
        }
    }

    public static class SpecWithIgnoredContexts extends Specification<Void> {
        public static List<String> executedContexts = new ArrayList<String>();

        public abstract class ContextThatShouldBeIgnored {
            public void someBehavior() {
                executedContexts.add(getClass().getSimpleName());
            }
        }

        class ContextWithDefaultVisibility extends ContextThatShouldBeIgnored {
        }

        @Ignore
        public class ContextWithJUnit4IgnoreAnnotation extends ContextThatShouldBeIgnored {
        }

        public class TheOnlyProperContext {
            public void someBehavior() {
            }
        }
    }

    @Parallel(contextThreads=2, behaviourThreads=5)
    public static class ParallelBooleanSpec extends BooleanSpec {
    }

    @Parallel(contextThreads=1, behaviourThreads=3)
    public static class ParallelBehavioursSpec extends Specification<Void> {
        public class Context {
            public void dummyBehaviour1() { }
            public void dummyBehaviour2() { }
            public void dummyBehaviour3() { }
        }
    }

    @Parallel(contextThreads=3, behaviourThreads=1)
    public static class ParallelContextsSpec extends Specification<Void> {
        public class ParallelContext1 {
            public void dummyBehaviour() { }
        }
        public class ParallelContext2 {
            public void dummyBehaviour() { }
        }
        public class ParallelContext3 {
            public void dummyBehaviour() { }
        }
    }

}