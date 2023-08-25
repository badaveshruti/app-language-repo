package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.clientmodel.Location
import javax.inject.Inject

@HiltViewModel
class LocationsFragmentViewModel @Inject constructor(private val locationRepo: LocationRepository) :
    ViewModel() {

    fun getAllLocationForSite(siteId: Int): HashMap<String, ArrayList<Location>> {
        val map = locationRepo.getAllLocationForSite(siteId)

        if (map.isNotEmpty()) {
            if (!map.containsKey(GlobalStrings.FORM_DEFAULT))
                map[GlobalStrings.FORM_DEFAULT] = arrayListOf()
            if (!map.containsKey(GlobalStrings.NON_FORM_DEFAULT))
                map[GlobalStrings.NON_FORM_DEFAULT] = arrayListOf()
        }

        return map
    }

    fun deleteLocation(locationId: String) = locationRepo.deleteLocation(locationId)
}