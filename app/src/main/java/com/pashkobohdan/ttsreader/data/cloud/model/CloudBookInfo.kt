package com.pashkobohdan.ttsreader.data.cloud.model

import com.pashkobohdan.ttsreader.data.model.dto.common.CommonDTO

class CloudBookInfo : CommonDTO{

    var name: String? = null
    var author: String? = null
    var text: String? = null
    var fullTextKey: String? = null

    constructor() {}

    constructor(name: String, author: String, text: String, fullTextKey: String) {
        this.name = name
        this.author = author
        this.text = text
        this.fullTextKey = fullTextKey
    }
}
