package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms

import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import javax.inject.Inject

class FormsRepository @Inject constructor(
    private val appData: SiteMobileAppDataSource,
    private val apiServiceImpl: ApiServiceImpl
) {
    fun getAllForms(siteId: Int): List<SSiteMobileApp> =
        appData.getAllAppsV16(siteId)
}