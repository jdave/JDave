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

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import jdave.Parallel;
import jdave.Specification;
import jdave.support.ParallelExecutor;
import jdave.support.Reflection;

/**
 * @author Joni Freeman
 * @author Pekka Enberg
 * @author Kim Blomqvist
 */
public abstract class Context {
    private final Class<? extends Specification<?>> specType;
    private final Class<?> contextType;
    private ISpecIntrospection introspection;
    private ISpecExecutor executor;

    public Context(final Class<? extends Specification<?>> specType, final Class<?> contextType) {
        this.specType = specType;
        this.contextType = contextType;
    }

    public String getName() {
        return contextType.getSimpleName();
    }

    protected abstract Behavior newBehavior(Method method,
            Class<? extends Specification<?>> specType, Class<?> contextType);

    void run(final ISpecVisitor callback) {
        final ISpecExecutor runExecutor = getExecutor();
        for (final Method method : ClassMemberSorter.getMethods(contextType)) {
            if (isBehavior(method)) {
                runExecutor.schedule(new Callable<Void>() {
                    public Void call() throws Exception {
                        callback.onBehavior(newBehavior(method, specType, contextType));
                        return null;
                    }
                });
            }
        }
        runExecutor.getResults();
    }

    private boolean isBehavior(final Method method) {
        return getIntrospection().isBehavior(method);
    }

    public boolean isContextClass() {
        return getIntrospection().isContextClass(specType, contextType);
    }

    synchronized private ISpecIntrospection getIntrospection() {
        if (null != introspection) {
            return introspection;
        }
        IntrospectionStrategy strategy = Reflection.getAnnotation(specType, IntrospectionStrategy.class);
        if (null != strategy) {
            try {
                introspection = strategy.value().newInstance();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            introspection = new DefaultSpecIntrospection();
        }
        return introspection;
    }

    synchronized protected ISpecExecutor getExecutor() {
        if (null != executor) {
            return executor;
        }
        Parallel parallelAnnotation = Reflection.getAnnotation(specType, Parallel.class);
        if (null != parallelAnnotation && parallelAnnotation.behaviourThreads() > 1) {
            executor = new ParallelExecutor(parallelAnnotation.behaviourThreads());
        } else {
            executor = SerialSpecExecutor.getInstance();
        }
        return executor;
    }

}
