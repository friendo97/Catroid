/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.ui

import android.os.Bundle
import android.view.Menu
import org.catrobat.catroid.R
import org.catrobat.catroid.databinding.PreferenceBinding
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfilesFragment.SETTINGS_FRAGMENT_INTENT_KEY
import org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment

class SettingsActivity : BaseActivity() {

    private lateinit var binding: PreferenceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, SettingsFragment())
            .commit()
        if (intent.extras != null &&
            intent.getBooleanExtra(SETTINGS_FRAGMENT_INTENT_KEY, false)) {
                // todo same
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame, AccessibilitySettingsFragment())
                .addToBackStack(AccessibilitySettingsFragment.TAG)
                .commit()
        }

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setTitle(R.string.preference_title)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings_preference, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) = false
}
