package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem
import qnopy.com.qnopyandroid.db.FileFolderDataSource
import javax.inject.Inject

class FolderRepository @Inject constructor(private val fileFolderDataSource: FileFolderDataSource) {

    private fun getHomeFileFolderItemList(siteId:Int):ArrayList<FileFolderItem> = fileFolderDataSource.getHomeFileFolderItemList(siteId.toString())
    private fun getSubFileFolderItemList(siteId:Int, parentFolderId:Int):ArrayList<FileFolderItem> = fileFolderDataSource.getSubFileFolderItemList(parentFolderId.toString(), siteId.toString())

    fun isEmpty(siteId: Int):Boolean = getHomeFileFolderItemList(siteId).isEmpty()

    fun getParentFileFolderMap(siteId: Int):MutableMap<Char, ArrayList<FileFolderItem>>{
        val folderMap = mutableMapOf<Char,ArrayList<FileFolderItem>>()
        for(folder in getHomeFileFolderItemList(siteId)){
            val key = folder.itemTitle[0].uppercaseChar()
            val list:ArrayList<FileFolderItem> = folderMap[key] ?: ArrayList()
            list.add(folder)
            folderMap[key] = list
        }
        return folderMap
    }

    fun getSubFileFolderMap(siteId: Int, parentFolderId: Int):MutableMap<Char, ArrayList<FileFolderItem>>{
        val folderMap = mutableMapOf<Char,ArrayList<FileFolderItem>>()
        for(folder in getSubFileFolderItemList(siteId, parentFolderId)){
            val key = folder.itemTitle[0].uppercaseChar()
            val list:ArrayList<FileFolderItem> = folderMap[key] ?: ArrayList()
            list.add(folder)
            folderMap[key] = list
        }
        return folderMap
    }

    suspend fun getParentFileFolderHashMap(siteId: Int) : Flow<MutableMap<Char, ArrayList<FileFolderItem>>>{
      return flow {

              val folderMap = mutableMapOf<Char,ArrayList<FileFolderItem>>()
              for(folder in getHomeFileFolderItemList(siteId)){
                  var key = folder.itemTitle[0].uppercaseChar()
                  val list:ArrayList<FileFolderItem> = folderMap[key] ?: ArrayList()
                  list.add(folder)
                  folderMap[key] = list
              }
          emit(folderMap)
          }.flowOn(Dispatchers.IO)
      }

    suspend fun getSubFileFolderHashMap(siteId: Int, parentFolderId: Int) : Flow<MutableMap<Char, ArrayList<FileFolderItem>>>{
        return flow {

            val folderMap = mutableMapOf<Char,ArrayList<FileFolderItem>>()
            for(folder in getSubFileFolderItemList(siteId, parentFolderId)){
                val key = folder.itemTitle[0].uppercaseChar()
                val list:ArrayList<FileFolderItem> = folderMap.get(key) ?: ArrayList()
                list.add(folder)
                folderMap.put(key,list)
            }
            emit(folderMap)
        }.flowOn(Dispatchers.IO)
    }


}