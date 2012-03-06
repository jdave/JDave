package jdave.wicket;

import org.apache.wicket.Component;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @author Timo Rantalaiho
*/
public class ComponentsModelMatchesTo<T extends Component> extends BaseMatcher<T> {
    private final Matcher<?> matcher;

    public ComponentsModelMatchesTo(Matcher<?> matcher) {
        this.matcher = matcher;
    }

    @SuppressWarnings({"unchecked"})
    public final boolean matches(Object o) {
        T component = (T) o;
        return matches(component);
    }

    protected boolean matches(T component) {
        if (matcher instanceof AnyModelMatcher) {
            return true;
        }
        return (matcher.matches(component.getDefaultModelObject()));
    }

    public void describeTo(Description description) {
        matcher.describeTo(description);
    }
}
