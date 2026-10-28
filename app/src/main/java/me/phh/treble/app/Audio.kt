package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Audio: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            AudioSettings.headsetDevinput -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.overlay.devinputjack", if (value) "true" else "false")
            }
            AudioSettings.disableAudioEffects -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_audio_effects", if (value) "1" else "0")
            }
            AudioSettings.disableFastAudio -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_fast_audio", if (value) "1" else "0")
            }
            AudioSettings.disableVoiceCallIn -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_voice_call_in", if (value) "true" else "false")
            }
            AudioSettings.alternateAudiopolicy -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "1" else "0"
                Tools.safeSetprop("persist.sys.phh.caf.audio_policy", value)
            }
            // Bluetooth
            AudioSettings.sysbta -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.bluetooth.system_audio_hal.enabled", if (value) "true" else "false")
            }
            AudioSettings.workarounds -> {
                val value = sp.getString(key, "none")
                when (value) {
                    "none" -> {
                        SystemProperties.set("persist.sys.bt.unsupported.commands", "")
                        SystemProperties.set("persist.sys.bt.unsupported.ogfeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.lefeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.states", "")
                    }
                    "mediatek", "huawei" -> {
                        SystemProperties.set("persist.sys.bt.unsupported.commands", "182")
                        SystemProperties.set("persist.sys.bt.unsupported.ogfeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.lefeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.states", "")
                    }
                }
            }
            AudioSettings.escoTransportUnitSize -> {
                val value = sp.getString(key, "0")
                SystemProperties.set("persist.sys.bt.esco_transport_unit_size", value)
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Starting Audio service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot

        sp.edit().putBoolean(AudioSettings.sysbta, SystemProperties.getBoolean("persist.bluetooth.system_audio_hal.enabled", false)).apply()
        if (SamsungSettings.enabled(ctxt)) { sp.edit().putString(AudioSettings.escoTransportUnitSize, "16").apply() }
        spListener.onSharedPreferenceChanged(sp, AudioSettings.workarounds)
    }
}
