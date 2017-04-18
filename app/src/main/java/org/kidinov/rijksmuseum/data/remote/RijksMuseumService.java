package org.kidinov.rijksmuseum.data.remote;

import org.kidinov.rijksmuseum.data.model.agenda.Agenda;
import org.kidinov.rijksmuseum.data.model.collection.Collection;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Interface used by Retrofit2 to generate service which will perform network calls
 */
public interface RijksMuseumService {
    String ENDPOINT = "https://www.rijksmuseum.nl/api/en/";
    String AUTH_KEY = "4zzNJ8r0";
    String RESULT_FORMAT = "json";

    @GET("agenda/{date}")
    Observable<Agenda> getAgenda(@Path("date") String date);

    //field "maker" in API seems doesn't work, so using just search
    @GET("collection")
    Observable<Collection> getCollectionByMaker(@Query("q") String maker, @Query("p") int page,
                                                @Query("ps") int limit, @Query("s") String order,
                                                @Query("imgonly") boolean imgOnly);
}
