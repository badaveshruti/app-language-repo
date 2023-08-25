/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qnopy.com.qnopyandroid.photogallery;


import android.content.Context;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import qnopy.com.qnopyandroid.clientmodel.GalleryItem;
import qnopy.com.qnopyandroid.db.AttachmentDataSource;

/**
 * Some simple test data to use for this sample app.
 */
public class Images {

    /**
     * This are PicasaWeb URLs and could potentially change. Ideally the PicasaWeb API should be
     * used to fetch the URLs.
     * <p/>
     * Credit to Romain Guy for the photos:
     * http://www.curious-creature.org/
     * https://plus.google.com/109538161516040592207/about
     * http://www.flickr.com/photos/romainguy
     */
    int siteID, mobApp = 0;

    AttachmentDataSource ads;
    List<GalleryItem> list = null;
    public String[] imageUrls = null;

//	public String[] imageThumbUrls = getImageUrl();

    public Images(Context context, int siteID, String LocID, String eventID, String siteName, String locationName, int mobapp) {
        this.siteID = siteID;
        this.mobApp = mobapp;
        ads = new AttachmentDataSource(context);
//		List<Attachment> list = ads.getAttachmentDataListFromDB(DataForSync.DataNotSynced);
        list = ads.getCardAttachmentDataListFromDB(AttachmentDataSource.DataForSync.DataNotSynced, siteID + "",
                eventID, LocID, siteName, locationName, mobapp + "");
        Collections.sort(list, new CustomComparator());
        imageUrls = getImageUrl();
    }

    public String[] getImageUrl() {

        String[] tempThumbIds = new String[list.size()];
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                GalleryItem attach = list.get(i);
                if (attach.getFileLocation() != null)
                    tempThumbIds[i] = attach.getFileLocation();
                else if (attach.getFileKeyEncode() != null)
                    tempThumbIds[i] = attach.getFileKeyEncode();

                System.out.println("Image..." + tempThumbIds[i]);
            }
        } else {
//			Toast.makeText(getApplicationContext(), "No Images", Toast.LENGTH_LONG).show();
        }
        return tempThumbIds;
    }


    public class CustomComparator implements Comparator<GalleryItem> {

        @Override
        public int compare(GalleryItem lhs, GalleryItem rhs) {

            if (lhs.getTxtDate() != null && rhs.getTxtDate() != null) {

                Long date1 = Long.parseLong(lhs.getTxtDate());
                Long date2 = Long.parseLong(rhs.getTxtDate());

                int count = date2.compareTo(date1);
                return count;

            }
            return 0;
        }
    }


}
