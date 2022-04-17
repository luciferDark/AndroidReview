package com.ll.test;

import com.ll.retrofitlib.annotations.Field;
import com.ll.retrofitlib.annotations.Get;
import com.ll.retrofitlib.annotations.Post;
import com.ll.retrofitlib.annotations.Query;

import okhttp3.Call;

public interface WeatherApi {
    @Post("/v3/weather/weatherInfo")
    Call postWeather(@Field("city") String city, @Field("key") String key);


    @Get("/v3/weather/weatherInfo")
    Call  getWeather(@Query("city") String city, @Query("key") String key);
}
