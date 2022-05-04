package kr.rabbito.shuttlelocationprojectdriver.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Location(
    val driverName: String,
    var latitude: Double,
    var longitude: Double
) : Parcelable {
    constructor() : this("", 0.0, 0.0)
}