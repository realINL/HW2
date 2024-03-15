package model

import repository.Database

class UsersTable(db: Database) : EntityTable(db) {
    init {

        table = "Users"
        columns = db.getColumns(table)
        this.db = db
    }
}