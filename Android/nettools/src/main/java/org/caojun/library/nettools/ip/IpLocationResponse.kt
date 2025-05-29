package org.caojun.library.nettools.ip

data class IpLocationResponse(
    val query: String,//"114.232.150.44"
    val status: String,//"success"
    val country: String,//"中国"
    val countryCode: String,//"CN"
    val region: String,//"JS"
    val regionName: String,//"江苏"
    val city: String,//"南京"
    val zip: String,//"210000"
    val lat: Double,//32.0607
    val lon: Double,//118.763
    val timezone: String,//"Asia/Shanghai"
    val isp: String,//"Chinanet"
    val org: String,//"Chinanet JS"
//    val as: String,//"AS4134 CHINANET-BACKBONE"
)