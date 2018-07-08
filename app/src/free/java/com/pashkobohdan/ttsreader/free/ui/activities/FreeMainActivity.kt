package com.pashkobohdan.ttsreader.free.ui.activities

import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.pashkobohdan.ttsreader.free.utils.FreeVersionConstants.ADS_APP_ID
import com.pashkobohdan.ttsreader.ui.activities.MainActivity

class FreeMainActivity : MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this, ADS_APP_ID);
    }
}