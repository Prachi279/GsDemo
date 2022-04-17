package com.example.gsdemo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import com.example.gsdemo.MyApplication
import com.example.gsdemo.model.APODDetail
import com.example.gsdemo.utils.PreferenceHelper.get
import com.example.gsdemo.utils.PreferenceHelper.set
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * The CommonUtils method, to create general methods
 */
object CommonUtils {
    /**
     * The getFormattedDate method, to get date in a specific format
     */
    fun getFormattedDate(date: Date = Date()): String =
        SimpleDateFormat(AppConstants.DATE_FORMAT).format(date).toString()

    /**
     * The saveObjIntoPref method, to save object into preference
     */
    fun <T> saveObjIntoPref(data: T, prefName: String) {
        val gson = Gson()
        val json: String = gson.toJson(data)
        MyApplication.prefHelper!![prefName] = json
    }

    /**
     * The getTodayAPODData method, to get today's apod data from the preference
     */
    fun getTodayAPODData(): APODDetail? {
        return getObjFromPref(AppConstants.TODAY_APOD_DATA, APODDetail::class.java)
    }

    /**
     * The retrieveObjFromPref method, to retrieve object from Preferences according to the given class and return the object
     */
    private fun <T> getObjFromPref(prefName: String, className: Class<T>): T? {
        val gson = Gson()
        val data: String = MyApplication.prefHelper!![prefName, ""]!!
        return if (!TextUtils.isEmpty(data)) {
            val obj = gson.fromJson(data, className)
            obj
        } else {
            null
        }
    }

    /**
     * The getArrayListFromPref method, to convert string to arraylist object
     */
    fun getArrayListFromPref(prefName: String): List<APODDetail>? {
        val gson = Gson()
        val data: String? = MyApplication.prefHelper!![prefName, ""]!!
        if (!TextUtils.isEmpty(data)) {
            val type = object : TypeToken<List<APODDetail?>?>() {}.type
            return gson.fromJson(data, type)
        }
        return null
    }

    /**
     * The isOnline method, to check whether network is available or not
     * @param context
     * @return Boolean
     */
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val ni = cm.activeNetworkInfo
            if (ni != null) {
                return (ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE))
            }
        } else {
            val network = cm.activeNetwork
            if (network != null) {
                val nc = cm.getNetworkCapabilities(network)
                return (nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                ))
            }
        }

        return false
    }

    /**
     * The showError method, to show error message to the user
     */
    fun showError(response: Any): String? {
        val errorJson = JSONObject((response as ResponseBody).string())
        var errorMessage: String? = ""
        if (errorJson.has("error")) {
            val jsonObj = JSONObject((errorJson.getString("error")).toString())
            errorMessage = jsonObj.getString("message")
        } else if (errorJson.has("msg")) {
            errorMessage = errorJson.getString("msg")
        }
        return errorMessage
    }
}