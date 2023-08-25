package qnopy.com.qnopyandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.TaskFromSite;
import qnopy.com.qnopyandroid.clientmodel.TaskView;
import qnopy.com.qnopyandroid.requestmodel.TaskAttributes;
import qnopy.com.qnopyandroid.requestmodel.WorkOrderTask;
import qnopy.com.qnopyandroid.util.Util;

/**
 * Created by shantanu on 6/14/17.
 */

public class WorkOrderTaskDataSource {
    private static final String TAG = "WorkOrderTask ";

    String KEY_LocationID = "LocationID";
    String KEY_LocationName = "Location";
    String KEY_TaskID = "TaskID";
    String KEY_TaskDesc = "TaskDesc";
    String KEY_WorkOrderID = "wo_id";
    String KEY_WorkOrderTaskID = "wo_task_id";
    String KEY_PlanName = "PlanName";
    String KEY_TaskName = "TaskName";
    String KEY_CocFlag = "CocFlag";
    String KEY_UserID = "UserID";
    String KEY_PLANSTARTDATE = "planStartDate";
    String KEY_PLANENDDATE = "planEndDate";
    String KEY_Latitude = "Latitude";
    String KEY_Longitude = "Longitude";
    String KEY_LocInstruction = "loc_instruction";
    String KEY_STATUS = "status";
    String KEY_WO_STARTDATE = "wo_planStartDate";
    String KEY_WO_ENDDATE = "wo_planEndDate";
    String KEY_FORMID = "parentAppID";


    Context mContext;
    public SQLiteDatabase database;

    public WorkOrderTaskDataSource(Context context) {
        mContext = context;
        database = DbAccess.getInstance(context).database;
        if (database == null) {
            DbAccess.getInstance(context).open();
            database = DbAccess.getInstance(context).database;

        }
    }

//    String workorderTable = "work_order_task";


