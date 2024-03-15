package view

import model.ChoiceMenu

class ChoiceMenuPrint {

    fun printMenu(choiceMenu: ChoiceMenu) {
        for (elem in choiceMenu.menu) {
            println("${elem.key} - ${elem.value.first}")
        }
        println("\u001B[36mВыберите один вариант\u001B[0m")
    }
}