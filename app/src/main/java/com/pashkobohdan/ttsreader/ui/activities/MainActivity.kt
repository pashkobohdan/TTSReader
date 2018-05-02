package com.pashkobohdan.ttsreader.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderProApplication
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.ui.ActivityStartable
import com.pashkobohdan.ttsreader.ui.PermissionUtil
import com.pashkobohdan.ttsreader.ui.Screen.BOOK_LIST
import com.pashkobohdan.ttsreader.ui.Screen.BOOK_READING
import com.pashkobohdan.ttsreader.ui.common.CustomFragmentNavigator
import com.pashkobohdan.ttsreader.ui.fragments.book.list.BookListFragment
import com.pashkobohdan.ttsreader.ui.fragments.book.reading.BookFragment
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.commands.Replace
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ActivityStartable, PermissionUtil {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    @Inject
    lateinit var router: Router

    private val activityResultsMap: MutableMap<Int, (Intent?, Int) -> Unit> = mutableMapOf()
    private val permissionRequestMap: MutableMap<Int, () -> Unit> = mutableMapOf()

    private val navigator = object : CustomFragmentNavigator(supportFragmentManager, R.id.main_container) {
        private var lastTryExitTime = 0L

        override fun createFragment(screenKey: String, data: Any?): Fragment {
            when (screenKey) {
                BOOK_LIST -> return BookListFragment.newInstance
                BOOK_READING -> return BookFragment.getNewInstance(data as BookDTO)
                else -> throw IllegalArgumentException("Not supported screen: $screenKey")
            }
        }

        override fun showSystemMessage(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun exit() {
            val currentTime = System.nanoTime()
            if (currentTime - lastTryExitTime < CLICK_AGAIN_TO_EXIT_TIME) {
                finish()
            } else {
                Toast.makeText(this@MainActivity, getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show()
                lastTryExitTime = currentTime
            }
        }
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
        TTSReaderProApplication.INSTANCE.applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            navigator.applyCommand(Replace(BOOK_LIST, null))
        } else {
            navigator.applyCommand(Replace(BOOK_LIST, null))
            //TODO maybe don't need this because I have a moxy (with stateStrategy)
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
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            Manifest.permission.READ_CONTACTS)) {
//                // Show an explanation to the user *asynchronously* -- don't block this thread
//            } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
            permissionRequestMap.put(requestCode, grantedCallback)
//            }
        }
    }

    private fun isAllResultsGranted(grantResults: IntArray) : Boolean {
        for(result in grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        for(permissionKey in permissionRequestMap.keys) {
            if(requestCode.equals(permissionKey)) {
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

    companion object {
        private val CLICK_AGAIN_TO_EXIT_TIME = 2 * 1000000000
    }
}
