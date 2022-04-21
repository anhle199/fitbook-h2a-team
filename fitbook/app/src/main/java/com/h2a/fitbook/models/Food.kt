package com.h2a.fitbook.models

class Food(
    var _name: String,
    var _calories: Float,
    var _imgLink: String,
    var _unit: String) {
    fun toHashMap(amount: Int): HashMap<String, Any> {
        return hashMapOf(
            "image" to _imgLink,
            "name" to _name,
            "caloriesPerUnit" to String.format("%.2f", _calories).toDouble(),
            "unit" to _unit,
            "quantity" to amount
        )
    }
}
