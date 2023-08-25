package qnopy.com.qnopyandroid.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CreationOfTable {
    private static final String TAG = "CreationOfTable";
    SQLiteDatabase database;

    public CreationOfTable(Context context) {
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }
    }

    public void createTableBoreDefinition() {
        String fields = "UserID int not null references d_User(UserID), " +
                "EventID int not null references d_Event(EventID), " +
                "MobileAppID int not null references s_MobileApp(MobileAppID), " +
                "LocationID int not null references s_Location(LocationID), " +
                "TotalDepth int, " +
                "DepthDifference int, " +
                "ExtField1 varchar(100), " +
                "ExtField2 varchar(100), " +
                "ExtField3 varchar(100)";
        String sql = "create table if not exists d_BoreDefinition(" + fields + ")";
        try {
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTableBoreDepths() {
        String fields = "UserID int not null references d_User(UserID), " +
                "EventID int not null references d_Event(EventID), " +
                "MobileAppID int not null references s_MobileApp(MobileAppID), " +
                "LocationID int not null references s_Location(LocationID), " +
                "DepthLevels double, " +
                "SetID int, " +
                "ExtField1 varchar(100), " +
                "ExtField2 varchar(100), " +
                "ExtField3 varchar(100)";
        String sql = "create table if not exists d_BoreDepths(" + fields + ")";
        try {
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTableLocFormStatus_IfNotExist() {
        String fields = "'LocationID'  INT(11), " +
                "'MobileAppID' INT(11), " +
                "'StatusID'    INT(11)";
        String sql = "create table if not exists " + DbAccess.TABLE_LOC_FORM_STATUS + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "LocFormStatus Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "LocFormStatus Table Creation Error:" + e.getMessage());
        }
    }

    public void createTableEventLocations() {
        String fields = "'LocationID' INT(11) NOT NULL, " +
                "'EventID' INT NOT NULL, " +
                "'MobileAppID' INT NOT NULL, " +
                "'Location' VARCHAR(1000) NOT NULL, " +
                "'SiteID' INT NOT NULL";
        String sql = "create table if not exists " + DbAccess.TABLE_EVENT_LOCATIONS
                + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_EventLocations Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_EventLocations Table Creation Error:"
                    + e.getMessage());
        }
    }

    //created on 14 June, 2021 for site Specific fields
    public void createTableSiteFormFields() {
        String fields = "`site_form_fields_id` int(11) NOT NULL,\n" +
                "  `is_required` bit(1) DEFAULT NULL,\n" +
                "  `mandatory_field` int(11) DEFAULT NULL,\n" +
                "  `mobile_app_id` int(11) DEFAULT NULL,\n" +
                "  `field_parameter_id` int(11) DEFAULT NULL,\n" +
                "  `calculated_field` varchar(1) DEFAULT NULL,\n" +
                "  `formula` varchar(500) DEFAULT NULL,\n" +
                "  `percent_diff` double DEFAULT NULL,\n" +
                "  `creation_date` bigint(20) DEFAULT NULL,\n" +
                "  `created_by` int(11) DEFAULT NULL,\n" +
                "  `modification_date` bigint(20) DEFAULT NULL,\n" +
                "  `modified_by` int(11) DEFAULT NULL,\n" +
                "  `site_id` int(11) DEFAULT NULL,\n" +
                "  `location_id` varchar(500) DEFAULT NULL,\n" +
                "  `desired_units` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field1` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field2` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field3` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field4` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field5` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field6` varchar(50) DEFAULT NULL,\n" +
                "  `ext_field7` varchar(50) DEFAULT NULL,\n" +
                "  `field_parameter_operands` varchar(500) DEFAULT NULL,\n" +
                "  `enable_parameter_notes` varchar(1) DEFAULT NULL,\n" +
                "  `showLast2` varchar(1) DEFAULT NULL,\n" +
                "  `parameter_hint` varchar(500) DEFAULT NULL,\n" +
                "  `parent_parameter_id` int(11) DEFAULT NULL,\n" +
                "  `routine_id` int(11) DEFAULT NULL,\n" +
                "  `multiNote` varchar(100) DEFAULT NULL,\n" +
                "  `straight_difference` double DEFAULT NULL,\n" +
                "  `field_action` varchar(100) DEFAULT NULL,\n" +
                "  `field_score` varchar(100) DEFAULT NULL,\n" +
                "  `font_style` varchar(100) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`site_form_fields_id`)";

        String sql = "create table if not exists " + DbAccess.TABLE_SITE_FORM_FIELDS
                + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_site_form_fields Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_site_form_fields Table Creation Error:"
                    + e.getMessage());
        }
    }

    public void createTableFormSites() {
        String fields = "`formSiteId` int(11) NOT NULL,\n" +
                "  `siteId` int(11) DEFAULT NULL,\n" +
                "  `formId` int(11) DEFAULT NULL,\n" +
                "  `formName` varchar(100) DEFAULT NULL,\n" +
                "  `status` varchar(1) DEFAULT NULL,\n" +
                "  `isInsert` varchar(1) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`formSiteId`)";

        String sql = "create table if not exists " + DbAccess.TABLE_FORM_SITES
                + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "FormSites Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "FormSites Table Creation Error:"
                    + e.getMessage());
        }
    }

    public void createTableTempLogs() {
        String fields = "'date'  VARCHAR(200), " +
                "'screen_name' VARCHAR(8000), " +
                "'allIds' VARCHAR(8000), " +
                "'details' VARCHAR(8000)";
        String sql = "create table if not exists " + DbAccess.TABLE_LOGS_DATA
                + "(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "temp_logs_data Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "temp_logs_data Table Creation Error:"
                    + e.getMessage());
        }
    }

    public void createTableDownloadDataSyncStatus() {
        String fields = "'eventId'  VARCHAR(20), " +
                "'siteId' VARCHAR(20), " +
                "'lastSyncDate' VARCHAR(100)";

        String sql = "create table if not exists " + DbAccess.TABLE_DATA_SYNC_STATUS + "(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "data_sync_status Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "data_sync_status Table Creation Error:" + e.getMessage());
        }
    }

    public void createTable_s_project_folder_IfNotExist() {

        /* Folder creation query */
        String fields = " folder_id INT(11) NOT NULL , " +
                "                site_id INT(11) NULL, " +
                "                folder_name VARCHAR(100) NULL, " +
                "                folder_guid VARCHAR(100) NOT NULL, " +
                "                parent_id INT(11) NULL, " +
                "                folder_status INT(11) NULL, " +
                "                description VARCHAR(255) NULL, " +
                "                created_by INT NULL," +
                "                creation_date BIGINT(20) NULL, " +
                "                modified_by INT NULL," +
                "                modification_date BIGINT(20) NULL, " +
                "                notes varchar(500), " +
                "                PRIMARY KEY (folder_id)";
        String sql = "create table if not exists " + DbAccess.TABLE_S_PROJECT_FOLDER + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_project_folder Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_project_folder Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_s_LocationFormPercentage_IfNotExist() {

        /* s_LocationFormPercentage creation query */

        String fields = " Loc_Percentage_ID      INTEGER  PRIMARY KEY,\n" +
                "             locationID             VARCHAR (400),\n" +
                "             site_id                VARCHAR (400),\n" +
                "             roll_into_app_id       VARCHAR (400),\n" +
                "             startDate              VARCHAR (4000),\n" +
                "             endDate                VARCHAR (4000),\n" +
                "             percentage_from_server VARCHAR (4000),\n" +
                "             percentage_from_local  VARCHAR (4000) ";
        String sql = "create table if not exists " + DbAccess.TABLE_S_LOCATION_FORM_PERCENTAGE + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_LocationFormPercentage Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_LocationFormPercentage Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_temp_project_folder_IfNotExist() {

        /* Folder creation query */
        String fields = " folder_id INT(11) NOT NULL , " +
                "                site_id INT(11) NULL, " +
                "                folder_name VARCHAR(100) NULL, " +
                "                folder_guid VARCHAR(100) NOT NULL, " +
                "                parent_id INT(11) NULL, " +
                "                folder_status INT(11) NULL, " +
                "                description VARCHAR(255) NULL, " +
                "                created_by INT NULL," +
                "                creation_date BIGINT(20) NULL, " +
                "                modified_by INT NULL," +
                "                modification_date BIGINT(20) NULL, " +
                "                notes varchar(500), " +
                "                PRIMARY KEY (folder_id)";
        String sql = "create table if not exists " + DbAccess.TABLE_TEMP_PROJECT_FOLDER + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "temp_project_folder Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "temp_project_folder Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_s_project_file_IfNotExist() {

        /* Folder creation query */
        String fields = " file_id INT(11) NOT NULL , " +
                "  file_name VARCHAR(255) NULL, " +
                "  file_type VARCHAR(100) NULL, " +
                "  file_status INT(11) NULL, " +
                "  file_description VARCHAR(255) NULL, " +
                "  file_checksum VARCHAR(255) NULL, " +
                "  file_guid VARCHAR(100) NULL, " +
                "  site_id INT(11) NULL, " +
                "  file_path VARCHAR(255) NULL, " +
                "  created_by INT NULL, " +
                "  creation_date BIGINT(20) NULL, " +
                "  modified_by INT NULL, " +
                "  modification_date BIGINT(20) NULL, " +
                "  notes varchar(500)," +
                "  PRIMARY KEY (file_id)";
        String sql = "create table if not exists " + DbAccess.TABLE_S_PROJECT_FILE + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_project_file Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_project_file Table Creation Error:" + e.getMessage());

        }
    }


    public void createTable_s_LocationAttribute_IfNotExist() {

        /* Folder creation query */
        String fields =
                "    locAttributesId INTEGER (11),\n" +
                        "    locationID      INTEGER (11),\n" +
                        "    attributeName   VARCHAR (4000),\n" +
                        "    attributeValue  VARCHAR (4000),\n" +
                        "    createdBy       INTEGER (11),\n" +
                        "    creationDate    DOUBLE,\n" +
                        "    modifiedBy      INTEGER (11),\n" +
                        "    modifiedDate    DOUBLE";
        String sql = "create table if not exists " + DbAccess.TABLE_S_LOCATION_ATTRIBUTE + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_LocationAttribute Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_LocationAttribute Table Creation Error:" + e.getMessage());
        }
    }

    public void alterTable_s_project_file(String column, String dataType, String defaultValue) {

        /* Folder creation query */
        String sql;
        if (defaultValue != null) {
            sql = "ALTER TABLE " + DbAccess.TABLE_S_PROJECT_FILE + "  ADD COLUMN " + column + " " + dataType + " DEFAULT " + defaultValue;

        } else {
            sql = "ALTER TABLE " + DbAccess.TABLE_S_PROJECT_FILE + "  ADD COLUMN " + column + " " + dataType;

        }
//        String sql1 = "ALTER TABLE " + DbAccess.TABLE_S_PROJECT_FILE + "  ADD COLUMN folder_id INT (11) ";
//        CREATE TABLE IF NOT EXISTS
        try {
            if (!isColumnExists(DbAccess.TABLE_S_PROJECT_FILE, column)) {
                database.execSQL(sql);
                Log.i("CreationOfTable", "s_project_file Table Altered.");
            } else {
                Log.i("CreationOfTable", "s_project_file already Table Altered.");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_project_file Table Alteration Error:" + e.getMessage());

        }
    }


    public void alterTable_s_Location() {

        /* Folder creation query */
        String sql = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN  SyncFlag INT (11)";
//        String sqlColumn = "ALTER TABLE " + DbAccess.TABLE_LOCATION + " ALTER COLUMN Latitude VARCHAR (400)";
//        CREATE TABLE IF NOT EXISTS
        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "SyncFlag")) {
                database.execSQL(sql);
                Log.i("CreationOfTable", "s_location Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_location Table Alteration Error:" + e.getMessage());
        }

        String sql2 = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN  status  VARCHAR (100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "status")) {
                database.execSQL(sql2);
                Log.i("CreationOfTable", "s_location Table Altered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_location Table Alteration Error:" + e.getMessage());
        }

        String sql3 = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN  ClientLocationID  VARCHAR (100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "ClientLocationID")) {
                database.execSQL(sql3);
                Log.i("CreationOfTable", "s_location Table Altered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_location Table Alteration Error:" + e.getMessage());
        }
