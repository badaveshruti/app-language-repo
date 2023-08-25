package qnopy.com.qnopyandroid.flowWithAdmin.ui.events.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.clientmodel.EventData
import qnopy.com.qnopyandroid.databinding.EventLayoutBinding
import qnopy.com.qnopyandroid.db.SiteDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFieldEventListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.events.EventsFragment
import qnopy.com.qnopyandroid.util.Util

class EventsAdapter(
    var allEventsList: ArrayList<EventData>,
    val eventsFragment: EventsFragment,
    var mOnFieldEventListener: OnFieldEventListener
) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>(), Filterable {
    private var mFilter = EventFilter()
    private var originalList: ArrayList<EventData> = arrayListOf()

    init {
        originalList.addAll(allEventsList)
    }

    inner class EventViewHolder(val binding: EventLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventData) = with(binding) {
            tvEventDate.text = getFormattedDate(event.startDate)
            tvEventName.text = event.eventName

            var username = eventsFragment.eventsViewModel.getUserById(
                event.userId.toString()
            )

            if (!event.eventUserName.isNullOrBlank())
                username = event.eventUserName
            else if (username.isBlank())
                username = "Created by: Unknown"

            tvFormName.text = username

            tvProjectName.text = event.siteName

            //1 is enable 0 is disabled event
            if (event.status == 0) {
                layoutCloseEvent.visibility = View.INVISIBLE
                tvEventDate.setBackgroundColor(Color.GRAY)
                tvEventName.setTextColor(Color.GRAY)
                tvFormName.setTextColor(Color.GRAY)
                tvProjectName.setTextColor(Color.GRAY)

                ivEventOptions.setOnClickListener { }
                tvEventDate.setOnClickListener { }
                tvEventName.setOnClickListener { }
                tvFormName.setOnClickListener { }
                tvProjectName.setOnClickListener { }
                layoutCloseEvent.setOnClickListener { }
            } else {

                val isSiteDemo =
                    SiteDataSource(eventsFragment.requireContext()).isSiteTypeDemo(event.siteID)

                if (ScreenReso.isLimitedUser || isSiteDemo)
                    layoutCloseEvent.visibility = View.INVISIBLE
                else
                    layoutCloseEvent.visibility = View.VISIBLE

                tvEventDate.setBackgroundColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.colorPrimary,
                        null
                    )
                )

                tvEventName.setTextColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.colorBlack,
                        null
                    )
                )

                tvFormName.setTextColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.colorBlack,
                        null
                    )
                )

                tvProjectName.setTextColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.colorBlack,
                        null
                    )
                )

                ivEventOptions.setOnClickListener {
                    mOnFieldEventListener.onEventOptionsClicked(
                        event,
                        absoluteAdapterPosition
                    )
                }

                layoutCloseEvent.setOnClickListener {
                    mOnFieldEventListener.onCloseEventClicked(
                        event.status,
                        event,
                        absoluteAdapterPosition
                    )
                }

                itemView.setOnClickListener {
                    mOnFieldEventListener.onEventClicked(
                        event,
                        absoluteAdapterPosition
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

        val binding: EventLayoutBinding =
            EventLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )

        return EventViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event: EventData = allEventsList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = allEventsList.size

    private fun getFormattedDate(date: Long): String =
        Util.getFormattedDateFromMilliS(date, " dd \nMMM")

    override fun getFilter(): Filter = mFilter

    fun updateEvent(eventToClose: EventData) {
        var posToUpdate = 0
        allEventsList.forEachIndexed { pos, event ->
            if (event.eventID == eventToClose.eventID) {
                event.status = 0
                posToUpdate = pos
            }
        }

        notifyItemChanged(posToUpdate)
    }

    private inner class EventFilter : Filter() {
        //runs on background thread
        override fun performFiltering(charSeq: CharSequence?): FilterResults {
            val filteredList = arrayListOf<EventData>()
            val result = FilterResults()
            if (charSeq == null || charSeq.toString().isEmpty()) {
                filteredList.apply {
                    clear()
                    addAll(originalList)
                }
            } else {
                val pattern = charSeq.toString().lowercase().trim()
                originalList.forEach {
                    if (it.eventName.lowercase().contains(pattern)) {
//                        || it.mobAppName.lowercase().contains(pattern) || it.siteName.lowercase().contains(pattern)
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

        //runs on UI thread
        override fun publishResults(charSeq: CharSequence?, result: FilterResults?) {
            allEventsList.apply {
                clear()
                addAll(result?.values as ArrayList<EventData>)
                notifyDataSetChanged()
            }
        }
    }
}