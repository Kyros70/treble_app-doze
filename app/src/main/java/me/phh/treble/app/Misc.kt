package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.ServiceManager
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager
import vendor.ims.zenmotion.V1_0.IZenMotion
import vendor.mediatek.hardware.agolddaemon.IAgoldDaemon

object Misc: EntryStartup {
    fun safeSetprop(key: String, value: String?) {
        try {
            Log.d("PHH", "Setting property $key to $value")
            SystemProperties.set(key, value)
        } catch (e: Exception) {
            Log.d("PHH", "Failed setting prop $key", e)
        }
    }

    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when (key) {
            MiscSettings.treatVirtualSensorsAsReal -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.virtual_sensors_are_real", if (value) "1" else "0")
            }
            MiscSettings.disableSaeUpgrade -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.wifi_disable_sae", if (value) "true" else "false")
            }
            MiscSettings.storageFUSE -> {
                val value = sp.getBoolean(key, false)
                Log.d("PHH", "Setting storageFUSE to $value")
                SystemProperties.set("persist.sys.fflag.override.settings_fuse", if (!value) "true" else "false")
            }
            MiscSettings.maxAspectRatioPreO -> {
                val value = sp.getString(key, "1.86")
                SystemProperties.set("persist.sys.max_aspect_ratio.pre_o", value)
                Log.d("PHH", "Setting max aspect ratio for pre-o app $value")
            }
            MiscSettings.preferSwCodecs -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.force_sw_codecs", if (value) "true" else "false")
            }
            MiscSettings.dt2w -> {
                val value = sp.getBoolean(key, false)
                val asusSvc = try { IZenMotion.getService() } catch (e: Exception) { null }
                asusSvc?.setDclickEnable(if (value) 1 else 0)
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading Misc fragment")
        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh on boot
        spListener.onSharedPreferenceChanged(sp, MiscSettings.storageFUSE)
        spListener.onSharedPreferenceChanged(sp, MiscSettings.dt2w)
        spListener.onSharedPreferenceChanged(sp, MiscSettings.preferSwCodecs)
        spListener.onSharedPreferenceChanged(sp, MiscSettings.maxAspectRatioPreO)
    }
}
