package com.devtides.androidmonetisation.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.devtides.androidmonetisation.R
import com.devtides.androidmonetisation.model.Country
import com.devtides.androidmonetisation.util.getProgressDrawable
import com.devtides.androidmonetisation.util.loadImage
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    lateinit var country: Country
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if(intent.hasExtra(PARAM_COUNTRY) && intent.getParcelableExtra<Country>(PARAM_COUNTRY) != null) {
            country = intent.getParcelableExtra(PARAM_COUNTRY)
        } else {
            finish()
        }

//        showInterstitialAd()

        populate()
    }

    private fun showInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_id)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                interstitialAd.show()
            }
        }
    }

    fun populate() {
        countryFlag.loadImage(country.flag, getProgressDrawable(this))
        textName.text = country.countryName
        textCapital.text = "Capital: ${country.capital}"
        textArea.text = "Area: ${country.area}"
        textPopulation.text = "Population: ${country.population}"
        textRegion.text = "Region: ${country.region}"
    }

    companion object {
        val PARAM_COUNTRY = "country"

        fun getIntent(context: Context, country: Country?): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(PARAM_COUNTRY, country)
            return intent
        }
    }
}
