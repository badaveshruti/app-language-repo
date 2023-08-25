package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.siteCompoundView

import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource
import javax.inject.Inject

class SiteItemRepository @Inject constructor(private val appDataSource: SiteMobileAppDataSource) {
    fun getAllForms(siteId:Int, companyId:Int) = appDataSource.getAllApps(siteId, companyId)
}