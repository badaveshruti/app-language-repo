package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem
import qnopy.com.qnopyandroid.databinding.FolderLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFileFolderListener

class FolderItemAdapter(val folderList: MutableList<FileFolderItem>,val mOnFileFolderListener: OnFileFolderListener):
                        RecyclerView.Adapter<FolderItemAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(val binding: FolderLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(fileFolder: FileFolderItem) = with(binding){

            folderTitleTextView.text = fileFolder.itemTitle

            val drawable = if (fileFolder.itemType.equals("folder")){
                ResourcesCompat.getDrawable(itemView.context.resources, R.mipmap.ic_folder, null)
            }else{
                ResourcesCompat.getDrawable(itemView.context.resources, R.mipmap.ic_doc, null)
            }
            folderImgView.setImageDrawable(drawable)

            itemView.setOnClickListener { mOnFileFolderListener.onFileFolderClicked(fileFolder) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = FolderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val fileFolder = folderList.get(position)
        holder.bind(fileFolder)
    }

    override fun getItemCount(): Int = folderList.size
}