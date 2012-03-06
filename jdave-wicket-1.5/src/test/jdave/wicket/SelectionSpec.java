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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import jdave.Specification;
import jdave.junit4.JDaveRunner;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.junit.runner.RunWith;


/**
 * @author Mikko Peltonen
 */
@RunWith(JDaveRunner.class)
public class SelectionSpec extends Specification<Selection<Component>>{
    public class AnySelection {
        private Selector selector;
        private MarkupContainer searchContext;
        private Component returnedComponent;
        private Selection<Component> selection;
        
        public Selection<Component> create() {
            selector = mock(Selector.class);
            searchContext = mock(MarkupContainer.class);
            returnedComponent = mock(Component.class);
            return selection = new Selection<Component>(Component.class) {
                @Override
                protected Selector newSelector() {
                    return selector;
                }
            };
        }
        
        public void matchesAnythingByDefault() {
            checking(new Expectations() {{
                // This expectation does not actually test the third param as it looks like, 
                // because you can't apparently match Matchers(?). 
                one(selector).first(with(equalTo(searchContext)), with(equalTo(Component.class)), (Matcher<?>) with(is(anything())));will(returnValue(returnedComponent)); 
            }});
            specify(selection.from(searchContext), does.equal(returnedComponent));
        }
        
        public void usesGivenMatcher() {
            checking(new Expectations() {{
                // This expectation does not actually test the third param as it looks like, 
                // because you can't apparently match Matchers(?). 
                one(selector).first(with(equalTo(searchContext)), with(equalTo(Component.class)), (Matcher<?>) with(notNullValue()));will(returnValue(returnedComponent)); 
            }});
            specify(selection.which(is(notNullValue())).from(searchContext), does.equal(returnedComponent));            
        }
    }
}

