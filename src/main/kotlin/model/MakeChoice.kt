package model

import view.PrintColor

class MakeChoice(choiceMenu: ChoiceMenu) {
    private var choiceMenu: ChoiceMenu

    init {
        this.choiceMenu = choiceMenu
    }

    fun makeChoice() {
        var flag = false
        var choice: Int
        while (!flag) {
            try {
                choice = readln().toInt()
                if (choiceMenu.menu.containsKey(choice)) {
                    choiceMenu.menu[choice]?.second?.invoke()
                    flag = true
                } else {
                    PrintColor().print("Такого варианта не существует!", 31)
                }
            } catch (e: NumberFormatException) {

                PrintColor().print("Вводите число!", 31)
            }
        }
    }
}