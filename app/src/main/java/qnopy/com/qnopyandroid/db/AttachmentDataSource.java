package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.Attachment;
import qnopy.com.qnopyandroid.clientmodel.DownloadEventDataResponse;
import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.clientmodel.GridImageItem;
import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures;
import qnopy.com.qnopyandroid.clientmodel.LogDetails;
import qnopy.com.qnopyandroid.requestmodel.CustomerSign;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.FieldDataForEventDownload;
import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;
import qnopy.com.qnopyandroid.util.Util;

@Singleton
public class AttachmentDataSource {
    final String KEY_EventID = "EventID";// INT NOT NULL REFERENCES d_Event (EventID)
    final String KEY_LocationID = "LocationID";// INT NOT NULL REFERENCES s_Location (LocationID)

    final String KEY_AttachmentType = "AttachmentType";
    final String KEY_FileLocation = "FileLocation";

    final String KEY_Latitude = "Latitude";// REAL
    final String KEY_Longitude = "Longitude";// REAL

    final String KEY_Notes = "Notes";// VARCHAR(200)
    final String KEY_CreationDate = "CreationDate";// long
    final String KEY_EmailSentFlag = "EmailSentFlag";// VARCHAR(1)
    final String KEY_DataSyncFlag = "DataSyncFlag";// VARCHAR(1)
    final String KEY_TimeTaken = "timeTaken";

    final String KEY_SiteID = "SiteID";
    final String KEY_SetID = "SetID";
    final String KEY_UserID = "UserID";
    final String KEY_MobileAppID = "MobileAppID";

    final String KEY_ExtField1 = "ExtField1";// VARCHAR(100)
    final String KEY_ExtField2 = "ExtField2";// VARCHAR(100)
    final String KEY_ExtField3 = "ExtField3";// VARCHAR(100)
    final String KEY_ExtField4 = "ExtField4";// VARCHAR(100)
    final String KEY_ExtField5 = "ExtField5";// VARCHAR(100)
    final String KEY_ExtField6 = "ExtField6";// VARCHAR(100)
    final String KEY_ExtField7 = "ExtField7";// VARCHAR(100)
    final String KEY_FieldParameterID = "FieldParameterID";// VARCHAR(100)
    final String KEY_Attachment_Date = "AttachmentDate";// VARCHAR(100)
    final String KEY_Attachment_Time = "AttachmentTime";// VARCHAR(100)
    final String KEY_ModificationDate = "ModificationDate";// VARCHAR(100)
    final String KEY_Azimuth = "Azimuth";// VARCHAR(100)
    final String KEY_AttachmentName = "AttachmentName";// VARCHAR(100)
    final String KEY_fileKey = "fileKey";// VARCHAR(100)
    final String KEY_fileKeyThumb = "fileKeyThumb";// VARCHAR(100)
    final String KEY_originalFileName = "originalFileName";// VARCHAR(100)
    final String KEY_fileKeyEncode = "fileKeyEncode";// VARCHAR(100)
    final String KEY_fileKeyThumbEncode = "fileKeyThumbEncode";// VARCHAR(100)
    final String KEY_attachmentUUID = "attachmentUUID";// VARCHAR(100)
    final String KEY_noteUpdate = "noteUpdate";// int(1)
    final String KEY_file1000Loc = "file1000Loc";// int(1)
    final String KEY_fileThumbLoc = "fileThumbLoc";// int(1)

    public SQLiteDatabase database;
    private static final String TAG = "AttachmentDS";

    Context mContext;

    enum AttachmentColsBulkInsert {
        KEY_EventID(1), KEY_LocationID(2), KEY_FieldParameterID(3),
        KEY_AttachmentType(4), KEY_Notes(5), KEY_SetID(6), KEY_Latitude(7), KEY_Longitude(8),
        KEY_ExtField1(9), KEY_SiteID(10), KEY_UserID(11), KEY_MobileAppID(12), KEY_ExtField2(13),
        KEY_ExtField3(14), KEY_ExtField4(15), KEY_ExtField5(16), KEY_ExtField6(17), KEY_ExtField7(18),
        KEY_CreationDate(19), KEY_EmailSentFlag(20), KEY_DataSyncFlag(21),
        KEY_Attachment_Date(22), KEY_Attachment_Time(23), KEY_Azimuth(24), KEY_fileKey(25),
        KEY_fileKeyThumb(26), KEY_originalFileName(27), KEY_fileKeyEncode(28), KEY_fileKeyThumbEncode(29),
        KEY_attachmentUUID(30), KEY_TimeTaken(31);

        private final int index;

        public int getIndex() {
            return index;
        }

        AttachmentColsBulkInsert(int value) {
            this.index = value;
        }
    }

