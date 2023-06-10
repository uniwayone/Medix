package app.slyworks.auth_feature.registration

import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import app.slyworks.auth_feature.R
import app.slyworks.utils_lib.utils.displayMessage
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Created by Joshua Sylvanus, 9:33 PM, 08-Dec-2022.
 */

/* you might need to change the implementation of displayMessage()
* and comment out onAttached() in RegistrationGeneral1Fragment*/
@RunWith(Enclosed::class)
class RegistrationGeneral1FragmentTest{

    @RunWith(RobolectricTestRunner::class)
    class PasswordUnitTest(){
        @Test
        fun `does regex work for at least 1 number`(){
            val s:CharSequence = "password1"
            assertThat(s.contains("[0-9]".toRegex())).isTrue()
        }

        @Test
        fun `does regex work for at least 1 uppercase character`(){
            val s:CharSequence = "Password"
            assertThat(s.contains("[A-Z]".toRegex())).isTrue()
        }

        @Test
        fun `does regex work for at least 1 special character`(){
            val s:CharSequence = "Password$"
            assertThat(s.contains("[@#\$*!~%^&+=]".toRegex())).isTrue()
        }
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    class ValidPasswordUnitTests(private val passwordToTest:String){
        companion object{
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name= "passwordToTest")
            fun passwords():Collection<String> =
                listOf(
                    "password1",
                    "Password1",
                    "password.",
                    "Password.",
                    "password.1")
        }

        private lateinit var fragment: RegistrationGeneral1Fragment

        fun setup2(){
            var activity:RegistrationActivity? = null
            ActivityScenario.launch(RegistrationActivity::class.java).onActivity{
                activity = it
            }

            onView(withId(R.id.siv_patient))
                .perform(click())

            Thread.sleep(2_000)

            fragment = activity!!.supportFragmentManager
                .findFragmentByTag(RegistrationGeneral1Fragment::class.simpleName) as RegistrationGeneral1Fragment
        }

        @Before
        fun setup(){
            launchFragment<RegistrationGeneral1Fragment>().onFragment{
                fragment = it
            }
        }

        @Test
        fun  `test passwords`(){
            assertThat(
                fragment.check(
                    "@gmail.com",
                    passwordToTest,
                    passwordToTest)).isFalse()
        }
    }

}