/*        String sql4 = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN  locationType  VARCHAR (100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "locationType")) {
                database.execSQL(sql4);
                Log.i("CreationOfTable", "s_location Table Altered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_location Table Alteration Error:" + e.getMessage());
        }*/


        String sqlLocationTab = "ALTER TABLE " + DbAccess.TABLE_LOCATION + " ADD COLUMN location_tabs VARCHAR (100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "location_tabs")) {
                database.execSQL(sqlLocationTab);
                Log.i("CreationOfTable", "s_location Table Altered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_location Table Alteration Error:" + e.getMessage());
        }
    }

    public void alterTable_temp_project_file() {

        /* Folder creation query */
        String sql = "ALTER TABLE " + DbAccess.TABLE_TEMP_PROJECT_FILE + "  ADD COLUMN  folder_id  INT (11) ";
//        CREATE TABLE IF NOT EXISTS
        try {
            if (!isColumnExists(DbAccess.TABLE_TEMP_PROJECT_FILE, "folder_id")) {
                database.execSQL(sql);
                Log.i("Alter Table", "temp_project_file Table Altered.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_project_file Table Alteration Error:" + e.getMessage());
        }
    }

    //added fileName, cocId to notifications table 07 Apr, 22
    //added col fieldUUID to d_FieldData 01 April, 22
    //added two columns warningValue and violationFlag on 8 November, 21
    //added formQuery on 14 Dec, 21
    public void addColumns() {

        //added on 08 Dec, 22
        String sqlSetId = "ALTER TABLE " + DbAccess.TABLE_S_DEFAULT_VALUES + " ADD COLUMN " +
                "setId VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_S_DEFAULT_VALUES, "setId"))
                database.execSQL(sqlSetId);
            Log.i(TAG, "DefaultValues Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "DefaultValues Table Alteration Error:" + e.getMessage());
        }

        //added on 15 Oct, 22
        String sqlFile1000Loc = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + " ADD COLUMN " +
                "file1000Loc VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "file1000Loc"))
                database.execSQL(sqlFile1000Loc);
            Log.i(TAG, "Attachments Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Attachments Table Alteration Error:" + e.getMessage());
        }

        //added on 15 Oct, 22
        String sqlThumbLoc = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + " ADD COLUMN " +
                "fileThumbLoc VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "fileThumbLoc"))
                database.execSQL(sqlThumbLoc);
            Log.i(TAG, "Attachments Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Attachments Table Alteration Error:" + e.getMessage());
        }

        //added on 21 Sep, 22
        String sqlFormula = "ALTER TABLE " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " ADD COLUMN " +
                "formula VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA_ATTRIBUTES, "formula"))
                database.execSQL(sqlFormula);
            Log.i(TAG, "MetaAttr Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "MetaAttr Table Alteration Error:" + e.getMessage());
        }

        //added on 21 Sep, 22
        String sqlStraightDiff = "ALTER TABLE " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " ADD COLUMN " +
                "straightDifference VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA_ATTRIBUTES, "straightDifference"))
                database.execSQL(sqlStraightDiff);
            Log.i(TAG, "MetaAttr Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "MetaAttr Table Alteration Error:" + e.getMessage());
        }

        //added on 21 Sep, 22
        String sqlFontStyle = "ALTER TABLE " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " ADD COLUMN " +
                "fontStyle VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA_ATTRIBUTES, "fontStyle"))
                database.execSQL(sqlFontStyle);
            Log.i(TAG, "MetaAttr Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "MetaAttr Table Alteration Error:" + e.getMessage());
        }

        //added on 21 Sep, 22
        String sqlFieldAction = "ALTER TABLE " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " ADD COLUMN " +
                "fieldAction VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA_ATTRIBUTES, "fieldAction"))
                database.execSQL(sqlFieldAction);
            Log.i(TAG, "MetaAttr Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "MetaAttr Table Alteration Error:" + e.getMessage());
        }

        //added on 20 Sep, 22 - value to detect if note only needs to update
        String sqlAttachmentNoteUpdate = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + " ADD COLUMN " +
                "noteUpdate INT(1)";

        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "noteUpdate"))
                database.execSQL(sqlAttachmentNoteUpdate);
            Log.i(TAG, "Attachments Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Attachments Table Alteration Error:" + e.getMessage());
        }

        //added on 27 July, 22
        String sqlAttachmentUUID = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + " ADD COLUMN " +
                "attachmentUUID VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "attachmentUUID"))
                database.execSQL(sqlAttachmentUUID);
            Log.i(TAG, "Attachments Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Attachments Table Alteration Error:" + e.getMessage());
        }

        //added on 07 July, 22
        String sqlAppType = "ALTER TABLE " + DbAccess.TABLE_FORM_SITES + " ADD COLUMN appType VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_FORM_SITES, "appType"))
                database.execSQL(sqlAppType);
            Log.i(TAG, "Form sites Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Form sites Table Alteration Error:" + e.getMessage());
        }

        String sqlApprovalRequired = "ALTER TABLE " + DbAccess.TABLE_FORM_SITES
                + " ADD COLUMN approvalRequired VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_FORM_SITES, "approvalRequired"))
                database.execSQL(sqlApprovalRequired);
            Log.i(TAG, "Form sites Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Form sites Table Alteration Error:" + e.getMessage());
        }

        String sqlAppDescription = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP
                + " ADD COLUMN appDescription VARCHAR(100)";

        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "appDescription"))
                database.execSQL(sqlAppDescription);
            Log.i(TAG, "Site Mobile App Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "Site Mobile App Table Alteration Error:" + e.getMessage());
        }//end

        String sqlCOCId = "ALTER TABLE " + DbAccess.TABLE_NOTIFICATIONS + " ADD COLUMN cocId INT";

        try {
            if (!isColumnExists(DbAccess.TABLE_NOTIFICATIONS, "cocId"))
                database.execSQL(sqlCOCId);
            Log.i(TAG, "notifications Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "notifications Table Alteration Error:" + e.getMessage());
        }

        String sqlFileName = "ALTER TABLE " + DbAccess.TABLE_NOTIFICATIONS + " ADD COLUMN fileName VARCHAR(2000)";

        try {
            if (!isColumnExists(DbAccess.TABLE_NOTIFICATIONS, "fileName"))
                database.execSQL(sqlFileName);
            Log.i(TAG, "notifications Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "notifications Table Alteration Error:" + e.getMessage());
        }

        String sqlFieldUUID = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + " ADD COLUMN fieldUUID VARCHAR(2000)";

        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "fieldUUID"))
                database.execSQL(sqlFieldUUID);
            Log.i(TAG, "d_FieldData Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_FieldData Table Alteration Error:" + e.getMessage());
        }

        String sqlFormQuery = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP + " ADD COLUMN formQuery VARCHAR(2000) ";

        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "formQuery"))
                database.execSQL(sqlFormQuery);
            Log.i(TAG, "siteMobileApp Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "siteMobileApp Table Alteration Error:" + e.getMessage());
        }

        String sqlViolationFlag = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + " ADD COLUMN violationFlag int ";

        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "violationFlag"))
                database.execSQL(sqlViolationFlag);
            Log.i(TAG, "d_FieldData Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_FieldData Table Alteration Error:" + e.getMessage());
        }

        String sqlWarningValue = "ALTER TABLE " + DbAccess.TABLE_S_DEFAULT_VALUES
                + " ADD COLUMN warningValue VARCHAR(100) ";

        try {
            if (!isColumnExists(DbAccess.TABLE_S_DEFAULT_VALUES, "warningValue"))
                database.execSQL(sqlWarningValue);
            Log.i(TAG, "s_DefaultValues Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_DefaultValues Table Alteration Error:" + e.getMessage());
        }

