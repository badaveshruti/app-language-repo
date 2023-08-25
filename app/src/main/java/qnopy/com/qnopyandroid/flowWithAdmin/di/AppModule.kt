package qnopy.com.qnopyandroid.flowWithAdmin.di

import android.content.Context
import android.os.Build
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import qnopy.com.qnopyandroid.GlobalStrings
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.flowWithAdmin.network.ApiService
import qnopy.com.qnopyandroid.restfullib.AquaBlueServiceImpl
import qnopy.com.qnopyandroid.services.MyFirebaseMessagingService
import qnopy.com.qnopyandroid.util.DeviceInfo
import qnopy.com.qnopyandroid.util.Util
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesAquaBlueService(@ApplicationContext appContext: Context) =
        AquaBlueServiceImpl(appContext)

    @Provides
    fun providesVolleyRequestQueue(@ApplicationContext appContext: Context) =
        Volley.newRequestQueue(appContext)

    @Provides
    fun providesUrl(@ApplicationContext appContext: Context) =
        appContext.getString(R.string.prod_base_uri)

    @Provides
    @Singleton
    fun providesGson(): Gson =
        GsonBuilder()
            .setLenient()
            .create();

    @Provides
    @Singleton
    fun providesApiService(
        url: String,
        client: OkHttpClient,
        gson: Gson
    ): ApiService =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    class RequestInterceptor constructor(
        val appContext: Context
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {

            val deviceInfo = DeviceInfo.getDeviceInfo(appContext)
            val deviceToken =
                Util.getSharedPreferencesProperty(
                    appContext,
                    GlobalStrings.NOTIFICATION_REGISTRATION_ID
                )

            if (deviceToken.isNullOrEmpty()) Thread {
                MyFirebaseMessagingService
                    .generateFireBaseToken(appContext)
            }.start()

            val userId = Util.getSharedPreferencesProperty(appContext, GlobalStrings.USERID)
            val appVersion = Util.getAppVersion(appContext)
            val newRequest = chain.request()
            val builder = newRequest.newBuilder()

            if (!userId.isNullOrEmpty() && !deviceInfo.user_guid.isNullOrEmpty()) {
                builder
                    .addHeader("user_id", userId)
                    .addHeader("user_guid", deviceInfo.user_guid)
                    .addHeader("device_token", deviceToken)
                    .addHeader("device_type", deviceInfo.deviceType)
                    .addHeader("device_id", deviceInfo.deviceId)
                    .addHeader("device_name", deviceInfo.device_name)
                    .addHeader("app_version", appVersion)
                    .addHeader("os_version", deviceInfo.os_version)

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    builder.addHeader("mac_address", deviceInfo.mac_address)
                        .addHeader("ip_address", deviceInfo.ip_address)
                }
            }

            return chain.proceed(builder.build())
        }
    }

    @Provides
    fun provideRequestInterceptor(@ApplicationContext appContext: Context): RequestInterceptor {
        return RequestInterceptor(appContext)
    }

    @Provides
    @Singleton
    fun provideClient(
        interceptor: HttpLoggingInterceptor,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(requestInterceptor).build()
    }
}