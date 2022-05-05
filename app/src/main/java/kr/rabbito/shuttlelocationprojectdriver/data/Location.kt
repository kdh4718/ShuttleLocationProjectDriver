package kr.rabbito.shuttlelocationprojectdriver.data

class Location(
    val driverName: String,
    var latitude: Double,
    var longitude: Double
) {
    constructor() : this("", 0.0, 0.0)
}