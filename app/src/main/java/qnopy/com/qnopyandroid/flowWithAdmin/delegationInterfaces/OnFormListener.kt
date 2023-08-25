package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp

interface OnFormListener {
    fun onShowPreviewClicked(form: Form)
    fun onFormTitleClicked(form: Form)
    fun onFormAssignClicked(form: Form)
}