package controller

import repository.Database

val db = Database()
val kitchen: Kitchen = Kitchen()
fun main() {

    kitchen.startCooking()
    Authentication().authentication()

}