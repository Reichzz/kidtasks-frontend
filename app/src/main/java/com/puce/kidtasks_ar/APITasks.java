package com.puce.kidtasks_ar;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APITasks {
    @GET("tasks/")
    Call<List<Tasks>> getTasks(
            @Query("child_id") String child
    );

    @POST("tasks/")
    Call<Void> addTasksI(
            @Body Tasks tasks
    );

    @PUT("tasks/{id}/")
    Call<Void> updateTasksI(
            @Path("id") String id,
            @Body Tasks tasks
    );

    @DELETE("tasks/{id}/")
    Call<Void> deleteTasksI(
            @Path("id") String id
    );



}