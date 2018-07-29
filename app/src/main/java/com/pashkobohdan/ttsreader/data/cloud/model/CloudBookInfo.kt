package com.pashkobohdan.ttsreader.data.cloud.model

import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO

class CloudBookInfo : CommonDTO{

    var name: String = ""
    var author: String = ""
    var text: String = ""
    var fullTextKey: String = ""

    constructor()

    constructor(name: String, author: String, text: String, fullTextKey: String) {
        this.name = name
        this.author = author
        this.text = text
        this.fullTextKey = fullTextKey
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CloudBookInfo

        if (name != other.name) return false
        if (author != other.author) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }
}
