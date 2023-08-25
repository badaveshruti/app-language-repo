package qnopy.com.qnopyandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbAccess {

    private static final Object DATABASE_VERSION = 1;
    public static DbAccess dbAccess = null;
    public static Context mContext;
    public SQLiteDatabase database;

    private FieldSQLiteHelper dbHelper;
    public static final String TABLE_MOBILE_APPS = "s_MobileApp";
    public static final String TABLE_META_DATA = "s_MetaData";
    public static final String TABLE_META_DATA_ATTRIBUTES = "s_MetaData_attributes";
    public static final String TABLE_FIELD_DATA = "d_FieldData";
    public static final String TABLE_ATTACHMENT = "d_Attachment";
    public static final String TABLE_SITES = "s_Site";
    public static final String TABLE_LOCATION = "s_Location";
    public static final String TABLE_USER = "s_User";
    public static final String TABLE_TEMP_USER = "tmp_s_User";
    public static final String TABLE_EVENT = "d_Event";
    public static final String TABLE_FIELD_PARAMETER = "r_FieldParameter";
    public static final String TABLE_TASK_ATTRIBUTES = "task_attributes";
    public static final String TABLE_PARAM_UNIT = "r_FieldParameterUnit";
    public static final String TABLE_UNIT_CONVERTER = "r_Unit_converter";
    public static final String TABLE_SITE_USER_ROLE = "s_SiteUserRole";
    public static final String TABLE_TEMP_SITE_USER_ROLE = "temp_s_SiteUserRole";
    public static final String TABLE_ROLE = "r_Roles";
    public static final String TABLE_WORK_ORDER_TASK_NEW = "s_work_order_task";

    public static final String TABLE_CM_COC_DETAILS = "cm_coc_details";
    public static final String TABLE_CM_METHODS = "cm_methods";
    public static final String TABLE_CM_COC_MASTER = "cm_coc_master";

    public static final String TABLE_WORK_ORDER_TASK_OLD = "work_order_task";

    public static final String TABLE_S_LOV = "s_lov";
    public static final String TABLE_LOV_ITEMS = "s_lov_items";
    public static final String TABLE_SITE_MOBILEAPP = "s_SiteMobileApp";

    public static final String TABLE_BORE_DEFINITION = "d_BoreDefinition";
    public static final String TABLE_BORE_DEPTHS = "d_BoreDepths";
    public static final String TABLE_LOC_FORM_STATUS = "LocFormStatus";
    public static final String TABLE_S_PROJECT_FOLDER = "s_project_folder";
    public static final String TABLE_S_LOCATION_FORM_PERCENTAGE = "s_LocationFormPercentage";
    public static final String TABLE_TEMP_PROJECT_FOLDER = "temp_project_folder";
    public static final String TABLE_S_PROJECT_FILE = "s_project_file ";
    public static final String TABLE_TEMP_PROJECT_FILE = "temp_project_file ";
    public static final String TABLE_S_FILE_PERMISSION = "s_file_permission ";
    public static final String TABLE_TEMP_D_FIELD_DATA = "d_field_data_temp";
    public static final String TABLE_D_SYNC_STATUS = "d_sync_status";
    public static final String TABLE_D_SAMPLE_MAPTAG = "d_SampleMapTag";
    public static final String TABLE_D_FIELD_DATA_CONFLICT = "d_FieldData_Conflict";
    public static final String TABLE_S_DEFAULT_VALUES = "s_Default_Values";
    public static final String TABLE_S_APP_PRFERENCE_MAPPING = "s_PreferenceMapping";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_S_LOCATION_ATTRIBUTE = "s_LocationAttribute";

    public static final String TABLE_CONSTRUCTION_POSTDATA = "c_PostData";
    public static final String TABLE_CONSTRUCTION_MEDIADATA = "c_MediaData";
    public static final String TABLE_CONSTRUCTION_CTAGDATA = "c_CTagData";
    public static final String TABLE_CONSTRUCTION_RTAGDATA = "c_RTagData";
    public static final String TABLE_CONSTRUCTION_TIMESTAMP_SIMPLENOTEDATA = "c_TimeStampSND";
    public static final String TABLE_TASK_DETAILS = "w_task_details";
    public static final String TABLE_TASK_ATTACHMENTS = "w_task_attachments";
    public static final String TABLE_TASK_COMMENTS = "w_task_comments";
    public static final String TABLE_TASK_USERS = "w_task_users";
    public static final String TABLE_LOCATION_PROFILE_PICTURES = "s_Location_ProfilePictures";
    public static final String TABLE_COPIED_FORM_TEMPLATE = "copied_form_templates";
    public static final String TABLE_LOGS_DATA = "temp_logs_data";
    public static final String TABLE_DATA_SYNC_STATUS = "data_sync_status";
    public static final String TABLE_EVENT_LOCATIONS = "s_EventLocations ";
    public static final String TABLE_SITE_FORM_FIELDS = "s_site_form_fields";
    public static final String TABLE_FORM_SITES = "FormSites";

    public static synchronized DbAccess getInstance(Context context) {
        mContext = context;
        if (dbAccess == null) {
            dbAccess = new DbAccess(context);
        }
        return dbAccess;
    }

    private DbAccess(Context context) {
        super();

        dbHelper = new FieldSQLiteHelper(context);
        try {

//			dbHelper.createDbExternal(); //uncomment this to enable db in external storage

            dbHelper.createDataBase(); //comment the following lines to enable db in external storage
            System.out.println("mmm" + "creatingDataBase");

			/* This is not required anymore
			dbHelper.CopyDbFromExternalStorage();
			*/


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("mmm" + "createDataBase failed");
        }
    }


    public void open() {
        dbHelper = new FieldSQLiteHelper(mContext);
        System.out.println("I am in Open db helper");
        try {
            database = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void close() {
        System.out.println("Close db helper");
        dbHelper.close();
    }

}
