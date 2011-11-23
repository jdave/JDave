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
package jdave.wicket;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.hamcrest.Matcher;

/**
 * @author Joni Freeman
 * @author Timo Rantalaiho
 */
public class MultiSelection<S extends Component> {
    private final Class<S> componentType;
    private final String wicketId;
    private Matcher<?> matcher;

    public MultiSelection(final Class<S> componentType) {
        this(componentType, null);
    }

    public MultiSelection(final Class<S> componentType, final String wicketId) {
        this.componentType = componentType;
        this.wicketId = wicketId;
        matcher = new AnyModelMatcher();
    }

    public <X extends S> List<X> from(final MarkupContainer root) {
        final Selector selector = new Selector();
        if (wicketId != null) {
            return selector.all(root, componentType, wicketId, matcher);
        }
        return selector.all(root, componentType, matcher);
    }

    public MultiSelection<S> which(final Matcher<?> matcher) {
        this.matcher = matcher;
        return this;
    }
}
