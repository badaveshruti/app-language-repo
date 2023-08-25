package qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces

import qnopy.com.qnopyandroid.clientmodel.FileFolderItem

interface OnFolderNavigation {
    fun navigate(fileFolderItem: FileFolderItem, position:Int)
}