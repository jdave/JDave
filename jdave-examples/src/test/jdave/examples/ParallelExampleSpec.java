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
package jdave.examples;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import jdave.Parallel;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

/**
 * Example testing that both contexts and behaviors are run in parallel.
 * @author Kim Blomqvist
 */
@RunWith(JDaveRunner.class)
@Parallel(contextThreads=2, behaviourThreads=2)
public class ParallelExampleSpec extends Specification<Void> {
    static CyclicBarrier contextBarrier = new CyclicBarrier(2);
    static CyclicBarrier behaviourBarrier = new CyclicBarrier(2);

    public class AContext {
        public void aBehavior() throws Exception {
            // delays long enough for the tests not to fail even on very slow machines
            contextBarrier.await(200, TimeUnit.MILLISECONDS);
            behaviourBarrier.await(200, TimeUnit.MILLISECONDS);
        }
        public void bBehavior() throws Exception {
            behaviourBarrier.await(500, TimeUnit.MILLISECONDS);
        }
    }
    public class BContext {
        public void cBehavior() throws Exception {
            contextBarrier.await(500, TimeUnit.MILLISECONDS);
        }
    }
}
