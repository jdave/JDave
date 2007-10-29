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
package jdave.junit4;

import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import jdave.Specification;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class StringComparisonFailureTest extends TestCase {
    private Throwable actualException;
    
    @Test
    public void testThrowsComparisonFailureExceptionWhenComparingNonEqualStrings() {
        JDaveRunner runner = new JDaveRunner(FailingSpec.class);
        runner.run(new RunNotifier() {
            @Override
            public void fireTestFailure(Failure failure) {
                actualException = failure.getException();
            }
        });
        assertEquals(ComparisonFailure.class, actualException.getClass());
    }
    
    public static class FailingSpec extends Specification<Void> {
        public class FailingContext {
            public void failingBehavior() {
                specify("foo", does.equal("bar"));
            }
        }
    }
}