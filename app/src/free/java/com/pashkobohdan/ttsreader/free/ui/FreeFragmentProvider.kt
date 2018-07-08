package com.pashkobohdan.ttsreader.free.ui

import com.pashkobohdan.ttsreader.free.ui.fragments.book.list.FreeBookListFragment
import com.pashkobohdan.ttsreader.free.ui.fragments.book.reading.FreeBookFragment
import com.pashkobohdan.ttsreader.free.ui.fragments.cloudBook.FreeCloudBookListFragment
import com.pashkobohdan.ttsreader.ui.navigation.FragmentProvider

class FreeFragmentProvider : FragmentProvider() {

    override fun createBookListFragment() = FreeBookListFragment()
    override fun createBookReadingFragment(data: Long) = FreeBookFragment.getNewInstance(data)
    override fun createCloudBookListFragment() = FreeCloudBookListFragment()
}