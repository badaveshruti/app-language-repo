package qnopy.com.qnopyandroid.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.util.Util
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventDbSource @Inject constructor(private val context: Context) {
    private var database: SQLiteDatabase?

    init {
        database = DbAccess.getInstance(context).database
        if (database == null) {
            DbAccess.getInstance(context).open()
            database = DbAccess.getInstance(context).database
        }
    }

    suspend fun getAllEvents(siteId: String?): Triple<HashMap<String, ArrayList<EventData>>,
            ArrayList<EventData>, HashMap<String, Int>> {

        val mapEvents = HashMap<String, ArrayList<EventData>>() //events by date
        val listAllEvents = ArrayList<EventData>() //for all events
        val mapEventsForShowingDots =
            HashMap<String, Int>() //events date with their size for showing dots on calendar dates

        var query =
            "select distinct a.EventID, IFNuLL(e.Date, a.EventStartDateTime) as sortingDate, " +
                    "IFNuLL(e.modifiedBy,e.UserID) as updatedBy,(case when (a.EventName is '' " +
                    "or a.EventName is Null) then sm.formName else a.EventName end) " +
                    "as MobileAppName, a.EventStartDateTime as eventDate, \n" +
                    "IFNuLL(a.EventEndDateTime,0) as EndDate, b.SiteName, b.SiteID,a.EventStatus, " +
                    "a.MobileAppID, count(distinct d.LocationID), e.Date, c.ExtField4, a.UserID, " +
                    "strftime('%Y-%m-%d', EventStartDateTime, 'unixepoch') as strEventDate," +
                    " sm.formName, a.eventUserName " +
                    "from d_Event a inner join s_Site b on a.SiteID = b.SiteID " +
                    " inner join " +
                    "s_SiteMobileApp c on a.MobileAppID=c.roll_into_app_id " +
                    "inner join s_Location d on a.SiteID=d.SiteID inner join FormSites sm " +
                    "on c.roll_into_app_id = sm.formId and a.SiteID = sm.siteId LEFT OUTER join (select e.EventID, " +
                    "e.UserID, e.modifiedBy, MAX(IFNuLL(distinct e.CreationDate,0)) as Date " +
                    "from d_FieldData e group by e.EventID) e on a.EventID = e.EventID where a.EventStatus != 5 "

        if (siteId != null && siteId.isNotEmpty() && siteId != "-1") query =
            "$query and a.SiteID = $siteId"
        query = "$query group by a.EventID,a.SiteID,a.MobileAppID"

        val c = database!!.rawQuery(query, null)
        if (c != null && c.moveToFirst()) {
            try {
                do {
                    val event = EventData()
                    val eventID = c.getInt(0)
                    val sortedDate = c.getLong(1)
                    val updatedBy = c.getString(2)
                    val eventName = c.getString(3)
                    val startDate = c.getLong(4)
                    val endDate = c.getLong(5)
                    val siteName = c.getString(6)
                    val siteID = c.getInt(7)
                    val eventStatus = c.getInt(8)
                    val mobAppID = c.getInt(9)
                    val locationCount = c.getInt(10)
                    val dateCreation = c.getString(11) //not sure which date it is
                    val extField4 = c.getString(12)
                    val userId = c.getInt(13)
                    var eventDateFormatted = c.getString(14)
                    @SuppressLint("Range") val mobAppName = c.getString(15)
                    val eventUserName = c.getString(16)

//                    if (eventDateFormatted == null)
                    eventDateFormatted = Util.getyyyyMMddFromMilliSeconds(startDate.toString() + "")
                    event.eventID = eventID
                    event.siteID = siteID
                    event.mobAppID = mobAppID
                    event.startDate = startDate
                    event.endDate = endDate
                    event.status = eventStatus
                    event.siteName = siteName
                    event.mobAppName = mobAppName
                    event.locationCount = locationCount
                    event.eventName = eventName
                    event.extField4 = extField4
                    event.userId = userId
                    event.updatedBy = updatedBy //this userId could be modifiedBy or userId
                    event.sortedDate = sortedDate
                    event.eventDateFormatted = eventDateFormatted
                    event.modificationDate = if (sortedDate != 0L) sortedDate else startDate
                    event.eventUserName = eventUserName

                    listAllEvents.add(event)

                    if (mapEvents.containsKey(eventDateFormatted)) {
                        mapEvents[eventDateFormatted]!!.add(event)

                        if (mapEventsForShowingDots.containsKey(eventDateFormatted)) {
                            mapEventsForShowingDots[eventDateFormatted] =
                                mapEvents[eventDateFormatted]!!.size
                        }
                    } else {
                        val alist = ArrayList<EventData>()
                        alist.add(event)
                        mapEvents[eventDateFormatted] = alist
                        mapEventsForShowingDots[eventDateFormatted] = alist.size
                    }
                } while (c.moveToNext())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("EventDbSource", "Error in getEvents():" + e.message)
            } finally {
                c.close()
            }
        }
        return Triple(mapEvents, listAllEvents, mapEventsForShowingDots)
    }
}