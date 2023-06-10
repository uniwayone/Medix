package app.slyworks.communication_lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement


/**
 * Created by Joshua Sylvanus, 10:28 PM, 02/11/2022.
 */
class CoroutineMainDispatcherRule
  @OptIn(ExperimentalCoroutinesApi::class)
  constructor(val testDispatcher: TestDispatcher = UnconfinedTestDispatcher())
  : TestWatcher() {
    //region Vars
    //endregion

  override fun starting(description: Description?) {
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description?) {
    Dispatchers.resetMain()
  }
}