package qnopy.com.qnopyandroid.flowWithAdmin.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.util.AlertManager
import java.io.Serializable

abstract class BaseFragment : Fragment() {

    private lateinit var progressDialog: AlertDialog
    private var activity: AppCompatActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = AlertManager.showQnopyProgressBar(activity, getString(R.string.loading))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity?
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    open fun getBaseActivity(): AppCompatActivity? {
        return activity
    }

    fun showProgress() {
        progressDialog.show()
    }

    fun showProgress(msg: String) {
        progressDialog = AlertManager.showQnopyProgressBar(activity, msg)
        progressDialog.show()
    }

    fun cancelProgress() {
        if (progressDialog.isShowing)
            progressDialog.cancel()
    }

    fun showToast(msg: String, isShortLength: Boolean) {
        Toast.makeText(activity, msg, if (isShortLength) Toast.LENGTH_SHORT else Toast.LENGTH_LONG)
            .show()
    }
}
