package com.example.notesdailyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class EditNoteActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var noteId: String? = null
    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        firestore = FirebaseFirestore.getInstance()

        noteId = intent.getStringExtra("noteId")
        if (noteId != null) {
            isEditing = true
            loadNote()
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnSaveNote).setOnClickListener {
            saveNote()
        }
    }

    private fun loadNote() {
        firestore.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("notes").document(noteId!!).get()
            .addOnSuccessListener { document ->
                val note = document.toObject(Note::class.java)
                if (note != null) {
                    findViewById<EditText>(R.id.etNoteTitle).setText(note.title)
                    findViewById<EditText>(R.id.etNoteContent).setText(note.content)
                }
            }
    }

    private fun saveNote() {
        val title = findViewById<EditText>(R.id.etNoteTitle).text.toString().trim()
        val content = findViewById<EditText>(R.id.etNoteContent).text.toString().trim()

        val note = Note(
            id = noteId ?: UUID.randomUUID().toString(),
            title = title,
            content = content
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .collection("notes").document(note.id).set(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}