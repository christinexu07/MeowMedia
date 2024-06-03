package com.lukasandchristine.meowmedia.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Posts(
    var ownerId: String? = null,
    var postTitle: String? = null,
    var postDescription: String? = "",
    var postContent: String? = null,
    var isVideo: Boolean = false,
    var likeCount: Int = 0,
    var comments: List<Comments>? = null,
    var objectId: String? = null,
    var created: Long = 0,
): Parcelable
