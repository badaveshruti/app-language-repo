package qnopy.com.qnopyandroid.ui.forms

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.MetaData
import qnopy.com.qnopyandroid.ui.activity.FormActivity
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity
import qnopy.com.qnopyandroid.ui.forms.viewHolders.*
import qnopy.com.qnopyandroid.util.Util

const val LAYOUT_DATE = 0
const val input_type_date = "DATE"

const val LAYOUT_TIME = 1
const val input_type_time = "TIME"

const val LAYOUT_NUMERIC = 2
const val LAYOUT_NUMERIC_LAST2 = 478
const val LAYOUT_NUMERIC_LAST3 = 479
const val input_type_numeric = "NUMERIC"

const val LAYOUT_RADIO = 3
const val input_type_radio = "RADIO"

const val LAYOUT_TEXT = 4
const val input_type_text = "TEXT"

const val LAYOUT_COUNTER = 5
const val LAYOUT_PICKER = 6
const val LAYOUT_TASK = 7
const val LAYOUT_PHOTOS = 8
const val LAYOUT_GPS = 9

const val LAYOUT_MULTI_AUTOCOMPLETE = 10
const val input_type_multi_autocomplete = "MULTIAUTOCOMPLETE"

const val LAYOUT_AUTOCOMPLETE = 11
const val input_type_autocomplete = "AUTOCOMPLETE"

const val LAYOUT_SIGNATURE = 12
const val LAYOUT_WEATHER = 13

const val LAYOUT_TEXT_CONTAINER = 14
const val input_type_text_container = "TEXTCONTAINER"

const val LAYOUT_QRCODE = 15
const val LAYOUT_RATING = 16
const val LAYOUT_LABEL = 17
const val LAYOUT_CHECKBOX = 18

const val LAYOUT_TOTALIZER = 19
const val input_type_totalizer = "TOTALIZER"

const val LAYOUT_MULTI_METHODS = 22
const val input_type_multi_methods = "MULTIMETHODS"

const val LAYOUT_BARCODE = 20

const val LAYOUT_HEADER = 999

val job = Job()
val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
val scopeIO = CoroutineScope(job + Dispatchers.IO)

