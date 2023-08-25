package qnopy.com.qnopyandroid.flowWithAdmin.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.ItemFormBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnSiteListener
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp

class FormTileAdapter(
    val site: Site,
    val formList: List<SSiteMobileApp>,
    val mSiteListener: OnSiteListener
) :
    RecyclerView.Adapter<FormTileAdapter.FormTileViewHolder>() {

    inner class FormTileViewHolder(val binding: ItemFormBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sSiteMobileApp: SSiteMobileApp) = with(binding) {
            if (sSiteMobileApp.isGetNewForm) {
                layoutNewForm.visibility = View.VISIBLE
                layoutFormName.visibility = View.GONE
            } else {
                layoutNewForm.visibility = View.GONE
                layoutFormName.visibility = View.VISIBLE

                tvFormName.text = sSiteMobileApp.display_name
            }

            itemView.setOnClickListener {
                mSiteListener.onFormTileClick(
                    site,
                    sSiteMobileApp,
                    absoluteAdapterPosition
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FormTileAdapter.FormTileViewHolder {

        val binding = ItemFormBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return FormTileViewHolder(binding)

    }

    override fun onBindViewHolder(holder: FormTileViewHolder, position: Int) {
        val sSiteMobileApp: SSiteMobileApp = formList.get(position)
        holder.bind(sSiteMobileApp)
    }

    override fun getItemCount(): Int = formList.size


}