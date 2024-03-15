package controller

import model.Dish

class OrderingReader {
    fun readQuantityOfDishes(dish: Dish): Int {
        println("Сколько порций ${dish.dishName}?")
        var flag = false
        var quantity = 0
        while (!flag) {
            try {
                quantity = readln().toInt()
                if (quantity <= dish.available) {
                    flag = true
                } else {
                    println("К сожалению, сейчас доступно только ${dish.available} порций")
                }
            } catch (e: NumberFormatException) {
                println("Вводите число!")
            }
        }
        return quantity
    }
}