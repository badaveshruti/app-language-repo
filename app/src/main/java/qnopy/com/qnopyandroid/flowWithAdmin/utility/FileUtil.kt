package qnopy.com.qnopyandroid.flowWithAdmin.utility

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import qnopy.com.qnopyandroid.BuildConfig
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.util.FileUtils
import qnopy.com.qnopyandroid.util.Util
import java.io.*
import java.util.*

object FileUtil {
    val FILETYPE_PDF = ".pdf"
    val FILETYPE_DOCX = ".docx"
    val FILETYPE_JSON = ".json"

    fun getFileByName(context: Context, fileName: String) =
        File(context.filesDir.absolutePath + "/" + fileName)

    fun isFileExists(context: Context, fileName: String): Boolean =
        getFileByName(context, fileName).exists()

    fun openFile(context: Context, file: File) {
        val map = MimeTypeMap.getSingleton()
        val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
        val type = map.getMimeTypeFromExtension(ext)

        val data =
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )


        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(data, type)

        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val pm = context.packageManager
        val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else
            pm.queryIntentActivities(intent, 0)


        if (resolveInfoList.size > 0) {
            context.startActivity(intent)
        } else {
            val builder = AlertDialog.Builder(context, R.style.dialogStyle)
            builder.setNeutralButton(context.getString(R.string.ok), null)
            val message: String =
                if (Locale.getDefault().language.contains("en"))
                    "No suitable application installed on your device to view this(.$ext) file."
                else
                    context.getString(R.string.no_app_installed_to_view_this_file)

            builder.setMessage(message)
            builder.setTitle("Oops!")
            val dialog = builder.create()
            dialog.show()
        }
    }

    fun saveFileToInternalStorage(context: Context, fileName: String, byteArray: ByteArray): File? {
        try {
            val fos = context.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)
            fos.write(byteArray)
            fos.flush()
            fos.close()
            return getFileByName(context, fileName)

        } catch (e: Exception) {
            Log.e("**", "saveFileToInternalStorage: FAILED $e")
        }
        return null
    }

    fun exportCSVFile(context: Context, csvData: String) {
        try {
            val fileName = "qnopy_sheet_" + System.currentTimeMillis() + ".csv"
            val outputStream: FileOutputStream =
                context.openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream.write(csvData.toByteArray())
            outputStream.close()
            val fileLocation: File = File(context.getFilesDir(), fileName)
            val contentUri =
                FileProvider.getUriForFile(context, "com.aqua.fieldbuddy.provider", fileLocation)
            val csvIntent = Intent(Intent.ACTION_VIEW)
            csvIntent.setDataAndType(contentUri, "text/csv")
            csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            csvIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val manager: PackageManager = context.packageManager
            val infos = manager.queryIntentActivities(csvIntent, 0)
            if (infos.size > 0) {
                context.startActivity(
                    Intent.createChooser(
                        csvIntent,
                        context.getString(R.string.choose_app_to_open)
                    )
                )
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.you_may_not_have_proper_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun copyDataBase(context: Context, uri: Uri?) {
        val fileDb = File(FileUtils.getRealPath(context, uri))
        if (fileDb.name != "aqua") {
            Toast.makeText(
                context,
                context.getString(R.string.choose_valid_db_file),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Open your local db as the input stream
        val fileInputStream = FileInputStream(fileDb)
        Log.d("Path", "" + FileUtils.getRealPath(context, uri))
        // Path to the just created empty db
        val outFileName =
            Util.getBaseContextPath(context) + GlobalStrings.DB_PATH + GlobalStrings.DATABASE_NAME

        // Open the empty db as the output stream
        val myOutput: OutputStream = FileOutputStream(outFileName)

        // transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        while (fileInputStream.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }

        // Close the streams
        myOutput.flush()
        myOutput.close()
        fileInputStream.close()
    }
}