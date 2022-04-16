package com.example.gsdemo.viewmodel

import android.app.Application
import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.example.gsdemo.R
import com.example.gsdemo.adapter.APODListAdapter
import com.example.gsdemo.model.APODDetail
import com.example.gsdemo.network.ProjectRepository
import com.example.gsdemo.utils.APICallback
import com.example.gsdemo.utils.AppConstants
import com.example.gsdemo.utils.CommonUtils
import com.example.gsdemo.utils.CommonUtils.getFormattedDate
import com.example.gsdemo.utils.snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * The MainViewModel class, a ViewModel class to fetch and show the data
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * The apoDetailObs , an observer for today's APOD data
     */
    var apoDetailObs: ObservableField<APODDetail> = ObservableField()

    /**
     * The isProgressDialogVisibile, to show and hide progressbar based on conditions
     */
    var isProgressDialogVisibile: ObservableBoolean = ObservableBoolean(false)

    /**
     * The startDateObs, an observer to set start date for date filter
     */
    var startDateObs: ObservableField<String> = ObservableField()

    /**
     * The endDateObs, and observer to set end date for date filter
     */
    var endDateObs: ObservableField<String> = ObservableField()

    /**
     * The isRangeListAvailable, a boolean observer to check whether the list is received from the API or not
     */
    var isRangeListAvailable: ObservableBoolean = ObservableBoolean(false)

    /**
     * The apodListAdapter , an adapter instance
     */
    var apodListAdapter: APODListAdapter = APODListAdapter(this)


    /**
     * The callTodayAPODAPI method, to get today's image
     */
    fun callTodayAPODAPI(
        callback: APICallback
    ) {
        val call = ProjectRepository.getInstance().callRetrofitBuilder()
            ?.getTodayImage(
                generateQueryParams()
            )
        call?.enqueue(object : Callback<APODDetail> {
            override fun onResponse(
                call: Call<APODDetail>,
                response: Response<APODDetail>
            ) {
                if (response.isSuccessful) {
                    val apodRes = response.body()
                    CommonUtils.saveObjToPref(apodRes!!, AppConstants.TODAY_APOD_DATA)
                    apoDetailObs.set(apodRes)
                } else {
                    callback.apiError(response)
                    apoDetailObs.set(null)
                }
            }

            override fun onFailure(call: Call<APODDetail>, t: Throwable) {
                apoDetailObs.set(null)
            }
        })
    }

    /**
     * The callAPODListAPI method, to get apod list based on date range
     */
    fun callAPODListAPI(
        view: View
    ) {
        if (CommonUtils.isOnline(view.context)) {
            isProgressDialogVisibile.set(true)
            val call = ProjectRepository.getInstance().callRetrofitBuilder()
                ?.getAPODList(
                    generateListQueryParams()
                )
            call?.enqueue(object : Callback<ArrayList<APODDetail>> {
                override fun onResponse(
                    call: Call<ArrayList<APODDetail>>,
                    response: Response<ArrayList<APODDetail>>
                ) {
                    isProgressDialogVisibile.set(false)
                    if (response.isSuccessful) {
                        val apodRes = response.body()
                        if (apodRes?.size!! > 0) {
                            CommonUtils.saveObjToPref(apodRes, AppConstants.APOD_LIST)
                            isRangeListAvailable.set(true)
                            notifyDataChange(rangeList = apodRes)

                        } else {
                            isRangeListAvailable.set(false)
                        }

                    } else {
                        isRangeListAvailable.set(false)
                        view.context.snackbar(
                            view,
                            response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<ArrayList<APODDetail>>, t: Throwable) {
                    isRangeListAvailable.set(false)
                    isProgressDialogVisibile.set(false)
                }
            })
        } else {
            view.context.snackbar(
                view,
                view.context.getString(R.string.internet_error)
            )
        }
    }


    /** The generateQueryParams method, To generate query parameters for APOD API
     * @return queryParamMap HashMap of params
     */
    private fun generateQueryParams(): HashMap<String, Any> {
        val queryParamMap = HashMap<String, Any>()
        queryParamMap["date"] = getFormattedDate()
        queryParamMap["api_key"] = "DEMO_KEY"
        return queryParamMap
    }

    /** The generateQueryParams method, To generate query parameters for date range APOD API
     * @return queryParamMap HashMap of params
     */
    private fun generateListQueryParams(): HashMap<String, Any> {
        val queryParamMap = HashMap<String, Any>()
        queryParamMap["start_date"] = startDateObs.get().toString()
        queryParamMap["end_date"] = endDateObs.get().toString()
        queryParamMap["api_key"] = "DEMO_KEY"
        return queryParamMap
    }

    /**
     * The openDatePicker dialog, to set start and end date
     */
    fun openDatePicker(editText: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(
            editText.context,
            { view, year, monthOfYear, dayOfMonth ->
                val date: String = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                (editText as AppCompatEditText).setText(
                    getFormattedDate(SimpleDateFormat(AppConstants.DATE_FORMAT).parse(date))
                )
                if (editText.id == R.id.etStartDate) {
                    startDateObs.set(editText.text.toString())
                } else {
                    endDateObs.set(editText.text.toString())
                }
            },
            year,
            month,
            day
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        if (!datePicker.isShowing) {
            datePicker.show()
        }
    }

    /**
     * The getAPODListAdapter Class, to get the instance of adapter class
     */
    fun getAPODListAdapter(): APODListAdapter {
        return apodListAdapter
    }

    /**
     * The notifyDataChange method, to set and notify adapter
     */
    fun notifyDataChange(rangeList: List<APODDetail>) {
        apodListAdapter.setAPODList(rangeList)
        apodListAdapter.notifyDataSetChanged()
    }

    /**
     * The manageFav method, to manage favourite list offline
     */
    fun manageFav(view: View, apodDetail: APODDetail) {
        CommonUtils.getArrayListFromPref(AppConstants.APOD_LIST)
            ?.let { it ->
                val obj = it.single { it.date == apodDetail.date }
                obj.also {
                    if (it.isFav) {
                        it.isFav = false
                        (view as AppCompatImageView).setBackgroundResource(R.drawable.ic_unfav)
                    } else {
                        it.isFav = true
                        (view as AppCompatImageView).setBackgroundResource(R.drawable.ic_fav)
                    }
                }
                CommonUtils.saveObjToPref(it, AppConstants.APOD_LIST)
                notifyDataChange(it)
            }
    }
}