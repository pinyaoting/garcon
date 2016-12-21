package com.pinyaoting.garcon.application;

import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;
import com.pinyaoting.garcon.R;
import com.pinyaoting.garcon.dependencies.components.application.ApplicationComponent;
import com.pinyaoting.garcon.dependencies.components.application.DaggerApplicationComponent;
import com.pinyaoting.garcon.dependencies.components.data.DaggerDataLayerComponent;
import com.pinyaoting.garcon.dependencies.components.data.DataLayerComponent;
import com.pinyaoting.garcon.dependencies.components.domain.DaggerDomainLayerComponent;
import com.pinyaoting.garcon.dependencies.components.domain.DomainLayerComponent;
import com.pinyaoting.garcon.dependencies.components.presentation.DaggerPresentationLayerComponent;
import com.pinyaoting.garcon.dependencies.components.presentation.PresentationLayerComponent;
import com.pinyaoting.garcon.dependencies.modules.core.AppModule;
import com.pinyaoting.garcon.dependencies.modules.core.NetModule;
import com.pinyaoting.garcon.dependencies.modules.data.DataLayerModule;
import com.pinyaoting.garcon.dependencies.modules.domain.DomainLayerModule;
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

        Batch.Push.setGCMSenderId("797609406506");

        // TODO : switch to live Batch Api Key before shipping
        Batch.setConfig(new Config("DEV585AF9F92C546FDFC55E6CEA121")); // devloppement
        // Batch.setConfig(new Config("585AF9F92C1EB0B95B2171A0990A3F")); // live
        Batch.Push.setNotificationsColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //Batch.Push.setSmallIconResourceId(R.drawable.ic_notification);
    }

    public PresentationLayerComponent getPresentationLayerComponent() {
        return mPresentationLayerComponent;
    }
}
