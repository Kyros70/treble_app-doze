package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File

object MiscSettings : Settings {
    val treatVirtualSensorsAsReal = "key_misc_treat_virtual_sensors_as_real"
    val disableSaeUpgrade = "key_misc_disable_sae_upgrade"
    val storageFUSE = "key_misc_storage_fuse"
    val dt2w = "key_misc_dt2w"
    val preferSwCodecs = "key_misc_force_sw_codecs"
    val maxAspectRatioPreO = "key_misc_max_aspect_ratio_pre_o"

    override fun enabled(context: Context): Boolean = true
    fun isRoot() = File(Tools.phhsu).exists()
}

class MiscSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_misc) // Inicializa preferências aqui

        activity?.let { context ->
            EntryService.getEnabledPreferences(context).forEach { (key, isEnabled) ->
                if (!isEnabled) {
                    findPreference(key)?.let { pref ->
                        pref.parent?.removePreference(pref)
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_misc_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura a Toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        // Configura o ListView
        view.findViewById<ListView>(android.R.id.list)?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = false
            setPadding(32, 64, 32, 32)
        }
    }
}