    public void truncateWorkOrderTask() {

        int ret = 0;

        if (database == null) {
            database = DbAccess.getInstance(mContext).database;
        }

        try {
            database.beginTransaction();

            try {
                ret = database.delete(DbAccess.TABLE_WORK_ORDER_TASK_NEW, null, null);
                Log.i(TAG, "deleted table:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Caught for Table name=" + ret);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            database = DbAccess.getInstance(mContext).database;
        } finally {
            if (database != null) {
                database.endTransaction();
            }
        }

    }


    public int insertNewWorkOrderDataList(WorkOrderTask workOrderTask) {
        int ret = 0;

        database.beginTransaction();
        //   WorkOrderTask workOrderTask;

        try {

            //    for (int i = 0; i < dataList.size(); i++) {
            ContentValues values = new ContentValues();
            //  workOrderTask = dataList.get(i);
            int locationID = workOrderTask.getLocationId();
            String planname = workOrderTask.getPlanName();
            int cocflag = workOrderTask.getCocFlag();
            String locationName = workOrderTask.getLocationName();
            int taskid = workOrderTask.getTaskId();
            String taskdesc = workOrderTask.getTaskDescription();
            int workorderid = workOrderTask.getWorkOrderId();
            String latitude = String.valueOf(workOrderTask.getLatitude());
            String longitude = String.valueOf(workOrderTask.getLongitude());
            int workOrderTaskid = workOrderTask.getWorkOrderTaskId();
            String taskName = workOrderTask.getTaskName();
            String locInstruction = workOrderTask.getInstruction();
            String planStartDate = workOrderTask.getPlanStartDate();
            String planEndDate = workOrderTask.getPlanEndDate();
            String status = workOrderTask.getStatus();
            int userID = workOrderTask.getUserId();
//            if (userID < 1) {
//                userID = Integer.parseInt(Util.getSharedPreferencesProperty(mContext, GlobalStrings.USERID));
//            }
            String wo_PlanStartDate = workOrderTask.getWoPlanStartDate();
            String wo_PlanEndDate = workOrderTask.getWoPlanEndDate();

            String formid = workOrderTask.getFormId();

            if (formid!=null && formid.contains(",")) {
                String[] result = formid.split(",");
                for (String s : result) {
                    values.put(KEY_FORMID, s);
                    values.put(KEY_PlanName, planname);
                    values.put(KEY_CocFlag, cocflag);
                    values.put(KEY_LocationName, locationName);
                    values.put(KEY_TaskID, taskid);
                    values.put(KEY_TaskDesc, taskdesc);
                    values.put(KEY_WorkOrderID, workorderid);
                    values.put(KEY_Latitude, latitude);
                    values.put(KEY_Longitude, longitude);
                    values.put(KEY_WorkOrderTaskID, workOrderTaskid);
                    values.put(KEY_TaskName, taskName);
                    values.put(KEY_LocInstruction, locInstruction);
                    values.put(KEY_PLANSTARTDATE, planStartDate);
                    values.put(KEY_PLANENDDATE, planEndDate);
                    values.put(KEY_STATUS, status);
                    values.put(KEY_LocationID, locationID);
                    values.put(KEY_UserID, userID);
                    values.put(KEY_WO_STARTDATE, wo_PlanStartDate);
                    values.put(KEY_WO_ENDDATE, wo_PlanEndDate);
                    ret = (int) database.insert(DbAccess.TABLE_WORK_ORDER_TASK_NEW, null, values);
                    // Log.i(TAG, "Insert new Data List Return Count:" + ret);

                }
            } else {
                values.put(KEY_FORMID, formid);
                values.put(KEY_PlanName, planname);
                values.put(KEY_CocFlag, cocflag);
                values.put(KEY_LocationName, locationName);
                values.put(KEY_TaskID, taskid);
                values.put(KEY_TaskDesc, taskdesc);
                values.put(KEY_WorkOrderID, workorderid);
                values.put(KEY_Latitude, latitude);
                values.put(KEY_Longitude, longitude);
                values.put(KEY_WorkOrderTaskID, workOrderTaskid);
                values.put(KEY_TaskName, taskName);
                values.put(KEY_LocInstruction, locInstruction);
                values.put(KEY_PLANSTARTDATE, planStartDate);
                values.put(KEY_PLANENDDATE, planEndDate);
                values.put(KEY_STATUS, status);
                values.put(KEY_LocationID, locationID);
                values.put(KEY_UserID, userID);
                ret = (int) database.insert(DbAccess.TABLE_WORK_ORDER_TASK_NEW, null, values);
                // Log.i(TAG, "Insert new Data List Return Count:" + ret);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage() + ret);
        } finally {
            database.endTransaction();
        }
        return ret;
    }


    public ArrayList<TaskFromSite> getPlanDatafromDB() {

        Cursor c = null;
        ArrayList<TaskFromSite> list = new ArrayList<>();
        TaskFromSite tasks = new TaskFromSite();

        String query = "select distinct a.PlanName, a.wo_id , count(distinct TaskID) as count, " +
                "c.SiteID, s.SiteName, a.parentAppID, sm.display_name_roll_into_app ,a.wo_PlanStartDate," +
                " a.wo_PlanEndDate from s_work_order_task a, s_Location c , " +
                "s_Site s,s_SiteMobileApp sm where a.LocationID = c.LocationID  " +
                "and c.SiteID = s.SiteID and s.SiteID = sm.SiteID and a.parentAppID = sm.roll_into_app_id group by wo_id";

        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    tasks = new TaskFromSite();
                    tasks.setPlanname(c.getString(0));
                    tasks.setWorkorderid(c.getString(1));
                    tasks.setTaskcount(c.getString(2));
                    tasks.setSiteid(c.getString(3));
                    tasks.setSitename(c.getString(4));
                    tasks.setParentappid(c.getString(5));
                    tasks.setFormname(c.getString(6));
                    tasks.setW0_planstartdate(c.getString(7));
                    tasks.setWo_planendDate(c.getString(8));
                    list.add(tasks);
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return list;
    }

    public ArrayList<TaskFromSite> getPlanDataforLocfromDB(int siteid,int parentappid) {

        Cursor c = null;
        ArrayList<TaskFromSite> list = new ArrayList<>();
        TaskFromSite tasks = new TaskFromSite();

        String query = "select distinct a.PlanName, a.wo_id , count(distinct TaskID) as count, " +
                "c.SiteID, s.SiteName, a.parentAppID, sm.display_name_roll_into_app " +
                ",a.wo_PlanStartDate, a.wo_PlanEndDate from s_work_order_task a," +
                " s_Location c , s_Site s,s_SiteMobileApp sm where a.LocationID = c.LocationID" +
                " and c.SiteID = s.SiteID and s.SiteID = sm.SiteID and a.parentAppID = sm.roll_into_app_id" +
                " and c.SiteID="+siteid+" and a.parentAppID="+parentappid+" group by wo_id";

        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    tasks = new TaskFromSite();
                    tasks.setPlanname(c.getString(0));
                    tasks.setWorkorderid(c.getString(1));
                    tasks.setTaskcount(c.getString(2));
                    tasks.setSiteid(c.getString(3));
                    tasks.setSitename(c.getString(4));
                    tasks.setParentappid(c.getString(5));
                    tasks.setFormname(c.getString(6));
                    tasks.setW0_planstartdate(c.getString(7));
                    tasks.setWo_planendDate(c.getString(8));
                    list.add(tasks);
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return list;
    }

    public ArrayList<TaskFromSite> getPlanDataforFormfromDB(int siteId) {

        Cursor c = null;
        ArrayList<TaskFromSite> list = new ArrayList<>();
        TaskFromSite tasks = new TaskFromSite();

        String query = "select distinct a.PlanName, a.wo_id , count(distinct TaskID) as count, c.SiteID, s.SiteName, a.parentAppID, sm.display_name_roll_into_app ," +
                "a.wo_PlanStartDate, a.wo_PlanEndDate from s_work_order_task a, s_Location c ," +
                " s_Site s,s_SiteMobileApp sm where a.LocationID = c.LocationID and c.SiteID = s.SiteID" +
                " and s.SiteID = sm.SiteID and a.parentAppID = sm.roll_into_app_id and c.SiteID="+siteId +
                " group by wo_id";
        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    tasks = new TaskFromSite();
                    tasks.setPlanname(c.getString(0));
                    tasks.setWorkorderid(c.getString(1));
                    tasks.setTaskcount(c.getString(2));
                    tasks.setSiteid(c.getString(3));
                    tasks.setSitename(c.getString(4));
                    tasks.setParentappid(c.getString(5));
                    tasks.setFormname(c.getString(6));
                    tasks.setW0_planstartdate(c.getString(7));
                    tasks.setWo_planendDate(c.getString(8));
                    list.add(tasks);
                } while (c.moveToNext());
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return list;
    }


    public List<WorkOrderTask> getTaskNameList(String locationID, int userID) {

        Cursor c = null;
        List<WorkOrderTask> tasklist = new ArrayList<>();
        WorkOrderTask worktask = new WorkOrderTask();

        String query = "select distinct TaskName from " + DbAccess.TABLE_WORK_ORDER_TASK_NEW + " where LocationID=" + locationID;

        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    worktask = new WorkOrderTask();
                    worktask.setTaskName(c.getString(0));
                    tasklist.add(worktask);
                } while (c.moveToNext());
                c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getTaskNameList() error:" + e.getMessage());
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return tasklist;
    }

