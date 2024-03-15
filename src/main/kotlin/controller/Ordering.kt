package controller

import model.*
import repository.Database
import view.ChoiceMenuPrint

class Ordering(visitorId: Int) {

    private var visitorId: Int
    private var order: Order

    init {

        this.visitorId = visitorId
        order = Order(visitorId)
    }

    constructor(order: Order) : this(-1) {
        this.order = Order(order.userId, order.orderId, true)
        this.order.orderId
        this.order.alreadyExistDishList = order.getDishList()
        start()
    }

    fun start() {
        Menu().selectDish()?.let { selectedDish ->
            selectingDishes(selectedDish)
        } ?: returnToUserMenu()

    }


    private fun returnToUserMenu() {

        return
    }

    private fun selectingDishes(selectedDish: Dish) {
        val quantityOfDishes: Int = OrderingReader().readQuantityOfDishes(selectedDish)
        order.addDish(selectedDish, quantityOfDishes)
        order.checkOrder()
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }


    private val choiceMenu = ChoiceMenu(
        listOf(1, 2, 3, 4, 0), listOf(
            Pair("Продолжить выбор") { start() },
            Pair("Изменть заказ") { change() },
            Pair("Посмотреть заказ") { checkOrder() },
            Pair("Заказать") { order.pushOrder() },
            Pair("Назад") { return@Pair })
    )

    private fun change() {
        order.deleteDishFormList()
        order.checkOrder()
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }

    private fun checkOrder() {
        order.checkOrder()
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }


}