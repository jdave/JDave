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

import jdave.junit4.JDaveRunner;
import org.apache.wicket.model.IModel;
import static org.hamcrest.Matchers.sameInstance;
import org.junit.runner.RunWith;

/**
 * @author Joni Freeman
 */
@RunWith(JDaveRunner.class)
public class EmptyPanelSpec extends ComponentSpecification<EmptyPanel,Object> {
    private EmptyPanel panel;
    
    public class WhenContainerIsStarted {
        public EmptyPanel create() {
            return startComponent(null);
        }
        
        public void theContextIsSameInstanceAsPanelCreatedInNewContainer() {
            specify(context, sameInstance(panel));
        }
        
        public void wicketTesterInstanceIsCreated() {
            specify(wicket, should.not().equal(null));
        }
    }

    @Override
    protected EmptyPanel newComponent(String id, IModel<Object> model) {
        return panel = new EmptyPanel(id);
    }
}
