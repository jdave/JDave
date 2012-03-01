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

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.hamcrest.Matcher;

/**
 * @author Joni Freeman
 * @author Mikko Peltonen
 * @author Timo Rantalaiho
 */
public class Selection<S extends Component> {
    private final Class<S> componentType;
    private final String wicketId;
    private Matcher<?> matcher;

    public Selection(final Class<S> componentType) {
        this(componentType, null);
    }

    public Selection(final Class<S> componentType, final String wicketId) {
        this.componentType = componentType;
        matcher = new AnyModelMatcher();
        this.wicketId = wicketId;
    }

    public <X extends S> X from(final MarkupContainer root) {
        // Note that the redundant type parameters are needed because of this javac bug:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
        if (wicketId != null) {
            return newSelector().<S, X> first(root, componentType, wicketId, matcher);
        }
        return newSelector().<S, X> first(root, componentType, matcher);
    }

    public Selection<S> which(final Matcher<?> matcher) {
        this.matcher = matcher;
        return this;
    }

    protected Selector newSelector() {
        return new Selector();
    }
}
