package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.databinding.RecentProjectBinding
import qnopy.com.qnopyandroid.db.UserDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnRecentEventListener
import qnopy.com.qnopyandroid.util.Util

class RecentEventsAdapter(
    var eventList: List<EventData>,
    var onRecentListener: OnRecentEventListener,
    var context: Context
) : RecyclerView.Adapter<RecentEventsAdapter.RecentProjectViewHolder>() {

    inner class RecentProjectViewHolder(val binding: RecentProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventData) = with(binding) {
            projectNameTv.text =
                event.eventName.ifEmpty { event.mobAppName }
            formNameTv.text = UserDataSource(context).getFullName(event.userId.toString())

            if (event.startDate.toString().length <= 10)
                event.startDate *= 1000

            dateTv.text = getFormattedDate(event.startDate)
//            projectNameTv.setOnClickListener { onRecentListner.onEventClicked(event) }
            root.setOnClickListener { onRecentListener.onEventClicked(event) }
            moreImg.setOnClickListener { onRecentListener.onShowMoreClicked(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentProjectViewHolder {
        val binding =
            RecentProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentProjectViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecentProjectViewHolder, position: Int) {
        val event: EventData = eventList.get(position)
        holder.bind(event)
    }

    override fun getItemCount(): Int = eventList.size

    fun getFormattedDate(timeInMillis: Long): String {
        return Util.getFormattedDateFromMilliS(timeInMillis, "dd MMM yyyy")
    }
}