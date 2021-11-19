package com.example.sampleproject.authentication;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import com.example.sampleproject.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> onActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(OdabirActivity.class.getName(),null,false);


    @Before
    public void setUp() throws Exception {

        mActivity = onActivityTestRule.getActivity();
    }

    @Test
    public void testLaunchOfOdabirActivityOnButtonClick(){
        assertNotNull(mActivity.findViewById(R.id.textViewSignUp));
        onView(withId(R.id.textViewSignUp)).perform(click());
        Activity odabirActivity = getInstrumentation().waitForMonitorWithTimeout(monitor,5000);
        assertNotNull(odabirActivity);

        odabirActivity.finish();
    }
    @After
    public void tearDown() throws Exception {

        mActivity=null;
    }
}