package com.example.gsdemo.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.gsdemo.R
import com.example.gsdemo.databinding.ActivityMainBinding
import com.example.gsdemo.model.APODDetail
import com.example.gsdemo.utils.APICallback
import com.example.gsdemo.utils.AppConstants
import com.example.gsdemo.utils.CommonUtils
import com.example.gsdemo.utils.CommonUtils.getTodayAPODData
import com.example.gsdemo.utils.snackbar
import com.example.gsdemo.viewmodel.MainViewModel
import retrofit2.Response

/**
 * The MainActivity class, to show today's image and filtered list based on date range
 */
class MainActivity : AppCompatActivity() {
    /**
     * The mainViewModel, variable of MainViewModel
     */
    private lateinit var mainViewModel: MainViewModel

    /**
     * The binding, variable of DataBinding
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * The activity, variable of MainActivity class
     */
    private lateinit var activity: MainActivity

    /**
     * The isFilterDialogOpen , Boolean flag to check filter dialog is open or not
     */
    private var isFilterDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewModelProvider(this)[MainViewModel::class.java].also { mainViewModel = it }
        binding.mainViewModel = mainViewModel

        setUpToolbar()
        setUpListener()
        setInitialData()
    }

    /**
     * The setUpToolbar method, to set toolbar
     */
    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    /**
     * The setUpListener method, to hide filter dialog on framelayout click
     */
    private fun setUpListener() {
        binding.flMain.setOnClickListener {
            if (isFilterDialogOpen) {
                closeDialog()
            }
        }
        binding.ivFilter.setOnClickListener {
            if (isFilterDialogOpen) {
                closeDialog()
            } else {
                openDialog()
            }
        }
    }

    /**
     * The setInitialData method, to show data based on online/offline condition
     */
    private fun setInitialData() {
        if (CommonUtils.isOnline(activity)) {
            mainViewModel.callTodayAPODAPI(object : APICallback {
                override fun apiError(response: Any) {
                    activity.snackbar(binding.root, (response as Response<*>).message())
                }
            })
            showOfflineAPODList()
        } else {
            val data = getTodayAPODData()
            data.let {
                mainViewModel.apoDetailObs.set(it)
            }
            showOfflineAPODList()
            if (data == null && showOfflineAPODList() == null) {
                activity.snackbar(binding.root, getString(R.string.no_data_error))
            }

        }
    }

    /**
     * The showOfflineAPODList method, to show filtered list in offline
     */
    private fun showOfflineAPODList(): List<APODDetail>? {
        val offlineList = CommonUtils.getArrayListFromPref(AppConstants.APOD_LIST)
        offlineList
            ?.let {
                mainViewModel.notifyDataChange(it)
                mainViewModel.isRangeListAvailable.set(true)
            }
        return offlineList
    }

    /**
     * The openDialog method, to set animation
     */
    private fun openDialog() {
        val dialogAnim: Animation = AnimationUtils.loadAnimation(
            this,
            R.anim.bottom_up
        )
        isFilterDialogOpen = true
        binding.llFilterMain.startAnimation(dialogAnim)
        binding.flMain.visibility = View.VISIBLE
    }

    /**
     * The closeDialog method, to set animation
     */
    private fun closeDialog() {
        val dialogAnim: Animation = AnimationUtils.loadAnimation(
            this,
            R.anim.bottom_down
        )
        isFilterDialogOpen = false
        binding.llFilterMain.startAnimation(dialogAnim)
        binding.flMain.visibility = View.GONE
    }

}