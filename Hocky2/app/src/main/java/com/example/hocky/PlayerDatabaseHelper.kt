package com.example.hocky

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// PlayerDatabaseHelper.kt
class PlayerDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "hockey.db"
        const val DATABASE_VERSION = 1

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
        val createPlayerTable = """
            CREATE TABLE $TABLE_PLAYERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DOB TEXT NOT NULL,
                $COLUMN_JERSEY TEXT NOT NULL,
                $COLUMN_CONTACT TEXT NOT NULL,
                $COLUMN_EMAIL TEXT,
                $COLUMN_TEAM TEXT NOT NULL,
                $COLUMN_GENDER TEXT NOT NULL,
                $COLUMN_POSITION TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createPlayerTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
        onCreate(db)
    }

    // Insert a new player
    fun insertPlayer(player: Player): Boolean {
        val db = writableDatabase
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
        val result = db.insert(TABLE_PLAYERS, null, values)
        db.close()
        return result != -1L
    }

    // Get all players
    fun getAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PLAYERS, null, null, null, null, null, "$COLUMN_ID DESC"
        )
        with(cursor) {
            while (moveToNext()) {
                players.add(
                    Player(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                        dob = getString(getColumnIndexOrThrow(COLUMN_DOB)),
                        jerseyNumber = getString(getColumnIndexOrThrow(COLUMN_JERSEY)),
                        contact = getString(getColumnIndexOrThrow(COLUMN_CONTACT)),
                        email = if (isNull(getColumnIndexOrThrow(COLUMN_EMAIL))) null
                        else getString(getColumnIndexOrThrow(COLUMN_EMAIL)),
                        team = getString(getColumnIndexOrThrow(COLUMN_TEAM)),
                        gender = getString(getColumnIndexOrThrow(COLUMN_GENDER)),
                        position = getString(getColumnIndexOrThrow(COLUMN_POSITION))
                    )
                )
            }
            close()
        }
        db.close()
        return players
    }
}