package com.lukasandchristine.meowmedia.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comments(
    var ownerId: String? = null,
    var contents: String? = null,
    var likes: Int = 0
): Parcelable
