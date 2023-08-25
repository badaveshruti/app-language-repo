package qnopy.com.qnopyandroid.restfullib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.logging.HttpLoggingInterceptor;
import qnopy.com.qnopyandroid.BuildConfig;
import qnopy.com.qnopyandroid.GlobalStrings;
import qnopy.com.qnopyandroid.TaskClasses.AttachmentTaskResponseModel;
import qnopy.com.qnopyandroid.clientmodel.ConstructionBitmapCaptionDataModel;
import qnopy.com.qnopyandroid.clientmodel.ConstructionMediaDataModel;
import qnopy.com.qnopyandroid.clientmodel.DataSyncRequest;
import qnopy.com.qnopyandroid.clientmodel.DownloadEventDataResponse;
import qnopy.com.qnopyandroid.clientmodel.PrintCOCByDatesResponse;
import qnopy.com.qnopyandroid.clientmodel.DeviceInfoModel;
import qnopy.com.qnopyandroid.clientmodel.metaForms.FormsData;
import qnopy.com.qnopyandroid.clientmodel.metaForms.MetaFormsJsonResponse;
import qnopy.com.qnopyandroid.db.CocMasterDataSource;
import qnopy.com.qnopyandroid.flowWithAdmin.ui.generateReportById.FetchAllReportByIdResponse;
import qnopy.com.qnopyandroid.photogallery.DiskLruCache;
import qnopy.com.qnopyandroid.requestmodel.DEvent;
import qnopy.com.qnopyandroid.requestmodel.RUnitConverter;
import qnopy.com.qnopyandroid.requestmodel.SCocMaster;
import qnopy.com.qnopyandroid.requestmodel.SLocation;
import qnopy.com.qnopyandroid.requestmodel.SSite;
import qnopy.com.qnopyandroid.requestmodel.SSiteUserRole;
import qnopy.com.qnopyandroid.requestmodel.SiteModel;
import qnopy.com.qnopyandroid.responsemodel.ActivationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.ActivationResponseModelV4;
import qnopy.com.qnopyandroid.responsemodel.AttachmentQnoteResponseModel;
import qnopy.com.qnopyandroid.responsemodel.AttachmentResponseModel;
import qnopy.com.qnopyandroid.responsemodel.CocObjectModel;
import qnopy.com.qnopyandroid.responsemodel.CocResponseModel;
import qnopy.com.qnopyandroid.responsemodel.DeviceUpdateResponseModel;
import qnopy.com.qnopyandroid.responsemodel.DownloadDataResponseModel;
import qnopy.com.qnopyandroid.responsemodel.EventResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FieldDataSyncStaging;
import qnopy.com.qnopyandroid.responsemodel.FielddataResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FileFolderResponseModel;
import qnopy.com.qnopyandroid.responsemodel.FileResponseModel;
import qnopy.com.qnopyandroid.responsemodel.JsonCocDetailsObjectModel;
import qnopy.com.qnopyandroid.responsemodel.LoginResponseModelV4;
import qnopy.com.qnopyandroid.responsemodel.MetaSyncConstructionResponseModel;
import qnopy.com.qnopyandroid.responsemodel.MetaSyncResponseModel;
import qnopy.com.qnopyandroid.responsemodel.MobileReportRequiredResponseCollector;
import qnopy.com.qnopyandroid.responsemodel.NewClientLocation;
import qnopy.com.qnopyandroid.responsemodel.NewLocationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.PreferenceMappingResponse;
import qnopy.com.qnopyandroid.responsemodel.RegistrationResponseModel;
import qnopy.com.qnopyandroid.responsemodel.SubmittalResponseCollector;
import qnopy.com.qnopyandroid.responsemodel.TaskDataResponse;
import qnopy.com.qnopyandroid.responsemodel.TaskSyncResponse;
import qnopy.com.qnopyandroid.responsemodel.VerifyEmailResponseModel;
import qnopy.com.qnopyandroid.responsemodel.WorkOrderResponseModel;
import qnopy.com.qnopyandroid.responsemodel.newFormLabelResponse;
import qnopy.com.qnopyandroid.responsemodel.newLabelResponseModel;
import qnopy.com.qnopyandroid.responsemodel.newLocPercentageResponseModel;
import qnopy.com.qnopyandroid.responsemodel.newLovData;
import qnopy.com.qnopyandroid.responsemodel.newLovResponseModel;
import qnopy.com.qnopyandroid.responsemodel.newSiteUserResponseModel;
import qnopy.com.qnopyandroid.responsemodel.updateUserLocationResponse;
import qnopy.com.qnopyandroid.services.MyFirebaseMessagingService;
import qnopy.com.qnopyandroid.ui.activity.AddsiteResponseModel;
import qnopy.com.qnopyandroid.util.AquaBlueServiceException;
import qnopy.com.qnopyandroid.util.DeviceInfo;
import qnopy.com.qnopyandroid.util.FileUtils;
import qnopy.com.qnopyandroid.util.MyClientHttpRequestInterceptor;
import qnopy.com.qnopyandroid.util.Util;

//import com.bumptech.glide.disklrucache.DiskLruCache;
//import com.bumptech.glide.load.data.BufferedOutputStream;

/**
 * Class implements REST methods
 * Allows CRUD operations
 */
@Singleton
public class AquaBlueServiceImpl implements AquaBlueService {

    // Define Class member
    HttpStatus errorCode = null;
    String mResponseMesssage = null;
    String mResponseCode = null;
    String mResponseBody = null;
    HttpHeaders mRequestHeaders = null;
    List<MediaType> mAceptableMediaTypes = null;
    HttpEntity<?> mRequestEntity = null;
    RestTemplate mRestTemplate = null;
    ResponseEntity<?> mRespEntity = null;
    private Context context;
    private SSLSocketFactory savedFactory = null;
    ResponseEntity<InputStreamResource> fileRespEntity = null;

    // Define log tag
    protected static final String TAG = AquaBlueServiceImpl.class.getSimpleName();
    public static final String FILE_TO_UPLOAD = "/data/data/com.aquablue.client/QuizCD.txt";

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final boolean LOG_CACHE_OPERATIONS = false;

    @Inject
    public AquaBlueServiceImpl(Context context) {
        this.context = context;
    }

    // Local method to initializes all class members
    void initializeClassMembers() {
        mRequestHeaders = null;
        mAceptableMediaTypes = null;
        mRequestEntity = null;
        mRestTemplate = null;
        mRespEntity = null;
        mResponseCode = null;
    }// end of initalizeClassMembers

    // Return the response code
    public String getResponseCode() {
        return mResponseCode;
    }

    public String getResponseBody() {
        return mResponseBody;
    }

//    public String getmResponseMesssage() {
//        return mResponseMesssage;
//    }
//
//    public HttpStatus getmErrorCode() {
//        return errorCode;
//    }

