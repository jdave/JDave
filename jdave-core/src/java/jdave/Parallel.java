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
package jdave;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark specification contexts suitable for parallel execution.
 * <p/>
 * To disable either of context or behaviour parallelization, set the corresponding
 * of parameter to <code>1</code>.
 * <p/>
 * The maximum number of active threads on executing behaviours is thus:
 * <code>contextThreads * behaviourThreads</code>.
 * @author Kim Blomqvist
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Parallel {

    public static int defaultContextThreads = 1;
    public static int defaultBehaviourThreads = 2;

    /**
     * Number of threads to use for the running of <b>Contexts</b> of a specification.
     * @return defaults to 4.
     */
    int contextThreads() default defaultContextThreads;

    /**
     * Number of threads to use for the running of <b>Behaviours</b> of each Context. Note:
     * Each context of a Specification gets its own set of behaviour executing threads.
     * @return the number of threads executing behaviours of each context. Defaults to 8.
     */
    int behaviourThreads() default defaultBehaviourThreads;

}
