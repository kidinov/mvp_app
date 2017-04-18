package org.kidinov.rijksmuseum.injection.module;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.kidinov.rijksmuseum.BuildConfig;
import org.kidinov.rijksmuseum.data.local.realm.RealmString;
import org.kidinov.rijksmuseum.data.remote.RequestInterceptor;
import org.kidinov.rijksmuseum.data.remote.RijksMuseumService;
import org.kidinov.rijksmuseum.data.remote.RxErrorHandlingCallAdapterFactory;
import org.kidinov.rijksmuseum.util.RxEventBus;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmList;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides network related dependencies.
 */
@Module
public class NetworkModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(RequestInterceptor requestInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(interceptor);
        }

        builder.addInterceptor(requestInterceptor);

        return builder
                .build();
    }

    @Provides
    @Singleton
    RequestInterceptor provideUnathorizedInterceptor(RxEventBus rxEventBus) {
        return new RequestInterceptor(rxEventBus);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        Type token = new TypeToken<RealmList<RealmString>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        builder.registerTypeAdapter(token, new TypeAdapter<RealmList<RealmString>>() {

            @Override
            public void write(JsonWriter out, RealmList<RealmString> value) throws IOException {
                out.beginArray();
                for (RealmString realmString : value) {
                    out.value(realmString.getVal());
                }
                out.endArray();
            }

            @Override
            public RealmList<RealmString> read(JsonReader in) throws IOException {
                RealmList<RealmString> list = new RealmList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(new RealmString(in.nextString()));
                }
                in.endArray();
                return list;
            }
        });

        return builder.create();
    }

    @Provides
    @Singleton
    RijksMuseumService provideRemoteService(Gson gson, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RijksMuseumService.ENDPOINT)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        return retrofit.create(RijksMuseumService.class);
    }
}
