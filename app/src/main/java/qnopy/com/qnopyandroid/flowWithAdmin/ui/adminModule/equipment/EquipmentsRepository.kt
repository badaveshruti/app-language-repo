package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentOrdersListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model.EquipmentOrderRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model.SaveEquipmentsOrderResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListResponse
import javax.inject.Inject

class EquipmentsRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    fun fetchEquipmentOrdersList(vendorId: String): Flow<EquipmentOrdersListResponse> = flow {
        emit(apiServiceImpl.fetchEquipmentOrdersList(vendorId))
    }.flowOn(Dispatchers.IO)

    fun saveEquipmentOrders(request: Order): Flow<SaveEquipmentsOrderResponse> =
        flow {
            emit(apiServiceImpl.saveEquipmentOrder(request))
        }.flowOn(Dispatchers.IO)
}