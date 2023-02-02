package com.falcon.video.downloader.Helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.skytek.live.wallpapers.MainLauncher.adcheck
import com.skytek.live.wallpapers.R
import org.jetbrains.annotations.NotNull
import java.util.*


class AdsManager {


    @SuppressLint("StaticFieldLeak")
    var nativeAdView: NativeAdView? = null
     var mInterstitial: InterstitialAd? = null

    @SuppressLint("StaticFieldLeak")
    var adContainerView: FrameLayout? = null
    var adView: AdView? = null
    var nativeAd: NativeAd? = null

    @SuppressLint("StaticFieldLeak")
    var nativeAdContainer: FrameLayout? = null

    @SuppressLint("MissingPermission")
    fun loadBanner(activity: Activity, id: Int) {
        adContainerView = activity.findViewById(id)
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = AdView(activity)
        adView!!.adUnitId = activity.getString(R.string.Ad_Mob_Banner_ID)
        adContainerView?.addView(adView)
        val adRequest = AdRequest.Builder()
            .build()
        val adSize = getAdSize(activity)
        // Step 4 - Set the adaptive ad size on the ad view.
        adView!!.adSize = adSize
        // Step 5 - Start loading the ad in the background.
        adView!!.loadAd(adRequest)
    }

    fun getAdSize(activity: Activity): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

        fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView =
                adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            (Objects.requireNonNull(adView.headlineView) as TextView).text = nativeAd.headline
            Objects.requireNonNull(adView.mediaView)
                .setMediaContent(Objects.requireNonNull(nativeAd.mediaContent))

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                Objects.requireNonNull(adView.bodyView).visibility = View.INVISIBLE
            } else {
                Objects.requireNonNull(adView.bodyView).visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                Objects.requireNonNull(adView.callToActionView).visibility = View.INVISIBLE
            } else {
                Objects.requireNonNull(adView.callToActionView).visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                Objects.requireNonNull(adView.iconView).visibility = View.GONE
            } else {
                (Objects.requireNonNull(adView.iconView) as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
                )
                adView.iconView.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                Objects.requireNonNull(adView.priceView).visibility = View.INVISIBLE
            } else {
                Objects.requireNonNull(adView.priceView).visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                Objects.requireNonNull(adView.storeView).visibility = View.INVISIBLE
            } else {
                Objects.requireNonNull(adView.storeView).visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                Objects.requireNonNull(adView.starRatingView).visibility = View.INVISIBLE
            } else {
                (Objects.requireNonNull(adView.starRatingView) as RatingBar).rating =
                    nativeAd.starRating.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                Objects.requireNonNull(adView.advertiserView).visibility = View.INVISIBLE
            } else {
                (Objects.requireNonNull(adView.advertiserView) as TextView).text =
                    nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
        }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */


    @SuppressLint("MissingPermission")
    fun refreshAd(activity: Activity, id: Int) {
            val builder = AdLoader.Builder(
                activity,
                activity.resources.getString(R.string.Ad_Mob_Native_ID)
            )
            builder.forNativeAd { nativeAd: NativeAd ->
                val frameLayout = activity.findViewById<FrameLayout>(id)
                @SuppressLint("InflateParams") val adView = activity.layoutInflater
                    .inflate(R.layout.ad_unified, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
            }
            val videoOptions = VideoOptions.Builder()
                .build()
            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()
            builder.withNativeAdOptions(adOptions)
            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(@NotNull error: LoadAdError) {
                    Log.d("Ads", error.toString())
                }
            }).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }



    fun loadNativeAdd(ctx: Activity, id: Int) {
        val builder = AdLoader.Builder(ctx, ctx.getString(R.string.Ad_Mob_Native_ID))
        builder.forNativeAd { nativeAd1 ->
            if (nativeAd != null) {
                nativeAd?.destroy()
            }
            nativeAd = nativeAd1
            nativeAdContainer = ctx.findViewById(id)
            nativeAdView =
                ctx.layoutInflater.inflate(R.layout.ad_unified_small, null) as NativeAdView
            populateNativeAdView2(nativeAd1, nativeAdView!!)
            nativeAdContainer?.removeAllViews()
            nativeAdContainer?.addView(nativeAdView)
            nativeAdContainer?.visibility = View.VISIBLE
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            fun onAdFailedToLoad(errorCode: Int) {}
            override fun onAdClicked() {
                // Log the click event or other custom behavior.
            }

//                fun onAdLeftApplication() {
//                    super.onAdLeftApplication()
//                }
        }).build()
        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }

    private fun populateNativeAdView2(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        //adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.priceView = adView.findViewById(R.id.ad_price)
        val constraint: ConstraintLayout = adView.findViewById(R.id.constraint)
        val ad_body = adView.findViewById<TextView>(R.id.ad_body)
        val ad_headline = adView.findViewById<TextView>(R.id.ad_headline)


        //adView.setStoreView(adView.findViewById(R.id.ad_store));
        //adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.GONE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.GONE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraint)
            constraintSet.connect(
                R.id.ad_headline,
                ConstraintSet.TOP,
                constraint.id,
                ConstraintSet.TOP,
                24
            )
            constraintSet.connect(
                R.id.ad_headline,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                32
            )
            constraintSet.connect(
                R.id.ad_body,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                32
            )
            constraintSet.connect(
                R.id.ad_body,
                ConstraintSet.TOP,
                R.id.ad_headline,
                ConstraintSet.BOTTOM,
                3
            )
            ad_headline.textSize = 16f
            ad_body.textSize = 14f
            constraintSet.applyTo(constraint)
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }


    fun load_inter(ctx: Context) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            ctx,
            ctx.resources.getString(R.string.Ad_Mob_Intersticial_ID),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitial = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitial = interstitialAd
                    mInterstitial?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                adcheck=false;
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                            }

                            override fun onAdShowedFullScreenContent() {
                                mInterstitial = null
                            }
                        }
                }
            })
    }

    fun show_inter(act: Activity): Boolean {
        if (mInterstitial != null) {
            mInterstitial?.show(act)
            return true
        } else {
            return false
        }
    }


}