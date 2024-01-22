package com.mecheka.core

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserDemoState(private val context: Context) {
    val userList = mutableStateListOf<String>()

    suspend fun addUser(name: String) {
        val values = ContentValues().apply {
            put(UserDemoContentProvider.name, name)
        }
        context.contentResolver.insert(UserDemoContentProvider.CONTENT_URI, values)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "New record inserted", Toast.LENGTH_LONG).show()
        }
    }

    fun loadUser() {
        userList.clear()
        val cursor = context.contentResolver.query(
            Uri.parse("content://com.demo.user.provider/users"),
            null,
            null,
            null,
            null
        )

        if (cursor?.moveToFirst() == true) {
            val count = cursor.count
            var index = 0
            Log.d("UserDemoState", cursor.count.toString())
            Log.d("UserDemoState", cursor.getColumnIndex("id").toString())
            while (index != count) {
                index++
                userList.add(cursor.getUser())
                cursor.moveToNext()
            }
        } else {
            userList.add("No record found")
        }
    }

    private fun Cursor.getUser(): String {
        val id = this.getColumnIndex("id")
        val name = this.getColumnIndex("name")
        return "${this.getString(id)} - ${this.getString(name)}"
    }
}