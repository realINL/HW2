package model

import controller.*
import repository.Database
import view.ChoiceMenuPrint
import view.OrderPrint
import view.PrintColor

//class UserMenu(name: String, password: String): User(name, password) {
class UserMenu(id: Int) {
    private val id: Int

    init {
        this.id = id
    }

    private val choiceMenu = ChoiceMenu(
        listOf(1, 2, 0), listOf(
            Pair("Новый заказ") { newOrder() },
            Pair("Мои заказы") { myOrders() },
            Pair("Выйти из аккаунта (несохранённые заказы удаляться)") { return@Pair })
    )

    fun start() {
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }

    private fun workWithOrders(orders: MutableList<Order>) {
        val choiceMenu = ChoiceMenu(
            listOf(1, 2, 3, 4, 0), listOf(
                Pair("Оплатить") { pay(orders) },
                Pair("Оценить заказ") { feedBack(orders) },
                Pair("Изменить заказ") { changeCreatedOrder(orders) },
                Pair("Отменить заказ") { deleteOrder(orders) },
                Pair("Назад") { return@Pair })
        )
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
    }

    private fun newOrder() {
        Ordering(id).start()
        start()


    }

    private fun myOrders() {
        val result = OrdersTable(db).select(2, 1, 1, id.toString())
        if (result.isEmpty()) {
            PrintColor().print("У вас нет заказов!", 33)
            start()
            return
        }
        val orders: MutableList<Order> = mutableListOf()
        result.forEach {
            val orderParams = it.split('|')
            orders.add(Order(orderParams[0].toInt(), id))
        }
        OrderPrint().printOrders(orders)
        workWithOrders(orders)
        start()
        return

    }

    private fun deleteOrder(orders: MutableList<Order>) {
        PrintColor().print("Выберите какой заказ удалить:", 36)
        val order = selectOrder(orders, "Принят в обработку", "Готовится", "Принят в обработку", "Готовится")
        order?.let {
            order.delete(); PrintColor().print("Заказ удалён!", 35)
            return
        }
        return

    }

    private fun pay(orders: MutableList<Order>) {
        PrintColor().print("Выберите какой заказ оплатить:", 36)
        val order = selectOrder(orders, "Готов", "Готов", "Готов", "Готов")
        order?.let {
            it.nextStatus()
            PrintColor().print("Заказ оплачен!", 35)
            return
        }
    }

    private fun selectOrder(
        orders: MutableList<Order>,
        status1: String = "Принят в обработку",
        status2: String = "Готовится",
        status3: String = "Готов",
        status4: String = "Оплачен"
    ): Order? {
        var left: List<Int>
        val right: MutableList<Pair<String, () -> Unit>> = mutableListOf()
        var size = 0
        var result: Order? = null
        orders.forEachIndexed { index, order ->
            if ((order.getStatus() == status1) || (order.getStatus() == status2) || (order.getStatus() == status3) || (order.getStatus() == status4)) {
                right.add(Pair("Заказ ${index + 1}") { result = order })
                size += 1
            }

        }
        if (size > 0) {
            left = (1..size).toList() + listOf(0)
        } else {
            left = listOf(0)
        }
        right.add(Pair("Назад") { result = null })
        val choiceMenu = ChoiceMenu(left, right)
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
        return result

    }

    fun changeCreatedOrder(orders: MutableList<Order>) {
        PrintColor().print(
            "Вы можете добавить блюда в  заказ.\nЕсли вы хоите отменить заказ. Используйте 'отменить заказ' на уровень выше",
            36
        )
        PrintColor().print("Выберите какой заказ изменить:", 36)
        val order =
            selectOrder(orders, "Принят в обработку", "Готовится", "Принят в обработку", "Готовится")
        order?.let {
            PrintColor().print("Добавтье блюда:", 36)
            Ordering(it)


            PrintColor().print("Заказ изменён!", 35)
            return
        }
    }

    fun feedBack(orders: MutableList<Order>) {
        PrintColor().print("Выберите какой заказ оценить:", 36)
        val order = selectOrder(orders, "Оплачен", "Оплачен", "Оплачен", "Оплачен")
        order?.let {
            var left: List<Int> = listOf()
            val right: MutableList<Pair<String, () -> Unit>> = mutableListOf()
            var size = 0
            val dishes = order.getDishList()
            dishes.forEach { dish ->
                size += 1
                right.add(Pair(dish.dishName) {
                    FeedBackTable(db).insert(
                        FeedBackReader(
                            dish.dishId,
                            dish.dishName
                        ).read()
                    )
                })
            }
            left = (1..size).toList() + listOf(0)
            right.add(Pair("Назад") { feedBack(orders); return@Pair })
            PrintColor().print("Выберите какое блюдо оценить:", 36)
            val choiceMenu = ChoiceMenu(left, right)
            ChoiceMenuPrint().printMenu(choiceMenu)
            MakeChoice(choiceMenu).makeChoice()
            return
        }

        return

    }


}