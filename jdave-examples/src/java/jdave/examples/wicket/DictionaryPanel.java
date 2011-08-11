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
package jdave.examples.wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author Joni Freeman
 */
public abstract class DictionaryPanel extends Panel {
    private List<String> hits = new ArrayList<String>();
    private String query;
    
    public DictionaryPanel(String id) {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new Form<Void>("form") {
            {
                add(new TextField<String>("query", new PropertyModel<String>(DictionaryPanel.this, "query")));
            }
            @Override
            protected void onSubmit() {
                hits = getService().lookup(query);
                if (hits.isEmpty()) {
                    DictionaryPanel.this.info("No hits.");
                }
            }
        });
        add(new RefreshingView<String>("words") {
            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return new ModelIteratorAdapter<String>(hits.iterator()) {
                    @Override
                    protected IModel<String> model(String object) {
                        return new Model<String>(object);
                    }
                };
            }

            @Override
            protected void populateItem(Item<String> item) {
                item.add(new Label("word", item.getDefaultModelObjectAsString()));
            }
        });
    }

    protected abstract IDictionaryService getService();
}
