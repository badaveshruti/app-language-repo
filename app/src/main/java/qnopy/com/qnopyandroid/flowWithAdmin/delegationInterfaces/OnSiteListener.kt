package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp

interface OnSiteListener {
    fun onAdminSettingsClicked(site: Site)
    fun onSiteFavoriteClicked(site: Site, isFavorite: Boolean, pos: Int)
    fun onNewFormTileClicked(site: Site, form: SSiteMobileApp)
    fun onFormTileClick(site: Site, form: SSiteMobileApp, formPosition: Int)
}