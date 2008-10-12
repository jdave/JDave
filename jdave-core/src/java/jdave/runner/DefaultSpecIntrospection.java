/*
 * Copyright 2007 the original author or authors.
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
import java.lang.reflect.Modifier;

/**
 * @author Joni Freeman
 */
public class DefaultSpecIntrospection implements ISpecIntrospection {
    public static final String INITIALIZER_NAME = "create";
    static final String DISPOSER_NAME = "destroy";

    public boolean isBehavior(Method method) {
        if (method.getDeclaringClass().equals(Object.class)) {
            return false;
        }
        if (method.getName().equals(INITIALIZER_NAME)) {
            return false;
        }
        if (method.getName().equals(DISPOSER_NAME)) {
            return false;
        }
        if (method.isSynthetic()) {
            return false;
        }
        if (method.getParameterTypes().length > 0) {
            return false;
        }
        return true;
    }

    public boolean isContextClass(Class<?> specType, Class<?> possibleContext) {
        if (!isConcreteAndPublic(possibleContext)) {
            return false;
        }
        if (!hasBehaviors(possibleContext)) {
            return false;
        }
        return isInnerClass(specType, possibleContext);
    }
    
    private boolean hasBehaviors(Class<?> possibleContext) {
        for (Method method : possibleContext.getMethods()) {
            if (isBehavior(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean isConcreteAndPublic(Class<?> possibleContext) {
        int mod = possibleContext.getModifiers();
        return (Modifier.isPublic(mod) && !Modifier.isAbstract(mod));
    }

    private boolean isInnerClass(Class<?> specType, Class<?> possibleContext) {
        return possibleContext.getEnclosingClass() != null;
    }
}
