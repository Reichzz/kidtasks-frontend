package com.puce.kidtasks_ar;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdaptadorRetrofit {
    Retrofit retrofit;

    public AdaptadorRetrofit() {

    }
    public Retrofit getAdaptador(){
        String URL = "https://kidtasks-backend-production.up.railway.app/";
        //String URL = "http://10.0.2.2:8000/";
        //String URL = "http://192.168.100.88:8000";
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return  retrofit;
    }
}
