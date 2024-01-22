package com.mecheka.core

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class UserDemoContentProvider : ContentProvider() {

    // to match the content URI
    // every time user access table under content provider

    private var uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private lateinit var db: SQLiteDatabase

    init {
        // to access whole table
        uriMatcher.addURI(
            PROVIDER_NAME,
            "users",
            uriCode
        )

        // to access a particular row
        // of the table
        uriMatcher.addURI(
            PROVIDER_NAME,
            "users/*",
            uriCode
        )
    }

    override fun onCreate(): Boolean {
        db = DatabaseHelper(context).writableDatabase
        return true
    }

    override fun getType(uri: Uri): String? {
        return checkUriAndDoSomething(uri) { URI_USER }
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?,
    ): Cursor? {
        var sortOrderId = sortOrder
        val qb = SQLiteQueryBuilder()
            .apply {
                tables = TABLE_NAME
                checkUriAndDoSomething(uri) {
                    projectionMap = values
                }
            }

        if (sortOrderId.isNullOrEmpty()) {
            sortOrderId = id
        }
        val user = qb.query(db, projection, selection, selectionArgs, null, null, sortOrderId)
        context?.contentResolver?.let {
            user.setNotificationUri(it, uri)
        }
        return user
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowId = db.insert(TABLE_NAME, "", values)
        if (rowId != 0L) {
            val internalUri = ContentUris.withAppendedId(CONTENT_URI, rowId)
            context?.contentResolver?.notifyChange(internalUri, null)
            return internalUri
        }
        throw SQLiteException("Failed to add a record into $uri")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?,
    ): Int {
        var count = 0
        count = checkUriAndDoSomething(uri) {
            db.update(TABLE_NAME, values, selection, selectionArgs)
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        count = checkUriAndDoSomething(uri) {
            db.delete(TABLE_NAME, selection, selectionArgs)
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    private fun <T> checkUriAndDoSomething(uri: Uri, block: () -> T): T {
        return when (uriMatcher.match(uri)) {
            uriCode -> block()
            else -> error("Unknown URI $uri")
        }
    }

    companion object {
        // defining authority so that other application can access it
        const val PROVIDER_NAME = "com.demo.user.provider"

        // defining content URI
        const val URI = "content://$PROVIDER_NAME/users"

        // parsing the content URI
        val CONTENT_URI: Uri = Uri.parse(URI)
        const val id = "id"
        const val name = "name"
        const val uriCode = 1

        val values: HashMap<String, String>? = null

        const val URI_USER = "vnd.android.cursor.dir/users"

        // declaring name of the database
        const val DATABASE_NAME = "UserDB"

        // declaring table name of the database
        const val TABLE_NAME = "Users"

        // declaring version of the database
        const val DATABASE_VERSION = 1

        // sql query to create the table
        const val CREATE_DB_TABLE =
            (" CREATE TABLE " + TABLE_NAME
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + " name TEXT NOT NULL);")
    }

    private class DatabaseHelper  // defining a constructor
    internal constructor(context: Context?) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
        // creating a table in the database
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(
            db: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int,
        ) {

            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }
}