    public int getcountForID(int metaParamID, int locid) {

        int count = 0;
        String query = "select (1) from d_attachment where FieldParameterID=" + metaParamID + " and LocationID=" + locid;

        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            count = c.getCount();
        }
        return count;
    }

    public int deleteAttachmentset(String locationID, int eventID, int AppID, int curSetID, int siteid) {
        int ret = 0;
        String whereClause = "EventID =? and LocationID=? and SiteID=? and MobileAppID=? and SetID=?";
        String[] whereArgs = new String[]{"" + eventID, locationID + "", siteid + "", AppID + "", curSetID + ""};
        try {
            ret = database.delete(DbAccess.TABLE_ATTACHMENT, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void updateAttachmentset(String locationID, int eventID, int AppID, int siteID, int curSetID) {
        Cursor c = null;
        String query = "update d_Attachment set SetID=SetID-1 where LocationID="
                + locationID + " and EventID =" + eventID + " and SiteID=" + siteID + "" +
                " and MobileAppID=" + AppID + " and SetID >" + curSetID;
        Log.i(TAG, " updateAttachmentset() for set query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, null);
            Log.i(TAG, " updateAttachmentset() for set result count:" + c.getCount());
            c.moveToFirst();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    public void updateAttachmentThumbnail(Attachment attachment) {
        Cursor c = null;
        String query = "update d_Attachment set fileThumbLoc = ? where LocationID="
                + attachment.getLocationId() + " and EventID =" + attachment.getEvent().getEventId()
                + " and SiteID=" + attachment.getSiteId() + "" +
                " and MobileAppID=" + attachment.getMobileAppId() + " and SetID = " + attachment.getSetId();
        Log.i(TAG, " updateAttachmentThumb() query:" + query);

        int ret = 0;
        try {
            c = database.rawQuery(query, new String[]{attachment.getFileThumb()});
            Log.i(TAG, " updateAttachmentThumb() result count:" + c.getCount());
            c.moveToFirst();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
    }

    public void updateAttachmentSet(FieldDataSyncStaging fieldData, int setIndex) {
        Cursor cursor = null;

        String query = "UPDATE d_Attachment SET SetID = ? where EventID = ? and SetID = ? and " +
                "LocationID = ? and MobileAppID= ?";


        String[] whereClause = new String[]{setIndex + "",
                fieldData.getEventId() + "", fieldData.getExtField1(), fieldData.getLocationId() + "",
                fieldData.getMobileAppId() + ""};

        int ret = 0;
        try {
            cursor = database.rawQuery(query, whereClause);
            Log.i(TAG, " updateAttachmentSet() result count:" + cursor.getCount());
            cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public void updateAttachmentType(GalleryItem galleryItem, String attachmentType) {
        LocationProfilePictureDataSource pictureDataSource = new LocationProfilePictureDataSource(mContext);
        List<LocationProfilePictures> profilePictures = new ArrayList<>();
        LocationProfilePictures pictures = new LocationProfilePictures();
        pictures.setLocationId(Integer.parseInt(galleryItem.getLocID()));
        pictures.setAttachmentId(-Util.randInt(99, 99999));
        pictures.setAttachmentURL(galleryItem.getFileLocation());
        pictures.setCreationDate(System.currentTimeMillis());
        pictures.setThumbnailURL(galleryItem.getFileLocation());
        pictures.setModificationDate(System.currentTimeMillis());
        pictures.setInsert(true);
        profilePictures.add(pictures);

        //added check for fileLocation as it'll be unique only
        if (pictureDataSource.isAttachmentExists(galleryItem.getFileLocation())
                && attachmentType.equalsIgnoreCase("P")) {
            pictureDataSource.deleteAttachment(galleryItem.getFileLocation());
        } else {
            //should be inserted if set to profile picture only
            if (attachmentType.equalsIgnoreCase("L"))
                pictureDataSource.insertLocProfilePictures(profilePictures);
        }

        int ret = 0;
        ContentValues values = new ContentValues();
        //  if (notes != null) {
        String nullValue = null;

        values.put(KEY_AttachmentType, attachmentType);
        values.put(KEY_DataSyncFlag, nullValue);
        values.put(KEY_ExtField5, System.currentTimeMillis());
        //}
        String whereClause = "SiteID = ? and EventID = ? and FileLocation = ?";
        String[] whereArgs = new String[]{"" + galleryItem.getSiteID(),
                galleryItem.getEventID(), galleryItem.getFileLocation()};
        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, values, whereClause, whereArgs);
            Log.i(TAG, " updateAttachmentType() for set result count:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum DataForSync {
        All,
        EmailNotSent,
        DataNotSynced
    }

    public enum SyncType {
        email, data
    }

    @Inject
    public AttachmentDataSource(Context context) {
        Log.i(TAG, "AttachmentDataSource() IN time:" + System.currentTimeMillis());
        mContext = context;

        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;
        }

        Log.i(TAG, "AttachmentDataSource() OUT time:" + System.currentTimeMillis());

    }

    public String getCreationDateForMobileApp(int currentAppID, int eventID, int siteID, String locationID, int userID, int extfield1) {

        String creationdate11 = null;
        Cursor cursor = null;
        String query = "select distinct CreationDate from d_FieldData where" +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID + " and SiteID=" + siteID + " and LocationID=" + locationID + " and UserID=" + userID + " and (FieldParameterID=25 OR FieldParameterID=15)";

        query = "select min(CreationDate) from d_FieldData where" +
                " MobileAppID=" + currentAppID + " and EventID=" + eventID +
                " and SiteID=" + siteID + " and LocationID=" + locationID +
                " and UserID=" + userID + " and ExtField1=" + extfield1;
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    creationdate11 = cursor.getString(0);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                // c = null;
            }
        }
        return creationdate11;
    }

    public long insertAttachmentDataForSignature(AttachmentData attachData) {

        long ret = 0;
        if (attachData.getSetId() < 1) {
            attachData.setSetId(1);
        }

        String setCreationdate = getCreationDateForMobileApp(attachData.getMobileAppId(), attachData.getEventID(), attachData.getSiteId(),
                attachData.getLocationID(), attachData.getUserId(), attachData.getSetId());

        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_EventID, attachData.getEventID());
            values.put(KEY_LocationID, attachData.getLocationID());
            values.put(KEY_AttachmentType, attachData.getAttachmentType());
            values.put(KEY_FileLocation, attachData.getFileLocation());
            values.put(KEY_Latitude, attachData.getLatitude());
            values.put(KEY_Longitude, attachData.getLongitude());
            values.put(KEY_Notes, attachData.getNotes());

            values.put(KEY_SiteID, attachData.getSiteId());
            values.put(KEY_UserID, attachData.getUserId());
            values.put(KEY_MobileAppID, attachData.getMobileAppId());

            if (attachData.getSetId() != null) {
                values.put(KEY_SetID, attachData.getSetId());
            }

            values.put(KEY_TimeTaken, attachData.getTimeTaken());

            if (setCreationdate != null && !setCreationdate.isEmpty() && !setCreationdate.equalsIgnoreCase("0")) {
                values.put(KEY_CreationDate, setCreationdate);
            }

            values.put(KEY_EmailSentFlag, attachData.getEmailSentFlag());
            values.put(KEY_DataSyncFlag, attachData.getDataSyncFlag());
            values.put(KEY_ExtField1, attachData.getExtField1());

            // TODO: 28-Feb-17 Added new Columns
            values.put(KEY_FieldParameterID, attachData.getFieldParameterID());
            values.put(KEY_ModificationDate, System.currentTimeMillis());

            String time, date;
            Long currentDateTime_In_Millis = System.currentTimeMillis();
            date = Util.getMMddyyyyFromMilliSeconds(currentDateTime_In_Millis + "");
            time = Util.gethhmmFromMilliS(currentDateTime_In_Millis);

            Log.i(TAG, "Attachment Date:" + date + " ,Attachment Time:" + time);

            values.put(KEY_Attachment_Date, date);
            values.put(KEY_Attachment_Time, time);
            values.put(KEY_Azimuth, attachData.getAzimuth());
            values.put(KEY_ExtField2, attachData.getExtField2());
            values.put(KEY_ExtField3, date);
            values.put(KEY_ExtField4, time);
            values.put(KEY_ExtField5, attachData.getModificationDate());
            values.put(KEY_ExtField6, attachData.getExtField6());
            values.put(KEY_ExtField7, attachData.getExtField7());
            values.put(KEY_AttachmentName, attachData.getName());

            values.put(KEY_file1000Loc, attachData.getFile1000());
            values.put(KEY_fileThumbLoc, attachData.getFileThumb());
            //    values.put(KEY_NAME,attachData.getName());

            String uuid = Util.randomUUID(mContext, false);
            values.put(KEY_attachmentUUID, uuid);

            try {
                ret = database.insert(DbAccess.TABLE_ATTACHMENT, null, values);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in insertAttachmentData() transaction error=" + e.getMessage());
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in insertAttachmentData() =" + e.getMessage());

        } finally {
            database.endTransaction();
        }

        return ret;
    }

    public ArrayList<String> getAllProfilePictures(String mEventId, String locationID, String siteId) {
        ArrayList<String> filePathList = new ArrayList<>();
        String filePath = null;
        Cursor cursor = null;
        String query = "select FileLocation from d_Attachment where" +
                " EventID=" + mEventId + " and SiteID=" + siteId + " and LocationID=" + locationID
                + " and AttachmentType = 'L'";
        try {
            cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    filePath = cursor.getString(0);
                    filePathList.add(filePath);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return filePathList;
    }

    public ArrayList<CustomerSign> getAttachmentListForSignature(int evntid, int siteid,
                                                                 int fieldparamid, String locid,
                                                                 int usrid, int mid, int setid) {
        String query = null;
        ArrayList<CustomerSign> attachlist = new ArrayList<>();
        String path = null;

        query = "select d.AttachmentName, d.FileLocation, d.attachmentUUID from d_Attachment d where" +
                " d.EventID=" + evntid + " and d.SiteID=" + siteid + " and d.FieldParameterID="
                + fieldparamid + " and d.MobileAppID=" + mid + " and d.UserID=" + usrid +
                " and d.LocationID=" + locid + " and d.AttachmentType='S'" +
                " and d.SetID=" + setid;

        Log.i(TAG, "getAttachmentListForSignature() query:" + query);
        Cursor c = database.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                CustomerSign attachdata = new CustomerSign();
                attachdata.setName(c.getString(0));
                path = c.getString(1);
                attachdata.setFilepath(path);
                attachdata.setAttachmentUUID(c.getString(c.getColumnIndexOrThrow(KEY_attachmentUUID)));

                ImageView view = new ImageView(mContext);
                view.setImageBitmap(Util.getSignbitmap(path));
                attachdata.setView(view);

                attachlist.add(attachdata);

            } while (c.moveToNext());
            c.close();
        }

        return attachlist;

    }

    public long insertNoteAttachmentData(AttachmentData attachData) {
        long ret = 0;
        String setCreationdate = System.currentTimeMillis() + "";
        if (attachData.getSetId() < 1) {
            attachData.setSetId(1);
        }
        setCreationdate = getCreationDateForMobileApp(attachData.getMobileAppId(), attachData.getEventID(), attachData.getSiteId(),
                attachData.getLocationID(), attachData.getUserId(), attachData.getSetId());
        Log.i(TAG, "getAttachmentListForSignature() Attachment Set:" + attachData.getSetId() + " Creation Date:" + setCreationdate);
        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_EventID, attachData.getEventID());
            values.put(KEY_LocationID, attachData.getLocationID());
            values.put(KEY_AttachmentType, attachData.getAttachmentType());
            values.put(KEY_FileLocation, attachData.getFileLocation());
            values.put(KEY_Latitude, attachData.getLatitude());
            values.put(KEY_Longitude, attachData.getLongitude());
            values.put(KEY_Notes, attachData.getNotes());

            values.put(KEY_SiteID, attachData.getSiteId());
            values.put(KEY_UserID, attachData.getUserId());
            values.put(KEY_MobileAppID, attachData.getMobileAppId());

            if (attachData.getSetId() != null) {
                values.put(KEY_SetID, attachData.getSetId());
            }

            values.put(KEY_TimeTaken, attachData.getTimeTaken());
            values.put(KEY_CreationDate, setCreationdate);
            values.put(KEY_EmailSentFlag, attachData.getEmailSentFlag());
            values.put(KEY_DataSyncFlag, attachData.getDataSyncFlag());
            values.put(KEY_ExtField2, attachData.getFieldParameterID());

            // TODO: 28-Feb-17 Added new Columns
            values.put(KEY_FieldParameterID, attachData.getFieldParameterID());
            values.put(KEY_ModificationDate, System.currentTimeMillis());

            String time, date;
            Long currentDateTime_In_Millis = System.currentTimeMillis();
            date = Util.getMMddyyyyFromMilliSeconds(currentDateTime_In_Millis + "");
            time = Util.gethhmmFromMilliS(currentDateTime_In_Millis);

            Log.i(TAG, "Note Capture Attachment Date:" + date + " ,Attachment Time:" + time);

            values.put(KEY_Attachment_Date, date);
            values.put(KEY_Attachment_Time, time);
            values.put(KEY_Azimuth, attachData.getAzimuth());
            values.put(KEY_ExtField3, date);
            values.put(KEY_ExtField4, time);
            values.put(KEY_ExtField5, attachData.getModificationDate());
            values.put(KEY_ExtField6, attachData.getExtField6());
            values.put(KEY_ExtField7, attachData.getExtField7());

            ret = database.insert(DbAccess.TABLE_ATTACHMENT, null, values);
            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AttachmentDataSource", "Attachment Save Error:" + e.getMessage());

        } finally {
            database.endTransaction();
        }

        System.out.println("insertNoteAttachmentData RET=" + ret);
        return ret;
    }

    public void insertAttachmentData(AttachmentData attachData, boolean isSignature) {

        SharedPreferences prefs = mContext.getSharedPreferences("BADELFGPS", mContext.MODE_PRIVATE);
        String lat = prefs.getString("latitude", "");
        String lng = prefs.getString("longitude", "");

        String setCreationDate = System.currentTimeMillis() + "";

        if (attachData.getSetId() < 1) {
            attachData.setSetId(1);
        }

        if (!isSignature) {
            setCreationDate = getCreationDateForMobileApp(attachData.getMobileAppId(), attachData.getEventID(), attachData.getSiteId(),
                    attachData.getLocationID(), attachData.getUserId(), attachData.getSetId());
        }

        Log.i(TAG, "insertAttachmentData() IN Time:" + System.currentTimeMillis());
        long ret = 0;

        Log.i(TAG, "insertAttachmentData() Attachment Set:" + attachData.getSetId()
                + " Creation Date:" + setCreationDate);

        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_EventID, attachData.getEventID());
            values.put(KEY_LocationID, attachData.getLocationID());
            values.put(KEY_AttachmentType, attachData.getAttachmentType());
            values.put(KEY_FileLocation, attachData.getFileLocation());

            if (lat.equals("") && lng.equals("")) {
                Log.e("attachmentLatLng", "insertAttachmentData: NO LAT LNG CAPTURED ");
                values.put(KEY_Latitude, attachData.getLatitude());
                values.put(KEY_Longitude, attachData.getLongitude());
            } else {
                Log.e("attachmentLatLng", "insertAttachmentData: LAT LNG CAPTURED " + lat + " LNG " + lng);
                values.put(KEY_Latitude, Double.parseDouble(lat));
                values.put(KEY_Longitude, Double.parseDouble(lng));
            }

            values.put(KEY_Notes, attachData.getNotes());

            values.put(KEY_SiteID, attachData.getSiteId());
            values.put(KEY_UserID, attachData.getUserId());
            values.put(KEY_MobileAppID, attachData.getMobileAppId());

            if (attachData.getSetId() != null) {
                values.put(KEY_SetID, attachData.getSetId());
            }

            values.put(KEY_TimeTaken, attachData.getTimeTaken());

            values.put(KEY_CreationDate, setCreationDate);
            Log.i(TAG, "insertAttachmentData() setCreationDate:" + setCreationDate);
            values.put(KEY_EmailSentFlag, attachData.getEmailSentFlag());
            values.put(KEY_DataSyncFlag, attachData.getDataSyncFlag());
            values.put(KEY_ExtField1, attachData.getExtField1());

            //28-Feb-17 Added new Columns
            values.put(KEY_FieldParameterID, attachData.getFieldParameterID());
            values.put(KEY_ModificationDate, System.currentTimeMillis());

            String time, date;
            Long currentDateTime_In_Millis = System.currentTimeMillis();
            date = Util.getMMddyyyyFromMilliSeconds(currentDateTime_In_Millis + "");
            time = Util.gethhmmFromMilliS(currentDateTime_In_Millis);

            Log.i(TAG, "Attachment Date:" + date + " ,Attachment Time:" + time);

            values.put(KEY_Attachment_Date, date);
            values.put(KEY_Attachment_Time, time);
            values.put(KEY_Azimuth, attachData.getAzimuth());
            values.put(KEY_ExtField2, attachData.getFieldParameterID());
            values.put(KEY_ExtField3, date);
            values.put(KEY_ExtField4, time);
            values.put(KEY_ExtField5, System.currentTimeMillis());
            values.put(KEY_ExtField6, attachData.getExtField6());
            values.put(KEY_ExtField7, attachData.getExtField7());

            //added on 04/11/22
            values.put(KEY_file1000Loc, attachData.getFile1000());
            values.put(KEY_fileThumbLoc, attachData.getFileThumb());

            String uuid = Util.randomUUID(mContext, false);
            values.put(KEY_attachmentUUID, uuid);

            try {

                ret = database.insert(DbAccess.TABLE_ATTACHMENT, null, values);
                Log.i(TAG, "insertAttachmentData() RESULT:" + ret);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in insertAttachmentData() transaction error=" + e.getMessage());
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in insertAttachmentData() =" + e.getMessage());

        } finally {
            database.endTransaction();
        }

        Log.i(TAG, "insertAttachmentData() OUT Time:" + System.currentTimeMillis());

        //return ret;
    }

    public boolean isAttachmentUUIDExist(String uuid) {
        Cursor c = null;
        if (uuid != null) {
            int count = 0;
            String query = "select count(" + KEY_attachmentUUID + ") from d_Attachment where " + KEY_attachmentUUID
                    + " like '" + uuid + "'";

            try {
                c = database.rawQuery(query, null);
                if (c != null && c.moveToFirst()) {
                    count = c.getInt(0);
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            }

            return count > 0;
        } else {
            return true;
        }
    }

    public void insertDownloadedAttachmentData(DownloadEventDataResponse.FieldDataAttachment
                                                       attachData, boolean isSignture) {

        if (attachData.getUuid() != null && isAttachmentUUIDExist(attachData.getUuid()))
            return;
        else if (attachData.getFileKeyImageEncode() != null
                && isFileKeyAlreadyExist(attachData.getFileKeyImageEncode()))
            return;

        SharedPreferences prefs = mContext.getSharedPreferences("BADELFGPS", Context.MODE_PRIVATE);
        String lat = prefs.getString("latitude", "");
        String lng = prefs.getString("longitude", "");

        String setCreationdate = System.currentTimeMillis() + "";

        if (attachData.getSetId() < 1) {
            attachData.setSetId(1);
        }

        if (!isSignture) {
            setCreationdate = getCreationDateForMobileApp(attachData.getMobileAppId(),
                    attachData.getEventId(), attachData.getSiteId(),
                    attachData.getLocationId() + "", attachData.getUserId(), attachData.getSetId());
        }

        long ret = 0;

        try {
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(KEY_EventID, attachData.getEventId());
            values.put(KEY_LocationID, attachData.getLocationId());
            values.put(KEY_AttachmentType, attachData.getAttachmentType());
//            values.put(KEY_FileLocation, attachData.getFileLocation());

            if (lat.equals("") && lng.equals("")) {
                Log.e("attachmentLatLng", "insertAttachmentData: NO LAT LNG CAPTURED ");
                values.put(KEY_Latitude, attachData.getLatitude());
                values.put(KEY_Longitude, attachData.getLongitude());
            } else {
                Log.e("attachmentLatLng", "insertAttachmentData: LAT LNG CAPTURED " + lat + " LNG " + lng);
                values.put(KEY_Latitude, Double.parseDouble(lat));
                values.put(KEY_Longitude, Double.parseDouble(lng));
            }

            String notes = attachData.getNotes();

            if (notes != null && notes.toLowerCase().contains("%2b")) {
                notes = notes.replaceAll("%2b", "+");
                notes = notes.replaceAll("%2B", "+");
            }

            values.put(KEY_Notes, notes);

            values.put(KEY_SiteID, attachData.getSiteId());
            values.put(KEY_UserID, attachData.getUserId());
            values.put(KEY_MobileAppID, attachData.getMobileAppId());

            if (attachData.getSetId() != null) {
                values.put(KEY_SetID, attachData.getSetId());
            }

            values.put(KEY_TimeTaken, attachData.getTimeTaken());

            values.put(KEY_CreationDate, setCreationdate);
            Log.i(TAG, "insertAttachmentData() setCreationDate:" + setCreationdate);
            values.put(KEY_EmailSentFlag, 1);
            values.put(KEY_DataSyncFlag, 1);
            values.put(KEY_ExtField1, attachData.getExtField1());

            values.put(KEY_FieldParameterID, attachData.getFieldParameterId());
//            values.put(KEY_ModificationDate, System.currentTimeMillis());

            values.put(KEY_Attachment_Date, attachData.getAttachmentDate());
            values.put(KEY_Attachment_Time, attachData.getAttachmentTime());
            values.put(KEY_Azimuth, attachData.getAzimuth());
            values.put(KEY_ExtField2, attachData.getExtField2());
            values.put(KEY_ExtField3, attachData.getAttachmentDate());
            values.put(KEY_ExtField4, attachData.getAttachmentTime());
            values.put(KEY_ExtField5, System.currentTimeMillis());
            values.put(KEY_ExtField6, attachData.getExtField6());
            values.put(KEY_ExtField7, attachData.getExtField7());
            values.put(KEY_fileKey, attachData.getFileKeyImage());
            values.put(KEY_fileKeyThumb, attachData.getFileKeyThumbImage());
            values.put(KEY_originalFileName, attachData.getFileOnFileSystem());//will have file name
            values.put(KEY_fileKeyEncode, attachData.getFileKeyImageEncode());
            values.put(KEY_fileKeyThumbEncode, attachData.getFileKeyThumbImageEncode());
            values.put(KEY_attachmentUUID, attachData.getUuid());

            try {
                ret = database.insert(DbAccess.TABLE_ATTACHMENT, null, values);
                Log.i(TAG, "insertAttachmentData() RESULT:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in insertAttachmentData() transaction error=" + e.getMessage());
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in insertAttachmentData() =" + e.getMessage());

        } finally {
            database.endTransaction();
        }
    }

    public long storeBulkAttachments(ArrayList<DownloadEventDataResponse.FieldDataAttachment> listAttachment) {
        long count = 0;
        String[] arrColumns = {KEY_EventID, KEY_LocationID, KEY_FieldParameterID,
                KEY_AttachmentType, KEY_Notes, KEY_SetID, KEY_Latitude, KEY_Longitude,
                KEY_ExtField1, KEY_SiteID, KEY_UserID, KEY_MobileAppID, KEY_ExtField2,
                KEY_ExtField3, KEY_ExtField4, KEY_ExtField5, KEY_ExtField6, KEY_ExtField7,
                KEY_CreationDate, KEY_EmailSentFlag, KEY_DataSyncFlag,
                KEY_Attachment_Date, KEY_Attachment_Time, KEY_Azimuth, KEY_fileKey,
                KEY_fileKeyThumb, KEY_originalFileName, KEY_fileKeyEncode, KEY_fileKeyThumbEncode,
                KEY_attachmentUUID, KEY_TimeTaken};

        String columns = Util.splitArrayToString(arrColumns);

        String sql = "INSERT INTO " + DbAccess.TABLE_ATTACHMENT + "(" + columns + ")"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();

        try {

            for (DownloadEventDataResponse.FieldDataAttachment attachment : listAttachment) {

                if (attachment.getEventId() != null)
                    statement.bindLong(AttachmentColsBulkInsert.KEY_EventID.getIndex(),
                            attachment.getEventId());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_EventID.getIndex());

                if (attachment.getLocationId() != null)
                    statement.bindLong(AttachmentColsBulkInsert.KEY_LocationID.getIndex(),
                            attachment.getLocationId());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_LocationID.getIndex());

                if (attachment.getFieldParameterId() != null)
                    statement.bindLong(AttachmentColsBulkInsert.KEY_FieldParameterID.getIndex(),
                            attachment.getFieldParameterId());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_FieldParameterID.getIndex());

                if (attachment.getAttachmentType() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_AttachmentType.getIndex(),
                            attachment.getAttachmentType());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_AttachmentType.getIndex());

                //case when '+' sign is added from web
                String notes = attachment.getNotes();
                if (notes != null && notes.toLowerCase().contains("%2b")) {
                    notes = notes.replaceAll("%2b", "+");
                    notes = notes.replaceAll("%2B", "+");
                    attachment.setNotes(notes);
                }

                if (attachment.getNotes() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_Notes.getIndex(),
                            attachment.getNotes());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_Notes.getIndex());

                if (attachment.getSetId() != null) {
                    if (attachment.getSetId() < 1) {
                        attachment.setSetId(1);
                    }

                    statement.bindLong(AttachmentColsBulkInsert.KEY_SetID.getIndex(),
                            attachment.getSetId());
                } else
                    statement.bindLong(AttachmentColsBulkInsert.KEY_SetID.getIndex(), 1);

                if (attachment.getLatitude() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_Latitude.getIndex(),
                            attachment.getLatitude() + "");
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_Latitude.getIndex());

                if (attachment.getLongitude() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_Longitude.getIndex(),
                            attachment.getLongitude() + "");
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_Longitude.getIndex());

                if (attachment.getExtField1() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField1.getIndex(),
                            attachment.getExtField1());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField1.getIndex());

                if (attachment.getSiteId() != null)
                    statement.bindLong(AttachmentColsBulkInsert.KEY_SiteID.getIndex(),
                            attachment.getSiteId());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_SiteID.getIndex());

                if (attachment.getUserId() != null)
                    statement.bindLong(AttachmentColsBulkInsert.KEY_UserID.getIndex(),
                            attachment.getUserId());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_UserID.getIndex());

                if (attachment.getMobileAppId() != null)
                    statement.bindLong(AttachmentColsBulkInsert.KEY_MobileAppID.getIndex(),
                            attachment.getMobileAppId());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_MobileAppID.getIndex());

                if (attachment.getExtField2() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField2.getIndex(),
                            attachment.getExtField2());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField2.getIndex());

                if (attachment.getExtField3() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField3.getIndex(),
                            attachment.getExtField3());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField3.getIndex());

                if (attachment.getExtField4() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField4.getIndex(),
                            attachment.getExtField4());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField4.getIndex());

                if (attachment.getExtField5() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField5.getIndex(),
                            attachment.getExtField5());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField5.getIndex());

                if (attachment.getExtField6() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField6.getIndex(),
                            attachment.getExtField6());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField6.getIndex());

                if (attachment.getExtField7() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_ExtField7.getIndex(),
                            attachment.getExtField7());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_ExtField7.getIndex());

                statement.bindLong(AttachmentColsBulkInsert.KEY_CreationDate.getIndex(),
                        attachment.getCreationDate());

                statement.bindLong(AttachmentColsBulkInsert.KEY_EmailSentFlag.getIndex(), 1);

                statement.bindLong(AttachmentColsBulkInsert.KEY_DataSyncFlag.getIndex(), 1);

                if (attachment.getAttachmentDate() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_Attachment_Date.getIndex(),
                            attachment.getAttachmentDate());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_Attachment_Date.getIndex());

                if (attachment.getAttachmentTime() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_Attachment_Time.getIndex(),
                            attachment.getAttachmentTime());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_Attachment_Time.getIndex());

                if (attachment.getAzimuth() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_Azimuth.getIndex(),
                            attachment.getAzimuth());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_Azimuth.getIndex());

                if (attachment.getFileKeyImage() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_fileKey.getIndex(),
                            attachment.getFileKeyImage());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_fileKey.getIndex());

                if (attachment.getFileKeyThumbImage() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_fileKeyThumb.getIndex(),
                            attachment.getFileKeyThumbImage());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_fileKeyThumb.getIndex());

                if (attachment.getFileOnFileSystem() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_originalFileName.getIndex(),
                            attachment.getFileOnFileSystem());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_originalFileName.getIndex());

                if (attachment.getFileKeyImageEncode() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_fileKeyEncode.getIndex(),
                            attachment.getFileKeyImageEncode());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_fileKeyEncode.getIndex());

                if (attachment.getFileKeyThumbImageEncode() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_fileKeyThumbEncode.getIndex(),
                            attachment.getFileKeyThumbImageEncode());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_fileKeyThumbEncode.getIndex());

                if (attachment.getUuid() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_attachmentUUID.getIndex(),
                            attachment.getUuid());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_attachmentUUID.getIndex());

                if (attachment.getTimeTaken() != null)
                    statement.bindString(AttachmentColsBulkInsert.KEY_TimeTaken.getIndex(),
                            attachment.getTimeTaken());
                else
                    statement.bindNull(AttachmentColsBulkInsert.KEY_TimeTaken.getIndex());

                count = statement.executeInsert();
                statement.clearBindings();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Bulk insertion of attachment data:" + e.getMessage());
        } finally {
            database.endTransaction();
        }

        return count;
    }

    public ArrayList<GridImageItem> getNoteAttachment(int eventID, int fieldParameterID, int setID,
                                                      int appID, String locID, int siteID) {

        ArrayList<GridImageItem> gridList = new ArrayList<>();
        String query = null;
//		String whereClause = null;
        String value = null;

        String[] whereArgs = null;
        query = "select FileLocation FROM d_Attachment " +
                " where " + KEY_FieldParameterID + "=?" + " and " + KEY_LocationID + "=?" + " and " + " SiteID=? " + " and " + " MobileAppID=? and AttachmentType IS NOT 'S' and FileLocation IS NOT NULL " +
                " and SetID=? and " + KEY_EventID + "=?";

        whereArgs = new String[]{"" + fieldParameterID, "" + locID, "" + siteID, "" + appID, "" + setID, eventID + ""};
        Log.i(TAG, "getNoteAttachments() query=" + query);
//		System.out.println("qqqq args"+"setID="+setID+ "locid="+locID+"siteID="+siteID+"mapp="+appID+"fid="+fieldParameterID);
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, whereArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        value = cursor.getString(0);
                        gridList.add(new GridImageItem(value));
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("", "getNoteAttachment Error:" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return gridList;
    }

    public void deleteNoteAttachment(int eventID, int fieldParameterID, int setID,
                                     int appID, String locID, int siteID) {

        ArrayList<GridImageItem> gridList = new ArrayList<>();
        String query = null;
//		String whereClause = null;
        String value = null;

        String[] whereArgs = null;
        query = "Delete FROM d_Attachment " +
                " where " + KEY_FieldParameterID + "=?" + " and " + KEY_LocationID + "=?" + " and " + " SiteID=? " + " and " + " MobileAppID=? and AttachmentType IS NOT 'S' and FileLocation IS NOT NULL " +
                " and SetID=? and " + KEY_EventID + "=?";

        whereArgs = new String[]{"" + fieldParameterID, "" + locID, "" + siteID, "" + appID, "" + setID, eventID + ""};
        Log.i(TAG, "deleteNoteAttachments() query=" + query);

        try {
            database.execSQL(query, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("d_Attachment", "getNoteAttachment Error:" + e.getLocalizedMessage());
        }
    }

    public List<Attachment> getAttachmentDataToResyncAll() {
        List<Attachment> dataList = new ArrayList<Attachment>();
        String[] fieldDataColumns = null;
        String whereClause = null;
//		String orderBy = null;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_AttachmentType, KEY_FileLocation, KEY_Latitude,
                KEY_Longitude, KEY_Notes, KEY_TimeTaken, KEY_CreationDate, KEY_SetID, KEY_SiteID, KEY_MobileAppID,
                KEY_UserID, KEY_ExtField1, KEY_FieldParameterID, KEY_Attachment_Date,
                KEY_Attachment_Time, KEY_ModificationDate, KEY_ExtField6, KEY_ExtField7,
                KEY_Azimuth, KEY_file1000Loc, KEY_fileThumbLoc};

        whereClause = "FileLocation IS NOT NULL and (EventID>0 and LocationID>=0)";

        Cursor cursor = database.query(DbAccess.TABLE_ATTACHMENT, fieldDataColumns,
                whereClause, null, null, null, null);

        if (cursor == null) {
            Log.i(TAG, "No data for Attachment Data List");
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Attachment data = cursorToAttachData(cursor);
            dataList.add(data);
            cursor.moveToNext();
        }

        return dataList;
    }

    public List<Attachment> getAttachmentDataListFromDB(DataForSync sync) {
        List<Attachment> dataList = new ArrayList<Attachment>();
        String[] fieldDataColumns = null;
        String whereClause = null;
//		String orderBy = null;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_AttachmentType, KEY_FileLocation, KEY_Latitude,
                KEY_Longitude, KEY_Notes, KEY_TimeTaken, KEY_CreationDate, KEY_SetID, KEY_SiteID, KEY_MobileAppID,
                KEY_UserID, KEY_ExtField1, KEY_FieldParameterID, KEY_Attachment_Date,
                KEY_Attachment_Time, KEY_ModificationDate, KEY_ExtField6, KEY_ExtField7,
                KEY_Azimuth, KEY_file1000Loc, KEY_fileThumbLoc, KEY_attachmentUUID};

        if (sync == DataForSync.All) {
//			whereClause = null;
            whereClause = "FileLocation IS NOT NULL ";
        } else if (sync == DataForSync.EmailNotSent) {
            whereClause = KEY_EmailSentFlag + " IS NULL ";
        } else if (sync == qnopy.com.qnopyandroid.db.AttachmentDataSource.DataForSync.DataNotSynced) {
            whereClause = "(" + KEY_DataSyncFlag + " IS NULL OR DataSyncFlag LIKE '') and FileLocation IS NOT NULL and (EventID>0 and LocationID>=0)";
        }

//		orderBy = null;
        //orderBy = "KEY_FieldDataID";

        Cursor cursor = database.query(DbAccess.TABLE_ATTACHMENT, fieldDataColumns,
                whereClause, null, null, null, null);

        if (cursor == null) {
            Log.i(TAG, "No data for Attachment Data List");
            return null;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Attachment data = cursorToAttachData(cursor);
            dataList.add(data);
            cursor.moveToNext();
        }

        return dataList;
    }

    public boolean attachmentsAvailableToSync() {
        //05-Jun-17 Remove All entries with blank file path
//        deleteAttachmentHavingBlankFilePath();

        int count = 0;
        String query = "select count(FileLocation) from d_Attachment where DataSyncFlag IS NOT 1 " +
                "AND FileLocation IS NOT NULL " +
                "AND EventID > 0 " +
                "AND LocationID > 0";
        Cursor cursor = null;
        TempLogsDataSource tempLogsDataSource = new TempLogsDataSource(mContext);
        LogDetails logDetails = new LogDetails();
        logDetails.setAllIds("");
        logDetails.setDate(Util.getFormattedDateFromMilliS(System.currentTimeMillis(),
                GlobalStrings.DATE_FORMAT_MM_DD_YYYY_HRS_MIN));

        try {
            cursor = database.rawQuery(query, null);

            logDetails.setScreenName("Are attachments available to sync query");

            boolean cursorHasData = cursor != null && !cursor.isClosed();
            boolean databaseConnection = database != null && database.isOpen();
            logDetails.setDetails("Checking cursor and database instance: Database: "
                    + databaseConnection + " Cursor: " + cursorHasData);

            tempLogsDataSource.insertTempLogs(logDetails);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;

            logDetails.setScreenName("Are attachments available to sync query exception");
            logDetails.setDetails("Exception: " + e);

            tempLogsDataSource.insertTempLogs(logDetails);
        }
        return count > 0;
    }

    public boolean attachmentsAvailableToSync_Service() {
        //05-Jun-17 Remove All entries with blank file path
//        deleteAttachmentHavingBlankFilePath();

        int count = 0;
        String query = "select count(FileLocation) from d_Attachment where DataSyncFlag IS NOT 1 " +
                "AND FileLocation IS NOT NULL " +
                "AND EventID >0 " +
                "AND LocationID >0";
        Cursor cursor = null;

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                Log.i(TAG, "attachmentsAvailableToSync_Service() d_Attachment query result count=" + count);

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count > 0;// >5 updated
    }

    public boolean isAttachNoteExists(String eventID, String LocationID, String SiteID, String MobileAppID, String SetID, String FieldParameterID) {
        int count = 0;
        String query = "select count(*) from d_Attachment where EventID=" + eventID + " and LocationID=" + LocationID + " and SiteID=" + SiteID +
                " and MobileAppID=" + MobileAppID +
                " and SetID=" + SetID + " and  FieldParameterID=" + FieldParameterID;
        Cursor cursor = null;
        Log.i(TAG, "isAttachNoteExists() Query:" + query);

        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    private Attachment cursorToAttachData(Cursor cursor) {
        Attachment data = new Attachment();

        DEvent event = new DEvent();
        String fileLocation = cursor.getString(3);

        try {
            event.setEventId(cursor.getInt(0));
            data.setEvent(event);
            data.setFileLocation(fileLocation);
            data.setLocationId(cursor.getString(1));
            data.setAttachmentType(cursor.getString(2));
            data.setLatitude(cursor.getDouble(4));
            data.setLongitude(cursor.getDouble(5));
            data.setNotes(cursor.getString(6));
            data.setTimeTaken(cursor.getLong(7));
            data.setCreationDate(cursor.getLong(8));
            data.setSetId(cursor.getInt(9));
            data.setSiteId(cursor.getInt(10));
            data.setMobileAppId(cursor.getInt(11));
            data.setUserId(cursor.getInt(12));
            data.setExtField1(cursor.getString(13));
            String fpID = cursor.getString(14);
            //12-Jan-17
            if (fpID == null || fpID.isEmpty()) {
                fpID = "0";
            }
            data.setFieldParameterID(fpID);
//            data.setExtField2(fpID);
//            data.setExtField3(cursor.getString(15));
//            data.setExtField4(cursor.getString(16));
//            data.setExtField5(cursor.getString(17));
            data.setAttachmentDate(cursor.getString(15));
            data.setAttachmentTime(cursor.getString(16));
            data.setModificationDate(cursor.getString(17));
            data.setExtField6(cursor.getString(18));
            data.setExtField7(cursor.getString(19));
            data.setAzimuth(cursor.getString(20));

            data.setFile1000(cursor.getString(cursor.getColumnIndexOrThrow(KEY_file1000Loc)));
            data.setFileThumb(cursor.getString(cursor.getColumnIndexOrThrow(KEY_fileThumbLoc)));
            data.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(KEY_attachmentUUID)));

//			data.setEmailSentFlag(cursor.getString(8));
//			data.setDataSyncFlag(cursor.getString(9));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "cursorToAttachData error" + e.getLocalizedMessage());
        }

        return data;
    }

    public int updateNotes(int siteID, String eventID, String path, String notes) {
        int ret = 0;
        ContentValues values = new ContentValues();
        //  if (notes != null) {
        String nullValue = null;

        values.put(KEY_Notes, notes);
        values.put(KEY_DataSyncFlag, nullValue);
        values.put(KEY_ModificationDate, System.currentTimeMillis());
        //}
        String whereClause = "SiteID = ? and EventID = ? and FileLocation = ?";
        String[] whereArgs = new String[]{"" + siteID, "" + eventID, "" + path};
        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int updateMultiNotes(int siteID, String eventID, String notes, String fieldParamID,
                                String locationID, String mobID, String setID, String fileLoc) {
        int ret = 0;
        ContentValues values = new ContentValues();
        //  if (notes != null) {
        String nullValue = null;

        values.put(KEY_Notes, notes);
        values.put(KEY_DataSyncFlag, nullValue);
        values.put(KEY_ModificationDate, System.currentTimeMillis());
        values.put(KEY_noteUpdate, 1);
        //}
        String whereClause = "SiteID = ? and EventID = ?  and FieldParameterID=? and LocationID=? " +
                "and MobileAppID=? and SetID=? ";
        String[] whereArgs = new String[]{"" + siteID, "" + eventID, "" + fieldParamID, locationID, mobID, setID};

        if (!fileLoc.trim().isEmpty()) {
            whereClause = "SiteID = ? and EventID = ?  and FieldParameterID=? and LocationID=? " +
                    "and MobileAppID=? and SetID=? and FileLocation = ?";
            whereArgs = new String[]{"" + siteID, "" + eventID, "" + fieldParamID, locationID, mobID, setID, fileLoc};
        }

        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int updateModificationDate(String path, String lati, String longi) {
        int ret = 0;
        ContentValues values = new ContentValues();
        //  if (notes != null) {
        String nullValue = null;


        values.put(KEY_DataSyncFlag, nullValue);
        if (lati != null && longi != null) {
            values.put(KEY_Latitude, lati);
            values.put(KEY_Longitude, longi);
        }
        values.put(KEY_ModificationDate, System.currentTimeMillis());
        //}
        String whereClause = "FileLocation = ?";
        String[] whereArgs = new String[]{path};
        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String readNotes(int siteID, String eventID, String path) {
        Cursor cursor = null;
        String notes = null;
        String query = "select Notes from d_Attachment where SiteID = ? and EventID = ? and FileLocation = ?";
        String[] whereArgs = new String[]{"" + siteID, "" + eventID, "" + path};
        try {
            if (path != null) {
                cursor = database.rawQuery(query, whereArgs);
            }
        } catch (Exception e) {

        }
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                notes = cursor.getString(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return notes;
    }

//    public String getExt2asFieldParameterID(int siteID, String eventID, String locID, int mobappID, String path) {
//        Cursor cursor = null;
//        String fieldparamID = null;
//        String query = "select ExtField2 from d_Attachment where SiteID = ? and EventID = ?  and FileLocation = ? and LocationID = ? and MobileAppID = ?";
//        String[] whereArgs = new String[]{"" + siteID, "" + eventID, "" + path, "" + locID, "" + mobappID};
//        try {
//            if (path != null) {
//                cursor = database.rawQuery(query, whereArgs);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (cursor != null) {
//            cursor.moveToFirst();
//            try {
//                fieldparamID = cursor.getString(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return fieldparamID;
//    }

    public String getFieldParameterID(int siteID, String eventID, String locID, int mobappID, String path) {
        Cursor cursor = null;
        String fieldparamID = null;
        String query = "select FieldParameterID from d_Attachment where SiteID = ? and EventID = ?  and FileLocation = ? and LocationID = ? and MobileAppID = ?";
        String[] whereArgs = new String[]{"" + siteID, "" + eventID, "" + path, "" + locID, "" + mobappID};
        try {
            if (path != null) {
                cursor = database.rawQuery(query, whereArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                fieldparamID = cursor.getString(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fieldparamID;
    }

    public boolean isFileAlreadyExist(String fileLocation) {

        Cursor cursor;
        try {
            String query = "Select count (FileLocation) from d_Attachment " +
                    " where FileLocation='" + fileLocation + "'";

            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);

                cursor.close();
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isFileAlreadyExist :" + e.getMessage());
            return false;
        }

        return false;
    }

    public boolean isFileKeyAlreadyExist(String fileKeyEncode) {

        Cursor cursor;
        try {
            String query = "Select count (fileKeyEncode) from d_Attachment " +
                    " where fileKeyEncode='" + fileKeyEncode + "'";

            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);

                cursor.close();
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isFileKeyAlreadyExist :" + e.getMessage());
            return false;
        }

        return false;
    }

    public ArrayList<GalleryItem> getCardAttachmentDataListFromDB(DataForSync sync, String siteID, String eventID,
                                                                  String LocID, String siteName, String loc, String mobID) {
        ArrayList<GalleryItem> attachmentList = new ArrayList<>();
        String[] fieldDataColumns = null;
        String whereClause = null;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_AttachmentType, KEY_FileLocation, KEY_Latitude,
                KEY_Longitude, KEY_Notes, KEY_SetID, KEY_Attachment_Date,
                KEY_MobileAppID, KEY_Azimuth, KEY_FieldParameterID, KEY_Attachment_Time,
                KEY_fileKey, KEY_fileKeyThumb, KEY_originalFileName, KEY_fileKeyEncode,
                KEY_fileKeyThumbEncode};

        whereClause = " EventID=" + eventID + " and SiteID = " + siteID + " and LocationID = "
                + LocID /*+ " and AttachmentType  NOT IN ('s','S')"*/;// " and MobileAppID = " + mobID +

        try {

            Log.i(TAG, "get CardAttachmentDataListFromDB whereClause " + whereClause);

            Cursor cursor = database.query(DbAccess.TABLE_ATTACHMENT, fieldDataColumns,
                    whereClause, null, null, null, null);

            if (cursor == null) {
                Log.i(TAG, "No Attachments to show in Gallery");
                // return dataList;
            } else {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    GalleryItem attachment = null;
                    DEvent event = null;
                    boolean FileAvailable = false;
                    String attachmentType = cursor.getString(2);
                    String fileLocation = cursor.getString(3);

                    if (fileLocation != null && !fileLocation.isEmpty()) {
                        File fl = new File(fileLocation);//04-Jan-16 Check file is present in external or internal storage
                        if (fl.exists()) {
                            FileAvailable = true;
                        }
                    }

                    event = new DEvent();
                    attachment = new GalleryItem();
                    try {

                        String date = cursor.getString(8);
                        String time = cursor.getString(12);
                        String[] attchDate = date.split("\\/");//mm/dd/yyyy
                        String[] attchTime = time.split("\\:");//Hours:Min:ss

                        int month = Integer.parseInt(attchDate[0]);
                        int day = Integer.parseInt(attchDate[1]);
                        int year = Integer.parseInt(attchDate[2]);

                        int hour = Integer.parseInt(attchTime[0]);
                        int mins = Integer.parseInt(attchTime[1]);

                        int secs = 0;

                        if (attchTime.length > 2) {
                            secs = Integer.parseInt(attchTime[2]);
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, mins);
                        cal.set(Calendar.SECOND, secs);
                        cal.set(Calendar.MILLISECOND, 0);
                        cal.set(Calendar.MONTH, month - 1);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        cal.set(Calendar.YEAR, year);
                        String dateTime = cal.getTimeInMillis() + "";

                        event.setEventId(cursor.getInt(0));
                        attachment.setFileLocation(fileLocation);
                        attachment.setLatitude(cursor.getDouble(4));
                        attachment.setLongitude(cursor.getDouble(5));
                        attachment.setTxtNote(cursor.getString(6));
                        attachment.setTxtDate(dateTime);
                        attachment.setSiteName(siteName);
                        attachment.setLocationName(loc);
                        attachment.setSiteID(Integer.parseInt(siteID));
                        attachment.setLocID(LocID);
                        attachment.setEventID(eventID);
                        attachment.setMobAppID(cursor.getInt(9));
                        // data.setThumbnail(thumbnail);
                        attachment.setSetID(Integer.parseInt(cursor.getString(7)));
                        attachment.setAzimuth(cursor.getString(10));
                        attachment.setFieldParamID(cursor.getString(11));
                        attachment.setAttachmentType(attachmentType);
                        attachment.setFileKey(cursor.getString(13));
                        attachment.setFileKeyThumb(cursor.getString(14));
                        attachment.setOriginalFileName(cursor.getString(15));
                        attachment.setFileKeyEncode(cursor.getString(16));
                        attachment.setFileKeyThumbEncode(cursor.getString(17));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, " exception:" + e.getLocalizedMessage());
                    }
                    attachmentList.add(attachment);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, " Card Attachment Data List:" + e.getMessage());
        }
        return attachmentList;
    }

    public ArrayList<GalleryItem> getAttachmentForFieldParam(String siteID, String eventID,
                                                             String LocID, String mobID, String fpId,
                                                             int curSetID, String siteName,
                                                             String locName) {
        ArrayList<GalleryItem> dataList = new ArrayList<>();
        String[] fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_AttachmentType, KEY_FileLocation, KEY_Latitude,
                KEY_Longitude, KEY_Notes, KEY_SetID, KEY_Attachment_Date,
                KEY_MobileAppID, KEY_Azimuth, KEY_FieldParameterID, KEY_Attachment_Time};

        String whereClause = " EventID=" + eventID + " and SiteID = " + siteID + " and LocationID = "
                + LocID + " and AttachmentType NOT IN ('s','S') and MobileAppID = "
                + mobID + " and FieldParameterID = " + fpId + " and SetID = " + curSetID;

        try {
            Log.i(TAG, "getAttachmentForFieldParam whereClause " + whereClause);

            Cursor cursor = database.query(DbAccess.TABLE_ATTACHMENT, fieldDataColumns,
                    whereClause, null, null, null, null);

            if (cursor == null) {
                Log.i(TAG, "No Attachments to show");
            } else {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String fileLocation = cursor.getString(3);

                    if (fileLocation != null && !fileLocation.isEmpty() && new File(fileLocation).exists()) {
                        File file = new File(fileLocation);
                        if (file.exists()) {
                            GalleryItem galleryItem = new GalleryItem();

                            String attachmentType = cursor.getString(2);

                            String date = cursor.getString(8);
                            String time = cursor.getString(12);
                            String[] attchDate = date.split("\\/");//mm/dd/yyyy
                            String[] attchTime = time.split("\\:");//Hours:Min:ss

                            int month = Integer.parseInt(attchDate[0]);
                            int day = Integer.parseInt(attchDate[1]);
                            int year = Integer.parseInt(attchDate[2]);

                            int hour = Integer.parseInt(attchTime[0]);
                            int mins = Integer.parseInt(attchTime[1]);

                            int secs = 0;

                            if (attchTime.length > 2) {
                                secs = Integer.parseInt(attchTime[2]);
                            }

                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, hour);
                            cal.set(Calendar.MINUTE, mins);
                            cal.set(Calendar.SECOND, secs);
                            cal.set(Calendar.MILLISECOND, 0);
                            cal.set(Calendar.MONTH, month - 1);
                            cal.set(Calendar.DAY_OF_MONTH, day);
                            cal.set(Calendar.YEAR, year);
                            String dateTime = cal.getTimeInMillis() + "";

                            galleryItem.setFileLocation(fileLocation);
                            galleryItem.setLatitude(cursor.getDouble(4));
                            galleryItem.setLongitude(cursor.getDouble(5));
                            galleryItem.setTxtNote(cursor.getString(6));
                            galleryItem.setTxtDate(dateTime);
                            galleryItem.setSiteName(siteName);
                            galleryItem.setLocationName(locName);
                            galleryItem.setSiteID(Integer.parseInt(siteID));
                            galleryItem.setLocID(LocID);
                            galleryItem.setEventID(eventID);
                            galleryItem.setMobAppID(cursor.getInt(9));
                            galleryItem.setSetID(Integer.parseInt(cursor.getString(7)));
                            galleryItem.setAzimuth(cursor.getString(10));
                            galleryItem.setFieldParamID(cursor.getString(11));
                            galleryItem.setAttachmentType(attachmentType);

                            galleryItem.setFileLocation(fileLocation);
                            dataList.add(galleryItem);
                        }
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "getAttachmentForFieldParam:" + e.getMessage());
        }
        return dataList;
    }

    public int getFormAttachmentCount(String siteID, String eventID, String LocID, String mobID) {
        String[] fieldDataColumns = null;
        String whereClause = null;
        int count = 0;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_AttachmentType, KEY_FileLocation, KEY_Latitude,
                KEY_Longitude, KEY_Notes, KEY_SetID, KEY_Attachment_Date, KEY_MobileAppID, KEY_Azimuth, KEY_FieldParameterID, KEY_Attachment_Time};

        whereClause = " EventID=" + eventID + " and SiteID = " + siteID + " and LocationID = " + LocID
                + " and AttachmentType NOT IN ('s','S') ";//and MobileAppID = " + mobID ;

        try {

            Log.i(TAG, "get getFormAttachmentCount whereClause " + whereClause);

            Cursor cursor = database.query(DbAccess.TABLE_ATTACHMENT, fieldDataColumns,
                    whereClause, null, null, null, null);
            String dispname;

            if (cursor == null) {
                Log.i(TAG, "No Attachments to show in Gallery");
                // return dataList;
            } else {

                count = cursor.getCount();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in card attachment list:" + e.getMessage());
        }


        return count;
    }


    public List<Attachment> getAttachmentDataListForSiteFromDB(DataForSync sync, int siteID, int eventID, String LocID, int mobID) {
        List<Attachment> dataList = new ArrayList<Attachment>();
        String[] fieldDataColumns = null;
        String whereClause = null;
//		String[] whereArgs = null;
//		String orderBy = null;

        fieldDataColumns = new String[]{
                KEY_EventID, KEY_LocationID,
                KEY_AttachmentType, KEY_FileLocation, KEY_Latitude,
                KEY_Longitude, KEY_Notes, KEY_TimeTaken, KEY_CreationDate};

        if (sync == DataForSync.All) {
            whereClause = null;
//			whereArgs = null;
        } else if (sync == DataForSync.EmailNotSent) {
            whereClause = KEY_EmailSentFlag + " IS NULL";
//			whereArgs = new String[] {""+Integer.toString(0)};
        } else if (sync == DataForSync.DataNotSynced) {
//			whereClause = KEY_DataSyncFlag+" IS NULL"; //commented to show all images
//			whereArgs = new String[] {""+Integer.toString(0)};
        }
        // TODO: 09-Nov-15  filter by eventID and LocationID Added

        if (whereClause == null) {
            // whereClause = "EventID in (select EventID from d_Event where SiteID = " + siteID + ")";
            whereClause = " EventID=" + eventID + " and SiteID = " + siteID + " and LocationID = " + LocID;//+ " and MobileAppID = " + mobID

            // whereClause = " EventID=" + eventID + " and SiteID = " + siteID + " and LocationID = " + LocID+ " and LocationID = " + LocID;
        } else {
            // whereClause = whereClause + " and EventID in (select EventID from d_Event where SiteID = " + siteID + ")";
            whereClause = whereClause + " and EventID=" + eventID + " and SiteID = " + siteID + " and LocationID = " + LocID;//+ " and MobileAppID = " + mobID
        }

        Cursor cursor = database.query(DbAccess.TABLE_ATTACHMENT, fieldDataColumns,
                whereClause, null, null, null, null);


        if (cursor == null) {
            Log.i(TAG, "No data for getFieldDataListFromDB4");
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Attachment data = cursorToAttach(cursor);
            if (data != null) {
                dataList.add(data);
            }
            cursor.moveToNext();
        }

        return dataList;
    }

    private Attachment cursorToAttach(Cursor cursor) {
        Attachment data = null;
        DEvent event = null;
        boolean FileAvailable = false;
        String fileLocation = cursor.getString(3);
        if (fileLocation != null && !fileLocation.isEmpty()) {
            File fl = new File(fileLocation);// TODO: 04-Jan-16 Check file is Present in External or Internal Storage
            if (fl.exists()) {
                FileAvailable = true;
            }
        }

        if (FileAvailable) {
            event = new DEvent();
            data = new Attachment();
            try {
                event.setEventId(cursor.getInt(0));
                data.setEvent(event);
                String attachType = cursor.getString(2);
                data.setLocationId(cursor.getString(1));
                if (attachType.equalsIgnoreCase("S")) {
//				data.setAttachmentType(attachType);
                    return null;
                } else {
                    data.setAttachmentType(attachType);
                }
                data.setFileLocation(fileLocation);
                data.setLatitude(cursor.getDouble(4));
                data.setLongitude(cursor.getDouble(5));
                data.setNotes(cursor.getString(6));
                data.setTimeTaken(cursor.getLong(7));
                data.setCreationDate(cursor.getLong(8));
                data.setSetId(cursor.getInt(9));
                data.setSiteId(cursor.getInt(10));
                data.setMobileAppId(cursor.getInt(11));
                data.setUserId(cursor.getInt(12));
//			data.setEmailSentFlag(cursor.getString(8));
//			data.setDataSyncFlag(cursor.getString(9));
            } catch (Exception e) {
                Log.i(TAG, "cursorToAttach exception:" + e.getLocalizedMessage());
            }
        }


        return data;
    }

    public int setImageSyncFlag(SyncType type, Attachment attachment) {
        int ret = 0;
        ContentValues values = new ContentValues();
        if (type == SyncType.data) {
            values.put(KEY_DataSyncFlag, "1");
        } else if (type == SyncType.email) {
            values.put(KEY_EmailSentFlag, "1");
        }
        String whereClause = "EventID=? AND LocationID=? AND FileLocation=?";
        String[] whereArgs = new String[]{"" + attachment.getEvent().getEventId(),
                "" + attachment.getLocationId(),
                "" + attachment.getFileLocation()};
        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "setImageSyncFlag() : " + "Ret value for updateValue = " + ret);
        return ret;

    }

    public int updateEventID(int clientEventID, int serverEventID) {

        Log.i(TAG, "updateEventID() IN time:" + System.currentTimeMillis());
        Log.i(TAG, "updateEventID() localEventID:" + clientEventID + "->ServerEventID:" + serverEventID);


        int ret = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_EventID, serverEventID);
        String whereClause = "EventID = ?";
        String[] whereArgs = new String[]{"" + clientEventID};
        try {
            ret = database.update(DbAccess.TABLE_ATTACHMENT, values, whereClause, whereArgs);
            Log.i(TAG, "updateEventID() result:" + ret);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.i(TAG, "updateEventID() Error:" + e.getMessage());
            }


        }
        Log.i(TAG, "updateEventID() OUT time:" + System.currentTimeMillis());

        return ret;
    }

    public boolean deleteAttachments(List<String> path) {
        String query = null;
        for (int i = 0; i < path.size(); i++) {
            query = "delete from " + DbAccess.TABLE_ATTACHMENT + " where FileLocation = '" + path.get(i) + "'";
            try {
                database.execSQL(query);
            } catch (Exception e) {
                System.out.println("deleteAttachments " + e.getLocalizedMessage());
            }
        }
        return true;
    }

    public boolean deleteImage(String path) {
        String query = null;
        if (path != null) {
            query = "delete from " + DbAccess.TABLE_ATTACHMENT + " where FileLocation = '" + path + "'";
            try {
                database.execSQL(query);
            } catch (Exception e) {
                System.out.println("deleteAttachments error:" + e.getLocalizedMessage());
                return false;
            }
        }

        return true;
    }

    public int deleteAttachment(int eventID, String attachmentType) {
        int ret = 0;
        String whereClause = KEY_EventID + " = ? and " + KEY_AttachmentType + " =? ";
        String[] whereArgs = new String[]{"" + eventID, attachmentType};
        try {
            ret = database.delete(DbAccess.TABLE_ATTACHMENT, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int deleteAttachment() {
        int ret = 0;
        String whereClause = null;
        String[] whereArgs = null;
        try {
            ret = database.delete(DbAccess.TABLE_ATTACHMENT, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int deleteAttachmentsForEvent(int eventID) {
        int ret = 0;
        String whereClause = "EventID =?";
        String[] whereArgs = new String[]{"" + eventID};
        try {
            ret = database.delete(DbAccess.TABLE_ATTACHMENT, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int deleteAttachmentHavingBlankFilePath() {
        int ret = 0;
        String query = "delete  FROM d_Attachment where FileLocation IS NULL ";
        try {
            database.execSQL(query);
            ret = 1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteAttachmentHavingBlankFilePath() error:" + e.getLocalizedMessage());
        }
        return ret;
    }

}
