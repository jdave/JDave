/*
 * Copyright 2011 the original author or authors.
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

import java.lang.reflect.Method;
import jdave.Parallel;
import jdave.Specification;
import jdave.support.ParallelExecutor;
import jdave.support.Reflection;
import junit.framework.Assert;
import org.junit.Test;


/**
 * @author Kim Blomqvist
 */
public class ContextTest {

    @Test
    public void testUnannotatedContextExecutedUsingSerialExecutor() {
        Context context = new Context(SpecRunnerTest.BaseSpec.class, SpecRunnerTest.BaseSpec.CommonContext.class) {
            @Override
            protected Behavior newBehavior(Method method, Class<? extends Specification<?>> specType, Class<?> contextType) {
                return null;
            }
        };
        Assert.assertEquals(SerialSpecExecutor.class, context.getExecutor().getClass());
    }

    @Test
    public void testParallelAnnotatedContextExecutedUsingParallelExecutor() {
        Context context = new Context(SpecWithParallelContext.class, SpecWithParallelContext.TheParallelContext.class) {
            @Override
            protected Behavior newBehavior(Method method, Class<? extends Specification<?>> specType, Class<?> contextType) {
                return null;
            }
        };
        Assert.assertEquals(ParallelExecutor.class, context.getExecutor().getClass());
    }

    @Test
    public void testContextWillCacheExecutor() {
        Context context = new Context(null, null) {
            @Override
            protected Behavior newBehavior(Method method, Class<? extends Specification<?>> specType, Class<?> contextType) {
                return null;
            }
        };
        SerialSpecExecutor cachedExecutor = new SerialSpecExecutor();
        Reflection.setPrivateField(context, "executor", cachedExecutor);
        Assert.assertEquals(cachedExecutor, context.getExecutor());
    }

    @Parallel
    public static class SpecWithParallelContext extends Specification<Object> {
        public class TheParallelContext {
            public void someMethod() {
            }
        }
    }

}
