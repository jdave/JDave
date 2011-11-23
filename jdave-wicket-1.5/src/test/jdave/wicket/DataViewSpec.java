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

import java.util.Arrays;
import java.util.List;
import jdave.junit4.JDaveRunner;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import org.junit.runner.RunWith;

/**
 * @author Janne Hietam&auml;ki
 */
@RunWith(JDaveRunner.class)
public class DataViewSpec extends ComponentSpecification<DataView<String>, List<String>> {
    private DataView<String> dataView;

    @Override
    protected DataView<String> newComponent(String id, IModel<List<String>> model) {
        return dataView = new DataView<String>(id, new ListDataProvider<String>(Arrays.asList("one", "two", "three"))) {
            @Override
            protected void populateItem(Item<String> item) {
            }
        };
    }

    public class WhenContainerIsStarted {
        public DataView<String> create() {
            return startComponent(null);
        }

        public void theContextIsSameInstanceAsDataViewCreatedInNewContainer() {
            specify(context, is(sameInstance(dataView)));
        }

        public void dataViewIsInitialized() {
            specify(context.size(), should.equal(3));
        }
    }
}
