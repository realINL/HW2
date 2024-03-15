package model

import repository.Database
import controller.db
import controller.kitchen
import view.PrintColor

class Chef(chefId: Int) : Runnable {
    val chefId: Int

    init {
        this.chefId = chefId
    }

    private fun cook(dish: Dish) {
        synchronized(db) {
            kitchen.updateStatus(dish.orderId!!.toInt(), dish.orderedDishId!!.toInt(), "Готовится")
        }

        val time: Long = (dish.timeToWait / 10 * 6000).toLong()

        Thread.sleep(time)
        PrintColor().print("Блюдо ${dish.dishName} готово", 35)

        synchronized(db) {
            kitchen.updateStatus(dish.orderId!!.toInt(), dish.orderedDishId!!.toInt(), "Готово")
        }
    }

    private fun getDish(): Dish? {

        synchronized(kitchen.dishesToPrepare) {
            if (kitchen.dishesToPrepare.isNotEmpty()) {
                val dish: Dish

                dish = kitchen.dishesToPrepare.removeAt(0)

                return dish
            }
        }
        return null
    }


    override fun run() {
        while (true) {
            val dish = getDish()
            dish?.let { cook(dish) }

            Thread.sleep(100)
        }
    }
}
