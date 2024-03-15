package controller

import model.ChoiceMenu
import model.MakeChoice
import model.UserMenu
import model.UsersTable
import view.ChoiceMenuPrint
import view.PrintColor
import kotlin.system.exitProcess

class Authentication {


    val choiceMenu = ChoiceMenu(
        listOf(1, 2, 0), listOf(
            Pair("Войти") { logIn() },
            Pair("Зарегистрироваться") { signIn() },
            Pair("Выйти из приложения") { exitProcess(0) })
    )

    fun authentication() {
        ChoiceMenuPrint().printMenu(choiceMenu)
        MakeChoice(choiceMenu).makeChoice()
        authentication()
    }

    private fun logIn() {
        val user = AuthenticationReader().readUser()
        val isUserCorrect = user.checkUser(db)
        if (isUserCorrect) {
            PrintColor().print("Здравствуйте, ${user.name}!", 34)
            if (user.isAdmin()) {
                println("admin")
                Administration().start()
                return
            }
            val visitor = user.toVisitor()
            UserMenu(visitor.getId()).start()
            return
        }
        authentication()

    }

    private fun signIn() {
        val visitor = AuthenticationReader().readUser().toVisitor()

        val result = UsersTable(db).select(2, 1, 1, visitor.name)
        if (result.isNotEmpty()) {
            PrintColor().print("Пользлватель с таким именем уже существует!", 31)
            authentication()
            return
        }
        UsersTable(db).insert(visitor)
        visitor.setId()
        PrintColor().print("Пользователь зарегистрирован", 34)
        UserMenu(visitor.getId()).start()

    }
}
