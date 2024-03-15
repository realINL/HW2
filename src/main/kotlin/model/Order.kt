package model

import controller.db
import controller.kitchen
import view.ChoiceMenuPrint
import view.PrintColor

class Order(userId: Int, oderId: Int = -1, alredyExist: Boolean = false) : Entity() {
    var userId: Int
    private var status: String
    var orderId: Int = oderId
    private val alredyExist: Boolean
    private var _Bill: Int = 0
    val Bill: Int
        get() = _Bill
    private var dishList: MutableList<Dish> = mutableListOf()
    var alreadyExistDishList: MutableList<Dish> = mutableListOf()

    private val statusList = listOf(
        "Принят в обработку",
        "Готовится",
        "Готов",
        "Оплачен"
    )
    private var orderedDishIdToStart: Int = -1

    init {
        this.userId = userId
        status = statusList[0]
        this.alredyExist = alredyExist
        values.add(userId.toString())
        values.add(status)
    }


    constructor(oderId: Int, userId: Int) : this(userId) {
        val result: MutableList<String>
        synchronized(db) {
            result = OrdersTable(db).select(2, 1, 0, oderId.toString())
        }
        if (result.isNotEmpty()) {
            val orderParams = result[0].split('|')
            this.orderId = oderId
            this.userId = orderParams[1].toInt()
            status = orderParams[2]
            val dishes = OrderedDishesTable(db).select(2, 1, 3, oderId.toString())
            dishes.forEach {
                val dishParams = it.split('|')
                val dish = Dish(dishParams[1].toInt(), dishParams[2], orderId, dishParams[0].toInt())
                dishList.add(dish)
                _Bill += dish.price
            }
        } else throw IllegalArgumentException("Dish with dishId $oderId not found in the database")

    }

    private fun newOrder() {
        OrdersTable(db).insert(this)
    }

    fun addDish(dish: Dish, amountOfDishes: Int) {
        repeat(amountOfDishes) {
            dishList.add(dish.copy())
        }


    }

    fun checkOrder() {
        var bill = 0
        val alredyExistDishListSize = alreadyExistDishList.size
        PrintColor().print("Ваш заказ:", 34)
        alreadyExistDishList.forEachIndexed { index, dish ->
            println("\t${((index + 1).toString().padEnd(3))} - ${dish.dishName.padEnd(66)} ${dish.price} р.")
            bill += dish.price
        }
        dishList.forEachIndexed { index, dish ->
            println(
                "\t${
                    ((index + 1 + alredyExistDishListSize).toString().padEnd(3))
                } - ${dish.dishName.padEnd(66)} ${dish.price} р."
            )
            bill += dish.price
        }
        PrintColor().print("\tСумма${"".padEnd(64)} = ${bill} р.", 35)
        println()
    }

    fun pushOrder() {
        if (dishList.isEmpty()) {
            PrintColor().print("Заказ пуст!", 31)
            return
        }


        if (orderId == -1) {
            newOrder()
            orderId = OrdersTable(db).getLastInsertId()
        }


        orderedDishIdToStart =
            Sqlite_sequenceTable(db).select(2, 1, 0, "orderedDishes", "name")[0].split('|')[1].toInt() + 1


        dishList.forEach {
            it.setId(orderId)
            synchronized(db) {
                it.orderedDishId = orderedDishIdToStart
                OrderedDishesTable(db).insert(it)
                orderedDishIdToStart += 1
            }
            synchronized(db) {
                val available = MenuTable(db).select(2, 1, 0, it.dishId.toString())[0].split('|')[4].toInt()
                MenuTable(db).update(4, (available - 1).toString(), it.dishId.toString())
            }

            synchronized(kitchen.dishesToPrepare) {
                kitchen.addDish(it)
            }

            _Bill += it.price
        }

        if (alredyExist) {
            synchronized(db) {
                addDishesToCreatedOrder()
            }
        }


        PrintColor().print("Заказ принят в обработку!", 34)
    }

    private fun addDishesToCreatedOrder() {
        synchronized(db) {
            val alreadyExistedStatus = OrdersTable(db).select(2, 1, 0, orderId.toString())[0].split('|')[2]
            if (alreadyExistedStatus == "Готов")
                OrdersTable(db).update(2, statusList[1], orderId.toString())
        }
    }

    fun deleteDishFormList(existedOrder: Boolean = false) {
        println("Удалить блюдо:")
        var left: List<Int> = listOf()
        val right: MutableList<Pair<String, () -> Unit>> = mutableListOf()
        dishList.forEach {
            right.add(Pair(it.dishName) { dishList.remove(it) })
        }

        left = (1..dishList.size).toList() + listOf(dishList.size + 1, 0)
        right.add(Pair("Удалить всё") { dishList.clear() })
        right.add(Pair("Назад") { return@Pair })
        val choiceMenu = ChoiceMenu(left, right)
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }

    fun nextStatus() {
        val currentIndex = statusList.indexOf(status)
        if ((currentIndex != -1) && (currentIndex < 3)) {
            if (currentIndex == 1) {
                dishList.forEach {
                    if (it.status != "Готово") {
                        return
                    }
                }
            }
            status = statusList[currentIndex + 1]
            OrdersTable(db).update(2, status, orderId.toString())
        }
    }

    fun getStatus(): String {
        return status
    }

    fun getDishList(): MutableList<Dish> {
        return dishList
    }

    fun delete() {
        synchronized(db) {
            OrdersTable(db).delete(orderId.toString())
        }
        synchronized(kitchen.dishesToPrepare) {
            dishList.forEach {
                kitchen.dishesToPrepare.remove(it)
            }
        }

    }

}