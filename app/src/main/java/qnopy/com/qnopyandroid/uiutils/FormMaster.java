package qnopy.com.qnopyandroid.uiutils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity.REQUEST_CODE_BARCODE_SCANNER;
import static qnopy.com.qnopyandroid.ui.activity.FormActivity.AUTOCOMPLETE_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.activity.FormActivity.CAPTURE_GPS_LOCATION_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.activity.FormActivity.CAPTURE_NOTE_REQUEST_CODE;
import static qnopy.com.qnopyandroid.ui.activity.FormActivity.CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.pchmn.materialchips.ChipView;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import qnopy.com.qnopyandroid.ExpressionParser.Parser;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.R;
import qnopy.com.qnopyandroid.adapter.ObservedPhotosAdapter;
import qnopy.com.qnopyandroid.adapter.SpinnerDropdownAdapter;
import qnopy.com.qnopyandroid.clientmodel.FieldData;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.clientmodel.MetaData;
import qnopy.com.qnopyandroid.clientmodel.MetaDataAttributes;
import qnopy.com.qnopyandroid.clientmodel.MobileApp;
import qnopy.com.qnopyandroid.customView.CustomButton;
import qnopy.com.qnopyandroid.customView.CustomTextView;
import qnopy.com.qnopyandroid.customWidgets.FBDateView;
import qnopy.com.qnopyandroid.customWidgets.FBTimeView;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;
import qnopy.com.qnopyandroid.db.CocDetailDataSource;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.db.DefaultValueDataSource;
import qnopy.com.qnopyandroid.db.FieldDataSource;
import qnopy.com.qnopyandroid.db.LocationDataSource;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.db.MetaDataAttributesDataSource;
import qnopy.com.qnopyandroid.db.MetaDataSource;
import qnopy.com.qnopyandroid.db.MethodDataSource;
import qnopy.com.qnopyandroid.db.MobileAppDataSource;
import qnopy.com.qnopyandroid.db.SampleMapTagDataSource;
import qnopy.com.qnopyandroid.db.TaskDetailsDataSource;
import qnopy.com.qnopyandroid.map.MapActivity;
import qnopy.com.qnopyandroid.requestmodel.CoCBottles;
import qnopy.com.qnopyandroid.requestmodel.CustomerSign;
import qnopy.com.qnopyandroid.requestmodel.SCocDetails;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;
import qnopy.com.qnopyandroid.responsemodel.DefaultValueModel;
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl;
import qnopy.com.qnopyandroid.signature.CaptureSignature;
import qnopy.com.qnopyandroid.signature.SignatureAdapter;
import qnopy.com.qnopyandroid.signature.SignatureUpdateListener;
import qnopy.com.qnopyandroid.ui.activity.AutoCompleteHandlerActivity;
import qnopy.com.qnopyandroid.ui.activity.BaseMenuActivity;
import qnopy.com.qnopyandroid.ui.activity.FormActivity;
import qnopy.com.qnopyandroid.ui.activity.LocationDetailActivity;
import qnopy.com.qnopyandroid.ui.activity.MapDragActivity;
import qnopy.com.qnopyandroid.ui.activity.NoteDialogBoxActivity;
import qnopy.com.qnopyandroid.ui.activity.ShowFilesActivity;
import qnopy.com.qnopyandroid.ui.mediaPicker.MediaPickerActivity;
import qnopy.com.qnopyandroid.ui.task.TaskIntentData;
import qnopy.com.qnopyandroid.ui.task.TaskTabActivity;
import qnopy.com.qnopyandroid.ui.weather.WeatherActivity;
import qnopy.com.qnopyandroid.util.AlertManager;
import qnopy.com.qnopyandroid.util.Util;
import qnopy.com.qnopyandroid.util.VectorDrawableUtils;

/**
 * Created by myog3 on 01-02-2018 and maintained & optimised by PatelSanket since Oct 2019
 */
public class FormMaster {

    private static final String TAG = "FormMaster";
    static Context context;
    //  LocationdetailAdapter mAdapter;
    public List<MetaData> form_meta_list = new ArrayList<>();
    public List<MetaData> form_meta_list_SelectedSpecies = new ArrayList<>();
    private String siteID, userID, username, deviceID, parentFormID, sitename;
    public int dpTextHeight, dpTextWidth, dpLongTextWidth;
    AlertDialog.Builder builder;
    AlertDialog alertdialog;
    public String locationID, eventID;
    int decimalPlaces = 0;

    private int formColor = Color.parseColor("#196b76");
    int compnyID = 0;
    private final LinkedHashMap<String, SCocMaster> cocMasterValueMap = new LinkedHashMap<>();

    int textHeight = 50;
    int textWidth = 160;
    int longTextWidth = 420;

    FormActivity formActivity;
    public boolean blankDateValue = false;
    public boolean blankTimeValue = false;
    private MetaDataAttributes attributes; //used for each metadata instance from arraylist

    private int CurrentSetID = 1;
    private int CurrentFormNumber = 0;
    List<FieldData> previousReading1 = null, previousReading2 = null, currentReading = null;

    LocationDetailActivity parentContext;

    public List<MetaData> metaObjects;
    public List<MetaData> filteredMetaObjects;

    public HashMap<String, DataHolder> mapObject;
    public HashMap<String, DataHolder> fmapObject;
    public HashMap<String, DataHolder> mapObjectSpecies;
    public HashMap<String, DataHolder> fmapObjectSpecies;

    private Double highlimitdefault, lowlimitdefault;
    private Boolean STORE_VALUE = false;
    public boolean AUTO_GENERATE = false;
    public String AUTO_SET_LAST_SELECTED_VALUES = null;
    public String AUTO_METHODS_LAST_SELECTED_VALUES = null;

    ArrayList<String> mMultipleSpeciesSelected = new ArrayList<>();
    String mDisplayValueForSpecies, mInputTypeSpecies;
    List<Integer> childparamList = new ArrayList<>();
    private int flowRateParamId = -1;
    private CustomButton btnClose, btnPrint;
    public boolean isSampleDateOrTimeSet = false;
    private boolean hasSample4Operand;
    private Map<String, MetaData> mapHiddenMetaObjects;
    private Map<String, MetaData> mapSetExprMetaObjects;
    public Map<Integer, MetaData> mapMetaObjects;//we use this to avoid for loops to find same metaobject to work on

    public HashMap<String, DataHolder> getFmapObject() {
        return fmapObject;
    }

    public Map<Integer, MetaData> getMapMetaObjects() {
        return mapMetaObjects;
    }

    public int getCurrentFormNumber() {
        return CurrentFormNumber;
    }

    public void setCurrentFormNumber(int currentFormNumber) {
        CurrentFormNumber = currentFormNumber;
        Log.i(TAG, "Current Form:" + currentFormNumber);
    }

    public int getCurrentSetID() {
        return CurrentSetID;
    }

    public void setCurrentSetID(int currentSetID) {
        CurrentSetID = currentSetID;
        Log.i(TAG, "Current Set:" + currentSetID);
    }

    public FormMaster(Context parent, FormActivity context, List<MetaData> form_meta_list, String siteID,
                      String locationID, String eventID, String parentFormID) {
        FormMaster.context = context;
        this.formActivity = context;
        this.siteID = siteID;
        this.locationID = locationID;
        this.eventID = eventID;
        this.parentFormID = parentFormID;
        this.userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        this.compnyID = Integer.parseInt(Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID));
        this.deviceID = Util.getSharedPreferencesProperty(context, GlobalStrings.SESSION_DEVICEID);
        this.sitename = Util.getSharedPreferencesProperty(context, GlobalStrings.CURRENT_SITENAME);

        dpTextHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                textHeight, formActivity.getResources().getDisplayMetrics());
        dpTextWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                textWidth, formActivity.getResources().getDisplayMetrics());
        dpLongTextWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                longTextWidth, formActivity.getResources().getDisplayMetrics());

        parentContext = (LocationDetailActivity) parent;

        filteredMetaObjects = new ArrayList<>();
        fmapObject = new HashMap<>();
        mapHiddenMetaObjects = new HashMap<>();
        this.metaObjects = form_meta_list;

        currentReading = formActivity.getCurrentReading1List();
        previousReading1 = formActivity.getPreviousReading1List();
        previousReading2 = formActivity.getPreviousReading2List();

        this.mapObject = formActivity.mapObject;
        mapSetExprMetaObjects = new HashMap<>();
        mapMetaObjects = new HashMap<>();

        //20-Jul-17 ADD ALL ITEMS IN FILTERED WHICH ARE NOT CHILD AND PARENT,IS PARENT,IS CHILD BUT VISIBLE
        for (int pos = 0; pos < metaObjects.size(); pos++) {
            MetaData metaData = metaObjects.get(pos);
            context.setMapObject(metaData, pos);

            String key = String.valueOf(metaData.getMetaParamID());
            DataHolder dh = mapObject.get(key);
            fmapObject.put(key, dh);

            if (!metaData.isVisible)
                mapHiddenMetaObjects.put(metaData.getMetaParamID() + "", metaData);
            if (metaData.getFieldParameterOperands() != null
                    && metaData.getFieldParameterOperands().toLowerCase().contains("!!set!!"))
                mapSetExprMetaObjects.put(metaData.getMetaParamID() + "", metaData);

            mapMetaObjects.put(metaData.getMetaParamID(), metaData);
        }

        String auto = Util.getSharedPreferencesProperty(context, GlobalStrings.AUTO_GENERATE);
        if (auto != null && !auto.isEmpty()) {
            AUTO_GENERATE = Boolean.valueOf(auto);
        } else {
            AUTO_GENERATE = false;
        }

