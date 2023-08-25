package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.alphaFolder.AlphaFolderItem
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFileFolderListener
import java.util.ArrayList


class FolderAdapter(val folderMap:MutableMap<Char, ArrayList<FileFolderItem>>, val mOnFileFolderListener: OnFileFolderListener ): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    val keys:List<Char>
    init {
        keys = ArrayList(folderMap.keys)
    }

    class FolderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var alphaFolderItem: AlphaFolderItem = view as AlphaFolderItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val folderItem = AlphaFolderItem(parent.context)
        return FolderViewHolder(folderItem)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folderKey = keys.get(position)
        val folder = folderMap.get(folderKey)
        folder?.let { holder.alphaFolderItem.setFolderSection(folderKey, it, mOnFileFolderListener) }
    }

    override fun getItemCount(): Int = keys.size
}