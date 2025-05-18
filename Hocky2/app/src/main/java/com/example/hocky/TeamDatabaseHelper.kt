import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.example.hocky.Team

class TeamDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "teams.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TEAMS = "teams"
        const val COLUMN_ID = "id"
        const val COLUMN_TEAM_NAME = "team_name"
        const val COLUMN_COACH_NAME = "coach_name"
        const val COLUMN_PLAYERS = "players"  // Can be null
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TEAMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEAM_NAME TEXT NOT NULL,
                $COLUMN_COACH_NAME TEXT NOT NULL,
                $COLUMN_PLAYERS TEXT
            );
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEAMS")
        onCreate(db)
    }

    fun insertTeam(teamName: String, coachName: String, players: String? = null): Boolean {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TEAM_NAME, teamName)
                put(COLUMN_COACH_NAME, coachName)
                put(COLUMN_PLAYERS, players)
            }
            val result = db.insert(TABLE_TEAMS, null, values)
            db.close()
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllTeams(): List<Team> {
        val teamList = mutableListOf<Team>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TEAMS,
            null, null, null, null, null, "$COLUMN_ID DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val teamName = getString(getColumnIndexOrThrow(COLUMN_TEAM_NAME))
                val coachName = getString(getColumnIndexOrThrow(COLUMN_COACH_NAME))
                val players = getString(getColumnIndexOrThrow(COLUMN_PLAYERS))
                teamList.add(Team(id, teamName, coachName, players))
            }
            close()
        }
        db.close()
        return teamList
    }

}
