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
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.PreferenceMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Cat.Gadgets
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_CAST_GLOBALLY_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_MULTIPLAYER_VARIABLES_ENABLED
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_ARDUINO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_JUMPING_SUMO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_NFC_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_PHIRO_BRICKS
import org.catrobat.catroid.ui.settingsfragments.SETTINGS_SHOW_RASPI_BRICKS
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManageExtensionsSettingsFragmentTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SettingsActivity::class.java, true, false
    )

    private val extensionSettings: List<String> =
        listOf(
            SETTINGS_SHOW_ARDUINO_BRICKS,
            SETTINGS_SHOW_PHIRO_BRICKS,
            SETTINGS_SHOW_NFC_BRICKS,
            SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED,
            SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED,
            SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED,
            SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED,
            SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS,
            SETTINGS_SHOW_JUMPING_SUMO_BRICKS,
            SETTINGS_SHOW_RASPI_BRICKS,
            SETTINGS_MULTIPLAYER_VARIABLES_ENABLED,
            SETTINGS_CAST_GLOBALLY_ENABLED,
            SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
            SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
            SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS,
            SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
            SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
        )

    @Category(AppUi::class, Smoke::class, Gadgets::class)
    @Test
    fun legoNxtSettingsTest() {
        Espresso.onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_nxt_bricks))
            .perform(click())
        switchPreference(
            R.string.preference_title_enable_mindstorms_nxt_bricks,
            SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED
        )
        switchPreference(
            R.string.preference_disable_nxt_info_dialog,
            SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED
        )
    }

    @Category(AppUi::class, Smoke::class, Gadgets::class)
    @Test
    fun legoEv3SettingsTest() {
        Espresso.onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_ev3_bricks))
            .perform(click())
        switchPreference(
            R.string.preference_title_enable_mindstorms_ev3_bricks,
            SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED
        )
        switchPreference(
            R.string.preference_disable_nxt_info_dialog,
            SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED
        )
    }

    @Category(AppUi::class, Smoke::class, Gadgets::class)
    @Test
    fun parrotArSettingsTest() {
        Espresso.onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_quadcopter_bricks))
            .perform(click())
        switchPreference(
            R.string.preference_title_enable_quadcopter_bricks,
            SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS
        )
    }

    @Category(AppUi::class, Smoke::class, Gadgets::class)
    @Test
    fun rasPiSettingsTest() {
        Espresso.onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_raspi_bricks))
            .perform(click())
        switchPreference(R.string.preference_title_enable_raspi_bricks, SETTINGS_SHOW_RASPI_BRICKS)
    }

    @Category(AppUi::class, Smoke::class, Gadgets::class)
    @Test
    fun aiSettingsTest() {
        Espresso.onData(PreferenceMatchers.withTitle(R.string.preference_title_ai))
            .perform(click())
        switchPreference(
            R.string.preference_title_ai_speech_recognition,
            SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS
        )
        switchPreference(
            R.string.preference_title_ai_speech_synthetization,
            SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS
        )
        switchPreference(
            R.string.preference_title_ai_face_detection,
            SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS
        )
        switchPreference(
            R.string.preference_title_ai_pose_detection,
            SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS
        )
        switchPreference(
            R.string.preference_title_ai_text_recognition,
            SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS
        )
    }

    private fun switchPreference(displayedTitleResourceString: Int, sharedPreferenceTag: String) {
        val sharedPreferences = getDefaultSharedPreferences(getApplicationContext())

        clickOnSettingsItem(displayedTitleResourceString)

        assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, false))

        clickOnSettingsItem(displayedTitleResourceString)

        assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, false))
    }

    private fun clickOnSettingsItem(resourceId: Int) {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(resourceId)),
                    click()
                )
            )
    }
}