/*
 * Copyright 2008 the original author or authors.
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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.junit.runner.RunWith;

/**
 * @author Mikko Peltonen
 */
@RunWith(JDaveRunner.class)
public class MarkupProvidingPageSpec extends ComponentSpecification<Form<Void>, String> {
    public class PageWithProvidedMarkup {
        public Form<Void> create() {
            return startComponent(new Model<String>("field text"), pageMarkup(), "form");
        }

        public void containsComponentsSpecifiedInMarkup() {
            final Form<?> form = selectFirst(Form.class, "form").from(context.getPage());
            final TextField<?> textField = selectFirst(TextField.class, "textField").from(context);
            specify(form, isNotNull());
            specify(textField, isNotNull());
            specify(textField.getModelObject(), does.equal("field text"));
        }

        private CharSequence pageMarkup() {
            final StringBuilder markup = new StringBuilder("<html><body>");
            markup
                    .append("<form wicket:id='form'><input type='text' wicket:id='textField'/></form>");
            return markup.append("</body></html>");
        }
    }

    public class FormWithProvidedMarkup {
        public Form<Void> create() {
            return startForm(new Model<String>("field text"), formMarkup());
        }

        public void containsComponentsSpecifiedInMarkup() {
            final Form<?> form = selectFirst(Form.class).from(context.getPage());
            final TextField<?> textField = selectFirst(TextField.class, "textField").from(context);
            specify(form, isNotNull());
            specify(textField, isNotNull());
            specify(textField.getModelObject(), does.equal("field text"));
        }

        private CharSequence formMarkup() {
            return "<input type='text' wicket:id='textField'/>";
        }
    }

    @Override
    protected Form<Void> newComponent(final String id, final IModel<String> model) {
        final Form<Void> form = new Form<Void>(id);
        form.add(new TextField<Object>("textField", cast(model)));
        return form;
    }
}
