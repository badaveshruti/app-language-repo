package qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiServiceImpl
import qnopy.com.qnopyandroid.flowWithAdmin.ui.adminModule.liveFeed.model.LiveFeedResponse
import qnopy.com.qnopyandroid.util.Util
import java.util.regex.Pattern
import javax.inject.Inject

class LiveFeedRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    fun getLiveFeed(siteId: String, lastSyncDate: String): Flow<LiveFeedResponse> = flow {
        val response = apiServiceImpl.getLiveFeed(siteId, lastSyncDate)

        //24/03/23 Note: from web end api has issues as thumbkey key will get original keys and fileKey will have thumb keys
        response.data?.let {
            for (liveFeed in response.data.liveFeedList) {
                val listLiveFeedPicsDataList = ArrayList<LiveFeedResponse.LiveFeedPictureData>()

                val listFileKeyImageEncode = ArrayList(
                    Util.splitStringToArray(",", liveFeed.fileKeyImageEncode)
                )
                val listThumbKeyEncode = ArrayList(
                    Util.splitStringToArray(",", liveFeed.fileKeyThumbImageEncode)
                )

                var listNotes = ArrayList<String>()

                //extracting notes from last element formatted in key***~~|note|~~|<empty>|~~
                if (listThumbKeyEncode.size > 0) {
                    val lastElement = listThumbKeyEncode[listThumbKeyEncode.size - 1]
                    if (!lastElement.isNullOrEmpty()) {
                        val splitList = Util.splitStringToArray("\\*\\*\\*", lastElement)
                        if (splitList.size > 0) {
                            try {
                                listThumbKeyEncode[listThumbKeyEncode.size - 1] = splitList[0]
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            //extracting notes from second element
                            if (splitList.size == 2) {
                                var patternComments =
                                    splitList[1].replace("||", "")
                                        .replace("~~", "~")
                                patternComments = patternComments.replaceFirst("~", "")
                                patternComments = patternComments.replaceAfterLast("~", "")

                                listNotes = ArrayList(
                                    Util.splitStringToArray(
                                        "~",
                                        patternComments
                                    )
                                )
                            }
                        }
                    }

                    for (index in listThumbKeyEncode.indices) {
                        try {
                            val notes = try {
                                if (listNotes.isEmpty())
                                    "N/A"
                                else
                                    listNotes[index]
                            } catch (e: Exception) {
                                "N/A"
                            }

                            val cacheIdThumb = liveFeed.postId + Util.randInt(11111, 99999)
                            val cacheIdOriginal = liveFeed.postId + Util.randInt(11111, 99999)

                            val liveFeedPicData = LiveFeedResponse.LiveFeedPictureData(
                                listFileKeyImageEncode[index],
                                listThumbKeyEncode[index],
                                notes,
                                cacheIdThumb,
                                cacheIdOriginal,
                                liveFeed.postId.toString()
                            )
                            listLiveFeedPicsDataList.add(liveFeedPicData)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    liveFeed.liveFeedPicsDataList = listLiveFeedPicsDataList
                }
            }
        }

        emit(response)
    }.flowOn(Dispatchers.IO)

    private fun getNotesList(expression: String?): ArrayList<String?> {
        var expr = expression
        if (expr!!.contains("~")) {
            expr = expr.replace("||", "")
            val replacement = HashMap<String, String>()
            val pattern = "\\~\\d+\\~"
            val p = Pattern.compile(pattern)
            val m = p.matcher(expr)

            val listNotes = ArrayList<String?>()
            while (m.find()) {
                val item = expr.substring(m.start(), m.end())

                val value =
                    item.substring(item.indexOf("~") + 1, item.lastIndexOf("~"))

                listNotes.add(value)
//                replacement[item] = value
            }
            /*  if (replacement.size > 0) {
                  for (key in replacement.keys) {
                      val value = replacement[key]
                      expression = if (value != null && value.isNotEmpty()) {
                          expression!!.replace(key, value)
                      } else {
                          return ""
                      }
                  }
              }*/
            return listNotes
        }
        return ArrayList()
    }
}