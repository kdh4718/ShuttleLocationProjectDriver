package kr.rabbito.shuttlelocationprojectdriver.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Location(
    var location: String
) : Parcelable {
    constructor() : this("")
}