    public ArrayList<TaskView> getTaskListFromDB(int woid) {
        Cursor c = null;
        ArrayList<TaskView> tasklist = new ArrayList<>();
        TaskView task = new TaskView();


/*
        String query = "select distinct TaskName from work_order_task where LocationID=" + locationID + " and UserID=" + userID;
*/
        String query = "select distinct a.TaskName,a.Location ,a.LocationID, a.PlanStartDate, a.PlanEndDate, a.Status,a.wo_task_id  from s_work_order_task a, s_Location c where a.LocationID = c.LocationID and a.wo_id ="+woid+" group by a.wo_task_id";
       /* String query="select distinct a.TaskName,a.Location ,a.LocationID, a.PlanStartDate, a.PlanEndDate, a.Status" +
                "  from s_work_order_task a, s_Location c where a.LocationID = c.LocationID and a.wo_id ="+woid+" group by a.wo_task_id";*/
        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                do {
                    task = new TaskView();
                    task.setTaskname(c.getString(0));
                    task.setLocation(c.getString(1));
                    task.setLocationId(c.getInt(2));
                    task.setTaskstartdate(c.getString(3));
                    task.setTaskduedate(c.getString(4));
                    task.setStatus(c.getString(5));
                    task.setWoTaskID(c.getString(6));
                    tasklist.add(task);
                } while (c.moveToNext());
                c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return tasklist;
    }

