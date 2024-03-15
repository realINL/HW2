package model

import repository.Database

class Sqlite_sequenceTable(db: Database) : EntityTable(db) {
    init {
        synchronized(db) {
            table = "sqlite_sequence"
            columns = db.getColumns(table)
            this.db = db
        }
    }
}