package controller

import model.User

class AuthenticationReader {

    fun readUser(): User {
        println("login:")
        val login = readln()
        println("password:")
        val password = readln()
        return User(login, password)
    }
}