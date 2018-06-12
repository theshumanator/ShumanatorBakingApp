package com.example.fatoumeh.shumanatorbakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by fatoumeh on 12/06/2018.
 */
@RunWith(AndroidJUnit4.class)
public class PrevNextButtonsTest {

    @Rule
    public ActivityTestRule<PlayRecipeVideoActivity> activityTestRule=
            new ActivityTestRule<>(PlayRecipeVideoActivity.class);

    @Test
    public void checkPrevNextButtonsDisplayed() {
        onView(withId(R.id.bt_previous)).check(matches(isDisplayed()));
        onView(withId(R.id.bt_next)).check(matches(isDisplayed()));
    }
}
