package com.pashkobohdan.ttsreader.free.ui.fragments.cloudBook

import android.os.Bundle
import android.view.View
import butterknife.BindView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.ui.fragments.cloudBook.CloudBookListFragment

class FreeCloudBookListFragment : CloudBookListFragment() {

    @BindView(R.id.adView)
    lateinit var mAdView : AdView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}