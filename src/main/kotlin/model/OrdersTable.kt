package model

import repository.Database

class OrdersTable(db: Database) : EntityTable(db) {
    init {
        synchronized(db) {
            table = "orders"
            columns = db.getColumns(table)
            this.db = db
        }
    }
}