    // Local method to build Rest
    void buildRestRequest(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending a JSON object i.e. "application/json"
        mRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);
        } else { //to accommodate for close event which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        enableSSL();

        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(120 * 1000);

        mRestTemplate.setRequestFactory(requestFactory);

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }// end of buildRestRequest

    @Override
    public LoginResponseModelV4 checkLogin(String strBaseUrl, String strResource, String strName, String strPwd) {

        String strFinal = null;
//        Boolean retVal = false;
//        SUser result = null;

        LoginResponseModelV4 responseModel = null;
        ObjectMapper mapper = new ObjectMapper();


        if (null == strBaseUrl || null == strResource || null == strPwd) {
            return null;
        }

//        // URL construction
        strFinal = strBaseUrl + strResource;
//        JSONObject params = new JSONObject();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        Log.e("userPass", "checkLogin: " + strName + " PASS- " + strPwd);
        try {
            map.add("userName", strName);
            map.add("password", strPwd);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Login Post request Error:" + e.getMessage());
        }
        System.out.println("checkLogin URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();
        // Build Rest request
        //buildRestRequestFormEncoded(map);
        buildRestRequestFormEncodedLogin(map);
        try {
            // Make the network request, posting the message, expecting a String in response from the server
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);


//            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.GET, mRequestEntity,
//                    SUser.class);
            // Save response code
            mResponseCode = mRespEntity.getStatusCode().toString();

            Log.i(TAG, " Login response code= " + mResponseCode);

//            responseModel = mapper.readValue(mRespEnt8ity.getBody().toString(), LoginResponseModel.class);
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), LoginResponseModelV4.class);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkLogin() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public PreferenceMappingResponse downloadAppPreferences(String strBaseUrl, String strResource, String uGuid) {

        String strFinal = null;

        PreferenceMappingResponse responseModel = null;

        if (null == strBaseUrl || null == strResource) {
            return null;
        }

//        // URL construction
        strFinal = strBaseUrl + strResource;
//        JSONObject params = new JSONObject();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userGuid", uGuid);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "App Preferences Download request Error:" + e.getMessage());
        }
        System.out.println("downloadAppPreferences URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();
        // Build Rest request
        buildRestRequestFormEncoded(map);

        try {
            // Make the network request, posting the message, expecting a String in response from the server
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();

            Log.i(TAG, " downloadAppPreferences response code= " + mResponseCode);

//            responseModel = mapper.readValue(mRespEntity.getBody().toString(), LoginResponseModel.class);
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), PreferenceMappingResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "downloadAppPreferences() Error:" + e.getMessage());
            Log.e(TAG, "downloadAppPreferences() Error");
            return null;
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public JsonCocDetailsObjectModel syncCoc(String strBaseUrl, String strResource, List<CocObjectModel> cData, String guid) {

        String strFinal = null;
        JsonCocDetailsObjectModel responseModel = null;
        if (null == strBaseUrl || null == strResource) {
            return responseModel;
        }

        // URL construction

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid;

        Log.i(TAG, " syncCoc URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(cData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), JsonCocDetailsObjectModel.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "syncCoc() Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "syncCoc()  Exception e1=" + e1.getMessage());
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public newLocPercentageResponseModel v1_setLocationPercentage(String strBaseUrl, String strResource,
                                                                  int parentAppIDWS, int siteidWS, String userGUIDWS, String deviceIDWS, String fromDateWS, String toDateWS, int eventID) {

        String strFinal = null;
        newLocPercentageResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();

        if (null == strBaseUrl || null == strResource) {
            return null;
        }

//        // URL construction
        strFinal = strBaseUrl + strResource;
//        JSONObject params = new JSONObject();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("rollIntoAppId", parentAppIDWS + "");
            map.add("siteId", siteidWS + "");
            map.add("userGuid", userGUIDWS + "");
            map.add("deviceId", deviceIDWS + "");
            map.add("fromDate", fromDateWS + "");
            map.add("toDate", toDateWS + "");
            map.add("eventId", eventID + "");


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "LocationPercentage Post request Error:" + e.getMessage());
        }
        System.out.println("checkLogin URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();
        // Build Rest request
        buildRestRequestFormEncoded(map);
        try {
            // Make the network request, posting the message, expecting a String in response from the server
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);
//            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.GET, mRequestEntity,
//                    SUser.class);
            // Save response code
            mResponseCode = mRespEntity.getStatusCode().toString();

            Log.i(TAG, " Login response code= " + mResponseCode);

//            responseModel = mapper.readValue(mRespEntity.getBody().toString(), LoginResponseModel.class);
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), newLocPercentageResponseModel.class);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "checkLogin() Error:" + e.getMessage());
            }
            Log.e(TAG, "checkLogin() Error");
            return null;
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    @Override
    public String changePassword(String strBaseUrl, String strResource, String strName,
                                 String oldPwd, String newPwd) {

        String strFinal = null;
        String result = null;
        Boolean retVal = false;

        if (null == strBaseUrl || null == strResource || null == strName ||
                null == oldPwd || null == newPwd) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource
                + "/" + strName
                + "/" + "changepassword?oldPassword=" + oldPwd
                + "&newPassword=" + newPwd;

        System.out.println("gggg" + " changePassword: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(strFinal);

        try {
            // Make the network request, posting the message, expecting a String in response from the server
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.PUT, mRequestEntity,
                    String.class);
        } catch (Exception e) {

        } finally {
            resetSSLFactory();
        }
        // Save response code
        mResponseCode = mRespEntity.getStatusCode().toString();

        Log.d(TAG, " RequestWithJson: response code= " + mResponseCode);

        if (mResponseCode.equals("200")) {
            retVal = true;
            result = mRespEntity.getBody().toString();
        }

        return result;
    }

    public MetaSyncConstructionResponseModel getConstructionMetaSyncData(String strBaseUrl, String strResource,
                                                                         String strName, String strPwd) {
        String strFinal = null;
        MetaSyncConstructionResponseModel retConstructionMetaSyncData = null;

        if (null == strBaseUrl || null == strResource || null == strName || null == strPwd) {
            return retConstructionMetaSyncData;
        }
        // URL construction
        strFinal = strBaseUrl + strResource;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userGuid", strName);
            jsonObject.put("password", strPwd);
            jsonObject.put("lastSync", 0);//"f8180e4a-3b36-11e5-9708-0ea7cb7cc776"

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
            return null;
        }

        Log.i(TAG, " getMetaSync URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequestwithStringHttpEntity(jsonObject);
        try {

            Log.i(TAG, "Request MetaSync Start Time:" + (System.currentTimeMillis()));

            //12/28/2017
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class);

            Log.i(TAG, "Request MetaSync End Time:" + (System.currentTimeMillis()));

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();
            Log.e("constructionA", "getMetaSyncData: " + mResponseBody);
            Log.i("constructionA", "Start Deserialize MetaSync Time:" + (System.currentTimeMillis()));
            retConstructionMetaSyncData = new Gson().fromJson(mResponseBody, MetaSyncConstructionResponseModel.class);
            Log.i("constructionA", "End Deserialize MetaSync Time:" + (System.currentTimeMillis()) + " " + retConstructionMetaSyncData);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sonawaneAbhishek", "getMetaSyncData() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }
        return retConstructionMetaSyncData;
    }

    public MetaSyncResponseModel getMetaSyncData(String strBaseUrl, String strResource,
                                                 String strName, String strPwd, String lastSyncDate) {

        String strFinal = null;
        MetaSyncResponseModel retMetaSyncData = null;

        if (null == strBaseUrl || null == strResource || null == strName || null == strPwd) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userGuid", strName);
            jsonObject.put("password", strPwd);
            jsonObject.put("lastSync", lastSyncDate);//"f8180e4a-3b36-11e5-9708-0ea7cb7cc776"

        } catch (JSONException e1) {
            e1.printStackTrace();
            Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
            return null;
        }

        Log.i(TAG, " getMetaSync URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequestwithStringHttpEntity(jsonObject);

        try {

            Log.i(TAG, "Request MetaSync Start Time:" + (System.currentTimeMillis()));

//            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
//                    String.class, jsonObject); 

            //12/28/2017
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class);

            Log.i(TAG, "Request MetaSync End Time:" + (System.currentTimeMillis()));

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();
            Log.e("normalMeta", "getMetaSyncData: " + mResponseBody);
            Log.i("normalMeta", "Start Deserialize MetaSync Time:" + (System.currentTimeMillis()));
            retMetaSyncData = new Gson().fromJson(mResponseBody, MetaSyncResponseModel.class);
            Log.i("normalMeta", "End Deserialize MetaSync Time:" + (System.currentTimeMillis()) + " " + retMetaSyncData);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getMetaSyncData() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return retMetaSyncData;
    }

    public MetaFormsJsonResponse getMetaJsonFormsData(String strBaseUrl, String strResource,
                                                      String userId, String lastSyncDate) {

        String strFinal = null;
        MetaFormsJsonResponse retMetaSyncData = null;

        if (null == strBaseUrl || null == strResource || null == userId) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userId", userId);
            jsonObject.put("lastFetchDate", lastSyncDate);
        } catch (JSONException e1) {
            e1.printStackTrace();
            Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
            return null;
        }

        Log.i(TAG, " getMetaFormJson URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequestwithStringHttpEntity(jsonObject);

        try {

            Log.i(TAG, "Request getMetaFormJson Start Time:" + (System.currentTimeMillis()));

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class);

            Log.i(TAG, "Request getMetaFormJson End Time:" + (System.currentTimeMillis()));

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();
            Log.e("normalMeta", "getMetaFormJson: " + mResponseBody);
            Log.i("normalMeta", "Start Deserialize MetaSync Time:" + (System.currentTimeMillis()));
            retMetaSyncData = new Gson().fromJson(mResponseBody, MetaFormsJsonResponse.class);
            Log.i("normalMeta", "End Deserialize MetaSync Time:"
                    + (System.currentTimeMillis()) + " " + retMetaSyncData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getMetaSyncData() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        if (retMetaSyncData.isSuccess() && retMetaSyncData.getData() != null) {
            for (MetaFormsJsonResponse.Forms forms : retMetaSyncData.getData().getForms()) {
                forms.setFormData(forms.getFormData());
                forms.setFormsDetails(new Gson().fromJson(forms.getFormData(), FormsData.class));
            }
        }

        return retMetaSyncData;
    }

    public AttachmentResponseModel upload_DB_toServer(String strBaseUrl, String strResource, MultiValueMap<String, Object> files) {
        Log.i(TAG, "upload_DB_toServer() IN TIME:" + System.currentTimeMillis());

        String strFinal = null;
        AttachmentResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == files) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        Log.d(TAG, "upload_DB_toServer() Send DB File: URL = " + strFinal);

        initializeClassMembers();
        // Initialize member variables

        // Build Rest request
        v1_buildRestRequestForFileUpload(files);

        try {
            Log.i(TAG, "upload_DB_toServer() call web api start time:" + System.currentTimeMillis());
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, AttachmentResponseModel.class, files);
            Log.i(TAG, "upload_DB_toServer() call web api end time:" + System.currentTimeMillis());

            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.i(TAG, "upload_DB_toServer() call web apiresponse code:" + mResponseCode);

            responseModel = (AttachmentResponseModel) mRespEntity.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "upload_DB_toServer() Error in fileUpload:" + e.getLocalizedMessage());
        } finally {
            resetSSLFactory();
        }
        Log.i(TAG, "upload_DB_toServer() OUT TIME:" + System.currentTimeMillis());

        return responseModel;

    }


    public ActivationResponseModel ActivateDevice(String strBaseUrl, String strResource,
                                                  String strName, String strPwd, String activationCode) {

        String strFinal = null;
        ActivationResponseModel retSyncData = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == strName || null == strPwd) {
            return retSyncData;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;


        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userName", strName);
            map.add("password", strPwd);
            map.add("activationCode", activationCode);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Login Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();

            retSyncData = new Gson().fromJson(mResponseBody, ActivationResponseModel.class);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "getMetaSyncData() Error:" + e.getMessage());
            }
            return null;
        } finally {
            resetSSLFactory();
        }


//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return retSyncData;
    }


    public ActivationResponseModelV4 ActivateDeviceV4(String strBaseUrl, String strResource,
                                                      String strName, String strPwd, String activationCode) {

        String strFinal = null;
        ActivationResponseModelV4 retSyncData = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == strName || null == strPwd) {
            return retSyncData;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userName", strName);
            map.add("password", strPwd);
            map.add("activationCode", activationCode);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Login Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();

            retSyncData = new Gson().fromJson(mResponseBody, ActivationResponseModelV4.class);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "getMetaSyncData() Error:" + e.getMessage());
            }
            return null;
        } finally {
            resetSSLFactory();
        }


