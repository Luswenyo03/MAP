package com.example.hocky

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class PlayerDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "PlayerDatabaseHelper"
        private const val DATABASE_NAME = "hockey.db"
        private const val DATABASE_VERSION = 2  // Incremented version for schema changes

        // Player Table
        const val TABLE_PLAYERS = "players"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DOB = "dob"
        const val COLUMN_JERSEY = "jersey_number"
        const val COLUMN_CONTACT = "contact"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_TEAM = "team"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_POSITION = "position"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val createPlayerTable = """
                CREATE TABLE $TABLE_PLAYERS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_NAME TEXT NOT NULL,
                    $COLUMN_DOB TEXT,
                    $COLUMN_JERSEY INTEGER NOT NULL,
                    $COLUMN_CONTACT TEXT NOT NULL,
                    $COLUMN_EMAIL TEXT,
                    $COLUMN_TEAM TEXT NOT NULL,
                    $COLUMN_GENDER TEXT NOT NULL,
                    $COLUMN_POSITION TEXT NOT NULL
                );
            """.trimIndent()
            db.execSQL(createPlayerTable)
            Log.d(TAG, "Database created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating database", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
            onCreate(db)
            Log.d(TAG, "Database upgraded from $oldVersion to $newVersion")
        } catch (e: Exception) {
            Log.e(TAG, "Error upgrading database", e)
        }
    }

    // Insert a new player with transaction and error handling
    fun addPlayer(player: Player): Long {
        val db = writableDatabase
        return try {
            db.beginTransaction()
            val values = ContentValues().apply {
                put(COLUMN_NAME, player.name)
                put(COLUMN_DOB, player.dob)
                put(COLUMN_JERSEY, player.jerseyNumber)
                put(COLUMN_CONTACT, player.contact)
                put(COLUMN_EMAIL, player.email)
                put(COLUMN_TEAM, player.team)
                put(COLUMN_GENDER, player.gender)
                put(COLUMN_POSITION, player.position)
            }
            val id = db.insert(TABLE_PLAYERS, null, values)
            db.setTransactionSuccessful()
            id
        } catch (e: Exception) {
            Log.e(TAG, "Error adding player", e)
            -1L
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Get all players with improved cursor handling
    fun getAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        val db = readableDatabase
        var cursor = db.query(
            TABLE_PLAYERS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_NAME ASC"  // Sort by name by default
        )

        cursor.use {
            while (it.moveToNext()) {
                try {
                    players.add(
                        Player(
                            id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                            dob = it.getString(it.getColumnIndexOrThrow(COLUMN_DOB)),
                            jerseyNumber = it.getInt(it.getColumnIndexOrThrow(COLUMN_JERSEY)),
                            contact = it.getString(it.getColumnIndexOrThrow(COLUMN_CONTACT)),
                            email = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_EMAIL)),
                            team = it.getString(it.getColumnIndexOrThrow(COLUMN_TEAM)),
                            gender = it.getString(it.getColumnIndexOrThrow(COLUMN_GENDER)),
                            position = it.getString(it.getColumnIndexOrThrow(COLUMN_POSITION))
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing player data", e)
                }
            }
        }
        db.close()
        return players
    }

    // Extension function for safer null handling
    private fun android.database.Cursor.getStringOrNull(columnIndex: Int): String? {
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}