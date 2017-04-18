package org.kidinov.rijksmuseum.data.remote;


import org.kidinov.rijksmuseum.util.BusEvents;
import org.kidinov.rijksmuseum.util.RxEventBus;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Interceptor of all http requests. Uses for setting up key and result format in each request.
 * Also it handles 403 response code and notify all subscribers about
 * {@link org.kidinov.rijksmuseum.util.BusEvents.AuthenticationError}
 */
public class RequestInterceptor implements Interceptor {
    private final RxEventBus rxEventBus;

    public RequestInterceptor(RxEventBus rxEventBus) {
        this.rxEventBus = rxEventBus;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl url = request.url().newBuilder()
                .addQueryParameter("key", RijksMuseumService.AUTH_KEY)
                .addQueryParameter("format", RijksMuseumService.RESULT_FORMAT)
                .build();

        request = request.newBuilder().url(url).build();

        Response response = chain.proceed(request);
        if (response.code() == 403) {
            Timber.e("Auth error - %s", response.body().string());
            rxEventBus.post(new BusEvents.AuthenticationError());
        }
        return response;
    }
}