//        showUnSyncedFieldsCount();
    }

    public void setDataOnChanged(int fpID) {
        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(formActivity);

        for (MetaData metaData : metaObjects) {

            boolean isShowLast3 = isShowLast3(metaData);

            //for volume purge and purge calc check
            if (metaData.getMetaParamLabel().equalsIgnoreCase("Flow Rate"))
                flowRateParamId = metaData.getMetaParamID();

            DefaultValueDataSource dv = new DefaultValueDataSource(context);

            DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID,
                    metaData.getCurrentFormID() + "",
                    metaData.getMetaParamID() + "", formActivity.getCurSetID() + "");

            MetaDataAttributesDataSource source = new MetaDataAttributesDataSource(context);
            attributes =
                    source.getMetaDataAttributes(Integer.parseInt(siteID),
                            formActivity.getCurrentAppID(), metaData.getMetaParamID());

            DataHolder dHolder = fmapObject.get(metaData.getMetaParamID() + "");
            String valueToSet = getDefaultValueToSet(dHolder, metaData, d_model.getDefaultValue());

            if (metaData.getForm_field_row() != null) {
                ViewHolder viewHolder = (ViewHolder) metaData.getForm_field_row().getTag();
                String expression = getExpressionFromMetaOrAttribute(metaData);

                boolean sample = expression != null && (expression.contains("SAMPLE")
                        || expression.toLowerCase().contains("!!coc!!")
                        || expression.toLowerCase().contains("!!set!!"));

                if (fpID > 0) {
                    if (expression != null
                            && expression.contains(fpID + "")) {

                        if (viewHolder != null) {
                            String inputtype = metaData.getMetaInputType();
                            switch (inputtype.toUpperCase()) {

                                case "TEXT":
/*                                    if (dHolder.value == null) {
                                        CocDetailDataSource coc_dv = new CocDetailDataSource(context);
                                        dHolder.value
                                                = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID,
                                                locationID, metaData.getMetaParamID() + "");

                                        if (dHolder.value != null && !dHolder.value.isEmpty()) {
                                            viewholder.alphaTextView.setText(dHolder.value);
                                            handleTextData(metaData, dHolder.value, formActivity.getCurSetID());
                                        } else
                                            viewholder.alphaTextView.setText(valueToSet);
                                    } else*/
//                                    viewholder.alphaTextView.setText(valueToSet);

                                    if (sample
                                            && !expression.contains("SAMPLE4")) {
                                        if (valueToSet == null || valueToSet.isEmpty())
                                            setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                                        else {
                                            if (isSampleDateOrTimeSet) {
                                                setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                                            } else {
                                                viewHolder.alphaTextView.setText(valueToSet);
                                            }
                                        }
                                    } else
                                        setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);

                                    setMandatoryFieldAlert(metaData, viewHolder);
                                    if (sample) {
                                        DataHolder tempData
                                                = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                                        if (formActivity.currCocID != null
                                                && tempData.value != null
                                                && !tempData.value.isEmpty()
                                                && !metaData.getMetaParamLabel().toLowerCase()
                                                .contains("duplicate sample id")) {
                                            viewHolder.printCOC.setVisibility(View.VISIBLE);
                                        } else {
                                            viewHolder.printCOC.setVisibility(View.GONE);
                                        }
                                    }
                                    break;
                                case "TEXTCONTAINER":
/*                                    if (dHolder.value == null) {
                                        CocDetailDataSource coc_dv = new CocDetailDataSource(context);
                                        dHolder.value
                                                = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID,
                                                locationID, metaData.getMetaParamID() + "");

                                        if (dHolder.value != null && !dHolder.value.isEmpty()) {
                                            viewholder.alphaTextView.setText(dHolder.value);
                                            handleTextData(metaData, dHolder.value, formActivity.getCurSetID());
                                        } else
                                            viewholder.alphaTextView.setText(valueToSet);
                                    } else*/
//                                    viewholder.alphaTextView.setText(valueToSet);

                                    setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                                    setMandatoryFieldAlert(metaData, viewHolder);
                                    break;

                                case "NUMERIC":
//                                    viewholder.numericView.setText(valueToSet);

                                    if (isShowLast3)
                                        setWeightForShowLast3(viewHolder.llHorizontalField, metaData, viewHolder);
                                    else if (metaData.isIsShowLast2()) {
                                        setWeightForShowLast2(metaData, viewHolder);
                                        if (getCurrentSetID() > 0)
                                            handleLast2Reading_And_Percentage(metaData, viewHolder,
                                                    viewHolder.numericView);
                                    } else
                                        setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);

                                    break;

                                case "LABEL":
                                case "CHECKBOX":
                                    updateBottleData(metaData);
                                    setCalculatedFieldParams(metaData, null, viewHolder);
                                    break;

                                case "TOTALIZER":
//                                    viewholder.numericView.setText(valueToSet);
                                    if (metaData.isIsShowLast2() && getCurrentSetID() > 0)
                                        handleLast2Reading_And_Percentage(metaData, viewHolder,
                                                viewHolder.numericView);
                                    else
                                        setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);
                                    break;

                                case "MULTIMETHODS":
                                    updateMultiMethodAutoCompleteView(metaData, viewHolder.new_actv, dHolder.value, inputtype);
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    }
                } else {
                    if (viewHolder != null) {
                        String inputType = metaData.getMetaInputType();

                        String warnhighLimit = d_model.getWarningHighDefaultValue();
                        String warnlowLimit = d_model.getWarningLowDefaultValue();

                        if ((d_model.getWarningHighDefaultValue() == null || d_model.getWarningHighDefaultValue().equals(""))) {
                            if (metaData.getRoutineId() == 111) {
                                warnhighLimit = String.valueOf(metaData.getMetaWarningHigh());
                            }
                        }
                        if ((d_model.getWarningLowDefaultValue() == null || d_model.getWarningLowDefaultValue().equals(""))) {
                            if (metaData.getRoutineId() == 111) {
                                warnlowLimit = String.valueOf(metaData.getMetaWarningLow());
                            }
                        }

                        String gText = metaData.ParamLabel;

                        if (inputType.equalsIgnoreCase("TOTALIZER")) {
                            if ((warnlowLimit != null && warnlowLimit.trim().length() > 0)) {
                                String styledText = gText + "\n\n"
                                        + "<font color='red'>Value should be greater than  " +
                                        warnlowLimit + "</font>";
                                Spanned result;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                                } else {
                                    result = Html.fromHtml(styledText);
                                }
                                viewHolder.lable.setText(result);
                            } else {
                                viewHolder.lable.setText(gText);
                                //adding this as label may have operand which may change label text
                                setCalculatedFieldParams(metaData, null, viewHolder);
                            }
                        } else {
                            if ((warnhighLimit != null && warnhighLimit.trim().length() > 0 &&
                                    !warnhighLimit.trim().contains("0.0"))
                                    || (warnlowLimit != null && warnlowLimit.trim().length() > 0 &&
                                    !warnlowLimit.trim().contains("0.0"))) {

                                String styledText = gText + "\n" + "<font color='blue'><small>Range: "
                                        + warnlowLimit + " to " + warnhighLimit + "</small></font>";
                                Spanned result;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                                } else {
                                    result = Html.fromHtml(styledText);
                                }
                                viewHolder.lable.setText(result);
                            } else {
                                viewHolder.lable.setText(gText);
                                //adding this as label may have operand which may change label text
                                setCalculatedFieldParams(metaData, null, viewHolder);
                            }
                        }

                        if (metaData.getMetaRequired_Y_N().equals("1")) {
                            String label = viewHolder.lable.getText().toString() + "*";
                            Spannable wordToSpan = new SpannableString(label);
                            wordToSpan.setSpan(new ForegroundColorSpan(Color.RED), wordToSpan.length() - 1,
                                    wordToSpan.length(), 0);
                            viewHolder.lable.setText(wordToSpan);
                        }

                        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                        String value = tempData.value;

                        switch (inputType.toUpperCase()) {

                            case "TIME":
                                setTimeViewData(metaData, (FBTimeView) viewHolder.timeView);
                                if (dHolder != null
                                        && (dHolder.value == null || dHolder.value.isEmpty())) {
                                    setCalculatedFieldParams(metaData, null, viewHolder);
                                }
                                break;
                            case "DATE":
                                setDateViewData(metaData, (FBDateView) viewHolder.dateView);
                                if (dHolder != null
                                        && (dHolder.value == null || dHolder.value.isEmpty())) {
                                    setCalculatedFieldParams(metaData, null, viewHolder);
                                }
                                break;
                            case "TEXT":
/*                                if (dHolder.value == null) {
                                    CocDetailDataSource coc_dv = new CocDetailDataSource(context);
                                    dHolder.value
                                            = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID,
                                            locationID, metaData.getMetaParamID() + "");

                                    if (dHolder.value != null && !dHolder.value.isEmpty()) {
                                        viewholder.alphaTextView.setText(dHolder.value);
                                        handleTextData(metaData, dHolder.value, formActivity.getCurSetID());
                                    } else
                                        viewholder.alphaTextView.setText(valueToSet);
                                } else*/

                                if (sample && !expression.contains("SAMPLE4")) {
                                    if (valueToSet == null || valueToSet.isEmpty())
                                        setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                                    else {
                                        if (isSampleDateOrTimeSet) {
                                            setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                                        } else {
                                            viewHolder.alphaTextView.setText(valueToSet);
                                        }
                                    }
                                } else
                                    viewHolder.alphaTextView.setText(valueToSet);

                                setMandatoryFieldAlert(metaData, viewHolder);

                                if (sample) {
                                    if (formActivity.currCocID != null && valueToSet != null
                                            && !valueToSet.isEmpty()
                                            && !metaData.getMetaParamLabel().toLowerCase()
                                            .contains("duplicate sample id")) {
                                        viewHolder.printCOC.setVisibility(View.VISIBLE);
                                    } else {
                                        viewHolder.printCOC.setVisibility(View.GONE);
                                    }
                                }

                                boolean hasVisibleOperand = expression != null && !expression.isEmpty()
                                        && expression.contains("!!visible!!");
                                if (viewHolder.alphaTextView.getText().toString().isEmpty() || hasVisibleOperand)
                                    setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                                break;
                            case "CHECKBOX":
                                if (expression != null
                                        && expression.toLowerCase().contains("!!set!!"))
                                    updateBottleData(metaData);
                                else
                                    updateCheckboxValues(viewHolder.llCocBottlesCheckOptions, metaData);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "LABEL":
                                //Note: have matched static string bottles in case you find something
                                //dynamic to match then its good
                                if (metaData.getMetaParamLabel().equalsIgnoreCase("Bottles"))
                                    updateLabelViewControl(metaData, dHolder.value, viewHolder.lableview);
                                else
                                    viewHolder.lableview.setText(dHolder.value);

                                setMandatoryFieldAlert(metaData, viewHolder);

                                if (expression != null
                                        && expression.toLowerCase().contains("!!set!!"))
                                    updateBottleData(metaData);

                                //adding this here LABEL may have operand that may change the label text
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "TEXTCONTAINER":

/*                                if (dHolder.value == null) {
                                    CocDetailDataSource coc_dv = new CocDetailDataSource(context);
                                    dHolder.value
                                            = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID,
                                            locationID, metaData.getMetaParamID() + "");

                                    if (dHolder.value != null && !dHolder.value.isEmpty()) {
                                        viewholder.alphaTextView.setText(dHolder.value);
                                        handleTextData(metaData, dHolder.value, formActivity.getCurSetID());
                                    } else
                                        viewholder.alphaTextView.setText(valueToSet);
                                } else*/
                                viewHolder.alphaTextView.setText(valueToSet);

                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "PHOTOS":
                                ArrayList<GalleryItem> listPhotos = attachmentDataSource.getAttachmentForFieldParam(siteID,
                                        eventID, locationID, metaData.getCurrentFormID() + "",
                                        metaData.getMetaParamID() + "",
                                        formActivity.getCurSetID(), formActivity.getSiteName(),
                                        formActivity.currentLocationName);

                                if (viewHolder.observedPhotosAdapter != null && viewHolder.rvObservedPhotos != null) {
                                    if (listPhotos.size() > 0) {
                                        viewHolder.observedPhotosAdapter
                                                = new ObservedPhotosAdapter(listPhotos, formActivity,
                                                metaData.getMetaParamID());
                                    } else {
                                        viewHolder.observedPhotosAdapter
                                                = new ObservedPhotosAdapter(new ArrayList<>(),
                                                formActivity, metaData.getMetaParamID());
                                    }

                                    viewHolder.rvObservedPhotos.setAdapter(viewHolder.observedPhotosAdapter);
                                } else {
                                    if (viewHolder.llLayout.getChildCount() > 2)
                                        viewHolder.llLayout.removeViewAt(viewHolder.llLayout.getChildCount() - 1);
                                    RecyclerView rvPhotos = new RecyclerView(formActivity);
                                    rvPhotos.setHasFixedSize(true);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(formActivity,
                                            LinearLayoutManager.HORIZONTAL, false);
                                    rvPhotos.setLayoutManager(linearLayoutManager);

                                    if (listPhotos.size() > 0) {
                                        viewHolder.observedPhotosAdapter
                                                = new ObservedPhotosAdapter(listPhotos,
                                                formActivity, metaData.getMetaParamID());
                                    } else {
                                        viewHolder.observedPhotosAdapter
                                                = new ObservedPhotosAdapter(new ArrayList<>(),
                                                formActivity, metaData.getMetaParamID());
                                    }

                                    rvPhotos.setAdapter(viewHolder.observedPhotosAdapter);
                                    rvPhotos.setTag(Integer.toString(metaData.getMetaParamID()));
                                    viewHolder.llLayout.addView(rvPhotos);
                                }
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "COUNTER":
                                LinearLayout holderview = (LinearLayout) viewHolder
                                        .numberPicker.getChildAt(1);
                                EditText counter = (EditText) (holderview.getChildAt(0));

                                if (valueToSet != null && !valueToSet.isEmpty()) {
                                    counter.setText(valueToSet);
                                } else {
                                    counter.setText("0");
                                }

                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "SIGNATURE":
                                /*if (dHolder.value != null && !dHolder.value.isEmpty()) {
                                    viewHolder.tvSignatureNames.removeAllViews();
                                    addChipsToView(viewHolder.tvSignatureNames, dHolder.value);
                                } else {
                                    viewHolder.tvSignatureNames.removeAllViews();
                                }*/
                                ArrayList<CustomerSign> customerSigns
                                        = attachmentDataSource.getAttachmentListForSignature(Integer.parseInt(eventID),
                                        Integer.parseInt(siteID), metaData.getMetaParamID(), locationID, Integer.parseInt(userID),
                                        formActivity.getCurrentAppID(), formActivity.getCurSetID());

                                viewHolder.signatureAdapter.addSignatures(customerSigns);
                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "RATING":

                                if (dHolder != null && dHolder.value == null || dHolder.value.isEmpty()) {
                                    Drawable progress = viewHolder.ratingView.getProgressDrawable();
                                    DrawableCompat.setTint(progress, Color.GRAY);
                                    viewHolder.ratingView.setRating(0);

                                } else {
                                    // viewHolder.ratingView.setIsIndicator(true);
                                    viewHolder.ratingView.setRating(Float.
                                            parseFloat(dHolder.value));
                                    LayerDrawable stars = (LayerDrawable) viewHolder.ratingView
                                            .getProgressDrawable();
                                    stars.getDrawable(2).setColorFilter(context.getResources()
                                                    .getColor(R.color.qnopy_teal),
                                            PorterDuff.Mode.SRC_ATOP);
                                }
                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "GPS":
                                if (valueToSet != null && !valueToSet.isEmpty()) {
                                    viewHolder.tvGpsCoordinates.setText(valueToSet);
                                } else {
                                    viewHolder.tvGpsCoordinates.setText("0.0000,0.0000");
                                }

                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "PICKER":

                                if (metaData.getNameValueMap().containsKey("Coc")
                                        || metaData.getNameValueMap().containsKey("COC")) {

                                    LocationDataSource ld = new LocationDataSource(formActivity);
                                    ArrayList<SCocMaster> cocMasterArrayList
                                            = ld.getAllCoCIDs(formActivity.getEventID() + "",
                                            formActivity.getSiteID() + "");
                                    LinkedHashMap<String, String> pickerCocNameValueMap = new LinkedHashMap<>();

                                    for (SCocMaster master : cocMasterArrayList) {
                                        pickerCocNameValueMap.put(master.getCocDisplayId(), master.getCocDisplayId());
                                        cocMasterValueMap.put(master.getCocDisplayId(), master);
                                    }

                                    dHolder.setNameValuePair(pickerCocNameValueMap);
                                } else {
                                    dHolder.setNameValuePair(metaData.getNameValueMap());
                                }

                                int sel_position = dHolder.getPositionForValue(dHolder.value);
                                if (sel_position == -1) {
                                    sel_position = 0;
                                } else {
                                    sel_position += 1;
                                }

                                if (metaData.getNameValueMap().containsKey("Coc")
                                        || metaData.getNameValueMap().containsKey("COC")) {
                                    if (cocMasterValueMap.containsKey(dHolder.value))
                                        formActivity.currCocID = cocMasterValueMap.get(dHolder.value).getCocId() + "";
                                }

                                viewHolder.dataSpinner.setSelection(sel_position);
                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);

                                break;
                            case "NUMERIC":

                                if (valueToSet != null && !valueToSet.isEmpty()) {
                                    String newValue = getLocaleFormattedString(valueToSet, Locale.getDefault());
                                    viewHolder.numericView.setText(newValue);
                                } else {
                                    viewHolder.numericView.setText(valueToSet);
                                }

                                if (isShowLast3)
                                    setWeightForShowLast3(viewHolder.llHorizontalField, metaData, viewHolder);
                                else if (metaData.isIsShowLast2()) {
                                    setWeightForShowLast2(metaData, viewHolder);
                                    if (getCurrentSetID() > 0)
                                        handleLast2Reading_And_Percentage(metaData, viewHolder,
                                                viewHolder.numericView);
                                } else
                                    setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);
                                break;
                            case "TOTALIZER":
                                viewHolder.numericView.setText(valueToSet);
                                if (metaData.isIsShowLast2() && getCurrentSetID() > 0)
                                    handleLast2Reading_And_Percentage(metaData, viewHolder,
                                            viewHolder.numericView);
                                else
                                    setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);
                                break;

                            case "RADIO":
                                dHolder.setNameValuePair(metaData.getNameValueMap());
                                int val = dHolder.getPositionForValue(dHolder.value);
                                //  RadioButton selectedradio= (RadioButton) viewholder.radioGroup.getChildAt(val);

                                if (val > -1) {
                                    viewHolder.radioGroup.check(viewHolder.radioGroup.
                                            getChildAt(val).getId());
                                } else {
                                    viewHolder.radioGroup.check(-1);
                                }
                                setMandatoryFieldAlert(metaData, viewHolder);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "AUTOCOMPLETE":
                                updateAutoCompleteView(metaData, viewHolder.new_actv, valueToSet,
                                        inputType, metaData.getMetaLovId(),
                                        dHolder.getParentlovItemID(), null);

                                if (dHolder.getGoto_formID() > 0) {
                                    viewHolder.enableForms.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.enableForms.setVisibility(View.GONE);
                                }
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "MULTIAUTOCOMPLETE":
                                updateAutoCompleteView(metaData, viewHolder.new_actv, valueToSet,
                                        inputType, metaData.getMetaLovId(),
                                        dHolder.getParentlovItemID(), metaData.getNameValueMap());
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                           /* case "MULTISPECIES":
                                updateAutoCompleteView(metaData, viewholder.new_actv, dholder.value, inputtype, metaData.getMetaLovId(),
                                        dholder.getParentlovItemID(), metaData.getNameValueMap());
                                break;*/

                            case "AUTOSETGENERATOR":
                                updateAutoCompleteView(metaData, viewHolder.new_actv, dHolder.value, inputType, metaData.getMetaLovId(),
                                        dHolder.getParentlovItemID(), metaData.getNameValueMap());
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "MULTIMETHODS":
                                updateMultiMethodAutoCompleteView(metaData, viewHolder.new_actv, dHolder.value, inputType);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;
                            case "WEATHER":
                                DataHolder dh = mapObject.get(metaData.getMetaParamID() + "");
                                if (dh != null && viewHolder.tvZipCode != null) {
                                    viewHolder.tvZipCode.setText(dh.value);
                                }
                                enableChildFieldsForWeatherData(metaData);
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "TASK":
                                int taskCount = 0;
                                TaskDetailsDataSource taskDetailsDataSource
                                        = new TaskDetailsDataSource(context);
                                taskCount = taskDetailsDataSource
                                        .getTasksCount(metaData.getMetaParamID(), siteID,
                                                getCurrentSetID(), formActivity.getCurrentAppID(), locationID);
                                if (taskCount > 0) {
                                    String count = String.valueOf(taskCount);
                                    viewHolder.tvAddTasks.setText(count);
                                    handleTextData(metaData, count, getCurrentSetID());
                                } else {
                                    viewHolder.tvAddTasks.setText(valueToSet);
                                }
                                setCalculatedFieldParams(metaData, null, viewHolder);
                                break;

                            case "QRCODE":
                            case "BARCODE": {
                                viewHolder.tvBarCode.setText(valueToSet);
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            }
        }
        //  formActivity.refreshMasterForm();
    }

    private String getReplacedNewLineString(String valueToSet) {
        try {
            //replacing with "\n" as database saves the "\n" text with preceding "\"
            if (valueToSet != null && valueToSet.contains("\\n"))
                return valueToSet.replace("\\n", "\n");
            else
                return valueToSet;
        } catch (Exception e) {
            e.printStackTrace();
            return valueToSet;
        }
    }

    private String getDefaultValueToSet(DataHolder dHolder, MetaData metaData, String defaultValue) {
        String value;

        if (metaData.getMetaInputType().equalsIgnoreCase("TEXT")
                || metaData.getMetaInputType().equalsIgnoreCase("TEXTCONTAINER")
                || metaData.getMetaInputType().equalsIgnoreCase("AUTOCOMPLETE")
                || metaData.getMetaInputType().equalsIgnoreCase("COUNTER")
                || metaData.getMetaInputType().equalsIgnoreCase("MULTIAUTOCOMPLETE")
                || metaData.getMetaInputType().equalsIgnoreCase("TASK")
                || metaData.getMetaInputType().equalsIgnoreCase("QRCODE")
                || metaData.getMetaInputType().equalsIgnoreCase("BARCODE")
                || metaData.getMetaInputType().equalsIgnoreCase("NUMERIC")
                || metaData.getMetaInputType().equalsIgnoreCase("RADIO")
                || metaData.getMetaInputType().equalsIgnoreCase("TOTALIZER")
                || metaData.getMetaInputType().equalsIgnoreCase("GPS")) {

            if (dHolder.value != null && !dHolder.value.isEmpty()) {
                value = dHolder.value; //not saving this value as it is already stored in db
            } else if (metaData.getDefaultValue() != null && !metaData.getDefaultValue().isEmpty()) {
                value = metaData.getDefaultValue();
                dHolder.value = getReplacedNewLineString(value);
                handleTextData(metaData, dHolder.value, formActivity.getCurSetID());
            } else if (defaultValue != null && !defaultValue.isEmpty()) {
                value = defaultValue;
                dHolder.value = getReplacedNewLineString(value);
                handleTextData(metaData, dHolder.value, formActivity.getCurSetID());
            } else {
                value = null;
            }
        } else {
            value = null;
        }

        return value;
    }

    private void setWeightForShowLast2(MetaData metaData, ViewHolder viewholder) {
        boolean isShowLast2 = metaData.isIsShowLast2();
        if (formActivity.getCurSetID() > 1 && isShowLast2) {
            setWeight(viewholder.llHorizontalField, metaData);
        } else {
            try {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                int margin = Util.dpToPx(5);
                params.setMargins(margin, margin, margin, 0);
                viewholder.llHorizontalField.getChildAt(0).setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCheckboxValues(LinearLayout llCocBottlesCheckOptions, MetaData metaData) {

        FieldDataSource fieldDataSource = new FieldDataSource(formActivity);
        ArrayList<String> checkOptions = fieldDataSource.getBottleCheckOptions(eventID,
                formActivity.getCurSetID(), locationID, siteID,
                formActivity.getCurrentAppID(), metaData.getMetaParamID());

        if (metaData.getNameValueMap().size() == 0 && checkOptions.size() > 0) {
            if (llCocBottlesCheckOptions != null) {
                llCocBottlesCheckOptions.removeAllViews();
                for (String item : checkOptions) {
                    llCocBottlesCheckOptions.addView(getCheckBox(metaData, item));
                }
            }
        } else if (metaData.getNameValueMap().size() > 0 && checkOptions.size() == 0) {
            llCocBottlesCheckOptions.removeAllViews();
            for (String item : metaData.getNameValueMap().keySet()) {
                llCocBottlesCheckOptions.addView(getCheckBox(metaData, item));
            }
        }
    }

    boolean hasAtLeastOneOperand = false;

    public List<MetaData> getFormMasterData() {

//        showUnSyncedFieldsBadge();

        form_meta_list = new ArrayList<>();
        filteredMetaObjects = new ArrayList<>();

        View rowView = new View(context);

        for (int ab = 0; ab < metaObjects.size(); ab++) {
//        for (MetaData metaData : metaObjects) {
            MetaData metaData = metaObjects.get(ab);
            String gText;

            //for volume purge and purge calc check
            if (metaData.getMetaParamLabel().equalsIgnoreCase("Flow Rate"))
                flowRateParamId = metaData.getMetaParamID();

            String expression = getExpressionFromMetaOrAttribute(metaData);

            if (expression != null) {

                if (expression.contains("SAMPLE4") || expression.contains("DUPSAMPLE4"))
                    hasSample4Operand = true;

                if (!hasAtLeastOneOperand) {
                    if (!expression.isEmpty()) {
                        hasAtLeastOneOperand = true;
                        formActivity.setRefreshButtonVisibility(View.VISIBLE);
                    } else
                        formActivity.setRefreshButtonVisibility(View.GONE);
                }
            } else if (!hasAtLeastOneOperand) {
                formActivity.setRefreshButtonVisibility(View.GONE);
            }

            MetaDataAttributesDataSource source = new MetaDataAttributesDataSource(context);
            attributes =
                    source.getMetaDataAttributes(Integer.parseInt(siteID),
                            formActivity.getCurrentAppID(), metaData.getMetaParamID());

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout detailMainLayout = null;
            LinearLayout itemlayout = null;
            LinearLayout llHorizontalField = null;
            LinearLayout llImageIcons = null;
            ViewHolder viewHolder = null;// new ViewHolder();

            rowView = inflater.inflate(R.layout.adapter_locdetail, null);

            detailMainLayout = (LinearLayout) rowView.findViewById(R.id.DetailLayout1);
            itemlayout = (LinearLayout) rowView.findViewById(R.id.radioContainer);
            llHorizontalField = rowView.findViewById(R.id.llHorizontalField);
            llImageIcons = rowView.findViewById(R.id.llImageIcons);

            rowView.setBackgroundColor(Color.WHITE);
            viewHolder = new ViewHolder();

            itemlayout.setTag(Integer.toString(metaData.getMetaParamID()));
            viewHolder.llLayout = itemlayout;

            llHorizontalField.setTag(Integer.toString(metaData.getMetaParamID()));
            viewHolder.llHorizontalField = llHorizontalField;

            viewHolder.lable = (TextView) rowView.findViewById(R.id.Field1);
            viewHolder.lable.setTextSize(16f);

            gText = metaData.ParamLabel;
            String inputType = metaData.InputType;

            DefaultValueDataSource dv = new DefaultValueDataSource(context);
            DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID, metaData.getCurrentFormID() + "",
                    metaData.getMetaParamID() + "", formActivity.getCurSetID() + "");

            DataHolder dHolder = fmapObject.get(metaData.getMetaParamID() + "");
            String valueToSet = getDefaultValueToSet(dHolder, metaData, d_model.getDefaultValue());

            String highLimit = d_model.getHighLimit();
            String warnhighLimit = d_model.getWarningHighDefaultValue();
            String warnlowLimit = d_model.getWarningLowDefaultValue();
            String lowLimit = d_model.getLowLimit();

            if ((d_model.getHighLimit() == null || d_model.getHighLimit().equals(""))) {
                if (metaData.getRoutineId() == 111) {
                    highLimit = String.valueOf(metaData.getMetaHighLimit());
                }
            }
            if ((d_model.getLowLimit() == null || d_model.getLowLimit().equals(""))) {
                if (metaData.getRoutineId() == 111) {
                    lowLimit = String.valueOf(metaData.getMetaLowLimit());
                }
            }
            if ((d_model.getWarningHighDefaultValue() == null || d_model.getWarningHighDefaultValue().equals(""))) {
                if (metaData.getRoutineId() == 111) {
                    warnhighLimit = String.valueOf(metaData.getMetaWarningHigh());
                }
            }
            if ((d_model.getWarningLowDefaultValue() == null || d_model.getWarningLowDefaultValue().equals(""))) {
                if (metaData.getRoutineId() == 111) {
                    warnlowLimit = String.valueOf(metaData.getMetaWarningLow());
                }
            }

            if (inputType != null && inputType.equalsIgnoreCase("TOTALIZER")) {
                if ((warnlowLimit != null && warnlowLimit.trim().length() > 0)) {
                    String styledText = gText + "\n\n"
                            + "<font color='red'>Value should be greater than  " +
                            warnlowLimit + "</font>";
                    Spanned result;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        result = Html.fromHtml(styledText);
                    }
                    viewHolder.lable.setText(result);
                } else {
                    viewHolder.lable.setText(gText);
                    //adding this as label may have operand which may change label text
                    setCalculatedFieldParams(metaData, null, viewHolder);
                }
            } else {
                if ((warnhighLimit != null && warnhighLimit.trim().length() > 0 &&
                        !warnhighLimit.trim().contains("0.0"))
                        || (warnlowLimit != null && warnlowLimit.trim().length() > 0 &&
                        !warnlowLimit.trim().contains("0.0"))) {
                    //commented on 27 Oct, 21 to manage it. show normal
                    /*String styledText = gText + "\n" + "<font color='red'><small>Range: "
                            + warnlowLimit + " to " + warnhighLimit + "</small></font>";*/

                    String styledText = gText + "\n" + "<font color='blue'><small>Range: "
                            + warnlowLimit + " to " + warnhighLimit + "</small></font>";
                    Spanned result;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        result = Html.fromHtml(styledText);
                    }
                    viewHolder.lable.setText(result);
                } else {
                    viewHolder.lable.setText(gText);
                    //adding this as label may have operand which may change label text
                    setCalculatedFieldParams(metaData, null, viewHolder);
                }
            }

            if (metaData.getMetaRequired_Y_N().equals("1")) {
                String label = viewHolder.lable.getText().toString() + "*";
                Spannable wordToSpan = new SpannableString(label);
                wordToSpan.setSpan(new ForegroundColorSpan(Color.RED), wordToSpan.length() - 1,
                        wordToSpan.length(), 0);
                viewHolder.lable.setText(wordToSpan);
            }

            Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            viewHolder.lable.setTypeface(type);

            String fontStyle = metaData.getExtField7();

            if (metaData.getFontStyle() != null
                    && !metaData.getFontStyle().isEmpty())
                fontStyle = metaData.getFontStyle();

            if (attributes != null)
                if (attributes.getFontStyle() != null
                        && !attributes.getFontStyle().isEmpty())
                    fontStyle = attributes.getFontStyle();

            if (inputType == null || inputType.equals("")) {
                if (fontStyle != null) {
                    if (fontStyle.equalsIgnoreCase("fontNormal"))
                        type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                    else
                        type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
                } else
                    type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
                viewHolder.lable.setTypeface(type);
            }

            switch (inputType.toUpperCase()) {
                case "TIME": {
                    viewHolder.timeView = getTimeView(metaData); //Time picker
                    detailMainLayout.addView(viewHolder.timeView);

                    if (dHolder != null
                            && (dHolder.value == null || dHolder.value.isEmpty())) {
                        setCalculatedFieldParams(metaData, null, viewHolder);
                    }
                    break;
                }

                case "DATE": {
                    viewHolder.dateView = getDateView(metaData); //date picker
                    detailMainLayout.addView(viewHolder.dateView);

                    if (dHolder != null
                            && (dHolder.value == null || dHolder.value.isEmpty())) {
                        setCalculatedFieldParams(metaData, null, viewHolder);
                    }
                    break;
                }

                case "COUNTER": {
                    viewHolder.numberPicker = getNumberPickerView(metaData);
                    itemlayout.addView(viewHolder.numberPicker);
                    break;
                }

                case "LABEL": {
                    viewHolder.lableview = getLabelViewControl(metaData);
                    itemlayout.addView(viewHolder.lableview);
                    setCalculatedFieldParams(metaData, null, viewHolder);
                    break;
                }

                case "CHECKBOX": {
                    viewHolder.llCocBottlesCheckOptions = getCheckboxView(metaData);
                    itemlayout.addView(viewHolder.llCocBottlesCheckOptions);
                    break;
                }

                case "TEXT": {
                    viewHolder.alphaTextView = getAlphaView(metaData, getLongTextLayoutParams());
                    itemlayout.addView(viewHolder.alphaTextView);

                    if (metaData.getRoutineId() == 999) {

                        ImageButton formHolder = new ImageButton(context);
                        formHolder.setBackgroundResource(R.drawable.sampletag_vector);

                        formHolder.setTag(Integer.toString(metaData.getMetaParamID()));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        formHolder.setLayoutParams(layoutParams);
                        viewHolder.enableSampleTag = formHolder;

                        detailMainLayout.addView(viewHolder.enableSampleTag);

                        final String sampleFieldParamID = metaData.getMetaParamID() + "";

                        final ViewHolder finalViewHolder = viewHolder;

                        viewHolder.enableSampleTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String sampleValue = finalViewHolder.alphaTextView.getText().toString();
                                if (sampleValue == null || sampleValue.isEmpty()) {
                                    finalViewHolder.alphaTextView.setError("Enter Value here");
                                } else {
                                    boolean gpsPermissionStatus = checkWriteExternalPermission();

                                    if (gpsPermissionStatus) {
                                        String sample_filename = "Sample_" + locationID + "_" + siteID + "_" +
                                                sampleFieldParamID;
                                        Intent i = new Intent(context, MapActivity.class);
                                        i.putExtra("SITE_ID", Integer.parseInt(siteID));
                                        i.putExtra("SITE_NAME", sitename);
                                        i.putExtra("EVENT_ID", Integer.parseInt(eventID));
                                        i.putExtra("APP_ID", metaData.getCurrentFormID());
                                        i.putExtra("USER_ID", Integer.parseInt(userID));
                                        i.putExtra("LOC_ID", locationID);
                                        i.putExtra("SET_ID", getCurrentSetID() + "");
                                        i.putExtra("PARAM_ID", sampleFieldParamID);
                                        i.putExtra("SAMPLE_VALUE", sampleValue);

                                        i.putExtra("PREV_CONTEXT", "Sample");
                                        i.putExtra("OPERATION", GlobalStrings.TAG_SAMPLE);
                                        i.putExtra("ENABLE_SCREEN_CAPTURE", true);
                                        i.putExtra("SAMPLE_PREFIX", sample_filename);
                                        context.startActivity(i);
                                    } else {
                                        Toast.makeText(formActivity, "Location permission denied", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }

                    LinearLayout.LayoutParams layoutParams;

                    if (expression != null) {
                        if (expression.contains("SAMPLE")
                                || expression.toLowerCase().contains("!!coc!!")
                                || expression.toLowerCase().contains("!!set!!")) {
                            viewHolder.printCOC = new ImageButton(context);
                            viewHolder.printCOC.setFocusable(true);
                            viewHolder.printCOC.setClickable(true);

                            viewHolder.printCOC.setTag(Integer.toString(metaData.getMetaParamID()));
                            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 5, 0);
                            viewHolder.printCOC.setLayoutParams(layoutParams);

                            viewHolder.printCOC.setBackgroundResource(R.drawable.ic_print);
                            detailMainLayout.addView(viewHolder.printCOC);

                            viewHolder.printCOC.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showPrintCOCOptionsBottomSheet(metaData);
                                }
                            });

                            DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                            if (tempData != null) {
                                if (tempData.value != null && formActivity.currCocID != null
                                        && !tempData.value.isEmpty()
                                        && !metaData.getMetaParamLabel().toLowerCase()
                                        .contains("duplicate sample id")) {
                                    viewHolder.printCOC.setVisibility(View.VISIBLE);
                                } else
                                    viewHolder.printCOC.setVisibility(View.GONE);
                            } else viewHolder.printCOC.setVisibility(View.GONE);
                        }
                    }

                    boolean hasVisibleOperand = expression != null && !expression.isEmpty()
                            && expression.contains("!!visible!!");
                    if (viewHolder.alphaTextView.getText().toString().isEmpty() || hasVisibleOperand)
                        setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                    break;
                }
                case "TEXTCONTAINER": {
                    viewHolder.alphaTextView = getAlphaView(metaData, getLongTextLayoutParams());
                    itemlayout.addView(viewHolder.alphaTextView);
                    setCalculatedFieldParams(metaData, viewHolder.alphaTextView, viewHolder);
                    break;
                }
                case "SIGNATURE": {
                    //signature button
                    viewHolder.signatureView = getSignatureView(metaData);
                    llImageIcons.addView(viewHolder.signatureView);

                    viewHolder.rvSignature = getSignatureRecyclerView(metaData, viewHolder);
                    viewHolder.rvSignature.setTag(Integer.toString(metaData.getMetaParamID()));
                    itemlayout.addView(viewHolder.rvSignature);

                    viewHolder.lable.setOnClickListener(view -> startSignatureActivity(metaData));

/*                    viewHolder.tvSignatureNames = getFlexBoxView();
                    viewHolder.tvSignatureNames.setTag(Integer.toString(metaData.getMetaParamID()));
                    itemlayout.addView(viewHolder.tvSignatureNames);*/

/*                    DataHolder tmpData = fmapObject.get(metaData.getMetaParamID() + "");

                    if (tmpData != null) {
                        if (tmpData.value != null && !tmpData.value.isEmpty()) {
                            addChipsToView(viewHolder.tvSignatureNames, tmpData.value);
                        }
                    }*/
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "RATING": {
                    viewHolder.ratingView = getRatingView(metaData);
                    itemlayout.addView(viewHolder.ratingView);
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "GPS": {
                    //02-Mar-17 GPS CONTROL ADDED BY SHWETA
                    //30-Nov-20 GPS CONTROL UPDATED BY PATELSANKET
                    viewHolder.gpsButton = getGpsView(metaData, viewHolder);
                    llImageIcons.addView(viewHolder.gpsButton);

                    viewHolder.tvGpsCoordinates = new TextView(formActivity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup
                            .LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = 5;
                    int padding = Util.dpToPx(10);
                    viewHolder.tvGpsCoordinates.setTag(metaData.getMetaParamID());
                    viewHolder.tvGpsCoordinates.setPadding(padding, padding, padding, padding);
                    viewHolder.tvGpsCoordinates.setLayoutParams(layoutParams);
                    viewHolder.tvGpsCoordinates.setText("0.0000,0.0000");
                    viewHolder.tvGpsCoordinates.setTextColor(formColor);
                    viewHolder.tvGpsCoordinates.setBackgroundResource(R.drawable.data_entry_control_bg);
                    viewHolder.tvGpsCoordinates.setGravity(Gravity.CENTER);
                    viewHolder.tvGpsCoordinates.setTextSize(14);
                    itemlayout.addView(viewHolder.tvGpsCoordinates);

                    ViewHolder finalViewH = viewHolder;
                    viewHolder.tvGpsCoordinates.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalViewH.gpsButton.performClick();
                        }
                    });

                    if (valueToSet != null && !valueToSet.isEmpty())
                        viewHolder.tvGpsCoordinates.setText(valueToSet);
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "NUMERIC":
                case "TOTALIZER": {
                                 /*if (metaData.getMetaParamLabel().equals("Well Depth (ft)") || metaData.getMetaParamLabel().equals("DTW (ft)")){
                    Log.e("checkLabel", "getFormMasterData: parameterlabel:- "+metaData.getMetaParamLabel()+"isIsShowLast2:- "+metaData.isIsShowLast2()+"WarningHigh:- "+metaData.getMetaWarningHigh()+"WarningLow:- "+metaData.getMetaWarningLow());
                    viewHolder.numericView = getNumericView(metaData, viewHolder, getNumericLayoutParamsWithoutLast2Reading());
                    itemlayout.addView(viewHolder.numericView);
                    setWeightWithoutLastReading(detailMainLayout, metaData);
                }else */

                    boolean isShowLast2 = metaData.isIsShowLast2();
                    boolean isShowLast3 = isShowLast3(metaData);

                    if (attributes != null)
                        if (attributes.isShowLast2())
                            isShowLast2 = attributes.isShowLast2();

                    if (isShowLast2 && metaData.getCurrentFormID() == 142) {
                        viewHolder.numericView = getNumericView(metaData, viewHolder,
                                getNumericLayoutParamsWithoutLast2Reading());
                        itemlayout.addView(viewHolder.numericView);

                        if (formActivity.getCurSetID() < 1)
                            setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);
                    } else if (isShowLast2) {
                        viewHolder.numericView = getNumericView(metaData, viewHolder,
                                getNumericLayoutParams());
                        llHorizontalField.addView(viewHolder.numericView);

                        TextView blankBetPrev = getBlank(0);
                        llHorizontalField.addView(blankBetPrev);
                        viewHolder.prevReading.setTextSize(14);
                        llHorizontalField.addView(viewHolder.prevReading);
                        llHorizontalField.setGravity(Gravity.CENTER_VERTICAL);
//                     blankBet = getBlank(2);
//                    detailMainLayout.addView(blankBet);

                        Double percentDifference = metaData.getPercentDifference();

                        if (attributes != null)
                            if (attributes.getPercentDifference() != 0)
                                percentDifference = attributes.getPercentDifference();

                        if (percentDifference != 0) {
                            llHorizontalField.addView(viewHolder.prevPercent1);
                            TextView blankBet = getBlank(5);
                            llHorizontalField.setGravity(Gravity.CENTER_VERTICAL);
                            llHorizontalField.addView(blankBet);
                            llHorizontalField.addView(viewHolder.prevPercent2);
                        }

                        if (formActivity.getCurSetID() > 1) {
                            setWeight(llHorizontalField, metaData);
                        } else
                            setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);//added here as it will work
                        //two times if we add outside this block
                    } else if (isShowLast3) {
                        viewHolder.numericView = getNumericView(metaData, viewHolder,
                                getNumericLayoutParams());
                        llHorizontalField.addView(viewHolder.numericView);

                        TextView blankBetPrev = getBlank(0);
                        llHorizontalField.addView(blankBetPrev);
                        viewHolder.prevReading.setTextSize(12);
                        llHorizontalField.addView(viewHolder.prevReading);
                        llHorizontalField.setGravity(Gravity.CENTER_VERTICAL);

                        Double percentDifference = metaData.getPercentDifference();

                        if (percentDifference != 0) {
                            TextView blankBetPrevReadAndPer = getBlank(5);
                            llHorizontalField.addView(blankBetPrevReadAndPer);

                            llHorizontalField.addView(viewHolder.prevPercent1);
                            llHorizontalField.setGravity(Gravity.CENTER_VERTICAL);

                            TextView blankBetAtLast = getBlank(5);
                            llHorizontalField.addView(blankBetAtLast);
                        }

                        setWeightForShowLast3(llHorizontalField, metaData, viewHolder);
                    } else {
                        viewHolder.numericView = getNumericView(metaData, viewHolder, getNumericLayoutParamsWithoutLast2Reading());
                        itemlayout.addView(viewHolder.numericView);
                        setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);
                    }
                    break;
                }
                case "PICKER": {
                    DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

                    if (metaData.getNameValueMap().containsKey("Coc")
                            || metaData.getNameValueMap().containsKey("COC")) {
                        LocationDataSource ld = new LocationDataSource(formActivity);
                        ArrayList<SCocMaster> cocMasterArrayList
                                = ld.getAllCoCIDs(formActivity.getEventID() + "",
                                formActivity.getSiteID() + "");
                        LinkedHashMap<String, String> pickerCocNameValueMap = new LinkedHashMap<>();

                        for (SCocMaster master : cocMasterArrayList) {
                            pickerCocNameValueMap.put(master.getCocDisplayId(), master.getCocDisplayId());
                            cocMasterValueMap.put(master.getCocDisplayId(), master);
                        }
                        tempData.setNameValuePair(pickerCocNameValueMap);
                    } else {
                        tempData.setNameValuePair(metaData.getNameValueMap());
                    }

                    if (tempData.value == null || tempData.value.isEmpty()) {
                        //30-Mar-2018 GET DEFAULT VALUE
                        dv = new DefaultValueDataSource(context);
                        tempData.value = dv.getDefaultValue(locationID, metaData.getCurrentFormID()
                                + "", metaData.getMetaParamID() + "", getCurrentSetID() + "");

                        if (tempData.value != null && !tempData.value.isEmpty()) {
                            fmapObject.put(Integer.toString(metaData.getMetaParamID()), tempData);
                            mapObject.put(Integer.toString(metaData.getMetaParamID()), tempData);
                        }
                    }

                    viewHolder.dataSpinner = getSpinnerView(metaData, tempData.getItemNames(),
                            tempData.getPositionForValue(tempData.value));
                    itemlayout.addView(viewHolder.dataSpinner);

                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "RADIO": {
                    DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                    tempData.setNameValuePair(metaData.getNameValueMap());
                    //getLinearRadioLayout(context, viewHolder.radioGroup, gText)
                    if (tempData.value == null || tempData.value.isEmpty()) {
                        //30-Mar-2018 GET DEFAULT VALUE
                        dv = new DefaultValueDataSource(context);
                        tempData.value = dv.getDefaultValue(locationID, metaData.getCurrentFormID()
                                + "", metaData.getMetaParamID() + "", getCurrentSetID() + "");

                        if (tempData.value != null && !tempData.value.isEmpty()) {
                            fmapObject.put(Integer.toString(metaData.getMetaParamID()), tempData);
                            mapObject.put(Integer.toString(metaData.getMetaParamID()), tempData);
                        }
                    }

                    int count = tempData.getItemNames().size();
                    if (count > 3) {
                        List<List<String>> parts = Util.getChopped(tempData.getItemNames(), 4);
                        for (int i = 0; i < parts.size(); i++) {

                            //  viewHolder.radioGroup = getRadioGroup(position, parts.get(i), tempData.getPositionForValue());
                            viewHolder.radioGroup = getRadioGroup(metaData, parts.get(i), tempData.getPositionForValue(tempData.value));

                            if (itemlayout != null) {
                                itemlayout.addView(viewHolder.radioGroup);
                            } else {
                                detailMainLayout.addView(viewHolder.radioGroup);
                            }
                        }
                    } else {
                        viewHolder.radioGroup = getRadioGroup(metaData, tempData.getItemNames(), tempData.getPositionForValue(tempData.value));
                        if (itemlayout != null) {
                            itemlayout.addView(viewHolder.radioGroup);
                        } else {
                            detailMainLayout.addView(viewHolder.radioGroup);
                        }
                    }
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "AUTOCOMPLETE": {
                    DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                    tempData.setLovID(metaData.getMetaLovId());

                    tempData.value = valueToSet;

                    LinearLayout.LayoutParams layoutParams;

                    //12-Jul-17 NAVIGATE TO NEW FORM ON THE BASIS OF SELECTED VALUE's FORM ID
                    ImageView formHolder = new ImageView(context);
                    formHolder.setImageResource(R.drawable.pen_fillform);
                    formHolder.setTag(Integer.toString(metaData.getMetaParamID()));
                    layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    formHolder.setLayoutParams(layoutParams);
                    viewHolder.enableForms = formHolder;

                    Log.i(TAG, "paramid " + metaData.getMetaParamID());

                    viewHolder.enableForms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int navigate_to_formID = fmapObject.get(Integer.toString(metaData.getMetaParamID())).getGoto_formID();
                            Log.i(TAG, "Clicked on form:" + navigate_to_formID);
                            int index = 0;//= context.childAppList.indexOf(mData);

                            MobileAppDataSource mobileAppSource = new MobileAppDataSource(context);

                            List<MobileApp> childAppList
                                    = mobileAppSource.getChildApps(Integer.parseInt(parentFormID),
                                    Integer.parseInt(siteID), locationID);

                            if (navigate_to_formID > 0) {

                                for (int i = 0; i < childAppList.size(); i++) {
                                    MobileApp app = childAppList.get(i);

                                    if (app.getAppID() == navigate_to_formID) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index > 0 && getCurrentFormNumber() != index) {
                                    parentContext.last_position = getCurrentFormNumber();
                                    parentContext.jumpToChildApp(index);
                                }
                            }
                        }
                    });

                    viewHolder.new_actv = getAutoCompleteView(metaData, tempData.value, inputType,
                            tempData.getLovID(), tempData.getParentlovItemID(), null);

                    if (fmapObject.get(Integer.toString(metaData.getMetaParamID())).getGoto_formID() > 0) {
                        viewHolder.enableForms.setVisibility(View.VISIBLE);
                    } else {
                        //detailMainLayout.removeView(viewHolder.enableNotes);
                        viewHolder.enableForms.setVisibility(View.GONE);
                    }

                    itemlayout.addView(viewHolder.new_actv);
                    detailMainLayout.addView(viewHolder.enableForms);
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "MULTIAUTOCOMPLETE":
                case "AUTOSETGENERATOR": {
                    //|| inputType.equalsIgnoreCase("MULTISPECIES")

                    DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

                    tempData.setLovID(metaData.getMetaLovId());
                    tempData.setParentlovItemID(0);
                    LovDataSource lovDS = new LovDataSource(context);
                    tempData.setNameValuePair(lovDS.getLovNameValuePair(tempData.getLovID(), siteID + ""));

                    String defaultValue = metaData.getDefaultValue();
                    LinearLayout.LayoutParams layoutParams;

                    //12-Jul-17 NAVIGATE TO NEW FORM ON THE BASIS OF SELECTED VALUE's FORM ID
                    ImageView formHolder = new ImageView(context);
                    formHolder.setImageResource(R.drawable.pen_fillform);
                    formHolder.setTag(Integer.toString(metaData.getMetaParamID()));
                    layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(5, 0, 0, 0);
                    formHolder.setLayoutParams(layoutParams);
                    viewHolder.enableForms = formHolder;

                    Log.i(TAG, "paramid " + metaData.getMetaParamID());

                    viewHolder.enableForms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int navigate_to_formID = fmapObject.get(Integer.toString(metaData.getMetaParamID())).getGoto_formID();
                            Log.i(TAG, "Clicked on form:" + navigate_to_formID);
                            int index = 0;//= context.childAppList.indexOf(mData);

                            MobileAppDataSource mobileAppSource = new MobileAppDataSource(context);

                            List<MobileApp> childAppList
                                    = mobileAppSource.getChildApps(Integer.parseInt(parentFormID),
                                    Integer.parseInt(siteID), locationID);

                            if (navigate_to_formID > 0) {

                                for (int i = 0; i < childAppList.size(); i++) {
                                    MobileApp app = childAppList.get(i);

                                    if (app.getAppID() == navigate_to_formID) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index > 0 && getCurrentFormNumber() != index) {
                                    parentContext.last_position = getCurrentFormNumber();
                                    parentContext.jumpToChildApp(index);
                                }
                            }
                        }
                    });

                    if ((tempData.value == null) || (tempData.value.length() == 0)) {
                        tempData.value = dv.getDefaultValue(locationID,
                                metaData.getCurrentFormID() + "",
                                metaData.getMetaParamID() + "", getCurrentSetID() + "");
                        if (tempData.value == null) {
                            tempData.value = defaultValue;
                        }

                        Log.i(TAG, "Default Value for MULTI Auto Complete:" + tempData.value);
                    }

                    viewHolder.new_actv = getAutoCompleteView(metaData, tempData.value, inputType,
                            tempData.getLovID(), tempData.getParentlovItemID(), tempData.nameValuePair);

                    if (metaData.getMetaInputType().equalsIgnoreCase("AUTOSETGENERATOR")) {

                        String fieldoperands_expression = getExpressionFromMetaOrAttribute(metaData);

                        if (fieldoperands_expression != null && !fieldoperands_expression.isEmpty()
                                && fieldoperands_expression.contains("COPY")) {
                            int mobAppID = Integer.parseInt(fieldoperands_expression
                                    .substring(fieldoperands_expression.indexOf("{") + 1,
                                            fieldoperands_expression.lastIndexOf("}")));
                            Log.i(TAG, "MobileAppID from COPY Expression:" + mobAppID);
                            metaData.setFormID(mobAppID);
                            tempData.setGoto_formID(mobAppID);
                            fmapObject.get(Integer.toString(metaData.getMetaParamID())).setGoto_formID(mobAppID);
                        }
                    }

                    if (fmapObject.get(Integer.toString(metaData.getMetaParamID())).getGoto_formID() > 0) {
                        viewHolder.enableForms.setVisibility(View.VISIBLE);
                    } else {
                        //detailMainLayout.removeView(viewHolder.enableNotes);
                        viewHolder.enableForms.setVisibility(View.GONE);
                    }

                    itemlayout.addView(viewHolder.new_actv);
                    detailMainLayout.addView(viewHolder.enableForms);
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "MULTIMETHODS": {
                    DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                    viewHolder.new_actv = getAutoCompleteView(metaData, tempData.value, inputType,
                            0, 0, null);
                    itemlayout.addView(viewHolder.new_actv);
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "PHOTOS": {
                    setObservedPhotosList(metaData, llImageIcons, itemlayout, viewHolder);
                    callSetCalculatedIfVisible(metaData, null);
                    break;
                }
                case "TASK": {
                    viewHolder.enableParent = new ImageButton(context);
                    viewHolder.enableParent.setFocusable(true);
                    viewHolder.enableParent.setClickable(true);

                    viewHolder.enableParent.setTag(Integer.toString(metaData.getMetaParamID()));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 5, 0);
                    viewHolder.enableParent.setLayoutParams(layoutParams);
                    viewHolder.enableParent.setBackgroundResource(R.drawable.ic_task_list);

                    viewHolder.tvAddTasks = getTaskCountView(metaData);

                    if (itemlayout != null) {
                        itemlayout.addView(viewHolder.tvAddTasks);
                    }

                    detailMainLayout.addView(viewHolder.enableParent);

                    viewHolder.enableParent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openTaskActivity(metaData);
                        }
                    });

                    viewHolder.tvAddTasks.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openTaskActivity(metaData);
                        }
                    });
                    break;
                }
                case "QRCODE":
                case "BARCODE": {
                    viewHolder.enableParent = new ImageButton(context);
                    viewHolder.enableParent.setFocusable(true);
                    viewHolder.enableParent.setClickable(true);

                    viewHolder.enableParent.setTag(Integer.toString(metaData.getMetaParamID()));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 5, 0);
                    viewHolder.enableParent.setLayoutParams(layoutParams);
                    viewHolder.enableParent.setBackgroundResource(R.drawable.ic_qr_code_scanner);

                    viewHolder.tvBarCode = getQRCodeView(metaData);

                    if (itemlayout != null) {
                        itemlayout.addView(viewHolder.tvBarCode);
                    }

                    detailMainLayout.addView(viewHolder.enableParent);

                    viewHolder.enableParent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openQRCodeActivity(metaData.getMetaParamID());
                        }
                    });
                    break;
                }
            }

            LinearLayout.LayoutParams layoutParams;
            boolean enableNotes = metaData.isIsEnableParameterNotes();

            if (attributes != null)
                if (attributes.isEnable_parameter_notes())
                    enableNotes = true;

            if (enableNotes) {
                ImageView notesHolder = new ImageView(context);
                DataHolder dh = fmapObject.get(metaData.getMetaParamID() + "");

                FieldDataSource fieldDataSource = new FieldDataSource(context);
                dh.isnote_taken = fieldDataSource.isNoteTaken_Data(parentContext.getEventID(), parentContext.getCurSetID(),
                        parentContext.getLocID(), parentContext.getSiteID(), parentContext.getCurrentAppID(),
                        metaData.getMetaParamID());

                if (dh.isnote_taken) {
                    notesHolder.setImageResource(R.drawable.data_entry_note_blue_icon);
                } else {
                    notesHolder.setImageResource(R.drawable.data_entry_gray_note_icon);
                }

                notesHolder.setTag(Integer.toString(metaData.getMetaParamID()));
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.setMarginEnd(8);
                    layoutParams.setMarginStart(10);
                }

//                layoutParams.setMargins(10, 0, 8, 16);
                notesHolder.setLayoutParams(layoutParams);
                viewHolder.enableNotes = notesHolder;

                // below inputTypes should have notes icon only
                if (inputType.equalsIgnoreCase("RADIO")
                        || inputType.equalsIgnoreCase("PICKER")
                        || inputType.equalsIgnoreCase("COUNTER")
                        || inputType.equalsIgnoreCase("AUTOCOMPLETE")
                        || inputType.equalsIgnoreCase("MULTIAUTOCOMPLETE")) {
                    viewHolder.enableNotes.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.enableNotes.setVisibility(View.GONE);
                }

                viewHolder.enableNotes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent notedialog = new Intent(context, NoteDialogBoxActivity.class);
                        notedialog.putExtra("EVENT_ID", Integer.parseInt(eventID));
                        notedialog.putExtra("LOCATION_ID", locationID);
                        notedialog.putExtra("APP_ID", metaData.getCurrentFormID());
                        notedialog.putExtra("SITE_ID", Integer.parseInt(siteID));
                        notedialog.putExtra("paramID", metaData.getMetaParamID());
                        notedialog.putExtra("setID", getCurrentSetID());
                        notedialog.putExtra("SiteName", sitename);
                        notedialog.putExtra("UserID", Integer.parseInt(userID));

                        formActivity.startActivityForResult(notedialog, CAPTURE_NOTE_REQUEST_CODE);
                    }
                });
                llImageIcons.addView(viewHolder.enableNotes);
            }

            if (metaData.getFieldParameterOperands() != null
                    && !metaData.getFieldParameterOperands().isEmpty()
                    && metaData.getMetaInputType().equalsIgnoreCase("NUMERIC")) {
                //adding summation icon in case we have an expression
                ImageView notesHolder = new ImageView(context);
                notesHolder.setImageResource(R.drawable.ic_sigma);

                notesHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //summation btn will also be clicked to refresh 11 May, 22
                        formActivity.tvCalculate.performClick();
                    }
                });

                notesHolder.setTag(Integer.toString(metaData.getMetaParamID()));
                layoutParams = new LinearLayout.LayoutParams(Util.dpToPx(15), Util.dpToPx(15));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layoutParams.setMarginEnd(8);
                    layoutParams.setMarginStart(10);
                }

//                layoutParams.setMargins(10, 0, 8, 16);
                notesHolder.setLayoutParams(layoutParams);
                viewHolder.enableNotes = notesHolder;
                llImageIcons.addView(viewHolder.enableNotes);
            }

            if (metaData.isParentField) {
                //expandable for other than species

                viewHolder.enableParent = new ImageButton(context);
                viewHolder.enableParent.setFocusable(true);
                viewHolder.enableParent.setClickable(true);

                viewHolder.enableParent.setTag(Integer.toString(metaData.getMetaParamID()));
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 5, 0);
                viewHolder.enableParent.setLayoutParams(layoutParams);

                if (!metaData.isExpanded) {
                    viewHolder.enableParent.setBackgroundResource(R.drawable.expand_arrow);
                } else {
                    viewHolder.enableParent.setBackgroundResource(R.drawable.collapse_arrow);
                }

                boolean isWeatherInputType = metaData.getMetaInputType()
                        .equalsIgnoreCase("weather");

                final ViewHolder finalViewHolder1 = viewHolder;

                viewHolder.enableParent.setOnClickListener(view -> {
                    if (isWeatherInputType) {
                        Intent intentW = new Intent(context, WeatherActivity.class);
                        intentW.putParcelableArrayListExtra(GlobalStrings.KEY_META_DATA,
                                new ArrayList<>(metaObjects));
                        intentW.putExtra(GlobalStrings.KEY_SET_ID, getCurrentSetID());
                        intentW.putExtra(GlobalStrings.KEY_LOCATION_ID, locationID);
                        intentW.putExtra(GlobalStrings.KEY_EVENT_ID, eventID);
                        intentW.putExtra(GlobalStrings.KEY_SITE_ID, siteID);
                        ((FormActivity) context).startActivityForResult(intentW,
                                GlobalStrings.REQUEST_CODE_WEATHER);
                    } else {
                        getChildParamsList(metaData, finalViewHolder1, view);
                    }
                });

                detailMainLayout.addView(viewHolder.enableParent);

                if (isWeatherInputType) {
                    viewHolder.enableParent.setBackgroundResource(R.drawable.ic_cloud);

                    int paramID = metaData.getMetaParamID();
                    final TextView tvZipCode = new TextView(context);
                    LinearLayout.LayoutParams lp = new LinearLayout
                            .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 20, 0);
                    tvZipCode.setLayoutParams(lp);
                    tvZipCode.setHint(formActivity.getString(R.string.zip_code));
                    tvZipCode.setTextSize(14);
                    tvZipCode.setTextColor(formColor);
                    tvZipCode.setPadding(20, 25, 20, 25);
                    tvZipCode.setTag(Integer.toString(paramID));
                    tvZipCode.setBackgroundResource(R.drawable.data_entry_control_bg);
                    tvZipCode.setGravity(Gravity.CENTER);

                    DataHolder dh = mapObject.get(metaData.getMetaParamID() + "");
                    if (dh != null) {
                        tvZipCode.setText(dh.value);
                    }
                    viewHolder.tvZipCode = tvZipCode;

                    if (itemlayout != null) {
                        itemlayout.addView(viewHolder.tvZipCode);
                    }

                    viewHolder.tvZipCode.setOnClickListener(view -> {
                        Intent intentW = new Intent(context, WeatherActivity.class);
                        intentW.putParcelableArrayListExtra(GlobalStrings.KEY_META_DATA,
                                getChildList(metaData));
                        intentW.putExtra(GlobalStrings.KEY_SET_ID, getCurrentSetID());
                        intentW.putExtra(GlobalStrings.KEY_LOCATION_ID, locationID);
                        intentW.putExtra(GlobalStrings.KEY_EVENT_ID, eventID);
                        intentW.putExtra(GlobalStrings.KEY_SITE_ID, siteID);
                        ((FormActivity) context).startActivityForResult(intentW,
                                GlobalStrings.REQUEST_CODE_WEATHER);
                    });

                    enableChildFieldsForWeatherData(metaData);
                }
            }

            //31-01-2018 UPDATED
            if ((inputType.equals("DATE")) || (inputType.equals("TIME"))) {
                setWeightDateAndTime(detailMainLayout, metaData);//
            }

            setMandatoryFieldAlert(metaData, viewHolder);

            String parameterHint = metaData.getParameterHint();

            if (parameterHint != null) {
                if (!parameterHint.trim().isEmpty()) {

                    ImageView ivParamHintInfo = new ImageView(formActivity);

                    ivParamHintInfo.setImageDrawable(VectorDrawableUtils.getDrawable(formActivity,
                            R.drawable.ic_info, R.color.half_black));
                    ivParamHintInfo.setTag(Integer.toString(metaData.getMetaParamID()));
                    LinearLayout.LayoutParams lParams
                            = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    lParams.setMargins(10, 0, 8, 3);
                    ivParamHintInfo.setLayoutParams(lParams);
                    llImageIcons.addView(ivParamHintInfo);
                    viewHolder.parameterHintInfo = ivParamHintInfo;

                    viewHolder.parameterHintInfo.setOnClickListener(v -> showAlertHint(parameterHint));
                }
            }

            if (itemlayout != null) {
                itemlayout.setOnClickListener(view -> {

                    String paramHint = metaData.getParameterHint();

                    if (attributes != null)
                        if (attributes.getParameterHint() != null
                                && !attributes.getParameterHint().isEmpty())
                            paramHint = attributes.getParameterHint();

                    if (paramHint != null) {
                        if (!paramHint.isEmpty())
                            showAlertHint(paramHint);
                    }
                });
            }

            rowView.setTag(viewHolder);
            metaData.setForm_field_row(rowView);
            form_meta_list.add(metaData);

