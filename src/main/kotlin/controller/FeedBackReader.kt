package controller

import model.ChoiceMenu
import model.Dish
import model.FeedBack
import model.MakeChoice
import repository.Database
import view.ChoiceMenuPrint
import view.PrintColor
import kotlin.system.exitProcess

class FeedBackReader(val dishId: Int, val dishName: String) {
    fun read(): FeedBack {
        PrintColor().print("Ваше впечатление о ${dishName}:", 36)
        val feedBack = readln()
        return FeedBack(feedBack, dishId)
    }

}