//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return retSyncData;
    }

    public MobileReportRequiredResponseCollector getMobileReport(String strBaseUrl, String strResource,
                                                                 String userID, String formId, String siteId, String eventId) {

        String strFinal = null;
        MobileReportRequiredResponseCollector retSyncData = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == userID) {
            return retSyncData;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userId", userID);
            map.add("formId", formId);
            map.add("siteId", siteId);
            map.add("eventId", eventId);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getEventList Post request Error:" + e.getMessage());
        }


        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e(TAG, "getEventList Post request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();

            retSyncData = new Gson().fromJson(mResponseBody, MobileReportRequiredResponseCollector.class);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "getMetaSyncData() Error:" + e.getMessage());
            }
            return null;
        } finally {
            resetSSLFactory();
        }

        return retSyncData;
    }

    public String generateReport(String strBaseUrl, String strResource,
                                 String siteId, String eventId, String formId,
                                 String userId, boolean isForPM, boolean isPdf, boolean isForSelf) {

        String strFinal = null;
        String response = "false";
        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        try {
            map.add("userId", userId);
            map.add("formId", formId);//0
            map.add("siteId", siteId);
            map.add("eventId", eventId);
            if (isForPM) {
                map.add("isForPM", String.valueOf(true));

                //commented on 03 Apr, 21 to keep pdf format always
/*                if (isPdf)
                    map.add("format", "pdf");
                else
                    map.add("format", "doc");*/
            }
            map.add("format", "pdf");

            map.add("isForSelf", String.valueOf(isForSelf));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "GenerateReport Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e("generateReport", "generateReport Post request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.e("generateReport", "generateReport Post request URL:" + mResponseBody);
            JSONObject jsonObject = null;
            String message = null;
            try {
                jsonObject = new JSONObject(mResponseBody);
                String responseCode = jsonObject.getString("responseCode");
                message = jsonObject.getString("message");
                Log.e("generateReport", "generateReport Post request URL MESSAGE:" + message + "resCode- " + responseCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (mResponseCode.equals("200")) {
                response = message;
            } else {
                response = "false";
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("generateReport", "generateReport Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return response;
    }

    public FetchAllReportByIdResponse getAllReportsById(String strBaseUrl, String strResource,
                                                        String formId) {
        String strFinal = null;
        FetchAllReportByIdResponse retSyncData = null;
        if (null == strBaseUrl || null == strResource || null == formId) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("formId", formId);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllReportsById Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e(TAG, "getAllReportsById Post request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();

            retSyncData = new Gson().fromJson(mResponseBody, FetchAllReportByIdResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllReportsById() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return retSyncData;
    }


    public String generateReport(String strBaseUrl, String strResource,
                                 String siteId, String eventId, String formId,
                                 String userId, boolean isForPM, boolean isPdf, boolean isForSelf,
                                 String reportId, String reportName) {

        String strFinal = null;
        String response = "false";
        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        try {
            map.add("userId", userId);
            map.add("formId", formId);//0
            map.add("siteId", siteId);
            map.add("eventId", eventId);
            map.add("reportId", reportId);
            map.add("reportName", reportName);
            map.add("format", "pdf");
            map.add("isForSelf", String.valueOf(isForSelf));

            if (isForPM) map.add("isForPM", String.valueOf(true));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getEventList Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e("generateReport", "generateReport by report name request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.e("generateReport", "generateReport by report name request URL:" + mResponseBody);
            JSONObject jsonObject = null;
            String message = null;
            try {
                jsonObject = new JSONObject(mResponseBody);
                String responseCode = jsonObject.getString("responseCode");
                message = jsonObject.getString("message");
                Log.e("generateReport", "generateReport by report name request URL MESSAGE:" + message + "resCode- " + responseCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (mResponseCode.equals("200")) {
                response = message;
            } else {
                response = "false";
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("generateReport", "generateReport by report name Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return response;
    }

    public SubmittalResponseCollector getEventList(String strBaseUrl, String strResource,
                                                   String userID, String lastSyncDate) {
        String strFinal = null;
        SubmittalResponseCollector retSyncData = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == userID) {
            return retSyncData;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userId", userID);
            map.add("lastSyncDate", lastSyncDate);//0
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getEventList Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e(TAG, "getEventList Post request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();

            retSyncData = new Gson().fromJson(mResponseBody, SubmittalResponseCollector.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getMetaSyncData() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return retSyncData;
    }

    public FileFolderResponseModel getFileFolderSyncData(String strBaseUrl, String strResource,
                                                         String userGuid, String siteId) {

        String strFinal = null;
        FileFolderResponseModel retFileFolderSyncData = null;

        if (null == strBaseUrl || null == strResource || null == userGuid || null == siteId) {
            return retFileFolderSyncData;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userGuid", userGuid);
            map.add("siteId", siteId);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFileFolderSyncData Post request Error:" + e.getMessage());
        }
        Log.i(TAG, " getFileFolderSyncData URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
//        buildRestRequestwithStringHttpEntity(map);
        buildRestRequestFormEncoded(map);

        AquaBlueServiceException aquaBlueServiceException = new AquaBlueServiceException(context);

        try {
            // Make the network request

//            mRespEntity = mRestTemplate.postForEntity(strFinal,params,
//                    MetaSyncDataModel.class);
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    FileFolderResponseModel.class, map);

            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            retFileFolderSyncData = (FileFolderResponseModel) mRespEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getFileFolderSyncData() Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return retFileFolderSyncData;
    }

    public String downloadFile(String strBaseUrl, String strResource,
                               String userGuid, String siteId, String fileGuid, String filename) {

        String strFinal = null;
        InputStream inputStream = null;
        FileOutputStream fos = null;

        FileResponseModel retFileData = null;
        if (null == strBaseUrl || null == strResource || null == userGuid || null == siteId) {
            return "false";
        }

        // URL construction
        strFinal = strBaseUrl + strResource + "?userGuid=" + userGuid + "&fileName=" + filename + "&siteId=" + siteId + "&fileGuid=" + fileGuid;

//        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//
//        try {
//            map.add("userGuid", userGuid);
//            map.add("siteId", siteId);
//            map.add("fileGuid", fileGuid);
//            map.add("fileName", filename);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "downloadFile Post request Error:" + e.getMessage());
//            return false;
//        }


        Log.i(TAG, " getFileFolderSyncData URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequestforDownloadFile(null);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class);

            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            if (mResponseCode.equals("200")) {

                String outdir = Util.getFileFolderDirPath(context, siteId);

                if (outdir.isEmpty())
                    return "false";

//                FileOutputStream output = new FileOutputStream(new File(outdir));

                //  DataInputStream stream = new DataInputStream(fileRespEntity.getBody());
                int length = Integer.parseInt(mRespEntity.getHeaders().getContentLength() + "");
                inputStream = new ByteArrayInputStream(mResponseBody.getBytes());
                byte[] buffer = new byte[length];
                int read = 0;

                File dFile = new File(outdir, fileGuid);

                fos = new FileOutputStream(dFile);

                while ((read = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } else {
                return mResponseBody;
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "getFileFolderSyncData() Error:" + e.getMessage());
                return "false";
            }
        } finally {

            resetSSLFactory();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    // outputStream.flush();
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return "true";
    }

    /*//TODO QNOTE APP Image Download and stored in cache
    public ArrayList<ConstructionBitmapCaptionDataModel> QnoteImageDownload(String strBaseUrl, String strResource, String userGuid, String postId, String deviceId, String userId, ArrayList<ConstructionMediaDataModel> ArrayListMediaData) {
        ArrayList<ConstructionBitmapCaptionDataModel> arrayListBitmapCaption = new ArrayList<>();
        String fileName, fileKey, caption;
        String url = strBaseUrl + strResource;
        String res = "true";
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        // String outdir = Util.getFileFolderDirPathForPDF(context, siteId, eventID);

        File cacheDir = getDiskCacheDir(this, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);

        for (int i = 0; i < ArrayListMediaData.size(); i++){
            if (ArrayListMediaData.get(i).getDisplayFlag() == 1){
                fileName = ArrayListMediaData.get(i).getFileName();
                fileKey = ArrayListMediaData.get(i).getFileKey();
                caption = ArrayListMediaData.get(i).getCaption();

                //File outputFile = new File(context.getCacheDir(), fileKey+"_"+fileName);
                String imageKey = fileKey+"_"+fileName;
                boolean isContained;

                isContained = containsKey(imageKey);
                if (isContained){
                    ConstructionBitmapCaptionDataModel constructionBitmapCaptionDataModel = new ConstructionBitmapCaptionDataModel();
                    Bitmap bitmapCache = getBitmap(imageKey);

                    constructionBitmapCaptionDataModel.setmBitmap(bitmapCache);
                    constructionBitmapCaptionDataModel.setmCaption(caption);
                    arrayListBitmapCaption.add(constructionBitmapCaptionDataModel);

                }else {
                    try {
                        URL obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();


                        con.setRequestMethod("POST");
                        con.setRequestProperty("user_guid", userGuid);
                        con.setRequestProperty("device_id", deviceId);
                        con.setRequestProperty("user_id", userId);
                        con.setRequestProperty("file_key", fileKey);
                        con.setRequestProperty("ratio", "640*480");
                        con.setRequestProperty("Content-Type", "application/octet-stream");

                        String urlParameters = "fileKey=" + fileKey + "&fileName=" + fileName;
                        // Send post request
                        con.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(urlParameters);
                        wr.flush();
                        wr.close();

                        InputStream inputStream = con.getInputStream();

                        if (inputStream != null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;


                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                            *//*int Height = bitmap.getHeight();
                            int Width = bitmap.getWidth();

                            int newHeight = 1000;
                            float scaleFactor = ((float) newHeight) / Height;
                            float newWidth = Width * scaleFactor;

                            float scaleWidth = scaleFactor;
                            float scaleHeight = scaleFactor;
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth, scaleHeight);

                            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, Width, Height, matrix, true);
                            bitmap.recycle();*//*

                            addBitmapToCache(imageKey, bitmap);
                            Bitmap bitmapCache = getBitmap(imageKey);

                            ConstructionBitmapCaptionDataModel constructionBitmapCaptionDataModel = new ConstructionBitmapCaptionDataModel();
                            constructionBitmapCaptionDataModel.setmBitmap(bitmapCache);
                            constructionBitmapCaptionDataModel.setmCaption(caption);

                            arrayListBitmapCaption.add(constructionBitmapCaptionDataModel);
                            //return bitmap;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("QnoteImageException", "Exception in file Download:" + e.getMessage());
                        res = "false";
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                            if (fout != null) {
                                fout.close();
                            }
                            //res = "true";
                        } catch (IOException e) {
                            e.printStackTrace();
                            res = "false";
                        }
                    }
                }
            }
        }
        return arrayListBitmapCaption;
    }*/
    //QNOTE APP Image Download and stored in cache
    public ArrayList<ConstructionBitmapCaptionDataModel> QnoteImageDownload(String strBaseUrl, String strResource, String userGuid, String postId, String deviceId, String userId, ArrayList<ConstructionMediaDataModel> ArrayListMediaData, String ratio) {
        ArrayList<ConstructionBitmapCaptionDataModel> arrayListBitmapCaption = new ArrayList<>();
        String fileName, fileKey, caption;
        String url = strBaseUrl + strResource;
        String res = "true";
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        // String outdir = Util.getFileFolderDirPathForPDF(context, siteId, eventID);

        File cacheDir = getDiskCacheDir(this, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);

        for (int i = 0; i < ArrayListMediaData.size(); i++) {
            fileName = ArrayListMediaData.get(i).getFileName();
            fileKey = ArrayListMediaData.get(i).getFileKey();
            caption = ArrayListMediaData.get(i).getCaption();

            //File outputFile = new File(context.getCacheDir(), fileKey+"_"+fileName);
            String imageKey = fileKey + "_" + fileName;
            boolean isContained = false;
            if (ratio.equals("thumbnail")) {
                isContained = containsKey(imageKey);
                if (isContained) {
                    ConstructionBitmapCaptionDataModel constructionBitmapCaptionDataModel = new ConstructionBitmapCaptionDataModel();
                    Bitmap bitmapCache = getBitmap(imageKey);

                    constructionBitmapCaptionDataModel.setmBitmap(bitmapCache);
                    constructionBitmapCaptionDataModel.setmCaption(caption);
                    arrayListBitmapCaption.add(constructionBitmapCaptionDataModel);
                    Log.e("abhishekkk", "isContained: ratio- " + ratio + "/" + postId + " - " + userGuid + " - " + deviceId + " - " + userId + " - " + fileKey);
                } else {
                    try {
                        URL obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                        con.setRequestMethod("POST");
                        con.setRequestProperty("user_guid", userGuid);
                        con.setRequestProperty("device_id", deviceId);
                        con.setRequestProperty("user_id", userId);
                        con.setRequestProperty("file_key", fileKey);
                        con.setRequestProperty("ratio", "original");
                        con.setRequestProperty("Content-Type", "application/octet-stream");

                        Log.e("abhishekkk", "QnoteImageDownload: ratio- " + ratio + "/" + postId + " - " + userGuid + " - " + deviceId + " - " + userId + " - " + fileKey);

                        String urlParameters = "fileKey=" + fileKey + "&fileName=" + fileName;
                        // Send post request
                        con.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(urlParameters);
                        wr.flush();
                        wr.close();

                        InputStream inputStream = con.getInputStream();

                        if (inputStream != null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;


                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                            int Height = bitmap.getHeight();
                            int Width = bitmap.getWidth();

                            int newHeight = 1000;
                            float scaleFactor = ((float) newHeight) / Height;
                            float newWidth = Width * scaleFactor;

                            float scaleWidth = scaleFactor;
                            float scaleHeight = scaleFactor;
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth, scaleHeight);

                            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, Width, Height, matrix, true);
                            bitmap.recycle();

                            addBitmapToCache(imageKey, resizedBitmap);
                            Bitmap bitmapCache = getBitmap(imageKey);

                            ConstructionBitmapCaptionDataModel constructionBitmapCaptionDataModel = new ConstructionBitmapCaptionDataModel();
                            constructionBitmapCaptionDataModel.setmBitmap(bitmapCache);
                            constructionBitmapCaptionDataModel.setmCaption(caption);

                            arrayListBitmapCaption.add(constructionBitmapCaptionDataModel);
                            //return bitmap;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("QnoteImageException", "Exception in file Download:" + e.getMessage());
                        res = "false";
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                            if (fout != null) {
                                fout.close();
                            }
                            //res = "true";
                        } catch (IOException e) {
                            e.printStackTrace();
                            res = "false";
                        }
                    }
                }
            } else if (ratio.equals("original")) {
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("user_guid", userGuid);
                    con.setRequestProperty("device_id", deviceId);
                    con.setRequestProperty("user_id", userId);
                    con.setRequestProperty("file_key", fileKey);
                    con.setRequestProperty("ratio", "original");
                    con.setRequestProperty("Content-Type", "application/octet-stream");

                    Log.e("abhishekkk", "QnoteImageDownload: ratio- " + ratio + "/" + postId + " - " + userGuid + " - " + deviceId + " - " + userId + " - " + fileKey);

                    String urlParameters = "fileKey=" + fileKey + "&fileName=" + fileName;
                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    InputStream inputStream = con.getInputStream();

                    if (inputStream != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;


                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                        /*int Height = bitmap.getHeight();
                        int Width = bitmap.getWidth();

                        int newHeight = 1000;
                        float scaleFactor = ((float) newHeight) / Height;
                        float newWidth = Width * scaleFactor;

                        float scaleWidth = scaleFactor;
                        float scaleHeight = scaleFactor;
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);

                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, Width, Height, matrix, true);
                        bitmap.recycle();*/

                        ConstructionBitmapCaptionDataModel constructionBitmapCaptionDataModel = new ConstructionBitmapCaptionDataModel();
                        constructionBitmapCaptionDataModel.setmBitmap(bitmap);
                        constructionBitmapCaptionDataModel.setmCaption(caption);

                        arrayListBitmapCaption.add(constructionBitmapCaptionDataModel);
                        //return bitmap;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("QnoteImageException", "Exception in file Download:" + e.getMessage());
                    res = "false";
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (fout != null) {
                            fout.close();
                        }
                        //res = "true";
                    } catch (IOException e) {
                        e.printStackTrace();
                        res = "false";
                    }
                }
            }

        }
        return arrayListBitmapCaption;
    }


    // TODO Here all method starts, necessary for storing images bitmap in disk cache.
    public void clearCache() {
        if (LOG_CACHE_OPERATIONS) {
            Log.e("cacheDisk", "disk cache CLEARED");
        }
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeImage(String key) {
        try {
            mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsKey(String key) {
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get(key);
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;
    }

    private Bitmap getBitmap(String imageKey) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskLruCache.get(imageKey);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn = new BufferedInputStream(in, Util.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return bitmap;
    }

    private void addBitmapToCache(String imageKey, Bitmap bitmap) {
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(imageKey);
            if (editor == null) {
                return;
            }
            if (writeBitmapToFile(bitmap, editor)) {
                mDiskLruCache.flush();
                editor.commit();
                if (BuildConfig.DEBUG) {
                    Log.d("cache_test_DISK_", "image put on disk cache " + imageKey);
                }
            } else {
                editor.abort();
                if (BuildConfig.DEBUG) {
                    Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + imageKey);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + imageKey);
            }
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), Util.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getDiskCacheDir(AquaBlueServiceImpl aquaBlueService, String diskCacheSubdir) {

        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Util.isExternalStorageRemovable() ?
                        Util.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + diskCacheSubdir);
    }

    // todo async task for disk cache
    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... files) {
            synchronized (mDiskCacheLock) {
                File cacheDir = files[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    // TODO Here all methods end required
    public String FileDownloadPDF(String strBaseUrl, String strResource,
                                  String userGuid, String siteId, String eventID,
                                  String formID, String userId, String deviceID) {
        String url = strBaseUrl + strResource;
        String res = "true";
        String outdir = Util.getFileFolderDirPathForPDF(context, siteId, eventID);
        File outputFile = new File(outdir, "" + eventID + ".pdf");

        if (outdir.isEmpty())
            return "false";

        BufferedInputStream in = null;
        FileOutputStream fout = null;
        //deviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) context);

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("user_guid", userGuid);
            con.setRequestProperty("device_id", deviceID);
            con.setRequestProperty("user_id", userId);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String urlParameters = "userId=" + userId + "&formId=" + formID + "&siteId=" + siteId + "&eventId=" + eventID;
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            Log.i("PDFRES", "Sending 'POST' FileDownload request to URL : " + url);
            //Log.i("PDFRES", "Post parameters : " + urlParameters);
            Log.i("PDFRES", "Response Code : " + responseCode + " " + responseMessage);
            if (responseCode == 200) {

                in = new BufferedInputStream(con.getInputStream());
                fout = new FileOutputStream(outputFile);
                byte data[] = new byte[4096];
                int count;
                while ((count = in.read(data)) != -1) {
                    fout.write(data, 0, count);
                }
                String s = new String(data);
                if (s.contains("NON_AUTHORITATIVE_INFORMATION")) {
                    res = "false";
                } else {
                    res = "true";
                }
                Log.i("aaaa", "Bufffer File Write:" + new String(data) + "response" + res + "responseCode--" + responseCode + "count" + count);

            } else {
                res = con.getResponseMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in file Download:" + e.getMessage());
            res = "false";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
                //res = "true";
            } catch (IOException e) {
                e.printStackTrace();
                res = "false";
            }
        }

        return res;
    }

    public PrintCOCByDatesResponse printCOCByDates(String strBaseUrl, String strResource,
                                                   String eventID, String selectedDates) {
        DeviceInfoModel ob = DeviceInfo.getDeviceInfo(context);
        String userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);

        String strFinal = null;
        PrintCOCByDatesResponse res;
        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        try {
            map.add("userId", userID);
            map.add("eventId", eventID);
            map.add("selectDate", selectedDates);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "printCOCByDates Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e("printCOCByDates", "printCOCByDates Post request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.e("printCOCByDates", "printCOCByDates Post request URL:" + mResponseBody);
            String message = null;
            PrintCOCByDatesResponse response = new Gson().fromJson(mResponseBody,
                    PrintCOCByDatesResponse.class);
            String responseCode = response.getResponseCode().toString();
            message = response.getMessage();
            Log.e("printCOCByDates", "printCOCByDates Post request URL MESSAGE:"
                    + message + "resCode- " + responseCode);
            if (mResponseCode.equals("200")) {
                res = response;
            } else {
                res = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("printCOCByDates", "printCOCByDates Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return res;
    }

    public String cocFileDownload(String strBaseUrl, String strResource,
                                  String siteId, String filename, String fileKey, String eventID) {
        String url = strBaseUrl + strResource;
        String res = "true";
        String outdir = Util.getFileFolderDirPathForCOCPDF(context);

        if (outdir.isEmpty()) {
            Toast.makeText(context, "Unable to load directory", Toast.LENGTH_SHORT).show();
            return "false";
        }

        File outputFile = new File(outdir, filename);

        BufferedInputStream in = null;
        FileOutputStream fout = null;
        DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) context);
        String userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("device_id", ob.getDeviceId());
            con.setRequestProperty("user_guid", ob.getUser_guid());
            con.setRequestProperty("user_id", userID);

            String urlParameters = "file=" + fileKey;
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            Log.i(TAG, "Sending 'POST' FileDownload request to URL : " + url);
            Log.i(TAG, "Post parameters : " + urlParameters);
            Log.i(TAG, "Response Code : " + responseCode);
            if (responseCode == 200) {
                in = new BufferedInputStream(con.getInputStream());
                fout = new FileOutputStream(outputFile);
                byte data[] = new byte[4096];
                int count;
                while ((count = in.read(data)) != -1) {
                    fout.write(data, 0, count);
                }
                //Log.i(TAG, "Bufffer File Write:" + new String(data));
                res = "true";
            } else {
                res = con.getResponseMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in file Download:" + e.getMessage());
            res = "false";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
                res = "true";
            } catch (IOException e) {
                e.printStackTrace();
                res = "false";
            }
        }

        return res.equals("true") ? outputFile.getAbsolutePath() : res;
    }

    public String FileDownload(String strBaseUrl, String strResource,
                               String userGuid, String siteId, String fileGuid, String filename, String userId) {
        String url = strBaseUrl + strResource;
        String res = "true";
        String outdir = Util.getFileFolderDirPath(context, siteId);

        Log.e(TAG, "**File:dir " + filename);

        if (outdir.isEmpty())
            return "false";

        File outputFile = new File(outdir, fileGuid);

        BufferedInputStream in = null;
        FileOutputStream fout = null;
        DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) context);

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("device_id", ob.getDeviceId());
            con.setRequestProperty("user_guid", ob.getUser_guid());
            con.setRequestProperty("user_id", userId);

//            String urlParameters = "userGuid=ddcf799e-93b3-11e5-9328-0aa26b506601&siteId=1&fileName=Agile_Software_Development_Succinctly.pdf&fileGuid=40f6efb6-7693-4fa7-bdc6-b095d03b0de0";
            String urlParameters = "userGuid=" + userGuid + "&fileName="
                    + filename + "&siteId=" + siteId + "&fileGuid=" + fileGuid;
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            Log.i(TAG, "Sending 'POST' FileDownload request to URL : " + url);
            Log.i(TAG, "Post parameters : " + urlParameters);
            Log.i(TAG, "Response Code : " + responseCode);
            if (responseCode == 200) {
                in = new BufferedInputStream(con.getInputStream());
                fout = new FileOutputStream(outputFile);
                byte data[] = new byte[4096];
                int count;
                while ((count = in.read(data)) != -1) {
                    fout.write(data, 0, count);
                }
                //Log.i(TAG, "Bufffer File Write:" + new String(data));
                res = "true";
            } else {
                res = con.getResponseMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in file Download:" + e.getMessage());
            res = "false";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
                res = "true";
            } catch (IOException e) {
                e.printStackTrace();
                res = "false";
            }
        }

        Log.e(TAG, "**FileDownload:dir " + outdir);
        Log.e(TAG, "**File:result " + res);
        return res;
    }

    public String cocFileDownload(String strBaseUrl, String strResource,
                                  String cocId, String userId) {
        String url = strBaseUrl + strResource;
        String res = "true";

        String path =
                FileUtils.getPathOfStoredFile(GlobalStrings.COC_DOCS_FOLDER);
        File outputFile = new File(path, cocId + ".zip");

        BufferedInputStream in = null;
        FileOutputStream fout = null;
        DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) context);

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("device_id", ob.getDeviceId());
            con.setRequestProperty("user_guid", ob.getUser_guid());
            con.setRequestProperty("user_id", userId);

            String urlParameters = "cocId=" + cocId + "&userId=" + userId;
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            Log.i(TAG, "Sending 'POST' FileDownload request to URL : " + url);
            Log.i(TAG, "Post parameters : " + urlParameters);
            Log.i(TAG, "Response Code : " + responseCode);
            if (responseCode == 200) {
                in = new BufferedInputStream(con.getInputStream());
                fout = new FileOutputStream(outputFile);
                byte data[] = new byte[4096];
                int count;
                while ((count = in.read(data)) != -1) {
                    fout.write(data, 0, count);
                }
                res = "true";

                CocMasterDataSource cocMasterDataSource = new CocMasterDataSource(context);
                SCocMaster cocMaster
                        = cocMasterDataSource.getCoCMasterDataForCOCID(cocId);
                String cocDisplayId = cocId;

                if (cocMaster != null) {
                    if (!cocMaster.getCocDisplayId().trim().isEmpty())
                        cocDisplayId = cocMaster.getCocDisplayId();
                }

                //commented on 14 Feb, 23 as playstore suggests adding changes to the unzip code..
/*                if (outputFile.exists()) {
                    String outPath = path + "/" + cocDisplayId + "/";
                    if (FileUtils.unpackZip(outputFile, new File(outPath)))
                        outputFile.delete();//we don't need the zip anymore once extracted
                }*/
            } else {
                res = con.getResponseMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in file Download:" + e.getMessage());
            res = "false";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
                res = "true";
            } catch (IOException e) {
                e.printStackTrace();
                res = "false";
            }
        }

        return res;
    }

    void buildRestRequestwithStringHttpEntity(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();
        mRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending a JSON object i.e. "application/json"
        mRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<String>(obj.toString(), mRequestHeaders);
        } else { //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        enableSSL();

        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }

    void buildRestRequestforDownloadFile(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending a JSON object i.e. "application/json"
        mRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        mRequestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<String>(obj.toString(), mRequestHeaders);
        } else { //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        enableSSL();


        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());
    }

    private void setHTTPInterceptor(RestTemplate mRestTemplate) {
        List<ClientHttpRequestInterceptor> listInterceptor = new ArrayList<>();
        listInterceptor.add(new MyClientHttpRequestInterceptor());
        mRestTemplate.setInterceptors(listInterceptor);
    }

    /*
     *  http://localhost:8080/AquaBlue-WebApp/metadatasync/username/site?lastSyncUpTime=0&password=password
     *  (GET method, returns List<SSite>)
     *  @resBaseUrl - http://localhost:8080/
     *  @strResource - metadatasync
     *  @strName - user name
     *  @strPwd - user password
     *  @strTime - sync time
     */

    //07-Dec-15
    public FielddataResponseModel v1_setFieldEventData(String strBaseUrl, String strResource,
                                                       List<FieldDataSyncStaging> fData, String guid) {

        String strFinal = null;
        FielddataResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();

        // URL construction

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid +
                "&deviceId=" + DeviceInfo.getDeviceID(context) +
                "&eventStatus=1" + "&processFlag=1";

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(fData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);

            mResponseCode = mRespEntity.getStatusCode().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), FielddataResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.out.println("Error e=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Error e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    //added on 21 May, 22 for testing sync data with uuid and sorted by event id
    public FielddataResponseModel uploadFieldEventData(String strBaseUrl, String strResource,
                                                       ArrayList<DataSyncRequest> fData, String guid) {

        String strFinal = null;
        FielddataResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();

        // URL construction

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid +
                "&deviceId=" + DeviceInfo.getDeviceID(context) +
                "&eventStatus=1" + "&processFlag=1";

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(fData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);

            mResponseCode = mRespEntity.getStatusCode().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), FielddataResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.out.println("Error e=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Error e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public updateUserLocationResponse updateUserLocation(String strBaseUrl, String strResource, String lat, String longi, String guid) {

        String strFinal = null;
        updateUserLocationResponse responseModel = null;
        if (null == strBaseUrl || null == strResource) {
            return null;
        }

//        // URL construction
        strFinal = strBaseUrl + strResource;
//        JSONObject params = new JSONObject();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userGuid", guid);
            map.add("latitude", lat);
            map.add("longitude", longi);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "update User Location Post request Error:" + e.getMessage());
        }
        Log.i(TAG, "updateUserLocation URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();
        // Build Rest request
        buildRestRequestFormEncoded(map);
        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            Log.i(TAG, "updateUserLocationResponse() Latitude=" + lat + " and longitude=" + longi);

            mResponseCode = mRespEntity.getStatusCode().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), updateUserLocationResponse.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "updateUserLocationResponse Error e=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "updateUserLocationResponse Error e=" + e1.getMessage());


        } finally {
            resetSSLFactory();
        }


        return responseModel;
    }


    // TODO: 20-Feb-16 Add Location
    public NewLocationResponseModel v1_setAddLocationData(String strBaseUrl, String strResource, List<NewClientLocation> lData, String guid) {

        String strFinal = null;
        NewLocationResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource) {
            return responseModel;
        }

        // URL construction

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid;

        Log.i(TAG, " v1_setAddLocationData URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(lData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), NewLocationResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
            Log.e("locationName", "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());
            Log.e("locationName", "Exception e1=" + e1.getMessage());
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public newLovResponseModel v1_setAddLovData(String strBaseUrl, String strResource, List<newLovData> lData, String guid) {

        String strFinal = null;
        newLovResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource) {
            return responseModel;
        }

        // URL construction

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid;

        Log.i(TAG, " v1_setAddLocationData URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(lData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), newLovResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public newSiteUserResponseModel v1_setAssignuserData(String strBaseUrl, String strResource, List<SSiteUserRole> lData, String guid) {

        String strFinal = null;
        newSiteUserResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource) {
            return responseModel;
        }

        // URL construction

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid;

        Log.i(TAG, " v1_setAssignuserData URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(lData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), newSiteUserResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public EventResponseModel v1_closeEventID(String strBaseUrl, String strResource, Integer eventID) {
        String strFinal = null;
//        Boolean eventClosed = false;
        EventResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();

        String userID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        if (null == strBaseUrl || null == strResource) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource + eventID + "/" + userID + "/";

        System.out.println("DDDD" + " setFieldEventData: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // TODO: 02-Dec-15 For Application-form-urlencoded
        // Build Rest request
        buildRestRequestFormEncoded(null);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.GET, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), EventResponseModel.class);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "Error in fileUpload:" + e.getLocalizedMessage());
            }
            return null;
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public AttachmentTaskResponseModel TaskMediaUpload(String strBaseUrl, String strResource, MultiValueMap<String, Object> files) {

        String strFinal = null;
        AttachmentTaskResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == files) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        Log.d("TaskMediaUpload", " SetFieldEventFile: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        v1_buildRestRequestForFileUpload(files);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, AttachmentTaskResponseModel.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.e("taskMediaUpload", "taskMediaUpload: " + mRespEntity.getStatusCode().toString() + " response body: " + mResponseBody);

            responseModel = (AttachmentTaskResponseModel) mRespEntity.getBody();
            Log.e("taskMediaUpload", "taskMediaUpload: " + responseModel.isSuccess() + " " + responseModel.getMessage() + " " + responseModel.getData());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (e != null) {
                e.printStackTrace();
                Log.e("taskMediaUpload", "Error in fileUpload:" + e.getLocalizedMessage());
            }

        } finally {
            resetSSLFactory();
        }

        return responseModel;

    }

    public AttachmentQnoteResponseModel QnoteMediaUpload(String strBaseUrl, String strResource, MultiValueMap<String, Object> files) {

        String strFinal = null;
        AttachmentQnoteResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == files) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        Log.d(TAG, " SetFieldEventFile: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        v1_buildRestRequestForFileUpload(files);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, AttachmentQnoteResponseModel.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.e("qnoteMediaUpload", "QnoteMediaUpload: " + mRespEntity.getStatusCode().toString() + " response body: " + mResponseBody);
            responseModel = (AttachmentQnoteResponseModel) mRespEntity.getBody();
            Log.e("qnoteMediaUpload", "QnoteMediaUpload: " + responseModel.getData().size());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (e != null) {
                e.printStackTrace();
                Log.e("qnoteMediaUpload", "Error in fileUpload:" + e.getLocalizedMessage());
            }

        } finally {
            resetSSLFactory();
        }

        return responseModel;

    }

    public AttachmentResponseModel v1_SetFieldEventFile(String strBaseUrl, String strResource,
                                                        MultiValueMap<String, Object> files, File file, String jsonString, String guid) {

        String strFinal = null;
        AttachmentResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == files) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        Log.d(TAG, " SetFieldEventFile: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        v1_buildRestRequestForFileUpload(files);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, AttachmentResponseModel.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();

            responseModel = (AttachmentResponseModel) mRespEntity.getBody();

/*            deviceInfoModel ob = DeviceInfo.getDeviceInfo(context);
            String deviceToken = Util.getSharedPreferencesProperty(context, GlobalStrings.NOTIFICATION_REGISTRATION_ID);

            if (deviceToken == null || deviceToken.trim().isEmpty())
                new Thread(() -> MyFirebaseMessagingService
                        .generateFireBaseToken(context)).start();

            String uID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);

            jsonString = URLEncoder.encode(jsonString, "UTF-8");*/

/*
            AndroidNetworking.upload(strFinal)
                    .addHeaders("device_token", deviceToken)
                    .addHeaders("device_type", ob.getDeviceType())
                    .addHeaders("device_id", ob.getDeviceId())
                    .addHeaders("user_id", uID)
                    .addHeaders("user_guid", ob.getUser_guid())
                    .addMultipartFile("files", file)
                    .addMultipartParameter("events", jsonString)
                    .addMultipartParameter("userGuid", guid)
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do anything with progress
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseModel[0] = new Gson().fromJson(response.toString(),
                                    AttachmentResponseModel.class);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("attachResponse", "Error in fileUpload:"
                                    + error.getLocalizedMessage());
                        }
                    });
*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("attachResponse", "Error in fileUpload:" + e.getLocalizedMessage());
        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    public WorkOrderResponseModel v1_getWorkOrderData(String strBaseUrl, String strResource, String userGuid) {

        String strfinal = null;

        WorkOrderResponseModel respmodel = null;

        if (null == strBaseUrl || null == strResource || null == userGuid) {
            return respmodel;
        }
        strfinal = strBaseUrl + strResource + "?userGuid=" + userGuid;
        initializeClassMembers();

        buildRestRequest(null);


        try {
            mRespEntity = mRestTemplate.exchange(strfinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            respmodel = new Gson().fromJson(mRespEntity.getBody().toString(), WorkOrderResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return respmodel;


    }


    public CocResponseModel v1_getCOCListData(String strBaseUrl, String strResource,
                                              String userGuid, String lastSyncDate) {

        String strfinal = null;

        CocResponseModel respmodel = null;

        if (null == strBaseUrl || null == strResource || null == userGuid) {
            return respmodel;
        }
        strfinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userGuid", userGuid);
            map.add("lastSync", "0");//0


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getEventList Post request Error:" + e.getMessage());
        }

        initializeClassMembers();
        buildRestRequestFormEncoded(map);

        try {
            mRespEntity = mRestTemplate.exchange(strfinal, HttpMethod.POST, mRequestEntity, String.class, map);
            mResponseCode = mRespEntity.getStatusCode().toString();
            respmodel = new Gson().fromJson(mRespEntity.getBody().toString(), CocResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return respmodel;
    }


    void v1_buildRestRequestForFileUpload(Object files) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();


        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending the content as Multipart i.e. "multipart/form-data"
        mRequestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        enableSSL();

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        mRequestEntity = new HttpEntity<Object>(files, mRequestHeaders);

        // Create a new RestTemplate instance
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setBufferRequestBody(false);
        factory.setChunkSize(1024);

        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(factory);

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }// end of buildRestRequest


    public SSite[] getMetaSite(String strBaseUrl, String strResource, String strName,
                               String strPwd, String strTime) {

        String strFinal = null;
        SSite[] retSite = null;

        if (null == strBaseUrl || null == strResource || null == strName
                || null == strPwd || null == strTime) {
            return retSite;
        }

        // URL construction
        strFinal = strBaseUrl + strResource
                + "/" + strName + "/site"
                + "?lastSyncUpTime=" + strTime
                + "&password=" + strPwd;

        Log.d(TAG, " getMetaSite: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(retSite);


        // Make the network request

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.GET, mRequestEntity,
                    SSite[].class);
        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            resetSSLFactory();
        }

        mResponseCode = mRespEntity.getStatusCode().toString();

        mResponseBody = mRespEntity.getBody().toString();

        Log.d(TAG, " RequestWithJson: response code = " + mResponseCode);

        retSite = (SSite[]) mRespEntity.getBody();

        Log.d(TAG, "Size = " + retSite.length);

        return retSite;
    }

    /*
     *  http://localhost:8080/AquaBlue-WebApp/metadatasync/username/location?lastSyncUpTime=0&password=password
     *  (GET method, returns List<SLocation>)
     *  @resBaseUrl - http://localhost:8080/
     *  @strResource - metadatasync
     *  @strName - user name
     *  @strPwd - user password
     *  @strTime - sync time
     */
    @JsonIgnore
    public SLocation[] getMetaLocation(String strBaseUrl, String strResource,
                                       String strName, String strPwd, String strTime) {

        String strFinal = null;
        SLocation[] retLocation = null;

        if (null == strBaseUrl || null == strResource || null == strName
                || null == strPwd || null == strTime) {
            return retLocation;
        }

        // URL construction
        strFinal = strBaseUrl + strResource
                + "/" + strName + "/location"
                + "?lastSyncUpTime=" + strTime
                + "&password=" + strPwd;

        System.out.println("gggg" + " getMetaLocation: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(retLocation);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.GET, mRequestEntity, SLocation[].class);
        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            resetSSLFactory();
        }

        // retLocation = mRestTemplate.getForObject(strFinal, SLocation[].class);

        mResponseCode = mRespEntity.getStatusCode().toString();

        mResponseBody = mRespEntity.getBody().toString();

        Log.d(TAG, " RequestWithJson: response code = " + retLocation);

        retLocation = (SLocation[]) mRespEntity.getBody();

        Log.d(TAG, "Size = " + retLocation.length);

        return retLocation;
    }

    /*
     *  http://localhost:8080/AquaBlue-WebApp/metadatasync/username/unitconverter?lastSyncUpTime=0&password=password
     *  (GET method, returns List<RUnitConverter>)
     *  @resBaseUrl - http://localhost:8080/
     *  @strResource - metadatasync
     *  @strName - user name
     *  @strPwd - user password
     *  @strTime - sync time
     */


    public RUnitConverter[] getMetaDataSync(String strBaseUrl, String strResource,
                                            String strName, String strPwd, String strTime) {

        String strFinal = null;
        RUnitConverter[] retMetaDataSync = null;

        if (null == strBaseUrl || null == strResource || null == strName
                || null == strPwd || null == strTime) {
            return retMetaDataSync;
        }

        // URL construction
        strFinal = strBaseUrl + strResource
                + "/" + strName + "/unitconverter"
                + "?lastSyncUpTime=" + strTime
                + "&password=" + strPwd;

        System.out.println("gggg" + " getMetaDataSync: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(retMetaDataSync);

        // Make the network request


        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.GET, mRequestEntity,
                    RUnitConverter[].class);
        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            resetSSLFactory();
        }

        mResponseCode = mRespEntity.getStatusCode().toString();

        mResponseBody = mRespEntity.getBody().toString();

        System.out.println("gggg" + " RequestWithJson: response code = " + mResponseCode);

        retMetaDataSync = (RUnitConverter[]) mRespEntity.getBody();

        System.out.println("gggg" + "Size = " + retMetaDataSync.length);

        return retMetaDataSync;
    }

    //// TODO: 12/15/2017
    public RegistrationResponseModel registrationwebservice(String strBaseUrl, String strResource,
                                                            String firstname, String lastname,
                                                            String companyName, String emailid,
                                                            String mobileNumber, String uname, String password1) {

        String strFinal = null;
        RegistrationResponseModel retRegRespData = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource || null == uname || null == companyName || null == mobileNumber || null == password1) {
            return retRegRespData;
        }
        strFinal = strBaseUrl + strResource;

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("firstName", firstname);
            jsonObject.put("lastName", lastname);
            jsonObject.put("companyName", companyName);
            jsonObject.put("primaryEmail", emailid);
            jsonObject.put("mobileNumber", mobileNumber);
            jsonObject.put("userName", uname);
            jsonObject.put("tempPassword", password1);
            jsonObject.put("confirmPassword", password1);

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
            return null;
        }

        Log.i(TAG, " getMetaSync URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequestwithStringHttpEntity(jsonObject);

        try {

            Log.i(TAG, "Request MetaSync Start Time:" + (System.currentTimeMillis()));

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, jsonObject);

            Log.i(TAG, "Request MetaSync End Time:" + (System.currentTimeMillis()));

            mResponseCode = mRespEntity.getStatusCode().toString();

            mResponseBody = mRespEntity.getBody().toString();

            Log.i(TAG, "Start Deserialize MetaSync Time:" + (System.currentTimeMillis()));
            retRegRespData = new Gson().fromJson(mResponseBody, RegistrationResponseModel.class);
            Log.i(TAG, "End Deserialize MetaSync Time:" + (System.currentTimeMillis()));

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.e(TAG, "getRegResp() Error:" + e.getMessage());
            }
            return null;
        } finally {
            resetSSLFactory();
        }

//		System.out.println("gggg"+  "Size = "+ retMetaSyncData.length );

        return retRegRespData;

    }


    public Boolean setFieldEventData(String strBaseUrl, String strResource, List<DEvent> eEvent,
                                     String strName, String strPwd) {

        String strFinal = null;

        if (null == strBaseUrl || null == strResource) {
            return false;
        }

        // URL construction
        strFinal = strBaseUrl + strResource + "/" + "fielddata/" + "?userName=" + strName + "&password=" + strPwd;

        System.out.println("DDDD" + " setFieldEventData: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(eEvent);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, Boolean.class, eEvent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resetSSLFactory();
        }
        if (mRespEntity == null) {
            return false;
        }
        mResponseCode = mRespEntity.getStatusCode().toString();

        System.out.println("DDDD" + " RequestWithJson: response code = " + mResponseCode);
        System.out.println("DDDD" + " RequestWithJson: response mRespEntity = " + mRespEntity.getBody().toString());

        if (mResponseCode.equals("200")) {
            boolean resp = (Boolean) mRespEntity.getBody();
            System.out.println("DDDD" + " mRespEntity bool= " + resp);

            return resp;
        }

        return false;
    }

    public Boolean SetFieldEventFile(String strBaseUrl, String strResource, MultiValueMap<String, Object> eEventData) {

        String strFinal = null;

        if (null == strBaseUrl || null == strResource || null == eEventData) {
            return false;
        }

        // URL construction
        strFinal = strBaseUrl + strResource + "/" + "file/";

        Log.d(TAG, " SetFieldEventFile URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequestForFileUpload(eEventData);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, Boolean.class, eEventData);
        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;

        } finally {
            resetSSLFactory();
        }

        mResponseCode = mRespEntity.getStatusCode().toString();
        Log.i(TAG, " RequestWithJson response code = " + mResponseCode);
        if (mResponseCode.equals("200")) {
            boolean resp = (Boolean) mRespEntity.getBody();
            Log.i(TAG, "mRespEntity bool= " + resp);

            return resp;

        }
        return false;
    }

    @Override
    public EventResponseModel generateEventIDFromServer(String strBaseUrl, String strResource,
                                                        DEvent event, String userName,
                                                        JSONObject jsonRequest) {
        String strFind = null;
        String strCreate = null;

        EventResponseModel responseModel = null;
        String guid = Util.getSharedPreferencesProperty(context, userName);
        ObjectMapper mapper = new ObjectMapper();

        if (null == strBaseUrl) {
            return null;
        }

        // URL construction
        //strFind = strBaseUrl + "/api/v1/event/find/";
        strCreate = strBaseUrl + strResource;

        Log.i(TAG, " createtFieldEventData: URL = " + strCreate);

        // Initialize member variables
        initializeClassMembers();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("siteId", event.getSiteId());
            jsonObject.put("mobileAppId", event.getMobileAppId());
            jsonObject.put("userGuid", guid);//"f8180e4a-3b36-11e5-9708-0ea7cb7cc776"
            jsonObject.put("userId", event.getUserId());
            jsonObject.put("deviceId", DeviceInfo.getDeviceID(context));
            jsonObject.put("latitude", event.getLatitude());
            jsonObject.put("longitude", event.getLongitude());
            jsonObject.put("eventDate", event.getEventDate());
            jsonObject.put("eventStartDate", event.getEventStartDate());
            jsonObject.put("eventEndDate", event.getEventEndDate());
            jsonObject.put("notes", event.getNotes());
            jsonObject.put("eventId", event.getEventId());
            jsonObject.put("clientEventId", event.getEventId());
            jsonObject.put("eventName", event.getEventName());
            jsonObject.put("createEventFlag", event.getCreateEventFlag());

        } catch (JSONException e1) {
            e1.printStackTrace();
            Log.e(TAG, "Error in Parsing :" + e1.getLocalizedMessage());
            return null;
        }

        if (jsonRequest != null)
            jsonObject = jsonRequest;

        // Build Rest request
        buildRestRequestwithStringHttpEntity(jsonObject);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strCreate, HttpMethod.POST, mRequestEntity, String.class, jsonObject);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), EventResponseModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in getting eventID From server:" + e.getLocalizedMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        if (event.getCreateEventFlag() == 0)
            responseModel.setRenameEvent(true);

        return responseModel;
    }

    // Mapping Status code
    private void mapResponseCodeToStatusCode() {

        Log.d(TAG, " mapResponseCodeToStatusCode: Response code= " + mRespEntity.getStatusCode());

        switch (mRespEntity.getStatusCode()) {
            case ACCEPTED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= ACCEPTED ");
                break;

            case BAD_GATEWAY:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= BAD_GATEWAY ");
                break;

            case BAD_REQUEST:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= BAD_REQUEST ");
                break;

            case BANDWIDTH_LIMIT_EXCEEDED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= BANDWIDTH_LIMIT_EXCEEDED ");
                break;

            case CHECKPOINT:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= CHECKPOINT ");
                break;

            case CONFLICT:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= CONFLICT ");
                break;

            case CREATED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= CREATED ");
                break;

            case FORBIDDEN:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= FORBIDDEN ");
                break;

            case FOUND:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= FOUND ");
                break;

            case HTTP_VERSION_NOT_SUPPORTED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= HTTP_VERSION_NOT_SUPPORTED ");
                break;

            case INTERNAL_SERVER_ERROR:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= INTERNAL_SERVER_ERROR ");
                break;

            case METHOD_FAILURE:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= METHOD_FAILURE ");
                break;

            case METHOD_NOT_ALLOWED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= METHOD_NOT_ALLOWED ");
                break;

            case NETWORK_AUTHENTICATION_REQUIRED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= NETWORK_AUTHENTICATION_REQUIRED ");
                break;

            case NOT_ACCEPTABLE:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= NOT_ACCEPTABLE ");
                break;

            case NOT_IMPLEMENTED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= NOT_IMPLEMENTED ");
                break;

            case NO_CONTENT:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= NO_CONTENT ");
                break;

            case OK:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= OK ");
                break;

            case REQUEST_ENTITY_TOO_LARGE:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= REQUEST_ENTITY_TOO_LARGE ");
                break;

            case REQUEST_HEADER_FIELDS_TOO_LARGE:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= REQUEST_HEADER_FIELDS_TOO_LARGE ");
                break;

            case REQUEST_TIMEOUT:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= REQUEST_TIMEOUT ");
                break;

            case REQUEST_URI_TOO_LONG:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= REQUEST_URI_TOO_LONG ");
                break;

            case SERVICE_UNAVAILABLE:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= SERVICE_UNAVAILABLE ");
                break;

            case UNAUTHORIZED:
                Log.d(TAG, " mapResponseCodeToStatusCode: Response code= UNAUTHORIZED ");
                break;

            default:
                break;

        }
    }

    public File readFile() {

        //Get the text file
        File file = new File("/data/data/com.aquablue.client/", "QuizCD.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        }

        Log.d(TAG, " readFile: " + text.toString());
        return file;
    }

    void buildRestRequestFormEncodedLogin(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeaderLogin(mRequestHeaders);
        //  Sending a JSON object i.e. "application/json"

        mRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);
        } else {
            //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        //27-Oct-15 Disable SSL
        enableSSL();

        // trustSelfSignedTSL();
        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());
    }

    private void setRequestHeaderLogin(HttpHeaders header) {
        DeviceInfoModel ob = DeviceInfo.getDeviceInfo((Activity) context);
        String deviceToken = Util.getSharedPreferencesProperty(context, GlobalStrings.NOTIFICATION_REGISTRATION_ID);

        header.set("device_token", deviceToken);
        header.set("device_type", ob.getDeviceType());
        header.set("device_id", ob.getDeviceId());
        String appVersion = Util.getAppVersion(context);
        header.set("service_provider", ob.getService_provider());
        header.set("model_number", ob.getModel_number());
        header.set("screen_resolution", ob.getScreen_resolution());
        header.set("phone_number", ob.getPhone_number());
        header.set("battery_percentage", ob.getBattery_percentage());
        header.set("imei_no", ob.getImei_no());
        header.set("os_version", ob.getOs_version());
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            header.set("mac_address", ob.getMac_address());
            header.set("ip_address", ob.getIp_address());
        }
        header.set("device_name", ob.getDevice_name());
        header.set("app_version", appVersion);
    }

    void buildRestRequestFormEncoded(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);
        //  Sending a JSON object i.e. "application/json"

        mRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);
        } else {
            //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        //27-Oct-15 Disable SSL
        enableSSL();

        // trustSelfSignedTSL();
        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }// end of buildRestRequestFormEncoded


