package qnopy.com.qnopyandroid;

import android.content.Context;
import android.location.Location;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GlobalStrings {
    public static final String WEATHER_API_KEY = "18ad7b46a4524f188cd81007201305";

    public static final String FORM_NAME = "form_name";
    public static final String SELECTED_TAB = "SELECTED";
    public static final String TASK_DATA = "task_data";
    public static final String TASK_INTENT_DATA = "task_intent_data";
    public static final String DATE_FORMAT_MM_DD_YYYY_HRS_MIN = "MM/dd/yyyy HH:mm";
    public static final String DATE_FORMAT_MM_DD_YYYY_MIN = "MM/dd/yyyy HH:mm aa";
    public static final String DATE_FORMAT_MM_DD_YYYY_MIN_12HR = "MM/dd/yyyy hh:mm aa";
    public static final String DATE_FORMAT_MM_DD_YYYY_MIN_24HR = "yyyy-dd-MM_HH_mm";
    public static final String DATE_FORMAT_MMM_DD_YYYY_H_M_12HR = "MMM dd, yyyy hh:mm aa";//hh for 12hr and HH for 24hr
    public static final String DATE_FORMAT_COC_LABELS = "yyyy-dd-MM_HH:mm:ss";
    public static final String DATE_FORMAT_YYYY_DD_MM = "yyyy-dd-MM";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MMM_DD_YYYY = "MMM dd, yyyy";
    public static final String DATE_FORMAT_MMM_DD_YYYY_TIME = "MMM dd, yyyy HH:mm";
    public static final String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_H_M = "hh:mm aa";
    public static final String DATE_FORMAT_H_M_S = "hh:mm:ss";
    public static final String DATE_FORMAT_24hr_H_M = "HH:mm";
    public static final String DATE_FORMAT_H = "HH aa";

    public static final String KEY_META_DATA = "metadata";
    public static final String KEY_SITE_ID = "siteId";
    public static final String SWITCHED_SITE_ID = "switchedSiteId";
    public static final String KEY_LOCATION_ID = "locationId";
    public static final String KEY_EVENT_ID = "eventId";
    public static final String SWITCHED_EVENT_ID = "switchedEventId";
    public static final String KEY_SET_ID = "setId";

    public static final String KEY_SELECTED_IMAGE_PATH = "selectedImagePath";
    public static final String KEY_SELECTED_IMAGE_THUMB_PATH = "key_selected_thumb_path";
    public static final String KEY_SELECTED_IMAGE_1000_PATH = "key_selected_1000*1000_path";

    public static final int COMPRESSION_RATE_100 = 100;
    public static final int COMPRESSION_RATE_50 = 50;

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_ONGOING = "Ongoing";
    public static final String STATUS_ASSIGNED = "Assigned";
    public static final String STATUS_DISCARDED = "Discarded";
    public static final String STATUS_COMPLETED = "Completed";
    public static final int REQUEST_CODE_EDIT_TASK = 1435;
    public static final int REQUEST_CODE_WEATHER = 3215;
    public static final String TASK_ID = "task_id";
    public static final String KEY_FIELD_PARAM_ID = "fieldParamId";
    public static final String KEY_MOBILE_APP_ID = "mobileAppId";
    public static final String IS_FROM_FORMS = "isFromForms";
    public static final int REQUEST_CODE_TAB_TASK_ACT = 3872;
    public static final String APP_TYPE_LIMITED = "limited";
    public static final String APP_TYPE_STANDARD = "standard";
    public static final String APP_TYPE_CALENDAR = "calendar";
    public static final String APP_TYPE_PROJECT = "project";
    public static final String APP_TYPE_MOBILE_2_POINT_0 = "Mobile2.0";
    public static final String EVENT_STAR_DATE = "eventStartDate";
    public static final String IS_CAMERA = "is_camera";
    public static final String FOLDER_QNOPY_DOCS = "Qnopy docs";
    public static final String FOLDER_TEMPLATES = "Templates";
    public static final String PATH_LIST = "path_list";
    public static final String POSITION = "position";
    public static final String SUPPORT_EMAIL = "support@qnopy.com";
    public static final String SUPPORT_PHONE = "+1-866-766-7924";
    public static final String FP_IDS_LIST = "fpIdList";
    public static final String IS_CALENDAR_REFRESH = "isCalendarRefresh";
    public static final String IS_TASK_REFRESH = "isTaskRefresh";
    public static final String IS_EVENTS_REFRESH = "isEventsRefresh";
    public static final String IS_SITE_REFRESH = "isSiteRefresh";
    public static final String OLD_APP_NAME = "oldAppName";
    public static final String IS_LOCATION_SWITCHED = "isLocSwitched";
    public static final String COC_DOCS_FOLDER = "COCDocs";
    public static final String FOLDER_NAME = "folderName";
    public static final String FETCHED_LOCATION = "fetched_location";
    public static final String REQUIRED_LOCATION = "required_location";
    public static final String IS_FROM_FORM_MASTER = "is_from_form_master";
    public static final String ENABLE_SPLIT_SCREEN = "enable_split_screen";
    public static final String ENABLE_INFO_BUTTONS = "enable_info_buttons";
    public static final String KEY_CALCULATED = "calculated";
    public static final String KEY_SHOW_LAST3 = "showLast3";
    public static final int REQUEST_TASK_TAB_ACTIVITY = 54337;
    public static final String IS_FROM_TASK_TAB_ACT = "isFromTaskTabActivity";
    public static final String IS_FROM_ADD_EDIT_TASK = "isFromAddTaskActivity";
    public static final String ADDED_LOCATIONS = "added_event_locations";
    public static final String IS_FROM_CREATE_EVENT_SCREEN = "fromCreateEventScreen";
    public static final String FORM_DEFAULT = "FormDefault";
    public static final String NON_FORM_DEFAULT = "NonFormDefault";
    public static final String PRINT_COC = "print_coc";
    public static final String KEY_ROLL_APP_ID = "rollAppId";
    public static final String FORM_DETAILS = "form_details";
    public static final String FROM_DASHBOARD = "from_dashboard";
    public static final String SYNC_DATE_TYPE_EVENT = "EVENT";
    public static final String SYNC_DATE_TYPE_META = "META";
    public static final String QR_SCANNED_TEXT = "qr_scanned_text";
    public static final String IS_SHOW_FASTER_FORMS = "is_Show_faster_forms";
    public static final String IS_RESYNC_DATA = "isResyncData";

    public static final String KEYCLOAK_CLIENT_ID = "qnopy-mobile";
    public static final Uri KEYCLOAK_REDIRECT_URI = Uri.parse("qnopy.com.qnopyandroid:/oauth2callback");
    public static final String KEY_OPEN_ID_TOKEN_RESPONSE = "openIdTokenResponse";
    public static final String KEY_SSO_RESPONSE = "key_sso_response";

    public static final String THUMBNAIL_EXTENSION = "_thumbnail.jpg";
    public static final String THUMBNAIL_EXTENSION_PNG = "_thumbnail.png";
    public static final String THOUSAND_EXTENSION = "_1000*1000.jpg";
    public static final int THUMBNAIL_HEIGHT_WIDTH = 160;
    public static final String FIELD_DATA_INSERT = "field_data_insert";
    public static final String FIELD_DATA_UPDATE = "field_data_update";
    public static final String TRIAL_PERIOD = "trial_period";
    public static final String ALL_EVENT_DATA_LAST_SYNC = "lastSyncDate";
    public static final String FEED_PICS_LIST = "feed_pics_list";
    public static final String KEYCLOAK_GUID = "keycloak_guid";

    //	public static String DATABASE_NAME = "aqua_site25";
    public static String DATABASE_NAME = "aqua";
    public static String LAST_LOCATION_ATTRIBUTE_VALUE = "selected_attribute_value";
    public static String LAST_LOCATION_ATTRIBUTE_NAME = "selected_attribute_name";
    public static String IS_CAPTURE_LOG = "is_capture_log";
    public static String IS_COMPRESS_IMAGE = "is_compress_image";
    public static String IS_SHOW_GALLERY = "is_show_gallery";
    public static Location CURRENT_GPS_LOCATION = null;

    public static boolean CAPTURE_LOG = false;
    public static String CAPTURE_SIGNATURE = "CAPTURE_SIGNATURE";
    public static String BG_SERVICE = "BACKGROUND SERVICE";
    public static boolean COMPRESS_IMAGE = false;
    public static boolean SHOW_GALLERY = false;
    public static String SHOW_HOSPITAL_ALERT_FOR_FIRSTTIME = "KEY_USERNAME_";

    public static String BATTERY_LEVEL = "100";
//    public static String taskSuccessMsg = "Tasks are downloaded";

    public static String APP_TYPE = "app_type";//EHS or REM
    public static String AUTO_GENERATE = "auto_generate";//EHS or REM
    public static String CITY_LAST_SYNC = "city_last_sync";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String USERROLE = "userRole";
    public static String USERAPPTYPE = "userAppType";
    public static String GUID = "guid";
    public static String DEVICEID = "deviceid";
    public static String NOTIFICATION_REGISTRATION_ID = "refresh_id";
    public static String USERID = "userid";
    public static String USERTTYPE = "UserType";
    public static String COMPANYID = "companyid";
    public static String CURRENT_SITEID = "siteid";
    public static String CURRENT_SITENAME = "sitename";
    public static String KEY_SITE_NAME = "site_name";
    public static String SITE_DETAILS = "siteDetails";
    public static String SWITCHED_SITENAME = "switchedSiteName";
    public static String CURRENT_APPID = "appid";
    public static String SWITCHED_APPID = "switchedAppid";
    public static String CURRENT_APPNAME = "appname";
    public static String SESSION_DEVICEID = "session_device";
    public static String SESSION_USERID = "session_userid";
    public static String SESSION_CARD = "SESSION_CARD";
    public static String IS_SESSION_ACTIVE = "session_status";
    public static String CURRENT_LOCATIONID = "locationid";
    //    public static String EVENT_LAST_VISITED_LOCATION = "event_last_location";
    public static String CURRENT_LOCATIONNAME = "location_name";
    public static String FF_LAST_SYNCTIME = "lastsync";
    public static String tempApkName = "temp.apk";
    public static int protocolVersion = 1;

    public static String responseMessage = "Unauthorized user account. Contact QNOPY support.";

    public static String no_tag_alert = "You have not tagged any location yet.";
    public static String text_input_limit_alert = "You have reached your maximum limit of characters allowed";

    public static String signatureDirectory = "signatures";

    public static final String THUMBNAILS_DIR = "Thumbnails";
    public static final String IMAGE_STORAGE_DIR = "QnopyPictures";
    public static final String DRAWING_STORAGE_DIR = "QnopyDrawings";
    public static final String DOCUMENT_STORAGE_DIR = "QnopySheets";
    public static final String DB_ZIP_STORAGE_DIR = "DatabaseZip";

    public static String KEY_FILE_PATH = "filePath";

    public static String DB_PATH = File.separator + "databases" + File.separator;//"/data/data/com.aqua.fieldbuddy/databases/";
    public static String FILEFOLDER = File.separator + "FileFolder" + File.separator;
    public static String FILEFOLDERPDF = File.separator + "FileFolderPDF" + File.separator;
    public static String COC_FOLDER_PDF = "AllDownloadedCOCPDF" + File.separator;
    //20-Jan-16 Folder will Create under "/data/data/com.aqua.fieldbuddy/" directory
    public static String EXT_FILEFOLDER_PATH = File.separator + "Qnopy" + FILEFOLDER;
    public static String EXT_FILEFOLDER_PATH_PDF = File.separator + "Qnopy" + FILEFOLDER;
    public static String ZIP_DB_PATH = File.separator + "Qnopy" + File.separator + "database" + File.separator;
    public static String LOG_FILE_PATH = File.separator + "Qnopy" + File.separator + "Log" + File.separator;
    public static String DEMO_IMAGE_PATH = File.separator + "Qnopy" + File.separator + "demo" + File.separator;

    public static String fromEmail = "donotreplyaqua@gmail.com";
    public static String fromPassword = "AQUAdonotreply";
    public static int SHOW_TAGGED_LOCATION = 0;
    public static int TAG_LOCATION = 1;
    public static int LOAD_KMZ = 2;
    public static int TAG_SAMPLE = 3;
    public static int LOAD_GPSTRACK = 4;

    public static String IP = "qnopy.com";
    public static String PORT = "";
    public static String API_VERSION = "v6";
    //    public static String Local_Base_URL= "https://"+IP+PORT+"/FetchForms/api/"+API_VERSION+"/";
    public static Context currentContext = null;

    public static long captureTime;

    //17-Jun-17 APP FEATURES
//    public static String KEY_APP_UPDATE = "APP_UPDATE";
    public static String KEY_PROJECT_FILE = "PROJECT_FILE";
    public static String KEY_LOAD_KMZ = "LOAD_KMZ";
    public static String KEY_ADD_LOCATION = "ADD_LOCATION";
    public static String KEY_PERCENTAGE = "PERCENTAGE";
    public static String KEY_MAP_VIEW = "MAP_VIEW";
    public static String KEY_OFFLINE_MAP = "OFFLINE_MAP";
    public static String KEY_PHOTO_GALLERY = "PHOTO_GALLERY";
    public static String KEY_PHOTO_RESOLUTION = "PHOTO_RESOLUTION";
    public static String KEY_REPORT = "REPORT";
    public static String KEY_DRAW_APP = "DRAW_APP";
    public static String KEY_EMERGENCY = "EMERGENCY";
    public static String KEY_DOWNLOAD_DATA = "DOWNLOAD_DATA";
    public static String KEY_SIGNATURE = "SIGNATURE";
    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    //17-05-2018 USER_TYPE
    public static int SUPER_ADMIN = 1, CLIENT_ADMIN = 2, STD_USER = 3, QA_VALIDATOR = 4, PROJECT_MANAGER = 5,
            REVIEWER = 6, REPORT = 7, CLIENT = 8, SALES_REP = 9, REFERRED_USER = 10, TRIAL_USER = 11;
    //24-05-2018 NOTIFICATION STATUS
    public static int UNREAD = 0, READ = 1;

    public static final String BROADCAST_ACTION = "com.qnopy.broadcastreceiver";

    //25-05-2018 PUSH NOTIFICATION OPERATION CODE
    public static final Integer DOWNLOAD_EVENT_OPERATION_CODE = 101, DOWNLOAD_FORMS_OPERATION_CODE = 102,
            CHANGE_PASSWORD_OPERATION_CODE = 103, EVENT_CLOSED_OPERATION_CODE = 104, SUSPEND_USER_OPERATION_CODE = 105,
            DOWNLOAD_COC_OPERATION_CODE = 107;

    public static int mDeviceConnectedStatusFlag = 0;
    public static String mDeviceConnectedName;
    public static int mHashMapContainsSameAttributeKey = 0;
    public static String mCapturePictureStoragePath;
    public static String mDrawingPictureStoragePath;

    public static final String EVENT_DATA = "event_data";

    public static final String DOWNLOAD_EVENT_DATA_DEMO_SITE = "download_event_data_demo_site";

    public static final String KEY_EVENT_IDS = "key_event_ids";

    public static final String KEY_EQUIPMENT_LIST = "key_equipment_list";

    public static final String KEY_EQUIPMENT_ORDER = "key_equipment_order_data";

    public static final String KEY_DEMO_SITES = "demo_sites";
    public static final String SITE_TYPE_NON_PHASE_1 = "non phase-I";

    public static final String LRU_CACHE_NAME = "cacheQnopy";
    public static final int IN_APP_UPDATE_REQUEST_CODE = 732879;
    public static final String LOCATION_DETAILS = "location_details";
    public static final String HEADER_KEY_USER_GUID = "user_guid";
}
