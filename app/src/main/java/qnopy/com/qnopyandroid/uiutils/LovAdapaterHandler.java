package qnopy.com.qnopyandroid.uiutils;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.clientmodel.LovItems;
import qnopy.com.qnopyandroid.db.LovDataSource;
import qnopy.com.qnopyandroid.util.Util;

public class LovAdapaterHandler {
     Context mContext;
    String mSiteID,mCompanyID;


    public LovAdapaterHandler(Context context) {
        mContext = context;

        mSiteID=Util.getSharedPreferencesProperty(mContext,GlobalStrings.CURRENT_SITEID);
        mCompanyID=Util.getSharedPreferencesProperty(mContext,GlobalStrings.COMPANYID);
    }

//    public LinkedHashMap<String, String> getNameValueLov(int lovID) {
//
//        MetaDataSource metaData = new MetaDataSource(mContext);
//        LinkedHashMap<String, String> lovNameValue = new LinkedHashMap<String, String>();
//        List<LovItems> LovValues = metaData.getLovItemValues(lovID);
//        System.out.println("hehehehe8 " + LovValues);
//        if (LovValues != null) {
//            long d = System.currentTimeMillis();
//            for (int i = 0; i < LovValues.size(); i++) {
//
//                lovNameValue.put(LovValues.get(i).item_value, LovValues.get(i).getItemDisplayName());
//
//                System.out.println("hehehehe10 " + lovNameValue);
//            }
//            long d1 = System.currentTimeMillis() - d;
//            System.out.println("timetaken11... " + d1);
//        }
//        return lovNameValue;
//    }

    public ArrayList<String> getLovNames(int lovID,int parent_lov_itemID) {

        LovDataSource lData = new LovDataSource(mContext);

        int compID = Integer.parseInt(mCompanyID==null?""+0 :mCompanyID);
        int siteID = Integer.parseInt(mSiteID==null?""+0 :mSiteID);
        ArrayList<String> lovNames = new ArrayList<String>();

        List<LovItems> LovValues = lData.getLovItemValues(lovID,siteID,compID,parent_lov_itemID);

        if (LovValues != null) {
            for (int i = 0; i < LovValues.size(); i++) {
                lovNames.add(LovValues.get(i).getItemDisplayName());
            }
        }

        return lovNames;
    }

    public String getLValueForLKey(int lovID, String Lovkey) {

        int compID = Integer.parseInt(mCompanyID==null?""+0 :mCompanyID);
        int siteID = Integer.parseInt(mSiteID==null?""+0 :mSiteID);


        LovDataSource lData = new LovDataSource(mContext);
        String val =lData.getLovValueForKey(lovID, Lovkey);
        return val;
    }

    public String getLKeyforLValue(int lovID, String value) {
        int siteID = Integer.parseInt(mSiteID==null?""+0 :mSiteID);

        LovDataSource lovData = new LovDataSource(mContext);
        String key = lovData.getKeyForLovValue(lovID, value,siteID+"");
        return key;
    }



}
