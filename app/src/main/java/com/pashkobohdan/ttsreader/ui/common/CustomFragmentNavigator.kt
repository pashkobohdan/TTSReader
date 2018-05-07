package com.pashkobohdan.ttsreader.ui.common

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.Toast
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.ui.activities.MainActivity
import com.pashkobohdan.ttsreader.ui.navigation.FragmentProvider
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.*

class CustomFragmentNavigator(val fragmentProvider: FragmentProvider,
                              val activity: MainActivity,
                              val fragmentManager: FragmentManager,
                              val containerId: Int) : SupportFragmentNavigator(fragmentManager, containerId) {

    private val CLICK_AGAIN_TO_EXIT_TIME = 2 * 1000000000

    private var lastTryExitTime = 0L

    override fun applyCommand(command: Command?) {
        if (command is Forward) {
            val forward = command as Forward?
            val screen = forward!!.screenKey
            AbstractPresenter.currentScreen = screen
            fragmentManager
                    .beginTransaction()
                    .replace(containerId, createFragment(screen, forward.transitionData))
                    .addToBackStack(screen)
                    .commit()
        } else if (command is Back) {
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStackImmediate()
            } else {
                exit()
            }
        } else if (command is Replace) {
            val replace = command as Replace?
            if (fragmentManager.backStackEntryCount > 0) {
                val screen = replace!!.screenKey
                AbstractPresenter.currentScreen = screen
                fragmentManager.popBackStackImmediate()
                fragmentManager
                        .beginTransaction()
                        .replace(containerId, createFragment(screen, replace.transitionData))
                        .addToBackStack(screen)
                        .commit()
            } else {
                val screen = replace!!.screenKey
                AbstractPresenter.currentScreen = screen
                fragmentManager
                        .beginTransaction()
                        .replace(containerId, createFragment(screen, replace.transitionData))
                        .commit()
            }
        } else if (command is BackTo) {
            val key = command.screenKey
            AbstractPresenter.currentScreen = key

            if (key == null) {
                for (i in 0 until fragmentManager.backStackEntryCount) {
                    fragmentManager.popBackStack()
                }
                fragmentManager.executePendingTransactions()
            } else {
                var hasScreen = false
                for (i in 0 until fragmentManager.backStackEntryCount) {
                    if (key == fragmentManager.getBackStackEntryAt(i).name) {
                        fragmentManager.popBackStackImmediate(key, 0)
                        hasScreen = true
                        break
                    }
                }
                if (!hasScreen) {
                    backToUnexisting()
                }
            }
        } else if (command is SystemMessage) {
            showSystemMessage(command.message)
        }
    }

    override fun createFragment(screenKey: String?, data: Any?): Fragment {
        activity.hideProgressWithUnlock()
        return fragmentProvider.createFragment(screenKey, data)
    }

    override fun exit() {
        val currentTime = System.nanoTime()
        if (currentTime - lastTryExitTime < CLICK_AGAIN_TO_EXIT_TIME) {
            activity.finish()
        } else {
            Toast.makeText(activity, activity.getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show()
            lastTryExitTime = currentTime
        }
    }

    override fun showSystemMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
