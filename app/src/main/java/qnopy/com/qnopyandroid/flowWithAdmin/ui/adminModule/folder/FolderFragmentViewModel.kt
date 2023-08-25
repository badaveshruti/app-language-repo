package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject

@HiltViewModel
class FolderFragmentViewModel @Inject constructor(private val folderRepository: FolderRepository):ViewModel() {


    fun getHomeFilesMap1(siteId:Int) = folderRepository.getParentFileFolderMap(siteId)
    fun getSubFileMap1(siteId:Int,parentFolderId:Int) = folderRepository.getSubFileFolderMap(siteId, parentFolderId)

    fun isFileFolderEmpty(siteId: Int) = folderRepository.isEmpty(siteId)


}