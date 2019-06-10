package com.devtides.androidmonetisation.presenter

import com.devtides.androidmonetisation.model.CountriesService
import com.devtides.androidmonetisation.model.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CountriesPresenter(val view: View) {

    private val service = CountriesService()

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        service.getCountries()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Country>>() {
                override fun onSuccess(value: List<Country>?) {
                    view.setCountries(value)
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    view.onError()
                }

            })
    }

    fun onRetry() {
        fetchCountries()
    }

    interface View {
        fun setCountries(countries: List<Country>?)
        fun onError()
    }
}