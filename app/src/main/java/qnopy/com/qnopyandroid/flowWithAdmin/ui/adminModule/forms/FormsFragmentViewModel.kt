package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import qnopy.com.qnopyandroid.requestmodel.SSiteMobileApp
import javax.inject.Inject

@HiltViewModel
class FormsFragmentViewModel @Inject constructor(private val formsRepository: FormsRepository) :
    ViewModel() {

    fun getAllForms(siteId: Int): List<SSiteMobileApp> =
        formsRepository.getAllForms(siteId)

}