package qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl
import qnopy.com.qnopyandroid.util.AlertManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FetchReportsById(
    private val activity: AppCompatActivity,
    private val formId: String,
    private val listener: FetchReportsByIdResponseListener,
    private val isEmailMyself: Boolean
) {

    private var progressBar: AlertDialog =
        AlertManager.showQnopyProgressBar(activity, activity.getString(R.string.loading))

    private val aquaBlueService: AquaBlueServiceImpl = AquaBlueServiceImpl(activity)

    var executor: ExecutorService = Executors.newSingleThreadExecutor()
    var handler: Handler = Handler(Looper.getMainLooper())

    fun fetchReportNamesList() {
        executor.execute {
            handler.post { progressBar.show() }

            val reportsResponse = aquaBlueService.getAllReportsById(
                activity.getString(R.string.prod_base_uri),
                activity.getString(R.string.get_reports_by_id),
                formId
            )

            handler.post {

                if (reportsResponse.success
                    && reportsResponse.responseCode == HttpStatus.OK.reasonPhrase
                    && reportsResponse.data.isNotEmpty()
                ) {
                    listener.onGetReportsSuccess(reportsResponse, isEmailMyself)
                } else {
                    val msg = if (!reportsResponse.message.isNullOrEmpty()) reportsResponse.message
                    else activity.getString(
                        R.string.something_went_wrong
                    )
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()

                    listener.onGetReportsFailed(msg)
                }

                if (progressBar.isShowing) progressBar.cancel()
            }
        }
    }

    interface FetchReportsByIdResponseListener {
        fun onGetReportsSuccess(response: FetchAllReportByIdResponse, isEmailMyself: Boolean)

        fun onGetReportsFailed(msg: String)
    }
}