/*
    public void VerifyHostname() {
        try {
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            DefaultHttpClient client = new DefaultHttpClient();

            SchemeRegistry registry = new SchemeRegistry();
            org.apache.http.conn.ssl.SSLSocketFactory socketFactory = org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

// Set verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/

    public void trustSelfSignedTSL() {
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] arg0, String arg1)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] arg0, String arg1)
                        throws java.security.cert.CertificateException {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLContext.setDefault(ctx);
        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred ", ex);
        }
    }


    // Local method to build Rest for FileUpload
    void buildRestRequestForFileUpload(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();


        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending the content as Multipart i.e. "multipart/form-data"
        mRequestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        enableSSL();

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);


        // Create a new RestTemplate instance
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setBufferRequestBody(false);
        factory.setChunkSize(1024);

        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(factory);

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }// end of buildRestRequest


    public Boolean closeEventID(String strBaseUrl, String strResource, Integer eventID,
                                String strName, String strPwd) {
        // TODO Auto-generated method stub
        String strFinal = null;
        Boolean eventClosed = false;

        if (null == strBaseUrl || null == strResource) {
            return false;
        }

        // URL construction
        strFinal = strBaseUrl + strResource + "/" + "event/" + eventID + "/" + "?userName=" + strName + "&password=" + strPwd
                + "&close=true";

        System.out.println("DDDD" + " setFieldEventData: URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();

        // Build Rest request
        buildRestRequest(null);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, null, Boolean.class);
        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            resetSSLFactory();
        }

        mResponseCode = mRespEntity.getStatusCode().toString();

        Log.i(TAG, " RequestWithJson: response code = " + mResponseCode);
        Log.i(TAG, " RequestWithJson: response mRespEntity = " + mRespEntity.getBody().toString());

        if (mResponseCode.equals("200")) {
            eventClosed = (Boolean) mRespEntity.getBody();
            Log.i(TAG, " mRespEntity bool= " + eventClosed);
        }

        return eventClosed;
    }

    void setRequestHeader(HttpHeaders header) {
        DeviceInfoModel ob = DeviceInfo.getDeviceInfo(context);
        String deviceToken = Util.getSharedPreferencesProperty(context, GlobalStrings.NOTIFICATION_REGISTRATION_ID);

        if (deviceToken == null || deviceToken.trim().isEmpty())
            new Thread(() -> MyFirebaseMessagingService
                    .generateFireBaseToken(context)).start();

        header.set("device_token", deviceToken);
        header.set("device_type", ob.getDeviceType());
        header.set("device_id", ob.getDeviceId());
        String uID = Util.getSharedPreferencesProperty(context, GlobalStrings.USERID);
        String appVersion = Util.getAppVersion(context);
        header.set("user_id", uID);
        header.set("user_guid", ob.getUser_guid());
        header.set("service_provider", ob.getService_provider());
        header.set("model_number", ob.getModel_number());
        header.set("screen_resolution", ob.getScreen_resolution());
        header.set("phone_number", ob.getPhone_number());
        header.set("battery_percentage", ob.getBattery_percentage());
        header.set("imei_no", ob.getImei_no());
        header.set("os_version", ob.getOs_version());

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            header.set("mac_address", ob.getMac_address());
            header.set("ip_address", ob.getIp_address());
        }

        header.set("user_location", ob.getUser_location());
        header.set("device_name", ob.getDevice_name());
        header.set("app_version", appVersion);
    }

    public newLabelResponseModel v1_setAddLabelData(String strBaseUrl, String strResource, List<newFormLabelResponse> labelData, String userguid, String password) {

        String strFinal = null;
//        newFormLabelResponse formlabel=new newFormLabelResponse(labelData);
        newLabelResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource) {

            return responseModel;
        }

        strFinal = strBaseUrl + strResource + "?userGuid=" + userguid + "&password=" + password;


        // URL construction

        //    strFinal = strBaseUrl + strResource;

        Log.i(TAG, " v1_setAddFormData URL = " + strFinal);

        // Initialize member variables
        initializeClassMembers();


        // Build Rest request
        buildRestRequestforForm(labelData);

        // Make the network request
        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), newLabelResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }

    void buildRestRequestforForm(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending a JSON object i.e. "application/json"
        mRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);
        } else { //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        enableSSL();

        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());
    }

    public VerifyEmailResponseModel verifyemailstatus(String strBaseUrl, String strResource, String userGuid
    ) {
        String strFinal = null;
        VerifyEmailResponseModel responseModel = null;
        ObjectMapper mapper = new ObjectMapper();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        strFinal = strBaseUrl + strResource;

        //  map.add("userMailId",username);


        initializeClassMembers();
        buildRestRequest(null);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);

            mResponseCode = mRespEntity.getStatusCode().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), VerifyEmailResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.out.println("Error e=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Error e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }
        return responseModel;
    }

    public DeviceUpdateResponseModel updateDevice(String strBaseUrl, String strResource) {

        String strFinal = null;
        DeviceUpdateResponseModel responseModel = null;

        strFinal = strBaseUrl + strResource;

        initializeClassMembers();
        buildRestRequest(null);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);

            mResponseCode = mRespEntity.getStatusCode().toString();

            responseModel = new Gson().fromJson(mRespEntity.getBody().toString(), DeviceUpdateResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.out.println("Error e=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Error e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responseModel;
    }


    public AddsiteResponseModel addSiteService(String strBaseUrl, String strResource,
                                               SiteModel sm, String formIds,
                                               String guid) {
        String strFinal = null;
        AddsiteResponseModel responsemodel = null;

        if (null == strBaseUrl || null == strResource || null == guid) {
            return responsemodel;
        }

        strFinal = strBaseUrl + strResource + "?userGuid=" + guid + "&formIds=" + formIds;

        initializeClassMembers();

        // Build Rest request
        buildRestRequestforSite(sm);

        //  buildRestRequestforSite(sm);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class);
            mResponseCode = mRespEntity.getStatusCode().toString();
            responsemodel = new Gson().fromJson(mRespEntity.getBody().toString(), AddsiteResponseModel.class);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Log.e(TAG, "Error HttpClientErrorException=" + e.getStatusCode() + e.getResponseBodyAsString());
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, "Exception e1=" + e1.getMessage());

        } finally {
            resetSSLFactory();
        }

        return responsemodel;
    }

    public TaskSyncResponse syncTasks(String strBaseUrl,
                                      JSONObject jsonRequest) {
        String strFinal;
        TaskSyncResponse response;
        // URL construction
        strFinal = strBaseUrl;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        try {
            map.add("postObject", jsonRequest.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e("getAllTasks", "get Tasks request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            response = new Gson().fromJson(mRespEntity.getBody().toString(), TaskSyncResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getAllTasks", "getAllTasks Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return response;
    }

    public TaskDataResponse getAllTasks(String strBaseUrl, String strResource,
                                        String lastSyncDate) {

        String strFinal;
        TaskDataResponse response;
        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        try {
            map.add("lastSyncDate", lastSyncDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeClassMembers();

        // Build Rest request
        buildRestRequestFormEncoded(map);
        Log.e("getAllTasks", "get Tasks request URL:" + strFinal);

        try {

            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity,
                    String.class, map);
            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            response = new Gson().fromJson(mRespEntity.getBody().toString(), TaskDataResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getAllTasks", "getAllTasks Error:" + e.getMessage());
            return null;
        } finally {
            resetSSLFactory();
        }

        return response;
    }

    private void buildRestRequestforSite(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        // Sending a JSON object i.e. "application/json"
        mRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);
        } else { //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        enableSSL();

        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }

    public DownloadDataResponseModel downloadActiveEventDataForUser(String strBaseUrl,
                                                                    String strResource,
                                                                    String userguid,
                                                                    String deviceid, int eventid,
                                                                    long lastsyncdate) {  //MultiValueMap<String, Object> files
        String strFinal = null;
        DownloadDataResponseModel respmodel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userGuid", userguid);
            map.add("deviceId", deviceid);
            map.add("lastSyncDate", String.valueOf(lastsyncdate));
            map.add("eventId", String.valueOf(eventid));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Login Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        v1_buildRestRequestForDownloadActiveEventData(map);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class, map);
            Log.i(TAG, "downloadActiveEventData() call web api end time:" + System.currentTimeMillis());

            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.i(TAG, "downloadActiveEventData() call web apiresponse code:" + mResponseCode);
            //   respmodel = (DownloadDataResponseModel) mRespEntity.getBody();

            respmodel = new Gson().fromJson(mResponseBody, DownloadDataResponseModel.class);

            Log.i(TAG, "downloadActiveEventData() call web apiresponse code:" + respmodel);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error:" + e.getMessage());
        }

        return respmodel;
    }

    public DownloadEventDataResponse downloadEventDataWithAttachments(String strBaseUrl,
                                                                      String strResource,
                                                                      String userguid,
                                                                      String deviceid, int eventid,
                                                                      long lastsyncdate) {
        String strFinal = null;
        DownloadEventDataResponse respmodel = null;
        ObjectMapper mapper = new ObjectMapper();
        if (null == strBaseUrl || null == strResource) {
            return null;
        }

        // URL construction
        strFinal = strBaseUrl + strResource;

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        try {
            map.add("userGuid", userguid);
            map.add("deviceId", deviceid);
            map.add("lastSyncDate", String.valueOf(lastsyncdate));
            map.add("eventId", String.valueOf(eventid));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Login Post request Error:" + e.getMessage());
        }

        initializeClassMembers();

        // Build Rest request
        v1_buildRestRequestForDownloadActiveEventData(map);

        try {
            mRespEntity = mRestTemplate.exchange(strFinal, HttpMethod.POST, mRequestEntity, String.class, map);
            Log.i(TAG, "downloadEventDataWithAttachments() call web api end time:" + System.currentTimeMillis());

            mResponseCode = mRespEntity.getStatusCode().toString();
            mResponseBody = mRespEntity.getBody().toString();
            Log.i(TAG, "downloadEventDataWithAttachments() call web apiresponse code:" + mResponseCode);

            respmodel = new Gson().fromJson(mResponseBody, DownloadEventDataResponse.class);

            Log.i(TAG, "downloadEventDataWithAttachments() call web apiresponse code:" + respmodel);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error:" + e.getMessage());
        }

        return respmodel;
    }

    void v1_buildRestRequestForDownloadActiveEventData(Object obj) {

        // Create the HTTP request
        mRequestHeaders = new HttpHeaders();

        // Close the request of each request and response
        mRequestHeaders.set("Connection", "Close");
        setRequestHeader(mRequestHeaders);

        //  Sending a JSON object i.e. "application/json"
//       ~~

        mRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Populate the Message object to serialize and headers in an
        // HttpEntity object to use for the request
        if (obj != null) {
            mRequestEntity = new HttpEntity<Object>(obj, mRequestHeaders);
        } else {
            //to accomodate for closeevent which has no object to post
            mRequestEntity = new HttpEntity<Object>(mRequestHeaders);
        }

        // TODO: 27-Oct-15 Disable SSL
        enableSSL();

        // Create a new RestTemplate instance
        mRestTemplate = new RestTemplate();
        setHTTPInterceptor(mRestTemplate);
        mRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());

    }// end of buildRestRequestFormEncoded


    private void enableSSL() {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)

     /*   try {
            ProviderInstaller.installIfNeeded(context);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        CertificateFactory cf = null;

        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = null;

        try {
            if (getContext().getResources().getString(R.string.prod_base_uri).contains("v14")) {
                caInput = getContext().getAssets().open("qnopynew.crt");
            } else if (getContext().getResources().getString(R.string.prod_base_uri).contains("v15")) {
                caInput = getContext().getAssets().open("qnopy_v15.crt");
            } else {
                caInput = getContext().getAssets().open("qnopy.crt");
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        Certificate ca = null;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
        }
        try {
            keyStore.load(null, null);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            keyStore.setCertificateEntry("ca", ca);
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
        }

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            tmf.init(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        // Create an SSLContext that uses our TrustManager
        SSLContext context = null;
        try {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                context = SSLContext.getInstance("TLSv1.2");
            } else {
                context = SSLContext.getInstance("TLS");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            context.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        savedFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());*/
    }

    private void resetSSLFactory() {
//        HttpsURLConnection.setDefaultSSLSocketFactory(savedFactory);
    }

}
