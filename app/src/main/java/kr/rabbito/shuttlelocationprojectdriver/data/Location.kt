package kr.rabbito.shuttlelocationprojectdriver.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Location(
    val driverName: String,
    var logitude: Double,
    var latitude: Double
) : Parcelable {
    constructor() : this("", 0.0, 0.0)
}