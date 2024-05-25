package com.lukasandchristine.meowmedia.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    var email: String? = null,
    var followerCount: Int = 0,
    var followingCount: Int = 0,
    var postsList: List<Post>? = null,
    var profileDescription: String? = "",
    var profilePicture: String = "",
    var username: String? = null,
): Parcelable
