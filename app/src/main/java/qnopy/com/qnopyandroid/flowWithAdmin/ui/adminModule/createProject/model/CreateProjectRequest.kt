package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.createProject.model

data class CreateProjectRequest(
    var address1: String = "",
    var address2: String = "",
    var city: String = "",
    var client: String = "",
    var companyId: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var project: String = "",
    var projectNumber: String = "",
    var siteId: Int = 0,
    var siteName: String = "",
    var siteNumber: String = "",
    var state: String = "",
    var status: String = "",
    var zip: String = ""
)