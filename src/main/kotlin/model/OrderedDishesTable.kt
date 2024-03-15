package model

import repository.Database

class OrderedDishesTable(db: Database) : EntityTable(db) {
    init {
        synchronized(db) {
            table = "orderedDishes"
            columns = db.getColumns(table)
            this.db = db
        }
    }
}