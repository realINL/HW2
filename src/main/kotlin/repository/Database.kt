package repository

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

//class Database {
//    private val jdbcUrl = "jdbc:sqlite:BD.db"
//    private var db: Statement? = null
//
//    init {
//
//        synchronized(this) {
//            try {
//                val connection = DriverManager.getConnection(jdbcUrl)
//                db = connection?.createStatement()
//            } catch (e: SQLException) {
//                println("Error connecting to SQLite database")
//                e.printStackTrace()
//            }
//        }
//    }
//
//    @Synchronized fun raw(request: String, select: Boolean): ResultSet? {
//        synchronized(this) {
//            var result: ResultSet? = null
//            try {
//                if (select) {
//                    result = db?.executeQuery(request)
//                } else {
//                    db?.executeUpdate(request)
//                }
//            } catch (e: SQLException) {
//                println("Error")
//                println(e)
//            }
//            return result
//        }
//    }
//
//    @Synchronized  fun getColumns(table: String): MutableList<String> {
//        synchronized(this) {
//            val columns = mutableListOf<String>()
//            val request = "SELECT name FROM pragma_table_info('$table');"
//            val result = raw(request, true) ?: return columns
//            while (result.next()) {
//                val name = result.getString("name")
//                columns.add(name)
//            }
//            return columns
//        }
//    }
//}

import java.util.concurrent.locks.ReentrantLock

class Database {
    private val jdbcUrl = "jdbc:sqlite:BD.db"
    private var db: Statement? = null
    private val lock = ReentrantLock()

    init {
        synchronized(lock) {
            try {
                val connection = DriverManager.getConnection(jdbcUrl)
                connection.createStatement().execute("PRAGMA foreign_keys = ON")
                db = connection?.createStatement()
            } catch (e: SQLException) {
                println("Error connecting to SQLite database")
                e.printStackTrace()
            }
        }

    }

    fun raw(request: String, select: Boolean): ResultSet? {
        lock.lock()
        try {
            var result: ResultSet? = null
            if (select) {
                result = db?.executeQuery(request)
            } else {
                db?.executeUpdate(request)
            }
            return result
        } catch (e: SQLException) {
            println("Error")
            println(e)
            return null
        } finally {
            lock.unlock()
        }
    }

    fun getColumns(table: String): MutableList<String> {
        lock.lock()
        try {
            val columns = mutableListOf<String>()
            val request = "SELECT name FROM pragma_table_info('$table');"
            val result = raw(request, true) ?: return columns
            while (result.next()) {
                val name = result.getString("name")
                columns.add(name)
            }
            return columns
        } catch (e: SQLException) {
            println("Error")
            println(e)
            return mutableListOf()
        } finally {
            lock.unlock()
        }
    }
}
