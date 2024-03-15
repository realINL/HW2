package repository

import model.Entity

interface CRUD {
    fun insert(entity: Entity) {}

    fun update(columnNumber: Int, columnValue: String, value: String) {}

    fun delete(value: String) {}

    fun select(
        type: Int, order: Int, columnNumber: Int = 0,
        columnValue: String = "", specialColumn: String = "_id"
    ): MutableList<String> {
        return mutableListOf()
    }
}