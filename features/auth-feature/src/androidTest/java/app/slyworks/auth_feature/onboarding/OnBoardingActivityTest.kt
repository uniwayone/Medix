package app.slyworks.auth_feature.onboarding

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.AndroidJUnitRunner
import app.slyworks.auth_feature.R
import app.slyworks.auth_feature.registration.RegistrationActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Joshua Sylvanus, 4:46 PM, 16-Dec-2022.
 */

@RunWith(AndroidJUnit4::class)
class OnBoardingActivityTest{
    @get:Rule
    val intentsTestRule = IntentsTestRule(OnBoardingActivity::class.java)
/*
     @Before
     fun setup(){
         ActivityScenario.launch(OnBoardingActivity::class.java)
     }*/

    @Test
    fun isEspressoWorking(){
        onView(withId(R.id.btnLogin_onboarding))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNavigatingToRegistrationActivity(){
        onView(withId(R.id.btnGetStarted_onboarding))
            .perform(ViewActions.click())

        Intents.intended(hasComponent(RegistrationActivity::class.java.name))
    }
}