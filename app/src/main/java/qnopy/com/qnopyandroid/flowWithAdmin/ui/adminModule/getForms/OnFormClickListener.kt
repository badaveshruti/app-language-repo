package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms

import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form

interface OnFormClickListener {
    public fun OnFormPreviewClicked(form: Form, pos:Int)
    public fun OnFormTitleClicked(form: Form, pos:Int)
    public fun OnFormAssignClicked(form: Form, pos:Int)
}