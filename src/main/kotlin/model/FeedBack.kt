package model

class FeedBack(feedBack: String, dishId: Int) : Entity() {
    val feedBack: String
    val dishId: Int

    init {
        this.feedBack = feedBack
        this.dishId = dishId
        values.add(dishId.toString())
        values.add(feedBack)
    }

    override fun toString(): String {
        val dish = Dish(dishId)
        return "${dish.dishName} - $feedBack"
    }
}