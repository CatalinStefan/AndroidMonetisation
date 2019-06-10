package com.devtides.androidmonetisation.model

import io.reactivex.Single
import retrofit2.http.GET

interface CountriesApi {
    @GET("countriesV2.json")
    fun getCountries(): Single<List<Country>>
}