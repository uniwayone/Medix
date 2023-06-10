package app.slyworks.auth_feature.login

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Created by Joshua Sylvanus, 4:43 AM, 18-Dec-2022.
 */
@RunWith(RobolectricTestRunner::class)
class LoginActivityTest{
    private val instrumentation:Instrumentation = InstrumentationRegistry.getInstrumentation()
    private val uiDevice: UiDevice = UiDevice.getInstance(instrumentation)
    private val TIMEOUT = 4_000L

    @get:Rule
    val activityTestRule:ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun uiSelectorSample(){
        uiDevice.findObject(
            UiSelector().resourceId(
                "app.slyworks.medix:id/btnLoginLogin"
            ))
            .click()


    }
}