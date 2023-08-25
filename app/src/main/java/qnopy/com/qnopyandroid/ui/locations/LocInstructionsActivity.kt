package qnopy.com.qnopyandroid.ui.locations

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.clientmodel.GridImageItem
import qnopy.com.qnopyandroid.clientmodel.Location
import qnopy.com.qnopyandroid.clientmodel.LocationProfilePictures
import qnopy.com.qnopyandroid.customView.CustomTextView
import qnopy.com.qnopyandroid.databinding.ActivityLocInstructionsBinding
import qnopy.com.qnopyandroid.db.LocationProfilePictureDataSource
import qnopy.com.qnopyandroid.flowWithAdmin.utility.SubUrls
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.ui.activity.NotesImagesSlideShowActivity
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class LocInstructionsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var isSatellite: Boolean = false
    private var hasLatLngs: Boolean = false
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var binding: ActivityLocInstructionsBinding
    private val maxImageShowCount = 9
    private var margin = 0

    @Inject
    lateinit var locProfDataSource: LocationProfilePictureDataSource

    companion object {
        fun startLocInstrActivity(context: Context, location: Location) {
            val intent = Intent(context, LocInstructionsActivity::class.java)
            intent.putExtra(GlobalStrings.LOCATION_DETAILS, location)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocInstructionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        location =
            Utils.getSerializable(intent, GlobalStrings.LOCATION_DETAILS, Location::class.java)

        hasLatLngs = !((location.latitude.isNullOrEmpty() && location.latitude.isNullOrEmpty())
                || (location.latitude.toDouble() == 0.0 && location.latitude.toDouble() == 0.0))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = location.locationName
    }

    override fun onStart() {
        super.onStart()
        setUpUi()
    }

    private fun setUpUi() {
        if (!location.locInstruction.isNullOrEmpty())
            binding.tvLocInstruction.text = location.locInstruction

        addGoogleMap()
        setImageStack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun addGoogleMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (!hasLatLngs) {
            binding.tvNoLocationTagged.visibility = View.VISIBLE
            binding.fabSatellite.visibility = View.GONE
            binding.fabCurrentLocation.visibility = View.GONE
            googleMap.uiSettings.setAllGesturesEnabled(false)
        } else {
            animateCameraZoom()
        }

        binding.fabSatellite.apply {
            setOnClickListener {
                if (isSatellite) {
                    isSatellite = false
                    setImageDrawable(
                        VectorDrawableUtils
                            .getDrawable(
                                this@LocInstructionsActivity, R.drawable.ic_satellite,
                                R.color.black_faint
                            )
                    )
                    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                } else {
                    isSatellite = true
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
                    setImageDrawable(
                        VectorDrawableUtils
                            .getDrawable(
                                this@LocInstructionsActivity, R.drawable.ic_satellite,
                                R.color.qnopy_splash
                            )
                    )
                }
            }
        }

        binding.fabCurrentLocation.setOnClickListener {
            animateCameraZoom()
        }
    }

    private fun animateCameraZoom() {
        val currentLoc = LatLng(location.latitude.toDouble(), location.longitude.toDouble())

        googleMap.addMarker(
            MarkerOptions()
                .position(currentLoc)
                .title(location.locationName)
        )

        val zoom = CameraUpdateFactory.newLatLngZoom(currentLoc, 15f)
        googleMap.animateCamera(zoom)
    }

    private fun setImageStack() {

        val filePathList: ArrayList<LocationProfilePictures> =
            locProfDataSource.getAllProfilePictures(location.locationID)

        val imagesList = java.util.ArrayList<GridImageItem>()
        for (i in filePathList.indices) {
            val path = filePathList[i]
            var attachmentUrl = ""
            var thumbUrl = ""

            //checking if attachment id is negative then it is local attachment else from server
            if (path.attachmentId > 0) {
                attachmentUrl = (getString(R.string.prod_base_uri)
                        + SubUrls.URL_DOWNLOAD_PDF + "?file=" + path.thumbnailURL)
                thumbUrl = attachmentUrl
            } else {
                attachmentUrl = path.attachmentURL
                thumbUrl = attachmentUrl
            }

            //thumb url will be used to show images in stack images and attachmentUrl will download original image
            if (i <= maxImageShowCount) addImageStack(
                thumbUrl,
                i
            ) else addImageStack(thumbUrl, filePathList.size)
            imagesList.add(GridImageItem(attachmentUrl))
        }
        binding.llProfilePics.setOnClickListener {
            val intent = Intent(this, NotesImagesSlideShowActivity::class.java)
            intent.putExtra(GlobalStrings.PATH_LIST, imagesList)
            intent.putExtra(GlobalStrings.POSITION, 0)
            startActivity(intent)
        }
    }

    private fun addImageStack(
        filePath: String,
        pos: Int
    ) {

        //showing 10 images only and later part will be blank image with further image count
        if (pos <= maxImageShowCount) {

            //below is left margin for each imageView in layout to overlap each other like stack
            if (margin == 0) {
                margin = 30
            } else {
                margin += 20
            }
            val lp = RelativeLayout.LayoutParams(
                Util.dpToPx(33),
                Util.dpToPx(33)
            )
            lp.setMargins(Util.dpToPx(margin), 0, 0, 0)
            val circularImageView = CircularImageView(this)
            circularImageView.layoutParams = lp
            circularImageView.borderWidth = Util.dpToPx(1).toFloat()
            circularImageView.borderColor = ContextCompat.getColor(
                this,
                R.color.light_grey
            )
            loadImages(filePath, circularImageView)
            binding.llProfilePics.addView(circularImageView)
        } else {
            margin += 20
            val lp = RelativeLayout.LayoutParams(
                Util.dpToPx(34),
                Util.dpToPx(34)
            )
            lp.setMargins(Util.dpToPx(margin), 0, 0, 0)
            //                lp.addRule(RelativeLayout.CENTER_VERTICAL);
            val circularTextView = CustomTextView(this)
            circularTextView.layoutParams = lp
            circularTextView.background = ContextCompat.getDrawable(
                this,
                R.drawable.circle_grey_bg
            )
            circularTextView.gravity = Gravity.CENTER
            circularTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            circularTextView.textSize = Util.dpToPx(5).toFloat()
            val imageCount = "+" + (pos - maxImageShowCount)
            circularTextView.text = imageCount
            binding.llProfilePics.addView(circularTextView)
        }
    }

    private fun loadImages(filePath: String, circularImageView: CircularImageView) {
        val uID = Util.getSharedPreferencesProperty(this, GlobalStrings.USERID)
        val ob = DeviceInfo.getDeviceInfo(this)
        val file = File(filePath)
        if (file.exists()) {
            Picasso.get().load(file)
                .into(circularImageView)
        } else {
            Glide.with(this).asGif().load(
                R.drawable.loader
            )
                .into(circularImageView)
            val client = AsyncHttpClient()
            client.addHeader("user_guid", ob.user_guid)
            client.addHeader("device_id", ob.deviceId)
            client.addHeader("user_id", uID)
            client.addHeader("ratio", "original")
            client.addHeader("Content-Type", "application/octet-stream")
            try {
                client.post(filePath, object : AsyncHttpResponseHandler() {
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
                            Glide.with(this@LocInstructionsActivity).asBitmap().load(image)
                                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(circularImageView)
                        } catch (arg: IllegalArgumentException) {
                            arg.printStackTrace()
                        }
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<Header>,
                        responseBody: ByteArray,
                        error: Throwable
                    ) {
                        Log.e("imageHttp", "onFailure: " + statusCode + " error:- " + error.message)
                        //                        holder.pbAttachment.setVisibility(View.GONE);
                    }
                })
            } catch (iae: IllegalArgumentException) {
                iae.printStackTrace()
            }
        }
    }
}