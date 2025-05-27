package com.example.hocky

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Database version and name constants
    companion object {
        const val DATABASE_NAME = "UserDatabase"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ID_NUMBER = "id_number"
        const val COLUMN_PASSWORD = "password"
    }

    // SQL to create the users table
    private val CREATE_TABLE_SQL = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_FULL_NAME TEXT,
            $COLUMN_EMAIL TEXT,
            $COLUMN_ID_NUMBER TEXT,
            $COLUMN_PASSWORD TEXT
        )
    """

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to insert a new user into the database
    fun insertUser(fullName: String, email: String, idNumber: String, password: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_FULL_NAME, fullName)
        contentValues.put(COLUMN_EMAIL, email)
        contentValues.put(COLUMN_ID_NUMBER, idNumber)
        contentValues.put(COLUMN_PASSWORD, password)

        return db.insert(TABLE_NAME, null, contentValues)
    }

    // Function to check if user credentials are valid
    fun checkUserCredentials(email: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }

}
