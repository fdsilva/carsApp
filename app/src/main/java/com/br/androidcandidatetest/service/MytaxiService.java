package com.br.androidcandidatetest.service;

import com.br.androidcandidatetest.model.VehicleCathalog;

import retrofit2.Call;
import retrofit2.http.GET;

/**This iterface represents the methods to make API requests  */
public interface MytaxiService {
    public static final String BASE_URL = "http://redirect.mytaxi.net/car2go/";

    @GET("vehicles.json")
    Call<VehicleCathalog> listAllVehicles();
}
