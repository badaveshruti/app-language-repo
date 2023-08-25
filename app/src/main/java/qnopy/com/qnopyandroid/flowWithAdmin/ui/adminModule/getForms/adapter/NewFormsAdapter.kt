package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.databinding.NewformLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.OnFormClickListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form

class NewFormsAdapter(
    val formList: ArrayList<Form>,
    val mOnFormClickListener: OnFormClickListener
) : RecyclerView.Adapter<NewFormsAdapter.NewFormViewHolder>() {

    inner class NewFormViewHolder(val binding: NewformLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(form: Form) = with(binding) {
            formTitle.text = form.formName
            previewTv.setOnClickListener {
                mOnFormClickListener.OnFormPreviewClicked(
                    form,
                    absoluteAdapterPosition
                )
            }
            formTitle.setOnClickListener {
                mOnFormClickListener.OnFormAssignClicked(
                    form,
                    absoluteAdapterPosition
                )
            }
            assignButton.setOnClickListener {
                mOnFormClickListener.OnFormAssignClicked(
                    form,
                    absoluteAdapterPosition
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFormViewHolder {
        val binding =
            NewformLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewFormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewFormViewHolder, position: Int) {
        val form: Form = formList.get(position)
        holder.bind(form)
    }

    override fun getItemCount(): Int = formList.size

}