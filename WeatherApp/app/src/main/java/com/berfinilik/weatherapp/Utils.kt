package com.berfinilik.weatherapp

class Utils {
    companion object{
        var BASE_URL : String = "https://api.openweathermap.org/data/2.5/"//apı istekleri için temel url
        var API_KEY  : String = "e4f0e8a712de54aa7adf8bd7ae8a6db5"//apı istekleri için apı anahtarı
        const val PERMISSION_REQUEST_CODE = 123//kullanıcı izni isteklerinde kullanılacak sabit değer
    }
}