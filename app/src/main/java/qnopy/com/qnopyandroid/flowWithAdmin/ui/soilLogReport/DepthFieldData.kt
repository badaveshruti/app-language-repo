package qnopy.com.qnopyandroid.flowWithAdmin.ui.soilLogReport

import java.io.Serializable

data class DepthFieldData(
    var siteId: String,
    var locId: String,
    var tabId: Int,
    var setId: Int,
    var eventId: String,
    var depthValue: Int,//this will have the rounded depth value
    var showFieldsValue: String? // this will have all show fields concatenated string values in order
) : Serializable