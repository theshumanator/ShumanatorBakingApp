package com.example.fatoumeh.shumanatorbakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by fatoumeh on 12/06/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SelectARecipeTest {

    public static final String RECIPE_NAME="Nutella Pie";

    @Rule
    public ActivityTestRule<SelectARecipeActivity> activityTestRule=new ActivityTestRule<>(SelectARecipeActivity.class);

    /*
        Test that the first item clicked is nutella pie: verify using the toolbar title
        because the recipe ame isnt saved anywhere else
     */
    @Test
    public void clickRecipeItem_OpensDetails() {
        onView(withId(R.id.rv_recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(Matchers.<CharSequence>is(RECIPE_NAME))));
    }

    //The method below was found in stackoverflow
    public static Matcher<View> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

}