class FormsAdapter(
    val context: LocationDetailActivity,
    val formActivity: FormActivity,
    var metaDataList: ArrayList<MetaData>,
    var siteId: String,
    var locationId: String,
    var eventId: String,
    var parentFormId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var weatherMetaParamId: Int = 0
    var formOperations: FormOperations

    private var companyId: Int = 0
    private var userId: String? = ""
    private var deviceId: String? = ""
    private var siteName: String? = ""
    var isSampleDateOrTimeSet = false

    @JvmField
    var AUTO_SET_LAST_SELECTED_VALUES: String? = null

    @JvmField
    var AUTO_METHODS_LAST_SELECTED_VALUES: String? = null

    var flowRateParamId = -1
    var mapRemovedFields = HashMap<Int, MetaData>()
    var metaDataListAllFields: ArrayList<MetaData> = metaDataList.clone() as ArrayList<MetaData>

    val mapHiddenMetaObjects: HashMap<String, MetaData> = HashMap()
    val mapSetExprMetaObjects: HashMap<String, MetaData> = HashMap()
    val mapChildMetaObjects: HashMap<Int, ArrayList<MetaData>> = HashMap()

    var mapMetaObjects: HashMap<Int, MetaData> =
        HashMap() //we use this to avoid for loops to find same metaobject to work on

    @JvmField
    var AUTO_GENERATE = false

    @JvmField
    var hasAtLeastOneOperand = false

    @JvmField
    var hasSample4Operand = false

    init {
        userId = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID)
        companyId = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).toInt()
        deviceId = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID)
        siteName = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME)

        formOperations = FormOperations(
            context, formActivity, metaDataList,
            siteId, locationId, eventId, parentFormId, this
        )

        metaDataListAllFields.forEachIndexed { pos, metaData ->
            metaData.apply { rowPosition = pos }

            //for volume purge and purge calc check
            if (metaData.metaParamLabel.equals(
                    "Flow Rate",
                    ignoreCase = true
                )
            ) flowRateParamId =
                metaData.metaParamID

            scopeMainThread.launch {
                val expression: String? = formOperations.getExpressionFromMetaOrAttribute(metaData)
                if (expression != null) {

                    if (expression.lowercase().contains("!!set!!"))
                        mapSetExprMetaObjects[metaData.metaParamID.toString() + ""] = metaData

                    if (expression.contains("SAMPLE4") || expression.contains("DUPSAMPLE4"))
                        hasSample4Operand = true

                    if (!hasAtLeastOneOperand) {
                        if (expression.isNotEmpty()) {
                            hasAtLeastOneOperand = true
                            formActivity.setRefreshButtonVisibility(View.VISIBLE)
                        } else formActivity.setRefreshButtonVisibility(View.GONE)
                    }
                } else if (!hasAtLeastOneOperand) {
                    formActivity.setRefreshButtonVisibility(View.GONE)
                }
            }

            mapMetaObjects[metaData.metaParamID] = metaData

            metaData.fieldParameterOperands?.let {
                if (it.lowercase().contains("!!set!!"))
                    mapSetExprMetaObjects[metaData.metaParamID.toString()] = metaData
            }

            if (!metaData.isRowVisible) {
                mapHiddenMetaObjects[metaData.metaParamID.toString()] = metaData

                formOperations.checkFieldsForVisibility(metaData, pos, null)
                if (!metaData.isRowVisible) {
                    metaDataList.remove(metaData)
                }
            }

            if (metaData.isChildField) {
                metaDataList.remove(metaData)
            }
        }

        val auto = Util.getSharedPreferencesProperty(context, GlobalStrings.AUTO_GENERATE)
        AUTO_GENERATE = if (auto != null && auto.isNotEmpty()) {
            auto.toBoolean()
        } else {
            false
        }
    }

    override fun getItemCount(): Int = metaDataList.size

    override fun getItemViewType(position: Int): Int {
        val metaData = metaDataList[position]
        return when (metaData.metaInputType) {
            "DATE" -> LAYOUT_DATE
            "TIME" -> LAYOUT_TIME
            "TOTALIZER" -> LAYOUT_NUMERIC
            "NUMERIC" -> when {
                formOperations.isShowLast2(metaData) && metaData.currentFormID == 142 -> LAYOUT_NUMERIC
                formOperations.isShowLast2(metaData) && formActivity.curSetID > 1 -> LAYOUT_NUMERIC_LAST2
                formOperations.isShowLast3(metaData) && formActivity.curSetID > 2 -> LAYOUT_NUMERIC_LAST3
                else -> LAYOUT_NUMERIC
            }
            "RADIO" -> LAYOUT_RADIO
            "TEXTCONTAINER" -> LAYOUT_TEXT_CONTAINER
            "TEXT" -> LAYOUT_TEXT
            "AUTOSETGENERATOR", "MULTIAUTOCOMPLETE", "MULTIMETHODS" -> LAYOUT_MULTI_AUTOCOMPLETE
            "AUTOCOMPLETE" -> LAYOUT_AUTOCOMPLETE
            "PICKER" -> LAYOUT_PICKER
            "TASK" -> LAYOUT_TASK
            "GPS" -> LAYOUT_GPS
            "QRCODE", "BARCODE" -> LAYOUT_QRCODE
            "WEATHER" -> LAYOUT_WEATHER
            "PHOTOS" -> LAYOUT_PHOTOS
            "SIGNATURE" -> LAYOUT_SIGNATURE
            "CHECKBOX" -> LAYOUT_CHECKBOX
            "", null -> LAYOUT_HEADER
            /*
                                                                   "Counter" -> LAYOUT_COUNTER
                                                                   "RATING" -> LAYOUT_RATING
                                                                   "LABEL" -> LAYOUT_LABEL
           */
            else -> LAYOUT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null

        return when (viewType) {
            LAYOUT_DATE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date, parent, false)
                DateViewHolder(view, formOperations)
            }
            LAYOUT_TIME -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_time, parent, false)
                TimeViewHolder(view, formOperations)
            }
            LAYOUT_NUMERIC -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_numeric, parent, false)
                NumericViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_NUMERIC_LAST2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_numeric_showlast2, parent, false)
                NumericLast2ViewHolder(view, formOperations, formActivity, this)
            }
            LAYOUT_NUMERIC_LAST3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_numeric_showlast3, parent, false)
                NumericLast3ViewHolder(view, formOperations, formActivity, this)
            }
            LAYOUT_RADIO -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_radio, parent, false)
                RadioBtnViewHolder(view, formOperations)
            }
            LAYOUT_CHECKBOX -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_checkbox, parent, false)
                CheckBoxViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_TEXT -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
                TextViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_TEXT_CONTAINER -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
                TextViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_PICKER -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_picker, parent, false)
                PickerViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_AUTOCOMPLETE, LAYOUT_MULTI_AUTOCOMPLETE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_auto_complete, parent, false)
                MultiAutoCompleteViewHolder(view, formOperations, formActivity, this)
            }
            LAYOUT_GPS -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_gps, parent, false)
                GPSViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_TASK -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
                TaskViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_QRCODE, LAYOUT_BARCODE -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_barcode_qr, parent, false)
                BarcodeViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_WEATHER -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_gps, parent, false)
                WeatherViewHolder(view, formOperations, formActivity)
            }
            LAYOUT_PHOTOS -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_signature_photos, parent, false)
                PhotosViewHolder(view, formOperations, formActivity, this)
            }
            LAYOUT_SIGNATURE -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_signature_photos, parent, false)
                SignatureViewHolder(view, formOperations, formActivity, this)
            }
            /*   LAYOUT_LABEL -> {
                   view =
                       LayoutInflater.from(parent.context).inflate(R.layout.view_lable, parent, false)
                   LabelViewHolder(view)
               }//checkbox and label have same func */
            /*            LAYOUT_COUNTER -> {
              view = LayoutInflater.from(parent.context)
                  .inflate(R.layout.view_counter, parent, false)
              CounterViewHolder(view)
          }
          LAYOUT_RATING -> {
              view =
                  LayoutInflater.from(parent.context).inflate(R.layout.view_rating, parent, false)
              RatingViewHolder(view)
          }*/
            LAYOUT_HEADER -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(view, formActivity, formOperations)
            }
            else -> {
                view =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_text, parent,
                        false
                    )
                TextViewHolder(view, formOperations, formActivity)
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        Log.e("CoroutineScope", "Caught ${Log.getStackTraceString(ex)}")
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val metaData = metaDataList[position]

        scopeIO.launch(exceptionHandler) {
            formOperations.fetchEssentialData(metaData)

            when (viewHolder.itemViewType) {
                LAYOUT_DATE -> {
                    val holder = viewHolder as DateViewHolder
                    scopeMainThread.launch { holder.tvDateLabel.text = metaData.metaParamLabel }
                    holder.setDate(metaData, formActivity)
//                    if (metaData.viewHolder == null)
                    holder.setClickListener(metaData, context)
                }
                LAYOUT_TIME -> {
                    val holder = viewHolder as TimeViewHolder
                    scopeMainThread.launch { holder.tvTimeLabel.text = metaData.metaParamLabel }
                    holder.setTime(metaData, formActivity)
//                    if (metaData.viewHolder == null)
                    holder.setTimeClickListener(metaData, context)
                }
                LAYOUT_NUMERIC -> {
                    //Note: this will work for totalizer also as both have same layout
                    val holder = viewHolder as NumericViewHolder
                    scopeMainThread.launch { holder.tvNumericLabel.text = metaData.metaParamLabel }
                    holder.setNumericValue(metaData)
                    holder.setLabelChanges(metaData)
                    holder.setListeners(metaData)
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_NUMERIC_LAST2 -> {
                    val holder = viewHolder as NumericLast2ViewHolder
                    scopeMainThread.launch { holder.tvNumericLabel.text = metaData.metaParamLabel }
                    holder.setFieldControlIcons(metaData)
                    holder.setNumericValue(metaData)
                    holder.setLabelChanges(metaData)
                    holder.setListeners(metaData)
                }
                LAYOUT_NUMERIC_LAST3 -> {
                    val holder = viewHolder as NumericLast3ViewHolder
                    scopeMainThread.launch { holder.tvNumericLabel.text = metaData.metaParamLabel }
//                    if (metaData.viewHolder == null)
                    holder.setFieldControlIcons(metaData)
                    holder.setNumericValue(metaData)
                    holder.setLabelChanges(metaData)
                    holder.setListeners(metaData)
                }
                LAYOUT_RADIO -> {
                    val holder = viewHolder as RadioBtnViewHolder
                    scopeMainThread.launch { holder.tvRadioLabel.text = metaData.metaParamLabel }
                    holder.setRadioItems(metaData, formActivity, locationId)
//                    if (metaData.viewHolder == null)
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_TEXT, LAYOUT_TEXT_CONTAINER -> {
                    val holder = viewHolder as TextViewHolder
//                    holder.setIsRecyclable(false)
                    scopeMainThread.launch { holder.tvTextLabel.text = metaData.metaParamLabel }
                    holder.setTextValue(metaData, context)
                    /*if (metaData.viewHolder == null) {

                    }*/
                    holder.setTextChangeListener(metaData, context)
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_AUTOCOMPLETE, LAYOUT_MULTI_AUTOCOMPLETE -> {
                    val holder = viewHolder as MultiAutoCompleteViewHolder
                    scopeMainThread.launch {
                        holder.tvAutoCompleteLabel.text = metaData.metaParamLabel
                        holder.addChipsToContainer(metaData)
                    }

//                    if (metaData.viewHolder == null) {
                    holder.setFieldControlIcons(metaData)
//                    }
                }
                LAYOUT_PICKER -> {
                    val holder = viewHolder as PickerViewHolder
                    scopeMainThread.launch { holder.tvPickerLabel.text = metaData.metaParamLabel }
//                    if (metaData.viewHolder == null) {
                    holder.setPickerData(metaData)

                    scopeMainThread.launch {
                        holder.setPickerAdapter(metaData)
                    }
                    holder.setFieldControlIcons(metaData)
//                    }
                }
                LAYOUT_TASK -> {
                    val holder = viewHolder as TaskViewHolder
                    scopeMainThread.launch { holder.tvTaskLabel.text = metaData.metaParamLabel }
                    holder.setTaskCount(metaData)
                    holder.addClickListener(metaData)
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_GPS -> {
                    val holder = viewHolder as GPSViewHolder
                    scopeMainThread.launch { holder.tvGpsLabel.text = metaData.metaParamLabel }
                    holder.setGpsLocation(metaData)
//                    if (metaData.viewHolder == null) {
                    holder.addClickListener(metaData)
                    holder.setFieldControlIcons(metaData)
//                    }
                }
                LAYOUT_QRCODE, LAYOUT_BARCODE -> {
                    val holder = viewHolder as BarcodeViewHolder
                    scopeMainThread.launch {
                        holder.tvBarcodeLabel.text = metaData.metaParamLabel
                        holder.setBarcodeText(metaData)
                    }
//                    if (metaData.viewHolder == null) {
                    holder.addClickListener(metaData)
                    holder.setFieldControlIcons(metaData)
//                    }
                }
                LAYOUT_WEATHER -> {
                    val holder = viewHolder as WeatherViewHolder
                    weatherMetaParamId = metaData.metaParamID
                    scopeMainThread.launch { holder.tvWeatherLabel.text = metaData.metaParamLabel }
                    holder.setWeather(metaData)
//                    if (metaData.viewHolder == null)
                    holder.addIcons(metaData)
                }
                LAYOUT_PHOTOS -> {
                    val holder = viewHolder as PhotosViewHolder
                    scopeMainThread.launch {
                        holder.tvPhotosLabel.text = metaData.metaParamLabel
                        holder.setPhotos(metaData)
                    }
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_SIGNATURE -> {
                    val holder = viewHolder as SignatureViewHolder
                    scopeMainThread.launch {
                        holder.tvSignatureLabel.text = metaData.metaParamLabel
                        holder.setSignature(metaData)
                    }
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_CHECKBOX -> {
                    val holder = viewHolder as CheckBoxViewHolder
                    scopeMainThread.launch {
                        holder.tvCheckBoxLabel.text = metaData.metaParamLabel
                        holder.setCheckBoxViews(metaData)
                    }
                    holder.setFieldControlIcons(metaData)
                }
                LAYOUT_HEADER -> {
                    val holder = viewHolder as HeaderViewHolder
                    scopeMainThread.launch {
                        holder.tvHeaderLabel.text = metaData.metaParamLabel
                        holder.setLabelTypeFace(metaData)
                        holder.setFieldControlIcons(metaData);
                    }
                }
                else -> {
                    val holder = viewHolder as TextViewHolder
                    scopeMainThread.launch { holder.tvTextLabel.text = metaData.metaParamLabel }
                    holder.setTextValue(metaData, context)
//                    if (metaData.viewHolder == null) {
                    holder.setTextChangeListener(metaData, context)
                    holder.setFieldControlIcons(metaData)
//                    }
                }
            }

            metaData.viewHolder = viewHolder
        }
    }

    fun updateAutoCompleteView(paramId: Int, selectedValues: String) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == paramId }
        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            metaData.currentReading = selectedValues
            val viewHolder = metaData.viewHolder as MultiAutoCompleteViewHolder
            viewHolder.addChipsToContainer(metaData)
            notifyItemChanged(pos)
            formOperations.checkForSubFieldsForVisibility(
                metaData,
                viewHolder.absoluteAdapterPosition
            )
        }
    }

    fun getCurrentReading(paramId: Int): String? {
        return try {
            val metaData = metaDataList.single { metaData -> metaData.metaParamID == paramId }
            metaData.currentReading
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentReadingAndVisibility(paramId: Int): Boolean {
        return try {
            val metaData = metaDataList.single { metaData -> metaData.metaParamID == paramId }
            metaData.currentReading.isNullOrEmpty() && metaData.isRowVisible
        } catch (e: Exception) {
            false
        }
    }

    fun setFieldVisibility(metaData: MetaData) {
        if (metaData.viewHolder != null) {
            metaData.viewHolder.itemView.visibility =
                if (metaData.isVisible) View.VISIBLE else View.GONE
            notifyItemChanged(metaData.viewHolder.absoluteAdapterPosition)
        }
    }

    fun updateGpsLocation(metaParamID: Int, location: String) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            metaData.currentReading = location
            val holder = metaData.viewHolder as GPSViewHolder
            scopeIO.launch { holder.setGpsLocation(metaData) }
            formOperations.saveDataAndUpdateCreationDate(metaData)
            notifyItemChanged(pos)
        }
    }

    fun updateBottles(metaParamID: Int, value: String) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            metaData.currentReading = value
            bindViewHolder(metaData.viewHolder, pos)
            notifyItemChanged(pos)
        }
    }

    fun updateBottlesData(metaParamID: Int) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            bindViewHolder(metaData.viewHolder, pos)
            notifyItemChanged(pos)
        }
    }

    fun updateBarcode(metaParamID: Int, barcode: String) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            metaData.currentReading = barcode
            val holder = metaData.viewHolder as BarcodeViewHolder
            holder.setBarcodeText(metaData)
            formOperations.saveDataAndUpdateCreationDate(metaData)
            notifyItemChanged(pos)
        }
    }

    fun updateTask(metaParamID: Int) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            val holder = metaData.viewHolder as TaskViewHolder
            scopeIO.launch { holder.setTaskCount(metaData) }
            notifyItemChanged(pos)
        }
    }

    fun updateNotes(metaParamID: Int, isNoteTaken: Boolean) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            onBindViewHolder(metaData.viewHolder, pos)
            notifyItemChanged(pos)
        }
    }

    fun updateSignaturePhotos() {

    }

    fun updatePhotos(metaParamID: Int, path: String) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            val holder = metaData.viewHolder as PhotosViewHolder
            holder.updatePhotos(path)
            notifyItemChanged(pos)
        }
    }

    //used when returned from CardGalleryActivity
    fun refreshPhotos(metaParamID: Int) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            val holder = metaData.viewHolder as PhotosViewHolder
            bindViewHolder(holder, pos)
            notifyItemChanged(pos)
        }
    }

    fun refreshSignatures(metaParamID: Int) {
        val metaData = metaDataList.single { metaData -> metaData.metaParamID == metaParamID }

        if (metaData.viewHolder != null) {
            val pos = metaData.viewHolder.absoluteAdapterPosition
            val holder = metaData.viewHolder as SignatureViewHolder
            bindViewHolder(holder, pos)
            notifyItemChanged(pos)
        }
    }

    fun updateVisibleFields(parentPos: Int, metaDataListToUpdate: ArrayList<MetaData>) {

        if (metaDataListToUpdate.isEmpty())
            return

        var parPos: Int = parentPos + 1
        for (metaData in metaDataListToUpdate) {

            val position: Int
            val foundElement = metaDataList.find {
                it.metaParamID == metaData.metaParamID
            }

            if (metaData.isRowVisible) {
                if (foundElement == null) {
                    if (metaData.rowPosition >= metaDataList.size) {
                        position = metaDataList.size
                        metaDataList.add(position, metaData)
                        notifyItemInserted(position)
                    } else {
                        metaDataList.add(parPos, metaData)
                        position = parPos
                        parPos++
                        notifyItemInserted(position)
                    }
                    notifyItemRangeChanged(position, metaDataList.size)
                }
            } else {
                if (foundElement != null) {
                    val index = metaDataList.indexOf(metaData)
                    metaDataList.remove(metaData)
                    position = index
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(position, metaDataList.size)
                }
            }
        }
    }

    fun updateChildFields(
        parentPos: Int, metaDataListToUpdate: ArrayList<MetaData>,
        parentMetaData: MetaData
    ) {

        if (metaDataListToUpdate.isEmpty())
            return

        var parPos: Int = parentPos + 1
        for (metaData in metaDataListToUpdate) {

            val position: Int
            val foundElement = metaDataList.find {
                it.metaParamID == metaData.metaParamID
            }

            if (parentMetaData.isExpanded) {
                if (foundElement == null) {
                    if ((metaData.metaParamLabel.lowercase().contains("species")
                                && metaData.rowPosition >= metaDataList.size) || parPos >= metaDataList.size
                    ) {
                        position = metaDataList.size
                        metaDataList.add(position, metaData)
                        notifyItemInserted(position)
                    } else {
                        metaDataList.add(parPos, metaData)
                        position = parPos
                        notifyItemInserted(position)
                        parPos++
                    }
//                    notifyItemRangeInserted(parentPos + 1, metaDataList.size)
                }
            } else {
                if (foundElement != null) {
                    val index = metaDataList.indexOf(metaData)
                    metaDataList.remove(metaData)
                    position = index
                    notifyItemRemoved(index)
//                    notifyItemRangeRemoved(position, metaDataList.size)
                }
            }
        }

        notifyItemRangeChanged(parentPos + 1, metaDataList.size)
        if (parentMetaData.InputType != null && parentMetaData.InputType.equals(
                "weather",
                ignoreCase = true
            )
        ) notifyDataSetChanged()
    }
}