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
package jdave.containment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Joni Freeman
 */
public class ExactContainment<T> extends CollectionContainment<T> {
    public ExactContainment(final Collection<T> elements) {
        super(elements);
    }

    public ExactContainment(final Iterator<T> elements) {
        super(elements);
    }

    public ExactContainment(final Iterable<T> elements) {
        super(elements);
    }

    @Override
    public boolean nullSafeMatches(final Collection<T> actual) {
        if (actual.size() != elements.size()) {
            return false;
        }
        final List<Object> remaining = new ArrayList<Object>(actual);
        for (final Object element : elements) {
            remaining.remove(element);
        }
        return remaining.isEmpty();
    }
}
