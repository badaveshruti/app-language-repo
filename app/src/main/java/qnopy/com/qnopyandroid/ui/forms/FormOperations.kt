package qnopy.com.qnopyandroid.ui.forms

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import qnopy.com.qnopyandroid.ExpressionParser.Parser
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.FieldData
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.customView.CustomEditText
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.db.AttachmentDataSource
import qnopy.com.qnopyandroid.db.CocDetailDataSource
import qnopy.com.qnopyandroid.db.DefaultValueDataSource
import qnopy.com.qnopyandroid.db.FieldDataSource
import qnopy.com.qnopyandroid.db.LocationDataSource
import qnopy.com.qnopyandroid.db.LovDataSource
import qnopy.com.qnopyandroid.db.MetaDataAttributesDataSource
import qnopy.com.qnopyandroid.db.MetaDataSource
import qnopy.com.qnopyandroid.db.MobileAppDataSource
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource
import qnopy.com.qnopyandroid.requestmodel.SCocDetails
import qnopy.com.qnopyandroid.signature.CaptureSignature
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity
import qnopy.com.qnopyandroid.ui.activity.MapDragActivity
import qnopy.com.qnopyandroid.ui.activity.NoteDialogBoxActivity
import qnopy.com.qnopyandroid.ui.forms.viewHolders.NumericLast2ViewHolder
import qnopy.com.qnopyandroid.ui.forms.viewHolders.NumericLast3ViewHolder
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity
import qnopy.com.qnopyandroid.ui.task.TaskIntentData
import qnopy.com.qnopyandroid.ui.task.TaskTabActivity
import qnopy.com.qnopyandroid.ui.weather.WeatherActivity
import qnopy.com.qnopyandroid.uiutils.FormMaster
import qnopy.com.qnopyandroid.uiutils.QRScannerActivity
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val TAG = "FormOperations"

//Pending work:

