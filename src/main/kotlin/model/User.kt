package model

import controller.db
import repository.Database
import view.PrintColor
import java.math.BigInteger
import java.security.MessageDigest

open class User(name: String, password: String) : Entity() {
    val name: String

    private val password: String
    private val passwordToVisitor: String
    var userId: Int = -1

    init {
        this.name = name
        passwordToVisitor = password
        this.password = hash(password)
        values.add(this.name)
        values.add(this.password)
    }


    private fun hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun checkUser(db: Database): Boolean {
        val checkString = UsersTable(db).select(2, 1, 1, name)
        if (checkString.isNotEmpty()) {
            if (checkString[0].split('|')[2] == password) {
                userId = checkString[0].split('|')[0].toInt()
                return true
            }
        }
        PrintColor().print("Неверный логин или пароль!\nПопробуйте ещё раз или зарегистрируйтесь", 31)
        return false
    }

    fun setId() {
        userId = UsersTable(db).getLastInsertId()
    }

    fun getId(): Int {
        return userId
    }

    fun toVisitor(): Visitor {
        return Visitor(this.name, passwordToVisitor, userId)
    }

    fun isAdmin(): Boolean {
        val checkString = UsersTable(db).select(2, 1, 1, name)
        return checkString[0].split('|')[3].toInt() == 1
    }
}