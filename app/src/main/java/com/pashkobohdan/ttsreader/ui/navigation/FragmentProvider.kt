package com.pashkobohdan.ttsreader.ui.navigation

import android.support.v4.app.Fragment
import com.pashkobohdan.ttsreader.ui.Screen
import com.pashkobohdan.ttsreader.ui.fragments.book.list.BookListFragment
import com.pashkobohdan.ttsreader.ui.fragments.book.reading.BookFragment
import com.pashkobohdan.ttsreader.ui.fragments.cloudBook.CloudBookListFragment
import javax.inject.Inject

class FragmentProvider @Inject constructor() {

    fun createFragment(screenKey: String?, data: Any?): Fragment {
        when (screenKey) {
            Screen.BOOK_LIST -> return BookListFragment.newInstance
            Screen.BOOK_READING -> return BookFragment.getNewInstance(data as Long)
            Screen.CLOUD_BOOK_LIST -> return CloudBookListFragment()
            else -> throw IllegalArgumentException("Not supported screen: $screenKey")
        }
    }
}