class FormOperations(
    val context: LocationDetailActivity,
    val formActivity: FormActivity,
    var metaDataList: List<MetaData>,
    var siteId: String,
    var locationId: String,
    var eventId: String,
    var parentFormId: String,
    val formsAdapter: FormsAdapter
) {

    private var sampleMapTagDataSource: SampleMapTagDataSource
    private var attachmentDataSource: AttachmentDataSource
    private var metaDataSource: MetaDataSource
    private var lovDataSource: LovDataSource
    private var metaDataAttrSource: MetaDataAttributesDataSource
    private var attachDataSource: AttachmentDataSource
    private var fieldDataSource: FieldDataSource
    private var companyId: Int = 0
    private var userId: String? = ""
    private var deviceId: String? = ""
    private var siteName: String? = ""
    var decimalPlaces = 0
    var STORE_VALUE = false

    init {
        userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID)
        companyId = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).toInt()
        deviceId = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID)
        siteName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME)

        fieldDataSource = FieldDataSource(context)
        attachDataSource = AttachmentDataSource(context)
        metaDataAttrSource = MetaDataAttributesDataSource(context)
        lovDataSource = LovDataSource(context)
        metaDataSource = MetaDataSource(context)
        attachmentDataSource = AttachmentDataSource(context)
        sampleMapTagDataSource = SampleMapTagDataSource(context)
    }

    fun getCurCocId(): String? {
        return formActivity.currCocID
    }

    fun getLocId(): String {
        return locationId
    }

    fun setIfSampleDateOrTimeSet(isSampleDateTimeSet: Boolean) {
        formsAdapter.isSampleDateOrTimeSet = isSampleDateTimeSet
    }

    fun handleDateAndTimeData(metaData: MetaData, tvLabel: CustomTextView) {
        //03-Jul-17 UPDATE DATE AND TIME
        if (!fieldDataSource.isParamIdExists(
                formActivity.curSetID, eventId.toInt(),
                locationId, siteId.toInt(), metaData.currentFormID,
                "", userId!!.toInt(), metaData.metaParamID
            )
        ) {
            val bFieldData: List<FieldData> =
                getBlankFieldData(
                    metaData, formActivity.curSetID
                )
            fieldDataSource.insertFieldDataList(bFieldData, userId!!.toInt(), deviceId + "")
        }

        if (metaData.metaParamID == 25) {
            fieldDataSource.updateDateInExt2(
                eventId.toInt(),
                formActivity.curSetID,
                locationId,
                siteId.toInt(),
                metaData.currentFormID,
                metaData.currentReading
            )
        }

        if (metaData.metaParamID == 15) {
            fieldDataSource.updateTimeInExt3(
                eventId.toInt(),
                formActivity.curSetID,
                locationId,
                siteId.toInt(),
                metaData.currentFormID,
                metaData.currentReading
            )
        }

        saveDataAndUpdateCreationDate(metaData)
        checkWarningValueForViolation(metaData)

        setMandatoryFieldAlert(metaData, tvLabel)

        if (hasExtField2ActionCalculated(metaData)) {
            //Todo find a way to refresh adapter to run calculations
            //causing error as recycler is not initialised fully
            //            formsAdapter.notifyDataSetChanged()
        }
    }

    fun hasExtField2ActionCalculated(metaData: MetaData): Boolean {
        var extField2 = metaData.extField2

        if (metaData.fieldAction != null
            && metaData.fieldAction.isNotEmpty()
        ) extField2 = metaData.fieldAction

        val attributes = metaDataAttrSource.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )

        if (attributes != null)
            if (attributes.ext_field2 != null
                && attributes.ext_field2.isNotEmpty()
            ) extField2 = attributes.ext_field2

        return extField2 != null && extField2.isNotEmpty()
                && extField2.equals(GlobalStrings.KEY_CALCULATED, ignoreCase = true)
    }

    //thread managed
    fun setMandatoryFieldAlert(metaData: MetaData, tvLabel: CustomTextView) {

        var mandatoryField = metaData.getMandatoryField()

        val attributes = metaDataAttrSource.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )

        if (attributes != null)
            if (attributes.mandatoryField != 0) mandatoryField =
                attributes.mandatoryField.toString()

        scopeMainThread.launch {
            if (mandatoryField.equals("2", ignoreCase = true)
                && metaData.currentReading != null && metaData.currentReading.isNotEmpty()
            ) {
                tvLabel.setTextColor(ContextCompat.getColor(context, R.color.color_chooser_green))
                formActivity.reqFieldCount--
            } else if (mandatoryField.equals("2", ignoreCase = true)
                && (metaData.currentReading == null || metaData.currentReading.isEmpty())
            ) {
                tvLabel.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.required_background_red1
                    )
                )
                formActivity.reqFieldCount++
            }
        }
    }

    //bg thread
    fun checkWarningValueForViolation(metaData: MetaData) {
        val defaultValueDataSource = DefaultValueDataSource(formActivity)
        val defValue = defaultValueDataSource.getDefaultValueToWarn(
            locationId,
            formActivity.currentAppID.toString(),
            metaData.metaParamID.toString(), formActivity.curSetID.toString()
        )
        if (defValue != null) {
            val warningValue = defValue.warningValue
            val fieldDataSource = FieldDataSource(formActivity)
            if (warningValue != null && warningValue.isNotEmpty()) {
                var flag = "0"
                if (metaData.currentReading != null && metaData.currentReading == warningValue) {
                    //add save and update method if necessary
                    flag = "1"
                }
                fieldDataSource.updateViolationFlag(
                    eventId,
                    metaData.metaParamID,
                    formActivity.curSetID,
                    locationId,
                    flag,
                    siteId.toInt(),
                    formActivity.currentAppID
                )
            }
        }
    }

    fun getBlankFieldData(metaData: MetaData, setId: Int): List<FieldData> {
        val flist: MutableList<FieldData> = ArrayList()
        try {
            val fieldData = FieldData()

            fieldData.stringValue = null
            fieldData.fieldParameterID = metaData.metaParamID
            fieldData.fieldParameterLabel = metaData.metaParamLabel

            fieldData.creationDate = 0
            fieldData.locationID = locationId
            fieldData.eventID = eventId.toInt()
            val fieldDataSource = FieldDataSource(context)
            val ext2 = fieldDataSource.getExt2ForMobileApp(
                metaData.currentFormID,
                eventId.toInt(),
                siteId.toInt(),
                locationId,
                setId
            )
            val ext3 = fieldDataSource.getExt3ForMobileApp(
                metaData.currentFormID,
                eventId.toInt(),
                siteId.toInt(),
                locationId,
                setId
            )
            fieldData.extField2 = ext2
            fieldData.extField3 = ext3
            fieldData.units = metaData.DesiredUnits
            fieldData.setCurSetID(setId)
            fieldData.extField4 = "0"
            fieldData.siteID = siteId.toInt()
            fieldData.userID = userId!!.toInt()
            fieldData.mobileAppID = metaData.currentFormID
            fieldData.deviceId = deviceId
            flist.add(fieldData)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return flist
    }

    fun fetchEssentialData(metaData: MetaData) {
        metaData.currentReading = fieldDataSource.getPreviousReading(
            eventId.toInt(), formActivity.curSetID,
            locationId, siteId.toInt(), formActivity.currentAppID,
            metaData.metaParamID, ""
        )

        try {
            if (formActivity.curSetID - 2 >= 1) {
                metaData.prevReading1 = fieldDataSource.getPreviousReading(
                    eventId.toInt(),
                    formActivity.curSetID - 2, locationId,
                    siteId.toInt(), formActivity.currentAppID,
                    metaData.metaParamID, ""
                )
                if (metaData.prevReading1 == null) { // to be able to put NR in
                    // prev reading1
                    metaData.prevReading1 = ""
                }
            } else {
                metaData.prevReading1 = null
            }

            if (formActivity.curSetID - 1 >= 1) {
                metaData.prevReading2 = fieldDataSource.getPreviousReading(
                    eventId.toInt(),
                    formActivity.curSetID - 1, locationId,
                    siteId.toInt(), formActivity.currentAppID,
                    metaData.metaParamID, ""
                )
                if (metaData.prevReading2 == null) { // to be able to put NR in
                    // prev reading2
                    metaData.prevReading2 = ""
                }
            } else {
                metaData.prevReading2 = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        metaData.isHasNote = fieldDataSource.isNoteTaken_Data(
            eventId.toInt(), formActivity.curSetID,
            locationId, siteId.toInt(), formActivity.currentAppID,
            metaData.metaParamID
        )

        if (!metaData.isHasNote) {
            // in case no note - CHECK IN ATTACHMENT
            metaData.isHasNote = attachDataSource.isAttachNoteExists(
                eventId,
                locationId,
                siteId,
                formActivity.currentAppID.toString(),
                formActivity.curSetID.toString(),
                metaData.metaParamID.toString()
            )
        }
    }

    //can work on background thread
    fun saveDataAndUpdateCreationDate(metaData: MetaData) {
        scopeIO.launch {
            val fieldDataSource = FieldDataSource(context)
            fieldDataSource.updateValue(
                eventId.toInt(), metaData.metaParamID, formActivity.curSetID,
                locationId, metaData.currentReading, siteId.toInt(), metaData.currentFormID,
                GlobalStrings.CURRENT_GPS_LOCATION,
                deviceId, userId
            )

            val creationDate = System.currentTimeMillis()
            val measurementTime = fieldDataSource.getMeasurementTime(
                eventId.toInt(),
                locationId,
                metaData.currentFormID.toString(),
                formActivity.curSetID,
                "25",
                siteId.toInt()
            )
            val oldCreationDate = fieldDataSource.getCreationDateForMobileApp(
                metaData.currentFormID,
                eventId.toInt(),
                siteId.toInt(),
                locationId,
                userId!!.toInt(),
                formActivity.curSetID
            )
            if (oldCreationDate != null) {
                fieldDataSource.updateCreationDate(
                    eventId.toInt(), formActivity.curSetID,
                    locationId, siteId.toInt(), metaData.currentFormID, oldCreationDate.toLong()
                )
            } else {
                fieldDataSource.updateCreationDate(
                    eventId.toInt(), formActivity.curSetID,
                    locationId, siteId.toInt(), metaData.currentFormID, creationDate
                )
            }

            if (metaData.metaInputType.equals("GPS", ignoreCase = true)) {
                if (metaData.currentReading != null && metaData.currentReading.isNotEmpty()) {
                    val correctedLatLng =
                        metaData.currentReading.split(",".toRegex()).toTypedArray()
                    fieldDataSource.updateCorrectedLatLong(
                        eventId.toInt(),
                        formActivity.curSetID,
                        locationId,
                        siteId.toInt(),
                        metaData.currentFormID,
                        correctedLatLng[0].toDouble(),
                        correctedLatLng[1].toDouble()
                    )
                }
            }
            if (measurementTime == null || measurementTime == 0L) fieldDataSource.updateMeasurementTime(
                eventId.toInt(), formActivity.curSetID,
                locationId, siteId.toInt(), metaData.currentFormID,
                creationDate
            ) else fieldDataSource.updateMeasurementTime(
                eventId.toInt(), formActivity.curSetID,
                locationId, siteId.toInt(), metaData.currentFormID,
                measurementTime
            )
        }
    }

    fun handleLast3ReadingAndPercentage(
        metaData: MetaData,
        holder: NumericLast3ViewHolder
    ) {
        val prevRead = getPrevReadingForShowLast3(metaData)
        holder.tvLast2Readings.text = prevRead

        var maxOfLast3 = 0.0f
        var minOfLast3 = 0.0f
        val colorOrange = -0xa6e9f
        val colorGreen = -0x63479b
        val prevReadingsArray: List<String>

        val attributes = metaDataAttrSource.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )

        var straightDiff = 0.0
        var percentDiff = 0.0

        if (attributes != null) {
            straightDiff = attributes.straightDifference
            percentDiff = attributes.percentDifference
        } else {
            if (metaData.straightDifference != null) straightDiff =
                metaData.straightDifference.toDouble()
            percentDiff = metaData.percentDifference
        }

        if (!prevRead.isNullOrEmpty()) {
            prevReadingsArray = listOf(*prevRead.split(",").toTypedArray())
            if (!prevRead.contains("NR") && prevReadingsArray.isNotEmpty()) {
                if (prevReadingsArray.size == 3) {

                    maxOfLast3 = max(
                        max(
                            prevReadingsArray[0].toFloat(),
                            prevReadingsArray[1].toFloat()
                        ), prevReadingsArray[2].toFloat()
                    )

                    minOfLast3 = min(
                        min(
                            prevReadingsArray[0].toFloat(),
                            prevReadingsArray[1].toFloat()
                        ), prevReadingsArray[2].toFloat()
                    )
                }
            }
        }

        setCalculatedFieldParams(metaData, holder.edtNumeric, holder.tvNumericLabel)

        if (minOfLast3 == 0f && maxOfLast3 == 0f) {
            holder.tvPrevPercent1.text = ""
            holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT)
            return
        }

        if (straightDiff != 0.0) {
            var difference = 0.0.toFloat()

            try {
                difference = FormMaster.calcLast3Difference(
                    maxOfLast3,
                    minOfLast3
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            difference = (Math.round(difference * 100.00) / 100.00).toFloat()
            difference = FormMaster.round(difference, 2)

            holder.tvPrevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            holder.tvPrevPercent1.text = "$difference"
            try {
                if (difference <= straightDiff.toFloat()) {
                    holder.tvPrevPercent1.setBackgroundColor(colorGreen)
                } else {
                    holder.tvPrevPercent1.setBackgroundColor(colorOrange)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        } else if (percentDiff != 0.0) {
            var percent = 0.0f

            try {
                percent = FormMaster.calcLast3Percentage(
                    maxOfLast3,
                    minOfLast3
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            percent = (Math.round(percent * 100.00) / 100.00).toFloat()
            percent = FormMaster.round(percent, 2)
            holder.tvPrevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            holder.tvPrevPercent1.text = "$percent%"
            try {
                if (percent <= percentDiff) {
                    holder.tvPrevPercent1.setBackgroundColor(colorGreen)
                } else {
                    holder.tvPrevPercent1.setBackgroundColor(colorOrange)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.tvPrevPercent1.text = ""
            holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    fun handleLast2ReadingAndPercentage(
        metaData: MetaData,
        holder: NumericLast2ViewHolder
    ) {
        val defaultValue = metaData.defaultValue
        var percent = 0f
        val colorOrange = -0xa6e9f
        val colorGreen = -0x63479b

        val attributes = metaDataAttrSource.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )

        setCalculatedFieldParams(metaData, holder.edtNumeric, holder.tvNumericLabel)
        percent = 0.0.toFloat()
        if (!metaData.prevReading1.isNullOrEmpty() && !metaData.prevReading2.isNullOrEmpty()) {
            val extField3 = metaData.straightDifference

            /*            if (attributes != null) if (!attributes.ext_field3.isNullOrEmpty())
                            extField3 = attributes.ext_field3*/

            if (!extField3.isNullOrEmpty()) {
                try {
                    percent = FormMaster.calcDifference(
                        metaData.prevReading1.toFloat(),
                        metaData.prevReading2.toFloat()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i(TAG, "Exception :" + e.message)
                }

                percent = (Math.round(percent * 100.00) / 100.00).toFloat()
//                tvPrevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                var percentDifference = metaData.percentDifference
                if (attributes != null) if (attributes.percentDifference != 0.0) percentDifference =
                    attributes.percentDifference
                if (percentDifference != 0.0) { //display % diff only if %diff is not null
                    //								tvPrevPercent1.setText(Float.toString(percent)+"%");
                    if (percent != 0f) {
                        try {
                            //not tampering percent original value
                            val newPercent =
                                getLocaleFormattedString(
                                    percent.toString(),
                                    Locale.getDefault()
                                )
                            holder.tvPrevPercent1.text = newPercent
                        } catch (e: Exception) {
                            e.printStackTrace()
                            holder.tvPrevPercent1.text = percent.toString()
                        }
                    } else {
                        holder.tvPrevPercent1.text = percent.toString()
                    }
                    try {
                        if (abs(percent) <= extField3.toFloat()) {
                            holder.tvPrevPercent1.setBackgroundColor(colorGreen)
                        } else {
                            holder.tvPrevPercent1.setBackgroundColor(colorOrange)
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        holder.tvPrevPercent1.setBackgroundColor(colorOrange)
                    }
                } else {
                    holder.tvPrevPercent1.text = ""
                    holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT)
                }
            } else {
                try {
                    percent = FormMaster.calcPercent(
                        metaData.prevReading1.toFloat(),
                        metaData.prevReading2.toFloat()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                holder.tvPrevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                percent = FormMaster.round(percent, 1)
                var percentDifference = metaData.percentDifference

                if (attributes != null) if (attributes.percentDifference != 0.0) percentDifference =
                    attributes.percentDifference

                if (percentDifference != 0.0) {
                    if (percent != 0f) {
                        try {
                            //not tampering percent original value
                            val newPercent = getLocaleFormattedString(
                                percent.toString(),
                                Locale.getDefault()
                            ) + "%"
                            holder.tvPrevPercent1.text = newPercent
                        } catch (e: Exception) {
                            e.printStackTrace()
                            holder.tvPrevPercent1.text = "$percent%"
                        }
                    } else holder.tvPrevPercent1.text = "$percent%"

                    if (abs(percent) <= percentDifference) {
                        holder.tvPrevPercent1.setBackgroundColor(colorGreen)
                    } else {
                        holder.tvPrevPercent1.setBackgroundColor(colorOrange)
                    }
                } else {
                    holder.tvPrevPercent1.text = ""
                    holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        } else {
            holder.tvPrevPercent1.text = ""
            holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT)
        }

        if (!metaData.currentReading.isNullOrEmpty()) {
            if (!metaData.prevReading2.isNullOrEmpty()) {
                percent = 0.toFloat()

                val extField3 = metaData.straightDifference
                //commented on 30 June, 22 - as we are not putting any values to attributes for ext3 or straightDiff
                /*                if (attributes != null) if (attributes.ext_field3 != null && attributes.ext_field3
                                        .isNotEmpty()
                                ) extField3 = attributes.ext_field3*/

                if (extField3 != null && !TextUtils.isEmpty(extField3)) {
                    try {
                        //	percent = calcPercent(Float.parseFloat(metaData.prevReading2), Float.parseFloat(value));
                        percent = FormMaster.calcDifference(
                            metaData.prevReading2.toFloat(),
                            metaData.currentReading.toFloat()
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    percent = (Math.round(percent * 100.00) / 100.00).toFloat()
//                    tvPrevPercent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    var percentDifference = metaData.percentDifference
                    if (attributes != null) if (attributes.percentDifference != 0.0) percentDifference =
                        attributes.percentDifference
                    Log.i(TAG, "percent $percentDifference")
                    if (percentDifference != 0.0) {
                        //tvPrevPercent2.setText(Float.toString(percent)+"%");
                        if (percent != 0f) {
                            try {
                                //not tampering percent original value
                                val newPercent = getLocaleFormattedString(
                                    percent.toString(),
                                    Locale.getDefault()
                                )
                                holder.tvPrevPercent2.text = newPercent
                            } catch (e: Exception) {
                                e.printStackTrace()
                                holder.tvPrevPercent2.text = percent.toString()
                            }
                        } else holder.tvPrevPercent2.text = percent.toString()

                        //								if (Math.abs(percent) <= 10) {
                        try {
                            if (Math.abs(percent) <= extField3.toFloat()) {
                                holder.tvPrevPercent2.setBackgroundColor(colorGreen)
                            } else {
                                holder.tvPrevPercent2.setBackgroundColor(colorOrange)
                            }
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                            holder.tvPrevPercent2.setBackgroundColor(colorOrange)
                        }
                    } else {
                        holder.tvPrevPercent2.text = ""
                        holder.tvPrevPercent2.setBackgroundColor(Color.TRANSPARENT)
                    }
                } else {
                    try {
                        percent = FormMaster.calcPercent(
                            metaData.prevReading2.toFloat(),
                            metaData.currentReading.toFloat()
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    percent = (Math.round(percent * 100.00) / 100.00).toFloat()
//                    holder.tvPrevPercent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    percent = FormMaster.round(percent, 1)
                    var percentDifference = metaData.percentDifference

                    if (attributes != null) if (attributes.percentDifference != 0.0) percentDifference =
                        attributes.percentDifference

                    if (percentDifference != 0.0) {
//                        holder.tvPrevPercent2.width = 30
                        if (percent != 0f) {
                            try {
                                //not tampering percent original value
                                val newPercent = getLocaleFormattedString(
                                    percent.toString(),
                                    Locale.getDefault()
                                ) + "%"
                                holder.tvPrevPercent2.text = newPercent
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                holder.tvPrevPercent2.text = "$percent%"
                            }
                        } else holder.tvPrevPercent2.text = "$percent%"
                        if (Math.abs(percent) <= percentDifference) {
                            holder.tvPrevPercent2.setBackgroundColor(colorGreen)
                        } else {
                            holder.tvPrevPercent2.setBackgroundColor(colorOrange)
                        }
                    }
                }
            } else {
                holder.tvPrevPercent2.text = ""
                holder.tvPrevPercent2.setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            /*            holder.tvPrevPercent1.setText("");
                        holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT);*/
            holder.tvPrevPercent2.text = ""
            holder.tvPrevPercent2.setBackgroundColor(Color.TRANSPARENT)
        }
        val prevStr = getPrevReadDispString(metaData)
        holder.tvLast2Readings.text = prevStr

        if (formActivity.curSetID == 1) {
            holder.tvLast2Readings.text =
                " " //do not know why some value is getting set when curSetID=1
            holder.tvPrevPercent1.text = ""
            holder.tvPrevPercent1.setBackgroundColor(Color.TRANSPARENT)
            holder.tvPrevPercent2.text = ""
            holder.tvPrevPercent2.setBackgroundColor(Color.TRANSPARENT)
        }
        setMandatoryFieldAlert(metaData, holder.tvNumericLabel)
    }

    fun getPrevReadingForShowLast3(metaData: MetaData): String {
        val prev3Readings = StringBuilder()
        if (formActivity.curSetID >= 3) {
            val fieldDataSource = FieldDataSource(context)
            var count = 3
            for (setId in formActivity.curSetID downTo 0) {
                val prevValue = fieldDataSource.getPreviousReading(
                    formActivity.eventID, setId,
                    formActivity.locationID, formActivity.siteID, formActivity.currentAppID,
                    metaData.metaParamID, ""
                )
                if (count == 3) {
                    if (prevValue != null && prevValue.isNotEmpty()) prev3Readings.append(
                        prevValue
                    )
                    else prev3Readings.append(
                        "NR"
                    )
                } else if (count == 2) {
                    val value2 = prev3Readings.toString()
                    prev3Readings.setLength(0)
                    if (prevValue != null && prevValue.isNotEmpty()) {
                        prev3Readings.append(prevValue)
                        prev3Readings.append(", ")
                        prev3Readings.append(value2)
                    } else {
                        prev3Readings.append("NR")
                        prev3Readings.append(", ")
                        prev3Readings.append(value2)
                    }
                } else if (count == 1) {
                    val value2 = prev3Readings.toString()
                    prev3Readings.setLength(0)
                    if (prevValue != null && prevValue.isNotEmpty()) {
                        prev3Readings.append(prevValue)
                        prev3Readings.append(", ")
                        prev3Readings.append(value2)
                    } else {
                        prev3Readings.append("NR")
                        prev3Readings.append(", ")
                        prev3Readings.append(value2)
                    }
                }
                count--
                if (count == 0) break
            }
        }
        return prev3Readings.toString()
    }

    fun getPrevReadDispString(metaData: MetaData): String {
        var prevReadDisp = ""
        if (metaData.prevReading1 != null) {
            prevReadDisp += if (metaData.prevReading1.isNotEmpty()) {
                val prevReading1 =
                    getLocaleFormattedString(metaData.prevReading1, Locale.getDefault())
                prevReading1
            } else {
                "NR"
            }
        } else {
            Log.i(TAG, "1l is null")
        }
        if (metaData.prevReading2 != null) {
            if (prevReadDisp.isNotEmpty()) { // do not add , if prevreding1 is ""
                prevReadDisp += ", "
            }
            prevReadDisp += if (metaData.prevReading2.isNotEmpty()) {
                val prevReading2 =
                    getLocaleFormattedString(metaData.prevReading2, Locale.getDefault())
                prevReading2
            } else {
                "NR"
            }
        } else {
            Log.i(TAG, "2l is null")
        }
        return prevReadDisp
    }

    fun isShowLast3(metaData: MetaData): Boolean {
        var fieldAction = ""
        if (metaData.extField2 != null
            && metaData.extField2.isNotEmpty()
        ) fieldAction = metaData.extField2
        if (metaData.fieldAction != null
            && metaData.fieldAction.isNotEmpty()
        ) fieldAction = metaData.fieldAction
        return fieldAction == GlobalStrings.KEY_SHOW_LAST3
    }

    fun isShowLast2(metaData: MetaData): Boolean {

        var isShowLast2 = metaData.isIsShowLast2

        val source = MetaDataAttributesDataSource(context)
        val attributes = source.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )
        if (attributes != null) if (attributes.isShowLast2) isShowLast2 = attributes.isShowLast2

        return isShowLast2
    }

    //bg thread
    fun handleTextData(metaData: MetaData, tvLabel: CustomTextView) {

        if (metaData.metaInputType.equals("MULTIMETHODS", ignoreCase = true)) {
            if (metaData.currentReading != null) {
                if (metaData.currentReading.contains("|null")) {
                    metaData.currentReading = metaData.currentReading
                        .replace("|null", "")
                } else if (metaData.currentReading.contains("null")) {
                    metaData.currentReading = metaData.currentReading
                        .replace("null", "")
                }
            }
        } else {
            if (metaData.currentReading != null) {
                if (metaData.currentReading.contains(",null")) {
                    metaData.currentReading = metaData.currentReading
                        .replace(",null", "")
                } else if (metaData.currentReading.contains("null")) {
                    metaData.currentReading = metaData.currentReading
                        .replace("null", "")
                }
            }
        }

        if (!fieldDataSource.isParamIdExists(
                formActivity.curSetID, eventId.toInt(),
                locationId, siteId.toInt(), metaData.currentFormID,
                deviceId, userId!!.toInt(), metaData.metaParamID
            )
        ) {
            val bFieldData = getBlankFieldData(metaData, formActivity.curSetID)
            fieldDataSource.insertFieldDataList(bFieldData, userId!!.toInt(), deviceId)
        }

        if (!metaData.metaInputType.equals("CHECKBOX")) {
            saveDataAndUpdateCreationDate(metaData)
        } else {
            /*val checkOptions = fieldDataSource.getBottleCheckOptions(
                        eventId,
                        formActivity.curSetID, locationId, siteId,
                        formActivity.currentAppID, metaData.metaParamID
                    )

                    val bottleLabels = getSeparatedBottlesStringValues (metaData);

                    StringBuilder labelValue = new StringBuilder();
                    for (String bottle : bottleLabels.keySet()) {
                        for (String label : checkOptions) {
                        if (label.trim().equals(bottle.trim())) {
                            if (!labelValue.toString().isEmpty()) {
                                labelValue.append("|");
                            }
                            labelValue.append(bottle);
                        }
                    }
                    }

                    if (metaData.getForm_field_row() != null) {
                        ViewHolder viewholder =(ViewHolder) metaData . getForm_field_row ().getTag();

                        if (labelValue.toString().isEmpty() && checkOptions.size() == 0)
                            viewholder.llCocBottlesCheckOptions.removeAllViews();
                    }*/

            saveDataAndUpdateCreationDate(metaData)
        }

        setMandatoryFieldAlert(metaData, tvLabel)
    }

    //bg thread
    fun calculateSetExpression(metaData: MetaData): String? {
        val operand = getExpressionFromMetaOrAttribute(metaData)

        if (operand == null || operand.isEmpty()) return ""

        val exprArray = Util.splitStringToArray("~", operand)

        var methodNames: String? = ""

        for (expression in exprArray) {
            if (expression != null && expression.isNotEmpty()) {
                if (expression.lowercase().contains("!!set!!")) {
                    if (isExpressionQueryValid(expression)) {
                        val query: String = replaceSetOrVisibleQueryCols(expression)
                        if (query.isNotEmpty()) {
                            methodNames =
                                FieldDataSource(formActivity).hitExpressionQuery(query)
                            val fieldDataSource = FieldDataSource(context)
                            if (!fieldDataSource.isParamIdExists(
                                    formActivity.curSetID, eventId.toInt(),
                                    locationId, siteId.toInt(), metaData.currentFormID,
                                    deviceId, userId!!.toInt(), metaData.metaParamID
                                )
                            ) {
                                val bFieldData = getBlankFieldData(
                                    metaData, formActivity.curSetID
                                )
                                fieldDataSource.insertFieldDataList(
                                    bFieldData,
                                    userId!!.toInt(),
                                    deviceId
                                )
                            }

                            metaData.currentReading = methodNames
                            saveDataAndUpdateCreationDate(metaData)
                        }
                    }
                }
            }
        }
        return methodNames
    }

    fun isExpressionQueryValid(expr: String): Boolean {
        var expression = expr
        expression = expression.lowercase()
        return (!expression.contains("insert") || !expression.contains("update")
                || !expression.contains("delete") || !expression.contains("truncate")
                || !expression.contains("drop"))
    }

    fun replaceSetOrVisibleQueryCols(expr: String): String {
        var expression = expr
        expression = expression.replace("!!set!!", "")
        expression = expression.replace("!!visible!!", "")

        val mapCols = HashMap<String, String>()
        mapCols["d_field_data"] = "d_FieldData"
        mapCols["d_event"] = "d_Event"
        mapCols["s_site_mobile_app"] = "s_SiteMobileApp"
        mapCols["string_value"] = "StringValue"
        mapCols["field_parameter_id"] = "FieldParameterID"
        mapCols["mobile_app_id"] = "MobileAppID"
        mapCols["location_id"] = "LocationID"
        mapCols["event_id"] = "EventID"
        mapCols["set_id"] = "ExtField1"
        mapCols["site_id"] = "SiteID"
        mapCols["app_order"] = "AppOrder"
        mapCols["cu_loc_id"] = locationId
        mapCols["cu_eve_id"] = eventId
        mapCols["cu_project_id"] = siteId
        mapCols["cu_setId"] = formActivity.curSetID.toString()
        mapCols["cu_ext_field1"] = formActivity.curSetID.toString()
        mapCols["true"] = "1"
        mapCols["false"] = "0"
        mapCols["observation_date"] = "ExtField2"

        for ((key, value) in mapCols) {
            expression = expression.replace(key, value)
        }
        return expression
    }

    //can work on background thread
    fun getDefaultValueToSet(metaData: MetaData, tvLabel: CustomTextView): String? {

        val value: String?

        val dv = DefaultValueDataSource(context)
        val dModel = dv.getDefaultValueToWarn(
            locationId,
            metaData.currentFormID.toString(),
            metaData.metaParamID.toString(), formActivity.curSetID.toString()
        )

        val defaultValue = dModel.defaultValue

        if (metaData.metaInputType.equals("TEXT", ignoreCase = true)
            || metaData.metaInputType.equals("TEXTCONTAINER", ignoreCase = true)
            || metaData.metaInputType.equals("AUTOCOMPLETE", ignoreCase = true)
            || metaData.metaInputType.equals("COUNTER", ignoreCase = true)
            || metaData.metaInputType.equals("MULTIAUTOCOMPLETE", ignoreCase = true)
            || metaData.metaInputType.equals("TASK", ignoreCase = true)
            || metaData.metaInputType.equals("QRCODE", ignoreCase = true)
            || metaData.metaInputType.equals("BARCODE", ignoreCase = true)
            || metaData.metaInputType.equals("RADIO", ignoreCase = true)
            || metaData.metaInputType.equals("NUMERIC", ignoreCase = true)
            || metaData.metaInputType.equals("TOTALIZER", ignoreCase = true)
            || metaData.metaInputType.equals("GPS", ignoreCase = true)
        ) {
            if (metaData.currentReading != null && metaData.currentReading.isNotEmpty()) {
                value =
                    metaData.currentReading //not saving this value as it is already stored in db
            } else if (metaData.defaultValue != null && metaData.defaultValue.isNotEmpty()) {
                value = metaData.defaultValue
                metaData.currentReading = value
                handleTextData(metaData, tvLabel)
            } else if (defaultValue != null && defaultValue.isNotEmpty()) {
                value = defaultValue
                metaData.currentReading = value
                handleTextData(metaData, tvLabel)
            } else {
                value = null
            }
        } else {
            value = null
        }

        setAsteriskIfRequiredField(metaData, tvLabel)

        return value
    }

    fun getExpressionFromMetaOrAttribute(metaData: MetaData): String? {
        var expression = metaData.fieldParameterOperands
        val source = MetaDataAttributesDataSource(context)
        val attributes = source.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )
        if (attributes != null) {
            if (attributes.field_parameter_operands != null
                && attributes.field_parameter_operands.isNotEmpty()
            ) expression = attributes.field_parameter_operands
        }
        return expression
    }

    //bg thread
    fun updateChild(metaData: MetaData, displayValue: String?) {
        val childParamID =
            metaDataSource.getchildparamID(
                metaData.currentFormID,
                metaData.metaParamID.toString()
            )

        if (childParamID > 0) {
            val parentLovItemId =
                lovDataSource.getparentLovItemID(metaData.metaLovId, displayValue)
            metaData.parentLovItemId = parentLovItemId
        }
    }

    //bg thread
    fun updateNavigateToFormID(metaData: MetaData, key: String?) {
        val navigateToFormID = lovDataSource.get_navigateToformID(metaData.metaLovId, key)
        metaData.gotoFormId = navigateToFormID
    }

    fun manageAutoGenerateSet(metaData: MetaData) {

        var seperatedValues = listOf<String>()
        if (metaData.currentReading != null && metaData.currentReading.isNotEmpty()) {
            if (metaData.currentReading.contains("|")) {
                seperatedValues = metaData.currentReading.split("\\|")
            } else {
                seperatedValues = listOf()
                seperatedValues[0]
            }

            //ADD AUTO SET
            for (setvalue in seperatedValues) {
                if (!fieldDataSource.isSetGeneratedAlready(
                        siteId, eventId, locationId,
                        metaData.currentFormID, metaData.metaParamID, setvalue
                    )
                ) {
                    val maxSet = fieldDataSource.getNextSetID_MobileApp(
                        locationId, eventId,
                        metaData.currentFormID.toString()
                    )

                    val bFieldData = getBlankFieldData(metaData, maxSet)

                    bFieldData[0].stringValue = setvalue
                    bFieldData[0].mobileAppID = metaData.formID

                    val ext2 = fieldDataSource.getExt2ForMobileApp(
                        metaData.currentFormID,
                        eventId.toInt(), siteId.toInt(), locationId, formActivity.curSetID
                    )

                    val ext3 = fieldDataSource.getExt3ForMobileApp(
                        metaData.currentFormID,
                        eventId.toInt(), siteId.toInt(), locationId, formActivity.curSetID
                    )

                    bFieldData[0].extField2 = ext2
                    bFieldData[0].extField3 = ext3

                    fieldDataSource.insertFieldDataList(bFieldData, userId!!.toInt(), deviceId)
                    val creationDate = System.currentTimeMillis()

                    val oldCreationDate = fieldDataSource.getCreationDateForMobileApp(
                        metaData.formID,
                        eventId.toInt(), siteId.toInt(), locationId, userId!!.toInt(), maxSet
                    )

                    fieldDataSource.updateCreationDate(
                        eventId.toInt(),
                        maxSet,
                        locationId,
                        siteId.toInt(),
                        metaData.formID,
                        oldCreationDate?.toLong() ?: creationDate
                    )

                    fieldDataSource.updateMeasurementTime(
                        eventId.toInt(),
                        maxSet,
                        locationId,
                        siteId.toInt(),
                        metaData.formID,
                        creationDate
                    )
                }
            }
        }

        //21-02-2018 IF NULL THEN IT WILL DELETE FORM FOR THAT SITE, EVENT,LOCATION OR
        // ALL UNMATCHED SET FOR THAT EVENT,LOCATION AND FORM

        if (formsAdapter.AUTO_SET_LAST_SELECTED_VALUES != null) {
            /**
             *Delete set from d_field_data
             */
            val splitBy = "\\|"
            val oldSelectedValues = formsAdapter.AUTO_SET_LAST_SELECTED_VALUES?.split(splitBy)
            val oldValues = oldSelectedValues?.toHashSet()
            val newValues = seperatedValues.toHashSet()

            val deleteItems = oldValues?.toHashSet()
            deleteItems?.removeAll(newValues)
            deleteItems?.add("")

            if (deleteItems != null)
                for (deleteSetValue in deleteItems) {

                    val deleteSet = fieldDataSource.getSetForAutoSetValue(
                        locationId, eventId,
                        metaData.formID.toString(), deleteSetValue, metaData.metaParamID
                    )

                    if (deleteSet > 0) {
                        fieldDataSource.deleteset(
                            locationId, eventId.toInt(),
                            metaData.formID, deleteSet, siteId.toInt()
                        )
                        fieldDataSource.updateset(
                            locationId,
                            eventId.toInt(),
                            metaData.currentFormID,
                            siteId.toInt(),
                            deleteSet
                        )

                        attachmentDataSource.deleteAttachmentset(
                            locationId,
                            eventId.toInt(),
                            metaData.formID,
                            deleteSet,
                            siteId.toInt()
                        )
                        attachmentDataSource.updateAttachmentset(
                            locationId,
                            eventId.toInt(),
                            metaData.formID,
                            siteId.toInt(),
                            deleteSet
                        )
                        sampleMapTagDataSource.deletesetfromsamplemap(
                            locationId,
                            siteId.toInt(),
                            eventId.toInt(),
                            deleteSet,
                            metaData.formID
                        )
                        sampleMapTagDataSource.updatesampletag(
                            locationId,
                            siteId.toInt(),
                            eventId.toInt(),
                            deleteSet,
                            metaData.formID
                        )
                    }
                }
        }
    }

    fun getLocaleFormattedString(value: String?, locale: Locale): String {
        var valueToConvert = value
        var convertedValue = ""
        val nf = NumberFormat.getInstance(locale)
        nf.isGroupingUsed = false

        try {
            if (!valueToConvert.isNullOrEmpty()) {
                if (valueToConvert.contains(".") || valueToConvert.contains(",")) {


                    //added this on 12 Jan, 23 as client wanted trailing zero in case eg. 5.70 is filled it shouldn't convert to 5.7
                    /*                    val decimalCount = Util.getDecimalPlaces(value)
                                        if (decimalCount == 2) nf.minimumFractionDigits =
                                            decimalCount else nf.minimumFractionDigits =
                                            decimalPlaces*/

                    /*                    nf.minimumFractionDigits = decimalPlaces
                                        nf.maximumFractionDigits = 5*/
                }
                valueToConvert = value?.replace(",".toRegex(), ".")
                /*                if (Util.hasDigitDecimalOnly(valueToConvert)) convertedValue =
                                    nf.format(valueToConvert?.toDouble())*/
                convertedValue = valueToConvert ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        decimalPlaces = 0
        return convertedValue
    }

    fun handleNumericData(
        metaData: MetaData,
        numericView: EditText, showAlert: Boolean, tvLabel: CustomTextView
    ) {
        var highlimitdefault: Double? = null
        var lowlimitdefault: Double? = null

        val value = metaData.currentReading

        val dv = DefaultValueDataSource(context)
        var computedValue = 0.0
        var msg: String? = null
        if (value != null && value.isNotEmpty()) {
            try {
                computedValue = value.toDouble()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            //09-Sep-16
            val d_model = dv.getDefaultValueToWarn(
                locationId,
                metaData.currentFormID.toString(),
                metaData.metaParamID.toString(), formActivity.curSetID.toString()
            )
            var warninghighlimit: Double? = null
            var warninglowlimit: Double? = null
            var finalWarn_low: String? = ""
            var finalWarn_high: String? = ""

            if (d_model != null) {
                var high = d_model.highLimitDefaultValue
                var low = d_model.lowLimitDefaultValue
                var warn_low = d_model.warningLowDefaultValue
                var warn_high = d_model.warningHighDefaultValue
                finalWarn_low = warn_low
                finalWarn_high = warn_high
                if (metaData.metaInputType.equals("TOTALIZER", ignoreCase = true)) {
                    if (warn_low == null || warn_low.isEmpty()) {
                        //store entered value in database
                        dv.insertNewWarningLowValue(
                            locationId, metaData.currentFormID
                                .toString(), metaData.metaParamID.toString(), value
                        )
                    } else {
                        var dbWarningLow = 0.0
                        dbWarningLow = warn_low.toDouble()

                        val updatedWarningLow = value.toDouble()
                        if (updatedWarningLow > dbWarningLow) {
                            //if entered value is greater than value stored in database then update new value to database
                            dv.updateWarningLowValue(
                                locationId,
                                metaData.currentFormID.toString(),
                                metaData.metaParamID.toString(),
                                value
                            )
                        } else {
                            //if entered value is less than stored value then show warning message to user and clear text field
                            scopeMainThread.launch {
                                msg =
                                    "Value is Low.\n Value should be greater than: $dbWarningLow"
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Alert")
                                builder.setMessage("" + msg)
                                builder.setCancelable(true)
                                val final_warn_low = finalWarn_low
                                val final_warn_high = finalWarn_high

                                builder.setNeutralButton("OK",
                                    DialogInterface.OnClickListener { _, _ ->
                                        if (!STORE_VALUE) {
                                            numericView.setText("")
                                        } else {
                                            try {
                                                if (tvLabel.text.toString()
                                                        .contains("Range")
                                                ) {
                                                    val styledText =
                                                        """<font color='red'>${metaData.ParamLabel}</font>
        <font color='red'><small>Range: $final_warn_low to $final_warn_high</small></font>"""
                                                    val result: Spanned =
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            Html.fromHtml(
                                                                styledText,
                                                                Html.FROM_HTML_MODE_LEGACY
                                                            )
                                                        } else {
                                                            Html.fromHtml(styledText)
                                                        }
                                                    tvLabel.text = result
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            numericView.setTextColor(
                                                ContextCompat.getColor(
                                                    formActivity,
                                                    R.color.red
                                                )
                                            )
                                        }
                                    })

                                val alertdialog = builder.create()
                                alertdialog.show()
                            }
                        }
                    }
                } else {
                    //here code for remaining numeric field will be added apart from TOTALIZER
                    if (high == null || high.isEmpty()) {
                        if (metaData.routineId == 111) {
                            high = metaData.metaHighLimit.toString()
                            highlimitdefault = high.toDouble()
                        } else {
                            highlimitdefault = null
                        }
                    } else {
                        highlimitdefault = high.toDouble()
                    }
                    if (low == null || low.trim { it <= ' ' }.isEmpty()) {
                        if (metaData.routineId == 111) {
                            low = metaData.metaLowLimit.toString()
                            lowlimitdefault = low.toDouble()
                        } else {
                            lowlimitdefault = null
                        }
                    } else {
                        lowlimitdefault = low.toDouble()
                    }
                    if (warn_low == null || warn_low.isEmpty()) {
                        if (metaData.routineId == 111) {
                            warn_low = metaData.metaWarningLow.toString()
                            warninglowlimit = warn_low.toDouble()
                        } else {
                            warninglowlimit = null
                        }
                    } else {
                        warninglowlimit = warn_low.toDouble()
                    }
                    if (warn_high == null || warn_high.isEmpty()) {
                        if (metaData.routineId == 111) {
                            warn_high = metaData.metaWarningHigh.toString()
                            warninghighlimit = warn_high.toDouble()
                        } else {
                            warninghighlimit = null
                        }
                    } else {
                        warninghighlimit = warn_high.toDouble()
                    }
                }
            }

            //01 Dec, 21 By Sanket - Added this condition as sometimes both default may have zero which cause
            //condition "val < lowlimitdefault" below in else to run when added a negative value to the field
            val bothDefaultHasZero = (highlimitdefault != null && lowlimitdefault != null
                    && highlimitdefault == 0.0 && lowlimitdefault == 0.0)

            val bothWarningHasZero = (warninghighlimit != null && warninglowlimit != null
                    && warninghighlimit == 0.0 && warninglowlimit == 0.0)


            if (bothDefaultHasZero) {
                highlimitdefault = null
                lowlimitdefault = null
            }

            if (bothWarningHasZero) {
                warninghighlimit = null
                warninglowlimit = null
            }

            if (highlimitdefault == null && lowlimitdefault == null) {
                STORE_VALUE = true
                if (warninghighlimit == null && warninglowlimit == null) { //no high-low and warning high-low present
                    //Store value as it is (No validation)
                } else if (warninghighlimit == null) { //24-Oct-16 x to Positive Values
                    if (computedValue >= warninglowlimit!!) {
                        //Valid No.
                    } else {
                        //Not valid
                        STORE_VALUE = true
                        msg = "Value is Low.\nValue should be greater than: $warninglowlimit"
                    }
                } else if (warninglowlimit == null) {
                    if (computedValue <= warninghighlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        STORE_VALUE = true
                        msg = "Value is High.\nValue should be less than: $warninghighlimit"
                    }
                } else { //Both are present
                    if (computedValue <= warninghighlimit && computedValue >= warninglowlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        STORE_VALUE = true
                        msg =
                            "Value should be in between the range: $warninglowlimit To $warninghighlimit"
                        //msg = "Value outside the range: " + warninglowlimit + " to " + warninghighlimit;
                    }
                }
            } else if (highlimitdefault == null) { //24-Oct-16 x to Positive Values
                STORE_VALUE = false
                if (warninghighlimit == null && warninglowlimit == null) { //24-Oct-16 no high-low and warning high-low present
                    if (lowlimitdefault != null && computedValue >= lowlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true
                    } else {
                        //Not valid
                        msg = "Value is Low.\nValue should be greater than: $lowlimitdefault"
                    }
                } else if (warninghighlimit == null) { //24-Oct-16 x to Positive Values
                    if (computedValue >= warninglowlimit!!) {
                        //Valid No.
                        STORE_VALUE = true
                    } else if (lowlimitdefault != null && computedValue >= lowlimitdefault && computedValue < warninglowlimit) {
                        //Not valid
                        STORE_VALUE = true
                        msg = "Value is Low.\nValue should be greater than: $warninglowlimit"
                    } else if (lowlimitdefault != null && computedValue < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg = "Value should be greater than: $lowlimitdefault"
                    }
                } else if (warninglowlimit == null) {
                    if (lowlimitdefault != null && computedValue < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range: $lowlimitdefault To $warninghighlimit"
                    } else if (computedValue >= warninghighlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value should be in between the range: $lowlimitdefault To $warninghighlimit"
                    } else if (lowlimitdefault != null && computedValue >= lowlimitdefault && computedValue <= warninghighlimit) {
                        //Valid No.
                        STORE_VALUE = true
                    }
                } else {
                    if (lowlimitdefault != null && computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        //                        msg = "Value should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                        msg = "Value should be greater than: $lowlimitdefault"
                    } else if (lowlimitdefault != null && computedValue >= lowlimitdefault && computedValue <= warninglowlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= warninglowlimit && computedValue <= warninghighlimit) {
                        STORE_VALUE = true
                    } else if (computedValue > warninghighlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                    }
                }
            } else if (lowlimitdefault == null) { //x to Negative values
                STORE_VALUE = false
                if (warninghighlimit == null && warninglowlimit == null) { //no high-low and warning high-low present
                    if (computedValue <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true
                    } else {
                        //Not valid
                        STORE_VALUE = false
                        msg = "Value is High.\nValue should be less than: $highlimitdefault"
                    }
                } else if (warninghighlimit == null) { //x to Positive Values
                    if (computedValue <= warninglowlimit!!) {
                        //Valid No.
                        STORE_VALUE = true
                        msg =
                            "Value is Low.\nValue should be in between the range: $warninglowlimit To $highlimitdefault"
                    } else if (computedValue <= highlimitdefault && computedValue >= warninglowlimit) {
                        //Not valid
                        STORE_VALUE = true
                    } else if (computedValue > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg = "Value is High.\nValue should be less than: $highlimitdefault"
                        //                      msg = "Value is High.\nValue should be in between the range: " + warninglowlimit + " To " + highlimitdefault;
                    }
                } else if (warninglowlimit == null) {
                    if (computedValue > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg = "Value should be less than: $highlimitdefault"
                    } else if (computedValue <= warninghighlimit) {
                        STORE_VALUE = true
                    } else if (computedValue <= highlimitdefault && computedValue >= warninghighlimit) {
                        //Valid No.
                        STORE_VALUE = true
                        msg = "Value is High.\nValue should be less than: $warninghighlimit"
                    }
                } else {
                    if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg = "Value should be less than: $highlimitdefault"
                        //                        msg = "Value should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    } else if (computedValue <= highlimitdefault && computedValue >= warninghighlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= warninglowlimit && computedValue <= warninghighlimit) {
                        STORE_VALUE = true
                    } else if (computedValue < warninglowlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                    }
                }
            } else { //24-Oct-16 Both are present
                STORE_VALUE = false
                if (warninghighlimit == null && warninglowlimit == null) { //24-Oct-16 no high-low and warning high-low present
                    if (computedValue >= lowlimitdefault && computedValue <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true
                    } else {
                        //Not valid No.
                        STORE_VALUE = false
                        msg =
                            " Value should be in between the range: $lowlimitdefault To $highlimitdefault"
                    }
                } else if (warninglowlimit == null) {
                    if (computedValue > warninghighlimit!! && computedValue <= highlimitdefault) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.\nValue should be in between the range: $lowlimitdefault To $warninghighlimit"
                    } else if (computedValue >= lowlimitdefault && computedValue <= warninghighlimit) {
                        STORE_VALUE = true
                    } else if (computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range: $lowlimitdefault To $warninghighlimit"
                    } else if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range: $lowlimitdefault To $warninghighlimit"
                    }
                } else if (warninghighlimit == null) {
                    if (computedValue >= lowlimitdefault && computedValue < warninglowlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.\nValue should be in between the range: $warninglowlimit To $highlimitdefault"
                    } else if (computedValue >= warninglowlimit && computedValue <= highlimitdefault) {
                        STORE_VALUE = true
                    } else if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range: $warninglowlimit To $highlimitdefault"
                    } else if (computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range: $warninglowlimit To $highlimitdefault"
                    }
                } else {
                    //24-Oct-16 High,low,warn_high n warn_low  all has value
                    if (computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range: $lowlimitdefault To $highlimitdefault"
                    } else if (computedValue >= lowlimitdefault && computedValue < warninglowlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= warninglowlimit && computedValue <= warninghighlimit) {
                        STORE_VALUE = true
                    } else if (computedValue > warninghighlimit && computedValue <= highlimitdefault) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                    } else if (computedValue > highlimitdefault) {
                        //STORE_VALUE = false;
                        if (computedValue > warninghighlimit && computedValue > 0.0 && highlimitdefault == 0.0) {
                            STORE_VALUE = true
                            msg =
                                "Value is High.\nValue should be in between the range: $warninglowlimit To $warninghighlimit"
                        } else {
                            STORE_VALUE =
                                false //Store value status change to true initially it was false on 28 may
                            msg =
                                "Value should be in between the range: $lowlimitdefault To $highlimitdefault"
                        }
                    }
                }
            }
            val finalWarn_low1 = finalWarn_low
            val finalWarn_high1 = finalWarn_high

            //considering if there is a message then of course it is prompting user that value
            //is not between range means it has been violated then 1 else 0
            var violationFlag = "0"

            //setting label to normal color if value entered is between range
            scopeMainThread.launch {
                if (msg == null) {
                    violationFlag = "0"
                    try {
                        if (tvLabel.text.toString().contains("Range")
                            && finalWarn_low1!!.isNotEmpty() && finalWarn_high1!!.isNotEmpty()
                        ) {
                            val styledText = """${metaData.ParamLabel}
        <font color='blue'><small>Range: $finalWarn_low1 to $finalWarn_high1</small></font>"""
                            val result: Spanned =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)
                                } else {
                                    Html.fromHtml(styledText)
                                }
                            tvLabel.text = result
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (msg != null && showAlert) {
                violationFlag = "1"
                scopeMainThread.launch {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Alert")
                    builder.setMessage("" + msg)
                    builder.setCancelable(true)
                    builder.setNeutralButton("OK",
                        DialogInterface.OnClickListener { _, _ ->
                            if (!STORE_VALUE) {
                                numericView.setText("")
                            } else {
                                try {
                                    if (tvLabel.text.toString().contains("Range")
                                        && finalWarn_low1!!.isNotEmpty() && finalWarn_high1!!.isNotEmpty()
                                    ) {
                                        val styledText =
                                            """<font color='red'>${metaData.ParamLabel}</font>
        <font color='blue'><small>Range: $finalWarn_low1 to $finalWarn_high1</small></font>"""
                                        val result: Spanned =
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                Html.fromHtml(
                                                    styledText,
                                                    Html.FROM_HTML_MODE_LEGACY
                                                )
                                            } else {
                                                Html.fromHtml(styledText)
                                            }
                                        tvLabel.text = result
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                numericView.setTextColor(
                                    ContextCompat.getColor(
                                        formActivity,
                                        R.color.red
                                    )
                                )
                            }
                        })

                    val alertdialog = builder.create()
                    alertdialog.show()
                }
            } /*else if (value != null && value.length != 0 && msg == null) {
                        tempData.imgStatus = FormMaster.indicatorStatus.ImageStausValid
                    } else {
                        tempData.imgStatus = FormMaster.indicatorStatus.ImageStatusInvisible
                    }*/
            val fieldDataSource = FieldDataSource(context)
            fieldDataSource.updateViolationFlag(
                eventId,
                metaData.metaParamID,
                formActivity.curSetID,
                locationId,
                violationFlag,
                siteId.toInt(),
                formActivity.currentAppID
            )
            if (fieldDataSource.isParamIdExists(
                    formActivity.curSetID, eventId.toInt(),
                    locationId, siteId.toInt(), metaData.currentFormID,
                    deviceId, userId!!.toInt(), metaData.metaParamID
                )
            ) {
                if (STORE_VALUE) {
                    metaData.currentReading = value
                } else {
                    metaData.currentReading = null
                }
                saveDataAndUpdateCreationDate(
                    metaData
                )
            } else {
                if (STORE_VALUE) {
                    metaData.currentReading = value
                } else {
                    metaData.currentReading = null
                }
                val bfieldData: List<FieldData> =
                    getBlankFieldData(metaData, formActivity.curSetID)
                fieldDataSource.insertFieldDataList(bfieldData, userId!!.toInt(), deviceId + "")
                metaData.currentReading = value
                saveDataAndUpdateCreationDate(
                    metaData
                )
            }
        } else {
            //will save whatever cur reading is received when method called
            saveDataAndUpdateCreationDate(
                metaData
            )
        }
        //        handleLast2Reading_And_Percentage(metaData, numericView, viewHolder);
    }

    //start from bg thread as ui thread is managed inside
    fun setCalculatedFieldParams(
        metaData: MetaData,
        editText: EditText?, tvLabel: CustomTextView
    ) {

        var value: String? = null
        val operand = getExpressionFromMetaOrAttribute(metaData) ?: return

        if (operand.isEmpty()) return

        //changed on 26 June, 21 added this for multiple queries that is separated by ~
        val exprArray = Util.splitStringToArray("~", operand)
        for (expression in exprArray) {
            try {
                value = calculateFromExpression(context, metaData, expression)
                if (expression != null && expression.contains("SAMPLE3") && value == null) {
                    val coc_dv = CocDetailDataSource(context)
                    value = coc_dv.getSampleID_from_cocDetail(
                        formActivity.currCocID,
                        locationId,
                        metaData.metaParamID.toString()
                    )
                }
                if (value != null && (value.contains("Error") || value.isEmpty())) {
                    value = null
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("Calculate Expr", "setCalculatedFieldParams error:" + e.message)
            }

            if (editText != null && metaData.metaInputType.trim().contains("NUMERIC")) {
                if (expression != null && expression.isNotEmpty()) {
                    try {
                        if (metaData.metaParamLabel.lowercase().contains("reference elevation")
                            && metaData.currentReading != value
                        ) {
                            metaData.currentReading = value
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (metaData.metaParamLabel.lowercase()
                                .contains("reference elevation")
                        ) metaData.currentReading = value
                    }

                    //commented on 07-10-2021
                    /*                    if (expression.contains("!!set!!")) {
                                editText.setEnabled(false);
                            }*/

                    if (expression.contains("PurgeCalculation")
                        || expression.contains("VolumePurged")
                    ) {
                        if (formActivity.curSetID > 1) {

                            /*val flowRateCurReading =
                                formsAdapter.getCurrentReading(formsAdapter.flowRateParamId)

                            if (!flowRateCurReading.isNullOrEmpty()) {
                                val newValue: String =
                                    getLocaleFormattedString(value, Locale.getDefault())
                                if (!value.isNullOrEmpty())
                                    scopeMainThread.launch { editText.setText(newValue) }
                                else scopeMainThread.launch { editText.setText(value) }
                            } else scopeMainThread.launch { editText.setText("") }*/

                            val newValue: String =
                                getLocaleFormattedString(value, Locale.getDefault())
                            if (!value.isNullOrEmpty())
                                scopeMainThread.launch { editText.setText(newValue) }
                            else scopeMainThread.launch { editText.setText(value) }

                            scopeMainThread.launch {
//                                editText.isEnabled = value == null || value!!.isEmpty()
                            }
                        } else {
                            value = editText.text.toString()
                            val newValue: String =
                                getLocaleFormattedString(value, Locale.ENGLISH)
                            if (value.isNotEmpty()) {
                                value = newValue
                            }
                            scopeMainThread.launch { editText.isEnabled = true }
                        }
                    } else {
                        val newValue: String =
                            getLocaleFormattedString(value, Locale.getDefault())
                        if (value != null && value.isNotEmpty()) {
                            if (!Util.containsAtLeastOneAlphabet(value)) {
                                value = getLocaleFormattedString(value, Locale.ENGLISH)
                                scopeMainThread.launch { editText.setText(newValue) }
                            } else if (expression.contains("SAMPLE") && !formsAdapter.isSampleDateOrTimeSet) {
                                scopeMainThread.launch { editText.setText(value) }
                            } else {
                                scopeMainThread.launch { editText.setText(value) }
                            }
                        }
                    }
                    scopeMainThread.launch { editText.isFocusable = true }
//                    metaData.currentReading = value
                    handleNumericDataForCalculatedFields(metaData, value, editText)
                } else {
                    editText.isFocusable = true
                }
            } else {
                if (value != null && value.isNotEmpty()) {
                    if (metaData.metaInputType.trim().isEmpty()) {
                        scopeMainThread.launch { tvLabel.text = value }
                    } else if (metaData.metaInputType.trim().contains("LABEL")) {
                        scopeMainThread.launch { tvLabel.text = value }
                    } else if (metaData.metaInputType.trim().contains("TEXT")
                        || metaData.metaInputType.trim().contains("TEXTCONTAINER")
                    ) {
                        editText?.setText(value)
                    } else if (metaData.metaInputType.trim().contains("DATE")
                        || metaData.metaInputType.trim().contains("TIME")
                    ) {
                        if (value.isNotEmpty()) {
                            tvLabel.text = value
                            metaData.currentReading = value
                        }
                    }
                }
            }
        }
    }

    fun calculateFromExpression(
        ObjContext: Context?,
        metaData: MetaData,
        expr: String?
    ): String? {
        var expression = expr
        val fp = FieldDataSource(ObjContext)

        //        String expression = metaData.getFieldParameterOperands();
        // 27-Jun-16 Direct Expression from FieldParameterOperands is Collected
        Log.i("calculateFromExpression", "InPut Expression:$expression")
        val lhs: String
        val rhs: String
        //      expression="(({310}#0#|1235|-{310}#1#|1235|)/((@{310}#0#|15|@)-(@{310}#-1#|15|@)))";
        if (expression != null && expression.isNotEmpty()) {

            //IF started
            if (expression.contains("IF") && !expression.contains("!!set!!")) {
                var lhsValue: String? = "0"
                var mobAppID = ""
                var setID = 0
                var f_paramID: String? = ""

                lhs = expression.substring(0, expression.indexOf(")"))

                if (lhs.contains("{")) {
                    mobAppID = lhs.substring(lhs.indexOf("{") + 1, lhs.lastIndexOf("}"))
                }

                if (lhs.contains("#")) setID =
                    lhs.substring(lhs.indexOf("#") + 1, lhs.lastIndexOf("#")).toInt()

                if (lhs.contains("|")) f_paramID =
                    lhs.substring(lhs.indexOf("|") + 1, lhs.lastIndexOf("|"))

                if (f_paramID != null && f_paramID.isNotEmpty()) {
                    if (setID <= 0) {
                        if (setID < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + setID
                            setID = res
                            lhsValue = if (setID <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(), locationId,
                                    if (mobAppID == null || mobAppID.isEmpty())
                                        metaData.currentFormID else mobAppID.toInt(),
                                    setID, f_paramID
                                )
                            }
                        } else if (setID == 0) {
                            setID = formActivity.curSetID
                            lhsValue = fp.getStringValueFromId(
                                eventId.toInt(), locationId,
                                if (mobAppID == null || mobAppID.isEmpty())
                                    metaData.currentFormID else mobAppID.toInt(),
                                setID, f_paramID
                            )
                        }
                    } else {
                        lhsValue = fp.getStringValueFromId(
                            eventId.toInt(), locationId,
                            if (mobAppID == null || mobAppID.isEmpty())
                                metaData.currentFormID else mobAppID.toInt(),
                            setID, f_paramID
                        )
                    }
                } else {
                    val pattern = "\\d+"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(lhs)
                    while (m.find()) {
                        lhsValue = lhs.substring(m.start(), m.end())
                    }
                }
                rhs = expression.substring(expression.indexOf(")"))
                val operator = rhs.substring(rhs.indexOf("$") + 1, rhs.lastIndexOf("$"))
                var conditionOperand: String? =
                    rhs.substring(rhs.lastIndexOf("$") + 1, rhs.lastIndexOf("?"))
                var conditionTrueValue: String? =
                    rhs.substring(rhs.lastIndexOf("?") + 1, rhs.lastIndexOf(":"))
                var conditionFalseValue: String? = rhs.substring(rhs.lastIndexOf(":") + 1)

                if (conditionOperand!!.contains("|")) {
                    var mobApp = ""
                    var set = 0
                    if (conditionOperand.contains("{")) {
                        mobApp = conditionOperand.substring(
                            conditionOperand.indexOf("{") + 1,
                            conditionOperand.lastIndexOf("}")
                        )
                    }
                    if (conditionOperand.contains("#")) {
                        set = conditionOperand.substring(
                            conditionOperand.indexOf("#") + 1,
                            conditionOperand.lastIndexOf("#")
                        ).toInt()
                    }
                    val fpID: String = conditionOperand.substring(
                        conditionOperand.indexOf("|") + 1,
                        conditionOperand.lastIndexOf("|")
                    )
                    if (mobApp == null || mobApp.isEmpty()) {
                        mobApp = metaData.currentFormID.toString()
                    }
                    if (set <= 0) {
                        if (set < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + set
                            set = res
                            conditionOperand = if (set <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(),
                                    locationId,
                                    mobApp.toInt(),
                                    set,
                                    fpID
                                )
                            }
                        } else if (set == 0) {
                            set = formActivity.curSetID
                            conditionOperand = fp.getStringValueFromId(
                                eventId.toInt(),
                                locationId,
                                mobApp.toInt(),
                                set,
                                fpID
                            )
                        }
                    } else {
                        conditionOperand = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            fpID
                        )
                    }
                } else {
                    val pattern = "\\d+"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(conditionOperand)
                    while (m.find()) {
                        conditionOperand = conditionOperand!!.substring(m.start(), m.end())
                    }
                }
                if (conditionTrueValue!!.contains("|")) {
                    var mobApp = ""
                    val fpID: String
                    var set = 0
                    if (conditionTrueValue.contains("{")) {
                        mobApp = conditionTrueValue.substring(
                            conditionTrueValue.indexOf("{") + 1,
                            conditionTrueValue.lastIndexOf("}")
                        )
                    }
                    if (conditionTrueValue.contains("#")) {
                        set = conditionTrueValue.substring(
                            conditionTrueValue.indexOf("#") + 1,
                            conditionTrueValue.lastIndexOf("#")
                        ).toInt()
                    }
                    if (mobApp.isEmpty()) {
                        mobApp = metaData.currentFormID.toString()
                    }
                    fpID = conditionTrueValue.substring(
                        conditionTrueValue.indexOf("|") + 1,
                        conditionTrueValue.lastIndexOf("|")
                    )
                    if (set <= 0) {
                        if (set < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + set
                            set = res
                            conditionTrueValue = if (set <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(),
                                    locationId,
                                    mobApp.toInt(),
                                    set,
                                    fpID
                                )
                            }
                        } else if (set == 0) {
                            set = formActivity.curSetID
                            conditionTrueValue = fp.getStringValueFromId(
                                eventId.toInt(),
                                locationId,
                                mobApp.toInt(),
                                set,
                                fpID
                            )
                        }
                    } else {
                        conditionTrueValue = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            fpID
                        )
                    }
                } else {
                    val pattern = "\\d+"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(conditionTrueValue)
                    while (m.find()) {
                        conditionTrueValue = conditionTrueValue!!.substring(m.start(), m.end())
                    }
                }
                if (conditionFalseValue!!.contains("|")) {
                    var mobApp = "0"
                    var set = 0
                    if (conditionFalseValue.contains("{")) {
                        mobApp = conditionFalseValue.substring(
                            conditionFalseValue.indexOf("{") + 1,
                            conditionFalseValue.lastIndexOf("}")
                        )
                    }
                    if (conditionFalseValue.contains("#")) {
                        set = conditionFalseValue.substring(
                            conditionFalseValue.indexOf("#") + 1,
                            conditionFalseValue.lastIndexOf("#")
                        ).toInt()
                    }
                    if (mobApp == "0") {
                        mobApp = metaData.currentFormID.toString()
                    }
                    val fpID: String = conditionFalseValue.substring(
                        conditionFalseValue.indexOf("|") + 1,
                        conditionFalseValue.lastIndexOf("|")
                    )
                    if (set <= 0) {
                        if (set < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + set
                            set = res
                            conditionFalseValue = if (set <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(),
                                    locationId,
                                    mobApp.toInt(),
                                    set,
                                    fpID
                                )
                            }
                        } else if (set == 0) {
                            set = formActivity.curSetID
                            conditionFalseValue = fp.getStringValueFromId(
                                eventId.toInt(),
                                locationId,
                                mobApp.toInt(),
                                set,
                                fpID
                            )
                        }
                    } else {
                        conditionFalseValue = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            fpID
                        )
                    }
                } else {
                    val pattern = "\\d+"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(conditionFalseValue)
                    while (m.find()) {
                        conditionFalseValue =
                            conditionFalseValue!!.substring(m.start(), m.end())
                    }
                }
                var res: String? = "NA"
                res = try {
                    if (lhsValue == null || lhsValue.isEmpty() || conditionOperand == null
                        || conditionOperand.isEmpty() || conditionTrueValue == null
                        || conditionTrueValue.isEmpty() || conditionFalseValue == null
                        || conditionFalseValue.isEmpty()
                    ) {
                        Log.i(
                            TAG,
                            "Not getting all arguments to build a conditional expression"
                        )
                        return res
                    } else {
                        FormMaster.buildConditionalExpressionResult(
                            lhsValue.toDouble(),
                            operator,
                            conditionOperand.toDouble(),
                            conditionTrueValue,
                            conditionFalseValue
                        )
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "calculateFromExpression (IF condition):" + e.message)
                    return res
                }
                return res
            } else if (expression.contains("SAMPLE3")) {
                var dateString: String? = null
                var timeString: String? = null
                var resultString: String? = null
                val s3subexpression =
                    expression.substring(
                        expression.indexOf("(") + 1,
                        expression.lastIndexOf(")")
                    )
                val s3FpIds: Array<String?> =
                    s3subexpression.split(",".toRegex()).toTypedArray()
                if (s3subexpression.contains("|")) {
                    val mobApp = metaData.currentFormID.toString()
                    val datefpID: String
                    val timeFpID: String
                    val set: Int = formActivity.curSetID
                    if (s3FpIds[0] != null && !s3FpIds[0]!!.isEmpty()) {
                        datefpID = s3FpIds[0]!!.substring(
                            s3FpIds[0]!!.indexOf("|") + 1,
                            s3FpIds[0]!!.lastIndexOf("|")
                        )
                        dateString = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            datefpID
                        )
                    }
                    if (s3FpIds[1] != null && !s3FpIds[1]!!.isEmpty()) {
                        timeFpID = s3FpIds[1]!!.substring(
                            s3FpIds[1]!!.indexOf("|") + 1,
                            s3FpIds[1]!!.lastIndexOf("|")
                        )
                        timeString = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            timeFpID
                        )
                    }
                    try {
                        if (dateString != null && dateString != "0") {
                            val locSource = LocationDataSource(ObjContext)
                            val fromFormat = SimpleDateFormat("mm/dd/yyyy", Locale.getDefault())
                            val toFormat = SimpleDateFormat("mmddyy", Locale.getDefault())
                            var newString: String? = null
                            try {
                                newString = toFormat.format(fromFormat.parse(dateString))
                                //03-Jan-16
                                //                                int quarter = Util.getQuarter(newString);
                                //                                String[] yr = newString.split("/");
                                //                                newString = "-" + quarter + yr[2];//Q[2][92]
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            val replacedLocationString = locSource.getLocationName(locationId)
                                .replace("/", "-")
                            resultString = if (replacedLocationString.endsWith("-")) {
                                replacedLocationString + "GW-" +
                                        newString //LocationName(replace "/" by "-")-mmddyy
                            } else {
                                replacedLocationString + "-GW-" +
                                        newString //LocationName(replace "/" by "-")-mmddyy
                            }
                        } else {
                            resultString = null
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        Log.e(TAG, "Error in SampleID3 Calculation:" + e.message)
                    }
                }
                if (resultString != null && formActivity.currCocID != null && !formActivity.currCocID.equals(
                        "0",
                        ignoreCase = true
                    )
                ) {
                    //19-03-2018 COC
                    handleSample3Data(dateString, timeString, resultString)
                }
                return resultString
            } else if (expression.contains("SAMPLE2")) {
                var resultString: String? = null
                var newString: String? = null
                var sampleDate: String? = null
                val sampleTime = ""
                if (expression.contains("|")) {
                    var mobApp = "0"
                    val fpID: String
                    var set = 0
                    if (expression.contains("{")) {
                        mobApp = expression.substring(
                            expression.indexOf("{") + 1,
                            expression.indexOf("}")
                        )
                    }
                    if (expression.contains("#")) {
                        set = expression.substring(
                            expression.indexOf("#") + 1,
                            expression.indexOf("#")
                        ).toInt()
                    }
                    if (mobApp == "0") {
                        mobApp = metaData.currentFormID.toString()
                    }
                    fpID = expression.substring(
                        expression.indexOf("|") + 1,
                        expression.lastIndexOf("|")
                    )
                    if (set <= 0) {
                        if (set < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + set
                            set = res
                            resultString = if (set <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(),
                                    locationId,
                                    mobApp.toInt(),
                                    set,
                                    fpID
                                )
                            }
                        } else if (set == 0) {
                            set = formActivity.curSetID
                            resultString = fp.getStringValueFromId(
                                eventId.toInt(),
                                locationId,
                                mobApp.toInt(),
                                set,
                                fpID
                            )
                        }
                    } else {
                        resultString = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            fpID
                        )
                    }
                    try {
                        if (resultString != null && resultString != "0") {
                            Log.e("resultString", "WITHIN IF AND TRY-----  $resultString")
                            sampleDate = resultString
                            val locSource = LocationDataSource(ObjContext)
                            val fromFormat = SimpleDateFormat("mm/dd/yyyy", Locale.getDefault())
                            val toFormat = SimpleDateFormat("mm/dd/yy", Locale.getDefault())
                            try {
                                newString = toFormat.format(fromFormat.parse(resultString))
                                //03-Jan-16
                                val quarter = Util.getQuarter(newString)
                                val yr = newString.split("/".toRegex()).toTypedArray()
                                newString = "Q" + quarter + yr[2] //Q[2][92]
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            resultString = locSource.getLocationName(locationId) + "-" +
                                    newString //LocationName-Q192
                        } else {
                            resultString = null
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        Log.e(TAG, "Error in SampleID2 Calculation:" + e.message)
                    } catch (n: NullPointerException) {
                        n.printStackTrace()
                    }
                }
                if (resultString != null && formActivity.currCocID != null && !formActivity.currCocID.equals(
                        "0",
                        ignoreCase = true
                    ) && metaData.currentFormID == 145
                ) {
                    //19-03-2018 COC
                    handleSample3Data(sampleDate, sampleTime, resultString)
                }
                return resultString
            } else if (expression.contains("SAMPLE")) {
                var resultString: String? = null
                if (expression.contains("|")) {
                    var mobApp = "0"
                    val fpID: String
                    var set = 0
                    if (expression.contains("{")) {
                        mobApp = expression.substring(
                            expression.indexOf("{") + 1,
                            expression.indexOf("}")
                        )
                    }
                    if (expression.contains("#")) {
                        set = expression.substring(
                            expression.indexOf("#") + 1,
                            expression.indexOf("#")
                        ).toInt()
                    }
                    if (mobApp == "0") {
                        mobApp = metaData.currentFormID.toString()
                    }
                    fpID = expression.substring(
                        expression.indexOf("|") + 1,
                        expression.lastIndexOf("|")
                    )
                    if (set <= 0) {
                        if (set < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + set
                            set = res
                            resultString = if (set <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(), locationId, mobApp.toInt(),
                                    set, fpID
                                )
                            }
                        } else if (set == 0) {
                            set = formActivity.curSetID
                            resultString = fp.getStringValueFromId(
                                eventId.toInt(),
                                locationId,
                                mobApp.toInt(),
                                set,
                                fpID
                            )
                        }
                    } else {
                        resultString = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            fpID
                        )
                    }
                    try {
                        if (resultString != null && resultString != "0") {
                            val locSource = LocationDataSource(ObjContext)
                            val fromFormat = SimpleDateFormat("mm/dd/yyyy", Locale.getDefault())
                            val toFormat = SimpleDateFormat("mm/dd/yy", Locale.getDefault())
                            var newString: String? = null
                            try {
                                newString = toFormat.format(fromFormat.parse(resultString))
                                //03-Jan-16
                                val quarter = Util.getQuarter(newString)
                                val yr = newString.split("/".toRegex()).toTypedArray()
                                newString = quarter.toString() + "Q" + yr[2] //2Q92
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            resultString =
                                siteName + "-" + locSource.getLocationName(locationId) +
                                        "(" + newString + ")" //SiteName-LocationName(1Q92)
                        } else {
                            resultString = null
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        Log.e(TAG, "Error in SampleID Calculation:" + e.message)
                    }
                }
                return resultString
            } else if (expression.contains("COPY")) {
                var mobAppID = ""
                var f_paramID: String? = null
                var setID = 0
                var copyValue: String? = null
                lhs = expression.substring(0, expression.lastIndexOf(")"))
                if (lhs.contains("{")) mobAppID =
                    lhs.substring(lhs.indexOf("{") + 1, lhs.lastIndexOf("}"))
                if (lhs.contains("#")) {
                    setID =
                        if (lhs.substring(lhs.indexOf("#") + 1, lhs.lastIndexOf("#"))
                                .contains("9999")
                        ) 1 else lhs.substring(lhs.indexOf("#") + 1, lhs.lastIndexOf("#"))
                            .toInt()
                }
                if (mobAppID == null || mobAppID.isEmpty()) {
                    mobAppID = metaData.currentFormID.toString()
                }
                if (lhs.contains("|")) {
                    f_paramID = lhs.substring(lhs.indexOf("|") + 1, lhs.lastIndexOf("|"))
                    if (setID <= 0) {
                        if (setID < 0) { //No. is Negative
                            val res: Int = formActivity.curSetID + setID
                            setID = res
                            copyValue = if (setID <= 0) {
                                null
                            } else {
                                fp.getStringValueFromId(
                                    eventId.toInt(),
                                    locationId,
                                    mobAppID.toInt(),
                                    setID,
                                    f_paramID
                                )
                            }
                        } else if (setID == 0) {
                            setID = formActivity.curSetID
                            copyValue = fp.getStringValueFromId(
                                eventId.toInt(),
                                locationId,
                                mobAppID.toInt(),
                                setID,
                                f_paramID
                            )
                        }
                    } else {
                        copyValue = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            mobAppID.toInt(),
                            setID,
                            f_paramID
                        )
                    }
                } else {
                    val pattern = "\\d+"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(lhs)
                    while (m.find()) {
                        f_paramID = lhs.substring(m.start(), m.end())
                    }
                    copyValue = fp.getStringValueFromId(
                        eventId.toInt(),
                        locationId,
                        mobAppID.toInt(),
                        setID,
                        f_paramID
                    )
                }
                Log.i(
                    TAG,
                    "Copy Value From FormID:$mobAppID SetID:$setID FieldParameterID:$f_paramID StringValue:$copyValue"
                )
                return copyValue
            } else if (expression.contains("PurgeCalculation")) {
                //if no flow rate no calculation
                /*               if (formsAdapter.flowRateParamId != -1) {
                                   val value = formsAdapter.getCurrentReading(formsAdapter.flowRateParamId)
                                       ?: return null
                                   if (value.isEmpty()) return null
                               }*/

                if (formActivity.curSetID > 1) {
                    val expList =
                        StringUtils.substringsBetween(expression, "(", ")")
                    //                    double valueLhs = getValueVolumePurgeCalculation(expList[1], false);
                    //                    double valueRhs = getValueVolumePurgeCalculation(expList[2], false);
                    //                    return String.valueOf(Double.parseDouble(stringValue) * (valueLhs - valueRhs));
                    return calculatePurgeVolumeExp(expList, expression, metaData, false)
                }
                return ""
            } else if (expression.contains("VolumePurged")) {
                //if no flow rate no calculation
                /*         if (formsAdapter.flowRateParamId != -1) {
                             val value = formsAdapter.getCurrentReading(formsAdapter.flowRateParamId)
                                 ?: return null
                             if (value.isEmpty()) return null
                         }*/

                if (formActivity.curSetID > 1) {
                    val expList =
                        StringUtils.substringsBetween(expression, "(", ")")
                    //                    double valueLhs = getValueVolumePurgeCalculation(expList[0], true);
                    //                    double valueRhs = getValueVolumePurgeCalculation(expList[1], true);
                    //                    return String.valueOf(valueLhs + valueRhs);
                    return calculatePurgeVolumeExp(expList, expression, metaData, true)
                }
                return ""
            } else if (expression.lowercase().contains("!!set!!")) {
                if (isExpressionQueryValid(expression)) {
                    val query: String = replaceSetOrVisibleQueryCols(expression)
                    return if (query.isNotEmpty()) {
                        FieldDataSource(formActivity).hitExpressionQuery(query)
                    } else null
                }
            } else if (expression.lowercase().contains("!!coc!!")) {
                if (isExpressionQueryValid(expression)) {
                    val query: String = replaceCOCQueryCols(expression)
                    return if (query.isNotEmpty()) {
                        val returnedValues =
                            FieldDataSource(formActivity).hitCOCExpressionQuery(query)
                        if (formActivity.currCocID != null
                            && !formActivity.currCocID.equals("0", ignoreCase = true)
                        ) {
                            handleSample3Data(
                                returnedValues.date, returnedValues.time,
                                returnedValues.stringValue
                            )
                        }
                        returnedValues.stringValue
                    } else null
                }
            } else if (expression.lowercase().contains("!!visible!!")) {
                if (isExpressionQueryValid(expression)) {
                    expression = expression.replace("true", "1")
                    expression = expression.replace("false", "0")
                    val query: String = replaceSetOrVisibleQueryCols(expression)
                    if (query.isNotEmpty()) {
                        val showField =
                            FieldDataSource(formActivity).hitExpressionQuery(query) == "1"

                        metaData.isRowVisible = showField
                        metaData.isVisible = showField
                        formsAdapter.setFieldVisibility(metaData)
                    }
                }
                return null
            } else {
                val fieldIDList = java.util.ArrayList<String>()
                if (expression.contains(",")) {
                    val pattern = ",\\d"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(expression)
                    while (m.find()) {
                        val item = expression!!.substring(m.start(), m.end())
                        if (item.contains(",")) try {
                            decimalPlaces = item.replace(",", "").toInt()
                            expression = expression.replace(item, "")
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                            decimalPlaces = 2
                        }
                    }
                }
                if (expression!!.contains("@")) {
                    val timeFieldIDs: HashMap<String, String?> =
                        getTimeFieldIdValue(metaData, expression) //Collect timeInMinutes
                    if (timeFieldIDs.size > 0) {
                        for (key in timeFieldIDs.keys) {
                            val value = timeFieldIDs[key]
                            expression = if (value != null && value.isNotEmpty()) {
                                expression!!.replace(key, value)
                            } else {
                                return null
                            }
                        }
                    }
                }
                if (expression!!.contains("{")) {
                    val replacement = HashMap<String, String>()
                    val pattern = "\\{\\d+\\}\\#(.*?)\\#\\|\\d+\\|"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(expression)
                    while (m.find()) {
                        val item = expression.substring(m.start(), m.end())
                        var mobAppID = ""
                        var fParamid: String? = null
                        var setID = 0
                        var value: String? = null
                        if (item.contains("{")) mobAppID =
                            item.substring(item.indexOf("{") + 1, item.lastIndexOf("}"))
                        if (item.contains("#")) setID =
                            item.substring(item.indexOf("#") + 1, item.lastIndexOf("#")).toInt()
                        if (mobAppID == null || mobAppID.isEmpty()) {
                            mobAppID = metaData.currentFormID.toString()
                        }
                        if (item.contains("|")) {
                            fParamid =
                                item.substring(item.indexOf("|") + 1, item.lastIndexOf("|"))
                            if (setID <= 0) {
                                if (setID < 0) { //No. is Negative
                                    val res: Int = formActivity.curSetID + setID
                                    setID = res
                                    value = if (setID <= 0) {
                                        null
                                    } else {
                                        fp.getStringValueFromId(
                                            eventId.toInt(),
                                            locationId,
                                            mobAppID.toInt(),
                                            setID,
                                            fParamid
                                        )
                                    }
                                } else if (setID == 0) {
                                    setID = formActivity.curSetID
                                    value = fp.getStringValueFromId(
                                        eventId.toInt(),
                                        locationId,
                                        mobAppID.toInt(),
                                        setID,
                                        fParamid
                                    )
                                }
                            } else {
                                value = fp.getStringValueFromId(
                                    eventId.toInt(),
                                    locationId,
                                    mobAppID.toInt(),
                                    setID,
                                    fParamid
                                )
                            }
                            if (value == null || value.isEmpty()) {
                                return null
                            }
                            replacement[item] = value
                            // expression=expression.replace(item,value);
                        }
                    }
                    if (replacement.size > 0) {
                        for (key in replacement.keys) {
                            val value = replacement[key]
                            expression = if (value != null && value.isNotEmpty()) {
                                expression!!.replace(key, value)
                            } else {
                                return null
                            }
                        }
                    }
                } else if (expression.contains("|")) {
                    val pattern = "\\|\\d+\\|"
                    val p = Pattern.compile(pattern)
                    val m = p.matcher(expression)
                    while (m.find()) {
                        val item = expression.substring(m.start(), m.end())
                        val pattern1 = "\\d+"
                        val p1 = Pattern.compile(pattern1)
                        val m1 = p1.matcher(item)
                        while (m1.find()) {
                            val item1 = item.substring(m1.start(), m1.end())
                            if (!fieldIDList.contains(item1)) {
                                fieldIDList.add(item1)
                            }
                        }
                    }
                    for (i in fieldIDList.indices) {
                        val fieldParamID = fieldIDList[i]
                        //                      int lastSetID = fp.getLastWorkingFieldSetID(locationId, ObjContext.getSiteID(), ObjContext.getCurrentAppID());
                        val value = fp.getStringValueFromId(
                            eventId.toInt(),
                            locationId,
                            metaData.currentFormID,
                            formActivity.curSetID,
                            fieldParamID
                        )
                        if (value != null && value.isNotEmpty()) {
                            expression = expression!!.replace(
                                "|" + fieldIDList[i] + "|",
                                value
                            ) //(value == null || value.isEmpty()) ? 0 + "" : value)
                        }
                    }
                }
                val prs = Parser()
                println("Output Expression:$expression")
                var result = prs.parse(expression)
                Log.i("NormalExpression ", " Result:$result")
                when (result) {
                    "NaN" -> {
                        result = null
                    }

                    "Infinity" -> {
                        result = null
                    }

                    else -> {
                        try {
                            if (!result!!.contains("Error")) result = Util.round(
                                result
                                    .replace(",".toRegex(), ".").toDouble(),
                                decimalPlaces
                            ).toString()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Log.e(TAG, "Error in parsing double:" + e.message)
                        }
                    }
                }
                return result
            } //NORMAL EXPRESSION end
        }
        return null
    }

    private fun getTimeFieldIdValue(
        metaData: MetaData,
        expression: String
    ): HashMap<String, String?> {
        val fieldIDValue = HashMap<String, String?>()
        val pattern = "@(.*?)@"
        val p = Pattern.compile(pattern)
        val m = p.matcher(expression)
        while (m.find()) {
            val item = expression.substring(m.start(), m.end())
            if (!fieldIDValue.containsKey(item)) {
                val timeInMinutes = getTimeInMinutesFromExpression(metaData, item)
                fieldIDValue[item] = timeInMinutes
            }
        }
        return fieldIDValue
    }

    fun getTimeInMinutesFromExpression(
        metaData: MetaData,
        timeExpression: String
    ): String? { //@{mobApp}#setID#|field_param_ID|@ OR @|field_param_ID|@
        val fp = FieldDataSource(context)
        var resultString: String? = null
        try {
            if (timeExpression.contains("|")) {
                var mobApp = "0"
                var set = 0
                if (timeExpression.contains("{")) {
                    mobApp = timeExpression.substring(
                        timeExpression.indexOf("{") + 1,
                        timeExpression.lastIndexOf("}")
                    )
                }
                if (timeExpression.contains("#")) {
                    set = timeExpression.substring(
                        timeExpression.indexOf("#") + 1,
                        timeExpression.lastIndexOf("#")
                    ).toInt()
                }
                if (mobApp == "0") {
                    mobApp = metaData.currentFormID.toString()
                }
                val fpID: String = timeExpression.substring(
                    timeExpression.indexOf("|") + 1,
                    timeExpression.lastIndexOf("|")
                )
                if (set <= 0) {
                    if (set < 0) { //No. is Negative
                        val res: Int = formActivity.curSetID + set
                        set = res
                        resultString = if (set <= 0) {
                            return null
                        } else {
                            fp.getMeasurmentTimeFromId(
                                eventId.toInt(),
                                locationId,
                                mobApp.toInt(),
                                set,
                                fpID
                            )
                        }
                    } else if (set == 0) {
                        set = formActivity.curSetID
                        resultString = fp.getMeasurmentTimeFromId(
                            eventId.toInt(),
                            locationId,
                            mobApp.toInt(),
                            set,
                            fpID
                        )
                    }
                } else {
                    resultString = fp.getMeasurmentTimeFromId(
                        eventId.toInt(),
                        locationId,
                        mobApp.toInt(),
                        set,
                        fpID
                    )
                }
                try {
                    if (resultString != null && resultString != "0") {
                        val millis = resultString.toLong()
                        val minutes: String = (millis / 60000).toString() //60000 milis = 1 min
                        Log.i(
                            TAG,
                            "getTimeInMinutesFromExpression $timeExpression: $minutes"
                        )
                        return minutes
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    Log.e(TAG, "Error in SampleID2 Calculation:" + e.message)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(TAG, "getTimeInMinutesFromExpression :" + e.message)
        }
        return null
    }

    fun buildConditionalExpressionResult(
        lhs: Double, operator: String?,
        rhs: Double, trueValue: String?, falseValue: String?
    ): String? {
        val result = "0"
        when (operator) {
            ">" -> return if (lhs > rhs) {
                trueValue
            } else {
                falseValue
            }

            "<" -> return if (lhs < rhs) {
                trueValue
            } else {
                falseValue
            }

            "==" -> return if (lhs == rhs) {
                trueValue
            } else {
                falseValue
            }

            "!=" -> return if (lhs != rhs) {
                trueValue
            } else {
                falseValue
            }

            ">=" -> return if (lhs >= rhs) {
                trueValue
            } else {
                falseValue
            }

            "<=" -> return if (lhs <= rhs) {
                trueValue
            } else {
                falseValue
            }
        }
        return result
    }

    //can start from bg thread as UI thread is managed inside
    fun handleNumericDataForCalculatedFields(
        metaData: MetaData,
        value: String?,
        numericEditText: EditText
    ) {

        var highlimitdefault: Double? = null
        var lowlimitdefault: Double? = null

        var builder: AlertDialog.Builder? = null
        var alertdialog: AlertDialog? = null

        var computedValue = 0.0
        var msg: String? = null
        if (value != null && value.isNotEmpty() && Util.hasDigitDecimalOnly(value)) {
            try {
                computedValue = value.toDouble()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            //09-Sep-16
            val dv = DefaultValueDataSource(context)
            val d_model = dv.getDefaultValueToWarn(
                locationId,
                metaData.currentFormID.toString(),
                metaData.metaParamID.toString(), formActivity.curSetID.toString()
            )
            var warninghighlimit: Double? = null
            var warninglowlimit: Double? = null
            if (d_model != null) {
                var high = d_model.highLimitDefaultValue
                var low = d_model.lowLimitDefaultValue
                var warn_low = d_model.warningLowDefaultValue
                var warn_high = d_model.warningHighDefaultValue

                /*if ((d_model.getHighLimit() == null || d_model.getHighLimit().equals(""))){
                            if (metaData.getRoutineId() == 111){
                                high = String.valueOf(metaData.getMetaHighLimit());
                            }
                        }
                        if ((d_model.getLowLimit() == null || d_model.getLowLimit().equals(""))){
                            if (metaData.getRoutineId() == 111){
                                low = String.valueOf(metaData.getMetaLowLimit());
                            }
                        }
                        if ((d_model.getWarningHighDefaultValue() == null || d_model.getWarningHighDefaultValue().equals(""))){
                            if (metaData.getRoutineId() == 111){
                                warn_high = String.valueOf(metaData.getMetaWarningHigh());
                            }
                        }
                        if ((d_model.getWarningLowDefaultValue() == null || d_model.getWarningLowDefaultValue().equals(""))){
                            if (metaData.getRoutineId() == 111){
                                warn_low = String.valueOf(metaData.getMetaWarningLow());
                            }
                        }

                        if (metaData.getRoutineId() == 111){
                            Log.e("limitsMetaData", "getFormMasterData: H:- "+high+" L:- "+low+" WH:- "+warn_high+" WL:- "+warn_low+" default value: "+d_model.getDefaultValue()+" RoutineId:- "+metaData.getRoutineId());
                        }*/if (high == null || high.isEmpty()) {
                    if (metaData.routineId == 111) {
                        high = metaData.metaHighLimit.toString()
                        highlimitdefault = high.toDouble()
                    } else {
                        highlimitdefault = null
                    }
                } else {
                    highlimitdefault = high.toDouble()
                }
                if (low == null || low.isEmpty()) {
                    if (metaData.routineId == 111) {
                        low = metaData.metaLowLimit.toString()
                        lowlimitdefault = low.toDouble()
                    } else {
                        lowlimitdefault = null
                    }
                } else {
                    lowlimitdefault = low.toDouble()
                }
                if (warn_low == null || warn_low.isEmpty()) {
                    if (metaData.routineId == 111) {
                        warn_low = metaData.metaWarningLow.toString()
                        warninglowlimit = warn_low.toDouble()
                    } else {
                        warninglowlimit = null
                    }
                } else {
                    warninglowlimit = warn_low.toDouble()
                }
                if (warn_high == null || warn_high.isEmpty()) {
                    if (metaData.routineId == 111) {
                        warn_high = metaData.metaWarningHigh.toString()
                        warninghighlimit = warn_high.toDouble()
                    } else {
                        warninghighlimit = null
                    }
                } else {
                    warninghighlimit = warn_high.toDouble()
                }
            }

            val bothDefaultHasZero = (highlimitdefault != null && lowlimitdefault != null
                    && highlimitdefault == 0.0 && lowlimitdefault == 0.0)

            val bothWarningHasZero = (warninghighlimit != null && warninglowlimit != null
                    && warninghighlimit == 0.0 && warninglowlimit == 0.0)


            if (bothDefaultHasZero) {
                highlimitdefault = null
                lowlimitdefault = null
            }

            if (bothWarningHasZero) {
                warninghighlimit = null
                warninglowlimit = null
            }

            if (highlimitdefault == null && lowlimitdefault == null) {
                STORE_VALUE = true
                if (warninghighlimit == null && warninglowlimit == null) { //no high-low and warning high-low present
                    //24-Oct-16 Store value as it is (No validation)
                } else if (warninghighlimit == null) { //24-Oct-16 x to Positive Values
                    if (computedValue >= warninglowlimit!!) {
                        //Valid No.
                    } else {
                        //Not valid
                        msg = "Value is Low.\n Value should be greater than :$warninglowlimit"
                    }
                } else if (warninglowlimit == null) {
                    if (computedValue <= warninghighlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        msg = "Value is High.\n Value should be less than :$warninghighlimit"
                    }
                } else { //24-Oct-16 Both are present
                    if (computedValue <= warninghighlimit && computedValue >= warninglowlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        msg =
                            "Value should be in between the range :$warninglowlimit To $warninghighlimit"
                    }
                }
            } else if (highlimitdefault == null) { //24-Oct-16 x to Positive Values
                STORE_VALUE = false
                if (warninghighlimit == null && warninglowlimit == null) { //24-Oct-16 no high-low and warning high-low present
                    if (lowlimitdefault != null && computedValue >= lowlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true
                    } else {
                        //Not valid
                        msg = "Value is Low.\n Value should be greater than :$lowlimitdefault"
                    }
                } else if (warninghighlimit == null) { //24-Oct-16 x to Positive Values
                    if (computedValue >= warninglowlimit!!) {
                        //Valid No.
                        STORE_VALUE = true
                    } else if (lowlimitdefault != null &&
                        computedValue >= lowlimitdefault && computedValue < warninglowlimit
                    ) {
                        //Not valid
                        STORE_VALUE = true
                        msg = "Value is Low.\n Value should be greater than :$warninglowlimit"
                    } else if (lowlimitdefault != null &&
                        computedValue < lowlimitdefault
                    ) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg = "Value is Low.\n Value should be greater than :$warninglowlimit"
                    }
                } else if (warninglowlimit == null) {
                    if (lowlimitdefault != null && computedValue < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range :$lowlimitdefault To $warninghighlimit"
                    } else if (computedValue >= warninghighlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value should be in between the range :$lowlimitdefault To $warninghighlimit"
                    } else if (lowlimitdefault != null &&
                        computedValue >= lowlimitdefault && computedValue <= warninghighlimit
                    ) {
                        //Valid No.
                        STORE_VALUE = true
                    }
                } else {
                    if (lowlimitdefault != null && computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range :$warninglowlimit To $warninghighlimit"
                    } else if (lowlimitdefault != null &&
                        computedValue >= lowlimitdefault && computedValue <= warninglowlimit
                    ) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= warninglowlimit
                        && computedValue <= warninghighlimit
                    ) {
                        STORE_VALUE = true
                    } else if (computedValue > warninghighlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    }
                }
            } else if (lowlimitdefault == null) { //24-Oct-16  x to Negative values
                STORE_VALUE = false
                if (warninghighlimit == null && warninglowlimit == null) { //24-Oct-16 no high-low and warning high-low present
                    if (computedValue <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true
                    } else {
                        //Not valid
                        msg = "Value is High.\n Value should be less than :$highlimitdefault"
                    }
                } else if (warninghighlimit == null) { //24-Oct-16 x to Positive Values
                    if (computedValue <= warninglowlimit!!) {
                        //Valid No.
                        STORE_VALUE = true
                        msg =
                            "Value is Low.\n Value should be in between the range :" +
                                    "$warninglowlimit To $highlimitdefault"
                    } else if (computedValue <= highlimitdefault
                        && computedValue >= warninglowlimit
                    ) {
                        //Not valid
                        STORE_VALUE = true
                    } else if (computedValue > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg =
                            "Value is High.\n Value should be in between the range :" +
                                    "$warninglowlimit To $highlimitdefault"
                    }
                } else if (warninglowlimit == null) {
                    if (computedValue > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false
                        msg = "Value should be less than:$warninghighlimit"
                    } else if (computedValue <= warninghighlimit) {
                        STORE_VALUE = true
                    } else if (computedValue <= highlimitdefault
                        && computedValue >= warninghighlimit
                    ) {
                        //Valid No.
                        STORE_VALUE = true
                        msg = "Value should be less than:$warninghighlimit"
                    }
                } else {
                    if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    } else if (computedValue <= highlimitdefault
                        && computedValue >= warninghighlimit
                    ) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= warninglowlimit
                        && computedValue <= warninghighlimit
                    ) {
                        STORE_VALUE = true
                    } else if (computedValue < warninglowlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    }
                }
            } else { //24-Oct-16 Both are present
                STORE_VALUE = false
                if (warninghighlimit == null && warninglowlimit == null) { //24-Oct-16 no high-low and warning high-low present
                    if (computedValue >= lowlimitdefault && computedValue <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true
                    } else {
                        //Not valid No.
                        STORE_VALUE = false
                        msg =
                            " Value should be in between the range :" +
                                    "$lowlimitdefault To $highlimitdefault"
                    }
                } else if (warninglowlimit == null) {
                    if (computedValue > warninghighlimit!! && computedValue <= highlimitdefault) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$lowlimitdefault To $warninghighlimit"
                    } else if (computedValue >= lowlimitdefault
                        && computedValue <= warninghighlimit
                    ) {
                        STORE_VALUE = true
                    } else if (computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$lowlimitdefault To $warninghighlimit"
                    } else if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$lowlimitdefault To $warninghighlimit"
                    }
                } else if (warninghighlimit == null) {
                    if (computedValue >= lowlimitdefault && computedValue < warninglowlimit) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$warninglowlimit To $highlimitdefault"
                    } else if (computedValue >= warninglowlimit
                        && computedValue <= highlimitdefault
                    ) {
                        STORE_VALUE = true
                    } else if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$warninglowlimit To $highlimitdefault"
                    } else if (computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$warninglowlimit To $highlimitdefault"
                    }
                } else {

                    //24-Oct-16 High,low,warn_high n warn_low  all has value
                    if (computedValue < lowlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= lowlimitdefault
                        && computedValue < warninglowlimit
                    ) {
                        STORE_VALUE = true
                        msg =
                            "Value is Low.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    } else if (computedValue >= warninglowlimit
                        && computedValue <= warninghighlimit
                    ) {
                        STORE_VALUE = true
                    } else if (computedValue > warninghighlimit
                        && computedValue <= highlimitdefault
                    ) {
                        STORE_VALUE = true
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    } else if (computedValue > highlimitdefault) {
                        STORE_VALUE = false
                        msg =
                            "Value is High.Value should be in between the range :" +
                                    "$warninglowlimit To $warninghighlimit"
                    }
                }
            }
            if (msg != null) {
                if (builder == null) {
                    builder = AlertDialog.Builder(context)
                }
                builder.setTitle("Alert")
                builder.setMessage("" + msg)
                builder.setCancelable(true)
                builder.setNeutralButton("OK", null)
                if (alertdialog == null || !alertdialog.isShowing) {
                    scopeMainThread.launch {
                        alertdialog = builder.create()
                        alertdialog!!.show()
                    }
                }
                val fieldDataSource = FieldDataSource(context)
                if (fieldDataSource.isParamIdExists(
                        formActivity.curSetID, eventId.toInt(),
                        locationId, siteId.toInt(), metaData.currentFormID,
                        deviceId, userId!!.toInt(), metaData.metaParamID
                    )
                ) {
                    if (STORE_VALUE) {
                        metaData.currentReading = value
                    } else {
                        metaData.currentReading = null
                    }
                    saveDataAndUpdateCreationDate(metaData)
                } else {
                    if (STORE_VALUE) {
                        val bfieldData: List<FieldData> =
                            getBlankFieldData(metaData, formActivity.curSetID)
                        fieldDataSource.insertFieldDataList(
                            bfieldData,
                            userId!!.toInt(),
                            deviceId
                        )
                        metaData.currentReading = value
                        saveDataAndUpdateCreationDate(
                            metaData,
                        )
                    } else {
                        val bfieldData: List<FieldData> =
                            getBlankFieldData(metaData, formActivity.curSetID)
                        fieldDataSource.insertFieldDataList(
                            bfieldData,
                            userId!!.toInt(),
                            deviceId
                        )
                        metaData.currentReading = null
                        saveDataAndUpdateCreationDate(
                            metaData,
                        )
                    }
                }
            } else {
                metaData.currentReading = value
                saveDataAndUpdateCreationDate(
                    metaData,
                )
            }
        } else {
            val fieldDataSource = FieldDataSource(context)
            if (!fieldDataSource.isParamIdExists(
                    formActivity.curSetID, eventId.toInt(),
                    locationId, siteId.toInt(), metaData.currentFormID,
                    deviceId, userId!!.toInt(), metaData.metaParamID
                )
            ) {
                val bfieldData: List<FieldData> =
                    getBlankFieldData(metaData, formActivity.curSetID)
                fieldDataSource.insertFieldDataList(bfieldData, userId!!.toInt(), deviceId)
            }
            if (numericEditText.text.toString().isEmpty())
                saveDataAndUpdateCreationDate(metaData)
        }
    }

    private fun replaceCOCQueryCols(expr: String): String {
        var expression = expr
        expression = expression.replace("!!coc!!", "")
        val mapCols = HashMap<String, String>()
        mapCols["d_field_data"] = "d_FieldData"
        mapCols["string_value"] = "StringValue"
        mapCols["field_parameter_id"] = "FieldParameterID"
        mapCols["location_id"] = "LocationID"
        mapCols["location"] = "Location"
        mapCols["event_id"] = "EventID"
        mapCols["cu_loc_id"] = locationId
        mapCols["cu_eve_id"] = eventId
        for ((key, value) in mapCols) {
            expression = expression.replace(key, value)
        }
        return expression
    }

    private fun replaceValidateQueryCols(expression: String): String? {
        var expr = expression
        expr = expr.replace("!!validate!!", "")
        val mapCols = HashMap<String, String>()
        mapCols["d_field_data"] = "d_FieldData"
        mapCols["string_value"] = "StringValue"
        mapCols["field_parameter_id"] = "FieldParameterID"
        mapCols["mobile_app_id"] = "MobileAppID"
        mapCols["location_id"] = "LocationID"
        mapCols["event_id"] = "EventID"
        mapCols["set_id"] = "ExtField1"
        mapCols["cu_loc_id"] = locationId
        mapCols["cu_eve_id"] = eventId
        mapCols["cu_ext_field1"] = formActivity.curSetID.toString()

        for ((key, value) in mapCols) {
            expr = expr.replace(key, value)
        }
        return expr
    }

    private fun calculatePurgeVolumeExp(
        exprList: Array<String>,
        expression: String,
        metaData: MetaData,
        isVolumePurged: Boolean
    ): String? {

        //don't be smart to add replace method directly to expression else the below
        //VolumePurge text replaced will erase the fieldOperand's text every time leading
        // to undetected by if condition of expr check
        var expr = StringBuilder(expression).toString()
        val expList = ArrayList(listOf(*exprList))
        expr = if (isVolumePurged) expr.replace("VolumePurged", "") else {
            expr.replace("PurgeCalculation", "")

            /*            String f_paramID = null;
                                String exp1 = expList.get(0).replace("(", "").replace(")", "");
                                if (exp1.contains("|")) {
                                    f_paramID = exp1.substring(exp1.indexOf("|") + 1,
                                            exp1.lastIndexOf("|"));
                                }

                                FieldDataSource fieldDataSource = new FieldDataSource(context);
                                String stringValue = fieldDataSource.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                        metaData.getCurrentFormID(), getCurrentSetID(), f_paramID);
                                expr = expr.replace(exp1, stringValue);
                                expList.remove(0);*/
        }
        for (exp in expList) {
            val expp = exp.replace("(", "").replace(")", "")
            val value: Double = getValueVolumePurgeCalculation(expp, metaData, isVolumePurged)
                ?: return null
            expr = expr.replace(expp, value.toString())
        }
        return parseExpression(expr)
    }

    private fun parseExpression(expression: String): String? {
        val prs = Parser()
        println("Output Expression:$expression")
        var result = prs.parse(expression)
        Log.i("NormalExpression ", " Result:$result")
        when (result) {
            "NaN" -> {
                result = null
            }

            "Infinity" -> {
                result = null
            }

            else -> {
                try {
                    if (!result!!.contains("Error")) result =
                        Util.round(
                            result.replace(",".toRegex(), ".").toDouble(),
                            2
                        ).toString()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "Error in parsing double:" + e.message)
                }
            }
        }
        return result
    }

    private fun getValueVolumePurgeCalculation(
        expression: String, metaData: MetaData,
        isVolumePurged: Boolean
    ): Double? {
        var mobAppID = ""
        var fParamId: String? = null
        var setID = 0
        var paramId: String? = null
        if (expression.contains("{")) {
            val pattern = "\\{\\d+\\}\\#(.*?)\\#\\|\\d+\\|"
            val p = Pattern.compile(pattern)
            val m = p.matcher(expression)
            while (m.find()) {
                val item = expression.substring(m.start(), m.end())
                if (item.contains("{")) mobAppID =
                    item.substring(item.indexOf("{") + 1, item.lastIndexOf("}"))
                if (item.contains("#")) setID =
                    item.substring(item.indexOf("#") + 1, item.lastIndexOf("#")).toInt()
                if (item.contains("|")) {
                    fParamId = item.substring(item.indexOf("|") + 1, item.lastIndexOf("|"))
                }
            }
        } else if (expression.contains("|")) {
            val pattern = "\\|\\d+\\|"
            val p = Pattern.compile(pattern)
            val m = p.matcher(expression)
            while (m.find()) {
                val item = expression.substring(m.start(), m.end())
                val pattern1 = "\\d+"
                val p1 = Pattern.compile(pattern1)
                val m1 = p1.matcher(item)
                while (m1.find()) {
                    paramId = item.substring(m1.start(), m1.end())
                }
            }
        }
        setID = if (setID == -1) formActivity.curSetID - 1 else formActivity.curSetID
        val fieldDataSource = FieldDataSource(context)
        var value: Double? = null
        if (isVolumePurged) {
            val stringValue = fieldDataSource.getStringValueFromId(
                eventId.toInt(),
                locationId,
                mobAppID.toInt(),
                setID,
                fParamId
            )
            if (stringValue != null && stringValue.isNotEmpty()) value = stringValue.toDouble()
        } else if (paramId != null) {
            val stringValue = fieldDataSource.getStringValueFromId(
                eventId.toInt(), locationId,
                metaData.currentFormID, formActivity.curSetID, paramId
            )
            if (stringValue != null && stringValue.isNotEmpty()) value = stringValue.toDouble()
        } else {
            val measurementTime = fieldDataSource.getMeasurementTimeByExt2n3(
                eventId.toInt(), locationId, mobAppID,
                setID, fParamId, formActivity.siteID
            )
            value = measurementTime.toDouble() / 60000 //converting secs to hours
        }
        return value
    }

    fun handleSample3Data(sampledate: String?, sampletime: String?, sampleID: String?) {
        var isLocationSampled = false
        val cocDS = CocDetailDataSource(context)
        isLocationSampled =
            cocDS.isCocAndLocationPresentAlready(formActivity.currCocID, locationId)
        if (isLocationSampled) {
            //UPDATE ROW
            cocDS.updateSampleID(
                sampledate,
                sampletime,
                sampleID,
                formActivity.currCocID,
                locationId,
                userId
            )
        } else {
            //INSERT NEW ROW
            val sDetailItem = SCocDetails()
            sDetailItem.sampleDate = sampledate
            sDetailItem.sampleTime = sampletime
            sDetailItem.sampleId = sampleID
            sDetailItem.creationDate = System.currentTimeMillis()
            sDetailItem.createdBy = userId!!.toInt()
            sDetailItem.modifiedBy = userId!!.toInt()
            sDetailItem.deleteFlag = 0
            sDetailItem.status = "COMPLETED"
            sDetailItem.cocFlag = 1
            sDetailItem.serverCreationDate = -1L
            sDetailItem.locationId = locationId.toLong()
            sDetailItem.cocId = Integer.valueOf(formActivity.currCocID)
            sDetailItem.cocDetailsId = System.currentTimeMillis().toString()
            cocDS.insertNewCoCdetail(sDetailItem)
        }
    }

    fun calculateSample4Expression(metaData: MetaData, sampleId: String?) {
        val fp = FieldDataSource(formActivity)
        val expression: String? = getExpressionFromMetaOrAttribute(metaData)
        if (expression != null && (expression.contains("SAMPLE4")
                    || expression.contains("DUPSAMPLE4"))
        ) {
            var dateString: String? = null
            var timeString: String? = null
            val resultString: String? = null
            val s3subexpression = expression.substring(
                expression.indexOf("(") + 1,
                expression.lastIndexOf(")")
            )
            val s3FpIds: Array<String?> = s3subexpression.split(",".toRegex()).toTypedArray()
            if (s3subexpression.contains("|")) {
                val mobApp = metaData.currentFormID.toString()
                val datefpID: String
                val timeFpID: String
                val set: Int = formActivity.curSetID
                if (s3FpIds[0] != null && s3FpIds[0]!!.isNotEmpty()) {
                    datefpID = s3FpIds[0]!!.substring(
                        s3FpIds[0]!!.indexOf("|") + 1,
                        s3FpIds[0]!!.lastIndexOf("|")
                    )
                    dateString = fp.getStringValueFromId(
                        eventId.toInt(),
                        locationId,
                        mobApp.toInt(),
                        set,
                        datefpID
                    )
                }
                if (s3FpIds[1] != null && s3FpIds[1]!!.isNotEmpty()) {
                    timeFpID = s3FpIds[1]!!.substring(
                        s3FpIds[1]!!.indexOf("|") + 1,
                        s3FpIds[1]!!.lastIndexOf("|")
                    )
                    timeString = fp.getStringValueFromId(
                        eventId.toInt(),
                        locationId,
                        mobApp.toInt(),
                        set,
                        timeFpID
                    )
                }
            }
            if (formActivity.currCocID != null
                && !formActivity.currCocID.equals("0", ignoreCase = true)
            ) {
                handleSample4Data(
                    dateString,
                    timeString,
                    sampleId,
                    expression.contains("DUPSAMPLE4")
                )
            }
        }
    }

    fun handleSample4Data(
        sampleDate: String?, sampleTime: String?, sampleID: String?,
        isDupSample4: Boolean
    ) {
        val cocDetailDataSource = CocDetailDataSource(formActivity)
        val cocMethods = cocDetailDataSource.getCOCMethodsForLocation(
            formActivity.currCocID,
            locationId, false
        )
        if (!isDupSample4) {
            for (method in cocMethods) {
                cocDetailDataSource.updateSampleID(
                    sampleDate, sampleTime, sampleID, formActivity.currCocID,
                    locationId, userId, method.methodId.toString(), 0
                )
            }
        } else {
            for (method in cocMethods) {
                val isMethodPresent = cocDetailDataSource.isCocMethodPresentAlready(
                    formActivity.currCocID,
                    locationId, method.methodId.toString(), 1
                )
                if (isMethodPresent) cocDetailDataSource.updateSampleID(
                    sampleDate, sampleTime, sampleID, formActivity.currCocID,
                    locationId, userId, method.methodId.toString(), 1
                ) else {
                    val cocMethod = SCocDetails()
                    cocMethod.sampleDate = sampleDate
                    cocMethod.sampleTime = sampleTime
                    cocMethod.sampleId = sampleID
                    cocMethod.creationDate = System.currentTimeMillis()
                    cocMethod.createdBy = userId!!.toInt()
                    cocMethod.modifiedBy = userId!!.toInt()
                    cocMethod.methodId = method.methodId
                    cocMethod.method = method.method
                    cocMethod.deleteFlag = 0
                    cocMethod.dupFlag = 1
                    cocMethod.status = "COMPLETED"
                    cocMethod.cocFlag = 1
                    cocMethod.serverCreationDate = -1L
                    cocMethod.locationId = locationId.toLong()
                    cocMethod.cocId = Integer.valueOf(formActivity.currCocID)
                    cocMethod.cocDetailsId = System.currentTimeMillis().toString()
                    cocDetailDataSource.insertNewCoCdetail(cocMethod)
                }
            }
        }
    }

    fun validateValue(metaData: MetaData, editText: CustomEditText) {
        val expression = metaData.fieldParameterOperands ?: return
        val strArray = Util.splitStringToArray("~", expression)
        for (expr in strArray) {
            if (expr != null && expr.isNotEmpty() && expr.lowercase()
                    .contains("!!validate!!")
            ) {
                if (isExpressionQueryValid(expr)) {
                    val query = replaceValidateQueryCols(expr)
                    if (query!!.isNotEmpty()) {
                        val value = FieldDataSource(formActivity).hitExpressionQuery(query)
                        if (value.isNotEmpty()) {
                            AlertManager.showNormalAlert(
                                formActivity.getString(R.string.alert),
                                value, formActivity.getString(R.string.ok), "",
                                false, formActivity
                            )
                        }
                    }
                }
            }
        }
    }

    fun openTaskActivity(metaData: MetaData) {
        val intent = Intent(formActivity, TaskTabActivity::class.java)
        val data = TaskIntentData()
        data.projectId = siteId
        data.locationId = locationId
        data.fieldParamId = metaData.metaParamID
        data.mobileAppId = formActivity.currentAppID
        data.setId = formActivity.curSetID
        intent.putExtra(GlobalStrings.TASK_INTENT_DATA, data)
        formActivity.startActivityForResult(intent, FormActivity.REQUEST_CODE_TASK)
    }

    fun openQRCodeActivity(fpId: Int) {
        if (formActivity.checkCameraPermission()) {
            val intent = Intent(formActivity, QRScannerActivity::class.java)
            intent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, fpId)
            formActivity.startActivityForResult(
                intent,
                BaseMenuActivity.REQUEST_CODE_BARCODE_SCANNER
            )
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                formActivity.requestCameraPermission()
            }
        }
    }

    //handled in defaultValueToSet Method as it is called everytime anyways
    fun setAsteriskIfRequiredField(metaData: MetaData, tvLabel: CustomTextView) {
        if (metaData.metaRequired_Y_N == "1") {
            scopeMainThread.launch {
                val label: String = tvLabel.text.toString() + "*"
                val wordToSpan: Spannable = SpannableString(label)
                wordToSpan.setSpan(
                    ForegroundColorSpan(Color.RED), wordToSpan.length - 1,
                    wordToSpan.length, 0
                )
                tvLabel.text = wordToSpan
            }
        }
    }

    //need ui thread
    fun addSupportingIcons(
        metaData: MetaData,
        layoutFieldControls: LinearLayout,
        curParentPos: Int
    ) {
        scopeMainThread.launch {

            //enable notes
            if (isEnableParameterNotes(metaData)) {
                val imageView = getImageView(metaData)

                if (metaData.isHasNote)
                    imageView.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.data_entry_note_blue_icon
                        )
                    )
                else imageView.setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.data_entry_gray_note_icon
                    )
                )
                layoutFieldControls.addView(imageView)

                imageView.setOnClickListener {
                    openNotesActivity(metaData)
                }
            }
            //notes end

            //param hint
            var paramHint = metaData.parameterHint

            val source = MetaDataAttributesDataSource(context)
            val attributes = source.getMetaDataAttributes(
                siteId.toInt(),
                formActivity.currentAppID, metaData.metaParamID
            )

            if (attributes != null) if (attributes.parameterHint != null
                && attributes.parameterHint.isNotEmpty()
            ) paramHint = attributes.parameterHint

            if (paramHint != null) {
                if (paramHint.isNotEmpty()) {
                    val imageView = getImageView(metaData)
                    imageView.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            formActivity,
                            R.drawable.ic_info, R.color.half_black
                        )
                    )

                    layoutFieldControls.addView(imageView)
                    imageView.setOnClickListener {
                        showAlertHint(paramHint)
                    }
                }
            }
            //param hint end

            //parent to expand and collapse
            if (metaData.isParentField
                && (metaData.metaInputType != null)
            ) {
                val imageView = AppCompatImageView(context)
                val layoutParams = LinearLayout.LayoutParams(
                    Util.dpToPx(24),
                    Util.dpToPx(24)
                )
                layoutParams.setMargins(0, 0, 5, 0)
                imageView.layoutParams = layoutParams
                imageView.tag = metaData.metaParamID

                if (!metaData.isExpanded)
                    imageView.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.arrow_right_enabled,
                            R.color.black_faint
                        )
                    )
                else imageView.setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.expand_arrow,
                        R.color.black_faint
                    )
                )

                layoutFieldControls.addView(imageView)

                imageView.setOnClickListener {
                    if (!metaData.isExpanded) {
                        metaData.isExpanded = true
                        imageView.setImageDrawable(
                            VectorDrawableUtils.getDrawable(
                                context,
                                R.drawable.expand_arrow
                            )
                        )
                    } else {
                        metaData.isExpanded = false
                        imageView.setImageDrawable(
                            VectorDrawableUtils.getDrawable(
                                context,
                                R.drawable.arrow_right_enabled
                            )
                        )
                    }
                    handleChildFieldsVisibility(metaData, curParentPos)
                }
            }
            //parent field end
        }
    }

    private fun handleChildFieldsVisibility(metaData: MetaData, curParentPos: Int) {
        if (metaData.childParamList == null)
            return

        val listToUpdate: ArrayList<MetaData> = ArrayList()

        if (formsAdapter.mapChildMetaObjects.containsKey(metaData.metaParamID)) {
            listToUpdate.addAll(formsAdapter.mapChildMetaObjects[metaData.metaParamID]!!)
        } else {
            for (fieldId in metaData.childParamList) {
                if (formsAdapter.mapMetaObjects.containsKey(fieldId))
                    formsAdapter.mapMetaObjects[fieldId]?.let { listToUpdate.add(it) }
            }
            formsAdapter.mapChildMetaObjects[metaData.metaParamID] = listToUpdate
        }
        formsAdapter.updateChildFields(curParentPos, listToUpdate, metaData)
    }

    fun addEnableFormsIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.pen_fillform
                )
            )

            imageView.setOnClickListener {
                val navigateToFormID: Int = metaData.formID
                Log.i(
                    TAG,
                    "Clicked on form:$navigateToFormID"
                )
                var index = 0 //= context.childAppList.indexOf(mData);

                val mobileAppSource = MobileAppDataSource(context)

                val childAppList =
                    mobileAppSource.getChildApps(
                        formsAdapter.parentFormId.toInt(),
                        formActivity.siteID,
                        formActivity.locationID
                    )

                if (navigateToFormID > 0) {
                    for (i in childAppList.indices) {
                        val app = childAppList[i]
                        if (app.appID == navigateToFormID) {
                            index = i
                            break
                        }
                    }
                    if (index > 0 && formActivity.currentFormNum != index) {
                        context.last_position = formActivity.currentFormNum
                        context.jumpToChildApp(index)
                    }
                }
            }
            layoutFieldControls.addView(imageView)
        }
    }

    fun addTaskIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.ic_task_list
                )
            )

            imageView.setOnClickListener {
                openTaskActivity(metaData)
            }
            layoutFieldControls.addView(imageView)
        }
    }

    fun addSummationIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {

            val imageView = AppCompatImageView(context)
            val layoutParams = LinearLayout.LayoutParams(
                Util.dpToPx(14),
                Util.dpToPx(14)
            )
            layoutParams.setMargins(0, 0, 5, 0)
            imageView.layoutParams = layoutParams
            imageView.tag = metaData.metaParamID

            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.ic_sigma
                )
            )
            imageView.setOnClickListener { formActivity.tvCalculate.performClick() }
            layoutFieldControls.addView(imageView)
        }
    }

    fun addGPSIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.ic_gps, R.color.black_faint
                )
            )

            imageView.setOnClickListener {
                val intent = Intent(formActivity, MapDragActivity::class.java)
                intent.putExtra(GlobalStrings.KEY_META_DATA, metaData)
                formActivity.startActivityForResult(
                    intent,
                    FormActivity.CAPTURE_GPS_LOCATION_REQUEST_CODE
                )
            }
            layoutFieldControls.addView(imageView)
        }
    }

    fun addBarcodeQRIcon(metaData: MetaData, drawable: Int, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    drawable
                )
            )

            imageView.setOnClickListener {
                openQRCodeActivity(metaData.metaParamID)
            }
            layoutFieldControls.addView(imageView)
        }
    }

    fun addWeatherIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    formActivity,
                    R.drawable.ic_cloud, R.color.black_faint
                )
            )

            imageView.setOnClickListener {
                openWeatherActivity()
            }
            layoutFieldControls.addView(imageView)
        }
    }

    fun addSignatureIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    formActivity,
                    R.drawable.signature_icon, R.color.black_faint
                )
            )

            imageView.setOnClickListener {
                startSignatureActivity(metaData)
            }

            layoutFieldControls.addView(imageView)
        }
    }

    fun addPhotosIcon(metaData: MetaData, layoutFieldControls: LinearLayout) {
        scopeMainThread.launch {
            val imageView = getImageView(metaData)
            imageView.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    formActivity,
                    R.drawable.ic_photo_camera, R.color.black_faint
                )
            )

            imageView.setOnClickListener {
                handleCamera(metaData)
            }

            layoutFieldControls.addView(imageView)
        }
    }

    fun startSignatureActivity(metaData: MetaData) {
        val signatureIntent = Intent(context, CaptureSignature::class.java)
        signatureIntent.putExtra("EVENT_ID", eventId)
        signatureIntent.putExtra("LOC_ID", locationId)
        signatureIntent.putExtra("APP_ID", metaData.currentFormID)
        signatureIntent.putExtra("SITE_ID", siteId)
        signatureIntent.putExtra("paramID", metaData.metaParamID)
        signatureIntent.putExtra("setID", formActivity.curSetID)
        signatureIntent.putExtra("SiteName", siteName)
        signatureIntent.putExtra("UserID", userId)
        formActivity.startActivityForResult(
            signatureIntent,
            FormActivity.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE
        )
    }

    fun isEnableParameterNotes(metaData: MetaData): Boolean {
        var enableNotes = metaData.isIsEnableParameterNotes

        val attributes = metaDataAttrSource.getMetaDataAttributes(
            siteId.toInt(),
            formActivity.currentAppID, metaData.metaParamID
        )

        if (attributes != null) if (attributes.isEnable_parameter_notes) enableNotes = true

        val inputType = metaData.metaInputType
        // below inputTypes should have notes icon only
        enableNotes =
            inputType != null && (enableNotes && (inputType.equals("RADIO", ignoreCase = true)
                    || inputType.equals("PICKER", ignoreCase = true)
                    || inputType.equals("COUNTER", ignoreCase = true)
                    || inputType.equals("AUTOCOMPLETE", ignoreCase = true)
                    || inputType.equals("MULTIAUTOCOMPLETE", ignoreCase = true)))

        return enableNotes
    }

    fun getImageView(metaData: MetaData): AppCompatImageView {

        val imageview = AppCompatImageView(context)
        val layoutParams = LinearLayout.LayoutParams(
            Util.dpToPx(20),
            Util.dpToPx(20)
        )
        layoutParams.setMargins(0, 0, 5, 0)
        imageview.layoutParams = layoutParams
        imageview.tag = metaData.metaParamID
        return imageview
    }

    fun openWeatherActivity() {
        val intentW = Intent(context, WeatherActivity::class.java)
        intentW.putParcelableArrayListExtra(
            GlobalStrings.KEY_META_DATA,
            ArrayList(formsAdapter.metaDataList)
        )
        intentW.putExtra(GlobalStrings.KEY_SET_ID, formActivity.curSetID)
        intentW.putExtra(GlobalStrings.KEY_LOCATION_ID, locationId)
        intentW.putExtra(GlobalStrings.KEY_EVENT_ID, eventId)
        intentW.putExtra(GlobalStrings.KEY_SITE_ID, siteId)
        formActivity.startActivityForResult(
            intentW,
            GlobalStrings.REQUEST_CODE_WEATHER
        )
    }

    private fun openNotesActivity(metaData: MetaData) {
        val noteDialog = Intent(
            context,
            NoteDialogBoxActivity::class.java
        )
        noteDialog.putExtra("EVENT_ID", eventId.toInt())
        noteDialog.putExtra("LOCATION_ID", locationId)
        noteDialog.putExtra("APP_ID", metaData.currentFormID)
        noteDialog.putExtra("SITE_ID", siteId.toInt())
        noteDialog.putExtra("paramID", metaData.metaParamID)
        noteDialog.putExtra("setID", formActivity.curSetID)
        noteDialog.putExtra("SiteName", siteName)
        noteDialog.putExtra("UserID", userId!!.toInt())

        formActivity.startActivityForResult(noteDialog, FormActivity.CAPTURE_NOTE_REQUEST_CODE)
    }

    fun showAlertHint(hint: String) {
        val builder = android.app.AlertDialog.Builder(context)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.layout_popup_parameter_hint, null, false)
        val type = Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf")
        val tvHint = view.findViewById<TextView>(R.id.tvParameterHint)
        tvHint.text = "Guide: $hint"
        tvHint.typeface = type
        builder.setView(view)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun handleCamera(metaData: MetaData) {
        try {
            val mediaIntent = Intent(formActivity, MediaPickerActivity::class.java)
            mediaIntent.putExtra(GlobalStrings.IS_CAMERA, true)
            mediaIntent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, metaData.metaParamID)
            mediaIntent.putExtra(GlobalStrings.IS_FROM_FORM_MASTER, true)
            formActivity.startActivityForResult(
                mediaIntent,
                BaseMenuActivity.REQUEST_CODE_FORM_MASTER_MEDIA_PICKER
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "handleCamera() error:" + e.localizedMessage)
        }
    }

    fun checkFieldsForVisibility(
        metaData: MetaData,
        position: Int,
        layoutItemText: ConstraintLayout?
    ) {
        val expression = metaData.fieldParameterOperands
        if (expression != null) {

            //if the expression will have "~" symbol then it has multiple queries
            val queryArray = Util.splitStringToArray("~", expression)
            if (queryArray.isNotEmpty()) {
                for (queryEx in queryArray) {
                    var queryExpr = queryEx
                    if (queryExpr.contains("!!visible!!")) {
                        if (isExpressionQueryValid(queryExpr)) {
                            queryExpr = queryExpr.replace("true", "1")
                            queryExpr = queryExpr.replace("false", "0")
                            val query = replaceSetOrVisibleQueryCols(queryExpr)
                            if (query.isNotEmpty()) {
                                val showField = FieldDataSource(formActivity)
                                    .hitExpressionQuery(query) == "1"
                                metaData.isRowVisible = showField
                                metaData.isVisible = showField

                                /*scopeMainThread.launch {
//                                   formsAdapter.updateVisibleFields(position, metaData)
                                        *//*if (metaData.isVisible) {
                                            val params = layoutItemText.layoutParams
                                            params.height =
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                            layoutItemText.layoutParams = params
                                            layoutItemText.visibility = View.VISIBLE
                                        } else {
                                            val params = layoutItemText.layoutParams
                                            params.height = 0
                                            layoutItemText.layoutParams = params
                                            layoutItemText.visibility = View.GONE
                                        }*//*
                                }*/
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkForSubFieldsForVisibility(metaData: MetaData, curParentPos: Int) {
        val listToUpdate: ArrayList<MetaData> = ArrayList()

        for ((_, metaValue) in formsAdapter.mapHiddenMetaObjects.entries) {
            val expression = metaValue.fieldParameterOperands
            if (expression != null) {

                //if the expression will have "~" symbol then it has multiple queries
                val queryArray = Util.splitStringToArray("~", expression)
                if (queryArray.isNotEmpty()) {
                    for (queryExp in queryArray) {
                        var queryExpr = queryExp
                        if (queryExpr.contains("!!visible!!")
                            && queryExpr.contains(metaData.metaParamID.toString())
                        ) {
                            if (isExpressionQueryValid(queryExpr)) {
                                queryExpr = queryExpr.replace("true", "1")
                                queryExpr = queryExpr.replace("false", "0")
                                val query: String = replaceSetOrVisibleQueryCols(queryExpr)
                                if (query.isNotEmpty()) {
                                    val showField = FieldDataSource(formActivity)
                                        .hitExpressionQuery(query) == "1"
                                    metaValue.isRowVisible = showField
                                    metaValue.isVisible = showField
                                    listToUpdate.add(metaValue)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (listToUpdate.isNotEmpty())
            formsAdapter.updateVisibleFields(curParentPos, listToUpdate)
    }
}

