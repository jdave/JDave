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

import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Joni Freeman
 */
public class PageWithItems extends WebPage {
    public PageWithItems(final IModel<List<Integer>> items) {
        add(new ListView<Integer>("listView", items) {
            @Override
            protected void populateItem(ListItem<Integer> item) {
                item.add(new Label("item", item.getModelObject().toString()));
            }
        });
        add(new RefreshingView<Integer>("refreshingView") {
            @Override
            protected Iterator<IModel<Integer>> getItemModels() {
                return new ModelIteratorAdapter<Integer>(items.getObject().iterator()) {
                    @Override
                    protected IModel<Integer> model(Integer object) {
                        return new Model<Integer>(object);
                    }                    
                };
            }

            @Override
            protected void populateItem(Item<Integer> item) {
                item.add(new Label("item", item.getModelObject().toString()));
            }
        });
        add(new WebMarkupContainer("container", new CompoundPropertyModel<List<Integer>>(this))
            .add(new WebMarkupContainer("innerContainer")));
    }
}
