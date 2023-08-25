package qnopy.com.qnopyandroid.flowWithAdmin.utility

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.ui.task.TasksTabFragment

const val DAYS_FOR_FLEXIBLE_UPDATE: Int = 10

class InAppUpdate(private val activity: AppCompatActivity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                //you have an app update. Request for update here
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    GlobalStrings.IN_APP_UPDATE_REQUEST_CODE
                )
//            }
            }
        }
    }
}