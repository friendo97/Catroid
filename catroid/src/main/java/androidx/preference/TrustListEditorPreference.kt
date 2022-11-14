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

package androidx.preference

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager
import org.catrobat.catroid.common.Constants

class TrustListEditorPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    init {
//        dialogLayoutResource = R.layout.alert_dialog_layout
//
//        // TODO working solution for EditTextPreference (help button not showing)
//        setDialogTitle(R.string.preference_screen_web_access_title)
//        setPositiveButtonText(R.string.ok)
//        setNegativeButtonText(R.string.cancel)
//
//        setOnBindEditTextListener { editText ->
//            val text: String = TrustedDomainManager.getUserTrustList()
//            editText.setText(text)
//            editText.setSelection(text.length)
//        }
//
//        setOnPreferenceChangeListener { _, newValue: Any? ->
//            TrustedDomainManager.setUserTrustList(newValue.toString())
//            false
//        }

        // TODO working solution for Preference below

        setOnPreferenceClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_trusted_list_preference, null)
            val editText = dialogView.findViewById<EditText>(R.id.trust_list_preference_edit_text)

            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle(R.string.preference_screen_web_access_title)
                .setPositiveButton(R.string.ok) { _, _ ->
                    TrustedDomainManager.setUserTrustList(editText.text.toString())
                }
                .setNeutralButton(R.string.help) { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEB_REQUEST_WIKI_URL))
                    startActivity(context, intent, null)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }

            dialogBuilder.create().apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                editText.setText(TrustedDomainManager.getUserTrustList())
                editText.setSelection(editText.text.length)
                editText.requestFocus()
                show()
            }
            true
        }
    }
}
