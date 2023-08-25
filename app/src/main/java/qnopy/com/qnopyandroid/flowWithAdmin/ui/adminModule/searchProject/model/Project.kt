package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.searchProject.model

import com.google.gson.annotations.SerializedName

data class Project(

    @field:SerializedName("zipCode")
    val zipCode: String? = null,

    @field:SerializedName("clientAddress1")
    val clientAddress1: Any? = null,

    @field:SerializedName("notes")
    val notes: Any? = null,

    @field:SerializedName("clientAddress2")
    val clientAddress2: Any? = null,

    @field:SerializedName("parentSiteId")
    val parentSiteId: Any? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("endDate")
    val endDate: Long? = null,

    @field:SerializedName("clientName")
    val clientName: Any? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("siteGuid")
    val siteGuid: Any? = null,

    @field:SerializedName("siteName")
    val siteName: String? = null,

    @field:SerializedName("siteNumber")
    val siteNumber: String? = null,

    @field:SerializedName("modifiedBy")
    val modifiedBy: Int? = null,

    @field:SerializedName("state")
    val state: Any? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null,

    @field:SerializedName("siteType")
    val siteType: Any? = null,

    @field:SerializedName("geoTrackerId")
    val geoTrackerId: Int? = null,

    @field:SerializedName("address2")
    val address2: String? = null,

    @field:SerializedName("address1")
    val address1: String? = null,

    @field:SerializedName("creationDate")
    val creationDate: Long? = null,

    @field:SerializedName("extField3")
    val extField3: Any? = null,

    @field:SerializedName("epaid")
    val epaid: Any? = null,

    @field:SerializedName("extField1")
    val extField1: Any? = null,

    @field:SerializedName("extField2")
    val extField2: Any? = null,

    @field:SerializedName("createdBy")
    val createdBy: String? = null,

    @field:SerializedName("modifiedDate")
    val modifiedDate: Long? = null,

    @field:SerializedName("siteId")
    val siteId: Int? = null,

    @field:SerializedName("startDate")
    val startDate: Long? = null,

    @field:SerializedName("status")
    val status: String? = null,

    var position: Int = 0,

    var isNotAssigned: Boolean = true
)