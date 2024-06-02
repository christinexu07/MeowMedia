package com.lukasandchristine.meowmedia.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    var email: String? = null,
    var followerCount: Int = 0,
    var followingCount: Int = 0,
    var profileDescription: String? = "",
    var profilePicture: String? = "",
    var username: String? = null,
    var ownerId: String? = null,
    var followingList: List<Users>? = null,
    var objectId: String? = null,
): Parcelable
