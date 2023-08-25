package qnopy.com.qnopyandroid.flowWithAdmin.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import qnopy.com.qnopyandroid.clientmodel.User
import qnopy.com.qnopyandroid.db.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun providesMetaDataSource(@ApplicationContext appContext: Context): MetaDataSource {
        return MetaDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesSiteDataSource(@ApplicationContext appContext: Context): SiteDataSource {
        return SiteDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesEventDataSource(@ApplicationContext appContext: Context): EventDataSource {
        return EventDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesEventDbSource(@ApplicationContext appContext: Context): EventDbSource {
        return EventDbSource(appContext)
    }

    @Singleton
    @Provides
    fun providesSiteMobileAppDataSource(@ApplicationContext appContext: Context): SiteMobileAppDataSource {
        return SiteMobileAppDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesUserDataSource(@ApplicationContext appContext: Context): UserDataSource {
        return UserDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesLocationDataSource(@ApplicationContext appContext: Context): LocationDataSource {
        return LocationDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesFileFolderDataSource(@ApplicationContext appContext: Context): FileFolderDataSource {
        return FileFolderDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesEventLocationDataSource(@ApplicationContext appContext: Context): EventLocationDataSource {
        return EventLocationDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesFieldDataSource(@ApplicationContext appContext: Context): FieldDataSource {
        return FieldDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesAttachmentDataSource(@ApplicationContext appContext: Context): AttachmentDataSource {
        return AttachmentDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesMobileAppDataSource(@ApplicationContext appContext: Context): MobileAppDataSource {
        return MobileAppDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesFormSitesDataSource(@ApplicationContext appContext: Context): FormSitesDataSource {
        return FormSitesDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesTaskDetailsDataSource(@ApplicationContext appContext: Context): TaskDetailsDataSource {
        return TaskDetailsDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesTaskAttachmentsDataSource(@ApplicationContext appContext: Context): TaskAttachmentsDataSource {
        return TaskAttachmentsDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesTaskCommentsDataSource(@ApplicationContext appContext: Context): TaskCommentsDataSource {
        return TaskCommentsDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesSiteUserRoleDataSource(@ApplicationContext appContext: Context): SiteUserRoleDataSource {
        return SiteUserRoleDataSource(appContext)
    }

    @Singleton
    @Provides
    fun providesLocationProfilePictureDataSource(@ApplicationContext appContext: Context): LocationProfilePictureDataSource {
        return LocationProfilePictureDataSource(appContext)
    }
}