/*            if ((!metaData.isParentField & !metaData.isChildField) | metaData.isParentField
                    | (metaData.isChildField & metaData.isVisible)) {
                filteredmetaObjects.add(metaData);
            }*/

            filteredMetaObjects.add(metaData);
            mapMetaObjects.put(metaData.getMetaParamID(), metaData);

            if (inputType.equalsIgnoreCase("CHECKBOX")
                    || inputType.equalsIgnoreCase("LABEL")) {
                updateBottleData(metaData);
            }
        }

        return filteredMetaObjects;
    }

    public RecyclerView getSignatureRecyclerView(MetaData metaData, ViewHolder viewHolder) {
        RecyclerView rvSignature = (RecyclerView) LayoutInflater.from(formActivity)
                .inflate(R.layout.layout_signature_recycler,
                        null, false);
        rvSignature.setLayoutManager(new LinearLayoutManager(formActivity,
                LinearLayoutManager.HORIZONTAL, false));

        setSignatureAdapter(rvSignature, metaData, viewHolder);
        return rvSignature;
    }

    public void setSignatureAdapter(RecyclerView rvSignature, MetaData metaData, ViewHolder viewHolder) {
        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(formActivity);
        ArrayList<CustomerSign> customerSigns
                = attachmentDataSource.getAttachmentListForSignature(Integer.parseInt(eventID),
                Integer.parseInt(siteID), metaData.getMetaParamID(), locationID, Integer.parseInt(userID),
                formActivity.getCurrentAppID(), formActivity.getCurSetID());

        SignatureAdapter signatureAdapter = new SignatureAdapter(customerSigns,
                formActivity, new SignatureUpdateListener() {
            @Override
            public void onSignatureRemoved(ArrayList<CustomerSign> sign) {

            }

            @Override
            public void onSignatureViewClicked() {
                startSignatureActivity(metaData);
            }
        }, true);
        rvSignature.setAdapter(signatureAdapter);
        viewHolder.signatureAdapter = signatureAdapter;
    }

    public void checkWarningValueForViolation(MetaData metaData, String value) {
        DefaultValueDataSource defaultValueDataSource = new DefaultValueDataSource(formActivity);
        DefaultValueModel defValue = defaultValueDataSource.getDefaultValueToWarn(locationID,
                formActivity.getCurrentAppID() + "", metaData.getMetaParamID() + "", formActivity.getCurSetID() + "");
        if (defValue != null) {
            String warningValue = defValue.getWarningValue();
            FieldDataSource fieldDataSource = new FieldDataSource(formActivity);

            if (warningValue != null && !warningValue.isEmpty()) {
                String flag = "0";
                if (value != null && value.equals(warningValue)) {
                    //add save and update method if necessary
                    flag = "1";
                }
                fieldDataSource.updateViolationFlag(eventID, metaData.getMetaParamID(),
                        formActivity.getCurSetID(), locationID, flag,
                        Integer.parseInt(siteID), formActivity.getCurrentAppID());
            }
        }
    }

    private void openQRCodeActivity(int fpId) {
        if (formActivity.checkCameraPermission()) {
            Intent intent = new Intent(formActivity, QRScannerActivity.class);
            intent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, fpId);
            formActivity.startActivityForResult(intent, REQUEST_CODE_BARCODE_SCANNER);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                formActivity.requestCameraPermission();
            }
        }
    }

    private void callSetCalculatedIfVisible(MetaData metaData, EditText editText) {
        if (metaData.getFieldParameterOperands() != null
                && metaData.getFieldParameterOperands().toLowerCase().contains("!!visible!!"))
            setCalculatedFieldParams(metaData, editText, null);
    }

    private void setObservedPhotosList(MetaData metaData, LinearLayout llImageIcons,
                                       LinearLayout lllayout, ViewHolder viewHolder) {
        ImageView photosHolder = new ImageView(context);
        photosHolder.setImageDrawable(VectorDrawableUtils.getDrawable(formActivity,
                R.drawable.ic_photo_camera, R.color.black_faint));
        photosHolder.setTag(Integer.toString(metaData.getMetaParamID()));
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd(8);
            layoutParams.setMarginStart(10);
        }
        photosHolder.setLayoutParams(layoutParams);
        viewHolder.observedPhotos = photosHolder;
        llImageIcons.addView(viewHolder.observedPhotos);

        viewHolder.observedPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCamera(metaData);
            }
        });

        RecyclerView rvPhotos = new RecyclerView(formActivity);
        rvPhotos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(formActivity,
                LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(linearLayoutManager);

        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(formActivity);
        ArrayList<GalleryItem> listPhotos = attachmentDataSource.getAttachmentForFieldParam(siteID,
                eventID, locationID, metaData.getCurrentFormID() + "",
                metaData.getMetaParamID() + "", formActivity.getCurSetID(),
                formActivity.getSiteName(), formActivity.currentLocationName);

        if (listPhotos.size() > 0) {
            viewHolder.observedPhotosAdapter
                    = new ObservedPhotosAdapter(listPhotos,
                    formActivity, metaData.getMetaParamID());
        } else {
            viewHolder.observedPhotosAdapter
                    = new ObservedPhotosAdapter(new ArrayList<>(),
                    formActivity, metaData.getMetaParamID());
        }
        rvPhotos.setAdapter(viewHolder.observedPhotosAdapter);
        rvPhotos.setTag(Integer.toString(metaData.getMetaParamID()));
        lllayout.addView(rvPhotos);
    }

    public void handleCamera(MetaData metaData) {

        try {
            Intent mediaIntent = new Intent(formActivity, MediaPickerActivity.class);
            mediaIntent.putExtra(GlobalStrings.IS_CAMERA, true);
            mediaIntent.putExtra(GlobalStrings.KEY_FIELD_PARAM_ID, metaData.getMetaParamID());
            mediaIntent.putExtra(GlobalStrings.IS_FROM_FORM_MASTER, true);
            formActivity.startActivityForResult(mediaIntent,
                    BaseMenuActivity.REQUEST_CODE_FORM_MASTER_MEDIA_PICKER);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "handleCamera() error:" + e.getLocalizedMessage());
        }
    }

    private FlexboxLayout getFlexBoxView() {
        FlexboxLayout flexBoxSignature = new FlexboxLayout(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        flexBoxSignature.setLayoutParams(lp);

        flexBoxSignature.setAlignContent(AlignContent.FLEX_START);
        flexBoxSignature.setAlignItems(AlignItems.FLEX_START);
        flexBoxSignature.setFlexWrap(FlexWrap.WRAP);
        flexBoxSignature.setShowDivider(FlexboxLayout.SHOW_DIVIDER_BEGINNING | FlexboxLayout.SHOW_DIVIDER_MIDDLE);
        flexBoxSignature.setDividerDrawable(context.getResources().getDrawable(R.drawable.flex_divider));

        flexBoxSignature.setPadding(20, 10, 10, 10);
        return flexBoxSignature;
    }

    private TextView getTaskCountView(MetaData metaData) {

        int paramID = metaData.getMetaParamID();
        final TextView tvAddTasks = new TextView(context);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 20, 0);
        tvAddTasks.setLayoutParams(lp);
        tvAddTasks.setTag(metaData.getMetaParamID());
        tvAddTasks.setHint(formActivity.getString(R.string.add_task));
        tvAddTasks.setTextSize(14);
        tvAddTasks.setTextColor(formColor);
        tvAddTasks.setPadding(20, 25, 20, 25);
        tvAddTasks.setTag(Integer.toString(paramID));
        tvAddTasks.setBackgroundResource(R.drawable.data_entry_control_bg);
        tvAddTasks.setGravity(Gravity.CENTER);

        int taskCount = 0;
        TaskDetailsDataSource taskDetailsDataSource = new TaskDetailsDataSource(context);
        taskCount = taskDetailsDataSource.getTasksCount(metaData.getMetaParamID(),
                siteID, getCurrentSetID(), formActivity.getCurrentAppID(), locationID);

        DefaultValueDataSource dv = new DefaultValueDataSource(context);
        DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID, metaData.getCurrentFormID() + "",
                metaData.getMetaParamID() + "", formActivity.getCurSetID() + "");
        DataHolder dh = mapObject.get(metaData.getMetaParamID() + "");

        if (taskCount > 0) {
            String count = String.valueOf(taskCount);
            tvAddTasks.setText(count);
            handleTextData(metaData, count, getCurrentSetID());
        } else {
            String value = getDefaultValueToSet(dh, metaData, d_model.getDefaultValue());
            tvAddTasks.setText(value);
        }
        return tvAddTasks;
    }

    private EditText getQRCodeView(MetaData metaData) {

        int paramID = metaData.getMetaParamID();
        final EditText tvQRCode = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 20, 0);
        tvQRCode.setLayoutParams(lp);
        tvQRCode.setTag(metaData.getMetaParamID());
        tvQRCode.setHint(formActivity.getString(R.string.barcode_text));
        tvQRCode.setTextSize(14);
        tvQRCode.setTextColor(formColor);
        tvQRCode.setPadding(20, 25, 20, 25);
        tvQRCode.setTag(Integer.toString(paramID));
        tvQRCode.setBackgroundResource(R.drawable.data_entry_control_bg);
        tvQRCode.setGravity(Gravity.CENTER);

        DefaultValueDataSource dv = new DefaultValueDataSource(context);
        DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID, metaData.getCurrentFormID() + "",
                metaData.getMetaParamID() + "", formActivity.getCurSetID() + "");
        DataHolder dh = mapObject.get(metaData.getMetaParamID() + "");

        String value = getDefaultValueToSet(dh, metaData, d_model.getDefaultValue());
        tvQRCode.setText(value);

        tvQRCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                String value = tvQRCode.getText().toString();
                saveData_and_updateCreationDate(formActivity, metaData, value, getCurrentSetID());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return tvQRCode;
    }

    private void showPrintCOCOptionsBottomSheet(MetaData metaData) {
        try {
            View sheetView = LayoutInflater.from(formActivity)
                    .inflate(R.layout.layout_bottomsheet_print_coc, null);
            BottomSheetDialog mBottomSheetEmailLogs = new BottomSheetDialog(formActivity);
            mBottomSheetEmailLogs.setContentView(sheetView);
            mBottomSheetEmailLogs.show();

            // Remove default white color background
            FrameLayout bottomSheet = mBottomSheetEmailLogs
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackground(null);
            }

            LinearLayout llViewCOC = sheetView.findViewById(R.id.llViewCOC);
            LinearLayout llDownloadCOC = sheetView.findViewById(R.id.llDownloadCOC);
            LinearLayout llPrintCOC = sheetView.findViewById(R.id.llPrintCOC);
            LinearLayout llCancel = sheetView.findViewById(R.id.llCancel);

            llViewCOC.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                startShowFilesActivity();
            });

            llDownloadCOC.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                if (formActivity.currCocID != null) {
                    new AsyncCocFileDownload().execute();
                }
            });

            llPrintCOC.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
                showPrintAlert(metaData);
            });

            llCancel.setOnClickListener(v -> {
                mBottomSheetEmailLogs.cancel();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPrintAlert(MetaData metaData) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(formActivity);
        View bottomView = LayoutInflater.from(formActivity).inflate(R.layout.layout_print_coc_details, null, false);

        btnClose = bottomView.findViewById(R.id.btnClose);
        btnPrint = bottomView.findViewById(R.id.btnPrint);
        CustomTextView tvClient = bottomView.findViewById(R.id.tvClient);
        CustomTextView tvProject = bottomView.findViewById(R.id.tvProjectName);
        CustomTextView tvSample = bottomView.findViewById(R.id.tvSampleId);
        CustomTextView tvDateTime = bottomView.findViewById(R.id.tvDateTime);
        CustomTextView tvPres = bottomView.findViewById(R.id.tvPres);

        tvClient.setText("client_name");
        tvProject.setText(formActivity.getSiteName());

        DataHolder dh = mapObject.get(metaData.getMetaParamID() + "");
        tvSample.setText(dh.value);

        DataHolder dhDate = mapObject.get("25");
        DataHolder dhTime = mapObject.get("15");
        String dateTime = dhDate.value + " " + dhTime.value;
        tvDateTime.setText(dateTime);

        CocDetailDataSource detailDataSource = new CocDetailDataSource(context);
        List<SCocDetails> sCocDetailsList = detailDataSource
                .getDefaultMethodfromcocDetail(formActivity.currCocID,
                        locationID);
        Log.i(TAG, "Current CoC ID:" + formActivity.currCocID);

        StringBuilder bottle = new StringBuilder();

        if (sCocDetailsList != null && sCocDetailsList.size() > 0) {
            StringBuilder methodidList = new StringBuilder();

            for (int i = 0; i < sCocDetailsList.size(); i++) {
                if (i == sCocDetailsList.size() - 1) {
                    methodidList.append(sCocDetailsList.get(i).getMethodId());
                } else {
                    methodidList.append(sCocDetailsList.get(i).getMethodId()).append(",");
                }
            }

            List<CoCBottles> cocbottles = new ArrayList<>();
            MethodDataSource methodDataSource1 = new MethodDataSource(context);
            List<CoCBottles> allbottlesList = new ArrayList<>();
            allbottlesList = methodDataSource1.getBottles(methodidList.toString());
            cocbottles.addAll(allbottlesList);

            if (cocbottles.size() > 0) {

                for (int i = 0; i < cocbottles.size(); i++) {
                    String bottlename = cocbottles.get(i).getBottleName();

                    if (i == cocbottles.size() - 1) {
                        bottle.append(bottlename);
                    } else {
                        bottle.append(bottlename).append(",");
                    }
                }
            }
        }

        if (!bottle.toString().isEmpty())
            tvPres.setText(bottle.toString());
        else
            tvPres.setText("N/A");
//            tvPres.setText("Amber Glass 4 deg C");

        builder.setView(bottomView);

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnClose.setOnClickListener(v -> alertDialog.cancel());

        btnPrint.setOnClickListener(v -> {
            btnClose.setVisibility(View.GONE);
            btnPrint.setVisibility(View.GONE);

            doPhotoPrint(Util.loadBitmapFromView(bottomView));
            alertDialog.cancel();
        });
    }

    private void doPhotoPrint(Bitmap bitmap) {
        PrintHelper photoPrinter = new PrintHelper(formActivity);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("COC Labels", bitmap);
    }

    class AsyncCocFileDownload extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            formActivity.showAlertProgressWithMsg(formActivity.getString(R.string.downloading_coc));
        }

        @Override
        protected String doInBackground(String... strings) {
            AquaBlueServiceImpl mAquaBlueService = new AquaBlueServiceImpl(formActivity);
            return mAquaBlueService.cocFileDownload(formActivity.getString(R.string.prod_base_uri),
                    formActivity.getString(R.string.url_coc_file_download),
                    formActivity.currCocID, userID);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            formActivity.cancelAlertProgressWithMsg();
            if (s.equals("true")) {
                formActivity.showToast("Downloaded Successfully", true);

                startShowFilesActivity();
            } else {
                formActivity.showToast("Download failed", true);
            }
        }
    }

    private void startShowFilesActivity() {
        if (formActivity.currCocID != null) {
            CocMasterDataSource cocMasterDataSource = new CocMasterDataSource(context);
            SCocMaster cocMaster
                    = cocMasterDataSource.getCoCMasterDataForCOCID(formActivity.currCocID);
            String cocDisplayId = formActivity.currCocID;

            if (cocMaster != null) {
                if (!cocMaster.getCocDisplayId().trim().isEmpty())
                    cocDisplayId = cocMaster.getCocDisplayId();
            }

            Intent intent = new Intent(formActivity, ShowFilesActivity.class);
            intent.putExtra(GlobalStrings.FOLDER_NAME, cocDisplayId);
            intent.putExtra(GlobalStrings.CURRENT_SITENAME, formActivity.getSiteName());
            formActivity.startActivity(intent);
        }
    }

    private void openTaskActivity(MetaData metaData) {
        Intent intent
                = new Intent(context, TaskTabActivity.class);

        TaskIntentData data = new TaskIntentData();
        data.setProjectId(siteID);
        data.setLocationId(locationID);
        data.setFieldParamId(metaData.getMetaParamID());
        data.setMobileAppId(formActivity.getCurrentAppID());
        data.setSetId(getCurrentSetID());

        intent.putExtra(GlobalStrings.TASK_INTENT_DATA, data);
        ((FormActivity) context).startActivity(intent);
    }

    private void enableChildFieldsForWeatherData(MetaData metaData) {
        boolean hasWeatherData = false;

        if (mapMetaObjects.containsKey(metaData.getMetaParamID())) {
/*            if (mapMetaObjects.get(metaData.getMetaParamID()).getMetaParamLabel()
                    .equalsIgnoreCase("Current condition")
                    || mapMetaObjects.get(metaData.getMetaParamID()).getMetaParamLabel()
                    .equalsIgnoreCase("Get Weather")) {

            }*/
            try {
                if ((fmapObject.get(metaData.getMetaParamID() + "").value != null)) {
                    hasWeatherData = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Handler handler = new Handler();
        boolean finalHasWeatherData = hasWeatherData;
        handler.postDelayed(() -> expandList(metaData, finalHasWeatherData), 1000);
    }

    private void getChildParamsList(MetaData metaData, ViewHolder finalViewHolder1, View view) {
        List<Integer> childparamList = metaData.childParamList;

        for (int childfpID : childparamList) {
            for (int i = 0; i < form_meta_list.size(); i++) {
                MetaData metaData1 = form_meta_list.get(i);
                if (metaData1.getMetaParamID() == childfpID) {
                    metaData1.isVisible = !metaData.isExpanded;
                    metaData1.isRowVisible = metaData1.isVisible;
                    formActivity.attachViewToScroller(metaData1);
                }
            }
        }

        if (!metaData.isExpanded) {
            finalViewHolder1.enableParent.setBackgroundResource(R.drawable.collapse_arrow);
            metaData.isExpanded = true;

/*            for (int childfpID : childparamList) {
                for (int i = 0; i < form_meta_list.size(); i++) {
                    MetaData metaData1 = form_meta_list.get(i);
                    if (metaData1.getMetaParamID() == childfpID) {
                        metaData1.isVisible = true;
                        String key = metaData1.getMetaParamID() + "";
                        if (!filteredmetaObjects.contains(metaData1)) {
                            //Insert new item at this position and move others to down
                            filteredmetaObjects.add(metaData1);
                        }

                        DataHolder dh = mapObject.get(metaData1.getMetaParamID() + "");//index(metaObjects)=key for mapObject
                        fmapObject.put(key, dh);
                        break;
                    }
                }
            }*/
        } else {
            finalViewHolder1.enableParent.setBackgroundResource(R.drawable.expand_arrow);
            metaData.isExpanded = false;
/*            for (int childfpID : childparamList) {
                for (int i = 0; i < form_meta_list.size(); i++) {
                    MetaData metaData1 = form_meta_list.get(i);
                    if (metaData1.getMetaParamID() == childfpID) {
                        metaData1.isVisible = false;
                        filteredmetaObjects.remove(metaData1);
                        break;
                    }
                }
            }*/
        }
//        rearrangeExpandableFields();
//        view.clearFocus();
    }

    private ArrayList<MetaData> getChildList(MetaData metaData) {

        List<Integer> childparamList = metaData.childParamList;
        ArrayList<MetaData> metaChilds = new ArrayList<>();
        metaChilds.add(metaData);//adding parent parameter also
        for (int childfpID : childparamList) {
            for (int i = 0; i < form_meta_list.size(); i++) {
                MetaData metaData1 = form_meta_list.get(i);
                if (metaData1.getMetaParamID() == childfpID) {
                    metaChilds.add(metaData1);
                }
            }
        }
        return metaChilds;
    }

    //used in case of weather only
    private void expandList(MetaData metaData, boolean hasData) {
        List<Integer> childparamList = metaData.childParamList;

        if (hasData) {
            metaData.isExpanded = true;
/*            for (int childfpID : childparamList) {
                for (int i = 0; i < form_meta_list.size(); i++) {
                    MetaData metaData1 = form_meta_list.get(i);
                    if (metaData1.getMetaParamID() == childfpID) {
                        metaData1.isVisible = true;
                        String key = metaData1.getMetaParamID() + "";
                        if (!filteredmetaObjects.contains(metaData1)) {
                            //Insert new item at this position and move others to down
                            filteredmetaObjects.add(metaData1);
                        }

                        DataHolder dh = mapObject.get(metaData1.getMetaParamID() + "");//index(metaObjects)=key for mapObject
                        fmapObject.put(key, dh);
                        break;
                    }
                }
            }*/
        } else {
            if (metaData.isExpanded) {
                metaData.isExpanded = false;
/*                for (int childfpID : childparamList) {
                    for (int i = 0; i < form_meta_list.size(); i++) {
                        MetaData metaData1 = form_meta_list.get(i);
                        if (metaData1.getMetaParamID() == childfpID) {
                            metaData1.isVisible = false;
                            filteredmetaObjects.remove(metaData1);
                            break;
                        }
                    }
                }*/
            }
        }

        for (int childfpID : childparamList) {
            for (int i = 0; i < form_meta_list.size(); i++) {
                MetaData metaData1 = form_meta_list.get(i);
                if (metaData1.getMetaParamID() == childfpID) {
                    metaData1.isVisible = metaData.isExpanded;
                    metaData1.isRowVisible = metaData1.isVisible;
                    formActivity.attachViewToScroller(metaData1);
                }
            }
        }

//        rearrangeExpandableFields();
    }

    private void showAlertHint(String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_popup_parameter_hint, null, false);
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        TextView tvHint = view.findViewById(R.id.tvParameterHint);
        tvHint.setText("Guide: " + hint);
        tvHint.setTypeface(type);
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void setTimeViewData(MetaData metaData, FBTimeView timeView) {

        String defaultValue = metaData.getDefaultValue();
        timeView.setBlankTime(defaultValue);

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        String value = tempData.value;
        blankTimeValue = value == null;

        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        timeView.setDisplayTime(value);
        Log.i(TAG, "load value=" + value);
        timeView.calculateTime();

        if (tempData.value == null) {
            if (defaultValue == null || defaultValue.isEmpty()
                    || defaultValue.equalsIgnoreCase("current")) {
                tempData.value = Util.get24hrFormatTime(timeView.getDisplayTime(), Locale.getDefault());
            }
        } else {
            tempData.value = Util.get24hrFormatTime(timeView.getDisplayTime(), Locale.getDefault());
        }
        handleDateAndTimeData(metaData, tempData.value, getCurrentSetID());
    }

    void setDateViewData(MetaData metaData, FBDateView dateView) {

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
        String defaultValue = metaData.getDefaultValue();

        if (tempData.value == null) {
            blankDateValue = true;
        }

       /* boolean isTablet=context.getResources().getBoolean(R.bool.isTablet);
        if (!isTablet){

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dateView.setLayoutParams(layoutParams);

            Log.e("noTab", "setDateViewData: is not a tablet");
            dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP,13f);
        }*/
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        dateView.setBlankDate(defaultValue);
        dateView.setDisplayDate(tempData.value);
        dateView.calculateDate();
        if (tempData.value == null) {
            if (defaultValue == null || defaultValue.equalsIgnoreCase("current") || defaultValue.isEmpty() || defaultValue.equals("")) {
                tempData.value = dateView.getDisplayDate();
            }
        } else {
            tempData.value = dateView.getDisplayDate();
        }

        handleDateAndTimeData(metaData, tempData.value, getCurrentSetID());
    }

    public void setCurrentSetDataToForm() {
        formActivity.setMapObject();
        this.mapObject = formActivity.mapObject;

        //20-Jul-17 ADD ALL ITEMS IN FILTERED WHICH ARE NOT CHILD AND PARENT,IS PARENT,IS CHILD BUT VISIBLE
        for (MetaData mData : metaObjects) {
            String key = String.valueOf(mData.getMetaParamID());
            DataHolder dh = this.mapObject.get(key);
            fmapObject.put(key, dh);
        }
    }

    public void setMandatoryFieldAlert(MetaData metaData, ViewHolder viewHolder) {
        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        if (viewHolder != null) {

            String mandatoryField = metaData.getMandatoryField();

            if (attributes != null)
                if (attributes.getMandatoryField() != 0)
                    mandatoryField = attributes.getMandatoryField() + "";

            if (mandatoryField.equalsIgnoreCase("2")
                    && (tempData.value != null && !tempData.value.isEmpty())) {
                viewHolder.lable.setTextColor(context.getResources().getColor(R.color.color_chooser_green));
                formActivity.reqFieldCount--;

                // ll.setBackgroundColor(ObjContext.getResources().getColor(R.color.required_background_red1));
            } else if (mandatoryField.equalsIgnoreCase("2")
                    && (tempData.value == null || tempData.value.isEmpty())) {
                viewHolder.lable.setTextColor(context.getResources().getColor(R.color.required_background_red1));
                formActivity.reqFieldCount++;
            }
        }
    }

    public void handleLast2Reading_And_Percentage(MetaData metaData, ViewHolder
            viewHolder, EditText numericView) {

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
        String defaultValue = metaData.getDefaultValue();

        float percent = 0;
        final int colorOrange = 0xFFF59161;
        final int colorGreen = 0xFF9CB865;

        Log.i(TAG, "In NUMERIC " + " value="
                + tempData.value + " ImgStatus="
                + tempData.imgStatus);

        setCalculatedFieldParams(metaData, numericView, viewHolder);
        percent = (float) 0.0;

        if ((tempData.dhPrevReading1 != null) && (!tempData.dhPrevReading1.equals("")) &&
                ((tempData.dhPrevReading2 != null) && (!tempData.dhPrevReading2.equals("")))) {

            String straightDifference = metaData.getStraightDifference();

            if (straightDifference != null && !TextUtils.isEmpty(straightDifference)) {
                try {
                    percent = calcDifference(Float.parseFloat(tempData.dhPrevReading1),
                            Float.parseFloat(tempData.dhPrevReading2));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Exception :" + e.getMessage());
                }

                // percent = round (percent, 1);
                percent = (float) (Math.round(percent * 100.00) / 100.00);
                viewHolder.prevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                Double percentDifference = metaData.getPercentDifference();

                if (attributes != null)
                    if (attributes.getPercentDifference() != 0)
                        percentDifference = attributes.getPercentDifference();

                if (percentDifference != 0) { //display % diff only if %diff is not null
                    //								viewHolder.prevPercent1.setText(Float.toString(percent)+"%");
                    if (percent != 0) {
                        try {
                            //not tampering percent original value
                            String newPercent = getLocaleFormattedString(String.valueOf(percent), Locale.getDefault());
                            viewHolder.prevPercent1.setText(newPercent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewHolder.prevPercent1.setText(String.valueOf(percent));
                        }
                    } else {
                        viewHolder.prevPercent1.setText(String.valueOf(percent));
                    }

                    try {
                        if (Math.abs(percent) <= Float.parseFloat(straightDifference)) {
                            viewHolder.prevPercent1.setBackgroundColor(colorGreen);
                        } else {
                            viewHolder.prevPercent1.setBackgroundColor(colorOrange);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        viewHolder.prevPercent1.setBackgroundColor(colorOrange);
                    }
                } else {
                    viewHolder.prevPercent1.setText("");
                    viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);
                }
            } else {
                try {
                    percent = calcPercent(Float.parseFloat(tempData.dhPrevReading1),
                            Float.parseFloat(tempData.dhPrevReading2));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                viewHolder.prevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                percent = round(percent, 1);

                Double percentDifference = metaData.getPercentDifference();

                if (attributes != null)
                    if (attributes.getPercentDifference() != 0)
                        percentDifference = attributes.getPercentDifference();

                if (percentDifference != 0) {
                    //display % diff only if %diff is not null
                    viewHolder.prevPercent2.setWidth(30);

                    if (percent != 0) {
                        try {
                            //not tampering percent original value
                            String newPercent = getLocaleFormattedString(String.valueOf(percent), Locale.getDefault()) + "%";
                            viewHolder.prevPercent1.setText(newPercent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewHolder.prevPercent1.setText(percent + "%");
                        }
                    } else
                        viewHolder.prevPercent1.setText(percent + "%");

                    //  viewHolder.prevPercent1.setWidth(20);
                    if (Math.abs(percent) <= percentDifference) {
                        viewHolder.prevPercent1.setBackgroundColor(colorGreen);
                    } else {
                        viewHolder.prevPercent1.setBackgroundColor(colorOrange);
                    }
                } else {
                    viewHolder.prevPercent1.setText("");
                    viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        } else {
            viewHolder.prevPercent1.setText("");
            viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);
        }

        if ((tempData.value != null) && (tempData.value.length() != 0)) {
            if ((tempData.dhPrevReading2 != null) && (!tempData.dhPrevReading2.equals(""))) {
//							Log.i("mmmm first calc"+"r1="+Float.parseFloat(value)
//									+"r2="+Float.parseFloat(tempData.dhPrevReading2));
                percent = (float) 0;

                String extField3 = metaData.getStraightDifference();

                if (extField3 != null && !TextUtils.isEmpty(extField3)) {
                    try {
                        //	percent = calcPercent(Float.parseFloat(tempData.dhPrevReading2), Float.parseFloat(value));
                        percent = calcDifference(Float.parseFloat(tempData.dhPrevReading2), Float.parseFloat(tempData.value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //							percent = round (percent, 1);
                    percent = (float) (Math.round(percent * 100.00) / 100.00);
                    viewHolder.prevPercent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                    Double percentDifference = metaData.getPercentDifference();

                    if (attributes != null)
                        if (attributes.getPercentDifference() != 0)
                            percentDifference = attributes.getPercentDifference();
                    Log.i(TAG, "percent " + percentDifference);

                    if (percentDifference != 0) {
                        //viewHolder.prevPercent2.setText(Float.toString(percent)+"%");

                        if (percent != 0) {
                            try {
                                //not tampering percent original value
                                String newPercent = getLocaleFormattedString(String.valueOf(percent), Locale.getDefault());
                                viewHolder.prevPercent2.setText(newPercent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                viewHolder.prevPercent2.setText(String.valueOf(percent));
                            }
                        } else
                            viewHolder.prevPercent2.setText(String.valueOf(percent));

                        //								if (Math.abs(percent) <= 10) {
                        try {
                            if (Math.abs(percent) <= Float.parseFloat(extField3)) {
                                viewHolder.prevPercent2.setBackgroundColor(colorGreen);
                            } else {
                                viewHolder.prevPercent2.setBackgroundColor(colorOrange);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            viewHolder.prevPercent2.setBackgroundColor(colorOrange);
                        }
                    } else {
                        viewHolder.prevPercent2.setText("");
                        viewHolder.prevPercent2.setBackgroundColor(Color.TRANSPARENT);
                    }
                } else {
                    try {
                        percent = calcPercent(Float.parseFloat(tempData.dhPrevReading2), Float.parseFloat(tempData.value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    percent = (float) (Math.round(percent * 100.00) / 100.00);
                    viewHolder.prevPercent2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    percent = round(percent, 1);

                    Double percentDifference = metaData.getPercentDifference();

                    if (attributes != null)
                        if (attributes.getPercentDifference() != 0)
                            percentDifference = attributes.getPercentDifference();

                    if (percentDifference != 0) {
                        viewHolder.prevPercent2.setWidth(30);

                        if (percent != 0) {
                            try {
                                //not tampering percent original value
                                String newPercent = getLocaleFormattedString(String.valueOf(percent),
                                        Locale.getDefault()) + "%";
                                viewHolder.prevPercent2.setText(newPercent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                viewHolder.prevPercent2.setText(percent + "%");
                            }
                        } else
                            viewHolder.prevPercent2.setText(percent + "%");

                        if (Math.abs(percent) <= percentDifference) {
                            viewHolder.prevPercent2.setBackgroundColor(colorGreen);
                        } else {
                            viewHolder.prevPercent2.setBackgroundColor(colorOrange);
                        }
                    }
                }
            } else {
                viewHolder.prevPercent2.setText("");
                viewHolder.prevPercent2.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
/*            viewHolder.prevPercent1.setText("");
            viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);*/
            viewHolder.prevPercent2.setText("");
            viewHolder.prevPercent2.setBackgroundColor(Color.TRANSPARENT);
        }

        String prevStr = getPrevReadDispString(tempData);
        Log.i(TAG, "available prevreading=" + viewHolder.prevReading.getText());

        viewHolder.prevReading.setText(prevStr);
        if (getCurrentSetID() == 1) {
            viewHolder.prevReading.setText(" "); //do not know why some value is getting set when curSetID=1
            viewHolder.prevPercent1.setText("");
            viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.prevPercent2.setText("");
            viewHolder.prevPercent2.setBackgroundColor(Color.TRANSPARENT);
        }

        setMandatoryFieldAlert(metaData, viewHolder);
    }

    public static float calcPercent(float prev, float curr) {
        float percent = 0;
        float zeroValue = 0;
        float hundredPercent = 100;

        if ((prev + curr) == zeroValue) {
            if (curr == zeroValue) {
                return zeroValue;
            } else {
                return hundredPercent;
            }
        }
        try {
            percent = (curr - prev) / (prev + curr);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "calcPercent() Error:" + e.getLocalizedMessage());
        }

        return Math.abs(percent * 200);
    }

    public static float calcDifference(float prev, float curr) {
        float percent = 0;
        try {
            percent = Math.abs(prev - curr);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "calcDifference() Error:" + e.getLocalizedMessage());
        }
        return Math.abs(percent);
    }

    public static float calcLast3Difference(float maxOfLast3, float minOfLast3) {
        float difference = 0;
        try {
            difference = Math.abs(maxOfLast3 - minOfLast3);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "calcLast3Difference() Error:" + e.getLocalizedMessage());
        }
        return Math.abs(difference);
    }

    public static float calcLast3Percentage(float maxOfLast3, float minOfLast3) {
        float percent = 0;
        try {
            percent = ((maxOfLast3 - minOfLast3) / minOfLast3) * 100;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "calcDifference() Error:" + e.getLocalizedMessage());
        }
        return Math.abs(percent);
    }

    public static float round(float d, int decimalPlace) {

        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public void setWeightDateAndTime(LinearLayout ll, MetaData metaData) {
        Double dControlWidth = metaData.getObjectWidth();
        String dLabelWidth = null;
        ArrayList<Float> arrList = new ArrayList<Float>();

        float labelWidth = 0,
                fieldWidth = 0,
                imgwidth = 0,
                notesWidth = 0,
                prevReadings = 0,
                perc1 = 0,
                perc2 = 0,
                perGap = 0;

        float fControlFieldValue = (float) (dControlWidth + 0.0);

        dLabelWidth = (dLabelWidth == null) ? "40" : dLabelWidth; //If labelWidth is null, set it to 50

        labelWidth = Float.parseFloat(dLabelWidth) * 10f;
        arrList.add(labelWidth);

        fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 50f;

        // fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 30f;

        fieldWidth = (float) ((fieldWidth * (900f - labelWidth)) / 100);

        arrList.add(fieldWidth);

        boolean enableNotes = metaData.isIsEnableParameterNotes();

        if (attributes != null)
            if (attributes.isEnable_parameter_notes())
                enableNotes = true;

        if (enableNotes || metaData.getRoutineId() == 999) {
            notesWidth = 50f;
            // notesWidth = 30f;

            arrList.add(notesWidth);
        }

        imgwidth = 60f;
        arrList.add(imgwidth);

        float remainingWidth = 900f - (labelWidth + fieldWidth + imgwidth + notesWidth);

        boolean isShowLast2 = metaData.isIsShowLast2();

        if (attributes != null)
            if (attributes.isShowLast2())
                isShowLast2 = attributes.isShowLast2();

        if (isShowLast2) {
            //prevReadings = (remainingWidth * (50f / 100f));
//          prevReadings = 120;
            arrList.add(prevReadings);
        }

        Double percentDifference = metaData.getPercentDifference();

        if (attributes != null)
            if (attributes.getPercentDifference() != 0)
                percentDifference = attributes.getPercentDifference();

        if (percentDifference != 0) {
            perc1 = perc2 = ((remainingWidth - prevReadings) * (49f / 100f));

            perGap = ((remainingWidth - prevReadings) - (2f * perc1));
//            perGap = 20;
            arrList.add(perc1);
            arrList.add(perGap);
            arrList.add(perc2);
        }

        if (ll != null) {

            ll.setGravity(Gravity.CENTER_VERTICAL);
            //  ll.setWeightSum(1000f);
            ll.setWeightSum(1000f);

            int childCount = ll.getChildCount();

            Log.i(TAG, "Control Count:" + childCount);

            for (int i = 0; i < childCount; i++) {
                float viewWidth = 0;
                try {
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(5,
                            LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                } catch (IndexOutOfBoundsException e) {
                    //09-DEC-16
                    notesWidth = 50f;
                    arrList.add(notesWidth);
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(5,
                            LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                }
            }
        }
    }

    public void setWeightWithoutLastReading(LinearLayout ll, MetaData metaData) {
        Double dControlWidth = metaData.getObjectWidth();
        // String dLabelWidth = ObjContext.getExtField1();
        String dLabelWidth = null;
/*        if (dControlWidth == 40) {
            Log.e("widdddth", "setWeight: initial" + dControlWidth);
            dControlWidth = dControlWidth + 8;
            Log.e("widdddth", "setWeight: after" + dControlWidth);
        }*/
        ArrayList<Float> arrList = new ArrayList<Float>();

        float labelWidth = 0,
                fieldWidth = 0,
                imgwidth = 0,
                notesWidth = 0,
                prevReadings = 0,
                perc1 = 0,
                perc2 = 0,
                perGap = 0;

        float fControlFieldValue = (float) (dControlWidth + 0.0);

        dLabelWidth = (dLabelWidth == null) ? "50" : dLabelWidth; //If labelWidth is null, set it to 50

        labelWidth = Float.parseFloat(dLabelWidth) * 10f;
        arrList.add(labelWidth);

        fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 50f;

        // fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 30f;

        //fieldWidth = (float) ((fieldWidth * (900f - labelWidth)) / 100);

        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        Log.i(TAG, "setWeight() isTablet:" + isTablet);
        if (isTablet) {
            fieldWidth = (float) ((fieldWidth * (1200f - labelWidth)) / 100);
        } else {
            fieldWidth = (float) ((fieldWidth * (1000f - labelWidth)) / 100);
        }
        arrList.add(fieldWidth);

        boolean enableNotes = metaData.isIsEnableParameterNotes();

        if (attributes != null)
            if (attributes.isEnable_parameter_notes())
                enableNotes = true;

        if (enableNotes || metaData.getRoutineId() == 999) {
            notesWidth = 50f;
            // notesWidth = 30f;
            arrList.add(notesWidth);
        }

        imgwidth = 60f;
        arrList.add(imgwidth);

        float remainingWidth = 1600f - (labelWidth + fieldWidth + imgwidth + notesWidth);
        //float remainingWidth = 1000f - (labelWidth + fieldWidth);

        boolean isShowLast2 = metaData.isIsShowLast2();

        if (attributes != null)
            if (attributes.isShowLast2())
                isShowLast2 = attributes.isShowLast2();

        if (isShowLast2) {
            prevReadings = (remainingWidth * (50f / 100f));
            //prevReadings = 40;
            arrList.add(prevReadings);
        }

        Double percentDifference = metaData.getPercentDifference();

        if (attributes != null)
            if (attributes.getPercentDifference() != 0)
                percentDifference = attributes.getPercentDifference();

        if (percentDifference != 0) {
            perc1 = perc2 = ((remainingWidth - prevReadings) * (49f / 100f));

            perGap = ((remainingWidth - prevReadings) - (2f * perc1));
//            perGap = 20;
            arrList.add(perc1);
            arrList.add(perGap);
            arrList.add(perc2);
        }
        if (ll != null) {

            ll.setGravity(Gravity.CENTER_VERTICAL);
            // ll.setGravity(Gravity.CENTER_VERTICAL);
            //  ll.setWeightSum(1000f);
            ll.setWeightSum(1600f);

            int childCount = ll.getChildCount();

            Log.i(TAG, "Control Count:" + childCount);
            for (int i = 0; i < childCount; i++) {
                float viewWidth = 0;
                try {
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                } catch (IndexOutOfBoundsException e) {
                    //09-DEC-16
                    notesWidth = 50f;
                    arrList.add(notesWidth);
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                }
            }
        }
    }

    public void setWeight(LinearLayout ll, MetaData metaData) {
        Double dControlWidth = metaData.getObjectWidth();
        // String dLabelWidth = ObjContext.getExtField1();
        String dLabelWidth = null;
        if (dControlWidth == 40) {
            Log.e("widdddth", "setWeight: initial" + dControlWidth);
            dControlWidth = dControlWidth + 10;
            Log.e("widdddth", "setWeight: after" + dControlWidth);
        }
        ArrayList<Float> arrList = new ArrayList<Float>();

        float labelWidth = 0,
                fieldWidth = 0,
                imgwidth = 0,
                notesWidth = 0,
                prevReadings = 0,
                perc1 = 0,
                perc2 = 0,
                perGap = 0;

        float fControlFieldValue = (float) (dControlWidth + 0.0);

        dLabelWidth = (dLabelWidth == null) ? "50" : dLabelWidth; //If labelWidth is null, set it to 50
        labelWidth = Float.parseFloat(dLabelWidth) * 10f;
        labelWidth = labelWidth + 5;
//        arrList.add(labelWidth);

        fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 50f;

        // fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 30f;

        //fieldWidth = (float) ((fieldWidth * (900f - labelWidth)) / 100);

        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        Log.i(TAG, "setWeight() isTablet:" + isTablet);
        if (isTablet) {
            fieldWidth = (float) ((fieldWidth * (1200f - labelWidth)) / 100);
        } else {
            fieldWidth = (float) ((fieldWidth * (1000f - labelWidth)) / 100);
        }
        fieldWidth = fieldWidth + 8;
        arrList.add(labelWidth + fieldWidth);

        boolean enableNotes = metaData.isIsEnableParameterNotes();

        if (attributes != null)
            if (attributes.isEnable_parameter_notes())
                enableNotes = true;

        if (enableNotes || metaData.getRoutineId() == 999) {
            notesWidth = 50f;
            // notesWidth = 30f;

            arrList.add(notesWidth);
        }

        imgwidth = 60f;
        arrList.add(imgwidth);
        Log.e("widdddth", "setWeight: after" + labelWidth + "----" + fieldWidth + "---" + imgwidth + "----" + notesWidth);
        float remainingWidth = 1400f - (labelWidth + fieldWidth + imgwidth + notesWidth);
        //float remainingWidth = 1000f - (labelWidth + fieldWidth);

        boolean isShowLast2 = metaData.isIsShowLast2();

        if (attributes != null)
            if (attributes.isShowLast2())
                isShowLast2 = attributes.isShowLast2();

        if (isShowLast2) {
            prevReadings = (remainingWidth * (50f / 100f));
            //prevReadings = 40;
            arrList.add(prevReadings);
        }

        Double percentDifference = metaData.getPercentDifference();

        if (attributes != null)
            if (attributes.getPercentDifference() != 0)
                percentDifference = attributes.getPercentDifference();

        if (percentDifference != 0) {
            perc1 = perc2 = ((remainingWidth - prevReadings) * (49f / 100f));

            perGap = ((remainingWidth - prevReadings) - (2f * perc1));
//            perGap = 20;
            arrList.add(perc1);
            arrList.add(perGap);
            arrList.add(perc2);
        }
        if (ll != null) {

            ll.setGravity(Gravity.CENTER_VERTICAL);
            //  ll.setWeightSum(1000f);
            ll.setWeightSum(1400f);

            int childCount = ll.getChildCount();

            Log.i(TAG, "Control Count:" + childCount);
            for (int i = 0; i < childCount; i++) {
                float viewWidth = 0;
                try {
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                } catch (IndexOutOfBoundsException e) {
                    //09-DEC-16
                    notesWidth = 50f;
                    arrList.add(notesWidth);
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                }
            }
        }
    }

    public void setWeightForShowLast3(LinearLayout ll, MetaData metaData, ViewHolder viewHolder) {
        if (formActivity.getCurSetID() < 3) {
            try {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                int margin = Util.dpToPx(5);
                params.setMargins(margin, margin, margin, 0);
                viewHolder.llHorizontalField.getChildAt(0).setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        Double dControlWidth = metaData.getObjectWidth();
        String dLabelWidth = null;
        if (dControlWidth == 40) {
            dControlWidth = dControlWidth + 10;
        }
        ArrayList<Float> arrList = new ArrayList<Float>();

        float labelWidth = 0,
                fieldWidth = 0,
                imgwidth = 0,
                notesWidth = 0,
                prevReadings = 0,
                perc1 = 0,
                perc2 = 0,
                perGap = 0;

        float fControlFieldValue = (float) (dControlWidth + 0.0);

        dLabelWidth = (dLabelWidth == null) ? "50" : dLabelWidth; //If labelWidth is null, set it to 50
        labelWidth = Float.parseFloat(dLabelWidth) * 10f;
        labelWidth = labelWidth + 5;
//        arrList.add(labelWidth);

        fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 50f;

        // fieldWidth = (fControlFieldValue != 0) ? fControlFieldValue : 30f;

        //fieldWidth = (float) ((fieldWidth * (900f - labelWidth)) / 100);

        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            fieldWidth = (float) ((fieldWidth * (1200f - labelWidth)) / 100);
        } else {
            fieldWidth = (float) ((fieldWidth * (1400f - labelWidth)) / 100);
        }
        fieldWidth = fieldWidth + 8;
        arrList.add(labelWidth + fieldWidth);

        boolean enableNotes = metaData.isIsEnableParameterNotes();

        if (attributes != null)
            if (attributes.isEnable_parameter_notes())
                enableNotes = true;

        if (enableNotes || metaData.getRoutineId() == 999) {
            notesWidth = 50f;
            // notesWidth = 30f;
            arrList.add(notesWidth);
        }

        imgwidth = 60f;
        arrList.add(imgwidth);
        Log.e("widdddth", "setWeight: after" + labelWidth + "----" + fieldWidth + "---" + imgwidth + "----" + notesWidth);
        float remainingWidth = 1400f - (labelWidth + fieldWidth + imgwidth + notesWidth);
        //float remainingWidth = 1000f - (labelWidth + fieldWidth);

        prevReadings = (remainingWidth * (60f / 100f));
        arrList.add(prevReadings);

        Double percentDifference = metaData.getPercentDifference();

        if (attributes != null)
            if (attributes.getPercentDifference() != 0)
                percentDifference = attributes.getPercentDifference();

        if (percentDifference != 0) {
            perc1 = perc2 = ((remainingWidth - prevReadings) * (40f / 100f));

            perGap = ((remainingWidth - prevReadings) - (2f * perc1));
//            perGap = 20;
            arrList.add(perc1);
            arrList.add(perGap);
//            arrList.add(perc2);
        }

        arrList.clear();
        fieldWidth = 0.5f * 1400;
        float blankSpaceBet = 0.01f * 1400;
        float blankSpaceBetReadAndPer = 0.05f * 1400;
        prevReadings = 0.3f * 1400;
        perc1 = (1400 - (fieldWidth + blankSpaceBet + prevReadings + blankSpaceBetReadAndPer)) - blankSpaceBet;

        arrList.add(fieldWidth);
        arrList.add(blankSpaceBet);
        arrList.add(prevReadings);
        arrList.add(blankSpaceBetReadAndPer);
        arrList.add(perc1);
        arrList.add(blankSpaceBet);

        if (ll != null) {

            ll.setGravity(Gravity.CENTER_VERTICAL);
            //  ll.setWeightSum(1000f);
            ll.setWeightSum(1400f);

            int childCount = ll.getChildCount();

            Log.i(TAG, "Control Count:" + childCount);
            for (int i = 0; i < childCount; i++) {
                float viewWidth = 0;
                try {
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                } catch (IndexOutOfBoundsException e) {
                    //09-DEC-16
                    notesWidth = 50f;
                    arrList.add(notesWidth);
                    viewWidth = arrList.get(i);
                    ll.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, viewWidth));
                    //ll.getChildAt(i).setPadding(8, 8, 8, 8);
                }
            }
        }

        if (getCurrentSetID() > 0)
            handleLast3ReadingAndPercentage(viewHolder, metaData);
    }

    private void handleLast3ReadingAndPercentage(ViewHolder viewHolder, MetaData metaData) {
        String prevRead = getPrevReadingForShowLast3(metaData);
        viewHolder.prevReading.setText(prevRead);
        float maxOfLast3 = 0.0f;
        float minOfLast3 = 0.0f;

        final int colorOrange = 0xFFF59161;
        final int colorGreen = 0xFF9CB865;

        double straightDiff = 0;
        double percentDiff = 0;

        if (attributes != null) {
            straightDiff = attributes.getStraightDifference();
            percentDiff = attributes.getPercentDifference();
        } else {
            if (metaData.getStraightDifference() != null)
                straightDiff = Double.parseDouble(metaData.getStraightDifference());
            percentDiff = metaData.getPercentDifference();
        }

        List<String> prevReadingsArray;
        if (prevRead != null && !prevRead.isEmpty()) {
            prevReadingsArray = Arrays.asList(prevRead.split("\\s*,\\s*"));
            if (!prevRead.contains("NR") && prevReadingsArray.size() > 0) {
                if (prevReadingsArray.size() == 3) {
                    maxOfLast3 = Math.max(Math.max(Float.parseFloat(prevReadingsArray.get(0)),
                            Float.parseFloat(prevReadingsArray.get(1))), Float.parseFloat(prevReadingsArray.get(2)));
                    minOfLast3 = Math.min(Math.min(Float.parseFloat(prevReadingsArray.get(0)),
                            Float.parseFloat(prevReadingsArray.get(1))), Float.parseFloat(prevReadingsArray.get(2)));
                }
            }
        }

        setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);

        if (minOfLast3 == 0 && maxOfLast3 == 0) {
            viewHolder.prevPercent1.setText("");
            viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);
            return;
        }

        if (straightDiff != 0) {
            float difference = (float) 0.0;

            try {
                difference = calcLast3Difference(maxOfLast3,
                        minOfLast3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            difference = (float) (Math.round(difference * 100.00) / 100.00);
            difference = round(difference, 2);

            viewHolder.prevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            viewHolder.prevPercent1.setText(difference + "");

            try {
                if (difference <= straightDiff) {
                    viewHolder.prevPercent1.setBackgroundColor(colorGreen);
                } else {
                    viewHolder.prevPercent1.setBackgroundColor(colorOrange);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (percentDiff != 0) {

            float percent = 0.0f;
            try {
                percent = calcLast3Percentage(maxOfLast3,
                        minOfLast3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            percent = (float) (Math.round(percent * 100.00) / 100.00);
            percent = round(percent, 2);

            viewHolder.prevPercent1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            viewHolder.prevPercent1.setText(percent + "%");

            try {
                if (percent <= percentDiff) {
                    viewHolder.prevPercent1.setBackgroundColor(colorGreen);
                } else {
                    viewHolder.prevPercent1.setBackgroundColor(colorOrange);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.prevPercent1.setText("");
            viewHolder.prevPercent1.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public String getPrevReadingForShowLast3(MetaData metaData) {
        StringBuilder prev3Readings = new StringBuilder();

        if (getCurrentSetID() >= 3) {
            FieldDataSource fieldDataSource = new FieldDataSource(context);

            int count = 3;
            for (int setId = getCurrentSetID(); setId >= 0; setId--) {

                String prevValue = fieldDataSource.getPreviousReading(Integer.parseInt(eventID), setId,
                        locationID, Integer.parseInt(siteID), formActivity.getCurrentAppID(),
                        metaData.getMetaParamID(), "");

                if (count == 3) {
                    if (prevValue != null && !prevValue.isEmpty())
                        prev3Readings.append(prevValue);
                    else
                        prev3Readings.append("NR");
                } else if (count == 2) {
                    String value2 = prev3Readings.toString();
                    prev3Readings.setLength(0);
                    if (prevValue != null && !prevValue.isEmpty()) {
                        prev3Readings.append(prevValue);
                        prev3Readings.append(", ");
                        prev3Readings.append(value2);
                    } else {
                        prev3Readings.append("NR");
                        prev3Readings.append(", ");
                        prev3Readings.append(value2);
                    }
                } else if (count == 1) {
                    String value2 = prev3Readings.toString();
                    prev3Readings.setLength(0);
                    if (prevValue != null && !prevValue.isEmpty()) {
                        prev3Readings.append(prevValue);
                        prev3Readings.append(", ");
                        prev3Readings.append(value2);
                    } else {
                        prev3Readings.append("NR");
                        prev3Readings.append(", ");
                        prev3Readings.append(value2);
                    }
                }
                count--;

                if (count == 0)
                    break;
            }
        }

        return prev3Readings.toString();
    }

    String getPrevReadDispString(DataHolder tempData) {
        String prevReadDisp = "";
        if (tempData.dhPrevReading1 != null) {
            Log.i(TAG, "1l=" + tempData.dhPrevReading1.length());

            if (tempData.dhPrevReading1.length() != 0) {
                String prevReading1
                        = getLocaleFormattedString(tempData.dhPrevReading1, Locale.getDefault());

                prevReadDisp += prevReading1;
            } else {
                prevReadDisp += "NR";
            }
        } else {
            Log.i(TAG, "1l is null");
        }

        if (tempData.dhPrevReading2 != null) {
            Log.i(TAG, ",2l=" + tempData.dhPrevReading2.length());
            if (prevReadDisp.length() != 0) { // do not add , if prevreding1 is ""
                prevReadDisp += ", ";
            }

            if (tempData.dhPrevReading2.length() != 0) {

                String prevReading2
                        = getLocaleFormattedString(tempData.dhPrevReading2, Locale.getDefault());

                prevReadDisp += prevReading2;
            } else {
                prevReadDisp += "NR";
            }
        } else {
            Log.i(TAG, "2l is null");
        }

        return prevReadDisp;
    }

    @SuppressLint("NewApi")
    public TextView getLabelView(String value) {

        ColorDrawable colorDraw = new ColorDrawable();
        colorDraw.setColor(Color.TRANSPARENT);

        ViewGroup.LayoutParams lp;
        // lp = new LayoutParams(dpTextWidth - 60/*40*/, dpTextHeight);
        lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT/*40*/, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView text = new TextView(context);
        text.setLayoutParams(lp);
        text.setHint(value);
        text.setTextSize(16);
        text.setBackground(colorDraw);
        text.setInputType(0);
        text.setText(value);
        text.setGravity(Gravity.CENTER);
        text.setTextColor(formColor);

        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        text.setLines(1);
        text.setSelected(true);
        text.setMarqueeRepeatLimit(-1);
        return text;
    }


    public TextView getPercentView(String value) {

        ColorDrawable colorDraw = new ColorDrawable();
        colorDraw.setColor(Color.TRANSPARENT);

        TextView percentView = new TextView(context);

//        percentView.setHint(value);
        percentView.setTextSize(16);
        percentView.setInputType(0);
        LinearLayout.LayoutParams lp
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        // lp.setMargins(20, 0, 0, 0);
        percentView.setLayoutParams(lp);
        percentView.setText(value);
        percentView.setTextColor(Color.BLACK);
        percentView.setGravity(Gravity.CENTER);
        percentView.setSingleLine(true);
        percentView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        percentView.setLines(1);
        percentView.setSelected(true);

        return percentView;
    }

    public TextView getBlank(int width) {

        TextView textView = new TextView(context);
        textView.setHint("");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp);

        return textView;
    }


    private FlexboxLayout getAutoCompleteView(MetaData metaData, String displayValues, String
            inputype, int lovID, int parentLovID,
                                              HashMap<String, String> nameVPair) {

        FlexboxLayout autocomplete_container = new FlexboxLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        autocomplete_container.setLayoutParams(lp);

        autocomplete_container.setAlignContent(AlignContent.FLEX_START);
        autocomplete_container.setAlignItems(AlignItems.FLEX_START);
        autocomplete_container.setFlexWrap(FlexWrap.WRAP);
        autocomplete_container.setShowDivider(FlexboxLayout.SHOW_DIVIDER_BEGINNING | FlexboxLayout.SHOW_DIVIDER_MIDDLE);
        autocomplete_container.setDividerDrawable(context.getResources().getDrawable(R.drawable.flex_divider));

        autocomplete_container.setTag(Integer.toString(metaData.getMetaParamID()));
        autocomplete_container.setPadding(20, 0, 10, 10);

        TextView emptyValueView = getEmptyValueTextView();

        if (inputype.equalsIgnoreCase("AUTOCOMPLETE")) {
            emptyValueView.setText(formActivity.getString(R.string.tap_here_to_select_value));
        } else {
            emptyValueView.setText(formActivity.getString(R.string.tap_here_to_select_multiple_values));
        }

        autocomplete_container.removeAllViews();

        if (inputype.equalsIgnoreCase("MULTIMETHODS")) {
            updateMultiMethodAutoCompleteView(metaData, autocomplete_container, displayValues, inputype);
        } else {
            updateAutoCompleteView(metaData, autocomplete_container, displayValues, inputype,
                    lovID, parentLovID, nameVPair);
        }

        return autocomplete_container;
    }

    public TextView getEmptyValueTextView() {
        TextView emptyValueView = new TextView(context);
        LinearLayout.LayoutParams tvLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int padding = Util.dpToPx(5);
        tvLayout.setMargins(0, padding, 0, 0);
        emptyValueView.setTextColor(formColor);
//        emptyValueView.setPadding(padding, padding, padding, padding);
        emptyValueView.setTextSize(16f);
        emptyValueView.setGravity(Gravity.CENTER);
//        emptyValueView.setBackgroundResource(R.drawable.data_entry_control_bg);
        return emptyValueView;
    }

    public void updateAutoCompleteView(final MetaData metaData, FlexboxLayout parentView,
                                       String displayvalues,
                                       final String inputype, final int lovID, final int parentLovID, HashMap<
            String, String> nameVPair) {

        String value = calculateSetExpression(metaData);

        if (value != null && !value.isEmpty())
            displayvalues = value;

        mDisplayValueForSpecies = displayvalues;
        mInputTypeSpecies = inputype;

        TextView emptyValueView = getEmptyValueTextView();

        final String pos = metaData.getMetaParamID() + "";
        DataHolder tempdata = fmapObject.get(pos);
        try {
            tempdata.setLovID(lovID);
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        if (inputype.equalsIgnoreCase("AUTOCOMPLETE")) {
            emptyValueView.setText(formActivity.getString(R.string.tap_here_to_select_value));
        } else {
            emptyValueView.setText(formActivity.getString(R.string.tap_here_to_select_multiple_values));
        }

        emptyValueView.setTextColor(formColor);

        String finalDisplayvalues = displayvalues;
        emptyValueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
//                if (inputype.equalsIgnoreCase("AUTOSETGENERATOR")) {
//                    intent.putExtra("INPUT_TYPE", "MULTIAUTOCOMPLETE");
//                } else {
                intent.putExtra("INPUT_TYPE", inputype);
//
//                }
                intent.putExtra("LOV_ID", lovID);
                intent.putExtra("PARENT_LOV_ID", parentLovID);
                intent.putExtra("SELECTED_VALUES", finalDisplayvalues);
                intent.putExtra("POSITION", pos);
                intent.putExtra("SET_ID", getCurrentSetID());

                formActivity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        parentView.removeAllViews();

        if (nameVPair != null) {
            //11-Jul-17 MULTIAUTOCOMPLTE

            String displaykeys = AutoCompleteHandler.getCharSeperatedKeys(nameVPair, displayvalues, "\\|");//displayValue conatins comma

            if (displaykeys != null && !displaykeys.isEmpty()) {
                if (displaykeys.contains(";null")) {
                    displaykeys = displaykeys.replace(";null", "");
                } else if (displaykeys.contains("null")) {
                    displaykeys = displaykeys.replace("null", "");
                }

                if (displaykeys.contains(";")) {//Multiple values selected MULTI-AUTOCOMPLETE

                    String[] selected_keys = displaykeys.split(";");
                    if (selected_keys.length > 0) {
                        for (String label : selected_keys) {
                            ChipView chipview = new ChipView(context);

                            updateChild(pos, label);
                            update_NavigateTo_formID(pos, label);

                            chipview.setDeletable(false);
                            chipview.setLabel(label);

                            chipview.setOnChipClicked(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
//
//                                    if (inputype.equalsIgnoreCase("AUTOSETGENERATOR")) {
//                                        intent.putExtra("INPUT_TYPE", "MULTIAUTOCOMPLETE");
//                                    } else {
                                    intent.putExtra("INPUT_TYPE", inputype);
//
//                                    }
                                    intent.putExtra("LOV_ID", lovID);
                                    intent.putExtra("PARENT_LOV_ID", parentLovID);
                                    intent.putExtra("SELECTED_VALUES", finalDisplayvalues);
                                    intent.putExtra("POSITION", pos);
                                    intent.putExtra("SET_ID", getCurrentSetID());

                                    formActivity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                                }
                            });

                            parentView.addView(chipview);
                        }
//                    autocomplete_container.invalidateView();
                    }
                } else {//Single value selected
                    ChipView chipview = new ChipView(context);
                    chipview.setDeletable(false);
                    chipview.setLabel(displaykeys);

                    updateChild(pos, displaykeys);
                    update_NavigateTo_formID(pos, displaykeys);

                    chipview.setOnChipClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
//                            if (inputype.equalsIgnoreCase("AUTOSETGENERATOR")) {
//                                intent.putExtra("INPUT_TYPE", "MULTIAUTOCOMPLETE");
//                            } else {
                            intent.putExtra("INPUT_TYPE", inputype);

                            intent.putExtra("LOV_ID", lovID);
                            intent.putExtra("PARENT_LOV_ID", parentLovID);
                            intent.putExtra("SELECTED_VALUES", finalDisplayvalues);
                            intent.putExtra("POSITION", pos);
                            intent.putExtra("SET_ID", getCurrentSetID());

                            formActivity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                        }
                    });

                    parentView.addView(chipview);
                }
            } else {
                //12-Sep-17 BUG FIX: PARENT-CHILD NAVIGATION FORM NOT REFRESHING
                updateChild(pos, displayvalues);
                update_NavigateTo_formID(pos, displayvalues);

                parentView.addView(emptyValueView);
            }

            if (metaData.getMetaInputType().equalsIgnoreCase("AUTOSETGENERATOR")
                    && AUTO_GENERATE) {

                String fieldoperands_expression = metaData.getFieldParameterOperands();

                if (attributes != null)
                    if (attributes.getField_parameter_operands() != null
                            && !attributes.getField_parameter_operands().isEmpty())
                        fieldoperands_expression = attributes.getField_parameter_operands();

                if (fieldoperands_expression != null && !fieldoperands_expression.isEmpty()
                        && fieldoperands_expression.contains("COPY")) {
                    int mobAppID = Integer.parseInt(fieldoperands_expression
                            .substring(fieldoperands_expression.indexOf("{") + 1,
                                    fieldoperands_expression.lastIndexOf("}")));
                    Log.i(TAG, "MobileAppID from COPY Expression:" + mobAppID);
                    metaData.setFormID(mobAppID);
                }
                String sel_values = displayvalues;
                manage_Auto_Generate_Set(metaData, eventID, locationID, siteID,
                        metaData.getFormID(), metaData.getMetaParamID(), sel_values);
            }
        } else {
            //11-Jul-17 AUTOCOMPLETE

            if (displayvalues != null && !displayvalues.isEmpty()) {

                //12-Jul-17
                LovDataSource lovDS = new LovDataSource(context);
                String key = lovDS.getKeyForLovValue(lovID, displayvalues, siteID + "");

                if (key == null || key.isEmpty()) {
                    key = displayvalues;
                }

                updateChild(pos, key);
                update_NavigateTo_formID(pos, key);

                ChipView chipview = new ChipView(context);
                chipview.setDeletable(false);
                chipview.setLabel(key);

                chipview.setOnChipClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
//                        if (inputype.equalsIgnoreCase("AUTOSETGENERATOR")) {
//                            intent.putExtra("INPUT_TYPE", "MULTIAUTOCOMPLETE");
//                        } else {
                        intent.putExtra("INPUT_TYPE", inputype);
//
//                        }
                        intent.putExtra("LOV_ID", lovID);
                        intent.putExtra("PARENT_LOV_ID", parentLovID);
                        intent.putExtra("SELECTED_VALUES", finalDisplayvalues);
                        intent.putExtra("POSITION", pos);
                        intent.putExtra("SET_ID", getCurrentSetID());

                        formActivity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    }
                });
                parentView.addView(chipview);
            } else {
                //12-Sep-17 BUG FIX: PARENT-CHILD NAVIGATION FORM NOT REFRESHING
                updateChild(pos, displayvalues);
                update_NavigateTo_formID(pos, displayvalues);
                parentView.addView(emptyValueView);
            }
        }

        handleTextData(metaData, displayvalues, getCurrentSetID());
        checkWarningValueForViolation(metaData, displayvalues);
        //commented for now to avoid loops on 26 Oct, 2021
        //updateNumericFieldsWithSetExpr();
    }

    private void updateNumericFieldsWithSetExpr() {
        for (Map.Entry<String, MetaData> entry : mapSetExprMetaObjects.entrySet()) {
            MetaData metaData = entry.getValue();
            String expression = metaData.getFieldParameterOperands();
            if (expression != null && !expression.isEmpty() && expression.contains("!!set!!")) {
                if (metaData.getForm_field_row() != null) {
                    FormMaster.ViewHolder viewHolder = (FormMaster.ViewHolder) metaData.getForm_field_row().getTag();
                    if (metaData.getMetaInputType().contains("NUMERIC"))
                        setCalculatedFieldParams(metaData, viewHolder.numericView, viewHolder);

                    //updating bottle data in case and methods are added
                    if (metaData.getMetaInputType().contains("CHECKBOX")
                            || metaData.getMetaInputType().contains("LABEL")) {
                        updateBottleData(metaData);
                    }
                }
            }
        }
    }

    public void addChipsToView(FlexboxLayout
                                       flexView, String displayValues) {
        if (displayValues != null) {
            if (!displayValues.trim().isEmpty()) {
                String[] splitArray = displayValues.split("\\|");
                for (String signName : splitArray) {
                    ChipView chipview = new ChipView(context);
                    chipview.setDeletable(false);
                    chipview.setLabel(signName);
                    flexView.addView(chipview);
                }
            }
        }
    }

    public void updateMultiMethodAutoCompleteView(final MetaData metaData, FlexboxLayout
            parentView, String displayvalues, final String inputype) {

        TextView emptyValueView = getEmptyValueTextView();
        emptyValueView.setText(formActivity.getString(R.string.tap_here_to_select_multiple_values));
        emptyValueView.setTextColor(formColor);
        parentView.removeAllViews();

        final String fpID = metaData.getMetaParamID() + "";

        DataHolder tempData = fmapObject.get(fpID);

        if (formActivity.currCocID != null && !formActivity.currCocID.isEmpty()) {

            List<SCocDetails> sCocDetailsList;

            // if (tempData.value == null) {
            CocDetailDataSource detailDataSource = new CocDetailDataSource(context);
            sCocDetailsList = detailDataSource.getDefaultMethodfromcocDetail(formActivity.currCocID,
                    locationID);
            Log.i(TAG, "Current CoC ID:" + formActivity.currCocID);

            if (sCocDetailsList != null && sCocDetailsList.size() > 0) {
                tempData.value = "";
                for (int i = 0; i < sCocDetailsList.size(); i++) {
                    String methodName = sCocDetailsList.get(i).getMethod();

                    if (sCocDetailsList.size() - 1 == i) {
                        tempData.value = tempData.value + methodName;
                    } else {
                        tempData.value = tempData.value + methodName + "|";
                    }
                }
            }
            //}

            String expression = getExpressionFromMetaOrAttribute(metaData);

            List<CoCBottles> cocbottles = new ArrayList<>();
            StringBuilder bottle = new StringBuilder();
            if (expression != null && expression.contains("COPY1")) {
                //COPY1(|1196|#1#|2530|)
                int bottleFpID = Integer.parseInt(expression.substring(expression.lastIndexOf("#")
                        + 2, expression.lastIndexOf("|")));

                if (sCocDetailsList != null && sCocDetailsList.size() > 0) {
                    StringBuilder methodidList = new StringBuilder();

                    for (int i = 0; i < sCocDetailsList.size(); i++) {
                        if (i == sCocDetailsList.size() - 1) {
                            methodidList.append(sCocDetailsList.get(i).getMethodId());
                        } else {
                            methodidList.append(sCocDetailsList.get(i).getMethodId()).append(",");
                        }
                    }

                    MethodDataSource methodDataSource1 = new MethodDataSource(context);
                    List<CoCBottles> allbottlesList = new ArrayList<>();
                    allbottlesList = methodDataSource1.getBottles(methodidList.toString());
                    cocbottles.addAll(allbottlesList);

                    if (cocbottles.size() > 0) {

                        for (int i = 0; i < cocbottles.size(); i++) {
                            String bottlename = cocbottles.get(i).getBottleName();

                            if (i == cocbottles.size() - 1) {
                                bottle.append(bottlename);
                            } else {
                                bottle.append(bottlename).append("|");
                            }
                        }
                    }
                }

                FieldDataSource fieldDataSource = new FieldDataSource(formActivity);
                fieldDataSource.updateCheckOptions(eventID,
                        formActivity.getCurSetID(), locationID, siteID,
                        formActivity.getCurrentAppID(), bottleFpID, bottle.toString());

                Log.i(TAG, "BottlesList:" + bottle);
                handleBottleData(bottleFpID, bottle.toString());
            }
        }

        displayvalues = tempData.value;

        if (displayvalues != null && !displayvalues.trim().isEmpty() && !displayvalues.equalsIgnoreCase("null")) {

            if (displayvalues.contains("|null")) {
                displayvalues = displayvalues.replace("|null", "");
            } else if (displayvalues.contains("null")) {
                displayvalues = displayvalues.replace("null", "");
            }

            if (displayvalues.contains("|")) {//Multiple values selected MULTI-AUTOCOMPLETE
                String[] selected_keys = displayvalues.split("\\|");
                if (selected_keys.length > 0) {
                    for (String label : selected_keys) {
                        ChipView chipview = new ChipView(context);
                        chipview.setDeletable(false);
                        chipview.setLabel(label);

                        final String finalDisplayvalues = displayvalues;
                        chipview.setOnChipClicked(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
                                intent.putExtra("INPUT_TYPE", inputype);
                                intent.putExtra("LOV_ID", 0);
                                intent.putExtra("PARENT_LOV_ID", 0);
                                intent.putExtra("SELECTED_VALUES", finalDisplayvalues);
                                intent.putExtra("POSITION", fpID);

                                formActivity.startActivityForResult(intent, formActivity.AUTOCOMPLETE_REQUEST_CODE);
                            }
                        });
                        if (!label.trim().isEmpty())
                            parentView.addView(chipview);
                    }
//                    autocomplete_container.invalidateView();
                }
            } else {//Single value selected
                ChipView chipview = new ChipView(context);
                chipview.setDeletable(false);
                chipview.setLabel(displayvalues);
                final String finalDisplayvalues = displayvalues;

                chipview.setOnChipClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
                        intent.putExtra("INPUT_TYPE", inputype);
                        intent.putExtra("LOV_ID", 0);
                        intent.putExtra("PARENT_LOV_ID", 0);
                        intent.putExtra("SELECTED_VALUES", finalDisplayvalues);
                        intent.putExtra("POSITION", fpID);

                        formActivity.startActivityForResult(intent, formActivity.AUTOCOMPLETE_REQUEST_CODE);
                    }
                });
                if (!displayvalues.trim().isEmpty())
                    parentView.addView(chipview);
            }
        } else {
            parentView.addView(emptyValueView);
        }

        final String finalDisplayvalues1 = displayvalues;
        emptyValueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AutoCompleteHandlerActivity.class);
                intent.putExtra("INPUT_TYPE", inputype);
                intent.putExtra("LOV_ID", 0);
                intent.putExtra("PARENT_LOV_ID", 0);
                intent.putExtra("SELECTED_VALUES", finalDisplayvalues1);
                intent.putExtra("POSITION", fpID);

                formActivity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        handleTextData(metaData, displayvalues, getCurrentSetID());
        checkWarningValueForViolation(metaData, displayvalues);
        //adding here at last line coz below method takes method from dFieldData so whatever
        //methods are stored above will get fetched
        //Note: commented on 13 August, 21 as for now commented it to check the form loading performance
        //by commenting you wont be able to update bottle data if analysis is added in case of !!set!! expression

        for (Map.Entry<String, MetaData> entry : mapSetExprMetaObjects.entrySet()) {
            updateBottleData(entry.getValue());
        }
    }

    public void manageAutoMethods(MetaData metaData, String cocID, String locationID, String
            displayValue) {
        if (AUTO_METHODS_LAST_SELECTED_VALUES != null) {
            if (displayValue != null) {
                /**
                 *Delete method from cm_COC_details
                 */
                String splitby = "\\|";
                String[] old_selectedvalues = AUTO_METHODS_LAST_SELECTED_VALUES.split(splitby);
                String[] new_selectedvalues = displayValue.split(splitby);
                Set<String> old_values = new HashSet<>(Arrays.asList(old_selectedvalues));
                Set<String> new_values = new HashSet<>(Arrays.asList(new_selectedvalues));

                Set<String> delete_items = new HashSet<>(old_values);
                delete_items.removeAll(new_values);

                Log.i(TAG, "REMOVE method for values :" + delete_items);
                CocDetailDataSource detailsDS = new CocDetailDataSource(context);

                //23-03-2018 SET DELETE_FLAG FOR DELETED COC
                for (Iterator<String> it = delete_items.iterator(); it.hasNext(); ) {
                    String delete_coc_method_value = it.next();
                    Log.i(TAG, "Delete set for value:" + delete_coc_method_value);

                    int methodID = detailsDS.getMethodIDForMethods(delete_coc_method_value);
                    detailsDS.deleteCocMethods(cocID, locationID, methodID + "");
                }

                detailsDS = new CocDetailDataSource(context);

                handleInsertUpdateCOCMethods(metaData, new_values, cocID);

                String[] sampledetails = detailsDS.getSampleDetailsForCOCID(cocID, locationID);
                if (sampledetails != null) {
                    String sampleID = sampledetails[0];
                    String sampleDate = sampledetails[1];
                    String sampleTime = sampledetails[2];
                    if (sampleID != null && !sampleID.isEmpty()) {
                        detailsDS.updateSampleID(sampleDate, sampleTime, sampleID, cocID, locationID, userID + "");
                    }
                }
            } else {
                /**
                 * Delete all methods for that COC_ID And LOCATION_ID if null
                 */
                CocDetailDataSource detailsDS = new CocDetailDataSource(context);
                detailsDS.deleteAllCocMethodsForCoCAndLocationID(cocID, locationID);
            }
        } else {
            if (displayValue != null) {
                /**
                 *Delete method from cm_COC_details
                 */
                String splitby = "\\|";

                String[] new_selectedvalues = displayValue.split(splitby);
                Set<String> new_values = new HashSet<>(Arrays.asList(new_selectedvalues));

                CocDetailDataSource detailsDS = new CocDetailDataSource(context);

                handleInsertUpdateCOCMethods(metaData, new_values, cocID);

                String[] sampledetails = detailsDS.getSampleDetailsForCOCID(cocID, locationID);
                if (sampledetails != null) {
                    String sampleID = sampledetails[0];
                    String sampleDate = sampledetails[1];
                    String sampleTime = sampledetails[2];
                    if (sampleID != null && !sampleID.isEmpty()) {
                        detailsDS.updateSampleID(sampleDate, sampleTime, sampleID, cocID, locationID, userID + "");
                    }
                }
            }
        }

        ViewHolder vh = (ViewHolder) metaData.getForm_field_row().getTag();
        updateMultiMethodAutoCompleteView(metaData, vh.new_actv, displayValue, metaData.getMetaInputType());
    }

    //created method to sample4 and non sample4 - 12 May, 21
    private void handleInsertUpdateCOCMethods(MetaData metaData, Set<String> new_values, String cocID) {
        CocDetailDataSource detailsDS = new CocDetailDataSource(context);

        //23-03-2018 ADD OR UPDATE NEW METHODS
        for (String coc_method_value : new_values) {

            Log.i(TAG, "Delete set for value:" + coc_method_value);

            int methodID = detailsDS.getMethodIDForMethods(coc_method_value);
            boolean isMethodAlreadyPresent = false;
            if (methodID > 0) {
                isMethodAlreadyPresent = detailsDS.isCocMethodPresentAlready(cocID, locationID, methodID + "");
                if (isMethodAlreadyPresent) {
                    //23-03-2018 UPDATE
                    detailsDS.updateDeleteFlag(cocID, locationID, methodID + "", userID + "");
                } else {
                    //23-03-2018 INSERT1

                    SCocDetails details = new SCocDetails();

                    details.setCreationDate(System.currentTimeMillis());
                    details.setCreatedBy(Integer.valueOf(userID));
                    details.setMethodId(methodID);
                    details.setMethod(coc_method_value);
                    details.setDeleteFlag(0);
                    details.setCocFlag(1);
                    details.setDupFlag(0);
                    details.setCocId(Integer.parseInt(cocID));
                    details.setCocDetailsId(System.currentTimeMillis() + "");
                    details.setLocationId(Long.parseLong(locationID));
                    details.setStatus("PLANNED");
                    details.setServerCreationDate(0L);

                    detailsDS.insertNewCoCdetail(details);
                }
            }
        }

        if (formActivity.currCocID != null
                && !formActivity.currCocID.equalsIgnoreCase("0") && hasSample4Operand) {

            String sampleDate = "";
            String sampleTime = "";
            String sampleId = "";

            CocDetailDataSource cocDetailDataSource = new CocDetailDataSource(formActivity);
            ArrayList<SCocDetails> cocMethodsSampleId
                    = cocDetailDataSource.getCOCMethodsForLocation(formActivity.currCocID + "",
                    locationID, false);

            if (cocMethodsSampleId.size() > 0) {
                SCocDetails method = cocMethodsSampleId.get(0);
                sampleDate = method.getSampleDate();
                sampleTime = method.getSampleTime();
                sampleId = method.getSampleId();
            }

            for (SCocDetails method : cocMethodsSampleId) {
                if (method.getDeleteFlag() == 0)
                    cocDetailDataSource.updateSampleID(sampleDate, sampleTime, sampleId, formActivity.currCocID,
                            locationID, userID + "", method.getMethodId() + "", 0);
            }

            //handling duplicate sampleid methods
            String dupSampleDate = "";
            String dupSampleTime = "";
            String dupSampleId = "";

            ArrayList<SCocDetails> cocMethodsDupSampleId
                    = cocDetailDataSource.getCOCMethodsForLocation(formActivity.currCocID + "",
                    locationID, true);

            if (cocMethodsDupSampleId.size() > 0) {
                SCocDetails method = cocMethodsDupSampleId.get(0);
                dupSampleDate = method.getSampleDate();
                dupSampleTime = method.getSampleTime();
                dupSampleId = method.getSampleId();
            }

            if (dupSampleDate == null || dupSampleDate.isEmpty()) {
                dupSampleDate = sampleDate;
            }

            if (dupSampleTime == null || dupSampleTime.isEmpty()) {
                dupSampleTime = sampleTime;
            }

            for (SCocDetails method : cocMethodsSampleId) {
                if (method.getDeleteFlag() == 0) {
                    boolean isMethodPresent
                            = cocDetailDataSource.isCocMethodPresentAlready(formActivity.currCocID,
                            locationID, method.getMethodId() + "", 1);

                    if (isMethodPresent)
                        cocDetailDataSource.updateSampleID(sampleDate, sampleTime, sampleId, formActivity.currCocID,
                                locationID, userID + "", method.getMethodId() + "", 1);
                    else {
                        SCocDetails cocMethod = new SCocDetails();
                        cocMethod.setSampleDate(dupSampleDate);
                        cocMethod.setSampleTime(dupSampleTime);
                        cocMethod.setSampleId(dupSampleId);
                        cocMethod.setCreationDate(System.currentTimeMillis());
                        cocMethod.setCreatedBy(Integer.valueOf(userID));
                        cocMethod.setModifiedBy(Integer.valueOf(userID));
                        cocMethod.setDeleteFlag(0);
                        cocMethod.setMethodId(method.getMethodId());
                        cocMethod.setMethod(method.getMethod());
                        cocMethod.setDupFlag(1);
                        cocMethod.setStatus("COMPLETED");
                        cocMethod.setCocFlag(1);
                        cocMethod.setServerCreationDate(-1L);
                        cocMethod.setLocationId(Long.parseLong(locationID));
                        cocMethod.setCocId(Integer.valueOf(formActivity.currCocID));
                        cocMethod.setCocDetailsId(System.currentTimeMillis() + "");

                        cocDetailDataSource.insertNewCoCdetail(cocMethod);
                    }
                }
            }
        }
    }

    public void handleBottleData(int fpID, String value) {

        MetaData bottleMetaData = mapMetaObjects.get(fpID);
        DataHolder temp = fmapObject.get(fpID + "");

        if (temp != null) {
            temp.value = value;
            fmapObject.put("" + fpID, temp);
            mapObject.put("" + fpID, temp);
        }

        if (bottleMetaData.getForm_field_row() != null) {
            ViewHolder vh = (ViewHolder) bottleMetaData.getForm_field_row().getTag();
//                    updateLabelViewControl(bottleMetaData, value, vh.lableview);
            updateCheckboxValues(vh.llCocBottlesCheckOptions, bottleMetaData);
        }

        handleTextData(bottleMetaData, value, getCurrentSetID());
    }

    private void manage_Auto_Generate_Set(MetaData metaData, String eventID, String
            locID, String siteID, int formID, int fieldParamID, String selectedValue) {
        String[] seperatedvalues = null;
        FieldDataSource fd = new FieldDataSource(context);

        if (selectedValue != null && !selectedValue.isEmpty()) {
            if (selectedValue.contains("|")) {
                seperatedvalues = selectedValue.split("\\|");
            } else {
                seperatedvalues = new String[1];
                seperatedvalues[0] = selectedValue;
            }

            //22-02-2018 ADD AUTO SET
            for (String setvalue : seperatedvalues) {
                if (!fd.isSetGeneratedAlready(siteID, eventID, locID, formID, fieldParamID, setvalue)) {
                    int maxset = fd.getNextSetID_MobileApp(locID, eventID, formID + "");

                    List<FieldData> bfieldData = getBlankFieldData(context, metaData, maxset);

                    bfieldData.get(0).setStringValue(setvalue);
                    bfieldData.get(0).setMobileAppID(formID);

                    String ext2 = fd.getExt2ForMobileApp(metaData.getCurrentFormID(),
                            Integer.parseInt(eventID), Integer.parseInt(siteID), locationID,
                            getCurrentSetID());
                    String ext3 = fd.getExt3ForMobileApp(metaData.getCurrentFormID(),
                            Integer.parseInt(eventID), Integer.parseInt(siteID), locationID,
                            getCurrentSetID());

                    bfieldData.get(0).setExtField2(ext2);
                    bfieldData.get(0).setExtField3(ext3);

                    fd.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID);
                    long creationDate = System.currentTimeMillis();

                    String oldcreationdate = fd.getCreationDateForMobileApp(metaData.getFormID(),
                            Integer.parseInt(eventID), Integer.parseInt(siteID), locationID, Integer.parseInt(userID), maxset);

                    if (oldcreationdate != null) {
                        fd.updateCreationDate(Integer.parseInt(eventID), maxset,
                                locationID, Integer.parseInt(siteID), metaData.getFormID(), Long.parseLong(oldcreationdate));
                    } else {
                        fd.updateCreationDate(Integer.parseInt(eventID), maxset,
                                locationID, Integer.parseInt(siteID), metaData.getFormID(), creationDate);
                    }

                    fd.updateMeasurementTime(Integer.parseInt(eventID), maxset,
                            locationID, Integer.parseInt(siteID), metaData.getFormID(),
                            creationDate);
                    // fd.addAutoSet(siteID, eventID, locID, formID, fieldParamID, setvalue);
                }
            }
        }

        //21-02-2018 IF NULL THEN IT WILL DELETE FORM FOR THAT SITE, EVENT,LOCATION OR
        // ALL UNMATCHED SET FOR THAT EVENT,LOCATION AND FORM

        if (AUTO_SET_LAST_SELECTED_VALUES != null) {
            if (seperatedvalues != null) {
                /**
                 *Delete set from d_field_data
                 */
                String splitby = "\\|";
                String[] old_selectedvalues = AUTO_SET_LAST_SELECTED_VALUES.split(splitby);
                Set<String> old_values = new HashSet<>(Arrays.asList(old_selectedvalues));
                Set<String> new_values = new HashSet<>(Arrays.asList(seperatedvalues));

                Set<String> delete_items = new HashSet<>(old_values);
                delete_items.removeAll(new_values);
                delete_items.add("");
                Log.i(TAG, "REMOVE set for values :" + delete_items);

                for (String delete_set_value : delete_items) {
                    Log.i(TAG, "Delete set for value:" + delete_set_value);

                    int deletset = fd.getSetForAutoSetValue(locID, eventID, formID + "", delete_set_value, fieldParamID);

                    if (deletset > 0) {
                        int ret = fd.deleteset(locID, Integer.parseInt(eventID), formID, deletset, Integer.parseInt(siteID));
                        Log.i(TAG, "deleteset :" + ret);
                        fd.updateset(locID, Integer.parseInt(eventID), formID, Integer.parseInt(siteID), deletset);

                        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(context);
                        attachmentDataSource.deleteAttachmentset(locID, Integer.parseInt(eventID), formID, deletset, Integer.parseInt(siteID));
                        attachmentDataSource.updateAttachmentset(locID, Integer.parseInt(eventID), formID, Integer.parseInt(siteID), deletset);

                        SampleMapTagDataSource sampleMapTagDataSource = new SampleMapTagDataSource(context);
                        sampleMapTagDataSource.deletesetfromsamplemap(locID, Integer.parseInt(siteID), Integer.parseInt(eventID), deletset, formID);
                        sampleMapTagDataSource.updatesampletag(locID, Integer.parseInt(siteID), Integer.parseInt(eventID), deletset, formID);
                    }
                }
            } else {
                /**
                 * Delete form if null
                 */
                fd.deleteformFieldDataFromAllTables(locID, Integer.parseInt(eventID), formID, Integer.parseInt(siteID));
            }
        }
    }

    public LinearLayout getCheckboxView(MetaData metaData) {
        FieldDataSource fieldDataSource = new FieldDataSource(formActivity);
        ArrayList<String> checkOptions = fieldDataSource.getBottleCheckOptions(eventID,
                formActivity.getCurSetID(), locationID, siteID,
                formActivity.getCurrentAppID(), metaData.getMetaParamID());

        LinearLayout llBottleCheckboxes = new LinearLayout(formActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llBottleCheckboxes.setLayoutParams(layoutParams);
        llBottleCheckboxes.setOrientation(LinearLayout.VERTICAL);
        llBottleCheckboxes.setPadding(Util.dpToPx(10), Util.dpToPx(10), Util.dpToPx(10), Util.dpToPx(10));
        llBottleCheckboxes.setTag(metaData.getMetaParamID());

        if (metaData.getNameValueMap().size() > 0 && checkOptions.size() == 0) {
            for (String item : metaData.getNameValueMap().keySet()) {
                llBottleCheckboxes.addView(getCheckBox(metaData, item));
            }
        } else {
            if (checkOptions.size() > 0) {
                for (String item : checkOptions) {
                    llBottleCheckboxes.addView(getCheckBox(metaData, item));
                }
                return llBottleCheckboxes;
            }
        }

        return llBottleCheckboxes;
    }

    public CheckBox getCheckBox(MetaData metaData, String item) {

        HashMap<String, String> mapCheckedOptions = getSeparatedBottlesStringValues(metaData);

        AppCompatCheckBox checkBox = new AppCompatCheckBox(context);
        checkBox.setText(item);

        if (isMapContainsKey(mapCheckedOptions, item.trim()))
            checkBox.setChecked(true);

        checkBox.setOnCheckedChangeListener((checkBoxView, isChecked) -> {
            HashMap<String, String> checkedLabelNames = getSeparatedBottlesStringValues(metaData);
            StringBuilder bottles = new StringBuilder();
            String bottleName = checkBoxView.getText().toString();
            if (isChecked) {
                checkedLabelNames.put(bottleName.trim(), bottleName.trim());
            } else {
                checkedLabelNames.remove(bottleName.trim());
            }

            int i = 0;
            for (String bottleLabel : checkedLabelNames.keySet()) {
                if (i == checkedLabelNames.size() - 1) {
                    bottles.append(bottleLabel);
                } else {
                    bottles.append(bottleLabel).append("|");
                }
                i++;
            }

            DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
            tempData.value = bottles.toString();
            saveData_and_updateCreationDate(context, metaData, bottles.toString(), formActivity.getCurSetID());
        });
        return checkBox;
    }

    private boolean isMapContainsKey(HashMap<String, String> mapCheckedOptions, String item) {
        for (Map.Entry<String, String> entry : mapCheckedOptions.entrySet()) {
            if (entry.getKey().toLowerCase().equals(item.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public HashMap<String, String> getSeparatedBottlesStringValues(MetaData metaData) {
        FieldDataSource fieldDataSource = new FieldDataSource(formActivity);

        String value = fieldDataSource.getStringValueFromId(Integer.parseInt(eventID), locationID,
                formActivity.getCurrentAppID(), formActivity.getCurSetID(),
                metaData.getMetaParamID() + "");

        HashMap<String, String> mapBottles = new HashMap<>();
        if (value != null) {
            if (!value.trim().isEmpty()) {
                String[] splitArray = value.split("\\|");
                for (String bottleString : splitArray) {
                    mapBottles.put(bottleString.trim(), bottleString.trim());
                }
            }
        }
        return mapBottles;
    }

    public TextInputEditText getNumericView(final MetaData metaData,
                                            final ViewHolder viewHolder, LinearLayout.LayoutParams params) {

        int paramID = metaData.getMetaParamID();

        final TextInputEditText editText = new TextInputEditText(context);
        int margin = Util.dpToPx(5);

        params.setMargins(margin, margin, margin, 0);
        editText.setLayoutParams(params);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);//
        editText.setHint("Numeric");

        if (Locale.getDefault().getLanguage().contains("pt")
                || Locale.getDefault().getLanguage().contains("fr"))
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789,-"));

        editText.setTextSize(14);
        editText.setTag(Integer.toString(paramID));
//        editText.setBackgroundResource(R.drawable.data_entry_control_bg);
        editText.setGravity(Gravity.CENTER);

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        if (isShowLast3(metaData))
            viewHolder.prevReading = getLabelView(getPrevReadingForShowLast3(metaData));
        else
            viewHolder.prevReading = getLabelView(getPrevReadDispString(tempData));

        viewHolder.prevPercent1 = getPercentView("");
        viewHolder.prevPercent2 = getPercentView("");

        DefaultValueDataSource dv = new DefaultValueDataSource(context);

        if (tempData.value == null || tempData.value.isEmpty()) {
            tempData.value = dv.getDefaultValue(locationID, metaData.getCurrentFormID()
                    + "", metaData.getMetaParamID() + "", getCurrentSetID() + "");

            if (tempData.value == null) {
                tempData.value = metaData.getDefaultValue();
            }

            handleNumericData(metaData, tempData.value, editText, true);
        }

        String newValue = "";
        if (tempData.value != null && !tempData.value.isEmpty()) {

            newValue = getLocaleFormattedString(tempData.value, Locale.getDefault());
            String convertedValue = getLocaleFormattedString(tempData.value, Locale.ENGLISH);
            tempData.value = convertedValue;
            handleNumericData(metaData, convertedValue, editText, true);
        }

        if (newValue.isEmpty())
            editText.setText(tempData.value);
        else
            editText.setText(newValue);

        editText.setTextColor(formColor);

        if (metaData.isIsShowLast2() && getCurrentSetID() > 0)
            handleLast2Reading_And_Percentage(metaData, viewHolder, editText);

        editText.addTextChangedListener(new TextWatcher() {

            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                oldText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {

                try {

                    String value = editText.getText().toString();

                    if (metaData.getMetaInputType().equalsIgnoreCase("NUMERIC")) {

                        String keyStr = (String) editText.getTag();

                        //this check is to remove any comma if only comma or no input after comma added
                        if (value.length() == 1 && charSequence.equals(",")) {
                            return;
                        } else if (value.length() != 0 && (charSequence.length() == start - 1)
                                && charSequence.charAt(start) == ',' && value.contains(",")) {
                            return;
                        }

                        String fetchedValue = null;
                        try {
                            fetchedValue = fmapObject.get(metaData.getMetaParamID() + "").value;
                        } catch (Exception e) {
                            e.printStackTrace();
                            fetchedValue = "";
                        }

                        if (fetchedValue != null && !fetchedValue.isEmpty()) {
                            fetchedValue = getLocaleFormattedString(fetchedValue, Locale.ENGLISH);
                        }

                        String convertedValue =
                                getLocaleFormattedString(value, Locale.ENGLISH);

                        if (!value.isEmpty())
                            handleNumericData(metaData, convertedValue, editText, false);
                        else
                            handleNumericData(metaData, value, editText, false);

                        editText.setTextColor(formColor);
                        Log.i(TAG, "Numeric Field User Entered:" + value);

                        if (fetchedValue != null)
                            if (!fetchedValue.equals(convertedValue)) {

                                String extField2 = metaData.getExtField2();

                                if (metaData.getFieldAction() != null
                                        && !metaData.getFieldAction().isEmpty())
                                    extField2 = metaData.getFieldAction();

                                if (extField2 != null && extField2.equalsIgnoreCase(GlobalStrings.KEY_CALCULATED)) {
                                    setDataOnChanged(metaData.getMetaParamID());
                                }
                            }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String toMatch = editable.toString();
                int commaCount = toMatch.replaceAll("[^,]", "").length();

                if (editable.toString().length() == 1 && editable.toString().equals(",")) {
                    editText.setText("");
                } else if (commaCount > 1 &&
                        editable.toString().charAt(editable.toString().length() - 1) == ',') {
                    editText.setText(oldText);
                    editText.setSelection(editText.getText().length());
                }

                checkWarningValueForViolation(metaData, toMatch);
            }
        });

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_DONE) {
                    String keyStr = (String) editText.getTag();
                    String value = (String) editText.getText().toString();
                    handleNumericData(metaData, value, editText, true);

                    if (!STORE_VALUE) {
                        editText.setText(null);
                    }

                    if (metaData.isIsShowLast2() && getCurrentSetID() > 0)
                        handleLast2Reading_And_Percentage(metaData, viewHolder, editText);

                    if (metaData.getMetaInputType().equalsIgnoreCase("TOTALIZER")) {
                        String gText;
                        gText = metaData.ParamLabel;
                        DefaultValueDataSource dv = new DefaultValueDataSource(context);
                        DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID, metaData.getCurrentFormID() + "",
                                metaData.getMetaParamID() + "", formActivity.getCurSetID() + "");

                        String warnlowLimit = d_model.getWarningLowDefaultValue();

                        if ((warnlowLimit != null && warnlowLimit.trim().length() > 0)) {
                            String styledText = gText + "\n\n" + "<font color='red'>Value should be greater than : " + warnlowLimit + "</font>";
                            Spanned result;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                            } else {
                                result = Html.fromHtml(styledText);
                            }
                            viewHolder.lable.setText(result);
                        } else {
                            viewHolder.lable.setText(gText);
                        }
                    }
                    checkForSubFieldsForVisibility(metaData);
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "in Focus Change listener");
                if (!hasFocus) {

                    try {
                        if (!STORE_VALUE && (tempData.value == null || tempData.value.isEmpty())) {
                            //added AND condition to avoid setting empty value if there is a value
                            editText.setText(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //considered tempdata is null already
                        if (!STORE_VALUE) {
                            editText.setText(null);
                        }
                    }

                    if (metaData.isIsShowLast2() && getCurrentSetID() > 0)
                        handleLast2Reading_And_Percentage(metaData, viewHolder, editText);
                    else if (isShowLast3(metaData) && getCurrentSetID() > 0)
                        handleLast3ReadingAndPercentage(viewHolder, metaData);
                    else
                        setCalculatedFieldParams(metaData, editText, viewHolder); //added this in else as it'll be called in above methods
                    //so if none of them called this can be invoked

                    validateValue(metaData, editText);
                    editText.clearFocus();
                    checkForSubFieldsForVisibility(metaData);
                }
            }
        });

        editText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                editText.setSelection(editText.getText().length());
            }
        });

        return editText;
    }

    private boolean isShowLast3(MetaData metaData) {

        String fieldAction = "";

        if (metaData.getExtField2() != null
                && !metaData.getExtField2().isEmpty())
            fieldAction = metaData.getExtField2();

        if (metaData.getFieldAction() != null
                && !metaData.getFieldAction().isEmpty())
            fieldAction = metaData.getFieldAction();

        return fieldAction.equals(GlobalStrings.KEY_SHOW_LAST3);
    }

    private void validateValue(MetaData metaData, TextInputEditText editText) {
        String expression = metaData.getFieldParameterOperands();

        if (expression == null)
            return;

        List<String> strArray = Util.splitStringToArray("~", expression);

        for (String expr : strArray) {
            if (expr != null && !expr.isEmpty() && expr.toLowerCase().contains("!!validate!!")) {
                if (isExpressionQueryValid(expr)) {
                    String query = replaceValidateQueryCols(expr);
                    if (!query.isEmpty()) {
                        String value = new FieldDataSource(formActivity).hitExpressionQuery(query);
                        if (!value.isEmpty()) {
                            AlertManager.showNormalAlert(formActivity.getString(R.string.alert),
                                    value, formActivity.getString(R.string.ok), "",
                                    false, formActivity);
                        }
                    }
                }
            }
        }
    }

    private String convertToEnglishLocaleFormat(String value) {
        String convertedValue = "";
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
//                        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(otherSymbols);
        df.setMaximumFractionDigits(3);
        try {
            convertedValue = Objects.requireNonNull(df.parse(value)).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedValue;
    }

    private EditText getAlphaView(final MetaData metaData, LinearLayout.LayoutParams params) {

        final EditText editText = new EditText(context);
//        editText.setForm_master_context(FormMaster.this);

        params.setMargins(0, Util.dpToPx(5), 0, 0);
        editText.setLayoutParams(params);
        editText.setHint(formActivity.getString(R.string.enter_text));
        editText.setGravity(Gravity.CENTER);

        editText.setTextSize(16);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        editText.setTag(Integer.toString(metaData.getMetaParamID()));
        editText.setBackgroundResource(R.drawable.data_entry_control_bg);
        int padding = Util.dpToPx(10);
        editText.setPadding(padding, padding, padding, padding);
        //editText.setPadding(8, 8, 8, 8);

        //26-Jun-17 Expand Vertically upto 3 lines and scroll after

        editText.setMaxLines(3);
        editText.setSingleLine(false);
        editText.setScrollContainer(true);
        //      editText.setListeners();
        editText.setTextColor(formColor);

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        if (tempData.value == null) {

            //02-04-2018 CHECK CM_DETAILS FOR SAMPLE ID OR DUPLICATE SAMPLE ID

            CocDetailDataSource coc_dv = new CocDetailDataSource(context);
            tempData.value = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID, locationID,
                    metaData.getMetaParamID() + "");

            if (tempData.value == null) {
                DefaultValueDataSource dv = new DefaultValueDataSource(context);
                tempData.value = dv.getDefaultValue(locationID, metaData.getCurrentFormID() +
                        "", metaData.getMetaParamID() + "", getCurrentSetID() + "");
            }

            if (tempData.value == null) {
                String defaultValue = metaData.getDefaultValue();
                tempData.value = defaultValue;
            }

            if (tempData.value != null) {
                fmapObject.put(metaData.getMetaParamID() + "", tempData);
                mapObject.put(metaData.getMetaParamID() + "", tempData);

                tempData.value = getReplacedNewLineString(tempData.value);
            }

            handleTextData(metaData, tempData.value, getCurrentSetID());
        } else {
            //06-04-2018 IF VALUE COLLECTED FOR 1st COC AND NOW 2nd COC FOR SAME FIELD
            if (formActivity.currCocID != null) {
                //02-04-2018 CHECK CM_DETAILS FOR SAMPLE ID OR DUPLICATE SAMPLE ID
                CocDetailDataSource coc_dv = new CocDetailDataSource(context);
                String val = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID, locationID,
                        metaData.getMetaParamID() + "");

                if (val != null && !val.isEmpty()) {
                    tempData.value = val;
                }
            }
        }

        tempData.value = getReplacedNewLineString(tempData.value);

        editText.setText(tempData.value);
        editText.setTextColor(formColor);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                String keyStr = (String) editText.getTag();
                String value = editText.getText().toString();
                tempData.value = value;
                editText.setTextColor(formColor);

                String expression = metaData.getFieldParameterOperands();

                if (expression != null && (expression.contains("SAMPLE")
                        || expression.toLowerCase().contains("!!coc!!"))
                        && !value.isEmpty()) {
                    isSampleDateOrTimeSet = false;
                }

                handleTextData(metaData, tempData.value, getCurrentSetID());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                checkWarningValueForViolation(metaData, value);
            }
        });

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_DONE) {
                    String expression = metaData.getFieldParameterOperands();
                    if (expression != null && (expression.contains("SAMPLE4")
                            || expression.contains("DUPSAMPLE4")) && !editText.getText().toString().isEmpty()) {
                        calculateSample4Expression(metaData, editText.getText().toString());
                    }
                    checkForSubFieldsForVisibility(metaData);
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    editText.clearFocus();
                    checkForSubFieldsForVisibility(metaData);
                }
            }
        });

        return editText;
    }

    private void checkForSubFieldsForVisibility(MetaData metaData) {
        for (Map.Entry<String, MetaData> entry : mapHiddenMetaObjects.entrySet()) {
            String expression = entry.getValue().getFieldParameterOperands();
            if (expression != null
                    && expression.contains(metaData.getMetaParamID() + "")) {

                //if the expression will have "~" symbol then it has multiple queries
                List<String> queryArray = Util.splitStringToArray("~", expression);

                if (!queryArray.isEmpty()) {
                    for (String queryExpr : queryArray) {
                        if (queryExpr.contains("!!visible!!")) {
                            if (isExpressionQueryValid(queryExpr)) {
                                queryExpr = queryExpr.replace("true", "1");
                                queryExpr = queryExpr.replace("false", "0");

                                String query = replaceSetOrVisibleQueryCols(queryExpr);
                                if (!query.isEmpty()) {
                                    boolean showField = new FieldDataSource(formActivity)
                                            .hitExpressionQuery(query).equals("1");
                                    for (MetaData mData : filteredMetaObjects) {
                                        if (String.valueOf(mData.getMetaParamID())
                                                .equals(entry.getKey())) {
                                            mData.isRowVisible = showField;
                                            mData.isVisible = showField;

                                            if (mapMetaObjects.containsKey(mData.getMetaParamID())) {
                                                mapMetaObjects.get(mData.getMetaParamID()).isRowVisible = showField;
                                            }

                                            if (mData.getForm_field_row() != null)
                                                formActivity.attachViewToScroller(mData);
                                            else
                                                formActivity.attachViewToScroller(filteredMetaObjects);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void makeFieldsVisible(MetaData metaData) {

        Predicate<MetaData> filter = new Predicate<MetaData>() {
            @Override
            public boolean apply(@Nullable MetaData entry) {
                String expression = getExpressionFromMetaOrAttribute(entry);
                return expression != null
                        && expression.contains(metaData.getMetaParamID() + "")
                        && entry.getMetaParamID() != metaData.getMetaParamID();
            }
        };

        Map<String, MetaData> filteredValues = Maps.filterValues(mapHiddenMetaObjects, filter);

        for (Map.Entry<String, MetaData> entry : filteredValues.entrySet()) {
            String expression = entry.getValue().getFieldParameterOperands();
            if (expression != null
                    && expression.contains(metaData.getMetaParamID() + "")) {

                //if the expression will have "~" symbol then it has multiple queries
                List<String> queryArray = Util.splitStringToArray("~", expression);

                if (!queryArray.isEmpty()) {
                    for (String queryExpr : queryArray) {
                        if (queryExpr.contains("!!visible!!")) {
                            if (isExpressionQueryValid(queryExpr)) {
                                queryExpr = queryExpr.replace("true", "1");
                                queryExpr = queryExpr.replace("false", "0");

                                String query = replaceSetOrVisibleQueryCols(queryExpr);
                                if (!query.isEmpty()) {
                                    boolean showField = new FieldDataSource(formActivity)
                                            .hitExpressionQuery(query).equals("1");

                                    if (mapMetaObjects.containsKey(entry.getValue().getMetaParamID())) {
                                        MetaData mData = mapMetaObjects.get(entry.getValue().getMetaParamID());
                                        //in case mData doesn't reflect change
                                        mapMetaObjects.get(entry.getValue().getMetaParamID()).isRowVisible = showField;

                                        mData.isRowVisible = showField;
                                        mData.isVisible = showField;

                                        if (mData.getForm_field_row() != null)
                                            formActivity.attachViewToScroller(mData);
                                        else
                                            formActivity.attachViewToScroller(filteredMetaObjects);
                                        setCalculatedFieldParams(mData, null, null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private ImageView getGpsView(final MetaData metaData, ViewHolder viewHolder) {

        ImageView gpsBtn = new ImageView(context);
        gpsBtn.setFocusable(true);
        gpsBtn.setClickable(true);
        gpsBtn.setTag(Integer.toString(metaData.getMetaParamID()));
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams(Util.dpToPx(20), Util.dpToPx(20));
        layoutParams.setMargins(0, 0, 5, 0);
        gpsBtn.setLayoutParams(layoutParams);
        gpsBtn.setImageDrawable(ContextCompat.getDrawable(formActivity, R.drawable.ic_gps));

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
   // commented on 01 Dec, 20
   int spid = metaData.getMetaParamID();

                String latitude1, longitude;

                boolean gpsPermissionStatus = checkWriteExternalPermission();

                if (gpsPermissionStatus) {
                    GPSTracker tracker = new GPSTracker(context);
                    GlobalStrings.CURRENT_GPS_LOCATION = tracker.getLocation();

                    latitude1 = String.valueOf(tracker.getLatitude());
                    longitude = String.valueOf(tracker.getLongitude());

                    Double lat = Double.valueOf(latitude1);
                    Double lng = Double.valueOf(longitude);
                    String value = lat + "," + lng;

                    handleTextData(metaData, value, getCurrentSetID());

                    viewHolder.tvGpsCoordinates.setText(value);
                    Log.i("Updated successfully", "");
                    Toast.makeText(context, "GPS coordinates captured.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(formActivity, "Location permission denied", Toast.LENGTH_SHORT).show();
                    GPSTracker tracker = new GPSTracker(context);
                    GlobalStrings.CURRENT_GPS_LOCATION = tracker.getLocation();

                    latitude1 = String.valueOf(tracker.getLatitude());
                    longitude = String.valueOf(tracker.getLongitude());

                    Double lat = Double.valueOf(latitude1);
                    Double lng = Double.valueOf(longitude);
                    String value = lat + "," + lng;

                    handleTextData(metaData, value, getCurrentSetID());

                    Log.i("Updated successfully", "");
                    //Toast.makeText(context, "GPS coordinates captured.", Toast.LENGTH_LONG).show();
                }*/

                /*
                //old commented code
                boolean isgpsEnabled = false;

                isgpsEnabled = tracker.isGPSEnabled();

                if (!isgpsEnabled) {
                    tracker.showSettingsAlert();
                } else {
                    Boolean l = false;

                    l = tracker.isGPSEnabled();
                    if (l) {
                        latitude1 = String.valueOf(tracker.getLatitude());
                        longitude = String.valueOf(tracker.getLongitude());

                        Double lat = Double.valueOf(latitude1);
                        Double lng = Double.valueOf(longitude);
                        String value = lat + "," + lng;

                        handleTextData(metaData, value, getCurrentSetID());


                        Log.i("Updated successfully", "");
                        gpsbtn.setBackgroundColor(context.getResources().getColor(R.color.color_chooser_green));
                        Toast.makeText(context, "GPS coordinates captured.", Toast.LENGTH_LONG).show();
                    } else {
                        tracker.showSettingsAlert();
                    }

                }*/

                Intent intent = new Intent(formActivity, MapDragActivity.class);
                intent.putExtra(GlobalStrings.KEY_META_DATA, metaData);
                formActivity.startActivityForResult(intent, CAPTURE_GPS_LOCATION_REQUEST_CODE);
            }
        });

        return gpsBtn;
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private RatingBar getRatingView(final MetaData metaData) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 5, 0, 0);

        RatingBar ratingBar = new RatingBar(context);

        String no_of_star = metaData.getMetaNameValuePair();
        //String no_of_star = String.valueOf(11);
        if (no_of_star != null) {
            try {
                int s = Integer.parseInt(no_of_star);
                if (s <= 10) {
//            final RatingBar ratingBar = new RatingBar(ObjContext);
                    ratingBar.setLayoutParams(lp);
                    ratingBar.setNumStars(s);
                    ratingBar.setFocusable(true);
                    ratingBar.setStepSize(1);
                    Drawable progress = ratingBar.getProgressDrawable();
                    DrawableCompat.setTint(progress, Color.GRAY);
                } else {
                    ratingBar.setNumStars(10);
                    ratingBar.setLayoutParams(lp);
                    ratingBar.setFocusable(true);
                    ratingBar.setStepSize(1);
                    Drawable progress = ratingBar.getProgressDrawable();
                    DrawableCompat.setTint(progress, Color.GRAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ratingBar.setNumStars(5);
            ratingBar.setLayoutParams(lp);
            ratingBar.setFocusable(true);
            ratingBar.setStepSize(1);
            Drawable progress = ratingBar.getProgressDrawable();
            DrawableCompat.setTint(progress, Color.GRAY);
        }


        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        String defaultValue = metaData.getDefaultValue();

        if (tempData.value == null) {
            tempData.value = defaultValue;
        }

        if (tempData.value == null || tempData.value.isEmpty()) {
            Drawable progress = ratingBar.getProgressDrawable();
            DrawableCompat.setTint(progress, Color.GRAY);
        } else {
            // viewHolder.ratingView.setIsIndicator(true);
            ratingBar.setRating(Float.parseFloat(tempData.value));
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.qnopy_teal),
                    PorterDuff.Mode.SRC_ATOP);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.qnopy_teal), PorterDuff.Mode.SRC_ATOP);
                int colorrating = (int) ratingBar.getRating();
                handleTextData(metaData, colorrating + "", getCurrentSetID());
            }
        });

        return ratingBar;
    }

    private ImageView getSignatureView(final MetaData metaData) {

        ImageView signatureBtn = new ImageView(context);
        signatureBtn.setFocusable(true);
        signatureBtn.setClickable(true);

        signatureBtn.setTag(Integer.toString(metaData.getMetaParamID()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, 0, 5, 0);
//        layoutParams.gravity = Gravity.END;
        signatureBtn.setLayoutParams(layoutParams);

        signatureBtn.setImageDrawable(ContextCompat.getDrawable(formActivity, R.drawable.signature_icon));

        signatureBtn.setOnClickListener(view -> startSignatureActivity(metaData));

        return signatureBtn;
    }

    private void startSignatureActivity(MetaData metaData) {

        Intent signatureIntent = new Intent(context, CaptureSignature.class);

        signatureIntent.putExtra("EVENT_ID", eventID);
        signatureIntent.putExtra("LOC_ID", locationID);
        signatureIntent.putExtra("APP_ID", metaData.getCurrentFormID());
        signatureIntent.putExtra("SITE_ID", siteID);
        signatureIntent.putExtra("paramID", metaData.getMetaParamID());
        signatureIntent.putExtra("setID", getCurrentSetID());
        signatureIntent.putExtra("SiteName", sitename);
        signatureIntent.putExtra("UserID", userID);

        formActivity.startActivityForResult(signatureIntent, CAPTURE_SIGNATURE_ACTIVITY_REQUEST_CODE);
    }

    private TextView getTimeView(MetaData metaData) {

//        FieldDataSource fieldDataSource = new FieldDataSource(context);
        FBTimeView timeView = new FBTimeView(context);
        timeView.setFormMasterContext(FormMaster.this);
        timeView.setLayoutParams(getTextLayoutParamsForNumber());
        timeView.setTag(metaData.getMetaParamID());//replaced to position

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        setTimeViewData(metaData, timeView);

//        handleDateAndTimeData(metaData, tempData.value, getCurrentSetID());

        timeView.setListeners(metaData);
        timeView.setGravity(Gravity.CENTER_VERTICAL);
        timeView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        timeView.setTextColor(formColor);

        return timeView;
    }

    private TextView getLabelViewControl(MetaData metaData) {

        TextView labelView = new TextView(context);
        labelView.setLayoutParams(getLongTextLayoutParams());
        labelView.setTag(metaData.getMetaParamID());//replaced to position

        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        handleTextData(metaData, tempData.value, getCurrentSetID());
        labelView.setHint(formActivity.getString(R.string.auto_label));
        labelView.setText(tempData.value);
        labelView.setTextSize(16f);
        //labelView.setTextSize(14f);
        labelView.setPadding(5, 0, 0, 5);
        labelView.setGravity(Gravity.CENTER_VERTICAL);
        labelView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        labelView.setTextColor(formColor);

        updateLabelViewControl(metaData, tempData.value, labelView);

        return labelView;
    }

    public void updateLabelViewControl(MetaData metaData, String value, TextView labelTextView) {
        StringBuilder splitdata = new StringBuilder();
        Spanned finalhtmldata = null;

        List<String> list = new ArrayList<>();

        String data = value;

        if (data != null && !data.isEmpty()) {
            String[] value_split;
            if (data.contains("|")) {
                value_split = data.split("\\|");
                list = Arrays.asList(value_split);
            } else {
                list = Arrays.asList(data);
            }

            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    splitdata.append("<br/>");
                }

                String name = list.get(i);
                name = name.replace("<", " less than ");
                name = name.replace(">", " greater than ");

                splitdata.append("<b>" + "<font color='black'>" + "\u2022 ").append(name).append("</b>").append("</font> ");
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                finalhtmldata = Html.fromHtml(splitdata.toString(), Html.FROM_HTML_MODE_LEGACY);
            } else {
                finalhtmldata = Html.fromHtml(splitdata.toString());
            }
        }

        if (labelTextView != null) {
            labelTextView.setText(finalhtmldata);
        }
    }

    private TextView getDateView(MetaData metaData) {

        final FBDateView dateView = new FBDateView(context);
        dateView.setFormMasterContext(FormMaster.this);

        dateView.setLayoutParams(getTextLayoutParamsForNumber());
        dateView.setTag(Integer.toString(metaData.getMetaParamID()));
        DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));

        setDateViewData(metaData, dateView);

        dateView.setGravity(Gravity.CENTER_VERTICAL);
        dateView.setListeners(metaData);

//        this.updateDate(this.date);//09-Dec-15
        dateView.setTextColor(formColor);

        return dateView;
    }

    private LinearLayout getNumberPickerView(final MetaData metaData) {
        final DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
        final LinearLayout lin = new LinearLayout(context);
        LinearLayout linNum = new LinearLayout(context);
        LinearLayout linPlus = new LinearLayout(context);
        LinearLayout linMinus = new LinearLayout(context);

        final EditText num = new EditText(context);
        num.setGravity(Gravity.CENTER);
        num.setImeOptions(EditorInfo.IME_ACTION_DONE);
        num.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        num.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        num.setInputType(InputType.TYPE_CLASS_NUMBER);
        linNum.addView(num);

        ImageButton plus = new ImageButton(context);
        plus.setBackgroundResource(R.mipmap.plus);
        linPlus.addView(plus);

        ImageButton minus = new ImageButton(context);
        minus.setBackgroundResource(R.mipmap.minus);
        linMinus.addView(minus);

        lin.addView(linMinus);
        lin.addView(linNum);
        lin.addView(linPlus);

        lin.setTag(Integer.toString(metaData.getMetaParamID()));

        lin.setPadding(5, 0, 0, 5);

        String defaultValue = metaData.getDefaultValue();

        if (tempData.value == null) {
            DefaultValueDataSource dv = new DefaultValueDataSource(context);
            tempData.value = dv.getDefaultValue(locationID,
                    metaData.getCurrentFormID() + "",
                    metaData.getMetaParamID() + "", getCurrentSetID() + "");

            if (tempData.value == null) {
                tempData.value = defaultValue;
            }
        }

        if (tempData.value == null || tempData.value.length() <= 0) {
            tempData.value = "0";
        }

        num.setText(tempData.value);

        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String val = num.getText().toString();
                int number = 0;

                Pattern p = Pattern.compile("^[0-9]+$");
                Matcher m = p.matcher(val);

                boolean b = m.find();
                if (b) {
                    if ((val != null) && (val.length() != 0)) {
                        number = (int) Long.parseLong(val);
                    }

                    number = number + 1;
                    val = Integer.toString(number);

                } else {
                    val = "" + 0;

                }

                num.setText(val);

                //   handleCounterData(metaData, val, getCurrentSetID());
            }

        });

        minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String val = num.getText().toString();

                int number = 0;


                Pattern p = Pattern.compile("^[0-9]+$");
                Matcher m = p.matcher(val);

                boolean b = m.find();
                if (b) {

                    if ((val != null) && (val.length() != 0)) {
                        number = (int) Long.parseLong(val);

                    }

                    if (number > 0) {
                        number = number - 1;
                    } else {
                        number = 0;
                    }
                    val = Integer.toString(number);


                } else {
                    val = "" + 0;

                }

                num.setText(val);

                // handleCounterData(metaData, val, getCurrentSetID());
            }

        });

        num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value = num.getText().toString();
                Log.i(TAG, "Number before text change:" + value);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String value = num.getText().toString();

                Pattern p = Pattern.compile("^[0-9]+$");
                Matcher m = p.matcher(value);

                boolean b = m.find();
                if (!b) {
                    value = "" + 0;
                    num.setText(value);
                }

                num.setSelection(value.length());
                handleCounterData(metaData, value, getCurrentSetID());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                checkWarningValueForViolation(metaData, value);
            }
        });


        lin.setWeightSum(4f);
        //lin.getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        lin.getChildAt(1).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
        lin.getChildAt(2).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        //lin.setGravity(Gravity.CENTER_VERTICAL);

        return lin;
    }

    private Spinner getSpinnerView(final MetaData metaData, List<String> dataList,
                                   int sel_position) {

        final DynamicWidthSpinner spnView = new DynamicWidthSpinner(context);
        spnView.setTag(Integer.toString(metaData.getMetaParamID()));

        spnView.setBackgroundResource(R.drawable.data_entry_control_bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = Util.dpToPx(5);
        lp.setMargins(0, margin, 0, 0);
        spnView.setLayoutParams(lp);
        spnView.setPadding(margin, margin, margin, margin);

        final List<String> list = new ArrayList<String>();
        list.add(formActivity.getString(R.string.select_spinner_first_item));
        list.addAll(dataList);

        //Picker WRAPS contents :bug fix
        SpinnerDropdownAdapter adapter = new SpinnerDropdownAdapter(context, list) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };

        spnView.setAdapter(adapter);

        if ((metaData.getNameValueMap().containsKey("Coc")
                || metaData.getNameValueMap().containsKey("COC")) && dataList.size() == 1) {
            sel_position = 1;
        } else {
            //catering for adding "Select" which is dummy. When value is null, sel_position will be -1
            if (sel_position == -1) {
                sel_position = 0;
            } else {
                sel_position += 1;
            }
        }

        spnView.setSelection(sel_position);

//        spnView.setBackgroundResource(R.drawable.data_entry_control_bg);
        DataHolder temp = fmapObject.get(metaData.getMetaParamID() + "");
        if (temp != null) {
            handleTextData(metaData, temp.value, getCurrentSetID());
        }

        spnView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                DataHolder tempData = fmapObject.get(Integer.toString(metaData.getMetaParamID()));
                ((TextView) parent.getChildAt(0)).setTextColor(formColor);

                //                adapter.getItem(position)
                if (tempData != null) {
                    if (tempData.nameValuePair == null) {

                        if (metaData.getNameValueMap().containsKey("Coc")
                                || metaData.getNameValueMap().containsKey("COC")) {
                            LocationDataSource ld = new LocationDataSource(formActivity);
                            ArrayList<SCocMaster> cocMasterArrayList
                                    = ld.getAllCoCIDs(formActivity.getEventID() + "",
                                    formActivity.getSiteID() + "");
                            LinkedHashMap<String, String> pickerCocNameValueMap = new LinkedHashMap<>();

                            for (SCocMaster master : cocMasterArrayList) {
                                pickerCocNameValueMap.put(master.getCocDisplayId(), master.getCocDisplayId());
                                cocMasterValueMap.put(master.getCocDisplayId(), master);
                            }
                            tempData.setNameValuePair(pickerCocNameValueMap);
                        } else {
                            tempData.setNameValuePair(metaData.getNameValueMap());
                        }
                    }

                    String selectedItem = tempData.nameValuePair.get(list.get(position));

                    if (selectedItem == null)
                        return;

                    tempData.value = selectedItem;

                    handleTextData(metaData, tempData.value, getCurrentSetID());
                    checkWarningValueForViolation(metaData, selectedItem);

                    if (metaData.getNameValueMap().containsKey("Coc")
                            || metaData.getNameValueMap().containsKey("COC")) {
                        if (cocMasterValueMap.containsKey(tempData.value)) {
                            formActivity.currCocID = cocMasterValueMap.get(tempData.value).getCocId() + "";
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setDataOnChanged(0);
                                }
                            }, 200);
                        }
                    }
                }

                if (metaData.getForm_field_row() != null)
                    checkForSubFieldsForVisibility(metaData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.d("", "");
            }
        });

        return spnView;
    }

    private void rearrangeExpandableFields() {
        // 14-02-2018 SORT BY ROW ORDER
        if (form_meta_list.size() > 0) {
            Collections.sort(form_meta_list, new CustomComparator());
        }
        formActivity.attachViewToScroller(form_meta_list);
    }

    public class CustomComparator implements Comparator<MetaData> {
        @Override
        public int compare(MetaData lhs, MetaData rhs) {
            int res = 0;
            if (lhs.getMetaRowOrder() > rhs.getMetaRowOrder()) {
                res = 1;
            } else {
                res = -1;
            }
            return res;


        }
    }

    public class DynamicWidthSpinner extends androidx.appcompat.widget.AppCompatSpinner {

        public DynamicWidthSpinner(Context context) {
            super(context);
        }

        public DynamicWidthSpinner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DynamicWidthSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setAdapter(SpinnerAdapter adapter) {
            super.setAdapter(adapter != null ? new DynamicWidthSpinner.WrapperSpinnerAdapter(adapter) : null);
        }

        public final class WrapperSpinnerAdapter implements SpinnerAdapter {

            private final SpinnerAdapter mBaseAdapter;

            public WrapperSpinnerAdapter(SpinnerAdapter baseAdapter) {
                mBaseAdapter = baseAdapter;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                return mBaseAdapter.getView(getSelectedItemPosition(), convertView, parent);
            }

            public final SpinnerAdapter getBaseAdapter() {
                return mBaseAdapter;
            }

            public int getCount() {
                return mBaseAdapter.getCount();
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return mBaseAdapter.getDropDownView(position, convertView, parent);
            }

            public Object getItem(int position) {
                return mBaseAdapter.getItem(position);
            }

            public long getItemId(int position) {
                return mBaseAdapter.getItemId(position);
            }

            public int getItemViewType(int position) {
                return mBaseAdapter.getItemViewType(position);
            }

            public int getViewTypeCount() {
                return mBaseAdapter.getViewTypeCount();
            }

            public boolean hasStableIds() {
                return mBaseAdapter.hasStableIds();
            }

            public boolean isEmpty() {
                return mBaseAdapter.isEmpty();
            }

            public void registerDataSetObserver(DataSetObserver observer) {
                mBaseAdapter.registerDataSetObserver(observer);
            }

            public void unregisterDataSetObserver(DataSetObserver observer) {
                mBaseAdapter.unregisterDataSetObserver(observer);
            }
        }
    }

    private RadioGroup getRadioGroup(final MetaData metaData, final List<String> strList,
                                     int sel_position) {
        final RadioGroup radioGroup = new RadioGroup(context);
        RadioButton radio = null;
        radioGroup.setOrientation(LinearLayout.HORIZONTAL); // 0-horizontal and 1-vertical
        radioGroup.setGravity(Gravity.CENTER);

        radioGroup.setBottom(4);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;

        params.setMargins(0, 5, 0, 0);

        DataHolder tempData = fmapObject.get(metaData.getMetaParamID() + "");
        handleTextData(metaData, tempData.value, getCurrentSetID());

        for (int i = 0; i < strList.size(); i++) {
            radio = new RadioButton(context);
            radio.setText(strList.get(i));
            radio.setLayoutParams(params);

//			radio.setTextColor(formColor);
            radio.setId(i);
            radio.setTag(Integer.toString(metaData.getMetaParamID()));
            if (i == sel_position) {
                radio.setChecked(true);
                radio.setTextColor(formColor);
            }

            radio.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    boolean checked = ((RadioButton) v).isChecked();
                    ((RadioButton) v).setTextColor(formColor);

                    // Check which radio button was clicked
                    int data = v.getId();
                    String keyStr = metaData.getMetaParamID() + "";
                    String sel_key = (String) ((RadioButton) v).getText();
                    DataHolder tempData = fmapObject.get(keyStr);

                    Log.i(TAG, "after gettag in radio data=" + data);

                    if (tempData.nameValuePair != null) {
                        tempData.value = tempData.nameValuePair.get(sel_key);
                        handleTextData(metaData, tempData.value, getCurrentSetID());
                        checkWarningValueForViolation(metaData, tempData.value);
                    }

                    if (metaData.getForm_field_row() != null)
                        checkForSubFieldsForVisibility(metaData);
                }
            });

            radioGroup.addView(radio);
        }

        return radioGroup;
    }

    public void handleTextData(MetaData metaData, String value, int curSetID) {

        if (metaData.getMetaInputType().equalsIgnoreCase("MULTIMETHODS")) {
            if (value != null) {
                if (value.contains("|null")) {
                    value = value.replace("|null", "");
                } else if (value.contains("null")) {
                    value = value.replace("null", "");
                }
            }
        } else {
            if (value != null) {
                if (value.contains(",null")) {
                    value = value.replace(",null", "");
                } else if (value.contains("null")) {
                    value = value.replace("null", "");
                }
            }
        }
        FieldDataSource fieldDataSource = new FieldDataSource(context);

        if (!fieldDataSource.isParamIdExists(curSetID, Integer.parseInt(eventID),
                locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {

            List<FieldData> bfieldData = getBlankFieldData(context, metaData, curSetID);
            fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID);
        }

        if (!metaData.getMetaInputType().equals("CHECKBOX")) {
            saveData_and_updateCreationDate(context, metaData, value, curSetID);
        } else {

            ArrayList<String> checkOptions = fieldDataSource.getBottleCheckOptions(eventID,
                    formActivity.getCurSetID(), locationID, siteID,
                    formActivity.getCurrentAppID(), metaData.getMetaParamID());

            HashMap<String, String> bottleLabels = getSeparatedBottlesStringValues(metaData);

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
                ViewHolder viewholder = (ViewHolder) metaData.getForm_field_row().getTag();

                if (labelValue.toString().isEmpty() && checkOptions.size() == 0)
                    viewholder.llCocBottlesCheckOptions.removeAllViews();
            }

            saveData_and_updateCreationDate(context, metaData, labelValue.toString(), curSetID);
        }

        if (metaData.getForm_field_row() != null) {
            setMandatoryFieldAlert(metaData, (ViewHolder) metaData.getForm_field_row().getTag());
        }
    }

    public void handleCounterData(MetaData metaData, String value, int curSetID) {

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        if (!fieldDataSource.isParamIdExists(getCurrentSetID(), Integer.parseInt(eventID),
                locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {

            List<FieldData> bfieldData = getBlankFieldData(context, metaData, curSetID);
            fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID);
        }
        saveData_and_updateCreationDate(context, metaData, value, curSetID);

        if (metaData.getForm_field_row() != null) {
            setMandatoryFieldAlert(metaData, (ViewHolder) metaData.getForm_field_row().getTag());
        }

        String extField2 = metaData.getExtField2();

        if (metaData.getFieldAction() != null
                && !metaData.getFieldAction().isEmpty())
            extField2 = metaData.getFieldAction();

        if (extField2 != null && extField2.equalsIgnoreCase(GlobalStrings.KEY_CALCULATED)) {
            setDataOnChanged(metaData.getMetaParamID());
        }
    }

    public void handleDateAndTimeData(MetaData metaData, String value, int curSetID) {

        fmapObject.get(Integer.toString(metaData.getMetaParamID())).value = value;

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        //03-Jul-17 UPDATE DATE AND TIME
        if (!fieldDataSource.isParamIdExists(curSetID, Integer.parseInt(eventID),
                locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {
            List<FieldData> bFieldData = getBlankFieldData(context, metaData, curSetID);
            fieldDataSource.insertFieldDataList(bFieldData, Integer.parseInt(userID), deviceID + "");
        }

        if (metaData.getMetaParamID() == 25) {
            fieldDataSource.updateDateInExt2(Integer.parseInt(eventID), curSetID, locationID + "",
                    Integer.parseInt(siteID), metaData.getCurrentFormID(), value);
        }

        if (metaData.getMetaParamID() == 15) {
            fieldDataSource.updateTimeInExt3(Integer.parseInt(eventID), curSetID, locationID + "",
                    Integer.parseInt(siteID), metaData.getCurrentFormID(), value);
        }

        saveData_and_updateCreationDate(context, metaData, value, curSetID);
        checkWarningValueForViolation(metaData, value);

        if (metaData.getForm_field_row() != null) {
            setMandatoryFieldAlert(metaData, (ViewHolder) metaData.getForm_field_row().getTag());
        }

        String extField2 = metaData.getExtField2();

        if (metaData.getFieldAction() != null
                && !metaData.getFieldAction().isEmpty())
            extField2 = metaData.getFieldAction();

        if (extField2 != null && extField2.equalsIgnoreCase(GlobalStrings.KEY_CALCULATED)) {
            setDataOnChanged(metaData.getMetaParamID());
        }
    }

    public void saveData_and_updateCreationDate(Context context, MetaData metaData,
                                                String value, int currentSetID) {

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        fieldDataSource.updateValue(Integer.parseInt(eventID), metaData.getMetaParamID(), currentSetID,
                locationID, value, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                GlobalStrings.CURRENT_GPS_LOCATION,
                deviceID, userID + "");
        //06-Jan-16
        long creationDate = System.currentTimeMillis();

        Long measurementTime = fieldDataSource.getMeasurementTime(Integer.parseInt(eventID), locationID,
                metaData.getCurrentFormID() + "", getCurrentSetID(), "25", Integer.parseInt(siteID));
        String oldCreationDate = fieldDataSource.getCreationDateForMobileApp(metaData.getCurrentFormID(),
                Integer.parseInt(eventID), Integer.parseInt(siteID), locationID, Integer.parseInt(userID), currentSetID);

        if (oldCreationDate != null) {
            fieldDataSource.updateCreationDate(Integer.parseInt(eventID), currentSetID,
                    locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(), Long.parseLong(oldCreationDate));
        } else {
            fieldDataSource.updateCreationDate(Integer.parseInt(eventID), currentSetID,
                    locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(), creationDate);
        }

        if (metaData.getMetaInputType().equalsIgnoreCase("GPS")) {
            if (value != null && !value.isEmpty()) {
                String[] corr_lat_lng = value.split(",");
                fieldDataSource.updateCorrectedLatLong(Integer.parseInt(eventID),
                        currentSetID, locationID, Integer.parseInt(siteID),
                        metaData.getCurrentFormID(), Double.parseDouble(corr_lat_lng[0]),
                        Double.parseDouble(corr_lat_lng[1]));
            }
        }

        if (measurementTime == null || measurementTime == 0)
            fieldDataSource.updateMeasurementTime(Integer.parseInt(eventID), currentSetID,
                    locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                    creationDate);
        else fieldDataSource.updateMeasurementTime(Integer.parseInt(eventID), currentSetID,
                locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                measurementTime);
    }

    private void showUnSyncedFieldsBadge() {

        if (Util.isThereAnyDataToSync(formActivity)) {
            formActivity.tvNotifyUploadStatus.setVisibility(View.VISIBLE);
        } else {
            formActivity.tvNotifyUploadStatus.setVisibility(View.GONE);
        }

/*        if (fieldCount > 0) {
            formActivity.tvNotifyUploadStatus.setVisibility(View.VISIBLE);
            formActivity.tvNotifyUploadStatus.setText(fieldCount);
        } *//*else if (fieldCount > 100) {
            formActivity.tvNotifyUploadStatus.setVisibility(View.VISIBLE);
            formActivity.tvNotifyUploadStatus.setText("100+");
        }*//* else {
            formActivity.tvNotifyUploadStatus.setVisibility(View.GONE);
            formActivity.tvNotifyUploadStatus.setText("");
        }*/
    }

    public void updateChild(String fpID, String displayValue) {

        LovDataSource ld = new LovDataSource(context);
        MetaDataSource md = new MetaDataSource(context);

        DataHolder tempData = fmapObject.get(fpID);

        if (mapMetaObjects.containsKey(Integer.parseInt(fpID))) {
            MetaData metaData = mapMetaObjects.get(Integer.parseInt(fpID));

            int childParamID = md.getchildparamID(metaData.getCurrentFormID(), metaData.getMetaParamID() + "");

            if (childParamID > 0) {
                int parentLovItemId = ld.getparentLovItemID(tempData.lovID, displayValue);
                fmapObject.get(childParamID + "").setParentlovItemID(parentLovItemId);
            }
        }
    }

    public void update_NavigateTo_formID(String fpID, String key) {
        DataHolder tempData = fmapObject.get(fpID);

        LovDataSource lovObject = new LovDataSource(context);
        int navigate_to_formID = lovObject.get_navigateToformID(tempData.lovID, key);
        fmapObject.get(fpID).setGoto_formID(navigate_to_formID);

        for (int i = 0; i < filteredMetaObjects.size(); i++) {
            MetaData mData = filteredMetaObjects.get(i);
            if (mData.getMetaParamID() == Integer.parseInt(fpID)) {
                filteredMetaObjects.get(i).setFormID(navigate_to_formID);
                break;
            }
        }
    }

    private List<FieldData> getBlankFieldData(Context context, MetaData metaData,
                                              int currentSetID) {

        FieldData fieldData = null;
        List<FieldData> flist = new ArrayList<>();

        try {
            fieldData = new FieldData();
            Log.i(TAG, "getBlankFieldData() FieldParamID="
                    + metaData.getMetaParamID() + ",FieldParameterLabel=" + metaData.getMetaParamLabel());

            fieldData.setStringValue(null);

            fieldData.setFieldParameterID(metaData.getMetaParamID());
            fieldData.setFieldParameterLabel(metaData.getMetaParamLabel());

            // TODO: 7/1/17
            fieldData.setCreationDate(0);
            fieldData.setLocationID(locationID);
            fieldData.setEventID(Integer.parseInt(eventID));
            FieldDataSource fieldDataSource = new FieldDataSource(context);


            String ext2 = fieldDataSource.getExt2ForMobileApp(metaData.getCurrentFormID(),
                    Integer.parseInt(eventID), Integer.parseInt(siteID), locationID, currentSetID);
            String ext3 = fieldDataSource.getExt3ForMobileApp(metaData.getCurrentFormID(),
                    Integer.parseInt(eventID), Integer.parseInt(siteID), locationID, currentSetID);

            fieldData.setExtField2(ext2);
            fieldData.setExtField3(ext3);


            fieldData.setUnits(metaData.DesiredUnits);

            fieldData.setCurSetID(currentSetID);
            fieldData.setExtField4("0");
            fieldData.setSiteID(Integer.parseInt(siteID));
            fieldData.setUserID(Integer.parseInt(userID));
            fieldData.setMobileAppID(metaData.getCurrentFormID());
            fieldData.setDeviceId(deviceID);

            flist.add(fieldData);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,
                    "getBlankFieldData() An Error Occured-" + e.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        return flist;
    }


    private LinearLayout.LayoutParams getNumericLayoutParamsWithoutLast2Reading() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int deviceWidth = metrics.widthPixels;

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        LinearLayout.LayoutParams lp;
        if (diagonalInches >= 6.0) {
            // 6.5inch device or bigger
            // LinearLayout.LayoutParams lp;
            lp = new LinearLayout.LayoutParams(deviceWidth / 2 + deviceWidth / 3 + 90, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            // smaller device
            lp = new LinearLayout.LayoutParams(deviceWidth / 2 + deviceWidth / 3 + 48, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

       /* LinearLayout.LayoutParams lp;
        lp = new LinearLayout.LayoutParams(deviceWidth/2 + deviceWidth/3 + 90, LinearLayout.LayoutParams.WRAP_CONTENT);
        return lp;*/
    }

    private LinearLayout.LayoutParams getNumericLayoutParams() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int deviceWidth = metrics.widthPixels;

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

        LinearLayout.LayoutParams lp;
        //lp = new LinearLayout.LayoutParams(deviceWidth/3 + 36, LinearLayout.LayoutParams.WRAP_CONTENT);
        /*if (diagonalInches >= 6.0){
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            return lp;
        }else {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            return lp;
        }*/
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        return lp;
    }

    private ViewGroup.LayoutParams getTextLayoutParamsForNumber() {
        ViewGroup.LayoutParams lp;
        lp = new ViewGroup.LayoutParams(dpLongTextWidth - 100, 30);
        //lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 30);
        return lp;
    }

    private LinearLayout.LayoutParams getLongTextLayoutParams() {
        LinearLayout.LayoutParams lp;
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        return lp;
    }

    static public class ViewHolder {
        public TextView lable;
        TextView lableview;
        TextView timeView;
        TextView dateView;
        EditText alphaTextView;
        public EditText numericView;
        Spinner dataSpinner;
        Spinner unitsSpinner;
        //        ImageView indicatorView;
        CheckBox checkBox;
        RadioGroup radioGroup;
        public TextView prevReading;
        public TextView prevPercent1;
        public TextView prevPercent2;
        ImageView gpsButton;
        public TextView tvGpsCoordinates;

        public ImageView signatureView;
        public FlexboxLayout tvSignatureNames;
        public RecyclerView rvSignature;
        public SignatureAdapter signatureAdapter;

        RatingBar ratingView;

        LinearLayout numberPicker;
        LinearLayout llLayout;
        LinearLayout llHorizontalField;
        LinearLayout llCocBottlesCheckOptions;
        LinearLayout CheckGroup;

        public ImageView enableNotes;
        public ImageView parameterHintInfo;
        public ImageView enableForms, enableSampleTag;
        ImageButton enableParent;
        ImageButton printCOC;
        TextView tvZipCode;
        TextView tvAddTasks;
        public EditText tvBarCode;
        ImageView observedPhotos;
        public RecyclerView rvObservedPhotos;
        public ObservedPhotosAdapter observedPhotosAdapter;

        public FlexboxLayout new_actv;
    }

    public enum indicatorStatus {
        ImageStatusInvisible,
        ImageStausValid,
        ImageStatusInvalid
    }

    static public class DataHolder {
        public String value;
        public String units;
        public String baseUntis;
        public static boolean isnote_taken;
        public indicatorStatus imgStatus;
        public HashMap<String, String> nameValuePair;
        public String dhPrevReading1;
        public String dhPrevReading11;
        public String dhPrevReading2;
        List<String> names = null;
        public int lovID = 0;
        int l_itemID = 0;
        LovAdapaterHandler lovAdapter = null;

        //30-Nov-16
        int parentlovItmID = 0;
        int goto_formID = 0;
        int mandatoryFieldStatus = 0;

        public int getMandatoryFieldStatus() {

            return mandatoryFieldStatus;
        }

        public void setMandatoryFieldStatus(int mandatoryFieldStatus) {
            this.mandatoryFieldStatus = mandatoryFieldStatus;
        }

        public int getL_itemID() {
            return l_itemID;
        }

        public void setL_itemID(int l_itemID) {
            this.l_itemID = l_itemID;
        }

        public int getGoto_formID() {
            return goto_formID;
        }

        public void setGoto_formID(int goto_formID) {
            this.goto_formID = goto_formID;
        }

        public void setParentlovItemID(int id) {
            parentlovItmID = id;
        }

        public int getParentlovItemID() {
            return parentlovItmID;
        }

        public void setLovID(int id) {
            if (id != 0) {
                lovID = id;
            }
            lovAdapter = new LovAdapaterHandler(context);
        }

        public int getLovID() {
            return lovID;
        }

        public void setNameValuePair(LinkedHashMap<String, String> pair) {
            nameValuePair = pair;
            setItemNames();
        }

        public void setItemNames() {

            List<String> values = null;
            names = new ArrayList<String>();
            for (String key : nameValuePair.keySet()) {
                names.add(key);
            }
        }

        public List<String> getItemNames() {
            return names;
        }

        public int getPositionForValue(String value) {
            int position = -1;

            if ((value != null) && (value.length() != 0)) {

                String key = getKeyByValue(nameValuePair, value);

                if (names != null) {
                    for (int i = 0; i < names.size(); i++) {
                        if (names.get(i).equalsIgnoreCase(key)) {
                            position = i;
                            break;
                        }
                    }
                }
            }
            return position;
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {

            if (value.toString().trim().equalsIgnoreCase(entry.getValue().toString())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void setCalculatedFieldParams(final MetaData metaData,
                                          final EditText editText, ViewHolder viewHolder) {

        //Check if this visible condition doesn't bother in future.
        //I have added this condition to avoid calculating expression when field is hidden to faster form loading
/*        if (!metaData.isVisible)
            return;*/

        String value = null;

        String operand = metaData.getFieldParameterOperands();

        if (operand == null)
            return;

        if (operand.trim().isEmpty())
            return;

        if (attributes != null)
            if (attributes.getField_parameter_operands() != null
                    && !attributes.getField_parameter_operands().isEmpty())
                operand = attributes.getField_parameter_operands();

        //27-Jun-16 Direct Expression from FieldParameterOperands is Collected
        Log.i(TAG, "Expression:" + operand);

        //changed on 26 June, 21 added this for multiple queries that is separated by ~
        List<String> exprArray = Util.splitStringToArray("~", operand);

        for (String expression : exprArray) {
            try {
                //28-Jun-16 New Expression Parser Added
                value = calculateFromExpression(context, metaData, expression);

                if (expression != null && expression.contains("SAMPLE3") && value == null) {
                    CocDetailDataSource coc_dv = new CocDetailDataSource(context);
                    value = coc_dv.getSampleID_from_cocDetail(formActivity.currCocID, locationID,
                            metaData.getMetaParamID() + "");
                }
                Log.i(TAG, "Result of expression:" + value);
                if (value != null && ((value.contains("Error")) || value.isEmpty())) {
                    value = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "setCalculatedFieldParams error:" + e.getMessage());
            }

            if (editText != null && metaData.getMetaInputType().trim().contains("NUMERIC")) {
                if (expression != null && !expression.isEmpty()) {
                    try {
                        if (metaData.getMetaParamLabel().toLowerCase().contains("reference elevation")
                                && !(fmapObject.get(metaData.getMetaParamID() + "").value).equals(value)) {
                            fmapObject.get(metaData.getMetaParamID() + "").value = value;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (metaData.getMetaParamLabel().toLowerCase().contains("reference elevation"))
                            fmapObject.get(metaData.getMetaParamID() + "").value = value;
                    }

                    if (expression.contains("PurgeCalculation") || expression.contains("VolumePurged")) {
                        if (getCurrentSetID() > 1) {
                            String newValue = getLocaleFormattedString(value, Locale.getDefault());

                            if (value != null && !value.isEmpty())
                                editText.setText(newValue);
                            else editText.setText(value);
                        } else {

                            value = editText.getText().toString();

                            String newValue = getLocaleFormattedString(value, Locale.ENGLISH);

                            if (!value.isEmpty()) {
                                value = newValue;
                            }

                            editText.setEnabled(true);
                        }
                    } else {

                        String newValue = getLocaleFormattedString(value, Locale.getDefault());

                        if (value != null && !value.isEmpty()) {

                            if (!Util.containsAtLeastOneAlphabet(value)) {
                                value = getLocaleFormattedString(value, Locale.ENGLISH);
                                editText.setText(newValue);
                            } else if (expression.contains("SAMPLE") && !isSampleDateOrTimeSet) {
                                editText.setText(value);
                            } else {
                                editText.setText(value);
                            }
                        } /*else {
                    editText.setText(value);
                }*/ //commented on 12 March, 21 to avoid replacing null value in edittext to make sure filled doesn't replace

                        //commented on 10 March 21 to enable editing numeric value
//                editText.setEnabled(false);
                    }

                    editText.setFocusable(true);

                    String keyStr = (String) editText.getTag();
                    Log.i(TAG, "setCalculatedFieldParams() keyStr:" + keyStr);
                    handleNumericDataForCalculatedFields(metaData, value, editText);
                } else {
                    editText.setFocusable(true);
                }
            } else {
                if (value != null && !value.isEmpty() && viewHolder != null) {
                    if (metaData.getMetaInputType().trim().isEmpty()) {
                        if (viewHolder.lable != null) {
                            viewHolder.lable.setText(value);
                        }
                    } else if (metaData.getMetaInputType().trim().contains("LABEL")) {
                        if (viewHolder.lableview != null) {
                            viewHolder.lableview.setText(value);
                        }
                    } else if (metaData.getMetaInputType().trim().contains("TEXT")
                            || metaData.getMetaInputType().trim().contains("TEXTCONTAINER")) {
                        if (viewHolder.alphaTextView != null) {
                            viewHolder.alphaTextView.setText(value);
                        }
                    } else if (metaData.getMetaInputType().trim().contains("DATE")) {
                        if (viewHolder.dateView != null) {
                            viewHolder.dateView.setText(value);
                            saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
                        }
                    } else if (metaData.getMetaInputType().trim().contains("TIME")) {
                        if (viewHolder.timeView != null) {
                            viewHolder.timeView.setText(value);
                            saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
                        }
                    }
                }
            }
        }
    }

    private String getLocaleFormattedString(String value, Locale locale) {
        String convertedValue = "";

        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setGroupingUsed(false);
/*        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);*/

        try {
            if (value != null && !value.isEmpty()) {
                if (value.contains(".") || value.contains(",")) {

                    //added this on 12 Jan, 23 as client wanted trailing zero in case eg. 5.70 is filled it shouldn't convert to 5.7
/*                    int decimalCount = Util.getDecimalPlaces(value);
                    if (decimalCount == 2)
                        nf.setMinimumFractionDigits(decimalCount);
                    else
                        nf.setMinimumFractionDigits(decimalPlaces);*/

//                    nf.setMaximumFractionDigits(5);
                }
                value = value.replaceAll(",", ".");
/*                if (Util.hasDigitDecimalOnly(value))
                    convertedValue = nf.format(Double.parseDouble(value));*/
                convertedValue = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        decimalPlaces = 0;
        return convertedValue;
    }

    public void handleNumericData(final MetaData metaData, String value,
                                  final EditText numericView, boolean showAlert) {

        DataHolder tempData = fmapObject.get(metaData.getMetaParamID() + "");
        tempData.value = value;
        DefaultValueDataSource dv = new DefaultValueDataSource(context);

        Double val = 0d;

        String msg = null;

        if ((value != null) && (value.length() > 0)) {
            try {
                val = Double.parseDouble(value);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //09-Sep-16
            DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID,
                    metaData.getCurrentFormID() + "", metaData.getMetaParamID() + "",
                    formActivity.getCurSetID() + "");

            Double warninghighlimit = null;
            Double warninglowlimit = null;

            String finalWarn_low = "";
            String finalWarn_high = "";

            if (d_model != null) {
                String high = d_model.getHighLimitDefaultValue();
                String low = d_model.getLowLimitDefaultValue();
                String warn_low = d_model.getWarningLowDefaultValue();
                String warn_high = d_model.getWarningHighDefaultValue();

                finalWarn_low = warn_low;
                finalWarn_high = warn_high;

                if (metaData.getMetaInputType().equalsIgnoreCase("TOTALIZER")) {

                    if (warn_low == null || warn_low.isEmpty()) {
                        //store entered value in database
//                        String updatedWarningLowValue = numericView.getText().toString();
                        dv.insertNewWarningLowValue(locationID, metaData.getCurrentFormID()
                                + "", metaData.getMetaParamID() + "", value);
                    } else {
                        double dbWarningLow = 0;
                        dbWarningLow = Double.parseDouble(warn_low);

//                        String updatedWarningLowValue = numericView.getText().toString();
                        double updatedWarningLow = Double.parseDouble(value);

                        if (updatedWarningLow > dbWarningLow) {
                            //if entered value is greater than value stored in database then update new value to database
                            dv.updateWarningLowValue(locationID,
                                    metaData.getCurrentFormID() + "", metaData.getMetaParamID() + "", value);
                        } else {
                            //if entered value is less than stored value then show warning message to user and clear text field
                            msg = "Value is Low.\n Value should be greater than: " + dbWarningLow;

                            if (builder == null) {
                                builder = new AlertDialog.Builder(context);
                            }
                            builder.setTitle("Alert");
                            builder.setMessage("" + msg);
                            builder.setCancelable(true);
                            String final_warn_low = finalWarn_low;
                            String final_warn_high = finalWarn_high;
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!STORE_VALUE) {
                                        //numericView.setText(null);
                                        numericView.setText("");
                                    } else {
                                        try {
                                            ViewHolder viewHolder
                                                    = (ViewHolder) metaData.getForm_field_row().getTag();
                                            if (viewHolder.lable.getText().toString().contains("Range")) {
                                                String styledText = "<font color='red'>" + metaData.ParamLabel
                                                        + "</font>\n" + "<font color='red'><small>Range: "
                                                        + final_warn_low + " to " + final_warn_high + "</small></font>";
                                                Spanned result;
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                                                } else {
                                                    result = Html.fromHtml(styledText);
                                                }

                                                viewHolder.lable.setText(result);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        numericView.setTextColor(ContextCompat.getColor(formActivity, R.color.red));
                                    }
                                }
                            });
                            if (alertdialog == null || !alertdialog.isShowing()) {
                                alertdialog = builder.create();
                                alertdialog.show();
                            }
                        }
                    }
                } else {
                    //here code for remaining numeric field will be added apart from TOTALIZER
                    if (high == null || high.isEmpty()) {
                        if (metaData.getRoutineId() == 111) {
                            high = String.valueOf(metaData.getMetaHighLimit());
                            highlimitdefault = Double.valueOf(high);
                        } else {
                            highlimitdefault = null;
                        }
                    } else {
                        highlimitdefault = Double.valueOf(high);
                    }
                    if (low == null || low.trim().isEmpty()) {
                        if (metaData.getRoutineId() == 111) {
                            low = String.valueOf(metaData.getMetaLowLimit());
                            lowlimitdefault = Double.valueOf(low);
                        } else {
                            lowlimitdefault = null;
                        }
                    } else {
                        lowlimitdefault = Double.valueOf(low);
                    }
                    if (warn_low == null || warn_low.isEmpty()) {
                        if (metaData.getRoutineId() == 111) {
                            warn_low = String.valueOf(metaData.getMetaWarningLow());
                            warninglowlimit = Double.valueOf(warn_low);
                        } else {
                            warninglowlimit = null;
                        }
                    } else {
                        warninglowlimit = Double.valueOf(warn_low);
                    }
                    if (warn_high == null || warn_high.isEmpty()) {
                        if (metaData.getRoutineId() == 111) {
                            warn_high = String.valueOf(metaData.getMetaWarningHigh());
                            warninghighlimit = Double.valueOf(warn_high);
                        } else {
                            warninghighlimit = null;
                        }
                    } else {
                        warninghighlimit = Double.valueOf(warn_high);
                    }
                }
            }

            //01 Dec, 21 By Sanket - Added this condition as sometimes both default may have zero which cause
            //condition "val < lowlimitdefault" below in else to run when added a negative value to the field
            boolean bothDefaultHasZero = (highlimitdefault != null && lowlimitdefault != null)
                    && (highlimitdefault == 0 && lowlimitdefault == 0);

            boolean bothWarningHasZero = (warninghighlimit != null && warninglowlimit != null
                    && warninghighlimit == 0.0 && warninglowlimit == 0.0);


            if (bothDefaultHasZero) {
                highlimitdefault = null;
                lowlimitdefault = null;
            }

            if (bothWarningHasZero) {
                warninghighlimit = null;
                warninglowlimit = null;
            }

            //24-Oct-16 Code By Yogendra
            if (highlimitdefault == null && lowlimitdefault == null) {
                STORE_VALUE = true;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present
                    // 24-Oct-16 Store value as it is (No validation)
                } else if (warninghighlimit == null) {//24-Oct-16 x to Positive Values

                    if (val >= warninglowlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be greater than: " + warninglowlimit;
                    }
                } else if (warninglowlimit == null) {

                    if (val <= warninghighlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        STORE_VALUE = true;
                        msg = "Value is High.\nValue should be less than: " + warninghighlimit;
                    }
                } else {//24-Oct-16 Both are present
                    if (val <= warninghighlimit && val >= warninglowlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        STORE_VALUE = true;
                        msg = "Value should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                        //msg = "Value outside the range: " + warninglowlimit + " to " + warninghighlimit;
                    }
                }
            } else if (highlimitdefault == null) {//24-Oct-16 x to Positive Values
                STORE_VALUE = false;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present

                    if (val >= lowlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true;
                    } else {
                        //Not valid
                        msg = "Value is Low.\nValue should be greater than: " + lowlimitdefault;
                    }
                } else if (warninghighlimit == null) {//24-Oct-16 x to Positive Values

                    if (val >= warninglowlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                    } else if (val >= lowlimitdefault && val < warninglowlimit) {
                        //Not valid
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be greater than: " + warninglowlimit;
                    } else if (val < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value should be greater than: " + lowlimitdefault;
                    }
                } else if (warninglowlimit == null) {

                    if (val < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value should be in between the range: " + lowlimitdefault + " To " + warninghighlimit;

                    } else if (val >= warninghighlimit) {
                        STORE_VALUE = true;
                        msg = "Value should be in between the range: " + lowlimitdefault + " To " + warninghighlimit;
                    } else if (val >= lowlimitdefault && val <= warninghighlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                    }
                } else {

                    if (val < lowlimitdefault) {
                        STORE_VALUE = false;
//                        msg = "Value should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                        msg = "Value should be greater than: " + lowlimitdefault;

                    } else if (val >= lowlimitdefault && val <= warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    } else if (val >= warninglowlimit && val <= warninghighlimit) {
                        STORE_VALUE = true;
                    } else if (val > warninghighlimit) {
                        STORE_VALUE = true;
                        msg = "Value is High.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    }
                }
            } else if (lowlimitdefault == null) {//24-Oct-16  x to Negative values

                STORE_VALUE = false;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present

                    if (val <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true;
                    } else {
                        //Not valid
                        STORE_VALUE = false;
                        msg = "Value is High.\nValue should be less than: " + highlimitdefault;
                    }
                } else if (warninghighlimit == null) {// 24-Oct-16 x to Positive Values

                    if (val <= warninglowlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be in between the range: " + warninglowlimit + " To " + highlimitdefault;

                    } else if (val <= highlimitdefault && val >= warninglowlimit) {
                        //Not valid
                        STORE_VALUE = true;
                    } else if (val > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value is High.\nValue should be less than: " + highlimitdefault;
//                      msg = "Value is High.\nValue should be in between the range: " + warninglowlimit + " To " + highlimitdefault;
                    }
                } else if (warninglowlimit == null) {

                    if (val > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value should be less than: " + highlimitdefault;
                    } else if (val <= warninghighlimit) {
                        STORE_VALUE = true;
                    } else if (val <= highlimitdefault && val >= warninghighlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                        msg = "Value is High.\nValue should be less than: " + warninghighlimit;
                    }
                } else {
                    if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be less than: " + highlimitdefault;
//                        msg = "Value should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    } else if (val <= highlimitdefault && val >= warninghighlimit) {
                        STORE_VALUE = true;
                        msg = "Value is High.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    } else if (val >= warninglowlimit && val <= warninghighlimit) {
                        STORE_VALUE = true;
                    } else if (val < warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    }
                }
            } else {//24-Oct-16 Both are present
                STORE_VALUE = false;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present

                    if (val >= lowlimitdefault && val <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true;
                    } else {
                        //Not valid No.
                        STORE_VALUE = false;
                        msg = " Value should be in between the range: " + lowlimitdefault + " To " + highlimitdefault;
                    }
                } else if (warninglowlimit == null) {
                    if (val > warninghighlimit && val <= highlimitdefault) {
                        STORE_VALUE = true;
                        msg = "Value is High.\nValue should be in between the range: " + lowlimitdefault + " To " + warninghighlimit;
                    } else if (val >= lowlimitdefault && val <= warninghighlimit) {
                        STORE_VALUE = true;
                    } else if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range: " + lowlimitdefault + " To " + warninghighlimit;

                    } else if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range: " + lowlimitdefault + " To " + warninghighlimit;
                    }
                } else if (warninghighlimit == null) {
                    if (val >= lowlimitdefault && val < warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be in between the range: " + warninglowlimit + " To " + highlimitdefault;
                    } else if (val >= warninglowlimit && val <= highlimitdefault) {
                        STORE_VALUE = true;
                    } else if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range: " + warninglowlimit + " To " + highlimitdefault;
                    } else if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range: " + warninglowlimit + " To " + highlimitdefault;
                    }
                } else {
                    //24-Oct-16 High,low,warn_high n warn_low  all has value
                    if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range: " + lowlimitdefault + " To " + highlimitdefault;
                    } else if (val >= lowlimitdefault && val < warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    } else if (val >= warninglowlimit && val <= warninghighlimit) {
                        STORE_VALUE = true;
                    } else if (val > warninghighlimit && val <= highlimitdefault) {
                        STORE_VALUE = true;
                        msg = "Value is High.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                    } else if (val > highlimitdefault) {
                        //STORE_VALUE = false;
                        if (val > warninghighlimit && val > 0.0 && highlimitdefault == 0.0) {
                            STORE_VALUE = true;
                            msg = "Value is High.\nValue should be in between the range: " + warninglowlimit + " To " + warninghighlimit;
                        } else {
                            STORE_VALUE = false; //Store value status change to true initially it was false on 28 may
                            msg = "Value should be in between the range: " + lowlimitdefault + " To " + highlimitdefault;
                        }
                    }
                }
            }

            String finalWarn_low1 = finalWarn_low;
            String finalWarn_high1 = finalWarn_high;

            //considering if there is a message then of course it is prompting user that value
            //is not between range means it has been violated then 1 else 0
            String violationFlag = "0";

            //setting label to normal color if value entered is between range
            if (msg == null) {
                violationFlag = "0";

                try {
                    if (metaData.getForm_field_row() != null) {
                        ViewHolder viewHolder
                                = (ViewHolder) metaData.getForm_field_row().getTag();
                        if (viewHolder.lable.getText().toString().contains("Range")
                                && !finalWarn_low1.isEmpty() && !finalWarn_high1.isEmpty()) {
                            String styledText = metaData.ParamLabel + "\n" + "<font color='blue'><small>Range: "
                                    + finalWarn_low1 + " to " + finalWarn_high1 + "</small></font>";
                            Spanned result;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                            } else {
                                result = Html.fromHtml(styledText);
                            }

                            viewHolder.lable.setText(result);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg != null && showAlert) {
                violationFlag = "1";
                if (builder == null) {
                    builder = new AlertDialog.Builder(context);
                }

                builder.setTitle("Alert");
                builder.setMessage("" + msg + " for field \""
                        + metaData.getMetaParamLabel() + "\"");
                builder.setCancelable(true);
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!STORE_VALUE) {
                            //numericView.setText(null);
                            numericView.setText("");
                        } else {
                            try {
                                ViewHolder viewHolder
                                        = (ViewHolder) metaData.getForm_field_row().getTag();
                                if (finalWarn_low1 != null && finalWarn_high1 != null)
                                    if (viewHolder.lable.getText().toString().contains("Range")
                                            && !finalWarn_low1.isEmpty()
                                            && !finalWarn_high1.isEmpty()) {
                                        String styledText = "<font color='red'>" + metaData.ParamLabel
                                                + "</font>\n" + "<font color='blue'><small>Range: "
                                                + finalWarn_low1 + " to " + finalWarn_high1 + "</small></font>";
                                        Spanned result;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            result = Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY);
                                        } else {
                                            result = Html.fromHtml(styledText);
                                        }

                                        viewHolder.lable.setText(result);
                                    }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            numericView.setTextColor(ContextCompat.getColor(formActivity, R.color.red));
                        }
                    }
                });

                if (alertdialog == null || !alertdialog.isShowing()) {
                    alertdialog = builder.create();
                    alertdialog.show();
                }
            } else if ((value != null && value.length() != 0) && msg == null) {
                tempData.imgStatus = indicatorStatus.ImageStausValid;
            } else {
                tempData.imgStatus = indicatorStatus.ImageStatusInvisible;
            }

            FieldDataSource fieldDataSource = new FieldDataSource(context);

            fieldDataSource.updateViolationFlag(eventID, metaData.getMetaParamID(),
                    formActivity.getCurSetID(), locationID, violationFlag,
                    Integer.parseInt(siteID), formActivity.getCurrentAppID());

            if (fieldDataSource.isParamIdExists(getCurrentSetID(), Integer.parseInt(eventID),
                    locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                    deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {

                if (STORE_VALUE) {
                    saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
                } else {
                    saveData_and_updateCreationDate(context, metaData, null, getCurrentSetID());
                }
            } else {

                if (STORE_VALUE) {
                    List<FieldData> bfieldData = getBlankFieldData(context, metaData, getCurrentSetID());
                    fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID + "");
                    saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
                } else {
                    List<FieldData> bfieldData = getBlankFieldData(context, metaData, getCurrentSetID());
                    fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID + "");
                    saveData_and_updateCreationDate(context, metaData, null, getCurrentSetID());
                }
            }
        } else {
            saveData_and_updateCreationDate(context, metaData, tempData.value, getCurrentSetID());
        }
