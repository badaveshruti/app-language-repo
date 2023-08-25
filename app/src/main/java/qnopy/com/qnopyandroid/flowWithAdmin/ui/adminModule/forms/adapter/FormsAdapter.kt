package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.databinding.FormPreviewLayoutBinding
import qnopy.com.qnopyandroid.flowWithAdmin.delegationInterfaces.OnFormListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.getForms.model.formListModel.Form
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

class FormsAdapter(
    val formList: ArrayList<Form>,
    val onFormListener: OnFormListener,
    val context: Context
) : RecyclerView.Adapter<FormsAdapter.FormHolder>(), Filterable {

    private val originalList: ArrayList<Form> = ArrayList()
    private val mFilter: FormsFilter = FormsFilter()

    init {
        originalList.addAll(formList)
    }

    inner class FormHolder(val binding: FormPreviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivSite.setImageDrawable(
                VectorDrawableUtils.getDrawable(
                    context,
                    R.drawable.showpassword,
                    R.color.event_start_blue
                )
            )
        }

        fun bind(form: Form) = with(binding) {

            if (form.getNewForm)
                btnAssign.visibility = View.VISIBLE
            else
                btnAssign.visibility = View.GONE

            tvFormName.text = form.formName
            tvFormName.setOnClickListener { onFormListener.onFormTitleClicked(form) }
            ivSite.setOnClickListener { onFormListener.onShowPreviewClicked(form) }
            btnAssign.setOnClickListener { onFormListener.onFormAssignClicked(form) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormHolder {
        val binding =
            FormPreviewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FormHolder(binding)
    }

    override fun onBindViewHolder(holder: FormHolder, position: Int) {
        val form = formList[position]
        holder.bind(form)
    }

    override fun getItemCount(): Int = formList.size

    override fun getFilter(): Filter = mFilter

    fun updateAssignButton(formId: Int?) {
        val form = formList.single { it.formId == formId }
        form.getNewForm = false
        notifyDataSetChanged()
    }

    fun updateOriginalList(newFormsList: ArrayList<Form>) {
        originalList.clear()
        if (formList.isEmpty()) {
            formList.addAll(newFormsList)
            originalList.addAll(newFormsList)
        } else
            originalList.addAll(formList)
    }

    private inner class FormsFilter : Filter() {
        override fun performFiltering(charSeq: CharSequence?): FilterResults {
            val filteredList = arrayListOf<Form>()
            val result = FilterResults()
            if (charSeq == null || charSeq.toString().isEmpty()) {
                filteredList.apply {
                    clear()
                    addAll(originalList)
                }
            } else {
                val pattern = charSeq.toString().lowercase().trim()
                originalList.forEach {
                    it.formName?.let { name ->
                        if (name.lowercase().contains(pattern)) {
                            filteredList.add(it)
                        }
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
            formList.apply {
                clear()
                addAll(result?.values as ArrayList<Form>)
                notifyDataSetChanged()
            }
        }
    }
}