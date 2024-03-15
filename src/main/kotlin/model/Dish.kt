package model

import controller.db

class Dish(
    dishId: Int,
    dishName: String,
    price: Int,
    timeToWait: Int,
    available: Int,
    status: String? = null,
    orderId: Int? = null,
    menuPart: Int? = null,
    orderedDishId: Int? = null
) : Entity() {

    var dishId: Int
    var dishName: String
    var orderId: Int?
    var price: Int
    var timeToWait: Int
    var status: String?
    var available: Int
    var menuPart: Int? = null
    var orderedDishId: Int? = null

    init {
        this.dishId = dishId
        this.dishName = dishName
        this.orderId = null
        this.price = price
        this.timeToWait = timeToWait
        this.available = available
        this.status = "В очереди"
        if (dishId == -1) {
            values.add(dishName)
            values.add(price.toString())
            values.add(timeToWait.toString())
            values.add(available.toString())
            values.add(menuPart.toString())

        } else {
            values.add(dishId.toString())
            orderId?.let { values.add(orderId.toString()) }
            status?.let { values.add(status) }
            menuPart?.let { this.menuPart = menuPart }
            orderedDishId?.let { this.orderedDishId = orderedDishId }
        }
    }


    constructor(dishId: Int, status: String? = null, orderId: Int? = null, orderedDishId: Int? = null) : this(
        dishId,
        "",
        0,
        0,
        0,
        status,
        orderId
    ) {
        this.dishId = dishId
        val dishInfo = fetchDishInfoFromDatabase(dishId)
        if (dishInfo.isNotEmpty()) {
            val dishParams = dishInfo[0].split('|')
            dishName = dishParams[1]
            orderId?.let { this.orderId = orderId }
            orderedDishId?.let { this.orderedDishId = orderedDishId }
            this.price = dishParams[2].toInt()
            this.timeToWait = dishParams[3].toInt()
            this.available = dishParams[4].toInt()
            this.status = "В очереди"
            values.add(dishId.toString())
            this.orderId?.let { values.add(it.toString()) }
            status?.let { this.status = status }
            status?.let { values.add(status) }
        } else {

            throw IllegalArgumentException("Dish with dishId $dishId not found in the database")
        }
    }


    @Synchronized
    private fun fetchDishInfoFromDatabase(dishId: Int): List<String> {
        synchronized(db) {
            return MenuTable(db).select(2, 1, 0, dishId.toString())
        }
    }


    fun setId(orderId: Int) {
        this.orderId = orderId
        values.add(orderId.toString())
    }

    fun copy(): Dish {
        return Dish(dishId, dishName, price, timeToWait, available, status, orderId, menuPart)
    }

    override fun toString(): String {
        return "${dishName}\n    Цена: $price р.\t\tВремя ожидания: $timeToWait"
    }
}
