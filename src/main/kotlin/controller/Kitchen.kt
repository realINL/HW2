package controller

import model.Chef
import model.Dish
import model.Order
import model.OrderedDishesTable
import org.sqlite.core.DB
import repository.Database
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread
import kotlin.random.Random

class Kitchen {

    val dishesToPrepare: MutableList<Dish> = CopyOnWriteArrayList()
    fun addDish(dish: Dish) {
        synchronized(dishesToPrepare) {
            dishesToPrepare.add(dish)
        }

    }

    fun updateStatus(orderId: Int, orderedDishId: Int, status: String) {
        synchronized(db) {
            OrderedDishesTable(db).update(2, status, orderedDishId.toString())
            try {
                val order = Order(orderId, -1)
                order.nextStatus()
            } catch (e: IllegalArgumentException) {
            }
        }
    }


    fun startCooking() {
        val chefsCount = 5
        val chefs = List(chefsCount) { Chef(it + 1) }
        chefs.forEach { thread(start = true, isDaemon = true, block = { it.run() }) }
        cookUncooked()
    }

    private fun cookUncooked() {
        synchronized(db) {
            val dishesInQueue = OrderedDishesTable(db).select(2, 1, 2, "В очереди")
            if (dishesInQueue.isNotEmpty()) {
                dishesInQueue.forEach {
                    val dishParams = it.split('|')
                    synchronized(dishesToPrepare) {
                        addDish(
                            Dish(
                                dishParams[1].toInt(),
                                dishParams[2],
                                dishParams[3].toInt(),
                                dishParams[0].toInt()
                            )
                        )
                    }
                }
            }

            val dishesDidntCooked = OrderedDishesTable(db).select(2, 1, 2, "Готовится")
            if (dishesDidntCooked.isNotEmpty()) {
                dishesDidntCooked.forEach {
                    val dishParams = it.split('|')
                    synchronized(dishesToPrepare) {
                        addDish(
                            Dish(
                                dishParams[1].toInt(),
                                dishParams[2],
                                dishParams[3].toInt(),
                                dishParams[0].toInt()
                            )
                        )
                    }
                }
            }
        }
    }
}