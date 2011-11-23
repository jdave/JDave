package jdave.wicket;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Timo Rantalaiho
*/
public class AnyModelMatcher extends BaseMatcher<Object> {
    public boolean matches(Object o) {
        return true;
    }

    public void describeTo(Description description) {
        description.appendText(" can be any model, even null");
    }
}
