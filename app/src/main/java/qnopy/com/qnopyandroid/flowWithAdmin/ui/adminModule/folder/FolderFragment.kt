package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.FileFolderItem
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentFolderBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFileFolderListener
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFolderNavigation
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.adapter.FolderAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.adapter.FolderNavAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.utility.FileUtil
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.ui.activity.FileFolderSyncActivity
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import java.io.File

@AndroidEntryPoint
class FolderFragment : Fragment() {

    private lateinit var site: Site
    private lateinit var binding: FragmentFolderBinding
    private val viewModel: FolderFragmentViewModel by viewModels()
    private lateinit var folderNavAdapter: FolderNavAdapter
    private var folderNavList: ArrayList<FileFolderItem> = arrayListOf()
    private var IS_SYNCED = false
    private var DO_REFRESH = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFolderBinding.inflate(inflater, container, false)
        folderNavAdapter = FolderNavAdapter(folderNavList, OnNavigate())

        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)

        if (viewModel.isFileFolderEmpty(site.siteID)) {
            IS_SYNCED = true
            Util.setSharedPreferencesProperty(
                context,
                GlobalStrings.CURRENT_SITEID,
                site.siteID.toString()
            )
            startActivity(Intent(requireActivity(), FileFolderSyncActivity::class.java))
        }

        binding.homeFolderImg.setOnClickListener {
            updateRv(viewModel.getHomeFilesMap1(site.siteID))
            folderNavAdapter.notifyItemRangeRemoved(0, folderNavList.size)
            folderNavList.clear()
        }

        binding.folderNavRv.adapter = folderNavAdapter
        folderNavAdapter.notifyItemRangeRemoved(0, folderNavList.size)
        folderNavList.clear()

        updateRv(viewModel.getHomeFilesMap1(site.siteID))

        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (isShowInfoEnabled) {

            binding.ivInfoFileFolder.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Folder", "The project folder can store any files you might " +
                                "need access to in the field – just like a digital clipboard! You " +
                                "can upload PDFs, images, text files, and more from the QNOPY web " +
                                "portal. Note that you must upload files to the Project Folder " +
                                "from the QNOPY web portal before heading out into the field in " +
                                "order to access your files.", "Got it", "",
                        false, context
                    )
                }
            }
        } else {
            binding.ivInfoFileFolder.visibility = View.GONE
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        DO_REFRESH = IS_SYNCED
        IS_SYNCED = false
    }

    override fun onResume() {
        super.onResume()
        if (DO_REFRESH) {
            updateRv(viewModel.getHomeFilesMap1(site.siteID))
        }
    }

    inner class FileFolderClickedListener : OnFileFolderListener {
        override fun onFileFolderClicked(fileFolderItem: FileFolderItem) {
            if (fileFolderItem.itemType.equals("folder")) {
                updateRv(viewModel.getSubFileMap1(site.siteID, fileFolderItem.itemID.toInt()))
                folderNavList.add(fileFolderItem)
                folderNavAdapter.notifyItemInserted(folderNavList.size - 1)
            } else {
                val fileDir = Util.getFileFolderDirPath(context, site.siteID.toString())
                if (fileDir.isEmpty()) {
                    Toast.makeText(context, "No files found", Toast.LENGTH_SHORT).show()
                } else {
                    val file = File(fileDir, fileFolderItem.itemGuid)
                    FileUtil.openFile(requireContext(), file)
                }
            }
        }
    }

    inner class OnNavigate : OnFolderNavigation {
        override fun navigate(fileFolderItem: FileFolderItem, position: Int) {
            updateRv(viewModel.getSubFileMap1(site.siteID, fileFolderItem.itemID.toInt()))

            folderNavAdapter.notifyItemRangeRemoved(position + 1, folderNavList.size)
            folderNavList.subList(position + 1, folderNavList.size).clear()
        }
    }

    private fun updateRv(mFileFolderMap: MutableMap<Char, ArrayList<FileFolderItem>>) {
        binding.foldersRv.adapter = FolderAdapter(mFileFolderMap, FileFolderClickedListener())

        if (mFileFolderMap.isEmpty()) {
            binding.foldersRv.visibility = View.INVISIBLE
            binding.tvNoFolders.visibility = View.VISIBLE
        } else {
            binding.foldersRv.visibility = View.VISIBLE
            binding.tvNoFolders.visibility = View.GONE
        }
    }
}