package com.pinyaoting.garcondecuisine.dependencies.components.application;

import android.app.Application;

import com.pinyaoting.garcondecuisine.dependencies.modules.core.AppModule;
import com.pinyaoting.garcondecuisine.dependencies.modules.core.NetModule;

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