//        handleLast2Reading_And_Percentage(metaData, numericView, viewHolder);
    }

    public void handleNumericDataForCalculatedFields(MetaData metaData, String value, EditText numericEditText) {
        Log.d("Has editText", "is null " + numericEditText);
//        LocationdetailAdapter.DataHolder tempData = fmapObject.get(keyStr);
        Double val = (double) 0;

        String msg = null;

        if ((value != null) && (value.length() != 0) && Util.hasDigitDecimalOnly(value)) {
            try {
                val = Double.parseDouble(value);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //09-Sep-16
            DefaultValueDataSource dv = new DefaultValueDataSource(context);
            DefaultValueModel d_model = dv.getDefaultValueToWarn(locationID,
                    metaData.getCurrentFormID() + "", metaData.getMetaParamID() + "",
                    formActivity.getCurSetID() + "");

            Double warninghighlimit = null;
            Double warninglowlimit = null;

            if (d_model != null) {
                String high = d_model.getHighLimitDefaultValue();
                String low = d_model.getLowLimitDefaultValue();
                String warn_low = d_model.getWarningLowDefaultValue();
                String warn_high = d_model.getWarningHighDefaultValue();

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
                }*/

                if (high == null || high.isEmpty()) {
                    if (metaData.getRoutineId() == 111) {
                        high = String.valueOf(metaData.getMetaHighLimit());
                        highlimitdefault = Double.valueOf(high);
                    } else {
                        highlimitdefault = null;
                    }
                } else {
                    highlimitdefault = Double.valueOf(high);
                }

                if (low == null || low.isEmpty()) {
                    if (metaData.getRoutineId() == 111) {
                        low = String.valueOf(metaData.getMetaLowLimit());
                        lowlimitdefault = Double.valueOf(low);
                    } else {
                        lowlimitdefault = null;
                    }
                } else {
                    lowlimitdefault = Double.valueOf(low);
                }

                if (warn_low == null || warn_low.isEmpty()) {
                    if (metaData.getRoutineId() == 111) {
                        warn_low = String.valueOf(metaData.getMetaWarningLow());
                        warninglowlimit = Double.valueOf(warn_low);
                    } else {
                        warninglowlimit = null;
                    }
                } else {
                    warninglowlimit = Double.valueOf(warn_low);
                }
                if (warn_high == null || warn_high.isEmpty()) {
                    if (metaData.getRoutineId() == 111) {
                        warn_high = String.valueOf(metaData.getMetaWarningHigh());
                        warninghighlimit = Double.valueOf(warn_high);
                    } else {
                        warninghighlimit = null;
                    }
                } else {
                    warninghighlimit = Double.valueOf(warn_high);

                }
            }

            boolean bothDefaultHasZero = (highlimitdefault != null && lowlimitdefault != null
                    && highlimitdefault == 0.0 && lowlimitdefault == 0.0);

            boolean bothWarningHasZero = (warninghighlimit != null && warninglowlimit != null
                    && warninghighlimit == 0.0 && warninglowlimit == 0.0);


            if (bothDefaultHasZero) {
                highlimitdefault = null;
                lowlimitdefault = null;
            }

            if (bothWarningHasZero) {
                warninghighlimit = null;
                warninglowlimit = null;
            }

            //24-Oct-16 Code By Yogendra
            if ((highlimitdefault == null && lowlimitdefault == null)) {

                STORE_VALUE = true;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present
                    //24-Oct-16 Store value as it is (No validation)
                } else if (warninghighlimit == null) {//24-Oct-16 x to Positive Values

                    if (val >= warninglowlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        msg = "Value is Low.\n Value should be greater than :" + warninglowlimit;
                    }
                } else if (warninglowlimit == null) {

                    if (val <= warninghighlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        msg = "Value is High.\n Value should be less than :" + warninghighlimit;
                    }
                } else {//24-Oct-16 Both are present
                    if (val <= warninghighlimit && val >= warninglowlimit) {
                        //Valid No.
                    } else {
                        //Not valid
                        msg = "Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;
                    }
                }
            } else if (highlimitdefault == null) {//24-Oct-16 x to Positive Values
                STORE_VALUE = false;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present

                    if (val >= lowlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true;

                    } else {
                        //Not valid
                        msg = "Value is Low.\n Value should be greater than :" + lowlimitdefault;
                    }

                } else if (warninghighlimit == null) {//24-Oct-16 x to Positive Values

                    if (val >= warninglowlimit) {
                        //Valid No.
                        STORE_VALUE = true;

                    } else if (val >= lowlimitdefault && val < warninglowlimit) {
                        //Not valid
                        STORE_VALUE = true;

                        msg = "Value is Low.\n Value should be greater than :" + warninglowlimit;

                    } else if (val < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value is Low.\n Value should be greater than :" + warninglowlimit;
                    }
                } else if (warninglowlimit == null) {

                    if (val < lowlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value should be in between the range :" + lowlimitdefault + " To " + warninghighlimit;

                    } else if (val >= warninghighlimit) {
                        STORE_VALUE = true;
                        msg = "Value should be in between the range :" + lowlimitdefault + " To " + warninghighlimit;

                    } else if (val >= lowlimitdefault && val <= warninghighlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                    }
                } else {

                    if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;

                    } else if (val >= lowlimitdefault && val <= warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;

                    } else if (val >= warninglowlimit && val <= warninghighlimit) {
                        STORE_VALUE = true;

                    } else if (val > warninghighlimit) {
                        STORE_VALUE = true;

                        msg = "Value is High.Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;

                    }

                }

            } else if (lowlimitdefault == null) {//24-Oct-16  x to Negative values

                STORE_VALUE = false;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present

                    if (val <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true;

                    } else {
                        //Not valid
                        msg = "Value is High.\n Value should be less than :" + highlimitdefault;


                    }

                } else if (warninghighlimit == null) {//24-Oct-16 x to Positive Values

                    if (val <= warninglowlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                        msg = "Value is Low.\n Value should be in between the range :" + warninglowlimit + " To " + highlimitdefault;

                    } else if (val <= highlimitdefault && val >= warninglowlimit) {
                        //Not valid
                        STORE_VALUE = true;


                    } else if (val > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value is High.\n Value should be in between the range :" + warninglowlimit + " To " + highlimitdefault;

                    }
                } else if (warninglowlimit == null) {

                    if (val > highlimitdefault) {
                        //Not Valid No.
                        STORE_VALUE = false;
                        msg = "Value should be less than:" + warninghighlimit;

                    } else if (val <= warninghighlimit) {
                        STORE_VALUE = true;


                    } else if (val <= highlimitdefault && val >= warninghighlimit) {
                        //Valid No.
                        STORE_VALUE = true;
                        msg = "Value should be less than:" + warninghighlimit;

                    }

                } else {

                    if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;

                    } else if (val <= highlimitdefault && val >= warninghighlimit) {
                        STORE_VALUE = true;
                        msg = "Value is High.Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;

                    } else if (val >= warninglowlimit && val <= warninghighlimit) {
                        STORE_VALUE = true;

                    } else if (val < warninglowlimit) {
                        STORE_VALUE = true;

                        msg = "Value is Low.Value should be in between the range :" + warninglowlimit + " To " + warninghighlimit;

                    }

                }


            } else {//24-Oct-16 Both are present
                STORE_VALUE = false;

                if (warninghighlimit == null && warninglowlimit == null) {//24-Oct-16 no high-low and warning high-low present

                    if (val >= lowlimitdefault && val <= highlimitdefault) {
                        //Valid No.
                        STORE_VALUE = true;

                    } else {
                        //Not valid No.
                        STORE_VALUE = false;

                        msg = " Value should be in between the range  :" + lowlimitdefault + " To " + highlimitdefault;

                    }

                } else if (warninglowlimit == null) {
                    if (val > warninghighlimit && val <= highlimitdefault) {
                        STORE_VALUE = true;
                        msg = "Value is High.Value should be in between the range  :" + lowlimitdefault + " To " + warninghighlimit;

                    } else if (val >= lowlimitdefault && val <= warninghighlimit) {
                        STORE_VALUE = true;
                    } else if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value is Low.Value should be in between the range  :" + lowlimitdefault + " To " + warninghighlimit;

                    } else if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value is High.Value should be in between the range  :" + lowlimitdefault + " To " + warninghighlimit;

                    }

                } else if (warninghighlimit == null) {
                    if (val >= lowlimitdefault && val < warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.Value should be in between the range  :" + warninglowlimit + " To " + highlimitdefault;

                    } else if (val >= warninglowlimit && val <= highlimitdefault) {
                        STORE_VALUE = true;
                    } else if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value is High.Value should be in between the range  :" + warninglowlimit + " To " + highlimitdefault;
                    } else if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value is Low.Value should be in between the range  :" + warninglowlimit + " To " + highlimitdefault;

                    }
                } else {

                    //24-Oct-16 High,low,warn_high n warn_low  all has value

                    if (val < lowlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value is Low.Value should be in between the range  :" + warninglowlimit + " To " + warninghighlimit;

                    } else if (val >= lowlimitdefault && val < warninglowlimit) {
                        STORE_VALUE = true;
                        msg = "Value is Low.Value should be in between the range  :" + warninglowlimit + " To " + warninghighlimit;

                    } else if (val >= warninglowlimit && val <= warninghighlimit) {
                        STORE_VALUE = true;

                    } else if (val > warninghighlimit && val <= highlimitdefault) {
                        STORE_VALUE = true;
                        msg = "Value is High.Value should be in between the range  :" + warninglowlimit + " To " + warninghighlimit;
                    } else if (val > highlimitdefault) {
                        STORE_VALUE = false;
                        msg = "Value is High.Value should be in between the range  :" + warninglowlimit + " To " + warninghighlimit;
                    }
                }
            }

            if ((msg != null)) {
                //   msg += " Range b/n :" + metaData.WarningLow + " To " + metaData.WarningHigh;
                // msg += " Range b/n :" + metaData.WarningLow + " To " + metaData.WarningHigh;

                if (builder == null) {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Alert");
                builder.setMessage("" + msg + " for field \""
                        + metaData.getMetaParamLabel() + "\"");
                builder.setCancelable(true);
                builder.setNeutralButton("OK", null);
                if (alertdialog == null || !alertdialog.isShowing()) {
                    alertdialog = builder.create();
                    alertdialog.show();
                }

                FieldDataSource fieldDataSource = new FieldDataSource(context);

                if (fieldDataSource.isParamIdExists(getCurrentSetID(), Integer.parseInt(eventID),
                        locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                        deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {

                    if (STORE_VALUE) {
                        saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
                    } else {
                        saveData_and_updateCreationDate(context, metaData, null, getCurrentSetID());
                    }
                } else {
                    if (STORE_VALUE) {
                        List<FieldData> bfieldData = getBlankFieldData(context, metaData, getCurrentSetID());
                        fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID + "");
                        saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
                    } else {
                        List<FieldData> bfieldData = getBlankFieldData(context, metaData, getCurrentSetID());
                        fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID + "");
                        saveData_and_updateCreationDate(context, metaData, null, getCurrentSetID());
                    }
                }
            } else {
                saveData_and_updateCreationDate(context, metaData, value, getCurrentSetID());
            }
        } else {
            FieldDataSource fieldDataSource = new FieldDataSource(context);

            if (!fieldDataSource.isParamIdExists(getCurrentSetID(), Integer.parseInt(eventID),
                    locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                    deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {
                List<FieldData> bfieldData = getBlankFieldData(context, metaData, getCurrentSetID());
                fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID + "");
            }

            if (numericEditText.getText().toString().isEmpty())
                saveData_and_updateCreationDate(context, metaData, null, getCurrentSetID());
        }
    }

    public String calculateFromExpression(Context ObjContext, MetaData metaData, String
            expression) {
        FieldDataSource fp = new FieldDataSource(ObjContext);

//        String expression = metaData.getFieldParameterOperands();//27-Jun-16 Direct Expression from FieldParameterOperands is Collected
        Log.i("calculateFromExpression", "InPut Expression:" + expression);

        String lhs;
        String rhs;
//      expression="(({310}#0#|1235|-{310}#1#|1235|)/((@{310}#0#|15|@)-(@{310}#-1#|15|@)))";

        if (expression != null && !expression.isEmpty()) {

            //IF started
            if (expression.contains("IF") && !expression.contains("!!set!!")) {
                String lhsValue = "0";
                String mobAppID = "";
                int setID = 0;
                String f_paramID = "";

                lhs = expression.substring(0, expression.indexOf(")"));

                if (lhs.contains("{")) {
                    mobAppID = lhs.substring(lhs.indexOf("{") + 1, lhs.lastIndexOf("}"));
                }

                if (lhs.contains("#"))
                    setID = Integer.parseInt(lhs.substring(lhs.indexOf("#") + 1, lhs.lastIndexOf("#")));
                if (lhs.contains("|"))
                    f_paramID = lhs.substring(lhs.indexOf("|") + 1, lhs.lastIndexOf("|"));

                if (f_paramID != null && !f_paramID.isEmpty()) {
                    if (setID <= 0) {

                        if (setID < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + setID;
                            setID = res;

                            if (setID <= 0) {
                                lhsValue = null;
                            } else {
                                lhsValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                        (mobAppID == null || mobAppID.isEmpty()) ? metaData.getCurrentFormID() : Integer.parseInt(mobAppID),
                                        setID, f_paramID);
                            }

                        } else if (setID == 0) {
                            setID = getCurrentSetID();

                            lhsValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                    (mobAppID == null || mobAppID.isEmpty()) ? metaData.getCurrentFormID() : Integer.parseInt(mobAppID),
                                    setID, f_paramID);
                        }


                    } else {
                        lhsValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                (mobAppID == null || mobAppID.isEmpty()) ? metaData.getCurrentFormID() : Integer.parseInt(mobAppID),
                                setID, f_paramID);
                    }

                } else {

                    String pattern = "\\d+";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(lhs);

                    while (m.find()) {
                        lhsValue = lhs.substring(m.start(), m.end());
                    }

                }

                rhs = expression.substring(expression.indexOf(")"));

                String operator = rhs.substring(rhs.indexOf("$") + 1, rhs.lastIndexOf("$"));
                String conditionOperand = rhs.substring(rhs.lastIndexOf("$") + 1, rhs.lastIndexOf("?"));
                String conditionTrueValue = rhs.substring(rhs.lastIndexOf("?") + 1, rhs.lastIndexOf(":"));
                String conditionFalseValue = rhs.substring(rhs.lastIndexOf(":") + 1);


                if (conditionOperand.contains("|")) {
                    String mobApp = "", fpID;
                    int set = 0;
                    if (conditionOperand.contains("{")) {
                        mobApp = conditionOperand.substring(conditionOperand.indexOf("{") + 1, conditionOperand.lastIndexOf("}"));
                    }

                    if (conditionOperand.contains("#")) {
                        set = Integer.parseInt(conditionOperand.substring(conditionOperand.indexOf("#") + 1, conditionOperand.lastIndexOf("#")));
                    }

                    fpID = conditionOperand.substring(conditionOperand.indexOf("|") + 1, conditionOperand.lastIndexOf("|"));

                    if (mobApp == null || mobApp.isEmpty()) {
                        mobApp = String.valueOf(metaData.getCurrentFormID());
                    }

                    if (set <= 0) {

                        if (set < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + set;
                            set = res;

                            if (set <= 0) {
                                conditionOperand = null;
                            } else {
                                conditionOperand = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                            }
                        } else if (set == 0) {
                            set = getCurrentSetID();
                            conditionOperand = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                        }
                    } else {
                        conditionOperand = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                    }
                } else {
                    String pattern = "\\d+";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(conditionOperand);

                    while (m.find()) {
                        conditionOperand = conditionOperand.substring(m.start(), m.end());
                    }
                }

                if (conditionTrueValue.contains("|")) {
                    String mobApp = "", fpID;
                    int set = 0;

                    if (conditionTrueValue.contains("{")) {
                        mobApp = conditionTrueValue.substring(conditionTrueValue.indexOf("{") + 1, conditionTrueValue.lastIndexOf("}"));
                    }

                    if (conditionTrueValue.contains("#")) {
                        set = Integer.parseInt(conditionTrueValue.substring(conditionTrueValue.indexOf("#") + 1, conditionTrueValue.lastIndexOf("#")));
                    }

                    if (mobApp.isEmpty()) {
                        mobApp = String.valueOf(metaData.getCurrentFormID());
                    }

                    fpID = conditionTrueValue.substring(conditionTrueValue.indexOf("|") + 1, conditionTrueValue.lastIndexOf("|"));

                    if (set <= 0) {

                        if (set < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + set;
                            set = res;

                            if (set <= 0) {
                                conditionTrueValue = null;
                            } else {
                                conditionTrueValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                        Integer.parseInt(mobApp), set, fpID);
                            }

                        } else if (set == 0) {
                            set = getCurrentSetID();
                            conditionTrueValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                        }
                    } else {
                        conditionTrueValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                    }
                } else {
                    String pattern = "\\d+";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(conditionTrueValue);

                    while (m.find()) {
                        conditionTrueValue = conditionTrueValue.substring(m.start(), m.end());
                    }
                }

                if (conditionFalseValue.contains("|")) {
                    String mobApp = "0", fpID;
                    int set = 0;

                    if (conditionFalseValue.contains("{")) {
                        mobApp = conditionFalseValue.substring(conditionFalseValue.indexOf("{") + 1, conditionFalseValue.lastIndexOf("}"));

                    }
                    if (conditionFalseValue.contains("#")) {
                        set = Integer.parseInt(conditionFalseValue.substring(conditionFalseValue.indexOf("#") + 1, conditionFalseValue.lastIndexOf("#")));
                    }

                    if (mobApp.equals("0")) {
                        mobApp = String.valueOf(metaData.getCurrentFormID());
                    }

                    fpID = conditionFalseValue.substring(conditionFalseValue.indexOf("|") + 1, conditionFalseValue.lastIndexOf("|"));

                    if (set <= 0) {

                        if (set < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + set;
                            set = res;

                            if (set <= 0) {
                                conditionFalseValue = null;
                            } else {
                                conditionFalseValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                            }
                        } else if (set == 0) {
                            set = getCurrentSetID();
                            conditionFalseValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                        }
                    } else {
                        conditionFalseValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                    }
                } else {

                    String pattern = "\\d+";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(conditionFalseValue);

                    while (m.find()) {
                        conditionFalseValue = conditionFalseValue.substring(m.start(), m.end());
                    }
                }

                String res = "NA";

                try {
                    if ((lhsValue == null || lhsValue.isEmpty()) || (conditionOperand == null || conditionOperand.isEmpty()) ||
                            (conditionTrueValue == null || conditionTrueValue.isEmpty() || conditionFalseValue == null || conditionFalseValue.isEmpty())) {
                        Log.i(TAG, "Not getting all arguments to build a conditional expression");
                        return res;
                    } else {
                        res = buildConditionalExpressionResult(Double.parseDouble(lhsValue), operator, Double.parseDouble(conditionOperand), conditionTrueValue, conditionFalseValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "calculateFromExpression (IF condition):" + e.getMessage());
                    return res;
                }
                return res;
            }
            //IF end

            //SAMPLE3 started
            else if (expression.contains("SAMPLE3")) {
                String dateString = null;
                String timeString = null;
                String resultString = null;
                String s3subexpression = expression.substring(expression.indexOf("(") + 1, expression.lastIndexOf(")"));
                String[] s3FpIds = s3subexpression.split(",");

                if (s3subexpression.contains("|")) {
                    String mobApp = String.valueOf(metaData.getCurrentFormID());
                    String datefpID;
                    String timeFpID;
                    int set = getCurrentSetID();

                    if (s3FpIds[0] != null && !s3FpIds[0].isEmpty()) {
                        datefpID = s3FpIds[0].substring(s3FpIds[0].indexOf("|") + 1,
                                s3FpIds[0].lastIndexOf("|"));
                        dateString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                Integer.parseInt(mobApp), set, datefpID);
                    }

                    if (s3FpIds[1] != null && !s3FpIds[1].isEmpty()) {
                        timeFpID = s3FpIds[1].substring(s3FpIds[1].indexOf("|") + 1,
                                s3FpIds[1].lastIndexOf("|"));
                        timeString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                                Integer.parseInt(mobApp), set, timeFpID);
                    }

                    try {
                        if ((dateString != null) && (!dateString.equals("0"))) {
                            LocationDataSource locSource = new LocationDataSource(ObjContext);
                            SimpleDateFormat fromFormat = new SimpleDateFormat("mm/dd/yyyy");
                            SimpleDateFormat toFormat = new SimpleDateFormat("mmddyy");
                            String newString = null;

                            try {
                                newString = toFormat.format(fromFormat.parse(dateString));
                                //03-Jan-16
//                                int quarter = Util.getQuarter(newString);
//                                String[] yr = newString.split("/");
//                                newString = "-" + quarter + yr[2];//Q[2][92]
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String replaced_locationstring = locSource.getLocationName(locationID)
                                    .replace("/", "-");
                            if (replaced_locationstring.endsWith("-")) {
                                resultString = replaced_locationstring + "GW-" +
                                        newString;//LocationName(replace "/" by "-")-mmddyy
                            } else {
                                resultString = replaced_locationstring + "-GW-" +
                                        newString;//LocationName(replace "/" by "-")-mmddyy
                            }
                        } else {
                            resultString = null;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in SampleID3 Calculation:" + e.getMessage());
                    }
                }

                if (resultString != null && formActivity.currCocID != null
                        && !formActivity.currCocID.equalsIgnoreCase("0")) {
                    //19-03-2018 COC
                    handleSample3Data(dateString, timeString, resultString);
                }

                return resultString;
            }
            //SAMPLE3 end

            //SAMPLE2 started
            else if (expression.contains("SAMPLE2")) {
                String resultString = null;
                String newString = null;
                String sampleDate = null;
                String sampleTime = "";

                if (expression.contains("|")) {
                    String mobApp = "0", fpID;
                    int set = 0;

                    if (expression.contains("{")) {
                        mobApp = expression.substring(expression.indexOf("{") + 1, expression.indexOf("}"));
                    }

                    if (expression.contains("#")) {
                        set = Integer.parseInt(expression.substring(expression.indexOf("#") + 1, expression.indexOf("#")));
                    }

                    if (mobApp.equals("0")) {
                        mobApp = String.valueOf(metaData.getCurrentFormID());
                    }

                    fpID = expression.substring(expression.indexOf("|") + 1, expression.lastIndexOf("|"));


                    if (set <= 0) {

                        if (set < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + set;
                            set = res;

                            if (set <= 0) {
                                resultString = null;
                            } else {
                                resultString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                            }

                        } else if (set == 0) {
                            set = getCurrentSetID();
                            resultString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                        }

                    } else {
                        resultString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                    }
                    try {
                        if ((resultString != null) && (!resultString.equals("0"))) {
                            Log.e("resultString", "WITHIN IF AND TRY-----  " + resultString);
                            sampleDate = resultString;
                            LocationDataSource locSource = new LocationDataSource(ObjContext);
                            SimpleDateFormat fromFormat = new SimpleDateFormat("mm/dd/yyyy");
                            SimpleDateFormat toFormat = new SimpleDateFormat("mm/dd/yy");


                            try {
                                newString = toFormat.format(fromFormat.parse(resultString));
                                //03-Jan-16
                                int quarter = Util.getQuarter(newString);
                                String[] yr = newString.split("/");
                                newString = "Q" + quarter + yr[2];//Q[2][92]
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            resultString = locSource.getLocationName(locationID) + "-" +
                                    newString;//LocationName-Q192
                        } else {
                            resultString = null;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in SampleID2 Calculation:" + e.getMessage());
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                    }
                }

                if (resultString != null && formActivity.currCocID != null && !formActivity.currCocID.equalsIgnoreCase("0") && metaData.getCurrentFormID() == 145) {
                    //19-03-2018 COC
                    handleSample3Data(sampleDate, sampleTime, resultString);
                    Log.e("dateSample", "calculteFromExpression: WITHIN IF-----  SAMPLE ID--- " + resultString + " SAMPLE DATE-- " + sampleDate + " sample time- " + sampleTime);
                }
                Log.e("dateSample", "calculteFromExpression: OUT OF IF-----  " + newString);

                return resultString;

            }
            //SAMPLE2 end

            //SAMPLE started
            else if (expression.contains("SAMPLE")) {
                String resultString = null;

                if (expression.contains("|")) {
                    String mobApp = "0", fpID;
                    int set = 0;

                    if (expression.contains("{")) {
                        mobApp = expression.substring(expression.indexOf("{") + 1, expression.indexOf("}"));

                    }
                    if (expression.contains("#")) {
                        set = Integer.parseInt(expression.substring(expression.indexOf("#") + 1, expression.indexOf("#")));
                    }

                    if (mobApp.equals("0")) {
                        mobApp = String.valueOf(metaData.getCurrentFormID());
                    }

                    fpID = expression.substring(expression.indexOf("|") + 1, expression.lastIndexOf("|"));

                    if (set <= 0) {

                        if (set < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + set;
                            set = res;

                            if (set <= 0) {
                                resultString = null;
                            } else {
                                resultString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp),
                                        set, fpID);
                            }

                        } else if (set == 0) {
                            set = getCurrentSetID();
                            resultString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                        }

                    } else {
                        resultString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);

                    }
                    try {
                        if ((resultString != null) && (!resultString.equals("0"))) {
                            LocationDataSource locSource = new LocationDataSource(ObjContext);
                            SimpleDateFormat fromFormat = new SimpleDateFormat("mm/dd/yyyy");
                            SimpleDateFormat toFormat = new SimpleDateFormat("mm/dd/yy");
                            String newString = null;

                            try {
                                newString = toFormat.format(fromFormat.parse(resultString));
                                //03-Jan-16
                                int quarter = Util.getQuarter(newString);
                                String[] yr = newString.split("/");
                                newString = quarter + "Q" + yr[2];//2Q92
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            resultString = sitename + "-" + locSource.getLocationName(locationID) +
                                    "(" + newString + ")";//SiteName-LocationName(1Q92)
                        } else {
                            resultString = null;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in SampleID Calculation:" + e.getMessage());
                    }
                }

                return resultString;
            }
            //SAMPLE end

            //COPY started e.g. COPY({138}#1#|3|)
            else if (expression.contains("COPY")) {

                String mobAppID = "", f_paramID = null;
                int setID = 0;
                String copyValue = null;

                lhs = expression.substring(0, expression.lastIndexOf(")"));

                if (lhs.contains("{"))
                    mobAppID = lhs.substring(lhs.indexOf("{") + 1, lhs.lastIndexOf("}"));

                if (lhs.contains("#")) {
                    if (lhs.substring(lhs.indexOf("#") + 1, lhs.lastIndexOf("#")).contains("9999"))
                        setID = 1;
                    else
                        setID = Integer.parseInt(lhs.substring(lhs.indexOf("#") + 1, lhs.lastIndexOf("#")));
                }

                if (mobAppID == null || mobAppID.isEmpty()) {
                    mobAppID = String.valueOf(metaData.getCurrentFormID());
                }

                if (lhs.contains("|")) {
                    f_paramID = lhs.substring(lhs.indexOf("|") + 1, lhs.lastIndexOf("|"));

                    if (setID <= 0) {

                        if (setID < 0) {//No. is Negative
                            int res = (getCurrentSetID()) + setID;
                            setID = res;

                            if (setID <= 0) {
                                copyValue = null;
                            } else {
                                copyValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                            }
                        } else if (setID == 0) {
                            setID = getCurrentSetID();
                            copyValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                        }
                    } else {
                        copyValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                    }
                } else {

                    String pattern = "\\d+";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(lhs);

                    while (m.find()) {
                        f_paramID = lhs.substring(m.start(), m.end());
                    }

                    copyValue = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                }

                Log.i(TAG, "Copy Value From FormID:" + mobAppID + " SetID:" + setID + " FieldParameterID:" + f_paramID + " StringValue:" + copyValue);
                return copyValue;
            }
            //COPY end

            //PurgeCalculation start
            else if (expression.contains("PurgeCalculation")) {
            /*    //if no flow rate no calculation
                if (flowRateParamId != -1) {
                    DataHolder dHolder = fmapObject.get(flowRateParamId + "");
                    if (dHolder.value == null)
                        return null;
                    if (dHolder.value.trim().isEmpty())
                        return null;
                }*/

                if (getCurrentSetID() > 1) {
                    String[] expList = StringUtils.substringsBetween(expression, "(", ")");
                    return calculatePurgeVolumeExp(expList, expression, metaData, false);
                }

                return "";
            }
            //PurgeCalculation end

            //VolumePurged start
            else if (expression.contains("VolumePurged")) {
              /*  //if no flow rate no calculation
                if (flowRateParamId != -1) {
                    DataHolder dHolder = fmapObject.get(flowRateParamId + "");
                    if (dHolder.value == null)
                        return null;
                    if (dHolder.value.trim().isEmpty())
                        return null;
                }*/

                if (getCurrentSetID() > 1) {
                    String[] expList = StringUtils.substringsBetween(expression, "(", ")");

                    return calculatePurgeVolumeExp(expList, expression, metaData, true);
                }
                return "";
            }
            //VolumePurged end

            //set start
            else if (expression.toLowerCase().contains("!!set!!")) {
                if (isExpressionQueryValid(expression)) {
                    String query = replaceSetOrVisibleQueryCols(expression);
                    if (!query.isEmpty()) {
                        return new FieldDataSource(formActivity).hitExpressionQuery(query);
                    } else return null;
                }
            }
            //set end

            else if (expression.toLowerCase().contains("!!coc!!")) {
                if (isExpressionQueryValid(expression)) {
                    String query = replaceCOCQueryCols(expression);
                    if (!query.isEmpty()) {
                        FieldDataSource.COCExpressionResults returnedValues
                                = new FieldDataSource(formActivity).hitCOCExpressionQuery(query);

                        if (formActivity.currCocID != null
                                && !formActivity.currCocID.equalsIgnoreCase("0")) {
                            handleSample3Data(returnedValues.getDate(), returnedValues.getTime(),
                                    returnedValues.getStringValue());
                        }

                        return returnedValues.getStringValue();
                    } else return null;
                }
            } else if (expression.toLowerCase().contains("!!visible!!")) {
                if (isExpressionQueryValid(expression)) {
                    expression = expression.replace("true", "1");
                    expression = expression.replace("false", "0");

                    String query = replaceSetOrVisibleQueryCols(expression);
                    if (!query.isEmpty()) {
                        boolean showField = new FieldDataSource(formActivity).hitExpressionQuery(query).equals("1");

                        if (mapMetaObjects.containsKey(metaData.getMetaParamID())) {
                            mapMetaObjects.get(metaData.getMetaParamID()).isRowVisible = showField;
                        }

                        for (MetaData mData : metaObjects) {
                            if (metaData.getMetaParamID() == mData.getMetaParamID()) {
                                mData.isRowVisible = showField;
                                mData.isVisible = showField;
                            }
                        }

                        if (metaData.getForm_field_row() != null)
                            formActivity.attachViewToScroller(metaData);
                    }
                }
                return null;
            }

            //NORMAL EXPRESSION started
            else {
                ArrayList<String> fieldIDList = new ArrayList<>();

                if (expression.contains(",")) {

                    HashMap<String, String> replacement = new HashMap<>();
                    String pattern = ",\\d";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(expression);

                    while (m.find()) {
                        String item = expression.substring(m.start(), m.end());
                        if (item.contains(","))
                            try {
                                decimalPlaces = Integer.parseInt(item.replace(",", ""));
                                expression = expression.replace(item, "");
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                decimalPlaces = 2;
                            }
                    }
                }

                if (expression.contains("@")) {

                    HashMap<String, String> timeFieldIDs = new HashMap<>();

                    timeFieldIDs = getTimeFieldIdValue(metaData, expression);//Collect timeInMinutes

                    if (timeFieldIDs.size() > 0) {
                        for (String key : timeFieldIDs.keySet()) {
                            String value = timeFieldIDs.get(key);

                            if (value != null && !value.isEmpty()) {
                                expression = expression.replace(key, value);
                            } else {
                                return null;
                            }
                        }
                    }
                }

                if (expression.contains("{")) {

                    HashMap<String, String> replacement = new HashMap<>();
                    String pattern = "\\{\\d+\\}\\#(.*?)\\#\\|\\d+\\|";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(expression);

                    while (m.find()) {

                        String item = expression.substring(m.start(), m.end());

                        String mobAppID = "", f_paramID = null;
                        int setID = 0;
                        String value = null;

                        if (item.contains("{"))
                            mobAppID = item.substring(item.indexOf("{") + 1, item.lastIndexOf("}"));
                        if (item.contains("#"))
                            setID = Integer.parseInt(item.substring(item.indexOf("#") + 1, item.lastIndexOf("#")));

                        if (mobAppID == null || mobAppID.isEmpty()) {
                            mobAppID = String.valueOf(metaData.getCurrentFormID());
                        }
                        if (item.contains("|")) {
                            f_paramID = item.substring(item.indexOf("|") + 1, item.lastIndexOf("|"));

                            if (setID <= 0) {

                                if (setID < 0) {//No. is Negative
                                    int res = (getCurrentSetID()) + setID;
                                    setID = res;

                                    if (setID <= 0) {
                                        value = null;
                                    } else {
                                        value = fp.getStringValueFromId(Integer.parseInt(eventID),
                                                locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                                    }
                                } else if (setID == 0) {
                                    setID = getCurrentSetID();
                                    value = fp.getStringValueFromId(Integer.parseInt(eventID),
                                            locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                                }
                            } else {
                                value = fp.getStringValueFromId(Integer.parseInt(eventID),
                                        locationID, Integer.parseInt(mobAppID), setID, f_paramID);
                            }

                            if (value == null || value.isEmpty()) {
                                return null;
                            }

                            replacement.put(item, value);
                            // expression=expression.replace(item,value);
                        }
                    }

                    if (replacement.size() > 0) {
                        for (String key : replacement.keySet()) {
                            String value = replacement.get(key);

                            if (value != null && !value.isEmpty()) {
                                expression = expression.replace(key, value);
                            } else {
                                return null;
                            }
                        }
                    }
                } else if (expression.contains("|")) {
                    String pattern = "\\|\\d+\\|";
                    Pattern p = Pattern.compile(pattern);
                    Matcher m = p.matcher(expression);

                    while (m.find()) {

                        String item = expression.substring(m.start(), m.end());

                        String pattern1 = "\\d+";
                        Pattern p1 = Pattern.compile(pattern1);
                        Matcher m1 = p1.matcher(item);

                        while (m1.find()) {
                            String item1 = item.substring(m1.start(), m1.end());
                            if (!fieldIDList.contains(item1)) {
                                fieldIDList.add(item1);
                            }
                        }
                    }

                    for (int i = 0; i < fieldIDList.size(); i++) {

                        String field_paramID = fieldIDList.get(i);
//                      int lastSetID = fp.getLastWorkingFieldSetID(locationID, ObjContext.getSiteID(), ObjContext.getCurrentAppID());
                        String value = fp.getStringValueFromId(Integer.parseInt(eventID), locationID, metaData.getCurrentFormID(), getCurrentSetID(), field_paramID);
                        if (value != null && !value.isEmpty()) {
                            expression = expression.replace("|" + fieldIDList.get(i) + "|", value);//(value == null || value.isEmpty()) ? 0 + "" : value)
                        }
                    }
                }

                Parser prs = new Parser();
                System.out.println("Output Expression:" + expression);

                String result = prs.parse(expression);
                Log.i("NormalExpression ", " Result:" + result);
                if (result.equals("NaN")) {
                    result = null;
                } else if (result.equals("Infinity")) {
                    result = null;
                } else {
                    try {
                        if (!result.contains("Error"))
                            result = Util.round(Double.parseDouble(result
                                            .replaceAll(",", ".")),
                                    decimalPlaces) + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in parsing double:" + e.getMessage());
                    }
                }
                return result;
            } //NORMAL EXPRESSION end
        }
        return null;
    }

    private String calculateSetExpression(MetaData metaData) {
        String operand = getExpressionFromMetaOrAttribute(metaData);

        if (operand == null || operand.isEmpty())
            return "";

        List<String> exprArray = Util.splitStringToArray("~", operand);
        String methodNames = "";

        for (String expression : exprArray) {
            if (expression != null && !expression.isEmpty()) {
                if (expression.toLowerCase().contains("!!set!!")) {
                    if (isExpressionQueryValid(expression)) {
                        String query = replaceSetOrVisibleQueryCols(expression);
                        if (!query.isEmpty()) {
                            methodNames = new FieldDataSource(formActivity).hitExpressionQuery(query);

                            FieldDataSource fieldDataSource = new FieldDataSource(context);

                            if (!fieldDataSource.isParamIdExists(getCurrentSetID(), Integer.parseInt(eventID),
                                    locationID, Integer.parseInt(siteID), metaData.getCurrentFormID(),
                                    deviceID, Integer.parseInt(userID), metaData.getMetaParamID())) {
                                List<FieldData> bfieldData = getBlankFieldData(context, metaData, getCurrentSetID());
                                fieldDataSource.insertFieldDataList(bfieldData, Integer.parseInt(userID), deviceID + "");
                            }

                            saveData_and_updateCreationDate(context, metaData, methodNames, getCurrentSetID());

                            DataHolder temp = fmapObject.get(metaData.getMetaParamID() + "");

                            if (temp != null) {
                                temp.value = methodNames;
                                fmapObject.put("" + metaData.getMetaParamID(), temp);
                                mapObject.put("" + metaData.getMetaParamID(), temp);
                            }
                        }
                    }
                }
            }
        }

        return methodNames;
    }

    private void updateBottleData(MetaData metaData) {
        //Todo: might cause error as there may be multiple queries in expression so need to add a loop here to find set query only
        String operand = getExpressionFromMetaOrAttribute(metaData);

        if (operand == null)
            return;

        if (operand.trim().isEmpty())
            return;

        String methodNames = "";
        List<String> exprArray = Util.splitStringToArray("~", operand);

        for (String expression : exprArray) {
            if (expression.toLowerCase().contains("!!set!!")) {
                if (isExpressionQueryValid(expression)) {
                    String query = replaceSetOrVisibleQueryCols(expression);
                    if (!query.isEmpty()) {
                        methodNames = new FieldDataSource(formActivity).hitExpressionQuery(query);
                    }
                } else return;
            } else return;
        }

        List<CoCBottles> cocBottles = new ArrayList<>();
        StringBuilder bottle = new StringBuilder();

        if (methodNames != null && !methodNames.isEmpty()) {
            StringBuilder methodIdList = new StringBuilder();

            List<String> listMethodNames = Util.splitStringToArray("|", methodNames);

            CocDetailDataSource cocDetailDataSource = new CocDetailDataSource(formActivity);

            for (int i = 0; i < listMethodNames.size(); i++) {
                String methodName = listMethodNames.get(i);
                int methodId = cocDetailDataSource.getMethodIDForMethods(methodName);

                if (i == listMethodNames.size() - 1) {
                    methodIdList.append(methodId);
                } else {
                    methodIdList.append(methodId).append(",");
                }
            }

            MethodDataSource methodDataSource1 = new MethodDataSource(context);
            List<CoCBottles> allBottlesList = new ArrayList<>();
            allBottlesList = methodDataSource1.getBottles(methodIdList.toString());
            cocBottles.addAll(allBottlesList);

            if (cocBottles.size() > 0) {
                for (int i = 0; i < cocBottles.size(); i++) {
                    String bottleName = cocBottles.get(i).getBottleName();

                    if (i == cocBottles.size() - 1) {
                        bottle.append(bottleName);
                    } else {
                        bottle.append(bottleName).append("|");
                    }
                }
            }
        }

        FieldDataSource fieldDataSource = new FieldDataSource(formActivity);
        fieldDataSource.updateCheckOptions(eventID,
                formActivity.getCurSetID(), locationID, siteID,
                formActivity.getCurrentAppID(), metaData.getMetaParamID(), bottle.toString());

        Log.i(TAG, "BottlesList:" + bottle);
        handleBottleData(metaData.getMetaParamID(), bottle.toString());
    }

    private boolean isExpressionQueryValid(String expression) {
        expression = expression.toLowerCase();

        return !expression.contains("insert") || !expression.contains("update")
                || !expression.contains("delete") || !expression.contains("truncate")
                || !expression.contains("drop");
    }

    public String replaceSetOrVisibleQueryCols(String expression) {

        expression = expression.replace("!!set!!", "");
        expression = expression.replace("!!visible!!", "");

        HashMap<String, String> mapCols = new HashMap<>();
        mapCols.put("d_field_data", "d_FieldData");
        mapCols.put("d_event", "d_Event");
        mapCols.put("s_site_mobile_app", "s_SiteMobileApp");
        mapCols.put("string_value", "StringValue");
        mapCols.put("field_parameter_id", "FieldParameterID");
        mapCols.put("mobile_app_id", "MobileAppID");
        mapCols.put("location_id", "LocationID");
        mapCols.put("event_id", "EventID");
        mapCols.put("set_id", "ExtField1");
        mapCols.put("site_id", "SiteID");
        mapCols.put("app_order", "AppOrder");
        mapCols.put("cu_loc_id", locationID);
        mapCols.put("cu_eve_id", eventID);
        mapCols.put("cu_project_id", siteID);
        mapCols.put("cu_setId", getCurrentSetID() + "");
        mapCols.put("cu_ext_field1", getCurrentSetID() + "");
        mapCols.put("true", "1");
        mapCols.put("false", "0");
        mapCols.put("observation_date", "ExtField2");

        for (Map.Entry<String, String> entry : mapCols.entrySet()) {
            expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        return expression;
    }

    private String replaceCOCQueryCols(String expression) {

        expression = expression.replace("!!coc!!", "");

        HashMap<String, String> mapCols = new HashMap<>();
        mapCols.put("d_field_data", "d_FieldData");
        mapCols.put("string_value", "StringValue");
        mapCols.put("field_parameter_id", "FieldParameterID");
        mapCols.put("location_id", "LocationID");
        mapCols.put("location", "Location");
        mapCols.put("event_id", "EventID");
        mapCols.put("cu_loc_id", locationID);
        mapCols.put("cu_eve_id", eventID);

        for (Map.Entry<String, String> entry : mapCols.entrySet()) {
//            expression = expression.replace(entry.getKey(), entry.getValue());
            expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        return expression;
    }

    private String replaceValidateQueryCols(String expression) {

        expression = expression.replace("!!validate!!", "");

        HashMap<String, String> mapCols = new HashMap<>();
        mapCols.put("d_field_data", "d_FieldData");
        mapCols.put("string_value", "StringValue");
        mapCols.put("field_parameter_id", "FieldParameterID");
        mapCols.put("mobile_app_id", "MobileAppID");
        mapCols.put("location_id", "LocationID");
        mapCols.put("event_id", "EventID");
        mapCols.put("set_id", "ExtField1");
        mapCols.put("cu_loc_id", locationID);
        mapCols.put("cu_eve_id", eventID);
        mapCols.put("cu_ext_field1", getCurrentSetID() + "");

        for (Map.Entry<String, String> entry : mapCols.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue());
        }

        return expression;
    }

    private String calculatePurgeVolumeExp(String[] exprList, String expression, MetaData
            metaData, boolean isVolumePurged) {

        //don't be smart to add replace method directly to expression else the below
        //VolumePurge text replaced will erase the fieldOperand's text every time leading
        // to undetected by if condition of expr check
        String expr = new StringBuilder(expression).toString();

        ArrayList<String> expList = new ArrayList<>(Arrays.asList(exprList));

        if (isVolumePurged)
            expr = expr.replace("VolumePurged", "");
        else {
            expr = expr.replace("PurgeCalculation", "");

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

        for (String exp : expList) {
            exp = exp.replace("(", "").replace(")", "");
            Double value =
                    getValueVolumePurgeCalculation(exp, metaData, isVolumePurged);
            if (value == null)
                return null;
            expr = expr.replace(exp, String.valueOf(value));
        }

        return parseExpression(expr);
//        return expression;
    }

    private String parseExpression(String expression) {
        Parser prs = new Parser();
        System.out.println("Output Expression:" + expression);

        String result = prs.parse(expression);
        Log.i("NormalExpression ", " Result:" + result);
        if (result.equals("NaN")) {
            result = null;
        } else if (result.equals("Infinity")) {
            result = null;
        } else {
            try {
                if (!result.contains("Error"))
                    result = Util.round(Double.parseDouble(result.replaceAll(",", ".")), 2) + "";
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in parsing double:" + e.getMessage());
            }
        }
        return result;
    }

    private Double getValueVolumePurgeCalculation(String expression, MetaData metaData,
                                                  boolean isVolumePurged) {
        String mobAppID = "", f_paramID = null;
        int setID = 0;
        String paramId = null;

        if (expression.contains("{")) {

            HashMap<String, String> replacement = new HashMap<>();
            String pattern = "\\{\\d+\\}\\#(.*?)\\#\\|\\d+\\|";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(expression);

            while (m.find()) {
                String item = expression.substring(m.start(), m.end());
                if (item.contains("{"))
                    mobAppID = item.substring(item.indexOf("{") + 1, item.lastIndexOf("}"));
                if (item.contains("#"))
                    setID = Integer.parseInt(item.substring(item.indexOf("#") + 1, item.lastIndexOf("#")));
                if (item.contains("|")) {
                    f_paramID = item.substring(item.indexOf("|") + 1, item.lastIndexOf("|"));
                }
            }
        } else if (expression.contains("|")) {
            String pattern = "\\|\\d+\\|";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(expression);

            while (m.find()) {

                String item = expression.substring(m.start(), m.end());

                String pattern1 = "\\d+";
                Pattern p1 = Pattern.compile(pattern1);
                Matcher m1 = p1.matcher(item);

                while (m1.find()) {
                    paramId = item.substring(m1.start(), m1.end());
                }
            }
        }

        if (setID == -1)
            setID = getCurrentSetID() - 1;
        else
            setID = getCurrentSetID();

        FieldDataSource fieldDataSource = new FieldDataSource(context);

        Double value = null;
        if (isVolumePurged) {
            String stringValue = fieldDataSource.getStringValueFromId(Integer.parseInt(eventID), locationID,
                    Integer.parseInt(mobAppID), setID, f_paramID);
            if (stringValue != null && !stringValue.isEmpty())
                value = Double.parseDouble(stringValue);
        } else if (paramId != null) {
            String stringValue = fieldDataSource.getStringValueFromId(Integer.parseInt(eventID), locationID,
                    metaData.getCurrentFormID(), getCurrentSetID(), paramId);
            if (stringValue != null && !stringValue.isEmpty())
                value = Double.parseDouble(stringValue);
        } else {
            Long measurementTime = fieldDataSource.getMeasurementTimeByExt2n3(Integer.parseInt(eventID), locationID, mobAppID,
                    setID, f_paramID, formActivity.getSiteID());
            value = (double) measurementTime / 60000; //converting secs to hours
        }
        return value;
    }

    public void handleSample3Data(String sampledate, String sampletime, String sampleID) {
        boolean isLocationSampled = false;
        CocDetailDataSource cocDS = new CocDetailDataSource(context);
        isLocationSampled = cocDS.isCocAndLocationPresentAlready(formActivity.currCocID, locationID);
        if (isLocationSampled) {
            //UPDATE ROW
            cocDS.updateSampleID(sampledate, sampletime, sampleID, formActivity.currCocID, locationID, userID + "");
        } else {
            //INSERT NEW ROW
            SCocDetails sdetailItem = new SCocDetails();
            sdetailItem.setSampleDate(sampledate);
            sdetailItem.setSampleTime(sampletime);
            sdetailItem.setSampleId(sampleID);
            sdetailItem.setCreationDate(System.currentTimeMillis());
            sdetailItem.setCreatedBy(Integer.valueOf(userID));
            sdetailItem.setModifiedBy(Integer.valueOf(userID));
            sdetailItem.setDeleteFlag(0);
            sdetailItem.setStatus("COMPLETED");
            sdetailItem.setCocFlag(1);
            sdetailItem.setServerCreationDate(-1L);
            sdetailItem.setLocationId(Long.parseLong(locationID));
            sdetailItem.setCocId(Integer.valueOf(formActivity.currCocID));
            sdetailItem.setCocDetailsId(System.currentTimeMillis() + "");

            cocDS.insertNewCoCdetail(sdetailItem);
        }
    }

    public void calculateSample4Expression(MetaData metaData, String sampleId) {

        FieldDataSource fp = new FieldDataSource(formActivity);
        String expression = getExpressionFromMetaOrAttribute(metaData);

        if (expression != null && (expression.contains("SAMPLE4")
                || expression.contains("DUPSAMPLE4"))) {
            String dateString = null;
            String timeString = null;
            String resultString = null;
            String s3subexpression = expression.substring(expression.indexOf("(") + 1,
                    expression.lastIndexOf(")"));
            String[] s3FpIds = s3subexpression.split(",");

            if (s3subexpression.contains("|")) {
                String mobApp = String.valueOf(metaData.getCurrentFormID());
                String datefpID;
                String timeFpID;
                int set = getCurrentSetID();

                if (s3FpIds[0] != null && !s3FpIds[0].isEmpty()) {
                    datefpID = s3FpIds[0].substring(s3FpIds[0].indexOf("|") + 1,
                            s3FpIds[0].lastIndexOf("|"));
                    dateString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                            Integer.parseInt(mobApp), set, datefpID);
                }

                if (s3FpIds[1] != null && !s3FpIds[1].isEmpty()) {
                    timeFpID = s3FpIds[1].substring(s3FpIds[1].indexOf("|") + 1,
                            s3FpIds[1].lastIndexOf("|"));
                    timeString = fp.getStringValueFromId(Integer.parseInt(eventID), locationID,
                            Integer.parseInt(mobApp), set, timeFpID);
                }
            }

            if (formActivity.currCocID != null
                    && !formActivity.currCocID.equalsIgnoreCase("0")) {
                handleSample4Data(dateString, timeString, sampleId, expression.contains("DUPSAMPLE4"));
            }
        }
    }

    private String getExpressionFromMetaOrAttribute(MetaData metaData) {
        String expression = metaData.getFieldParameterOperands();

        MetaDataAttributesDataSource source = new MetaDataAttributesDataSource(context);
        MetaDataAttributes attributes =
                source.getMetaDataAttributes(Integer.parseInt(siteID),
                        formActivity.getCurrentAppID(), metaData.getMetaParamID());

        if (attributes != null) {
            if (attributes.getField_parameter_operands() != null
                    && !attributes.getField_parameter_operands().isEmpty())
                expression = attributes.getField_parameter_operands();
        }

        return expression;
    }

    public void handleSample4Data(String sampleDate, String sampleTime, String sampleID,
                                  boolean isDupSample4) {

        CocDetailDataSource cocDetailDataSource = new CocDetailDataSource(formActivity);
        ArrayList<SCocDetails> cocMethods
                = cocDetailDataSource.getCOCMethodsForLocation(formActivity.currCocID + "",
                locationID, false);

        if (!isDupSample4) {
            for (SCocDetails method : cocMethods) {
                cocDetailDataSource.updateSampleID(sampleDate, sampleTime, sampleID, formActivity.currCocID,
                        locationID, userID + "", method.getMethodId() + "", 0);
            }
        } else {
            for (SCocDetails method : cocMethods) {
                boolean isMethodPresent
                        = cocDetailDataSource.isCocMethodPresentAlready(formActivity.currCocID,
                        locationID, method.getMethodId() + "", 1);

                if (isMethodPresent)
                    cocDetailDataSource.updateSampleID(sampleDate, sampleTime, sampleID, formActivity.currCocID,
                            locationID, userID + "", method.getMethodId() + "", 1);
                else {
                    SCocDetails cocMethod = new SCocDetails();
                    cocMethod.setSampleDate(sampleDate);
                    cocMethod.setSampleTime(sampleTime);
                    cocMethod.setSampleId(sampleID);
                    cocMethod.setCreationDate(System.currentTimeMillis());
                    cocMethod.setCreatedBy(Integer.valueOf(userID));
                    cocMethod.setModifiedBy(Integer.valueOf(userID));
                    cocMethod.setMethodId(method.getMethodId());
                    cocMethod.setMethod(method.getMethod());
                    cocMethod.setDeleteFlag(0);
                    cocMethod.setDupFlag(1);
                    cocMethod.setStatus("COMPLETED");
                    cocMethod.setCocFlag(1);
                    cocMethod.setServerCreationDate(-1L);
                    cocMethod.setLocationId(Long.parseLong(locationID));
                    cocMethod.setCocId(Integer.valueOf(formActivity.currCocID));
                    cocMethod.setCocDetailsId(System.currentTimeMillis() + "");

                    cocDetailDataSource.insertNewCoCdetail(cocMethod);
                }
            }
        }
    }

    private HashMap<String, String> getTimeFieldIdValue(MetaData metaData, String expression) {
        FieldDataSource fp = new FieldDataSource(context);

        HashMap<String, String> fieldIDValue = new HashMap<String, String>();
        String pattern = "@(.*?)@";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(expression);

        while (m.find()) {

            String item = expression.substring(m.start(), m.end());
            if (!fieldIDValue.containsKey(item)) {
                String timeInMinutes = getTimeInMinutesFromExpression(metaData, item);
                fieldIDValue.put(item, timeInMinutes);
            }
        }
        return fieldIDValue;
    }


    public String getTimeInMinutesFromExpression(MetaData metaData, String timeExpression) {//@{mobApp}#setID#|field_param_ID|@ OR @|field_param_ID|@

        FieldDataSource fp = new FieldDataSource(context);

        String resultString = null;
        try {
            if (timeExpression.contains("|")) {
                String mobApp = "0", fpID;
                int set = 0;

                if (timeExpression.contains("{")) {
                    mobApp = timeExpression.substring(timeExpression.indexOf("{") + 1, timeExpression.lastIndexOf("}"));
                }
                if (timeExpression.contains("#")) {
                    set = Integer.parseInt(timeExpression.substring(timeExpression.indexOf("#") + 1, timeExpression.lastIndexOf("#")));
                }

                if (mobApp.equals("0")) {
                    mobApp = String.valueOf(metaData.getCurrentFormID());
                }

                fpID = timeExpression.substring(timeExpression.indexOf("|") + 1, timeExpression.lastIndexOf("|"));


                if (set <= 0) {

                    if (set < 0) {//No. is Negative
                        int res = (getCurrentSetID()) + set;
                        set = res;

                        if (set <= 0) {
                            return null;
                        } else {
                            resultString = fp.getMeasurmentTimeFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                        }

                    } else if (set == 0) {
                        set = getCurrentSetID();
                        resultString = fp.getMeasurmentTimeFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                    }

                } else {
                    resultString = fp.getMeasurmentTimeFromId(Integer.parseInt(eventID), locationID, Integer.parseInt(mobApp), set, fpID);
                }
                try {
                    if ((resultString != null) && (!resultString.equals("0"))) {
                        Long millis = Long.parseLong(resultString);
                        String minutes = millis / 60000 + "";//60000 milis = 1 min
                        Log.i(TAG, "getTimeInMinutesFromExpression " + timeExpression + ": " + minutes);
                        return minutes;

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error in SampleID2 Calculation:" + e.getMessage());
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getTimeInMinutesFromExpression :" + e.getMessage());
        }
        return null;
    }

    public static String buildConditionalExpressionResult(double lhs, String operator,
                                                          double rhs, String trueValue, String falseValue) {

        String result = "0";

        switch (operator) {
            case ">":
                if (lhs > rhs) {
                    return trueValue;
                } else {
                    return falseValue;
                }

            case "<":
                if (lhs < rhs) {
                    return trueValue;
                } else {
                    return falseValue;
                }
            case "==":
                if (lhs == rhs) {
                    return trueValue;
                } else {
                    return falseValue;
                }
            case "!=":
                if (lhs != rhs) {
                    return trueValue;
                } else {
                    return falseValue;
                }
            case ">=":
                if (lhs >= rhs) {
                    return trueValue;
                } else {
                    return falseValue;
                }
            case "<=":
                if (lhs <= rhs) {
                    return trueValue;
                } else {
                    return falseValue;
                }
        }

        return result;
    }
}
