package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel
import qnopy.com.qnopyandroid.databinding.LayoutAttachmentPagerItemBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.Util

class FeedSlideShowPagerAdapter(
    private val feedList: ArrayList<LiveFeedResponse.LiveFeedPictureData>,
    private val context: Context
) : RecyclerView.Adapter<FeedSlideShowPagerAdapter.ViewHolder>() {

    var userId: String = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID)
    var deviceInfo: DeviceInfoModel = DeviceInfo.getDeviceInfo(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            LayoutAttachmentPagerItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(feedList[position])
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    inner class ViewHolder(private val binding: LayoutAttachmentPagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(liveFeed: LiveFeedResponse.LiveFeedPictureData) {
            setImage(liveFeed, binding)
        }
    }

    private fun setImage(
        liveFeedPicture: LiveFeedResponse.LiveFeedPictureData,
        binding: LayoutAttachmentPagerItemBinding
    ) {
        //removing == from key as disk cache validation don't allow equals
        val bitmap = try {
            ScreenReso.diskLruCache.getBitmap(
                liveFeedPicture.cacheIdOriginal
            )
        } catch (e: Exception) {
            null
        }

        if (bitmap != null) {
            binding.pbAttachment.visibility = View.GONE
            Glide.with(context).asBitmap().load(bitmap)
                .into(binding.ivAttachment)
        } else {

            val baseUrl: String = (context.getString(R.string.prod_base_uri)
                    + SubUrls.URL_DOWNLOAD_PDF + "?file=" + liveFeedPicture.fileKeyThumbImageEncode)

            binding.pbAttachment.visibility = View.VISIBLE

            val client = AsyncHttpClient()
            client.addHeader("user_guid", deviceInfo.user_guid)
            client.addHeader("device_id", deviceInfo.deviceId)
            client.addHeader("user_id", userId)
            client.addHeader("ratio", "original")
            client.addHeader("Content-Type", "application/octet-stream")

            try {
                client.post(baseUrl, object : AsyncHttpResponseHandler() {
                    override fun onSuccess(
                        statusCode: Int,
                        headers: Array<Header>,
                        responseBody: ByteArray
                    ) {
                        try {

                            val image = BitmapFactory.decodeByteArray(
                                responseBody, 0,
                                responseBody.size
                            )

                            if (image != null) {
                                if (liveFeedPicture.cacheIdOriginal.isEmpty()) {
                                    val id = liveFeedPicture.postId + Util.randInt(11111, 99999)
                                    liveFeedPicture.cacheIdOriginal = id
                                }

                                ScreenReso.diskLruCache?.put(liveFeedPicture.cacheIdOriginal, image)
                            }

                            Glide.with(context).asBitmap().load(image)
                                .into(binding.ivAttachment)
                            binding.pbAttachment.visibility = View.GONE
                        } catch (arg: IllegalArgumentException) {
                            arg.printStackTrace()
                            binding.pbAttachment.visibility = View.GONE
                        }
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<Header>,
                        responseBody: ByteArray,
                        error: Throwable
                    ) {
                        Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.message)
                        binding.pbAttachment.visibility = View.GONE
                    }
                })
            } catch (iae: IllegalArgumentException) {
                iae.printStackTrace()
            }
        }
    }
}
