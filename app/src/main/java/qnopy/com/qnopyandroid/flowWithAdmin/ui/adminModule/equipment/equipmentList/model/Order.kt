package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model

data class Order(
    var createdBy: String? = "",
    var orderedBy: String? = "",
    var creationDate: Long? = 0,
    var dateEquipmentNeeded: Long? = 0,
    var dateRentalStarts: Long? = 0,
    var daysNeeded: Int? = 0,
    var eventId: Int? = 0,
    var invoiceTotal: Double? = 0.0,
    var itemNeeded: String? = "",
    var orderContact: String? = "",
    var orderEmail: String? = "",
    var orderId: Int? = 0,
    var orderPhone: String? = "",
    var orderStatus: String? = "",
    var poNumber: String? = "",
    var projectName: String? = "",
    var projectNumber: String? = "",
    var returnTracking: String? = "",
    var shipTo: String? = "",
    var shipmentTracking: String? = "",
    var siteId: Int? = 0,
    var vendorContact: String? = "",
    var vendorId: Int? = 0,
    var vendorInvoice: String? = "",
    var vendorInvoiceKey: String? = "",
    var vendorLocation: String? = "",
    var vendorNotes: String? = "",
    var vendorOrderId: String? = "",
    var vendorSiteId: Int? = 0
) : java.io.Serializable {
    var value: String? = ""
}