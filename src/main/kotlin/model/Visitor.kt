package model

import repository.Database

class Visitor(name: String, password: String, id: Int) : User(name, password) {

    init {
        this.userId = id
        values.add("0")
    }
}
