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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import jdave.runner.ISpecExecutor;

/**
 * @author Kim Blomqvist
 */
public class ParallelExecutor implements ISpecExecutor {

    public static class ThreadedExecutorService {
        ExecutorService executor;
        int executorThreads;
        public ThreadedExecutorService(int executorThreads) {
            super();
            this.executorThreads = executorThreads;
            this.executor = Executors.newFixedThreadPool(executorThreads);
        }
    }

    // For performance, cache ExecutorService as long as parameters don't change
    protected static final ThreadLocal<ThreadedExecutorService> currentExecutor = new ThreadLocal<ThreadedExecutorService>();

    public ParallelExecutor(final int threads) {
        ThreadedExecutorService current = currentExecutor.get();
        if (current == null || current.executorThreads != threads) {
            if (current != null) {
                current.executor.shutdown();
            }
            currentExecutor.set(new ThreadedExecutorService(threads));
        }
    }

    private final List<Future<Void>> contextFutureResults = new ArrayList<Future<Void>>();

    public void schedule(final Callable<Void> contextCallable) {
        contextFutureResults.add(currentExecutor.get().executor.submit(contextCallable));
    }

    public void getResults() {
        for (Future<Void> each : contextFutureResults) {
            try {
                each.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
