package com.arlanallacsta.submissionstoryapp.datastore

import android.content.Context
import android.content.SharedPreferences
import hu.autsoft.krate.Krate
import hu.autsoft.krate.booleanPref
import hu.autsoft.krate.default.withDefault
import hu.autsoft.krate.stringPref

class UserPreference(context: Context) : Krate {

    override val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("user_preference", Context.MODE_PRIVATE)

    var isLogin by booleanPref().withDefault(false)
    var token by stringPref().withDefault("")

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}