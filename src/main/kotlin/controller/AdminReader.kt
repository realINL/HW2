package controller

import model.ChoiceMenu
import model.Dish
import model.MakeChoice
import repository.Database
import view.ChoiceMenuPrint
import view.PrintColor
import kotlin.system.exitProcess

class AdminReader {
    fun readDish(): Dish? {

        var menuPart: Int = -1
        var dishPrice: Int = -1
        var dishTime: Int = -1
        var dishAvailable: Int = -1
        var backFlag = false
        println("Раздел меню для позиции")
        val choiceMenu = ChoiceMenu(
            listOf(1, 2, 3, 4, 5, 0), listOf(
                Pair("Закуски и салаты") { menuPart = 1 },
                Pair("Супы") { menuPart = 2 },
                Pair("Основные блюда") { menuPart = 3 },
                Pair("Паста") { menuPart = 4 },
                Pair("Десерты") { menuPart = 5 },
                Pair("Назад") { backFlag = true })
        )
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
        if (backFlag) {
            return null
        }


        println("Название позиции:")
        val dishName: String = readln()


        var isValidInput = false
        while (!isValidInput) {
            try {
                println("Цена позиции:")
                dishPrice = readln().toInt()
                println("Время приготовления:")
                dishTime = readln().toInt()
                println("Количество, доступное для заказа:")
                dishAvailable = readln().toInt()


                isValidInput = true
            } catch (e: NumberFormatException) {
                PrintColor().print("Вводите число!", 31)
            }
        }

        return Dish(-1, dishName, dishPrice, dishTime, dishAvailable, null, null, menuPart)
    }

    fun readDishIntParam(paramName: String): String {
        var isValidInput = false
        var param: Int = -1
        while (!isValidInput) {
            try {
                println("$paramName вводите:")
                param = readln().toInt()
                isValidInput = true
            } catch (e: NumberFormatException) {
                PrintColor().print("Вводите число!", 31)
            }
        }
        return param.toString()
    }

}

