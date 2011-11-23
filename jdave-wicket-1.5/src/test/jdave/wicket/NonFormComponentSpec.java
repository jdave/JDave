/*
 * Copyright 2009 the original author or authors.
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

import jdave.Block;
import jdave.junit4.JDaveRunner;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.junit.runner.RunWith;

/**
 * @author Tuomas Kärkkäinen
 */
@RunWith(JDaveRunner.class)
public class NonFormComponentSpec extends ComponentSpecification<Label, String> {

    @Override
    protected Label newComponent(final String id, final IModel<String> model) {
        return new Label(id, model);
    }

    public class FormStarting {
        public void makesNoSense() {
            specify(
                    new Block() {
                        public void run() throws Throwable {
                            startForm(new Model<String>(), "");
                        }
                    },
                    must
                            .raise(
                                    IllegalArgumentException.class,
                                    "Your ComponentSpecification must be typed as <? extends Form<?>> if you want to start a form.  Instead you are using org.apache.wicket.markup.html.basic.Label"));
        }
    }

}
