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

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.runner.RunWith;

/**
 * @author Joni Freeman
 * @author Timo Rantalaiho
 */
@RunWith(JDaveRunner.class)
public class SelectorSpec extends Specification<Matcher<?>> {
    private Selector selector = new Selector();
    private WebMarkupContainer root;
    private WebMarkupContainer childWithDuplicateId;
    private Component child1, child2, child3, grandChildWithDuplicateId;

    public class WithMatchingMatcher {
        public Matcher<String> create() {
            return Matchers.is("foo");
        }
        
        public void returnsFirstMatchingItemWhenFirstItemAsked() {
            specify(selector.first(root, WebMarkupContainer.class, context), does.equal(child1));
        }
        
        public void returnsAllMatchingItemsWhenAllItemsAsked() {
            specify(selector.all(root, WebMarkupContainer.class, context), containsExactly(child1, child3));
        }
    }
    
    public class WithNonMatchingMatcher {
        public Matcher<String> create() {
            return Matchers.is("non-matching");
        }
        
        public void returnsNullWhenFirstItemAsked() {
            specify(selector.first(root, WebMarkupContainer.class, context), does.equal(null));
        }
        
        public void returnsEmptyCollectionWhenAllItemsAsked() {
            specify(selector.all(root, WebMarkupContainer.class, context), containsExactly());
        }
    }

    public class SelectingById {
        public Matcher<String> create() {
            return Matchers.anything();
        }

        public void returnsFirstElementWithGivenId() {
            specify(selector.first(root, WebMarkupContainer.class, "child2", context), does.equal(child2));
        }

        public void returnsAllElementsWithGivenId() {
            specify(selector.all(root, WebMarkupContainer.class, "child2", context), containsExactly(child2));
        }
    }

    public class SelectingFirstByIdAndMatcher {
        public Matcher<String> create() {
            return Matchers.is("child");
        }

        public void returnsFirstMatchingComponentWithGivenId() {
            specify(selector.first(root, WebMarkupContainer.class, "nonunique", context), does.equal(childWithDuplicateId));
        }

        public void returnsAllMatchingComponentsWithGivenId() {
            specify(selector.all(root, Component.class, "nonunique", context), containsInOrder(childWithDuplicateId, grandChildWithDuplicateId));
        }
    }

    @Override
    public void create() {
        new WicketTester();
        root = new WebMarkupContainer("root");
        child1 = new WebMarkupContainer("child1").setDefaultModel(new Model<String>("foo"));
        child2 = new WebMarkupContainer("child2").setDefaultModel(new Model<String>("bar"));
        child3 = new WebMarkupContainer("child3").setDefaultModel(new Model<String>("foo"));

        WebMarkupContainer parentWithDuplicateId = (WebMarkupContainer) new WebMarkupContainer("nonunique").setDefaultModel(new Model<String>("parent"));
        childWithDuplicateId = (WebMarkupContainer) new WebMarkupContainer("nonunique").setDefaultModel(new Model<String>("child"));
        grandChildWithDuplicateId = new WebMarkupContainer("nonunique").setDefaultModel(new Model<String>("child"));
        root.add(child1);
        root.add(child2);
        root.add(parentWithDuplicateId);
        parentWithDuplicateId.add(childWithDuplicateId);
        childWithDuplicateId.add(grandChildWithDuplicateId);
        ((WebMarkupContainer) child2).add(child3);
    }
}
