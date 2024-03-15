package view

import controller.db
import controller.kitchen
import model.Dish
import model.Order
import model.OrderedDishesTable
import repository.Database

class OrderPrint() {
//    private var db: Database

//    init {
//        this.db =db
//    }


    fun printOrders(orders: MutableList<Order>) {
        synchronized(db) {
            orders.forEachIndexed { index, order ->
                println("Заказ №${index + 1} - ${order.getStatus()}")
                order.getDishList().forEachIndexed { Index, orderedDish ->
                    println(
                        "\t${
                            ((Index + 1).toString().padEnd(3))
                        } - ${orderedDish.dishName.padEnd(66)} - ${orderedDish.status?.padEnd(15)} ${orderedDish.price} р."
                    )

                }
                PrintColor().print("\tСумма${"".padEnd(83)} = ${order.Bill} р.", 35)
//            println("\tСумма - ${dish.dishName.padEnd(50)}, ${splittedOrder.}")
            }
        }

    }

//    private val choiceMenu = ChoiceMenu(
//        listOf(1, 2), listOf(
////            Pair("Удалить профиль") { logIn() },
//            Pair("Редактировать заказ") { newOrder() },
//            Pair("Мои заказы") { myOrders() })
//    )
//
//    fun start() {
//        ChoiceMenuPrint().printMenu(choiceMenu)
//        MakeChoice(choiceMenu).makeChoice()
//    }
}