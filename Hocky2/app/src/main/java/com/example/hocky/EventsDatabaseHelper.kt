package com.example.hocky

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Event(
    val id: Int = 0,
    val name: String,
    val date: String,
    val location: String
)

class EventsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "EventsDatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EVENTS = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DATE = "date"
        const val COLUMN_LOCATION = "location"
    }

    private val CREATE_EVENTS_TABLE = """
        CREATE TABLE $TABLE_EVENTS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_DATE TEXT NOT NULL,
            $COLUMN_LOCATION TEXT NOT NULL
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_EVENTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    fun insertEvent(name: String, date: String, location: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DATE, date)
            put(COLUMN_LOCATION, location)
        }
        return db.insert(TABLE_EVENTS, null, values)
    }

    fun getAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EVENTS ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
                events.add(Event(id, name, date, location))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return events
    }

    fun updateEvent(event: Event, newName: String, newDate: String, newLocation: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", newName)
            put("date", newDate)
            put("location", newLocation)
        }
        return db.update("events", values, "name=? AND date=? AND location=?", arrayOf(event.name, event.date, event.location))
    }

    fun deleteEvent(event: Event): Int {
        val db = writableDatabase
        return db.delete("events", "name=? AND date=? AND location=?", arrayOf(event.name, event.date, event.location))
    }

    fun getEventsCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_EVENTS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }


}
