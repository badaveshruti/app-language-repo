package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model

data class EquipmentOrderRequest(
    val amount: Double,
    val dateEquipmentNeeded: Long,
    val dateRentalStarts: Long,
    val daysNeeded: Int,
    val eventId: Int,
    val fileName: String,
    val fileNameKey: String,
    val orderContact: String,
    val orderDate: Long,
    val orderId: Int,
    val orderPhone: String,
    val orderedBy: String,
    val orderedItems: String,
    val orderemail: String,
    val pO: String,
    val pineComments: String,
    val pineContact: String,
    val pineLocation: String,
    val pineOrder: String,
    val projectNumber: String,
    val returnTracking: String,
    val shipTo: String,
    val siteId: Int,
    val siteName: String,
    val status: String,
    val tracking: String,
    val vendorId: Int,
    val vendorSiteId: Int
)