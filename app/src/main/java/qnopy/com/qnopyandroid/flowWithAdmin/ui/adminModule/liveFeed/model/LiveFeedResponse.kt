package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model

import android.os.Parcel
import android.os.Parcelable

data class LiveFeedResponse(
    val data: Data?,
    val message: String,
    val responseCode: String,
    val success: Boolean
) : java.io.Serializable {

    data class Data(
        val lastSyncDate: Long,
        val liveFeedList: ArrayList<LiveFeed>
    )

    data class LiveFeed(
        val creationDate: Long,
        val eventId: Int,
        val eventName: String?,
        val fileKeyImageEncode: String?,
        val fileKeyThumbImageEncode: String?,
        val postId: String?,
        val postText: String?,
        val postType: String?,
        val siteId: Int,
        val status: Int,
        var liveFeedPicsDataList: ArrayList<LiveFeedPictureData>?
    )

    //created for my convenience as fileKeyThumbImageEncode will have all keys
    //along with notes for each image separated by this format keysCommaSepd***~~|note|~~|<empty>|~~
    data class LiveFeedPictureData(
        val fileKeyImageEncode: String = "",
        val fileKeyThumbImageEncode: String = "",
        val notes: String,
        var cacheIdThumb: String = "",
        var cacheIdOriginal: String = "",
        var postId: String = ""
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(fileKeyImageEncode)
            parcel.writeString(fileKeyThumbImageEncode)
            parcel.writeString(notes)
            parcel.writeString(cacheIdThumb)
            parcel.writeString(cacheIdOriginal)
            parcel.writeString(postId)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LiveFeedPictureData> {
            override fun createFromParcel(parcel: Parcel): LiveFeedPictureData {
                return LiveFeedPictureData(parcel)
            }

            override fun newArray(size: Int): Array<LiveFeedPictureData?> {
                return arrayOfNulls(size)
            }
        }
    }
}

