package qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById

data class FetchAllReportByIdResponse(
    val data: ArrayList<Data>,
    val message: String?,
    val responseCode: String,
    val success: Boolean
) {

    data class Data(
        val reportId: Int,
        val reportName: String
    )
}