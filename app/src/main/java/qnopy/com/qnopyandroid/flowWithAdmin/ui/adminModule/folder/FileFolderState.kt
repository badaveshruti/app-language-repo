package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder

import qnopy.com.qnopyandroid.clientmodel.FileFolderItem

sealed class FileFolderState {
    object Empty: FileFolderState()
    object Loading: FileFolderState()
    data class Error(val message:String): FileFolderState()
    data class Sucess(val fileFolderMap: MutableMap<Char, ArrayList<FileFolderItem>>): FileFolderState()
}