    public Boolean getCocFlagfromLocationID(String locID) {

        int count = 0;
        String query = "select distinct CocFlag from "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+" where LocationID=" + locID;

        Cursor c = null;
        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                count = c.getInt(0);
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count > 0;
    }

    public boolean isTaskClosed(int eventid) {
        int cnt=0;
        WorkOrderTask wt=new WorkOrderTask();
        String query="select LocationID,Location,TaskName from "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+" where status='completed'";

        Cursor c = null;
        try {
            c = database.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                cnt =c.getCount();

                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return cnt > 0;


    }



    public int updateStatus(int wotaskid,int locid) {

        Cursor c=null;
        int ret=0;


        String query="update "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+" set Status = 'completed' where wo_task_id ="+wotaskid+" and LocationID="+locid;
        try {

            c = database.rawQuery(query, null);
            Log.i(TAG, " updateStringValue() for GPS control result count:" + c.getCount());
            if(c!=null && c.moveToFirst())
            {
                ret=c.getCount();
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return ret;
    }

    public List<WorkOrderTask> getcompletedTask() {

        List<WorkOrderTask> taskList=new ArrayList<>();
        WorkOrderTask task=new WorkOrderTask();
        Cursor c=null;
        String query="select LocationID,Location,wo_task_id,wo_id from s_work_order_task where status='completed'";

        try
        {
            c=database.rawQuery(query,null);
            if(c!=null && c.moveToFirst())
            {
                task=new WorkOrderTask();
                task.setLocationId(c.getInt(0));
                task.setLocationName(c.getString(1));
                task.setTaskId(c.getInt(2));
                task.setWorkOrderId(c.getInt(3));
                taskList.add(task);

            }
            c.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return taskList;
    }



    public List<WorkOrderTask> collectWorkOrderData() {

        List<WorkOrderTask> taskList=new ArrayList<>();
        WorkOrderTask task=new WorkOrderTask();
        Cursor c=null;
        String query="select a.LocationID,a.Location,a.TaskID,a.TaskDesc,a.wo_id,a.wo_task_id,a.PlanName,a.TaskName," +
                "a.CocFlag,a.UserID,a.Latitude,a.Longitude,a.loc_instruction,a.planStartDate,a.planEndDate,\n" +
                "a.parentAppID,a.status,a.wo_planStartDate,a.wo_planEndDate from "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+" a where status='completed'";


        try
        {
            c=database.rawQuery(query,null);
            if(c!=null && c.moveToFirst())
            {
                do{
                    task=new WorkOrderTask();
                    task.setLocationId(c.getInt(0));
                    task.setLocationName(c.getString(1));
                    task.setTaskId(c.getInt(2));
                    task.setTaskDescription(c.getString(3));
                    task.setWorkOrderId(c.getInt(4));
                    task.setWorkOrderTaskId(c.getInt(5));
                    task.setPlanName(c.getString(6));
                    task.setTaskName(c.getString(7));
                    task.setCocFlag(c.getInt(8));
                    task.setUserId(c.getInt(9));
                    task.setLatitude(Double.valueOf(c.getString(10)));
                    task.setLongitude(Double.valueOf(c.getString(11)));
                    task.setInstruction(c.getString(12));
                    task.setPlanStartDate(c.getString(13));
                    task.setPlanEndDate(c.getString(14));
                    task.setFormId(String.valueOf(c.getInt(15)));
                    task.setStatus(c.getString(16));
                    task.setWoPlanStartDate(c.getString(17));
                    task.setWoPlanEndDate(c.getString(18));
                    taskList.add(task);
                }while(c.moveToNext());
                c.close();

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return taskList;
    }


    public boolean isworkOrderAvailableToSync() {

        int count = 0;
        String query = "select count(*) from "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+ " where status='completed'";
        Cursor cursor = null;


        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getCount();
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
        return count > 0;
    }

    public boolean isDataAvailableforUser(String userID) {
        int count = 0;
        String query = "select count(*) from "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+" where UserID="+userID;
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
    public boolean isDataAvailableforSiteNUser(String userID) {
        int count = 0;
        String query = "select count(*) from "+DbAccess.TABLE_WORK_ORDER_TASK_NEW+" where UserID="+userID+" and ";
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


}
