package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.ActivityOrderEquipmentsBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.EquipmentViewModel
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.adapter.OrderFormMaster
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.model.SaveEquipmentsOrderResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity

@Suppress("DEPRECATION")
@AndroidEntryPoint
class OrderEquipmentsActivity : ProgressDialogActivity() {

    private lateinit var formMasterAdapter: OrderFormMaster
    private lateinit var order: Order
    private lateinit var listEquipmentFields: ArrayList<EquipmentField>
    private lateinit var site: Site
    private lateinit var binding: ActivityOrderEquipmentsBinding
    private val viewModel: EquipmentViewModel by viewModels()

    companion object {
        fun startActivity(
            context: Context,
            site: Site,
            listEquipmentFields: ArrayList<EquipmentField>, order: Order?
        ) {
            val intent = Intent(context, OrderEquipmentsActivity::class.java)
            intent.putExtra(GlobalStrings.SITE_DETAILS, site)
            intent.putExtra(GlobalStrings.KEY_EQUIPMENT_LIST, listEquipmentFields)

            order?.let {
                intent.putExtra(GlobalStrings.KEY_EQUIPMENT_ORDER, order)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderEquipmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Equipment Order"

        getIntentData()
        addMenu()
        addObserver()
        setUpForm()
    }

    private fun addMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(qnopy.com.qnopyandroid.R.menu.menu_order_equip, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == qnopy.com.qnopyandroid.R.id.item_save_order) {
                    saveOrder()
                }
                return true
            }
        })
    }

    private fun saveOrder() {
        if (this::formMasterAdapter.isInitialized) {
            val isFormValid = formMasterAdapter.validateForm()
            if (isFormValid) {
                val request = formMasterAdapter.getOrderRequest()
                if (CheckNetwork.isInternetAvailable(this)) {
                    viewModel.saveEquipmentOrder(request)
                } else {
                    showToast(
                        getString(qnopy.com.qnopyandroid.R.string.please_check_internet_connection),
                        true
                    )
                }
            } else
                showToast("Please fill all required fields", true)
        }
    }

    private fun getIntentData() {
        site = Utils.getSerializable(intent, GlobalStrings.SITE_DETAILS, Site::class.java)

        listEquipmentFields = intent.getSerializableExtra(
            GlobalStrings.KEY_EQUIPMENT_LIST
        ) as ArrayList<EquipmentField>

        if (intent.hasExtra(GlobalStrings.KEY_EQUIPMENT_ORDER))
            order =
                Utils.getSerializable(intent, GlobalStrings.KEY_EQUIPMENT_ORDER, Order::class.java)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun addObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.saveEquipmentOrderFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showAlertProgress()
                    }
                    is ApiState.Success -> {
                        cancelAlertProgress()
                        val res = it.response as SaveEquipmentsOrderResponse

                        if (res.success) {
                            showToast(
                                "Equipments ordered successfully", true
                            )
                            finish()
                        } else {
                            res.message?.let { message -> showToast(message, true) }
                        }
                    }
                    is ApiState.Failure -> {
                        cancelAlertProgress()
                        showToast(
                            getString(qnopy.com.qnopyandroid.R.string.something_went_wrong),
                            true
                        )
                    }
                    is ApiState.Empty -> {}
                }
            }
        }
    }

    private fun setUpForm() {
        formMasterAdapter = if (this::order.isInitialized)
            OrderFormMaster(
                this,
                listEquipmentFields,
                site.siteID.toString(),
                order
            )
        else OrderFormMaster(this, listEquipmentFields, site.siteID.toString(), null)

        binding.rvOrderForm.apply {
            layoutManager =
                LinearLayoutManager(
                    this@OrderEquipmentsActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            setItemViewCacheSize(22)
            adapter = formMasterAdapter
        }
    }
}