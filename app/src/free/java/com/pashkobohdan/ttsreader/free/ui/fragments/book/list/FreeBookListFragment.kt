package com.pashkobohdan.ttsreader.free.ui.fragments.book.list

import android.os.Bundle
import android.view.View
import butterknife.BindView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.free.utils.FreeVersionConstants.OPEN_BOOK_READING_INTERSTITIAL_AD_ID
import com.pashkobohdan.ttsreader.ui.fragments.book.list.BookListFragment

class FreeBookListFragment : BookListFragment(){

    @BindView(R.id.adView)
    lateinit var mAdView : AdView

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO remove test device
        val adRequest = AdRequest.Builder().addTestDevice("2EF6C016D7BEC7434A20FC330D5CF434").build()
        mAdView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = OPEN_BOOK_READING_INTERSTITIAL_AD_ID
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                reloadInterstitialAd()
            }
        }
        reloadInterstitialAd()
    }

    private fun reloadInterstitialAd() {
        //TODO remove test device
        mInterstitialAd.loadAd(AdRequest.Builder().addTestDevice("2EF6C016D7BEC7434A20FC330D5CF434").build())
    }

    override fun tryOpenBook(book: BookDTO) {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
        super.tryOpenBook(book)
    }
}