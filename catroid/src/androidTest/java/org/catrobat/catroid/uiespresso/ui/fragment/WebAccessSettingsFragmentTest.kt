/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.ui.fragment

import org.catrobat.catroid.R
import android.content.Intent
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.PreferenceMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.hamcrest.Matcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebAccessSettingsFragmentTest {
    private lateinit var expectedBrowserIntent: Matcher<Intent>

    @Category(AppUi::class, Smoke::class)
    @Test
    fun webAccessSettingTest() {
        Espresso.onData(PreferenceMatchers.withTitle(R.string.preference_title_web_access))
            .perform(click())
        onView(withText(R.string.preference_screen_web_access_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.edit)).perform(ViewActions.typeText("domain.net"))
//        onView(withId(R.id.button1))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        onView(withId(R.id.button2))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        onView(withId(R.id.button3)).perform(click())
        Intents.intended(expectedBrowserIntent)
    }

    private fun switchPreference(displayedTitleResourceString: Int, sharedPreferenceTag: String) {
        val sharedPreferences =
            getDefaultSharedPreferences(getApplicationContext())

        clickOnSettingsItem(displayedTitleResourceString)

        assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, false))

        clickOnSettingsItem(displayedTitleResourceString)

        assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, false))
    }

    private fun clickOnSettingsItem(resourceId: Int) {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(ViewMatchers.withText(resourceId)),
                    click()
                )
            )
    }
}