package model

import repository.CRUD
import repository.Database
import java.util.concurrent.locks.ReentrantLock

abstract class EntityTable(db: Database) : CRUD {
    var table: String = ""
    internal var columns: MutableList<String> = mutableListOf()
    internal var db: Database
    private val resultList: MutableList<String> = mutableListOf()
    private val lock = ReentrantLock()

    init {
        synchronized(lock) {
            this.db = db
        }
    }

    override fun insert(entity: Entity) {
        lock.lock()
        try {
            val values = entity.values
            var request = "INSERT INTO $table VALUES (NULL"
            var i = 1
            for (elem in values) {
                val column = columns[i].split('_')
                request += if (column.count() == 2 && column[1] == "id") {
                    ", (SELECT _id FROM ${column[0]} WHERE ${column[0]}_name = '$elem')"
                } else {
                    ", '$elem'"
                }
                ++i
            }
            request += ");"
            make(request, false)
        } catch (e: Exception) {

        }
        lock.unlock()
    }


    fun getLastInsertId(): Int {

        lock.lock()
        try {
            val request = "SELECT last_insert_rowid() AS id"
            val result = db.raw(request, true)
            var lastInsertId = -1
            if (result?.next() == true) {
                lastInsertId = result.getInt("id")
            }
            lock.unlock()
            return lastInsertId
        } catch (e: Exception) {
            lock.unlock()
            throw e
        }


    }

    fun selectAll(): MutableList<String> {
        lock.lock()
        try {
            resultList.clear()
            val request = "SELECT * FROM $table"
            make(request, true)
            lock.unlock()
            return resultList
        } catch (e: Exception) {
            lock.unlock()
            throw e
        }


    }


    override fun update(columnNumber: Int, columnValue: String, id: String) {
        lock.lock()

        try {
            val request = "UPDATE $table SET ${columns[columnNumber]} = '$columnValue' WHERE _id = $id"
            make(request, false)
        } catch (e: Exception) {
        }
        lock.unlock()

    }

    override fun select(
        type: Int, order: Int, columnNumber: Int,
        columnValue: String, specialColumn: String
    ): MutableList<String> {
        lock.lock()
        try {
            resultList.clear()
            var requestLeft = "SELECT $table.$specialColumn"
            var requestRight = " FROM $table"
            var columnName = ""
            for (elem in columns) {
                if (elem == specialColumn) {
                    continue
                }
                val column = elem.split('_')
                if (column.count() == 2 && column[1] == specialColumn) {
                    requestLeft += ", ${column[0]}.${column[0]}_name"
                    requestRight += " JOIN ${column[0]} ON ${column[0]}.$specialColumn = $table.$elem"
                    if (elem == columns[columnNumber]) {
                        columnName = "${column[0]}_name"
                    }
                } else {
                    requestLeft += ", $table.$elem"
                }
            }
            if (type == 2) {
                requestRight += if (columnName != "") {
                    " WHERE $columnName = '$columnValue'"
                } else {
                    " WHERE ${columns[columnNumber]} = '$columnValue'"
                }
            }
            val orderStr = if (order == 1) {
                "ASC"
            } else {
                "DESC"
            }
            requestLeft += if (table != "session") {
                "$requestRight ORDER BY $table.$specialColumn $orderStr"
            } else {
                "$requestRight ORDER BY $table.end $orderStr"
            }
            make(requestLeft, true)
            lock.unlock()
            return resultList
        } catch (e: Exception) {
            lock.unlock()
            throw e
        }


    }


    override fun delete(id: String) {
        lock.lock()
        try {
            val request = if (!columns.contains("${table}_name")) {
                "DELETE FROM $table WHERE _id = '$id'"
            } else {
                "DELETE FROM $table WHERE ${table}_name = '$id'"
            }
            make(request, false)
        } catch (e: Exception) {
        }
        lock.unlock()
    }


    private fun make(request: String, bool: Boolean) {
        lock.lock()
        try {
            val result = db.raw(request, bool)
            if (bool) {
                while (result?.next() == true) {
                    var row = ""
                    for (elem in columns) {
                        val column = elem.split('_')
                        row += if (column.count() == 2 && column[1] == "id" && elem != "_id") {
                            result.getString("${column[0]}_name") + "|"
                        } else {
                            result.getString(elem) + "|"
                        }
                    }
                    resultList.add(row)
                }
            }
        } catch (e: Exception) {
            make(request, bool)
        }
        lock.unlock()
    }
}