package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.ScreenReso
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel
import qnopy.com.qnopyandroid.databinding.ItemPagerLiveFeedBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls
import qnopy.com.qnopyandroid.ui.task.AttachmentSlideShowActivity
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.Util

class FeedImagesAdapter(
    private val context: AppCompatActivity,
    private val listFeedImages: ArrayList<LiveFeedResponse.LiveFeedPictureData>,
    private val postId: String?
) : RecyclerView.Adapter<FeedImagesAdapter.ViewHolder>() {

    var userId: String = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID)
    var deviceInfo: DeviceInfoModel = DeviceInfo.getDeviceInfo(context)

    inner class ViewHolder(val binding: ItemPagerLiveFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(liveFeedPicture: LiveFeedResponse.LiveFeedPictureData) = with(binding) {
            tvNotes.text = liveFeedPicture.notes
            pbFeedImages.visibility = View.GONE

            ivFeed.setOnClickListener {
                val intent = Intent(context, AttachmentSlideShowActivity::class.java)
                intent.putParcelableArrayListExtra(
                    GlobalStrings.FEED_PICS_LIST,
                    listFeedImages
                )
                context.startActivity(intent)
            }

            setImage(liveFeedPicture, binding)
        }
    }

    private fun setImage(
        liveFeedPicture: LiveFeedResponse.LiveFeedPictureData,
        binding: ItemPagerLiveFeedBinding
    ) {
        //removing == from key as disk cache validation don't allow equals
        val bitmap = try {
            ScreenReso.diskLruCache.getBitmap(
                liveFeedPicture.cacheIdThumb
            )
        } catch (e: Exception) {
            null
        }

        if (bitmap != null) {
            binding.pbFeedImages.visibility = View.GONE
            Glide.with(context).asBitmap().load(bitmap)
                .into(binding.ivFeed)
        } else {

            val baseUrl: String = (context.getString(R.string.prod_base_uri)
                    + SubUrls.URL_DOWNLOAD_PDF + "?file=" + liveFeedPicture.fileKeyImageEncode)
           /* baseUrl =
                "https://staging.qnopy.com/qnopyserviceordering/secure/order/download/file?file=c3RhZ2luZy1mZXRjaGZvcm1zLWZpZWxkLWRhdGE6Njg3ODAyLWZpZWxkLWRhdGEtMTY3ODcwODEyMzg2OTUzNDgzMg=="
*/
            binding.pbFeedImages.visibility = View.VISIBLE

            val client = AsyncHttpClient()
            client.addHeader("user_guid", deviceInfo.user_guid)
            client.addHeader("device_id", deviceInfo.deviceId)
            client.addHeader("ratio", "original")
            client.addHeader("user_id", userId)
            client.addHeader("Content-Type", "application/octet-stream")

            /*client.addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZR1QwYXhOX0YteXl3d1FiX0FqV1lIVnh3cjlfSXVxOEpraTZtYW9jOHMwIn0.eyJleHAiOjE2ODA1Mjg0MzYsImlhdCI6MTY4MDUyNDgzNiwianRpIjoiZTRhODQ1OWItM2RmYi00NzE0LThmODktNjQ5NmU4ODhmM2M4IiwiaXNzIjoiaHR0cHM6Ly9zdGFnaW5nLnFub3B5LmNvbS9hdXRoL3JlYWxtcy9xbm9weW9yZGVyaW5nIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImM0MTExNmQ2LTczMTktNDYxZi1hZDVhLWEyMmFiODFmN2NiMCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLXJlc3QtY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjRhN2YxZTdjLTY3ZWQtNDc0Yy1iNGE4LWQyMDE4ZmVmYjQ0YiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXFub3B5b3JkZXJpbmciLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjRhN2YxZTdjLTY3ZWQtNDc0Yy1iNGE4LWQyMDE4ZmVmYjQ0YiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiSGVtYW50IEt1bWJoYXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJoZW1hbnQua3VtYmhhciIsImdpdmVuX25hbWUiOiJIZW1hbnQiLCJmYW1pbHlfbmFtZSI6Ikt1bWJoYXIiLCJlbWFpbCI6ImhlbWFudC5rdW1iaGFyQGdtYWlsLmNvbSJ9.HqWlrrK3btTNJuNCl6ZItCOXUSmTlllmLP22OOjf5HPFFTH1-R3JiSIoYpAaulg298h4VORciV9Sb7rTxBloXwkR5gulNzzYmjKTAVU3HCJDtyd3nbMPhn5Pwz4kaZJdnjE9-MauKPo00BxIl1EuGzJU9ht6-OZEPaGBz6h9FRjgBrGMcD1yi-u5JYWuA2HmQSmGiEZzOKltzsshTVlwWnkYwJts1wCuqRP_37fKIP9sUl59FV0hggidsPmm9VOD5gALVyO3CWW9-zJ5rWTyu9_bp8z8cslLj-fKN-yT83InJ3MUFuQwNeriVfYTPagNYnhPxpAatFAq8wlzRT-6vw"
            )
            client.addHeader("realm", "qnopyordering")
*/
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
                                if (liveFeedPicture.cacheIdThumb.isEmpty()) {
                                    val id = postId + Util.randInt(11111, 99999)
                                    liveFeedPicture.cacheIdThumb = id
                                }

                                ScreenReso.diskLruCache?.put(liveFeedPicture.cacheIdThumb, image)
                            }

                            Glide.with(context).asBitmap().load(image)
                                .into(binding.ivFeed)
                            binding.pbFeedImages.visibility = View.GONE
                        } catch (arg: IllegalArgumentException) {
                            arg.printStackTrace()
                            binding.pbFeedImages.visibility = View.GONE
                        }
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<Header>,
                        responseBody: ByteArray,
                        error: Throwable
                    ) {
                        Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.message)
                        binding.pbFeedImages.visibility = View.GONE
                    }
                })
            } catch (iae: IllegalArgumentException) {
                iae.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPagerLiveFeedBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listFeedImages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFeedImages[position])
    }
}