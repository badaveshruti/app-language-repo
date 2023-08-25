package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.Site
import qnopy.com.qnopyandroid.databinding.FragmentEquipmentsBinding
import qnopy.com.qnopyandroid.flowWithAdmin.base.BaseFragment
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.EquipmentViewModel
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.adapter.OrdersAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentField
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.EquipmentOrdersListResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.OnEquipmentOderClickListener
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.equipmentList.model.Order
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.equipment.orderEquipment.OrderEquipmentsActivity
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.forms.GetNewFormsViewModel
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.util.AlertManager
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils

@AndroidEntryPoint
class EquipmentsFragment : BaseFragment(), OnEquipmentOderClickListener {

    private lateinit var site: Site
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var jobOrdersList: Job

    private lateinit var binding: FragmentEquipmentsBinding
    private lateinit var viewModel: EquipmentViewModel

    private var listEquipmentFields: ArrayList<EquipmentField> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEquipmentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    private fun setUpUi() {

        site =
            Utils.getSerializable(requireArguments(), GlobalStrings.SITE_DETAILS, Site::class.java)

        viewModel = ViewModelProvider(
            this
        )[EquipmentViewModel::class.java]

        handleInfoButtonVisibility()

        setEquipmentAdapter()

        binding.ivStartOrder.setOnClickListener { binding.tvStartNewOrder.performClick() }
        binding.tvStartNewOrder.setOnClickListener {
            if (listEquipmentFields.isNotEmpty())
                OrderEquipmentsActivity.startActivity(
                    requireActivity(),
                    site,
                    listEquipmentFields, null
                )
        }

        binding.swipeRefreshLayoutEquipments.apply {
            setOnRefreshListener {
                isRefreshing = false
                fetchEquipmentList()
            }
        }

        addObserver()
    }

    private fun setEquipmentAdapter() {
        ordersAdapter = OrdersAdapter(ArrayList(), this, requireContext())

        binding.rvEquipmentOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ordersAdapter
        }

    }

    private fun handleInfoButtonVisibility() {
        val isShowInfoEnabled = Util.getSharedPrefBoolProperty(
            context,
            GlobalStrings.ENABLE_INFO_BUTTONS
        )

        if (isShowInfoEnabled) {
            binding.ivStartOrderInfo.apply {
                setImageDrawable(
                    VectorDrawableUtils.getDrawable(
                        requireActivity(),
                        R.drawable.ic_info, R.color.event_start_blue
                    )
                )

                setOnClickListener {
                    AlertManager.showNormalAlert(
                        "Equipment", "Tap this to order new equipments.", "Got it", "",
                        false, context
                    )
                }
            }
        } else {
            binding.ivStartOrderInfo.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        fetchEquipmentList()
    }

    private fun fetchEquipmentList() {
        if (CheckNetwork.isInternetAvailable(requireContext(), true)) {
            if (jobOrdersList.isActive) {
                jobOrdersList.cancel()
                addObserver()
            }
            viewModel.fetchEquipmentOrdersList(site.siteID.toString())
        }
    }

    private fun addObserver() {
        jobOrdersList = lifecycleScope.launchWhenStarted {
            viewModel.equipmentListFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgress("Fetching all orders...")
                    }
                    is ApiState.Success -> {
                        cancelProgress()
                        val res = it.response as EquipmentOrdersListResponse

                        if (res.success && res.responseCode.equals(
                                HttpStatus.OK.reasonPhrase,
                                ignoreCase = true
                            ) && res.data != null
                        ) {
                            if (this@EquipmentsFragment::ordersAdapter.isInitialized) {
                                res.data.order?.let { orderList ->
                                    ordersAdapter.updateList(
                                        orderList
                                    )
                                    binding.rvEquipmentOrders.visibility = View.VISIBLE
                                    binding.tvNoOrdersFound.visibility = View.GONE
                                }
                            }

                            res.data.equipmentFields?.let { fields ->
                                listEquipmentFields.clear()
                                listEquipmentFields.addAll(fields)
                            }
                        } else {
                            binding.rvEquipmentOrders.visibility = View.GONE
                            binding.tvNoOrdersFound.visibility = View.VISIBLE
                            res.message?.let { message -> showToast(message, true) }
                        }
                    }
                    is ApiState.Failure -> {
                        cancelProgress()
                        showToast(getString(R.string.something_went_wrong), true)
                    }
                    is ApiState.Empty -> {}
                }
            }
        }
    }

    override fun onOrderEditClicked(order: Order) {
        if (listEquipmentFields.isNotEmpty())
            OrderEquipmentsActivity.startActivity(
                requireActivity(),
                site,
                listEquipmentFields, order
            )
    }
}