package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import org.springframework.http.HttpStatus
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.databinding.ActivityLiveFeedBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.adapter.LiveFeedAdapter
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity
import qnopy.com.qnopyandroid.util.Util

@AndroidEntryPoint
class LiveFeedActivity : ProgressDialogActivity() {

    private lateinit var binding: ActivityLiveFeedBinding
    private lateinit var adapter: LiveFeedAdapter
    private val viewModel: LiveFeedViewModel by viewModels()
    private var siteId: String? = ""
    private var lastSyncDate = "0"

    companion object {
        fun startActivity(siteId: String, context: Context) {
            val intent = Intent(context, LiveFeedActivity::class.java)
            intent.putExtra(GlobalStrings.KEY_SITE_ID, siteId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        siteId = intent.getStringExtra(GlobalStrings.KEY_SITE_ID)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Live Feed"

        setUpRecyclerView()

        binding.swipeRefreshLiveFeed.apply {
            setOnRefreshListener {
                isRefreshing = false
                fetchLiveFeed(lastSyncDate)
            }
        }

        setObserver()
    }

    private fun fetchLiveFeed(lastSyncDate: String) {
        siteId?.let { viewModel.getLiveFeed(it, lastSyncDate) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchLiveFeedSF.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showAlertProgress("Fetching live feed...")
                    }

                    is ApiState.Success -> {
                        cancelAlertProgress()
                        val response = it.response as LiveFeedResponse

                        if (response.success && response.responseCode == HttpStatus.OK.reasonPhrase && response.data != null) {
                            lastSyncDate = response.data.lastSyncDate.toString()

                            if (response.data.liveFeedList.isNotEmpty()) {
                                response.data.liveFeedList.sortByDescending { feed -> feed.creationDate }
                                adapter.updateList(response.data.liveFeedList)
                                binding.rvLiveFeed.visibility = View.VISIBLE
                                binding.tvNoLiveFeed.visibility = View.GONE
                            } else if (adapter.itemCount == 0) {
                                binding.rvLiveFeed.visibility = View.INVISIBLE
                                binding.tvNoLiveFeed.visibility = View.VISIBLE
                            }
                        } else if (!response.success && response.responseCode.lowercase() == HttpStatus.UNAUTHORIZED.reasonPhrase.lowercase()) {
                            binding.rvLiveFeed.visibility = View.INVISIBLE
                            binding.tvNoLiveFeed.visibility = View.VISIBLE
                            GlobalStrings.responseMessage = response.message
                            Util.setDeviceNOT_ACTIVATED(this@LiveFeedActivity, "", "")
                        }
                    }
                    is ApiState.Failure -> {
                        cancelAlertProgress()
                    }
                    is ApiState.Empty -> {

                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        adapter = LiveFeedAdapter(this, ArrayList())
        binding.rvLiveFeed.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (lastSyncDate == "0")
            fetchLiveFeed(lastSyncDate)
    }
}