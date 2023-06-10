package app.slyworks.medix.ui

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


/**
 * Created by Joshua Sylvanus, 7:36 PM, 12/11/2022.
 */
//@RunWith(AndroidJUnit4::class)
@RunWith(RobolectricTestRunner::class)
class BaseTest {
    //region Vars
    //endregion

    /*@Rule
    val activityTestRule:ActivityTestRule<LoginActivity> = ActivityTestRule<>(LoginActivity::class.java)

    @Before
    fun setup(){
        setFailureHandler(CustomFailureHandler(
            InstrumentationRegistry.getInstrumentation().targetContext
        ))
    }*/

    /*
    * pressBack()
    * onData()
    * closeSoftKeyboard()
    * openContextualActionModeOverflowMenu()
    * openActionBarOverflowOptionsMenu()
    * */

    /*
    * @Test
public void objectMatcher() {
onView(not(isChecked()));
onView(allOf(withText("item 1"), isChecked()));
}
@Test
public void hierarchy() {
onView(withParent(withId(R.id.todo_item)));
onView(withChild(withText("item 2")));
onView(isDescendantOfA(withId(R.id.todo_item)));
onView(hasDescendant(isChecked()));
* onView(hasSibling(withContentDescription(R.string.menu_filter)));
}
@Test
public void input() {
onView(supportsInputMethods());
onView(hasImeAction(EditorInfo.IME_ACTION_SEND));
}
@Test
public void classMatchers() {
onView(isAssignableFrom(CheckBox.class));
onView(withClassName(is(FloatingActionButton.class.
getCanonicalName())));
}
@Test
public void rootMatchers() {
onView(isFocusable());
onView(withText(R.string.name_hint)).inRoot(isTouchable());
onView(withText(R.string.name_hint)).inRoot(isDialog());
onView(withText(R.string.name_hint)).inRoot(isPlatformPopup());
}
@Test
* public void preferenceMatchers() {
onData(withSummaryText("3 days"));
onData(withTitle("Send notification"));
onData(withKey("example_switch"));
onView(isEnabled());
}
@Test
public void layoutMatchers() {
onView(hasEllipsizedText());
onView(hasMultilineText());
}
    * onView(withId(R.id.viewId)).check(matches(withContentDescription(contains
String("YYZZ"))));
    *
    *
    * onView(withText(equalToIgnoringCase("xxYY"))).perform(click());

   *
   *
   * onView(withText(equalToIgnoringWhiteSpace("XX YY ZZ"))).perform(click());

   *
   * onView(withId(R.id.viewId)).check(matches(withText(not(containsString
("YYZZ")))));
*
* onView(withId(R.id.viewId))
.check(matches(allOf(withText(not(startsWith("ZZ"))),
withText(containsString("YYZZ")))));
*
* inRoot() : if there are 2 or more windows and you are interested
* in the parent
*
*
*
* @Test
public void checksToDoStateChange() {
// adding new TO-DO
onView(withId(R.id.fab_add_task)).perform(click());
onView(withId(R.id.add_task_title))
.perform(typeText(toDoTitle), closeSoftKeyboard());
onView(withId(R.id.add_task_description))
.perform(typeText(toDoDescription), closeSoftKeyboard());
onView(withId(R.id.fab_edit_task_done)).perform(click());
// marking our TO-DO as completed
onView(withId(R.id.todo_complete)).perform(click());
// filtering out the completed TO-DO
onView(withId(R.id.menu_filter)).perform(click());
onView(allOf(withId(android.R.id.title), withText(R.string.nav_completed)))
.perform(click());
onView(withId(R.id.todo_title))
.check(matches(allOf(withText(toDoTitle), isDisplayed())));
}
*
*
* onView(allOf(withId(android.R.id.title), withText(R.string.nav_completed)))
.perform(click());
onView(withId(R.id.todo_title))
.check(matches(allOf(withText(toDoTitle), isDisplayed())));


  @Test
public void dataInteraction() {
openDrawer();
onView(allOf(withId(R.id.design_menu_item_text),
withText(R.string.settings_title))).perform(click());
// start of the flow as shown in Figure 1-13
onData(instanceOf(PreferenceActivity.Header.class))
.inAdapterView(withId(android.R.id.list))
.atPosition(0)
.onChildView(withId(android.R.id.title))
.check(matches(withText("General")))
.perform(click());
onData(withKey("email_edit_text"))
/*we have to point explicitly to the parent of the General
prefs list
because there are two {@ListView}s with id android.R.id.list in
the hierarchy*/

.inAdapterView(allOf(withId(android.R.id.list),
withParent(withId(android.R.id.list_container))))
.check(matches(isDisplayed()))
.perform(click());
onView(withId(android.R.id.edit)).perform(replaceText("sample@ema.il"));
onView(withId(android.R.id.button1)).perform(click());
onData(withKey("email_edit_text"))
.inAdapterView(allOf(withId(android.R.id.list),
withParent(withId(android.R.id.list_container))))
.onChildView(withId(android.R.id.summary))
.check(matches(withText("sample@ema.il")));
}

     .inAdapterView(allOf(
withId(android.R.id.list),
withParent(withId(android.R.id.list_container))))
.onChildView(withId(android.R.id.summary))
.check(matches(withText("sample@ema.il")))
     */

    /*
    * testing recyclerView
    * @Test
public void addNewToDos() throws Exception {
generateToDos(12);
onView(withId(R.id.tasks_list))
.perform(actionOnItemAtPosition(10, scrollTo()));
onView(withId(R.id.tasks_list))
.perform(scrollToPosition(1));
onView(withId(R.id.tasks_list))
.perform(scrollToPosition(12));
onView(withId(R.id.tasks_list))
.perform(actionOnItemAtPosition(12, click()));
Espresso.pressBack();
onView(withId(R.id.tasks_list))
.perform(scrollToPosition(2));
}
You
    * */


    /*
    * @Test
public void addNewToDosChained() throws Exception {
generateToDos(12);
onView(withId(R.id.tasks_list))
.perform(actionOnItemAtPosition(10, scrollTo()))
.perform(scrollToPosition(1))
.perform(scrollToPosition(12))
.perform(actionOnItemAtPosition(12, click()))
.perform(pressBack())
.perform(scrollToPosition(2));
}
    * */
}