package com.devtides.androidmonetisation.util

import android.app.Activity
import com.android.billingclient.api.*

class BillingAgent(val activity: Activity, val callback: BillingCallback): PurchasesUpdatedListener {

    private var billingClient = BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases().build()
    private val productsSKUList = listOf("country_view")
    private val productsList = arrayListOf<SkuDetails>()
    private val subscriptionsSKUList = listOf("countries_subscription")
    private val subscriptionsList = arrayListOf<SkuDetails>()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if(billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    getAvailableProducts()
                    getAvailableSubscriptions()
                }
            }

        })
    }

    fun onDestroy() {
        billingClient.endConnection()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
//        checkProduct(billingResult, purchases)
        checkSubscription(billingResult, purchases)
    }

    fun checkSubscription(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if(billingResult?.responseCode == BillingClient.BillingResponseCode.OK ||
                billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            callback.onTokenConsumed()
        }
    }

    fun checkProduct(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        purchases?.let {
            var token: String? = null
            if(billingResult?.responseCode == BillingClient.BillingResponseCode.OK &&
                    purchases.size > 0) {
                token = purchases.get(0).purchaseToken
            } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                val purchasesList = billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
                if(purchasesList.size > 0) {
                    token = purchasesList[0].purchaseToken
                }
            }

            token?.let {
                val params = ConsumeParams
                    .newBuilder()
                    .setPurchaseToken(token)
                    .setDeveloperPayload("Token consumed")
                    .build()
                billingClient.consumeAsync(params) {billingResult, purchaseToken ->
                    if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        callback.onTokenConsumed()
                    }
                }
            }

        }
    }

    fun getAvailableProducts() {
        if(billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(productsSKUList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productsList.clear()
                    productsList.addAll(skuDetailsList)
                }
            }
        }
    }

    fun getAvailableSubscriptions() {
        if(billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(subscriptionsSKUList)
                .setType(BillingClient.SkuType.SUBS)
                .build()
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if(billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    subscriptionsList.clear()
                    subscriptionsList.addAll(skuDetailsList)
                }
            }
        }
    }

    fun purchaseView() {
        if(productsList.size > 0) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(productsList[0])
                .build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    fun purchaseSubscription() {
        val list = billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList
        if(list.size > 0) {
            callback.onTokenConsumed()
        } else {
            if(subscriptionsList.size > 0) {
                val billingFlowParams = BillingFlowParams
                    .newBuilder()
                    .setSkuDetails(subscriptionsList[0])
                    .build()
                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }
}