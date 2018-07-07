package com.pashkobohdan.ttsreader.data.cloud

import com.google.firebase.database.*
import com.pashkobohdan.ttsreader.data.cloud.model.CloudBookInfo
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.utils.TextSplitter
import java.util.*


object FirebaseHelper {

    private const val PUBLIC_BOOK_LIST_REF = "publicBooks"
    private const val PUBLIC_BOOK_TEXT_REF = "publicBooksTexts"
    private const val SHOWABLE_BOOK_TEXT_LENGTH = 400

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference()
    }

    private val publicBookDatabase: DatabaseReference by lazy {
        database.child(PUBLIC_BOOK_LIST_REF)
    }

    private val publicBookTextDatabase: DatabaseReference by lazy {
        database.child(PUBLIC_BOOK_TEXT_REF)
    }

    fun readBooksInfo(updateListener: (List<CloudBookInfo>) -> Unit, loadError: () -> Unit) {
        publicBookDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<CloudBookInfo>()
                for (snap in dataSnapshot.children) {
                    val bookInfo = snap.getValue(CloudBookInfo::class.java)
                    bookInfo?.let { list.add(it) }
                }
                updateListener(list)
            }

            override fun onCancelled(p0: DatabaseError) {
                loadError()
            }
        })
    }

    fun getBookDtoByInfo(info: CloudBookInfo, readListener: (BookDTO) -> Unit, loadError: () -> Unit) {
        val key = info.fullTextKey
        publicBookTextDatabase.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                loadError()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val text = p0.value as String
                val length = TextSplitter.sentencesCount(text)
                val bookDto = BookDTO(info.name, info.author,
                        text, length, 0, 100, 100, Date(), Date())
                readListener(bookDto)
            }
        })

    }

    fun uploadBookDTO(bookDTO: BookDTO, uploadSuccessful: () -> Unit, uploadError: () -> Unit) {
        val textRef = publicBookTextDatabase.push()
        val textKey = textRef.key
        if (textKey == null) {
            uploadError()
            return
        }
        textRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                mutableData.setValue(bookDTO.text);
                return Transaction.success(mutableData);
            }

            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                if (p0 != null) {
                    uploadError()
                } else {
                    publicBookDatabase.push().runTransaction(object : Transaction.Handler {

                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val partOfText = bookDTO.text.substring(0, SHOWABLE_BOOK_TEXT_LENGTH).plus("...")

                            mutableData.setValue(CloudBookInfo(bookDTO.name, bookDTO.author, partOfText, textKey))
                            return Transaction.success(mutableData);
                        }

                        override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                            if (p0 != null) {
                                uploadError()
                            } else {
                                uploadSuccessful()
                            }
                        }
                    })
                }
            }
        })
    }
}