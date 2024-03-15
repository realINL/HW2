package model

import repository.Database

class MenuTable(db: Database) : EntityTable(db) {
    init {
        synchronized(db) {
            table = "Menu"
            columns = db.getColumns(table)
            this.db = db
        }
    }
}



