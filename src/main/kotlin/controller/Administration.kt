package controller

import model.*
import view.ChoiceMenuPrint
import view.PrintColor
import kotlin.system.exitProcess

class Administration {


    val choiceMenu = ChoiceMenu(
        listOf(1, 2, 3, 4, 5, 0), listOf(
            Pair("Добавить позицию") { addDish() },
            Pair("Удалить позицию") { deleteDish() },
            Pair("Редактировать позицию") { editDish() },
            Pair("Посмотреть отзывы") { showFeedBack() },
            Pair("Посмотреть статистику") { showStatistic() },
            Pair("Выйти") { return@Pair })
    )

    fun start() {
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }


    private fun addDish() {
        val dish: Dish? = AdminReader().readDish()
        dish?.let {
            MenuTable(db).insert(dish)
            start()
        }
        start()

    }

    private fun deleteDish() {
        PrintColor().print("Выбертие какое блюдо удалить", 33)
        val dish = Menu().selectDish()
        dish?.let {
            MenuTable(db).delete(dish.dishId.toString())
        } ?: {
            deleteDish()
            start()
        }

    }

    private fun editDish() {
        PrintColor().print("Выбертие какое блюдо редактировать", 33)
        val dish = Menu().selectDish()
        dish?.let {
            var columnNumber = -1
            var change = ""
            val choiceMenu = ChoiceMenu(
                listOf(1, 2, 3, 4, 5, 0), listOf(
                    Pair("Название") {
                        change = AdminReader().readDishIntParam("Название"); change = readln(); columnNumber = 1
                    },
                    Pair("Цена") { change = AdminReader().readDishIntParam("Цена"); columnNumber = 2 },
                    Pair("Время ожидания") {
                        change = AdminReader().readDishIntParam("Время ожидания"); columnNumber = 3
                    },
                    Pair("Количество, доступное для заказа") {
                        change = AdminReader().readDishIntParam("Количество, доступное для заказа"); columnNumber = 4
                    },
                    Pair("Раздел меню") { change = AdminReader().readDishIntParam("Раздел меню"); columnNumber = 5 },
                    Pair("Выйти из приложения") { exitProcess(0) })
            )
            try {
                ChoiceMenuPrint().printMenu(choiceMenu)
                println("Вводите:")
                MakeChoice(choiceMenu).makeChoice()
            } catch (e: NumberFormatException) {
                PrintColor().print("Вводите числа!", 31)
            }
            MenuTable(db).update(columnNumber, change, dish.dishId.toString())
            start()
            return
        } ?: run {
            editDish()
            start()
            return
        }
    }

    private fun showFeedBack() {
        PrintColor().print("Выберите блюдо, отзыввы которого хотите посмотреть:", 36)
        val dish = Menu().selectDish()
        dish?.let {
            val result = FeedBackTable(db).select(2, 1, 1, it.dishId.toString())
            if (result.isNotEmpty()) {
                result.forEach {
                    val feedBack = it.split('|')
                    println("О блюде №${feedBack[1]} говорят: ${feedBack[2]}")
                }
                start()
                return
            }
            println("Нет отзывов")
            start()
        } ?: {
            showFeedBack()
            start()
        }

    }

    private fun showStatistic() {
        var money = 0
        val resOrders = OrdersTable(db).select(2, 1, 2, "Оплачен")
        if (resOrders.isNotEmpty()) {
            resOrders.forEach {
                val orderParam = it.split('|')
                val orderedDishes = OrderedDishesTable(db).select(2, 1, 3, orderParam[0])
                if (orderedDishes.isNotEmpty()) {
                    orderedDishes.forEach {
                        val dishParams = it.split('|')
                        val dish = Dish(dishParams[1].toInt())
                        money += dish.price
                    }
                }
            }
        }
        println("Выручка - $money")

        val listOfDishId: MutableList<Int> = mutableListOf()
        val resDishes = OrderedDishesTable(db).selectAll()

        if (resDishes.isNotEmpty()) {
            resDishes.forEach {
                val s = it.split('|')
                listOfDishId.add(s[1].toInt())
            }

            val mostPopularDish = listOfDishId.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
            val counts = listOfDishId.groupingBy { it }.eachCount()
                .toList()
                .sortedByDescending { it.second }
            mostPopularDish?.let {
                val dish = Dish(mostPopularDish)
                println("Самое популярное блюдо - ${dish.dishName}. Оно было заказано - ${counts.first.second} раз(а)")
                println("Далее по популярности:")
                counts.drop(1).forEachIndexed { index, (element, count) ->
                    val dish = Dish(element)
                    println("${index + 2} - ${dish.dishName.padEnd(60)} было заказано $count раз(а)")

                }
            }
            start()
            return
        } else {
            println("Не было заказов!")
        }

        start()
        return
    }
}