//        String addFavouriteSql = "ALTER TABLE " + DbAccess.TABLE_SITE_USER_ROLE + " ADD COLUMN favouriteStatus String";
//        try {
//            if (!isColoumnExistsInTable(DbAccess.TABLE_SITE_USER_ROLE, "favouriteStatus"))
//                database.execSQL(addFavouriteSql);
//            Log.i(TAG, "TABLE_S_PROJECT_FOLDER Table Altered.");
//        } catch (SQLException e) {
//            e.printStackTrace();
//            Log.i(TAG, "TABLE_S_PROJECT_FOLDER Table Alteration Error:" + e.getMessage());
//        }

    }

    //02-Mar-16 Alter Table Add new Colomns
    public void alterTable_metaSync() {
        /* Folder creation query */
        String sql = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN  loc_instruction varchar(100) ";
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN  loc_form_header varchar(100) ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_LOCATION + "  ADD COLUMN   well_diameter float ";
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_MOBILE_APPS + "  ADD COLUMN  label_width float ";
        String sql4 = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP + "  ADD COLUMN  app_type varchar(20) ";
        String sql5 = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP + "  ADD COLUMN  allow_multiple_sets bit(1) ";
        String sql6 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  site_id int ";
        String sql7 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  company_id int ";

        // TODO: 29-Nov-16
        String sql8 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  parentLovItemId int ";
        String sql9 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  formId int ";
        String sql10 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + "  ADD COLUMN  Reviewer BOOLEAN ";
        String sql11 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  l_item_id int ";

        String sql12 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + "  ADD COLUMN  mandatoryField int ";

        String sql13 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + "  ADD COLUMN  multinote VARCHAR(1000)";
        String sql14 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  syncflag int ";
        String sql15 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  modified_by  int ";
        String sql16 = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + "  ADD COLUMN  modification_date LONG ";
        String sql17 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + "  ADD COLUMN  locationIds VARCHAR(1000)";
        String sql18 = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  bottlesCheckOptions VARCHAR(1000)";
        String sql19 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + " ADD COLUMN enableParameterTasks VARCHAR(1)";
        String sql20 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + " ADD COLUMN straight_difference VARCHAR(1000)";
        String sql21 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + " ADD COLUMN field_action VARCHAR(1000)";
        String sql22 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + " ADD COLUMN field_score VARCHAR(1000)";
        String sql23 = "ALTER TABLE " + DbAccess.TABLE_META_DATA + " ADD COLUMN font_style VARCHAR(1000)";
        String sqlHeaderFlag = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP + "  ADD COLUMN headerFlag int ";
        String sqlFormDefault = "ALTER TABLE " + DbAccess.TABLE_LOCATION + " ADD COLUMN FormDefault int ";

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "straight_difference"))
                database.execSQL(sql20);
            Log.i(TAG, "s_Metadata Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Metadata Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "field_action"))
                database.execSQL(sql21);
            Log.i(TAG, "s_Metadata Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Metadata Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "field_score"))
                database.execSQL(sql22);
            Log.i(TAG, "s_Metadata Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Metadata Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "font_style"))
                database.execSQL(sql23);
            Log.i(TAG, "s_Metadata Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Metadata Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "enableParameterTasks"))
                database.execSQL(sql19);
            Log.i(TAG, "s_Metadata Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Metadata Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "bottlesCheckOptions"))
                database.execSQL(sql18);
            Log.i(TAG, "d_FieldData Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_FieldData Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "multinote")) {
                database.execSQL(sql13);
                Log.i("alterTable_s_Metadata", "s_MetaData Table Altered by multinote");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_Metadata", "s_MetaData Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "locationIds")) {
                database.execSQL(sql17);
                Log.i("alterTable_s_Metadata", "s_MetaData Table Altered by locationIds");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_Metadata", "s_MetaData Table Alteration Error:" + e.getMessage());
        }

//        CREATE TABLE IF NOT EXISTS
        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "loc_instruction")) {
                database.execSQL(sql);
                Log.i(TAG, "s_Location Table Altered.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Location Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "loc_form_header")) {
                database.execSQL(sql1);
                Log.i(TAG, "s_Location Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Location Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "well_diameter")) {
                database.execSQL(sql2);
                Log.i(TAG, "s_Location Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Location Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION, "FormDefault")) {
                database.execSQL(sqlFormDefault);
                Log.i(TAG, "s_Location Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_Location Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_MOBILE_APPS, "label_width"))
                database.execSQL(sql3);
            Log.i(TAG, "s_MobileApp Table Altered.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_MobileApp Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "headerFlag")) {
                database.execSQL(sqlHeaderFlag);
                Log.i(TAG, "s_SiteMobileApp Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_SiteMobileApp Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "app_type")) {
                database.execSQL(sql4);
                Log.i(TAG, "s_SiteMobileApp Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_SiteMobileApp Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "allow_multiple_sets")) {
                database.execSQL(sql5);
                Log.i(TAG, "s_SiteMobileApp Table Altered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_SiteMobileApp Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "site_id"))
                database.execSQL(sql6);
            Log.i(TAG, "s_LovItems Table Altered.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_LovItems Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "company_id"))
                database.execSQL(sql7);
            Log.i(TAG, "s_LovItems Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_LovItems Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "parentLovItemId"))
                database.execSQL(sql8);
            Log.i(TAG, "s_LovItems Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_LovItems Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "formId"))
                database.execSQL(sql9);
            Log.i(TAG, "s_LovItems Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_LovItems Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "Reviewer"))
                database.execSQL(sql10);
            Log.i(TAG, "s_MetaData Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_MetaData Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "l_item_id"))
                database.execSQL(sql11);
            Log.i(TAG, "s_LovItems Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_LovItems Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "mandatoryField"))
                database.execSQL(sql12);
            Log.i(TAG, "s_MetaData Table Altered.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_MetaData Table Alteration Error:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "syncflag"))
                database.execSQL(sql14);
            Log.i(TAG, "s_lovitem Table Altered.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_lovitem Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "modified_by"))
                database.execSQL(sql15);
            Log.i(TAG, "s_lovitem Table Altered.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_lovitem Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "modification_date"))
                database.execSQL(sql16);
            Log.i(TAG, "s_lovitem Table Altered.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "s_lovitem Table Alteration Error:" + e.getMessage());
        }
    }

    //added on 24 Aug, 21
    public void alterTableAddNewCols() {

        //added on 30 Aug, 22
        String colEventUserName = "ALTER TABLE " + DbAccess.TABLE_EVENT
                + " ADD COLUMN 'eventUserName' VARCHAR(100)";
        try {
            if (!isColumnExists(DbAccess.TABLE_EVENT, "eventUserName")) {
                database.execSQL(colEventUserName);
                Log.i("Alter Event", "Table Altered by colEventUserName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Event",
                    "Table Alteration Error for Status: " + e.getMessage());
        }

        //added on 30 Aug, 22
        String colCaptionRequired = "ALTER TABLE " + DbAccess.TABLE_SITES
                + " ADD COLUMN 'captionRequired' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_SITES, "captionRequired")) {
                database.execSQL(colCaptionRequired);
                Log.i("Alter Site", "Table Altered by captionRequired");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Event",
                    "Table Alteration Error for Status: " + e.getMessage());
        }

        //added on 9 June, 22
        String colFileKeyEncode = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT
                + " ADD COLUMN 'fileKeyEncode' VARCHAR(100)";
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "fileKeyEncode")) {
                database.execSQL(colFileKeyEncode);
                Log.i("Alter Attachment", "Table Altered by fileKeyEncode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Attachment",
                    "Table Alteration Error for Status: " + e.getMessage());
        }

        String colFileKeyThumbEncode = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT
                + " ADD COLUMN 'fileKeyThumbEncode' VARCHAR(100)";
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "fileKeyThumbEncode")) {
                database.execSQL(colFileKeyThumbEncode);
                Log.i("Alter Attachment", "Table Altered by fileKeyThumbEncode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Attachment",
                    "Table Alteration Error for Status: " + e.getMessage());
        }

        String colOriginalFileName = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT
                + " ADD COLUMN 'originalFileName' VARCHAR(100)";
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "originalFileName")) {
                database.execSQL(colOriginalFileName);
                Log.i("Alter Attachment", "Table Altered by originalFileName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Attachment",
                    "Table Alteration Error for Status: " + e.getMessage());
        }

        String colFileKey = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT
                + " ADD COLUMN 'fileKey' VARCHAR(100)";
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "fileKey")) {
                database.execSQL(colFileKey);
                Log.i("Alter Attachment", "Table Altered by fileKey");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Attachment",
                    "Table Alteration Error for Status: " + e.getMessage());
        }

        String colFileKeyThumb = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT
                + " ADD COLUMN 'fileKeyThumb' VARCHAR(100)";
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "fileKeyThumb")) {
                database.execSQL(colFileKeyThumb);
                Log.i("Alter Attachment", "Table Altered by fileKeyThumb");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("Alter Attachment",
                    "Table Alteration Error for Status: " + e.getMessage());
        }
        //end attachment cols

        String colStatusLocAttr = "ALTER TABLE " + DbAccess.TABLE_S_LOCATION_ATTRIBUTE
                + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_S_LOCATION_ATTRIBUTE, "Status")) {
                database.execSQL(colStatusLocAttr);
                Log.i("alterTableLocAttr", "alterTableLocAttr Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableLocAttr",
                    "alterTableLocAttr Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatus = "ALTER TABLE " + DbAccess.TABLE_MOBILE_APPS + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_MOBILE_APPS, "Status")) {
                database.execSQL(colStatus);
                Log.i("alterTables_MobileApp", "alterTables_MobileApp Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTables_MobileApp", "alterTables_MobileApp Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusSiteMob = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "Status")) {
                database.execSQL(colStatusSiteMob);
                Log.i("alterTableSiteMobApp", "alterTables_MobileApp Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableSiteMobApp", "alterTables_MobileApp Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusMetaData = "ALTER TABLE " + DbAccess.TABLE_META_DATA + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA, "Status")) {
                database.execSQL(colStatusMetaData);
                Log.i("alterTableMetaData", "alterTables_MobileApp Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableMetaData", "alterTables_MobileApp Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusLovItems = "ALTER TABLE " + DbAccess.TABLE_LOV_ITEMS + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_LOV_ITEMS, "Status")) {
                database.execSQL(colStatusLovItems);
                Log.i("alterTableLovItem", "alterTables_MobileApp Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableLovItem", "alterTables_MobileApp Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusDefVal = "ALTER TABLE " + DbAccess.TABLE_S_DEFAULT_VALUES + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_S_DEFAULT_VALUES, "Status")) {
                database.execSQL(colStatusDefVal);
                Log.i("alterTableDefVal", "alterTableDefVal Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableDefVal", "alterTableDefVal Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusSiteUser = "ALTER TABLE " + DbAccess.TABLE_SITE_USER_ROLE + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_USER_ROLE, "Status")) {
                database.execSQL(colStatusSiteUser);
                Log.i("alterTableSiteUser", "alterTableSiteUser Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableSiteUser", "alterTableSiteUser Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusLov = "ALTER TABLE " + DbAccess.TABLE_S_LOV + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_S_LOV, "Status")) {
                database.execSQL(colStatusLov);
                Log.i("alterTableLov", "alterTableLov Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableLov", "alterTableLov Table Alteration Error for Status: " + e.getMessage());
        }

        String colStatusMetaAttr = "ALTER TABLE " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA_ATTRIBUTES, "Status")) {
                database.execSQL(colStatusMetaAttr);
                Log.i("alterTableMetaAttr", "alterTableMetaAttr Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableMetaAttr", "alterTableMetaAttr Table Alteration Error for Status: " + e.getMessage());
        }

        //added on 21 Oct, 2021 - value to hide a particular form field
        String colHideMetaAttr = "ALTER TABLE " + DbAccess.TABLE_META_DATA_ATTRIBUTES + " ADD COLUMN 'hide' INT(11)";
        try {
            if (!isColumnExists(DbAccess.TABLE_META_DATA_ATTRIBUTES, "hide")) {
                database.execSQL(colHideMetaAttr);
                Log.i("alterTableMetaAttr", "alterTableMetaAttr Table Altered by hide");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableMetaAttr", "alterTableMetaAttr Table Alteration Error for hide: " + e.getMessage());
        }

        String colStatusProfPict = "ALTER TABLE " + DbAccess.TABLE_LOCATION_PROFILE_PICTURES + " ADD COLUMN 'Status' INT(1)";
        try {
            if (!isColumnExists(DbAccess.TABLE_LOCATION_PROFILE_PICTURES, "Status")) {
                database.execSQL(colStatusProfPict);
                Log.i("alterTableProfPict", "alterTableProfPict Table Altered by Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableProfPict", "alterTableProfPict Table Alteration Error for Status: " + e.getMessage());
        }


        String colLastSyncType = "ALTER TABLE " + DbAccess.TABLE_D_SYNC_STATUS + " ADD COLUMN 'Type' VARCHAR(50)";
        try {
            if (!isColumnExists(DbAccess.TABLE_D_SYNC_STATUS, "Type")) {
                database.execSQL(colLastSyncType);
                Log.i("alterTableDSYNC", "alterTableDSYNC Table Altered by Type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableDSYNC", "alterTableDSYNC Table Alteration Error for Type: " + e.getMessage());
        }
    }

    public void createTable_d_fielddata_conflict_IfNotExist() {

        /* Folder creation query */
        String fields = "  eventId  INT    NOT NULL   DEFAULT (NULL)," +
                "    locationId INT NOT NULL DEFAULT (NULL)," +
                "    fieldParameterId         INT           NOT NULL  DEFAULT (NULL)," +
                "    siteId                   INT           NOT NULL DEFAULT (NULL)," +
                "    userId                   INT           NOT NULL DEFAULT (NULL)," +
                "    mobileAppId              INT           NOT NULL DEFAULT (NULL)," +
                "    fieldParameterLabel      VARCHAR (100) DEFAULT (NULL)," +
                "    measurementTime          LONG          NOT NULL DEFAULT (NULL)," +
                "    stringValue              VARCHAR (100) DEFAULT (NULL)," +
                "    numericValue             REAL          DEFAULT (NULL)," +
                "    units                    VARCHAR (50)  DEFAULT (NULL)," +
                "    latitude                 REAL          DEFAULT (NULL)," +
                "    longitude                REAL          DEFAULT (NULL)," +
                "    extField1                VARCHAR (100) DEFAULT (NULL)," +
                "    extField2                VARCHAR (100) DEFAULT (NULL)," +
                "    extField3                VARCHAR (100) DEFAULT (NULL)," +
                "    extField4                VARCHAR (100) DEFAULT (NULL)," +
                "    extField5                VARCHAR (100) DEFAULT (NULL)," +
                "    extField6                VARCHAR (100) DEFAULT (NULL)," +
                "    extField7                VARCHAR (100) DEFAULT (NULL)," +
                "    notes                    VARCHAR (200) DEFAULT (NULL)," +
                "    creationDate             LONG          DEFAULT (NULL)," +
                "    modificationDate         LONG          DEFAULT (NULL)," +
                "    emailSentFlag            VARCHAR (1)   DEFAULT (NULL)," +
                "    dataSyncFlag             VARCHAR (1)   DEFAULT (NULL)," +
                "    fieldDataId              INTEGER       DEFAULT (NULL)," +
                "    parent_set_id            INT," +
                "    correctedLongitude       REAL   DEFAULT (NULL)," +
                "    correctedLatitude        REAL          DEFAULT (NULL)," +
                "    server_creation_date     LONG," +
                "    server_modification_date LONG," +
                "    deviceId  VARCHAR (50)  DEFAULT (NULL) ";
        String sql = "create table if not exists " + DbAccess.TABLE_D_FIELD_DATA_CONFLICT + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "d_field_data_conflict Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "d_field_data_conflict Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_d_field_data_temp_IfNotExist() {

        /* Folder creation query */
        String fields = "  eventId   INT  NOT NULL  DEFAULT (NULL)," +
                "locationId INT  NOT NULL  DEFAULT (NULL)," +
                " fieldParameterId  INT    NOT NULL  DEFAULT (NULL)," +
                " siteId INT NOT NULL   DEFAULT(NULL)," +
                "  userId INT NOT NULL  DEFAULT(NULL)," +
                "  mobileAppId INT NOT NULL    DEFAULT(NULL)," +
                "  fieldParameterLabel VARCHAR(100) DEFAULT(NULL)," +
                "  measurementTime LONG NOT NULL    DEFAULT(NULL)," +
                "  stringValue VARCHAR(100) DEFAULT(NULL)," +
                "  numericValue REAL DEFAULT (NULL)," +
                "  units VARCHAR(50) DEFAULT(NULL)," +
                "  latitude REAL DEFAULT (NULL)," +
                "  longitude REAL DEFAULT (NULL)," +
                "  extField1 VARCHAR(100) DEFAULT(NULL)," +
                "  extField2 VARCHAR(100) DEFAULT(NULL)," +
                "  extField3 VARCHAR(100) DEFAULT(NULL)," +
                "  extField4 VARCHAR(100) DEFAULT(NULL)," +
                "  extField5 VARCHAR(100) DEFAULT(NULL)," +
                "  extField6 VARCHAR(100) DEFAULT(NULL)," +
                "  extField7 VARCHAR(100) DEFAULT(NULL)," +
                "  notes VARCHAR(200) DEFAULT(NULL)," +
                "  creationDate LONG DEFAULT (NULL)," +
                "  modificationDate LONG DEFAULT (NULL)," +
                "  emailSentFlag VARCHAR(1) DEFAULT(NULL)," +
                "  dataSyncFlag VARCHAR(1) DEFAULT(NULL)," +
                "  fieldDataId INTEGER DEFAULT (NULL)," +
                "  parent_set_id INT," +
                "  correctedLongitude REAL DEFAULT (NULL)," +
                "  correctedLatitude REAL DEFAULT (NULL)," +
                "  server_creation_date LONG," +
                "  server_modification_date LONG," +
                " deviceId VARCHAR(50) DEFAULT(NULL) ";
        String sql = "create table if not exists " + DbAccess.TABLE_TEMP_D_FIELD_DATA + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "temp_d_field_data Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "temp_d_field_data Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_sync_status_IfNotExist() {

        /* Folder creation query */
        String fields = "  Sync_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " User_id        INTEGER," +
                "  last_sync_date LONG    DEFAULT 0," +
                " Event_id       INTEGER";

        String sql = "create table if not exists " + DbAccess.TABLE_D_SYNC_STATUS + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "d_sync_status Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "d_sync_status Table Creation Error:" + e.getMessage());

        }
    }


    public void createTable_d_SampleMapTag_IfNotExist() {

        /* Folder creation query */
        String fields =
                "   SampleTagID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                        "   LocationID   VARCHAR (100),\n" +
                        "    EventID      VARCHAR (100),\n" +
                        "    SiteID       VARCHAR (100),\n" +
                        "    UserID       VARCHAR (100),\n" +
                        "    MobAppID     VARCHAR (100),\n" +
                        "    FieldParamID VARCHAR (100),\n" +
                        "    SampleValue  VARCHAR (101),\n" +
                        "    FilePath     VARCHAR (1000),\n" +
                        "    Latitude     REAL,\n" +
                        "    Longitude    REAL,\n" +
                        "    SetID        VARCHAR (100)  DEFAULT (1)";

        String sql = "create table if not exists " + DbAccess.TABLE_D_SAMPLE_MAPTAG + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "d_SampleMapTag Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "d_SampleMapTag Table Creation Error:" + e.getMessage());

        }
    }


    public void createTable_s_DefaultValues_IfNotExist() {

        /* Folder creation query */
        String fields =
                "    DefaultvalueID   INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                        "    LocationID       INT (100)," +
                        "    MobileAppID      INT (100)," +
                        "    upperLimit       VARCHAR (1000)," +
                        "    lowerLimit       VARCHAR (1000)," +
                        "    defaultValue     VARCHAR (1000)," +
                        "    fieldParameterID VARCHAR (1000) ";

        String sql = "create table if not exists " + DbAccess.TABLE_S_DEFAULT_VALUES + "(" + fields + ")";
        //CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_Default_Values Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_Default_Values Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_notifications_IfNotExist() {

        /* Folder creation query */
        String fields = " Notification_ID     INTEGER        PRIMARY KEY AUTOINCREMENT," +
                "    Notification_Title  VARCHAR (4000)," +
                "    Notification_Info   VARCHAR (4000)," +
                "    Notification_Date   LONG," +
                "    Notification_Status INTEGER (100),\n" +
                "    Operation_Code      INTEGER (100)  DEFAULT (0), " +
                "    User_ID             INTEGER (100) ";

        String sql = "create table if not exists " + DbAccess.TABLE_NOTIFICATIONS + "(" + fields + ")";
        //CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "notifications Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "notifications Table Creation Error:" + e.getMessage());

        }
    }


    public void createTable_s_work_order_task_IfNotExist() {

        String fields =
                "    LocationID       INT,\n" +
                        "    Location         VARCHAR (1000),\n" +
                        "    TaskID           INT,\n" +
                        "    TaskDesc         VARCHAR (1000),\n" +
                        "    wo_id            INT,\n" +
                        "    wo_task_id       INT,\n" +
                        "    PlanName         VARCHAR (1000),\n" +
                        "    TaskName         VARCHAR (1000),\n" +
                        "    CocFlag          INT,\n" +
                        "    UserID           INT,\n" +
                        "    Latitude         REAL,\n" +
                        "    Longitude        REAL,\n" +
                        "    loc_instruction  VARCHAR (100),\n" +
                        "    planStartDate    VARCHAR (1000),\n" +
                        "    planEndDate      VARCHAR (1000),\n" +
                        "    parentAppID      INTEGER,\n" +
                        "    status           VARCHAR (100),\n" +
                        "    wo_planStartDate VARCHAR (1000),\n" +
                        "    wo_planEndDate   VARCHAR (1000)";

        String sql = "CREATE TABLE IF NOT EXISTS " + DbAccess.TABLE_WORK_ORDER_TASK_NEW + "(" + fields + ")";
        try {
            database.execSQL(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createTable_cm_coc_details_IfNotExist() {

        String fields =
                "    coc_details_id           VARCHAR        NOT NULL,\n" +
                        "    coc_id                   INT (11)       DEFAULT NULL,\n" +
                        "    location_id              INT (11)       DEFAULT NULL,\n" +
                        "    method_id                INT (11)       DEFAULT NULL,\n" +
                        "    wo_task_id               INT (11)       DEFAULT NULL,\n" +
                        "    wo_id                    INT (11)       DEFAULT NULL,\n" +
                        "    creation_date            BIGINT (20)    DEFAULT NULL,\n" +
                        "    created_by               INT (11)       DEFAULT NULL,\n" +
                        "    modification_date        BIGINT (20)    DEFAULT NULL,\n" +
                        "    modified_by              INT (11)       DEFAULT NULL,\n" +
                        "    modification_notes       VARCHAR (2000) DEFAULT NULL,\n" +
                        "    coc_remarks              VARCHAR (500)  DEFAULT NULL,\n" +
                        "    preservatives            VARCHAR (200)  DEFAULT NULL,\n" +
                        "    container                VARCHAR (200)  DEFAULT NULL,\n" +
                        "    method                   VARCHAR (200)  DEFAULT NULL,\n" +
                        "    sample_id                VARCHAR (200)  DEFAULT NULL,\n" +
                        "    sample_date              VARCHAR (200)  DEFAULT NULL,\n" +
                        "    sample_time              VARCHAR (200)  DEFAULT NULL,\n" +
                        "    delete_flag              INT (1)        DEFAULT NULL,\n" +
                        "    matrix                   VARCHAR (200)  DEFAULT NULL,\n" +
                        "    status                   VARCHAR (50)   DEFAULT NULL,\n" +
                        "    coc_flag                 INT (11)       DEFAULT NULL,\n" +
                        "    server_creation_date     BIGINT (20)    DEFAULT NULL,\n" +
                        "    server_modification_date BIGINT (20)    DEFAULT NULL,\n" +
                        "   field_parameter_id       INTEGER (100),\n" +
                        "   dup_flag                 INTEGER (100)";


        String sql = "create table if not exists " + DbAccess.TABLE_CM_COC_DETAILS + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "cm_coc_details Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "cm_coc_details Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_cm_methods_IfNotExist() {

        String fields =
                "             cm_methods_id INTEGER       NOT NULL PRIMARY KEY,\n" +
                        "             analyses      VARCHAR (500),\n" +
                        "             methods       VARCHAR (500),\n" +
                        "             container     VARCHAR (500),\n" +
                        "             sugg_qty      VARCHAR (100),\n" +
                        "             preservative  VARCHAR (100),\n" +
                        "             hold_time     VARCHAR (200),\n" +
                        "             created_by    BIGINT (11),\n" +
                        "             creation_date BIGINT (20),\n" +
                        "             modified_by   INT (11),\n" +
                        "             modified_date BIGINT (20)";

        String sql = "create table if not exists " + DbAccess.TABLE_CM_METHODS + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS

        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "cm_methods Table Created.");

/*            if (!isMethodsAlreadyExist()) {
                addCoCMethods();
            }*/

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("CreationOfTable", "cm_methods Table Creation Error:" + e.getMessage());
        }

        alterCMMethodsTableColumns();
    }

    public void alterCMMethodsTableColumns() {
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_CM_METHODS
                + " ADD COLUMN labName VARCHAR(1000) DEFAULT NULL ";

        try {
            if (!isColumnExists(DbAccess.TABLE_CM_METHODS, "labName"))
                database.execSQL(sql1);
            Log.i("cm_methods table", "cm_methods table Altered by labName");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("cm_methods table", "cm_methods table Alteration Error:" + e.getMessage());
        }

        String sql2 = "ALTER TABLE " + DbAccess.TABLE_CM_METHODS
                + " ADD COLUMN matrix VARCHAR(1000) DEFAULT NULL ";
        try {
            if (!isColumnExists(DbAccess.TABLE_CM_METHODS, "matrix"))
                database.execSQL(sql2);
            Log.i("cm_methods table", "cm_methods table Altered by matrix");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("cm_methods table", "cm_methods table Alteration Error:" + e.getMessage());
        }

        String sql3 = "ALTER TABLE " + DbAccess.TABLE_CM_METHODS
                + " ADD COLUMN noOfContainer VARCHAR(1000) DEFAULT NULL ";
        try {
            if (!isColumnExists(DbAccess.TABLE_CM_METHODS, "noOfContainer"))
                database.execSQL(sql3);
            Log.i("cm_methods table", "cm_methods table Altered by noOfContainer");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("cm_methods table", "cm_methods table Alteration Error:" + e.getMessage());
        }
    }

    private boolean isMethodsAlreadyExist() {

        int count = 0;
        String query = "select count(cm_methods_id) from " + DbAccess.TABLE_CM_METHODS;
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count > 0;
    }

    public void addCoCMethods() {

        int count = 0;
        String query = "INSERT INTO `cm_methods` (`cm_methods_id`, `analyses`, `methods`, `container`, `sugg_qty`, `preservative`, `hold_time`, `created_by`, `creation_date`, `modified_by`, `modified_date`) VALUES\n" +
                "(1, 'Alkalinity', 'Alkalinity by SM2320B', 'Plastic', '250 ml', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(2, 'Ammonia', 'Ammonia by 350.1', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(3, 'Ammonia', 'Ammonia by  SM4500NH3-BH', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(4, 'Biological Oxygen Demand (BOD)', 'Biological Oxygen Demand (BOD) by SM5210B', 'Plastic', '500 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(5, 'Chemical Oxygen Demand (COD)', 'Chemical Oxygen Demand (COD) by 410.4', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(6, 'Chemical Oxygen Demand (COD)', 'Chemical Oxygen Demand (COD) by  SM5220D', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(7, 'Chloride', 'Chloride by 300.0', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(8, 'Chloride', 'Chloride by  9056', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(9, 'Chloride', 'Chloride by  9251', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(10, 'Chloride', 'Chloride by  SM4500Cl-E', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(11, 'Chlorophyll-a', 'Chlorophyll-a by SM10200H', 'Dark Glass/Plastic or foil-covered container', '500 ml', '4 deg C', 'Filtration within 24 hours. Filtrate may be stored up to 3 Days, or stored frozen up to 3 Weeks.', NULL, NULL, NULL, NULL),\n" +
                "(12, 'Cyanide, Amenable', 'Cyanide, Amenable by 9010B', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(13, 'Cyanide, Amenable', 'Cyanide, Amenable by  SM4500CN-G', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(14, 'Cyanide, Free', 'Cyanide, Free by 9016', 'Plastic covered with foil or Amber Glass', '250 ml', '50% NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(15, 'Cyanide, Free', 'Cyanide, Free by  SM4500CN-CE(M)', 'Plastic covered with foil or Amber Glass', '250 ml', '50% NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(16, 'Cyanide, Physiologically Available (PACN)', 'Cyanide, Physiologically Available (PACN) by 9010B/C', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(17, 'Cyanide, Physiologically Available (PACN)', 'Cyanide, Physiologically Available (PACN) by  9014', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(18, 'Cyanide, Total', 'Cyanide, Total by 9010B/C', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(19, 'Cyanide, Total', 'Cyanide, Total by  9012A', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(20, 'Cyanide, Total', 'Cyanide, Total by  9014', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(21, 'Cyanide, Total', 'Cyanide, Total by  SM4500CN-CE', 'Plastic', '250 ml', 'NaOH, pH>12, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(22, 'Dissolved Oxygen (DO)', 'Dissolved Oxygen (DO) by SM4500O-C', 'BOD bottle with cap', '300 ml', 'Powder pillows, 4 deg C', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(23, 'Fluoride', 'Fluoride by 300.0', 'Plastic', '500 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(24, 'Fluoride', 'Fluoride by  SM4500F-B', 'Plastic', '500 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(25, 'Fluoride', 'Fluoride by  BC', 'Plastic', '500 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(26, 'Formaldehyde', 'Formaldehyde by 8315A', 'Amber Glass', '1000 ml', '4 deg C', '3 Days', NULL, NULL, NULL, NULL),\n" +
                "(27, 'Hardness', 'Hardness by 200.7', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(28, 'Hardness', 'Hardness by  200.8', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(29, 'Hardness', 'Hardness by  6010C', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(30, 'Hardness', 'Hardness by  6020A', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(31, 'Hardness', 'Hardness by  SM2340B', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(32, 'Hexavalent Chromium (Cr+6)', 'Hexavalent Chromium (Cr+6) by 7196A', 'Plastic', '500 ml', '4 deg C', '24 Hours', NULL, NULL, NULL, NULL),\n" +
                "(33, 'Hexavalent Chromium (Cr+6)', 'Hexavalent Chromium (Cr+6) by  SM3500Cr-D', 'Plastic', '500 ml', '4 deg C', '24 Hours', NULL, NULL, NULL, NULL),\n" +
                "(34, 'MBAS', 'MBAS by SM5540C', 'Plastic', '1000 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(35, 'Mercury', 'Mercury by 245.1', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '28 days', NULL, NULL, NULL, NULL),\n" +
                "(36, 'Mercury', 'Mercury by  7470A', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '28 days', NULL, NULL, NULL, NULL),\n" +
                "(37, 'Mercury, Low-Level', 'Mercury, Low-Level by 1631E', 'Glass', '250 ml', 'BrCl', '90 Days; 48 hours if unpreserved', NULL, NULL, NULL, NULL),\n" +
                "(38, 'Metals', 'Metals by 200.7', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(39, 'Metals', 'Metals by  200.8', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(40, 'Metals', 'Metals by  6010C', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(41, 'Metals', 'Metals by  6020A', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(42, 'Metals', 'Metals by  7000A70A', 'Plastic', '500 ml', 'HNO3, pH<2, 4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(43, 'Nitrate', 'Nitrate by 300.0', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(44, 'Nitrate', 'Nitrate by  353.2', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(45, 'Nitrate', 'Nitrate by  9056', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(46, 'Nitrate', 'Nitrate by  SM4500NO3 -F', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(47, 'Nitrate/Nitrite', 'Nitrate/Nitrite by 353.2', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(48, 'Nitrate/Nitrite', 'Nitrate/Nitrite by  SM4500NO3 -F', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(49, 'Nitrite', 'Nitrite by 353.2', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(51, 'Nitrite', 'Nitrite by  SM4500N02 -B', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(52, 'Nitrogen, Total Kjeldahl (TKN)', 'Nitrogen, Total Kjeldahl (TKN) by 353.3/.1 (Modified)', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(53, 'Nitrogen, Total Kjeldahl (TKN)', 'Nitrogen, Total Kjeldahl (TKN) by  SM4500Norg-C', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(54, 'Oil & Grease', 'Oil & Grease by 1664A', 'Amber Glass', '(2) 1000 ml', 'HCI, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(55, 'Orthophosphate', 'Orthophosphate by SM4500P-E', 'Plastic', '250 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(56, 'pH', 'pH by 9040C', 'Plastic', '250 ml', '4 deg C', 'Immediate', NULL, NULL, NULL, NULL),\n" +
                "(57, 'Phosphorous, Total', 'Phosphorous, Total by SM4500P-E', 'Plastic', '250 ml', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(58, 'Solids, Total (TS)', 'Solids, Total (TS) by 2540B', 'Plastic', '250 ml', '4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(59, 'Solids, Total Dissolved (TDS)', 'Solids, Total Dissolved (TDS) by SM2540C', 'Plastic', '500 ml', '4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(60, 'Solids, Total Suspended (TSS)', 'Solids, Total Suspended (TSS) by SM2540D', 'Plastic', '1000 ml', '4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(61, 'Solids, Total Volatile (TVS)', 'Solids, Total Volatile (TVS) by SM2540E', 'Plastic', '500 ml', '4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(63, 'Specific Conductance', 'Specific Conductance by  9050A', 'Plastic/Glass', '250 ml', '4 deg C', '24 hours; or filter within 24 hours and store for 28 Days', NULL, NULL, NULL, NULL),\n" +
                "(64, 'Sulfate', 'Sulfate by 300.0', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(65, 'Sulfate', 'Sulfate by  9038', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(66, 'Sulfate', 'Sulfate by  9056', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(67, 'Sulfate', 'Sulfate by  SM4500SO4 -E', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(68, 'Sulfate', 'Sulfate by  300', 'Plastic', '250 ml', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(69, 'Sulfide', 'Sulfide by 9030B', 'Plastic', '(2) 250 ml', 'ZnOAC, NaOH, pH>9, 4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(70, 'Sulfide', 'Sulfide by  SM4500S2 -AD', 'Plastic', '(2) 250 ml', 'ZnOAC, NaOH, pH>9, 4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(71, 'Total Organic Carbon (TOC)', 'Total Organic Carbon (TOC) by 9060', 'Amber Glass', '(2) 40 ml VOA Vials', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(72, 'Total Organic Carbon (TOC)', 'Total Organic Carbon (TOC) by  SM5310C', 'Amber Glass', '(2) 40 ml VOA Vials', 'H2SO4, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(73, 'Total Phenol', 'Total Phenol by 420.1', 'Amber Glass', '(2) 1000 ml', 'H2SO4, pH<4, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(74, 'Total Phenol', 'Total Phenol by  9065', 'Amber Glass', '(2) 1000 ml', 'H2SO4, pH<4, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(75, 'Total Residual Chlorine', 'Total Residual Chlorine by SM4500Cl-D', 'Plastic', '500 ml', '4 deg C', '24 Hours', NULL, NULL, NULL, NULL),\n" +
                "(76, 'Turbidity', 'Turbidity by 180.1', 'Plastic', '500 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(77, 'Turbidity', 'Turbidity by  SM2130B', 'Plastic', '500 ml', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(78, '1,4-Dioxane', '1,4-Dioxane by 8260C-SIM', 'Amber Glass', '(3) 40 ml VOA Vials', 'HCL, pH<2, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(79, 'Dissolved Gases', 'Dissolved Gases by RSKSOP-175', 'Amber Glass', '(2) 20 ml VOA Vials', 'HCL, pH<2, 4 deg C. If CO2, 4 deg C.', '14 Days preserved; 7 Days Unpreserved', NULL, NULL, NULL, NULL),\n" +
                "(80, 'Volatile Organics - 524.2', 'Volatile Organics - 524.2 by 524.2', 'Amber Glass', '(2) 40 ml VOA Vials', 'Ascorbic Acid, HCL, pH<2, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(81, 'Volatile Organics - 624', 'Volatile Organics - 624 by 624', 'Amber Glass', '(3) 40 ml VOA Vials', 'Na2S2O3, 4 deg C', '7 Days', NULL, NULL, NULL, NULL),\n" +
                "(82, 'Volatile Organics - 8260', 'Volatile Organics - 8260 by 8260C', 'Amber Glass', '(3) 40 ml VOA Vials', 'HCL, pH<2, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(83, '1,4-Dioxane', '1,4-Dioxane by 8270D', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(84, '1,4-Dioxane', '1,4-Dioxane by  8270D-SIM w/Isotope Dilution', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(85, 'Acid/Base Neutral Extractables (ABN) - 625', 'Acid/Base Neutral Extractables (ABN) - 625 by 625', 'Amber Glass', '(2) 1000 ml', 'Na2S2O3, 4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(86, 'Acid/Base Neutral Extractables (ABN) - 8270', 'Acid/Base Neutral Extractables (ABN) - 8270 by 8270D', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(87, 'PCB Aroclors', 'PCB Aroclors by 8270D-SIM/NOAA (M)', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(88, 'PCB Congeners', 'PCB Congeners by 8270D-SIM/NOAA (M)', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(89, 'PCB Homologs', 'PCB Homologs by 8270D-SIM/NOAA (M)', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(90, 'Polynuclear Aromatic Hydrocarbons (PAHs) - 625', 'Polynuclear Aromatic Hydrocarbons (PAHs) - 625 by 625', 'Amber Glass', '(2) 1000 ml', 'Na2S2O3, 4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(91, 'Polynuclear Aromatic Hydrocarbons (PAHs) - 8270', 'Polynuclear Aromatic Hydrocarbons (PAHs) - 8270 by 8270D', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(92, 'Polynuclear Aromatic Hydrocarbons (PAHs) - 8270', 'Polynuclear Aromatic Hydrocarbons (PAHs) - 8270 by  8270D-SIM', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(93, 'Perchlorate', 'Perchlorate by 332.0', 'Plastic', '250 ml', '4 deg C', '28 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(94, 'Perchlorate', 'Perchlorate by  6860', 'Plastic', '250 ml', '4 deg C', '28 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(95, 'CT ETPH', 'CT ETPH by CT ETPH', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(96, 'MA-DEP EPH', 'MA-DEP EPH by EPH-04-1', 'Amber Glass', '(2) 1000 ml', 'HCl, pH<2, 4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(97, 'MA-DEP VPH', 'MA-DEP VPH by VPH-04-1.1', 'Amber Glass', '(3) 40 ml VOA Vials', 'HCl, pH<2, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(98, 'ME DEP TPH DRO', 'ME DEP TPH DRO by ME 4.1.25', 'Amber Glass', '(2) 1000 ml', 'HCl, pH<2, 4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(99, 'ME DEP TPH GRO', 'ME DEP TPH GRO by ME 4.2.17', 'Amber Glass', '(2) 40 ml VOA Vials', 'HCl, pH<2, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(100, 'NJ EPH', 'NJ EPH by NJDEP EPH', 'Amber Glass', '(2) 1000 ml', 'HCl, pH<2, 4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(101, 'Petroleum Hydrocarbon Identification (PHI)', 'Petroleum Hydrocarbon Identification (PHI) by 8015D (M)', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(103, 'TPH GC/FID Quantitation only', 'TPH GC/FID Quantitation only by 8015C (M)', 'Amber Glass', '(2) 1000 ml', '4 deg C', '7 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(104, 'TPH-GRO', 'TPH-GRO by 8015C (M)', 'Amber Glass', '(2) 40 ml VOA Vials', 'HCl, pH<2, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(105, 'TPH - Oil & Grease', 'TPH - Oil & Grease by EPA 1664A', 'Amber Glass', '(2) 1000 ml', 'HCl, pH<2, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(106, 'Coliform, Fecal', 'Coliform, Fecal by SM9221E', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(107, 'Coliform, Fecal', 'Coliform, Fecal by  SM9222D', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(108, 'Coliform, Total', 'Coliform, Total by SM9221B', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(109, 'Coliform, Total', 'Coliform, Total by  SM9222B', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(110, 'Coliform, Total', 'Coliform, Total by  SM9223B', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(111, 'Enterococcus', 'Enterococcus by 1600', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(112, 'Heterotrophic Plate Count', 'Heterotrophic Plate Count by SM9215B', 'Specimen Cup (Sterile)', '100 ml, 1-inch headspace', '4 deg C; Na2S2O3 for chlorinated source', '8 Hours', NULL, NULL, NULL, NULL),\n" +
                "(113, 'Ammonia', 'Ammonia by SM4500NH3-B', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(114, 'Cyanide, Physiologically Available', 'Cyanide, Physiologically Available by 9010B/C', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(115, 'Cyanide, Physiologically Available', 'Cyanide, Physiologically Available by  9014', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(120, 'Flashpoint', 'Flashpoint by 1010', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(122, 'Ignitability', 'Ignitability by 1030', 'Amber Glass', '4 oz Container; no headspace', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(123, 'Mercury', 'Mercury by 7471B', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(124, 'Mercury', 'Mercury by  7474', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(127, 'Metals', 'Metals by  7000A', 'Amber Glass', '4 oz Container', '4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(128, 'Moisture Content', 'Moisture Content by SM2540G', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(131, 'pH', 'pH by 9045D', 'Amber Glass', '4 oz Container', '4 deg C', 'Immediate', NULL, NULL, NULL, NULL),\n" +
                "(132, 'Soot', 'Soot by 9060', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days (9060)', NULL, NULL, NULL, NULL),\n" +
                "(134, 'Specific Conductance', 'Specific Conductance by  SM2510B', 'Amber Glass', '4 oz Container', '4 deg C', '28 days', NULL, NULL, NULL, NULL),\n" +
                "(136, 'Total Organic Carbon (TOC)', 'Total Organic Carbon (TOC) by  Lloyd Kahn (LK) Method', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days (9060), 14 Days (LK)', NULL, NULL, NULL, NULL),\n" +
                "(138, 'Total Phosphorus', 'Total Phosphorus by SM4500P-E', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(141, 'Volatile Organics - 8260 (High Level)', 'Volatile Organics - 8260 (High Level) by 8260C / 5035 (High Level)', '40 ml Amber VOA Vial and Terracore, if required', '5-15 Grams (refer to collection instructions)', 'MeOH, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(142, 'Volatile Organics - 8260 (Low Level)', 'Volatile Organics - 8260 (Low Level) by 8260C / 5035 (Low Level)', '(2) 40 ml Amber VOA and Terracore, if required', '5 Grams', 'NaHSO4, 4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(144, 'Volatile Organics - 8260 (Encore)', 'Volatile Organics - 8260 (Encore) by 8260C / 5035', '(3) Encore samplers', '15 Grams (Three 5 gram samplers)', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(145, 'Acid/Base Neutral Extractables (ABN)', 'Acid/Base Neutral Extractables (ABN) by 8270D', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(149, 'Polynuclear Aromatic Hydrocarbons (PAHs)', 'Polynuclear Aromatic Hydrocarbons (PAHs) by 8270D', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(150, 'Polynuclear Aromatic Hydrocarbons (PAHs)', 'Polynuclear Aromatic Hydrocarbons (PAHs) by  8270D-SIM', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(151, 'PCBs', 'PCBs by 8082A', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(152, 'Pesticides (Organochlorine)', 'Pesticides (Organochlorine) by 8081B', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(153, 'Chlorinated Herbicides', 'Chlorinated Herbicides by 8151A', 'Amber Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(165, 'TPH - Oil & Grease', 'TPH - Oil & Grease by 1664A', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(166, 'Mercury', 'Mercury by 1311', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(167, 'Mercury', 'Mercury by  1312', 'Amber Glass', '4 oz Container', '4 deg C', '28 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(169, 'Metals', 'Metals by 1311', 'Amber Glass', '4 oz Container', '4 deg C', '180 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(170, 'Metals', 'Metals by  1312', 'Amber Glass', '4 oz Container', '4 deg C', '180 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(174, 'Semivolatiles', 'Semivolatiles by 1311', 'Amber Glass', '8 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(175, 'Semivolatiles', 'Semivolatiles by  1312', 'Amber Glass', '8 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(176, 'Semivolatiles', 'Semivolatiles by  8270D', 'Amber Glass', '8 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(177, 'Semivolatiles', 'Semivolatiles by  8081B', 'Amber Glass', '8 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(178, 'Semivolatiles', 'Semivolatiles by  8151A', 'Amber Glass', '8 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(179, 'Volatiles', 'Volatiles by 1311', 'Large Amber Glass VOA Vial and Terracore, if required', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(180, 'Volatiles', 'Volatiles by  1312', 'Large Amber Glass VOA Vial and Terracore, if required', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(181, 'Volatiles', 'Volatiles by  8260C', 'Large Amber Glass VOA Vial and Terracore, if required', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(182, 'Volatiles (Encore)', 'Volatiles (Encore) by 1311', '(1) Encore sampler', '25 grams', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(183, 'Volatiles (Encore)', 'Volatiles (Encore) by  1312', '(1) Encore sampler', '25 grams', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(184, 'Volatiles (Encore)', 'Volatiles (Encore) by  8260C', '(1) Encore sampler', '25 grams', '4 deg C', '48 Hours', NULL, NULL, NULL, NULL),\n" +
                "(185, 'AVS/SEM (Acid Volatile Sulfide / Simultaneously Extracted Metals)', 'AVS/SEM (Acid Volatile Sulfide / Simultaneously Extracted Metals) by EPA-121-R91-100', 'Glass with teflon septa cap', '4 oz Container', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(187, 'Cyanide, Physiologically Available', 'Cyanide, Physiologically Available by  9012A', 'Glass', '4 oz Container', '4 deg C', '14 Days', NULL, NULL, NULL, NULL),\n" +
                "(192, 'Grain Size, Bulk Density, Organic Matter, Moisture Content', 'Grain Size, Bulk Density, Organic Matter, Moisture Content by ASTM Methods', 'ziploc bag', 'Quart ziploc', 'NA', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(193, 'Grain Size, Bulk Density, Organic Matter, Moisture Content', 'Grain Size, Bulk Density, Organic Matter, Moisture Content by  SM2540G', 'ziploc bag', 'Quart ziploc', 'NA', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(196, 'Ignitability', 'Ignitability by 1010', 'Glass', '4 oz Container', '4 deg C', '180 Days', NULL, NULL, NULL, NULL),\n" +
                "(197, 'Inorganic Anions (Bromide, Chloride, Fluoride, Nitrate, Nitrite, Sulfate)', 'Inorganic Anions (Bromide, Chloride, Fluoride, Nitrate, Nitrite, Sulfate) by 300.0 (M)', 'Glass', '4 oz Container', '4 deg C', '28 Days (extraction) except Nitrate/Nitrite', NULL, NULL, NULL, NULL),\n" +
                "(198, 'Inorganic Anions (Bromide, Chloride, Fluoride, Nitrate, Nitrite, Sulfate)', 'Inorganic Anions (Bromide, Chloride, Fluoride, Nitrate, Nitrite, Sulfate) by  9056 (M)', 'Glass', '4 oz Container', '4 deg C', '28 Days (extraction) except Nitrate/Nitrite', NULL, NULL, NULL, NULL),\n" +
                "(204, 'pH/Corrosivity', 'pH/Corrosivity by 9045D', 'Glass', '4 oz Container', '4 deg C', 'Analyze immediately', NULL, NULL, NULL, NULL),\n" +
                "(205, 'Reactivity - Cyanide/Sulfide', 'Reactivity - Cyanide/Sulfide by Ch. 7 SW-846', 'Glass', '4 oz Container', '4 deg C', 'Analyze immediately', NULL, NULL, NULL, NULL),\n" +
                "(210, 'Total Solids', 'Total Solids by SM 2540B', 'Glass', '4 oz Container', '4 deg C', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(211, 'Total Volatile Solids', 'Total Volatile Solids by SM 2540G', 'Glass', '4 oz Container', '4 deg C', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(220, 'PCB Aroclors', 'PCB Aroclors by 8082A', 'Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(221, 'PCB Congeners', 'PCB Congeners by 8082A', 'Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(222, 'Pesticides', 'Pesticides by 8081B', 'Glass', '4 oz Container', '4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL),\n" +
                "(247, 'Metals', 'Metals by  7000', 'Glass', '4', 'Frozen', '1 Year', NULL, NULL, NULL, NULL),\n" +
                "(249, 'Percent Lipids', 'Percent Lipids by Alpha Method', 'Glass', '4 oz Container', 'Frozen', '1 Year', NULL, NULL, NULL, NULL),\n" +
                "(252, 'PCB Aroclors', 'PCB Aroclors by 8082A / 8270D-SIM/NOAA(M)', 'Glass', '4 oz Container', 'Frozen', '1 Year', NULL, NULL, NULL, NULL),\n" +
                "(253, 'PCB Congeners', 'PCB Congeners by 8082A / 8270D-SIM/NOAA(M)', 'Glass', '4 oz Container', 'Frozen', '1 Year', NULL, NULL, NULL, NULL),\n" +
                "(254, 'PCB Homologs', 'PCB Homologs by 8082A / 8270D-SIM/NOAA(M)', 'Glass', '4 oz Container', 'Frozen', '1 Year', NULL, NULL, NULL, NULL),\n" +
                "(255, 'Pesticides', 'Pesticides by 8081B / 8270D-SIM (M)', 'Glass', '4 oz Container', 'Frozen', '1 Year', NULL, NULL, NULL, NULL),\n" +
                "(258, 'PIANO Volatiles', 'PIANO Volatiles by 8260B Mod.', 'Glass', '20 mL vial', '4 deg C', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(262, 'Saturated Hydrocarbons', 'Saturated Hydrocarbons by 8015D (M)', 'Glass', '20 mL vial', '4 deg C', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(263, 'TPH-DRO', 'TPH-DRO by 8015D (M)', 'Glass', '20 mL vial', '4 deg C', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(264, 'Whole Oil Analysis', 'Whole Oil Analysis by Alpha Method', 'Glass', '20 mL vial', '4 deg C', 'NA', NULL, NULL, NULL, NULL),\n" +
                "(274, 'Oil & Grease', 'Oil & Grease by 9071B', '4oz. Amber Glass', '1 jar with wipe', '1:4 Acetone:Hexane, 4 deg C', '28 Days', NULL, NULL, NULL, NULL),\n" +
                "(280, 'PCB Congeners/Homologs', 'PCB Congeners/Homologs by 8270D-SIM/NOAA(M)', '4oz. Amber Glass', '1 jar with wipe', '1:4 Acetone:Hexane, 4 deg C', '14 Days (Extraction)', NULL, NULL, NULL, NULL);\n";
        try {
            database.execSQL(query);
            Log.i("addCoCMethods", "cm_methods Data inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("addCoCMethods", "cm_methods data insertion Error:" + e.getMessage());

        }
    }

    public void createTable_cm_coc_master_IfNotExist() {
        String fields =
                "    coc_id              VARCHAR (50)  DEFAULT (NULL),\n" +
                        "    coc_display_id      VARCHAR (20)  NOT NULL,\n" +
                        "    site_id             INT (11)      DEFAULT NULL,\n" +
                        "    ship_date           BIGINT (20)   DEFAULT NULL,\n" +
                        "    creation_date       BIGINT (20)   DEFAULT NULL,\n" +
                        "    modified_by         INT (11)      DEFAULT NULL,\n" +
                        "    created_by          INT (11)      DEFAULT NULL,\n" +
                        "    modification_date   BIGINT (20)   DEFAULT NULL,\n" +
                        "    coc_setup_id        VARCHAR (1)   DEFAULT NULL,\n" +
                        "    special_instruction VARCHAR (500) DEFAULT NULL,\n" +
                        "    status              VARCHAR (50)  DEFAULT NULL,\n" +
                        "    form_id             INT (11)      DEFAULT NULL,\n" +
                        "    client_coc_id       VARCHAR (50)  DEFAULT NULL";


        String sql = "create table if not exists " + DbAccess.TABLE_CM_COC_MASTER + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "cm_coc_master Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "cm_coc_master Table Creation Error:" + e.getMessage());
        }

        alterCOCMasterTableColumns();
    }

    public void alterCOCMasterTableColumns() {
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_CM_COC_MASTER
                + " ADD COLUMN eventId int(11) DEFAULT NULL ";

        try {
            if (!isColumnExists(DbAccess.TABLE_CM_COC_MASTER, "eventId"))
                database.execSQL(sql1);
            Log.i("COCMasterTable", "COCMasterTable Altered by eventId");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("COCMasterTable", "COCMasterTable Alteration Error:" + e.getMessage());
        }
    }

    public void moveOld_work_order_data() {

        String sql = "INSERT INTO " + DbAccess.TABLE_WORK_ORDER_TASK_NEW +
                "    (LocationID ,\n" +
                "    Location,\n" +
                "    TaskID,\n" +
                "    TaskDesc,\n" +
                "    wo_id,\n" +
                "    wo_task_id ,\n" +
                "    PlanName,\n" +
                "    TaskName,\n" +
                "    CocFlag ,\n" +
                "    UserID,\n" +
                "    Latitude,\n" +
                "    Longitude ,\n" +
                "    loc_instruction,\n" +
                "    planStartDate,\n" +
                "    planEndDate,\n" +
                "    parentAppID,\n" +
                "    status ,\n" +
                "    wo_planStartDate,\n" +
                "    wo_planEndDate) \n" +

                "  SELECT LocationID ,\n" +
                "    Location,\n" +
                "    TaskID,\n" +
                "    TaskDesc,\n" +
                "    wo_id,\n" +
                "    wo_task_id ,\n" +
                "    PlanName,\n" +
                "    TaskName,\n" +
                "    CocFlag ,\n" +
                "    UserID,\n" +
                "    Latitude,\n" +
                "    Longitude ,\n" +
                "    loc_instruction,\n" +
                "    planStartDate,\n" +
                "    planEndDate,\n" +
                "    parentAppID,\n" +
                "    status ,\n" +
                "    wo_planStartDate,\n" +
                "    wo_planEndDate from  " + DbAccess.TABLE_WORK_ORDER_TASK_OLD;

        try {
            database.execSQL(sql);
            Log.i(TAG, "moveTemp_work_order_data() All data moved.");

            String sql1 = "DROP TABLE " + DbAccess.TABLE_WORK_ORDER_TASK_OLD;
            database.execSQL(sql1);
            Log.i(TAG, "moveTemp_work_order_data() old table droped.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "moveTemp_work_order_data Error:" + e.getMessage());

        }


    }

    public void createTable_task_attributes_IfNotExist() {

        String fields = "'TaskID' INTEGER,'attributeName' VARCHAR (1000),'attributeValue' VARCHAR (1000)";

        String sql = "CREATE TABLE IF NOT EXISTS task_attributes(" + fields + ")";
        try {
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createTable_s_AppPreferences_IfNotExist() {

        /* Folder creation query */
        String fields =
                "    PreferenceMappingID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                        "    FeatureID           INTEGER (100),\n" +
                        "    FeatureName         VARCHAR (4000),\n" +
                        "    FeatureKey          VARCHAR (4000),\n" +
                        "    MappingStatus       INTEGER (100) ,\n " +
                        "    UserID              INTEGER (100)  ,\n" +
                        "    CompanyID           INTEGER (100)  ";

        String sql = "create table if not exists " + DbAccess.TABLE_S_APP_PRFERENCE_MAPPING + "(" + fields + ")";
        //CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_AppPreferences Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_AppPreferences Table Creation Error:" + e.getMessage());

        }
    }

    public void createTable_temp_project_file_IfNotExist() {

        /* Folder creation query */
        String fields = " file_id INT(11) NOT NULL , " +
                "  file_name VARCHAR(255) NULL, " +
                "  file_type VARCHAR(100) NULL, " +
                "  file_status INT(11) NULL, " +
                "  file_description VARCHAR(255) NULL, " +
                "  file_checksum VARCHAR(255) NULL, " +
                "  file_guid VARCHAR(100) NULL, " +
                "  site_id INT(11) NULL, " +
                "  file_path VARCHAR(255) NULL, " +
                "  created_by INT NULL, " +
                "  creation_date BIGINT(20) NULL, " +
                "  modified_by INT NULL, " +
                "  modification_date BIGINT(20) NULL, " +
                "  notes varchar(500)," +
                "  PRIMARY KEY (file_id)";
        String sql = "create table if not exists " + DbAccess.TABLE_TEMP_PROJECT_FILE + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "temp_project_file Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "temp_project_file Table Creation Error:" + e.getMessage());

        }
    }

    public void alterTable_s_Default_Values() {
        String sqld = "ALTER TABLE " + DbAccess.TABLE_S_DEFAULT_VALUES + "  ADD COLUMN  warning_high VARCHAR(1000) DEFAULT (null) ";
        String sqld1 = "ALTER TABLE " + DbAccess.TABLE_S_DEFAULT_VALUES + "  ADD COLUMN  warning_low VARCHAR(1000) DEFAULT (null) ";

        try {
            if (!isColumnExists(DbAccess.TABLE_S_DEFAULT_VALUES, "warning_high"))
                database.execSQL(sqld);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by warning_high");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_S_DEFAULT_VALUES, "warning_low"))
                database.execSQL(sqld1);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by warning_low");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());
        }
    }

    public void alterTable_s_sitemobileapp() {
        String sqls = "ALTER TABLE " + DbAccess.TABLE_SITE_MOBILEAPP + "  ADD COLUMN  display_name_roll_into_app VARCHAR(1000) DEFAULT (null) ";
        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_MOBILEAPP, "display_name_roll_into_app"))
                database.execSQL(sqls);
            Log.i("altersitemobileapp", "s_SiteMobileApp Table Altered by display_name_roll_into_app");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("altersitemobileapp", "s_SiteMobileApp Table Alteration Error:" + e.getMessage());
        }
    }

    public void rename_s_user() {
        String sqls = "ALTER TABLE " + DbAccess.TABLE_USER + " RENAME TO tmp_s_User";
        try {
            database.execSQL(sqls);
            Log.i("alterTable_s_User", "s_User Table Altered by UserGuid");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());
        }

        createTable_s_user();
    }

    public void alterTable_s_SiteUserRole() {

        String sqlFavStatus = "ALTER TABLE " + DbAccess.TABLE_SITE_USER_ROLE + " ADD COLUMN favouriteStatus varchar(10)";

        try {
            if (!isColumnExists(DbAccess.TABLE_SITE_USER_ROLE, "favouriteStatus"))
                database.execSQL(sqlFavStatus);
            Log.i(TAG, "SiteUserRole Table Altered.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "SiteUserRole Table Alteration Error:" + e.getMessage());
        }

        String sqls = "ALTER TABLE " + DbAccess.TABLE_SITE_USER_ROLE + " RENAME TO temp_s_SiteUserRole";
        try {
            database.execSQL(sqls);
            Log.i("alterTableSiteUserRole", "s_siteuserrole Table Altered by temp_siteuserrole");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTableSiteUserRole", "s_siteuserrole Table Alteration Error:" + e.getMessage());
        }

        createTable_s_siteuserrole();
    }

    private void createTable_s_user() {

        String fields =
                "    UserID  INT   ,\n" +
                        "    UserNumber      VARCHAR (100)  NOT NULL,\n" +
                        "    Username        VARCHAR (100) NOT NULL,\n" +
                        "    Password       VARCHAR (100)  NOT NULL,\n" +
                        "    PassKey       VARCHAR (100),\n" +
                        "    Firstname     VARCHAR (100),\n" +
                        "    LastName      VARCHAR (100),\n" +
                        "    CompanyID   INT,\n" +
                        "    ToMail        VARCHAR ( 256 ),\n" +
                        "    CCMail  VARCHAR ( 256 ),\n" +
                        "    Address1  VARCHAR ( 100 ),\n" +
                        "    Address2  VARCHAR ( 100 ),\n" +
                        "    City     VARCHAR ( 100 ),\n" +
                        "    State     VARCHAR ( 10),\n" +
                        "    ZipCode     VARCHAR ( 7 ),\n" +
                        "    Notes     VARCHAR ( 200 ),\n" +
                        "    CreationDate     LONG ,\n" +
                        "    Createdby     INT,\n" +
                        "    UserGuid     VARCHAR ( 100 ),\n" +
                        "    access_token  VARCHAR ( 100 ) DEFAULT (null),\n" +
                        "    refresh_token  VARCHAR ( 100 )DEFAULT (null),\n" +
                        "    status VARCHAR ( 100 ),\n" +
                        "    UserRole INT,\n" +
                        "PRIMARY KEY(`UserID`)";


        String sql = "create table if not exists " + DbAccess.TABLE_USER + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_user Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_user Table Creation Error:" + e.getMessage());

        }
        if (isTableExists(DbAccess.TABLE_USER)) {
            movetemp_user_data();
        }
    }

    private void createTable_s_siteuserrole() {

        String fields =
                "    UserID  INT NOT NULL ,\n" +
                        "    SiteID INT NOT NULL,\n" +
                        "    RoleID  INT NOT NULL,\n" +
                        "    Notes VARCHAR (200),\n" +
                        "    CreationDate   LONG ,\n" +
                        "    ModifiedDate  LONG,\n" +
                        "    Createdby INT,\n" +
                        "    SyncStatus INT," +
                        "    favouriteStatus VARCHAR (10)";

        String sql = "create table if not exists " + DbAccess.TABLE_SITE_USER_ROLE + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_siteuser Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_user Table Creation Error:" + e.getMessage());

        }

        if (isTableExists(DbAccess.TABLE_SITE_USER_ROLE)) {
            movetemp_siteuser_data();
        }
    }

    private void movetemp_user_data() {

        String sql = "INSERT INTO " + DbAccess.TABLE_USER +
                "    (UserID ,\n" +
                "    UserNumber,\n" +
                "    Username,\n" +
                "    Password,\n" +
                "    PassKey,\n" +
                "    Firstname ,\n" +
                "    LastName,\n" +
                "    CompanyID,\n" +
                "    ToMail ,\n" +
                "    CCMail,\n" +
                "    Address1,\n" +
                "    Address2 ,\n" +
                "    City,\n" +
                "    State,\n" +
                "    ZipCode,\n" +
                "    Notes,\n" +
                "    CreationDate ,\n" +
                "    Createdby,\n" +
                "    UserGuid,\n" +
                "    status,\n" +
                "    UserRole) \n" +

                "  SELECT UserID ,\n" +
                "    UserNumber,\n" +
                "    Username,\n" +
                "    Password,\n" +
                "    PassKey,\n" +
                "    Firstname ,\n" +
                "    LastName,\n" +
                "    CompanyID,\n" +
                "    ToMail ,\n" +
                "    CCMail,\n" +
                "    Address1,\n" +
                "    Address2 ,\n" +
                "    City,\n" +
                "    State,\n" +
                "    ZipCode,\n" +
                "    Notes,\n" +
                "    CreationDate ,\n" +
                "    Createdby,\n" +
                "    UserGuid,\n" +
                "    status,\n" +
                "    UserRole from  " + DbAccess.TABLE_TEMP_USER;

        try {
            database.execSQL(sql);
            Log.i(TAG, "moveTemp_work_order_data() All data moved.");

            String sql1 = "DROP TABLE " + DbAccess.TABLE_TEMP_USER;
            database.execSQL(sql1);
            Log.i(TAG, "moveTemp_work_order_data() old table droped.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "moveTemp_work_order_data Error:" + e.getMessage());

        }
    }

    private void movetemp_siteuser_data() {

        String sql = "INSERT INTO " + DbAccess.TABLE_SITE_USER_ROLE +
                "    (UserID ,\n" +
                "    SiteID,\n" +
                "    RoleID,\n" +
                "    Notes,\n" +
                "    CreationDate,\n" +
                "    ModifiedDate ,\n" +
                "    Createdby, favouriteStatus) \n" +

                "  SELECT UserID ,\n" +
                "    SiteID,\n" +
                "    RoleID,\n" +
                "    Notes,\n" +
                "    CreationDate,\n" +
                "    ModifiedDate ,\n" +
                "    Createdby, favouriteStatus from  " + DbAccess.TABLE_TEMP_SITE_USER_ROLE;

        try {
            database.execSQL(sql);
            Log.i(TAG, "movetemp_siteuser_data() All data moved.");

            String sql1 = "DROP TABLE " + DbAccess.TABLE_TEMP_SITE_USER_ROLE;
            database.execSQL(sql1);
            Log.i(TAG, "movetemp_siteuser_data() old table droped.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "movetemp_siteuser_data Error:" + e.getMessage());
        }
    }

    public void alterTable_s_User() {
        String sqls = "ALTER TABLE " + DbAccess.TABLE_USER + "  ADD COLUMN  UserGuid VARCHAR(1000)";
        try {
            if (!isColumnExists(DbAccess.TABLE_USER, "UserGuid"))
                database.execSQL(sqls);
            Log.i("alterTable_s_User", "s_User Table Altered by UserGuid");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());
        }
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_USER + "  ADD COLUMN  status INTEGER(11)";
        try {
            if (!isColumnExists(DbAccess.TABLE_USER, "status"))
                database.execSQL(sql1);
            Log.i("alterTable_s_User", "s_User Table Altered by status");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());

        }

        String sql2 = "ALTER TABLE " + DbAccess.TABLE_USER + " ADD COLUMN ContactNumber VARCHAR (50)";
        try {
            if (!isColumnExists(DbAccess.TABLE_USER, "ContactNumber"))
                database.execSQL(sql2);
            Log.i("alterTable_s_User", "s_User Table Altered by ContactNumber");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());

        }
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_USER + " ADD COLUMN UserType VARCHAR (1000)";
        try {
            if (!isColumnExists(DbAccess.TABLE_USER, "UserType"))
                database.execSQL(sql3);
            Log.i("alterTable_s_User", "s_User Table Altered by UserType");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());
        }

        String sql4 = "ALTER TABLE " + DbAccess.TABLE_USER + " ADD COLUMN UserRole VARCHAR (1000)";
        try {
            if (!isColumnExists(DbAccess.TABLE_USER, "UserRole"))
                database.execSQL(sql4);
            Log.i("alterTable_s_User", "s_User Table Altered by UserRole");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());
        }

        String sql5 = "ALTER TABLE " + DbAccess.TABLE_USER + " ADD COLUMN UserAppType VARCHAR (20)";
        try {
            if (!isColumnExists(DbAccess.TABLE_USER, "UserAppType"))
                database.execSQL(sql5);
            Log.i("alterTable_s_User", "s_User Table Altered by UserAppType");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_User", "s_User Table Alteration Error:" + e.getMessage());
        }
    }

    public void alterTable_d_fielddata() {

        /* Folder creation query */
        String sql = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  deviceId VARCHAR(1000) DEFAULT (null) ";
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  modifiedByDeviceId VARCHAR(1000)  ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  modifiedBy VARCHAR(1000) ";
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  oldStringValue VARCHAR(4000) ";
        String sql6 = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  copy_status VARCHAR(100) ";
        String sql7 = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN  copy_from VARCHAR(100) ";
        String sqlOldNote = "ALTER TABLE " + DbAccess.TABLE_FIELD_DATA + "  ADD COLUMN oldNote VARCHAR(1000) ";

//        CREATE TABLE IF NOT EXISTS
        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "deviceId"))
                database.execSQL(sql);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by deviceId");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "modifiedByDeviceId"))
                database.execSQL(sql1);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by modifiedByDeviceId");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "modifiedBy"))
                database.execSQL(sql2);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by modifiedBy");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "oldStringValue"))
                database.execSQL(sql3);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by oldStringValue");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "copy_status"))
                database.execSQL(sql6);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by copy_status");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "copy_from"))
                database.execSQL(sql7);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by copy_from");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_FIELD_DATA, "oldNote"))
                database.execSQL(sqlOldNote);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by oldNote");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());
        }
    }

    public void alterTable_d_Attachment() {

        /* Folder creation query */
        String sql = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + "  ADD COLUMN  FieldParameterID VARCHAR(1000) ";
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + "  ADD COLUMN  AttachmentDate VARCHAR(1000)  ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + "  ADD COLUMN  AttachmentTime VARCHAR(1000) ";
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + "  ADD COLUMN  ModificationDate VARCHAR(1000) ";
        String sql4 = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + "  ADD COLUMN  Azimuth VARCHAR(1000) ";
        String sql5 = "ALTER TABLE " + DbAccess.TABLE_ATTACHMENT + "  ADD COLUMN  AttachmentName VARCHAR(1000) ";

//        CREATE TABLE IF NOT EXISTS
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "FieldParameterID"))
                database.execSQL(sql);
            Log.i(TAG, "d_Attachment Table Altered by FieldParameterID");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "alterTable_d_Attachment() d_Attachment Table Alteration Error:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "AttachmentDate"))
                database.execSQL(sql1);
            Log.i(TAG, "alterTable_d_Attachment() d_Attachment Altered by AttachmentDate");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_Attachment Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "AttachmentTime"))
                database.execSQL(sql2);
            Log.i(TAG, "d_Attachment Table Altered by AttachmentTime");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_Attachment Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "ModificationDate"))
                database.execSQL(sql3);
            Log.i(TAG, "d_Attachment Table Altered by ModificationDate");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_Attachment Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "Azimuth"))
                database.execSQL(sql4);
            Log.i(TAG, "d_Attachment Table Altered by Azimuth");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_Attachment Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_ATTACHMENT, "AttachmentName"))
                database.execSQL(sql5);
            Log.i(TAG, "d_Attachment Table Altered by AttachmentName");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG, "d_Attachment Table Alteration Error:" + e.getMessage());

        }
    }

    public void alterTable_d_fielddata_temp() {

        /* Folder creation query */
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_TEMP_D_FIELD_DATA + "  ADD COLUMN  modifiedByDeviceId VARCHAR(1000)  ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_TEMP_D_FIELD_DATA + "  ADD COLUMN  modifiedBy VARCHAR(1000) ";
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_TEMP_D_FIELD_DATA + "  ADD COLUMN  oldStringValue VARCHAR(4000) ";


        try {
            if (!isColumnExists(DbAccess.TABLE_TEMP_D_FIELD_DATA, "modifiedByDeviceId"))
                database.execSQL(sql1);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by modifiedByDeviceId");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_TEMP_D_FIELD_DATA, "modifiedBy"))
                database.execSQL(sql2);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by modifiedBy");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_TEMP_D_FIELD_DATA, "oldStringValue"))
                database.execSQL(sql3);
            Log.i("alterTable_d_fielddata", "d_fielddata Table Altered by oldStringValue");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_d_fielddata", "d_fielddata Table Alteration Error:" + e.getMessage());

        }
    }


    public void alterTable_s_MobileApp() {

        /* Folder creation query */
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_MOBILE_APPS + "  ADD COLUMN  companyID INTEGER(100) DEFAULT 0 ";

        try {
            if (!isColumnExists(DbAccess.TABLE_MOBILE_APPS, "companyID"))
                database.execSQL(sql1);
            Log.i("alterTable_s_MobileApp", "s_MobileApp Table Altered by companyID");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_MobileApp", "s_MobileApp Table Alteration Error:" + e.getMessage());
        }
    }

    public void createTableEvents() {
        String fields = " EventID INT(11) NOT NULL , " +
                "  ClientEventID VARCHAR(400), " +
                "  GeneratedBy VARCHAR(40), " +
                "  DeviceID VARCHAR(100) NOT NULL, " +
                "  MobileAppID INT(11) NOT NULL, " +
                "  SiteID INT(11) NOT NULL, " +
                "  EventDate LONG, " +
                "  EventStartDateTime LONG, " +
                "  EventEndDateTime LONG, " +
                "  Latitude DOUBLE, " +
                "  Longitude DOUBLE, " +
                "  UserID INT(100) NOT NULL, " +
                "  Notes VARCHAR(200), " +
                "  EventStatus INT(20) NOT NULL  DEFAULT 1," +
                "  locationID INT(20) NULL, " +
                "  EventName VARCHAR(4000), " +
                "  PRIMARY KEY (EventID)";

        String sql = "create table if not exists " + DbAccess.TABLE_EVENT + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "d_field_event Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "d_field_event Table Creation Error:" + e.getMessage());
        }
    }

    public void alterTable_s_EventTable() {

        /* Folder creation query */
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_EVENT + "  ADD COLUMN  locationID INTEGER(100) ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_EVENT + "  ADD COLUMN  ClientEventID  VARCHAR(4000) ";
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_EVENT + "  ADD COLUMN  EventName  VARCHAR(4000) ";

        try {
            if (isColumnExists(DbAccess.TABLE_EVENT, "locationID")) {
                Log.i("alterTable_s_EventTable", "d_Event Table Already Altered by locationID");

            } else {
                database.execSQL(sql1);
                Log.i("alterTable_s_EventTable", "d_Event Table Altered by locationID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_MobileApp", "d_Event Table Alteration Error:" + e.getMessage());

        }


        try {
            if (isColumnExists(DbAccess.TABLE_EVENT, "ClientEventID")) {
                Log.i("alterTable_s_EventTable", "d_Event Table Already Altered by ClientEventID");

            } else {
                database.execSQL(sql2);
                Log.i("alterTable_s_EventTable", "d_Event Table Altered by ClientEventID");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_EventTable", "d_Event Table Alteration Error:" + e.getMessage());

        }
        try {
            if (isColumnExists(DbAccess.TABLE_EVENT, "EventName")) {
                Log.i("alterTable_s_EventTable", "d_Event Table Already Altered by EventName");

            } else {
                database.execSQL(sql3);
                Log.i("alterTable_s_EventTable", "d_Event Table Altered by EventName");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_EventTable", "d_Event Table Alteration Error:" + e.getMessage());

        }

    }


    public boolean renameTable(String old_name, String new_name) {

        boolean success = false;
        String sql_alter = "ALTER TABLE '" + old_name + "'  RENAME TO  '" + new_name + "'";
        try {
            database.execSQL(sql_alter);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "renameTable() Error:" + e.getMessage());
        }

        return success;
    }

    public boolean isTableExists(String table_name) {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table_name + "'";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            String name = cursor.getString(0);
            if (table_name.equalsIgnoreCase(name)) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean isColoumnExistsInTable(String table, String column) {
        Cursor cursor = database.rawQuery("select * from " + table, null);
        if (cursor != null) {
            int index = cursor.getColumnIndex(column);
            Log.e(TAG, "isColoumnExistsInTable: " + index);
            if (index != -1) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }

    public boolean isColumnExists(String table, String column) {
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + table + ")", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                if (column.equalsIgnoreCase(name)) {
                    cursor.close();
                    return true;
                }
            }

            cursor.close();
        }

        return false;
    }

    public void createTable_s_file_permission_IfNotExist() {

        /* Folder creation query */
        String fields = " file_permission_id INT(11) NOT NULL , " +
                "  site_id INT(11) NULL, " +
                "  folder_id INT(11) NULL, " +
                "  file_id INT(11) NULL, " +
                "  role_id INT(11) NULL, " +
                "  permission_id INT(11) NULL, " +
                "  mobile_app_id INT(11) NULL, " +
                "  permission_status INT(11) NULL, " +
                "  user_id INT(11) NULL, " +
                "  company_id INT(11) NULL, " +
                "  created_by VARCHAR(100) NULL, " +
                "  creation_date BIGINT(20) NULL, " +
                "  modified_by BIGINT(20) NULL, " +
                "  modification_date BIGINT(20) NULL, " +
                "  notes VARCHAR(500) NULL, " +
                "  PRIMARY KEY (file_permission_id)";
        String sql = "create table if not exists " + DbAccess.TABLE_S_FILE_PERMISSION + "(" + fields + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfTable", "s_file_permission Table Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfTable", "s_file_permission Table Creation Error:" + e.getMessage());

        }
    }

    public void createIndex_IfNotExist(String indexName, String columns, String table) {
        String sql = "create INDEX IF Not exists " + indexName + " ON " + table + "(" + columns + ")";
//        CREATE TABLE IF NOT EXISTS
        try {
            database.execSQL(sql);
            Log.i("CreationOfIndex", indexName + " Created.");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("CreationOfIndex", indexName + " Creation Error:" + e.getMessage());

        }
    }

    public void createTableSiteMobileApp() {
        String fields = "`SiteAppID` INT(11) NOT NULL, " +
                "`SiteID` INT(11) NULL DEFAULT NULL, " +
                "`MobileAppID` INT(11) NULL DEFAULT NULL,	" +
                "`AppOrder` FLOAT NULL DEFAULT NULL,	" +
                "`roll_into_app_id` INT(11) NULL DEFAULT NULL, " +
                "`parent_app_id` INT(11) NULL DEFAULT NULL, " +
                "`display_name` VARCHAR(200) NULL DEFAULT NULL,	" +
                "`ShowLast2` BIT(1) NULL DEFAULT NULL, " +
                "`label_width` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField1` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField2` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField3` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField4` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField5` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField6` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField7` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField8` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField9` VARCHAR(200) NULL DEFAULT NULL, " +
                "`ExtField10` VARCHAR(200) NULL DEFAULT NULL, " +
                "`notes` VARCHAR(500) NULL DEFAULT NULL, " +
                "`CreationDate` BIGINT(20) NULL DEFAULT NULL, " +
                "`CreatedBy` INT(11) NULL DEFAULT NULL, " +
                "`ModifiedDate` BIGINT(20) NULL DEFAULT NULL, " +
                "`ModifiedBy` INT(11) NULL DEFAULT NULL, " +
                "`CompanyID` INT(11) NULL DEFAULT NULL, " +
                "`LocationID` INT(11) NULL DEFAULT NULL, " +
                "PRIMARY KEY (`SiteAppID`)";
        String sql = "CREATE TABLE IF NOT EXISTS s_SiteMobileApp(" + fields + ")";
        try {
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void alterTable_s_Site() {
        String sql = "ALTER TABLE " + DbAccess.TABLE_SITES + "  ADD COLUMN  Address1 varchar(100) ";
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_SITES + "  ADD COLUMN  Address2 varchar(100) ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_SITES + "  ADD COLUMN  mobileReportRequired varchar(20) ";
        String sql3 = "ALTER TABLE " + DbAccess.TABLE_SITES + "  ADD COLUMN  clientName varchar(200)";

        try {
            if (!isColumnExists(DbAccess.TABLE_SITES, "Address1")) {
                database.execSQL(sql);
                Log.i("alterTable_s_Site", "s_Site Table Altered by Address1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_Site", "s_Site Table Alteration Error for Address1:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_SITES, "Address2")) {
                database.execSQL(sql1);
                Log.i("alterTable_s_Site", "s_Site Table Altered by Address2");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_Site", "s_Site Table Alteration Error for Address2: " + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_SITES, "mobileReportRequired")) {
                database.execSQL(sql2);
                Log.i("alterTable_s_Site", "s_Site Table Altered by mobileReportRequired");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_Site", "s_Site Table Alteration Error for mobileReportRequired: " + e.getMessage());

        }
        try {
            if (!isColumnExists(DbAccess.TABLE_SITES, "clientName")) {
                database.execSQL(sql3);
                Log.i("alterTable_s_Site", "s_Site Table Altered by clientName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_s_Site", "s_Site Table Alteration Error for clientName: " + e.getMessage());
        }
    }

    public void alterTable_notifications() {
        String sql = "ALTER TABLE " + DbAccess.TABLE_NOTIFICATIONS + "  ADD COLUMN  FormID INTEGER(100) ";
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_NOTIFICATIONS + "  ADD COLUMN  SiteID INTEGER(100) ";
        String sql2 = "ALTER TABLE " + DbAccess.TABLE_NOTIFICATIONS + "  ADD COLUMN  EventID INTEGER(100) ";

        try {
            if (!isColumnExists(DbAccess.TABLE_NOTIFICATIONS, "FormID")) {
                database.execSQL(sql);
                Log.i("alterTable_notify", "notification Table Altered by FormID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_notify", "notification Table Alteration Error for FormID:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_NOTIFICATIONS, "SiteID")) {
                database.execSQL(sql1);
                Log.i("alterTable_notify", "notification Table Altered by SiteID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_notify", "notification Table Alteration Error for SiteID:" + e.getMessage());

        }

        try {
            if (!isColumnExists(DbAccess.TABLE_NOTIFICATIONS, "EventID")) {
                database.execSQL(sql2);
                Log.i("alterTable_notify", "notification Table Altered by EventID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_notify", "notification Table Alteration Error for EventID:" + e.getMessage());

        }
    }

    //TODO: CONSTRUCTION APP TABLES BEGINS HERE
    public void createTable_PostData_IfNotExist() {

        String fields = "`PostID` BIGINT(20) NOT NULL," +
                "`UserID` INT(11) NOT NULL,	" +
                "`SiteId` INT(11) NOT NULL,	" +
                "`LocationId` INT(11) NULL , " +
                "`DisplayFlag` INT(11)  NULL , " +
                "`PostText` VARCHAR(4000) NULL , " +
                "`CreatedBy` INT(11)  NULL , " +
                "`ModifiedBy` INT(11) NULL , " +
                "`ServerCreationDate` BIGINT(20) NULL , " +
                "`ServerModificationDate` BIGINT(20) NULL , " +
                "`CreationDate` BIGINT(20) NULL , " +
                "`ModificationDate` BIGINT(20) NULL , " +
                "`Latitude` DOUBLE(20) NULL , " +
                "`Longitude` DOUBLE(20) NULL  , " +
                "`sPostID` INT(11) NULL , " +
                "`ClientPostId` VARCHAR(15) NULL , " +
                "`PostUserName` VARCHAR(30) NULL, " +
                "`DataSyncFlag` INT(5) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS c_PostData(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createTable_PostData_IfNotExist: Table Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createTable_PostData_IfNotExist: Table Not Created ");
        }
    }

    public void createTable_MediaData_IfNotExist() {

        String fields = "`PostID` BIGINT(20) NOT NULL, " +
                "`MediaID` INT(11) NOT NULL ,	" +
                "`FileName` VARCHAR(100) NULL ,	" +
                "`FileKey` VARCHAR(50) NULL ,	" +
                "`DisplayFlag` INT(11) NULL , " +
                "`CreatedBy` INT(11) NULL , " +
                "`ModifiedBy` INT(11) NULL , " +
                "`ServerCreationDate` BIGINT(20) NULL , " +
                "`ServerModificationDate` BIGINT(20) NULL , " +
                "`CreationDate` BIGINT(20) NULL , " +
                "`ModificationDate` BIGINT(20) NULL , " +
                "`Latitude` DOUBLE(20) NULL , " +
                "`Longitude` DOUBLE(20) NULL , " +
                "`Caption` VARCHAR(50) NULL ,	" +
                "`sMediaId` INT(11) NULL ,	" +
                "`SiteId` INT(11)  NULL, " +
                "`ClientMediaId` VARCHAR(15) NULL, " +
                "`attachmentType` VARCHAR(50) NULL, " +
                "`MediaUploadStatus` VARCHAR(20) NULL, " +
                "`File` VARCHAR(200) NULL, " +
                "`DataSyncFlag` INT(5) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS c_MediaData(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createTable_MediaData_IfNotExist: Media Data Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createTable_MediaData_IfNotExist: Media Data Table Not Created");
        }
    }

    public void createTable_CTagData_IfNotExist() {

        String fields = "`MediaID` INT(11) NOT NULL," +
                "`PostID` BIGINT(20) NOT NULL, " +
                "`TagID` INT(11) NOT NULL, " +
                "`CreatedBy` INT(11) NULL, " +
                "`ModifiedBy` INT(11) NULL, " +
                "`ServerCreationDate` BIGINT(20) NULL, " +
                "`ServerModificationDate` BIGINT(20) NULL, " +
                "`CreationDate` BIGINT(20) NULL, " +
                "`ModificationDate` BIGINT(20) NULL, " +
                "`sTagId` INT(11) NULL,	" +
                "`SiteId` INT(11) NULL,	" +
                "`DisplayFlag` INT(11) NULL, " +
                "`ClientTagId` VARCHAR(15) NULL, " +
                "`cTagId` INT(11) NULL, " +
                "`DataSyncFlag` INT(5) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS c_CTagData(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createTable_CTagData_IfNotExist: CTag Data table created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createTable_CTagData_IfNotExist: CTag Data table not created");
        }

    }

    public void createTable_RTagData_IfNotExist() {

        String fields = "`UserID` INT(11) NOT NULL,	" +
                "`SiteId` INT(11) NULL,	" +
                "`CompanyID` INT(11) NULL, " +
                "`TagID` BIGINT(20) NOT NULL, " +
                "`Tag` VARCHAR(400) NULL, " +
                "`CreatedBy` INT(11) NULL, " +
                "`ModifiedBy` INT(11) NULL, " +
                "`ServerCreationDate` BIGINT(20) NULL, " +
                "`ServerModificationDate` BIGINT(20) NULL, " +
                "`CreationDate` BIGINT(20) NULL, " +
                "`ModificationDate` BIGINT(20) NULL, " +
                "`ClientTagId` VARCHAR(15) NULL, " +
                "`DataSyncFlag` INT(5) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS c_RTagData(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createTable_RTagData_IfNotExist: RTag Data table created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createTable_RTagData_IfNotExist: RTag Data table not created");
        }
    }

    public void createTableMetaDataAttributesIFNotExists() {
        String fields = "SiteID INT, " +
                "MobileAppID INT NOT NULL, " +
                "LocationID INT, " +
                "FieldParameterID INT NOT NULL, " +
                "ParameterID INT, " +
                "DesiredUnits Varchar(50), " +
                "ext_field1 VARCHAR(500), " +
                "ext_field2 VARCHAR(500), " +
                "ext_field3 VARCHAR(500), " +
                "ext_field4 VARCHAR(500), " +
                "ext_field5 VARCHAR(500), " +
                "ext_field6 VARCHAR(500), " +
                "ext_field7 VARCHAR(500), " +
                "field_parameter_operands VARCHAR(500), " +
                "enable_parameter_notes VARCHAR(1), " +
                "showLast2 VARCHAR(1), " +
                "parameter_hint VARCHAR(500), " +
                "parent_parameter_id INT, " +
                "percent_difference DOUBLE, " +
                "routine_id INT, " +
                "CreationDate LONG, " +
                "ModifiedDate LONG, " +
                "Createdby INT, " +
                "multiNote VARCHAR(100) DEFAULT (null), " +
                "mandatoryField INTEGER";

        String sql = "CREATE TABLE IF NOT EXISTS " + DbAccess.TABLE_META_DATA_ATTRIBUTES
                + "(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("MetaAttributes",
                    "createTableMetaDataAttributesIFNotExists: Table Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("MetaAttributes", "createTableMetaDataAttributesIFNotExists: Table not created");
        }
    }

    public void createTableTaskDataListIFNotExists() {
        String fields = "`taskId` BIGINT(20) NOT NULL,	" +
                "`taskTitle` VARCHAR(50) NULL,	" +
                "`taskDescription` VARCHAR(300) NULL, " +
                "`parentTaskId` INT(11) NOT NULL, " +
                "`clientTaskId` INT(11) NULL, " +
                "`taskStatus` VARCHAR(20) NULL, " +
                "`projectId` INT(11) NULL, " +
                "`taskOwner` INT(11) NULL, " +
                "`dueDate` BIGINT(20) NULL, " +
                "`createdBy` INT(11) NULL, " +
                "`creationDate` BIGINT(20) NULL, " +
                "`modifiedBy` INT(11) NULL, " +
                "`modificationDate` BIGINT(20) NULL, " +
                "`dataSyncFlag` INT(11) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS TaskDataList(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskDataList", "createTableTaskDataListIFNotExists: Task Data List Table Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskDataList", "createTableTaskDataListIFNotExists: Task Data List Table not created");
        }
    }

    public void createTableTaskAttachmentListIfNotExists() {
        String fields = "`taskAttachmentId` BIGINT(20) NOT NULL, " +
                "`taskId` INT(11) NOT NULL,	" +
                "`fileName` VARCHAR(50) NULL,	" +
                "`fileExtension` VARCHAR(50) NULL,	" +
                "`fileKey` VARCHAR(300) NULL, " +
                "`attachmentDescription` VARCHAR(300) NULL, " +
                "`commentId` INT(11) NOT NULL, " +
                "`displayFlag` INT(11) NULL, " +
                "`latitude` DOUBLE(20) NULL, " +
                "`longitude` DOUBLE(20) NULL, " +
                "`createdBy` INT(11) NULL, " +
                "`creationDate` BIGINT(20) NULL, " +
                "`modifiedBy` INT(11) NULL, " +
                "`modificationDate` BIGINT(20) NULL, " +
                "`mediaUploadStatus` VARCHAR(20) NULL, " +
                "`dataSyncFlag` INT(11) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS TaskDataAttachmentList(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskDataList", "createTableTaskAttachmentListIfNotExists: Task Data Attachment List Table Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskDataList", "createTableTaskAttachmentListIfNotExists: Task Data Attachment List Table not created");
        }
    }

    public void createTableTaskCommentListIFNotExists() {
        String fields = "`taskCommentId` BIGINT(20) NOT NULL, " +
                "`taskId` INT(11) NOT NULL,	" +
                "`clientTaskCommentId` INT(11) NULL,	" +
                "`comment` VARCHAR(4000) NULL, " +
                "`isAttachment` INT(11) NULL, " +
                "`createdBy` INT(11) NULL, " +
                "`creationDate` BIGINT(20) NULL, " +
                "`modifiedBy` INT(11) NULL, " +
                "`modificationDate` BIGINT(20) NULL, " +
                "`dataSyncFlag` INT(11) NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS TaskDataCommentList(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskDataList", "createTableTaskCommentListIFNotExists: Task Data Comment List Table Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskDataList", "createTableTaskCommentListIFNotExists: Task Data Comment List Table not created");
        }
    }

    public void createTableLocationProfilePicIfNotExists() {

        String fields = "locationId INT(11) NOT NULL, " +
                "attachmentId INT(11) NOT NULL,	" +
                "attachmentURL VARCHAR(500) DEFAULT NULL, " +
                "thumbnailURL VARCHAR(500) DEFAULT NULL, " +
                "creationDate BIGINT(20) DEFAULT NULL, " +
                "modifiedDate BIGINT(20) DEFAULT NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS " + DbAccess.TABLE_LOCATION_PROFILE_PICTURES
                + "(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("LocationProfilePic", "createTableTLocationProfilePicIfNotExists: LocationProfilePic Table Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("LocationProfilePic", "createTableLocationProfilePicIfNotExists:LocationProfilePic Table not created");
        }
    }

    public void createSiteTableForConstruction() {

        String fields = "`SiteID` INT(11) NOT NULL,	" +
                "`SiteName` VARCHAR(11) ,	" +
                "`Latitude` DOUBLE(20) , " +
                "`Longitude` DOUBLE(20) , " +
                "`Address1` VARCHAR(100) , " +
                "`Address2` VARCHAR(100) ";


        String sql = "CREATE TABLE IF NOT EXISTS s_Site(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createSiteTableForConstruction: site table created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createSiteTableForConstruction: site table not created");
        }
    }

    public void createTable_SimpleNoteData_Timestamp_IfNotExist() {

        String fields = "`SiteID` INT(11) NOT NULL,	" +
                "`UserID` INT(11) ,	" +
                "`LastSyncDate` VARCHAR(20) ";

        String sql = "CREATE TABLE IF NOT EXISTS c_TimeStampSND(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createTable_SimpleNoteData_Timestamp_IfNotExist: TIMESTAMP table created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createTable_SimpleNoteData_Timestamp_IfNotExist: TIMESTAMP table not created");
        }
    }

    public void createTable_Construction_AttachmentMedia_IfNotExist() {

        String fields = "`PostID` BIGINT(20) NOT NULL, " +
                "`MediaID` INT(11) NOT NULL ,	" +
                "`FileName` VARCHAR(100) NULL ,	" +
                "`FileKey` VARCHAR(50) NULL ,	" +
                "`DisplayFlag` INT(11) NULL , " +
                "`CreatedBy` INT(11) NULL , " +
                "`ModifiedBy` INT(11) NULL , " +
                "`ServerCreationDate` BIGINT(20) NULL , " +
                "`ServerModificationDate` BIGINT(20) NULL , " +
                "`CreationDate` BIGINT(20) NULL , " +
                "`ModificationDate` BIGINT(20) NULL , " +
                "`Latitude` DOUBLE(20) NULL , " +
                "`Longitude` DOUBLE(20) NULL , " +
                "`Caption` VARCHAR(50) NULL ,	" +
                "`sMediaId` INT(11) NULL ,	" +
                "`SiteId` INT(11)  NULL ";

        String sql = "CREATE TABLE IF NOT EXISTS c_Attachment_MediaData(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("ConstructionTables", "createTable_MediaData_IfNotExist: Media Data Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("ConstructionTables", "createTable_MediaData_IfNotExist: Media Data Table Not Created");
        }
    }

    public void createTableTaskDetailsIfNotExist() {

        String fields = "`task_id` int(11) NOT NULL," +
                "`task_title` varchar(45) DEFAULT NULL," +
                "`task_description` varchar(5000) DEFAULT NULL," +
                "`parent_task_id` int(11) DEFAULT NULL," +
                "`task_status` varchar(45) DEFAULT NULL," +
                "`project_id` int(11) DEFAULT NULL," +
                "`task_owner` int(11) DEFAULT NULL," +
                "`due_date` bigint(20) DEFAULT NULL," +
                "`created_by` int(11) DEFAULT NULL," +
                "`creation_date` bigint(20) DEFAULT NULL," +
                "`modified_by` int(11) DEFAULT NULL," +
                "`modification_date` bigint(20) DEFAULT NULL," +
                "`server_creation_date` bigint(20) DEFAULT NULL," +
                "`server_modification_date` bigint(20) DEFAULT NULL," +
                "`data_sync_flag` int(11) DEFAULT 0," +
                "PRIMARY KEY (`task_id`)";

        String sql = "CREATE TABLE IF NOT EXISTS w_task_details(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskDetailsTable", "Create TaskDetailsTable: Data Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskDetailsTable", "Create TaskDetailsTable: Data Table Not Created");
        }
    }

    public void alterTaskTableColumns() {
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN  clientTaskId int(11) DEFAULT NULL";

        String sql2 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN field_parameter_id int(11) DEFAULT NULL";

        String sql3 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN location_id int(11) DEFAULT NULL";
        String sql4 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN mobile_app_id int(11) DEFAULT NULL";
        String sql5 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN set_id int(11) DEFAULT NULL";
        String sql6 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN latitude Double(20) DEFAULT NULL";
        String sql7 = "ALTER TABLE " + DbAccess.TABLE_TASK_DETAILS
                + " ADD COLUMN longitude Double(20) DEFAULT NULL";

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "clientTaskId"))
                database.execSQL(sql1);
            Log.i("alterTable_task_details", "Task details Table Altered by clientTaskId");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "field_parameter_id"))
                database.execSQL(sql2);
            Log.i("alterTable_task_details", "Task details Table Altered by field_parameter_id");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "location_id"))
                database.execSQL(sql3);
            Log.i("alterTable_task_details", "Task details Table Altered by location_id");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "mobile_app_id"))
                database.execSQL(sql4);
            Log.i("alterTable_task_details", "Task details Table Altered by mobile_app_id");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "set_id"))
                database.execSQL(sql5);
            Log.i("alterTable_task_details", "Task details Table Altered by set_id");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "latitude"))
                database.execSQL(sql6);
            Log.i("alterTable_task_details", "Task details Table Altered by latitude");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_DETAILS, "longitude"))
                database.execSQL(sql7);
            Log.i("alterTable_task_details", "Task details Table Altered by longitude");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alterTable_task_details", "Task details Table Alteration Error:" + e.getMessage());
        }
    }

    public void createTableTaskCommentsIfNotExist() {

        String fields = "`task_comments_id` int(11) NOT NULL," +
                "`task_id` int(11) DEFAULT NULL," +
                "`comment` varchar(5000) DEFAULT NULL," +
                "`is_attachment` bit(1) DEFAULT NULL," +
                "`created_by` int(11) DEFAULT NULL," +
                "`creation_date` bigint(20) DEFAULT NULL," +
                "`modified_by` int(11) DEFAULT NULL," +
                "`modification_date` bigint(20) DEFAULT NULL," +
                "`server_creation_date` bigint(20) DEFAULT NULL," +
                "`server_modification_date` bigint(20) DEFAULT NULL," +
                "`data_sync_flag` int(11) DEFAULT 0, " +
                "PRIMARY KEY (`task_comments_id`)";

        String sql = "CREATE TABLE IF NOT EXISTS w_task_comments(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskCommentsTable", "Create TaskCommentsTable: Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskCommentsTable", "Create TaskCommentsTable: Table Not Created");
        }
    }

    public void alterTaskCommentsTableColumns() {
        String sql1 = "ALTER TABLE " + DbAccess.TABLE_TASK_COMMENTS
                + " ADD COLUMN  clientTaskCommentId int(11) DEFAULT NULL ";

        try {
            if (!isColumnExists(DbAccess.TABLE_TASK_COMMENTS, "clientTaskCommentId"))
                database.execSQL(sql1);
            Log.i("alter_task_comments", "Task comments Table Altered by clientTaskCommentId");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("alter_task_comments", "Task comments Table Alteration Error:" + e.getMessage());
        }
    }

    public void createTableTaskAttachmentsIfNotExist() {

        String fields = "`task_attachments_id` int(11) NOT NULL," +
                "`task_id` int(11) DEFAULT NULL," +
                "`file_name` varchar(100) DEFAULT NULL," +
                "`file_key` varchar(200) DEFAULT NULL," +
                "`attachment_description` varchar(2000) DEFAULT NULL," +
                "`comment_id` int(11) DEFAULT NULL," +
                "`latitude` double DEFAULT NULL," +
                "`longitude` double DEFAULT NULL," +
                "`display_flag` int(11) DEFAULT NULL," +
                "`created_by` int(11) DEFAULT NULL," +
                "`creation_date` bigint(20) DEFAULT NULL," +
                "`modified_by` int(11) DEFAULT NULL," +
                "`modification_date` bigint(20) DEFAULT NULL," +
                "`server_creation_date` bigint(20) DEFAULT NULL," +
                "`server_modification_date` bigint(20) DEFAULT NULL," +
                "`data_sync_flag` int(11) DEFAULT 0, " +
                "PRIMARY KEY (`task_attachments_id`)";

        String sql = "CREATE TABLE IF NOT EXISTS w_task_attachments(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskAttachmentsTable", "Create TaskAttachmentsTable: Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskAttachmentsTable", "Create TaskAttachmentsTable: Table Not Created");
        }
    }

    public void createTableTaskUsersIfNotExist() {

        String fields = "`task_users_id` int(11) NOT NULL," +
                "`task_id` int(11) DEFAULT NULL," +
                "`user_id` int(11) DEFAULT NULL," +
                "`user_role` varchar(45) DEFAULT NULL," +
                "`creation_date` bigint(20) DEFAULT NULL," +
                "`created_by` int(11) DEFAULT NULL," +
                "`modification_date` bigint(20) DEFAULT NULL," +
                "`server_creation_date` bigint(20) DEFAULT NULL," +
                "`server_modification_date` bigint(20) DEFAULT NULL," +
                "`modified_by` int(11) DEFAULT NULL, " +
                "PRIMARY KEY (`task_users_id`)";

        String sql = "CREATE TABLE IF NOT EXISTS w_task_users(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskUsersTable", "Create TaskUsersTable: Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskUsersTable", "Create TaskUsersTable: Table Not Created");
        }
    }

    public void createTableCopiedFormTemplateIfNotExist() {

        String fields = "`fileName` varchar(500) DEFAULT NULL, " +
                "`creation_date` bigint(20) DEFAULT NULL, " +
                "`copiedTemplate` BLOB(30000) DEFAULT NULL";

        String sql = "CREATE TABLE IF NOT EXISTS " + DbAccess.TABLE_COPIED_FORM_TEMPLATE + "(" + fields + ")";
        try {
            database.execSQL(sql);
            Log.e("TaskUsersTable", "Create TaskUsersTable: Table Created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TaskUsersTable", "Create TaskUsersTable: Table Not Created");
        }
    }
}
