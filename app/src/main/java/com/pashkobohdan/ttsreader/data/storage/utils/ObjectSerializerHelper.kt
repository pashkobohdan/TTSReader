package com.pashkobohdan.ttsreader.data.storage.utils

import android.util.Base64
import java.io.*


object ObjectSerializerHelper {

    fun objectToString(`object`: Serializable): String? {
        var encoded: String? = null
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(`object`)
            objectOutputStream.close()
            encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return encoded
    }

    fun stringToObject(string: String): Serializable? {
        val bytes = Base64.decode(string, 0)
        var `object`: Serializable? = null
        try {
            val objectInputStream = ObjectInputStream(ByteArrayInputStream(bytes))
            `object` = objectInputStream.readObject() as Serializable
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        return `object`
    }

}