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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jdave.junit4.JDaveRunner;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.junit.runner.RunWith;

/**
 * @author Joni Freeman
 * @author Timo Rantalaiho
 */
@RunWith(JDaveRunner.class)
public class PageWithItemsSpec extends ComponentSpecification<PageWithItems, List<Integer>> {
    public class WhenListHasItems {
        private final List<Integer> list = Arrays.asList(0, 1, 2);

        public PageWithItems create() {
            return startComponent(new ListModel<Integer>(list));
        }

        public void theItemsCanBeReferencedEasilyInListView() {
            final ListItem<Integer> item = itemAt(selectFirst(ListView.class, "listView")
                    .<ListView<Integer>> from(context), 1);
            specify(item.get("item").getDefaultModelObject(), does.equal("1"));
        }

        public void theItemsCanBeReferencedEasilyInRefreshingView() {
            final Item<Integer> item = itemAt(selectFirst(RefreshingView.class, "refreshingView")
                    .<RefreshingView<Integer>> from(context), 1);
            specify(item.get("item").getDefaultModelObject(), does.equal("1"));
        }

        public void theModelObjectsCanBeCollectedFromComponents() {
            final ListView<Integer> listView = selectFirst(ListView.class, "listView")
                    .<ListView<Integer>> from(context);
            specify(modelObjects(listView.iterator()), containsInOrder(0, 1, 2));
        }

        public void theModelObjectsCanBeUsedInContainmentExpectations() {
            final ListView<Integer> listView = selectFirst(ListView.class, "listView")
                    .<ListView<Integer>> from(context);
            specify(listView, containsInOrder(0, 1, 2));
        }

        public void theModelObjectsCanBeUsedInNegativeContainmentExpectations() {
            final ListView<Integer> listView = selectFirst(ListView.class, "listView")
                    .<ListView<Integer>> from(context);
            specify(listView, does.not().containAll(0, 1, 2, 4));
        }

        public void theFirstItemCanBePickedUsingHamcrestMatcher() {
            final Item<Integer> item = selectFirst(Item.class).which(is(0)).from(context);
            specify(item.get("item").getDefaultModelObject(), does.equal("0"));
        }

        @SuppressWarnings("unchecked")
        public void allItemsCanBePickedUsingHamcrestMatcher() {
            final List<Item<Integer>> items = selectAll(Item.class).which(is(anyOf(is(0), is(1))))
                    .from(context);
            specify(modelObjects(items.iterator()), containsInOrder(0, 1));
        }

        public void theFirstItemCanBePickedUsingItsId() {
            final Label item = selectFirst(Label.class, "item").from(context);
            specify(item.getDefaultModelObjectAsString(), is("0"));
        }

        public void allItemsWithSameIdCanBePickedUsingTheirId() {
            final List<Label> items = selectAll(Label.class, "item").from(context);
            final List<String> modelObjects = collectModelObjects(items);
            specify(modelObjects, containsInOrder("0", "1", "2", "0", "1", "2"));
        }

        public void selectingFirstWithIdAlsoHonorsHamcrestMatchers() {
            final Label two = selectFirst(Label.class, "item").which(is("2")).from(context);
            specify(two.getDefaultModelObjectAsString(), does.equal("2"));
        }

        public void selectingAllWithIdAlsoHonorsHamcrestMatchers() {
            final List<Label> twos = selectAll(Label.class, "item").which(is("2")).from(context);
            specify(collectModelObjects(twos), containsExactly("2", "2"));
        }

        private List<String> collectModelObjects(final List<Label> items) {
            final List<String> modelObjects = new ArrayList<String>();
            for (final Label item : items) {
                modelObjects.add(item.getDefaultModelObjectAsString());
            }
            return modelObjects;
        }
    }

    public class ContainerWithinAContainerWithCompoundPropertyModel {
        public PageWithItems create() {
            return startComponent(new ListModel<Integer>(new ArrayList<Integer>()));
        }

        public void canBeSelectedById() {
            specify(selectAll(WebMarkupContainer.class, "innerContainer").from(context).size(),
                    does.equal(1));
            specify(selectFirst(WebMarkupContainer.class, "innerContainer").from(context),
                    isNotNull());
        }
    }

    @Override
    protected PageWithItems newComponent(final String id, final IModel<List<Integer>> model) {
        return new PageWithItems(model);
    }
}
