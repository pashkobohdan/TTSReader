package com.pashkobohdan.ttsreader.ui.navigation

import android.support.v4.app.Fragment
import com.pashkobohdan.ttsreader.ui.Screen
import com.pashkobohdan.ttsreader.ui.fragments.book.list.BookListFragment
import com.pashkobohdan.ttsreader.ui.fragments.book.reading.BookFragment
import com.pashkobohdan.ttsreader.ui.fragments.cloudBook.CloudBookListFragment

open class FragmentProvider {

    fun createFragment(screenKey: String?, data: Any?): Fragment {
        when (screenKey) {
            Screen.BOOK_LIST -> return createBookListFragment()
            Screen.BOOK_READING -> return createBookReadingFragment(data as Long)
            Screen.CLOUD_BOOK_LIST -> return createCloudBookListFragment()
            else -> throw IllegalArgumentException("Not supported screen: $screenKey")
        }
    }

    open fun createBookListFragment() = BookListFragment()
    open fun createBookReadingFragment(data: Long) = BookFragment.getNewInstance(data)
    open fun createCloudBookListFragment() = CloudBookListFragment()
}