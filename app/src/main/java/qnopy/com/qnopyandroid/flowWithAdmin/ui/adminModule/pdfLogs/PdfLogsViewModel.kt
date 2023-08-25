package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLog
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsRequest
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import javax.inject.Inject

@HiltViewModel
class PdfLogsViewModel @Inject constructor(private val pdfLogsRepository: PdfLogsRepository) :
    ViewModel() {

    private val _fetchPdfLogsSF = MutableSharedFlow<ApiState>()
    val fetchPdfLogsSF = _fetchPdfLogsSF.asSharedFlow()

    private val _showPdfState = MutableSharedFlow<ApiState>()
    val showPdfState = _showPdfState.asSharedFlow()

    var vPdfLog: PdfLog = PdfLog()
    var vPdfDownloadingPos = 0
    var vOpenAfterDownload = false

    val vPdfList: MutableList<PdfLog> = mutableListOf()

    fun fetchPdfLogs(pdfLogsRequest: PdfLogsRequest) =
        viewModelScope.launch(exceptionHandler) {
            _fetchPdfLogsSF.emit(ApiState.Loading)
            pdfLogsRepository.fetchPdfLogs(pdfLogsRequest).catch {
                _fetchPdfLogsSF.emit(ApiState.Failure(it))
            }.collect {
                _fetchPdfLogsSF.emit(ApiState.Success(it))
            }
        }

//    fun fetchPdfLogs(pdfLogsRequest: PdfLogsRequest) {
//        try {
//            runCatching {
//                viewModelScope.launch(Dispatchers.IO+exceptionHandler) {
//                    _fetchPdfLogsSF.value = ApiState.Loading
//                    pdfLogsRepository.fetchPdfLogs(pdfLogsRequest).catch {
//                        _fetchPdfLogsSF.value = ApiState.Failure(it)
//                    }.collect {
//                        _fetchPdfLogsSF.value = ApiState.Success(it)
//                    }
//                }
//            }
//        }catch (e:Exception){
//            Log.e("TAG", "fetchPdfLogs: "+e )
//        }
//    }

    fun downloadPdf(pdfLog: PdfLog, pos: Int, openAfterDownload: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            _showPdfState.emit(ApiState.Loading)
            vPdfLog = pdfLog
            vPdfDownloadingPos = pos
            vOpenAfterDownload = openAfterDownload

            pdfLogsRepository.downloadFile(pdfLog).catch { e ->
                _showPdfState.emit(ApiState.Failure(e))
            }.collect { responseBody ->
                _showPdfState.emit(ApiState.Success(responseBody))
            }
        }

    private val exceptionHandler = CoroutineExceptionHandler() { _, ex ->
        Log.e("CoroutineScope", "Caught ${Log.getStackTraceString(ex)}")
    }
}