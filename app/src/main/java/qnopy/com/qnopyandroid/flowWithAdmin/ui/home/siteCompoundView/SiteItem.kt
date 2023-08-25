package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.siteCompoundView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.ProjectLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnSiteListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter.FormTileAdapter
import qnopy.com.qnopyandroid.util.Util
import javax.inject.Inject

@AndroidEntryPoint
class SiteItem : RelativeLayout {

    private lateinit var mSite: Site
    private var mPos: Int = 0

    private lateinit var mOnSiteListener: OnSiteListener

    lateinit var binding: ProjectLayoutBinding

    @Inject
    lateinit var repo: SiteItemRepository

    constructor(context: Context, mOnSiteListener: OnSiteListener) : super(context) {
        this.mOnSiteListener = mOnSiteListener

        initUI(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initUI(context)
    }

    private fun initUI(context: Context) {
        binding = ProjectLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        with(binding) {
            siteTitle.setOnClickListener { mOnSiteListener.onAdminSettingsClicked(mSite) }
            favorite.setOnClickListener {
//                mOnSiteListener.onSiteFavoriteClicked(mSite, mSite.isFavStatus), mPos)
                updateFavourite()
            }
        }
    }

    private fun updateFavourite() = with(binding) {
        val favIcon = if (mSite.isFavStatus)
            ContextCompat.getDrawable(context, R.drawable.ic_favorite_solid)
        else
            ContextCompat.getDrawable(context, R.drawable.ic_favorite_border)
        favorite.setImageDrawable(favIcon)
    }

    fun setSite(site: Site, pos: Int) = with(binding) {
        mSite = site
        mPos = pos
        siteTitle.text = site.siteName

        val companyId = Util.getSharedPreferencesProperty(context, GlobalStrings.COMPANYID).toInt()

        formsTileRv.apply {
            adapter =
                FormTileAdapter(site, repo.getAllForms(site.siteID, companyId), mOnSiteListener)
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        updateFavourite()
    }

}