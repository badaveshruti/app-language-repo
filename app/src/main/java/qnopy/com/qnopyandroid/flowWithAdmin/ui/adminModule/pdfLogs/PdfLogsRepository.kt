package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLog
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLogsResponse
import javax.inject.Inject

class PdfLogsRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    fun fetchPdfLogs(pdfLogsRequest: PdfLogsRequest): Flow<PdfLogsResponse> = flow {
        emit(apiServiceImpl.fetchPdfLogs(pdfLogsRequest))
    }.flowOn(Dispatchers.IO)

    fun downloadFile(pdfLog: PdfLog):Flow<ResponseBody> = flow {
            pdfLog.fileKeyEncode?.let {
                emit(apiServiceImpl.downloadPdf(pdfLog.fileKeyEncode))
            }
    }.flowOn(Dispatchers.IO)
}