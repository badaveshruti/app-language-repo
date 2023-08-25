package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations

import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.db.LocationDataSource
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationDataSource: LocationDataSource
) {
    fun getAllLocationForSite(siteId: Int): HashMap<String, ArrayList<Location>> =
        locationDataSource.getAllLocDefaultOrNonAdmin(siteId)

    fun deleteLocation(locationId: String) = locationDataSource.deleteLocation(locationId)

}