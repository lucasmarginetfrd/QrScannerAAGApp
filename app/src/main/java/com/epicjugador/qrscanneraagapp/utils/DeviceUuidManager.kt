package com.epicjugador.qrscanneraagapp.utils

import android.content.Context
import java.util.UUID
import androidx.core.content.edit

object DeviceUuidManager {

    private const val PREF_NAME = "qr_prefs"
    private const val KEY_UUID = "device_uuid"
    private const val KEY_REGISTERED = "device_registered"

    fun getUuid(context: Context): String {

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var uuid = prefs.getString(KEY_UUID, null)

        if (uuid == null) {
            uuid = (System.currentTimeMillis().toString() +
                    (1..99).random()).toString()

            prefs.edit { putString(KEY_UUID, uuid) }
        }

        return uuid
    }

    fun isRegistered(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REGISTERED, false)
    }

    fun markRegistered(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_REGISTERED, true) }
    }
}
