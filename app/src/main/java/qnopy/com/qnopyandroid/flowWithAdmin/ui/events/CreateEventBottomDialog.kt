package qnopy.com.qnopyandroid.flowWithAdmin.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import qnopy.com.qnopyandroid.databinding.DialogStartDataCollectionBinding
import qnopy.com.qnopyandroid.databinding.LayoutCreateEventBottomsheetBinding

class CreateEventBottomDialog : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutCreateEventBottomsheetBinding

    companion object {
        fun newInstance(): CreateEventBottomDialog {
            return CreateEventBottomDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutCreateEventBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }
}