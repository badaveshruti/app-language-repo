package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.adapter

import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem
import qnopy.com.qnopyandroid.databinding.LayoutFileNavEntryBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFolderNavigation

class FolderNavAdapter(val fileFolderEntry:ArrayList<FileFolderItem>, val onNviagate:OnFolderNavigation):RecyclerView.Adapter<FolderNavAdapter.NavEntryVH>() {

    inner class NavEntryVH(val binding: LayoutFileNavEntryBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(folder: FileFolderItem) = with(binding){
            folderName.text = folder.itemTitle
            folderName.setOnClickListener { onNviagate.navigate(fileFolderEntry.get(absoluteAdapterPosition), absoluteAdapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavEntryVH {
        val binding = LayoutFileNavEntryBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return NavEntryVH(binding)
    }

    override fun onBindViewHolder(holder: NavEntryVH, position: Int) {
        val folder = fileFolderEntry.get(position)
        holder.bind(folder)
    }

    override fun getItemCount(): Int = fileFolderEntry.size

}