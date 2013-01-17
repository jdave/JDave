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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import jdave.Specification;
import jdave.support.Reflection;

import org.junit.Ignore;

/**
 * @author Pekka Enberg
 * @author Joni Freeman
 * @author Lasse Koskela
 */
public class SpecRunner {
    public <T extends Specification<?>> void visit(Class<T> specType, ISpecVisitor callback) {
        for (Class<?> contextType : getContextsOf(specType)) {
            Context context = new Context(specType, contextType) {
                @Override
                protected Behavior newBehavior(Method method,
                        Class<? extends Specification<?>> specType, Class<?> contextType) {
                    return new VisitingBehavior(method, contextType);
                }
            };
            run(callback, context);
        }
    }

    public <T extends Specification<?>> void run(Class<T> specType, ISpecVisitor callback) {
        runOnceBefores(specType);
        runContexts(specType, callback);
        runOnceAfters(specType);
    }

    private <T extends Specification<?>> void runOnceBefores(Class<?> specType) {
        if (specType != null) {
          runOnceBefores(specType.getSuperclass());
          runPublicStaticVoidMethodNamed("onceBefore", specType);
        }
    }

    private <T extends Specification<?>> void runOnceAfters(Class<?> specType) {
        if (specType != null) {
          runPublicStaticVoidMethodNamed("onceAfter", specType);
          runOnceAfters(specType.getSuperclass());
        }
    }

    private <T> void runPublicStaticVoidMethodNamed(String name, Class<T> specType) {
        try {
            Method method = Reflection.getMethod(specType, name, Modifier.PUBLIC, Modifier.STATIC);
            try {
                method.invoke(null);
            } catch (Exception e) {
            }
        } catch (NoSuchMethodException isOk) {
        }
    }

    private <T extends Specification<?>> void runContexts(Class<T> specType, ISpecVisitor callback) {
        for (Class<?> contextType : getContextsOf(specType)) {
            Context context = new Context(specType, contextType) {
                @Override
                protected Behavior newBehavior(Method method,
                        Class<? extends Specification<?>> specType, Class<?> contextType) {
                    return new ExecutingBehavior(method, specType, contextType);
                }
            };
            run(callback, context);
        }
    }

    private void run(ISpecVisitor callback, Context context) {
        if (context.isContextClass()) {
            callback.onContext(context);
            context.run(callback);
            callback.afterContext(context);
        }
    }

    private <T> Class<?>[] getContextsOf(Class<T> specType) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> member : ClassMemberSorter.getClasses(specType)) {
            if (qualifiesAsContext(member)) {
                classes.add(member);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private boolean qualifiesAsContext(Class<?> clazz) {
        return annotationIsPresent(clazz, Ignore.class) == false;
    }

    private boolean annotationIsPresent(Class<?> clazz, Class<? extends Annotation> annotationType) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().equals(annotationType)) {
                return true;
            }
        }
        return false;
    }
}
