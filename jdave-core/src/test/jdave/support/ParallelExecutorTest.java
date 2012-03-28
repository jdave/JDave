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
package jdave.support;

import java.lang.reflect.Field;
import jdave.support.ParallelExecutor.ThreadedExecutorService;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author Kim Blomqvist
 */
public class ParallelExecutorTest {

    @Test
    public void testParallelExecutor() throws Exception {
        ParallelExecutor executor = new ParallelExecutor(2);
        Assert.assertEquals(2, getExecutorService(executor).executorThreads);
        executor = new ParallelExecutor(3);
        Assert.assertEquals(3, getExecutorService(executor).executorThreads);
    }

    // schedule() and getResults() methods tested by SpecRunnerTest

    @SuppressWarnings("unchecked")
    protected ThreadedExecutorService getExecutorService(ParallelExecutor executor)
            throws NoSuchFieldException, IllegalAccessException {
        Field currentExecutorField = ParallelExecutor.class.getDeclaredField("currentExecutor");
        currentExecutorField.setAccessible(true);
        ThreadLocal<ThreadedExecutorService> currentExecutor =
                (ThreadLocal<ThreadedExecutorService>) currentExecutorField.get(null);
        ThreadedExecutorService threadedExecutorService = currentExecutor.get();
        return threadedExecutorService;
    }

}
