package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.GetNewFormsRepository
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.assignFormModel.AssignFormRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.FormListRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class GetNewFormsViewModel @Inject constructor(private val formsRepository: GetNewFormsRepository) :
    ViewModel() {

    private val _mobileFormsFlow = MutableSharedFlow<ApiState>()
    val mobileFormsFlow = _mobileFormsFlow.asSharedFlow()

    private val _assignedFormsState = MutableSharedFlow<ApiState>()
    val assignedFormsResponse = _assignedFormsState.asSharedFlow()

    private val _showPreviewState = MutableSharedFlow<ApiState>()
    val showPreviewState = _showPreviewState.asSharedFlow()

    private val _assignAFormState = MutableSharedFlow<ApiState>()
    val assignAFormState = _assignAFormState.asSharedFlow()

    var vForm = Form()

    fun getAssignedForms(request: FormListRequest) = viewModelScope.launch {
        _assignedFormsState.emit(ApiState.Loading)
        formsRepository.getAssignedForm(request).catch { e ->
            _assignedFormsState.emit(ApiState.Failure(e))
        }.collect { responseBody ->
            _assignedFormsState.emit(ApiState.Success(responseBody))
        }
    }

    fun fetchMobileForm(formListRequest: FormListRequest) = viewModelScope.launch(Dispatchers.IO) {
        _mobileFormsFlow.emit(ApiState.Loading)
        formsRepository.fetchMobileForms(formListRequest).catch {
            _mobileFormsFlow.emit(ApiState.Failure(it))
        }.collect {
            _mobileFormsFlow.emit(ApiState.Success(it))
        }
    }

    fun downloadPreviewPdf(form: Form) = viewModelScope.launch {
        _showPreviewState.emit(ApiState.Loading)
        vForm = form
        formsRepository.downloadFormPreview(form).catch { e ->
            _showPreviewState.emit(ApiState.Failure(e))
        }.collect { responseBody ->
            _showPreviewState.emit(ApiState.Success(responseBody))
        }
    }

    fun assignFormToProject(assignFormRequest: AssignFormRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            _assignAFormState.emit(ApiState.Loading)

            vForm.formId = assignFormRequest.formIdList[0]

            formsRepository.assignFormToProject(assignFormRequest).catch {
                _assignAFormState.emit(ApiState.Failure(it))
            }.collect { assignFormResponse: MetaFormsJsonResponse ->
                _assignAFormState.emit(ApiState.Success(assignFormResponse))
            }
        }
}