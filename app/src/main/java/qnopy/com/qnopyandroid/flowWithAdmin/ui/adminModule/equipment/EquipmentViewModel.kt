package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model.EquipmentOrderRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class EquipmentViewModel @Inject constructor(private val repository: EquipmentsRepository) :
    ViewModel() {

    private val _equipmentListFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val equipmentListFlow: StateFlow<ApiState> = _equipmentListFlow

    private val _saveEquipmentOrderFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Empty)
    val saveEquipmentOrderFlow: StateFlow<ApiState> = _saveEquipmentOrderFlow

    fun fetchEquipmentOrdersList(vendorId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _equipmentListFlow.value = ApiState.Loading
            repository.fetchEquipmentOrdersList(vendorId).catch {
                _equipmentListFlow.value = ApiState.Failure(it)
            }.collect {
                _equipmentListFlow.value = ApiState.Success(it)
            }
        }

    fun saveEquipmentOrder(request: Order) =
        viewModelScope.launch(Dispatchers.IO) {
            _saveEquipmentOrderFlow.value = ApiState.Loading
            repository.saveEquipmentOrders(request).catch {
                _saveEquipmentOrderFlow.value = ApiState.Failure(it)
            }.collect {
                _saveEquipmentOrderFlow.value = ApiState.Success(it)
            }
        }
}