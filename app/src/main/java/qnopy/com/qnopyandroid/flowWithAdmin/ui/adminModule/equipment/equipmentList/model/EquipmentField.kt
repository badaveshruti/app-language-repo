package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model

data class EquipmentField(
    var key: String,
    var options: ArrayList<Option>?,
    var title: String?,
    var type: String?,
    var isRequiredField: Boolean
) : java.io.Serializable {
    var value: String? = ""
}