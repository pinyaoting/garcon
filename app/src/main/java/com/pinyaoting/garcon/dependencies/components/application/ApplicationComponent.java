package com.pinyaoting.garcon.dependencies.components.application;

import android.app.Application;

import com.pinyaoting.garcon.dependencies.modules.core.AppModule;
import com.pinyaoting.garcon.dependencies.modules.core.NetModule;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface ApplicationComponent {

    Application application();

    Map<Integer, Retrofit> retrofitMap();

}
