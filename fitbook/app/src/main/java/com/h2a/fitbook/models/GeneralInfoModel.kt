package com.h2a.fitbook.models

class GeneralInfoModel(
    var _height: Double,
    var _weight: Double,
    var _age: Int,
) {
    fun clone() = GeneralInfoModel(_height, _weight, _age)
    fun toHashMap(): HashMap<String, Any>? {
        return if (_height == 0.0 && _weight == 0.0)
            null
        else {
            if (_height == 0.0)
                hashMapOf(
                    "weight" to _weight
                )
            else {
                if (_weight == 0.0) {
                    hashMapOf(
                        "height" to _height
                    )
                } else
                    hashMapOf(
                        "weight" to _weight,
                        "height" to _height
                    )
            }
        }
    }
}