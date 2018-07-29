package com.pashkobohdan.ttsreader.free.ui.fragments.book.reading

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import butterknife.BindView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.free.utils.FreeVersionConstants.AVAILABLE_PITCH_FREE_CHANGE
import com.pashkobohdan.ttsreader.free.utils.FreeVersionConstants.AVAILABLE_SPEED_FREE_CHANGE
import com.pashkobohdan.ttsreader.free.utils.FreeVersionConstants.PRO_VERSION_PACKAGE_NAME
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils
import com.pashkobohdan.ttsreader.ui.fragments.book.reading.BookFragment
import com.pashkobohdan.ttsreader.utils.Constants.DEFAULT_PITCH_RATE
import com.pashkobohdan.ttsreader.utils.Constants.DEFAULT_SPEED_RATE


class FreeBookFragment : BookFragment() {

    @BindView(R.id.adView)
    lateinit var mAdView : AdView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun tryChangeSpeech(newPitch: Int) {
        if (newPitch >= DEFAULT_PITCH_RATE - AVAILABLE_PITCH_FREE_CHANGE
                && newPitch <= DEFAULT_PITCH_RATE + AVAILABLE_PITCH_FREE_CHANGE) {
            super.tryChangeSpeech(newPitch)
        } else {
            resetPitchSeekBarValueToNormal(newPitch)
            showFreeVersionChangeParameters()
        }
    }

    override fun tryChangeSpeed(newSpeed: Int) {
        if (newSpeed >= DEFAULT_SPEED_RATE - AVAILABLE_SPEED_FREE_CHANGE
                && newSpeed <= DEFAULT_SPEED_RATE + AVAILABLE_SPEED_FREE_CHANGE) {
            super.tryChangeSpeed(newSpeed)
        } else {
            resetSpeedSeekBarValueToNormal(newSpeed)
            showFreeVersionChangeParameters()
        }
    }

    private fun resetPitchSeekBarValueToNormal(newPitch: Int) {
        if (minOf(newPitch, pitchSeekBar.progress) < DEFAULT_SPEED_RATE - AVAILABLE_SPEED_FREE_CHANGE) {
            pitchSeekBar.setProgress(DEFAULT_SPEED_RATE - AVAILABLE_SPEED_FREE_CHANGE)
        } else if (maxOf(newPitch, pitchSeekBar.progress) > DEFAULT_SPEED_RATE + AVAILABLE_SPEED_FREE_CHANGE) {
            pitchSeekBar.setProgress(DEFAULT_SPEED_RATE + AVAILABLE_SPEED_FREE_CHANGE)
        }
    }

    private fun resetSpeedSeekBarValueToNormal(newSpeed: Int) {
        if (minOf(newSpeed, speedSeekBar.progress) < DEFAULT_SPEED_RATE - AVAILABLE_SPEED_FREE_CHANGE) {
            speedSeekBar.setProgress(DEFAULT_SPEED_RATE - AVAILABLE_SPEED_FREE_CHANGE)
        } else if (maxOf(newSpeed, speedSeekBar.progress) > DEFAULT_SPEED_RATE + AVAILABLE_SPEED_FREE_CHANGE) {
            speedSeekBar.setProgress(DEFAULT_SPEED_RATE + AVAILABLE_SPEED_FREE_CHANGE)
        }
    }

    private fun showFreeVersionChangeParameters() {
        DialogUtils.showConfirm(getString(R.string.free_version), getString(R.string.free_version_get_pro),
                getString(R.string.get_pro_version), getString(R.string.cancel),
                context ?: throw IllegalStateException("Context is null"), {
            openProVersionInGP()
        }, {})
    }

    private fun openProVersionInGP() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PRO_VERSION_PACKAGE_NAME)))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PRO_VERSION_PACKAGE_NAME)))
        }
    }

    companion object {


        fun getNewInstance(bookId: Long): FreeBookFragment {
            return saveData(FreeBookFragment(), bookId)
        }
    }
}