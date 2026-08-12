package com.andrija.martinabudilica.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class NotesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("martina_notes", Context.MODE_PRIVATE)
    private val KEY_NOTES = "notes_list"

    fun getNotes(): List<Note> {
        val jsonString = prefs.getString(KEY_NOTES, "[]") ?: "[]"
        val notes = mutableListOf<Note>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                notes.add(
                    Note(
                        id = obj.getLong("id"),
                        text = obj.getString("text"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return notes.sortedByDescending { it.timestamp }
    }

    fun saveNote(text: String) {
        val notes = getNotes().toMutableList()
        val newNote = Note(
            id = System.currentTimeMillis(),
            text = text,
            timestamp = System.currentTimeMillis()
        )
        notes.add(newNote)
        saveNotesToPrefs(notes)
    }

    fun deleteNote(id: Long) {
        val notes = getNotes().filter { it.id != id }
        saveNotesToPrefs(notes)
    }

    private fun saveNotesToPrefs(notes: List<Note>) {
        val jsonArray = JSONArray()
        for (note in notes) {
            val obj = JSONObject().apply {
                put("id", note.id)
                put("text", note.text)
                put("timestamp", note.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_NOTES, jsonArray.toString()).apply()
    }
}
