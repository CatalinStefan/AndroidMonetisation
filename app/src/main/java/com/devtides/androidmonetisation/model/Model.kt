package com.devtides.androidmonetisation.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

val TYPE_COUNTRY = 0
val TYPE_AD = 1

open class ListItem(val type: Int)

data class Country(
    @SerializedName("name")
    val countryName: String?,
    @SerializedName("capital")
    val capital: String?,
    @SerializedName("flagPNG")
    val flag: String?,
    @SerializedName("population")
    val population: String?,
    @SerializedName("area")
    val area: String?,
    @SerializedName("region")
    val region: String?
): ListItem(TYPE_COUNTRY), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(countryName)
        parcel.writeString(capital)
        parcel.writeString(flag)
        parcel.writeString(population)
        parcel.writeString(area)
        parcel.writeString(region)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Country> {
        override fun createFromParcel(parcel: Parcel): Country {
            return Country(parcel)
        }

        override fun newArray(size: Int): Array<Country?> {
            return arrayOfNulls(size)
        }
    }
}

class BannerAd: ListItem(TYPE_AD)