package jdave.wicket;

import org.apache.wicket.Component;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Timo Rantalaiho
*/
public class WicketIdEqualsTo<T extends Component> extends BaseMatcher<T> {
    private final String wicketId;

    public WicketIdEqualsTo(String wicketId) {
        this.wicketId = wicketId;
    }

    @SuppressWarnings({"unchecked"})
    public final boolean matches(Object o) {
        T component = (T) o;
        return matches(component);
    }

    protected boolean matches(T component) {
        return (wicketId.equals(component.getId()));
    }

    public void describeTo(Description description) {
        description.appendText(wicketId);
    }
}
