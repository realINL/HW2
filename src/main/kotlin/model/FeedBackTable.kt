package model

import repository.Database

class FeedBackTable(db: Database) : EntityTable(db) {
    init {

        table = "FeedBack"
        columns = db.getColumns(table)
        this.db = db
    }
}