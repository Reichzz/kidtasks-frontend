package com.puce.kidtasks_ar;
import retrofit2.Call;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIChild {
    @GET("children/")
    Call<List<Child>> getChildrenI();

    @POST("children/")
    Call<Void> addChildI(
            @Body Child child
    );

    @PUT("children/{id}/")
    Call<Void> updateChildI(
            @Path("id") String id,
            @Body Child child
    );

    @DELETE("children/{id}/")
    Call<Void> deleteChildI(
            @Path("id") String id
    );
    @POST("children/{id}/reset-streak/")
    Call<Void> resetStreak(@Path("id") String id);

}
