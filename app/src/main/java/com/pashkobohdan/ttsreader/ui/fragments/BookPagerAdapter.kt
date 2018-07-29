package com.pashkobohdan.ttsreader.ui.fragments

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.service.readingData.ReadingText

class BookPagerAdapter(val mContext: Context, var text: ReadingText) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val page = text.pages[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(R.layout.book_page_layout, collection, false) as ViewGroup
        val pageTestView = layout.findViewById<TextView>(R.id.book_page_text)
        pageTestView.setText(page.text)
        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return text.pages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}