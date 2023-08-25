package qnopy.com.qnopyandroid.flowWithAdmin.ui.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentProjectBinding
import qnopy.com.qnopyandroid.db.EventDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.EquipmentsFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.folder.FolderFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms.FormsFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.home.HomeTabFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.LiveFeedActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.locations.LocationsFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.pdfLogs.PdfLogsFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.users.UsersFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.events.EventsFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.homeScreen.HomeScreenActivity
import qnopy.com.qnopyandroid.ui.task.TaskFragment
import qnopy.com.qnopyandroid.ui.task.TaskMapFragment
import qnopy.com.qnopyandroid.ui.task.TasksTabFragment
import qnopy.com.qnopyandroid.util.Util


/* PatelSanket 5 Dec, 22
* Note: this fragment show all tabs (admin tabs) so basically when project menu is clicked you wont see this fragment
* instead HomeFragment is loaded which have sites list upon site clicked this fragment will show up.
* The naming convention might be confusing as the flow was vice versa before and being time bound i skipped it.
* */

@AndroidEntryPoint
class ProjectFragment : Fragment() {
    private var selectedTabPos: Int = 0
    val args by navArgs<ProjectFragmentArgs>()

    lateinit var site: Site
    lateinit var binding: FragmentProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProjectBinding.inflate(layoutInflater, container, false)

        this.setHasOptionsMenu(true)
        (requireActivity() as HomeScreenActivity).backButtonVisibility(true)

        site = args.site

        Util.setSharedPreferencesProperty(
            requireContext(),
            GlobalStrings.CURRENT_SITEID, site.siteID.toString()
        )

        Util.setSharedPreferencesProperty(
            requireContext(),
            GlobalStrings.CURRENT_SITENAME, site.siteName
        )

        val tabNameList =
            arrayListOf(
                "Home",
                "Forms",
                "Tasks",
                "Equipment",
                "Users",
                "Locations",
                "Folder",
                "PDF Logs"
            )

        val fragmentList = arrayListOf(
            HomeTabFragment(),
            FormsFragment(),
            TasksTabFragment(),
            EquipmentsFragment(),
            UsersFragment(),
            LocationsFragment(),
            FolderFragment(),
            PdfLogsFragment()
        )

        binding.siteTabLayout.apply {

            doOnLayout {
                val tabWidth = this.width / tabNameList.size
                tabNameList.forEachIndexed { _, tabName ->

                    this.newTab().run {
                        setCustomView(R.layout.layout_custom_tab)
                        customView?.minimumWidth = tabWidth
                        text = tabName
                        this@apply.addTab(this)
                    }
                }
            }
            /*            tabNameList.forEachIndexed { _, s ->
                            addTab(this.newTab().setText(s))
                        }*/
        }

        val bundle = Bundle()
        bundle.putSerializable(GlobalStrings.SITE_DETAILS, site)
        fragmentList[0].arguments = bundle

        parentFragmentManager.beginTransaction().replace(
            binding.fragmentContainer.id,
            fragmentList[0]
        ).commit()

        binding.siteTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabPos = tab?.position ?: 0
                fragmentList[selectedTabPos].arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(binding.fragmentContainer.id, fragmentList[selectedTabPos])
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeScreenActivity).setTitle(args.site.siteName)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as HomeScreenActivity).enableHomeMenuItem()

        if (ScreenReso.isDownloadData || !EventDataSource(requireActivity()).isEventsDownloadedAlready) {
            ScreenReso.isDownloadData = false
            (requireActivity() as HomeScreenActivity).refreshEvents()
        }
    }

    fun refreshHomeTabOrEvents() {
        if (selectedTabPos == 0) {
            val fragment: Fragment? =
                parentFragmentManager.findFragmentById(binding.fragmentContainer.id)
            fragment?.let {
                if (it is HomeTabFragment)
                    it.handleRecyclerItems()

                if (it is EventsFragment)
                    it.fetchEvents()
            }
        }
    }

    fun openLiveFeedActivity() {
        LiveFeedActivity.startActivity(site.siteID.toString(), requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as HomeScreenActivity).handleLiveFeedItemVisibility(false)
    }
}