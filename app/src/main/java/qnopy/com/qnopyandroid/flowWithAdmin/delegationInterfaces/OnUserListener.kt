package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.requestmodel.SUser

interface OnUserListener {
    fun onUserBatchClicked(user:  SUser, pos:Int)
    fun onUserNameClicked(user:   SUser, pos:Int)
    fun onUserDeleteClicked(user: SUser, pos:Int)
    fun onUserAddClicked(user: SUser, pos:Int)
}