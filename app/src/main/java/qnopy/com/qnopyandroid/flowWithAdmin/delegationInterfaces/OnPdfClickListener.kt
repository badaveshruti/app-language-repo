package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.model.PdfLog

interface OnPdfClickListener {
    fun onPdfClicked(pdfLog:PdfLog, pos:Int)
    fun onPdfDownloadClicked(pdfLog:PdfLog, pos:Int)
}