package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.ItemSiteHeaderBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnRecentEventListener
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnSiteListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.home.siteCompoundView.SiteViewHolder

class SiteAdapter(
    var siteList: ArrayList<Site>,
    var mOnSiteListener: OnSiteListener,
    var mOnEventClickListener: OnRecentEventListener,
    val context: Context
) : RecyclerView.Adapter<SiteViewHolder>(), Filterable {

    private val mFilter: ProjectFilter = ProjectFilter()
    private val originalList: ArrayList<Site> = ArrayList()

    init {
        originalList.addAll(siteList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {

        val binding =
            ItemSiteHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SiteViewHolder(binding, context, mOnSiteListener, mOnEventClickListener)
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        val site = siteList[position]
        holder.bind(site)
    }

    override fun getItemCount(): Int = siteList.size

    override fun getFilter(): Filter = mFilter

    fun updateFavStatus(site: Site, isFavorite: Boolean) {
        val siteItem = siteList.single { sSite -> sSite.siteID == site.siteID }
        siteItem.isFavStatus = isFavorite
        notifyDataSetChanged()
    }

    fun filterByFavs(isFavorite: Boolean) {
        if (isFavorite) {
            val sortedList = siteList.sortedWith(compareByDescending { it.isFavStatus })
            siteList.clear()
            siteList.addAll(sortedList)
        } else {
            siteList.clear()
            siteList.addAll(originalList)
        }
        notifyDataSetChanged()
    }

    private inner class ProjectFilter : Filter() {

        override fun performFiltering(charSeq: CharSequence?): FilterResults {
            val filteredList = arrayListOf<Site>()
            val result = FilterResults()
            if (charSeq == null || charSeq.toString().isEmpty()) {
                filteredList.apply {
                    clear()
                    addAll(originalList)
                }
            } else {
                val pattern = charSeq.toString().lowercase().trim()
                originalList.forEach {
                    if (it.siteName.lowercase().contains(pattern)) {
                        filteredList.add(it)
                    }
                }
            }
            result.apply {
                values = filteredList
                count = filteredList.size
            }
            return result
        }

        override fun publishResults(charSeq: CharSequence?, result: FilterResults?) {
            siteList.apply {
                clear()
                addAll(result?.values as ArrayList<Site>)
                notifyDataSetChanged()
            }
        }
    }
}