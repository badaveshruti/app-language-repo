package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.alphaFolder

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem
import qnopy.com.qnopyandroid.databinding.AlphaFolderlistLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFileFolderListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.adapter.FolderItemAdapter

class AlphaFolderItem:RelativeLayout {
    lateinit var binding:AlphaFolderlistLayoutBinding
    constructor(context: Context) : super(context) {
        initUI(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initUI(context)
    }

    private fun initUI(context: Context){
        binding =  AlphaFolderlistLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setFolderSection(folderKey: Char, folders: ArrayList<FileFolderItem>, mOnFileFolderListener: OnFileFolderListener)
        = with(binding) {
        header.text = folderKey.toString()
        folderlistRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = FolderItemAdapter(folders, mOnFileFolderListener)
        }
    }
}