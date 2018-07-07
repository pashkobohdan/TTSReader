package com.pashkobohdan.ttsreader.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.ui.ActivityStartable
import com.pashkobohdan.ttsreader.ui.PermissionUtil
import com.pashkobohdan.ttsreader.ui.ProgressUtil
import com.pashkobohdan.ttsreader.ui.Screen.BOOK_LIST
import com.pashkobohdan.ttsreader.ui.Screen.BOOK_READING
import com.pashkobohdan.ttsreader.ui.common.CustomFragmentNavigator
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractScreenFragment
import com.pashkobohdan.ttsreader.ui.navigation.FragmentProvider
import com.pashkobohdan.ttsreader.utils.Constants
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject



class MainActivity : AppCompatActivity(), ActivityStartable, PermissionUtil, ProgressUtil {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    @Inject
    lateinit var router: Router
    @Inject
    lateinit var fragmentProvider: FragmentProvider

    @BindView(R.id.main_container)
    lateinit var mainContainer: View
    @BindView(R.id.main_progress_bar)
    lateinit var progressBar: View

    private val activityResultsMap: MutableMap<Int, (Intent?, Int) -> Unit> = mutableMapOf()
    private val permissionRequestMap: MutableMap<Int, () -> Unit> = mutableMapOf()

    private val navigator by lazy {
        CustomFragmentNavigator(
                fragmentProvider,
                this,
                supportFragmentManager,
                R.id.main_container)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TTSReaderApplication.INSTANCE.getApplicationComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        if (savedInstanceState == null) {
            navigator.applyCommand(Replace(BOOK_LIST, null))
        } else {
            navigator.applyCommand(Replace(BOOK_LIST, null))
            //TODO maybe don't need this because I have a moxy (with stateStrategy)
        }

        if (intent != null) {
            val bookId = intent.getLongExtra(Constants.OPEN_BOOK_ACTION_BOOK_ID_KEY, -1)
            if (bookId > 0) {
                navigator.applyCommand(Forward(BOOK_READING, bookId))
            }
        }
    }

    override fun onBackPressed() {
        router.exit()
    }

    override fun startActivityForResult(requestCode: Int, intent: Intent, callback: Function2<Intent?, Int, Unit>) {
        startActivityForResult(intent, requestCode)
        activityResultsMap.remove(requestCode)
        activityResultsMap.put(requestCode, callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultsMap[requestCode]?.invoke(data, resultCode)
        activityResultsMap.remove(requestCode)
    }

    private fun isPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this,
                            permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun requestPermission(permissions: Array<String>, requestCode: Int, grantedCallback: () -> Unit) {
        if (isPermissionsGranted(permissions)) {
            grantedCallback()
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
            permissionRequestMap.put(requestCode, grantedCallback)
        }
    }

    private fun isAllResultsGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        for (permissionKey in permissionRequestMap.keys) {
            if (requestCode.equals(permissionKey)) {
                if ((grantResults.isNotEmpty() && isAllResultsGranted(grantResults))) {
                    permissionRequestMap[permissionKey]?.invoke()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fragment = getActiveFragment()
            if(fragment is AbstractScreenFragment<*>) {
                fragment.onBackNavigation()
            }
            true
        } else super.onKeyDown(keyCode, event)

    }

    fun getActiveFragment(): Fragment {
        val fragments = supportFragmentManager.getFragments()
        return fragments[fragments.size - 1]
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showProgressWithLock() {
        showProgress()
        mainContainer.isEnabled = false
    }

    override fun hideProgressWithUnlock() {
        hideProgress()
        mainContainer.isEnabled = true
    }

}
