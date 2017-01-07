package com.pinyaoting.garcondecuisine.application;

import static com.batch.android.Batch.Push;

import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;
import com.pinyaoting.garcondecuisine.BuildConfig;
import com.pinyaoting.garcondecuisine.R;
import com.pinyaoting.garcondecuisine.dependencies.components.application.ApplicationComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.application
        .DaggerApplicationComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.data.DaggerDataLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.data.DataLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.domain.DaggerDomainLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.domain.DomainLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.presentation
        .DaggerPresentationLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.components.presentation
        .PresentationLayerComponent;
import com.pinyaoting.garcondecuisine.dependencies.modules.core.AppModule;
import com.pinyaoting.garcondecuisine.dependencies.modules.core.NetModule;
import com.pinyaoting.garcondecuisine.dependencies.modules.data.DataLayerModule;
import com.pinyaoting.garcondecuisine.dependencies.modules.domain.DomainLayerModule;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

public class GarconApplication extends MultiDexApplication {

    private ApplicationComponent mApplicationComponent;
    private DataLayerComponent mDataLayerComponent;
    private DomainLayerComponent mDomainLayerComponent;
    private PresentationLayerComponent mPresentationLayerComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
        mApplicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .build();

        mDataLayerComponent = DaggerDataLayerComponent.builder()
                .applicationComponent(mApplicationComponent)
                .dataLayerModule(new DataLayerModule())
                .build();

        mDomainLayerComponent = DaggerDomainLayerComponent.builder()
                .dataLayerComponent(mDataLayerComponent)
                .domainLayerModule(new DomainLayerModule(getBaseContext()))
                .build();

        mPresentationLayerComponent = DaggerPresentationLayerComponent.builder()
                .domainLayerComponent(mDomainLayerComponent)
                .build();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        FlowManager.init(new FlowConfig.Builder(this).build());
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Push.setGCMSenderId(BuildConfig.GCM_SENDER_ID);

        Batch.setConfig(new Config(BuildConfig.BATCH_APIKEY)); // live
        Push.setNotificationsColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //Batch.Push.setSmallIconResourceId(R.drawable.ic_notification);
    }

    public PresentationLayerComponent getPresentationLayerComponent() {
        return mPresentationLayerComponent;
    }
}
