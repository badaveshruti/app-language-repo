package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model

data class SaveEquipmentsOrderResponse(
    val data: Int,//gives order id
    val message: String?,
    val responseCode: String?,
    val success: Boolean
)