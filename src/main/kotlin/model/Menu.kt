package model

import controller.db
import repository.Database
import view.ChoiceMenuPrint

class Menu {

    private var selectedDish: Dish? = null


    fun selectDish(back: Boolean? = null): Dish? {
        back?.let { return null }
        printMenu()
        return selectedDish
    }

    private val choiceMenu = ChoiceMenu(
        listOf(1, 2, 3, 4, 5, 0), listOf(
            Pair("Закуски и салаты") { choosingDish("1") },
            Pair("Супы") { choosingDish("2") },
            Pair("Основные блюда") { choosingDish("4") },
            Pair("Паста") { choosingDish("3") },
            Pair("Десерты") { choosingDish("5") },
            Pair("Назад") { selectDish(true) })
    )

    private fun printMenu() {
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }


    private fun choosingDish(menuPartNumber: String) {
        val menuPart = MenuTable(db).select(2, 1, 5, menuPartNumber)
        var left: List<Int> = listOf()
        val right: MutableList<Pair<String, () -> Unit>> = mutableListOf()
        if (menuPart.isNotEmpty()) {
            var size: Int = menuPart.size
            menuPart.forEach { item ->
                val splittedItem = item.split('|')
                val dish = Dish(
                    splittedItem[0].toInt(),
                    splittedItem[1],
                    splittedItem[2].toInt(),
                    splittedItem[3].toInt(),
                    splittedItem[4].toInt()
                )
                if (dish.available != 0) {
                    right.add(Pair(dish.toString()) { dishSelect(dish) })
                } else {
                    size -= 1
                }
            }
            left = (1..size).toList() + listOf(0)
        }
        right.add(Pair("Назад") { back() })
        val choiceMenu = ChoiceMenu(left, right)
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }


    private fun back() {
        printMenu()
    }

    private fun dishSelect(dish: Dish) {
        selectedDish = dish
    }


}