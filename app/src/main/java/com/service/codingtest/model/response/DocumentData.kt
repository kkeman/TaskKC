package com.service.codingtest.model.response

import com.google.gson.annotations.SerializedName

data class DocumentData(
        @SerializedName("collection")
        val collection: String,

        @SerializedName("thumbnail_url")
        val thumbnail_url: String,

        @SerializedName("image_url")
        val image_url: String
)