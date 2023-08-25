package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.siteCompoundView

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.ItemSiteHeaderBinding
import qnopy.com.qnopyandroid.db.EventDataSource
import qnopy.com.qnopyandroid.db.SiteMobileAppDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnRecentEventListener
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnSiteListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter.FormTileAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter.RecentEventsAdapter
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

class SiteViewHolder(
    val binding: ItemSiteHeaderBinding,
    val context: Context,
    var mOnSiteListener: OnSiteListener,
    var mOnEventClickListener: OnRecentEventListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(site: Site) = with(binding) {
        tvSiteName.text = site.siteName.trim()

        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (!isShowInfoEnabled) {
            layoutChildItems.ivInfoStartNew.visibility = View.GONE
            layoutChildItems.ivInfoContinueWith.visibility = View.GONE
        } else {
            layoutChildItems.ivInfoStartNew.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Start New", "Start filling out a blank form from the list " +
                                "below. Every time you fill out one single form, that’s considered " +
                                "one Event. If you’ve just gotten to the site and need to start " +
                                "taking data, start a new Event for the appropriate form by " +
                                "tapping it below. You can also swipe to the right to see more " +
                                "available forms.", "Got it", "",
                        false, context
                    )
                }
            }

            layoutChildItems.ivInfoContinueWith.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        context,
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Continue with", "Tap on a form listed " +
                                "here to go back to a form you’ve already started. You can see who " +
                                "started the form and on what date. Be sure to check the name and date " +
                                "of the form before editing past data. You can scroll to the right " +
                                "to see more past forms.", "Got it", "",
                        false, context
                    )
                }
            }
        }

        ivAdmin.setImageDrawable(
            VectorDrawableUtils.getDrawable(
                context,
                R.drawable.ic_settings, R.color.dark_gray
            )
        )

        if (site.isFavStatus) {
            ivFav.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.ic_favorite_solid
                )
            )
        } else {
            site.isFavStatus = false

            ivFav.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.ic_favorite_border
                )
            )
        }

        if (site.isExpanded) {
            layoutChildItems.childItem.visibility = View.VISIBLE
            ivCollapse.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.expand_arrow, R.color.dark_gray
                )
            )
            handleChildRecyclerItems(site)
        } else {
            layoutChildItems.childItem.visibility = View.GONE
            ivCollapse.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.arrow_right_enabled, R.color.dark_gray
                )
            )
        }

        layoutChildItems.rvLatestEvents.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutChildItems.rvLatestEvents.itemAnimator = DefaultItemAnimator()

        layoutChildItems.rvForms.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutChildItems.rvForms.itemAnimator = DefaultItemAnimator()

        setUpClickListeners(site)
    }

    private fun setUpClickListeners(site: Site) {
        with(binding) {
            layoutHeader.setOnClickListener {
                mOnSiteListener.onAdminSettingsClicked(site)
/*                if (site.isExpanded) {
                    site.isExpanded = false
                    layoutChildItems.childItem.visibility = View.GONE
                    ivCollapse.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.arrow_right_enabled, R.color.dark_gray
                        )
                    )
                } else {
                    site.isExpanded = true
                    layoutChildItems.childItem.visibility = View.VISIBLE
                    ivCollapse.setImageDrawable(
                        VectorDrawableUtils.getDrawable(
                            context,
                            R.drawable.expand_arrow, R.color.dark_gray
                        )
                    )
                    handleChildRecyclerItems(site)
                }*/
            }

            ivFav.setOnClickListener {
                if (site.isFavStatus) {
                    handleFavProjectApi(site, false)
                } else {
                    handleFavProjectApi(site, true)
                }
            }

            ivAdmin.setOnClickListener { mOnSiteListener.onAdminSettingsClicked(site) }
        }
    }

    private fun handleFavProjectApi(site: Site, favStatus: Boolean) {
        mOnSiteListener.onSiteFavoriteClicked(
            site, favStatus,
            absoluteAdapterPosition
        )
    }

    private fun handleChildRecyclerItems(site: Site) {
        if (site.eventList.isEmpty()) {
            site.eventList.addAll(EventDataSource(context).getRecentEvents(site.siteID.toString()))
            if (site.eventList.size >= 10)
                site.eventList = ArrayList(site.eventList.slice(0..9))
        }

        if (site.formsList.isEmpty()) {
            site.formsList.add(SSiteMobileApp(true))
            site.formsList.addAll(SiteMobileAppDataSource(context).getAllAppsV16(site.siteID))
        }

        if (site.eventList.isNotEmpty()) {
            binding.layoutChildItems.layoutEvents.visibility = View.VISIBLE
            setEventAdapter(site.eventList)
        } else {
            binding.layoutChildItems.layoutEvents.visibility = View.GONE
        }

        if (site.formsList.isNotEmpty()) {
            binding.layoutChildItems.layoutForms.visibility = View.VISIBLE
            setFormsAdapter(site.formsList, site)
        } else {
            binding.layoutChildItems.layoutForms.visibility = View.GONE
        }
    }

    private fun setFormsAdapter(
        formsList: java.util.ArrayList<SSiteMobileApp>, site: Site
    ) {
        val adapter = FormTileAdapter(site, formsList, mOnSiteListener)
        binding.layoutChildItems.rvForms.adapter = adapter
    }

    private fun setEventAdapter(eventList: ArrayList<EventData>) {
        val adapter = RecentEventsAdapter(eventList, mOnEventClickListener, context)
        binding.layoutChildItems.rvLatestEvents.adapter = adapter
    }
}