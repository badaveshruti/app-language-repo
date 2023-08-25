package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.databinding.ItemLiveFeedBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils

class LiveFeedAdapter(
    private val context: AppCompatActivity,
    private val listLiveFeed: ArrayList<LiveFeedResponse.LiveFeed>
) : RecyclerView.Adapter<LiveFeedAdapter.ViewHolder>() {

    var displayMetrics: DisplayMetrics = Utils.getDisplayMetrics(context)

    inner class ViewHolder(val binding: ItemLiveFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(liveFeed: LiveFeedResponse.LiveFeed) = with(binding) {
            tvEventName.text = liveFeed.eventName

            tvTime.text = Utils.getDateTimeDaysAgo(liveFeed.creationDate)
            tvPostText.text = liveFeed.postText

            val params =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )

/*            params.height = displayMetrics.widthPixels
            pagerFeed.layoutParams = params*/

            if (liveFeed.liveFeedPicsDataList.isNullOrEmpty()) {
                pagerFeed.visibility = View.GONE
                circleIndicator.visibility = View.GONE
            } else {
                pagerFeed.visibility = View.VISIBLE
                circleIndicator.visibility = View.VISIBLE
                val adapter =
                    FeedImagesAdapter(context, liveFeed.liveFeedPicsDataList!!, liveFeed.postId)
                pagerFeed.adapter = adapter
                circleIndicator.attachToPager(pagerFeed)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLiveFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listLiveFeed.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listLiveFeed[position])
    }

    fun updateList(liveFeedList: java.util.ArrayList<LiveFeedResponse.LiveFeed>) {
        listLiveFeed.addAll(liveFeedList)
        notifyItemRangeChanged(0, listLiveFeed.size)
    }
}