package com.example.notesdailyapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomePageActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var notesAdapter: NotesAdapter
    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Setup RecyclerView and other components...

        notesAdapter = NotesAdapter(notesList, { note -> editNote(note) }, { note -> showDeleteConfirmationDialog(note) })


        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            logout()
        }

        firestore = FirebaseFirestore.getInstance()


        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@HomePageActivity)
            adapter = notesAdapter
        }

        findViewById<Button>(R.id.btnAddNote).setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }

        loadNotes()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showDeleteConfirmationDialog(note: Note) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ -> deleteNote(note) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNote(note: Note) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .collection("notes").document(note.id).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                    loadNotes() // Refresh the list after deletion
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private var notesListener: ListenerRegistration? = null

    private fun loadNotes() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            notesListener = firestore.collection("users").document(userId)
                .collection("notes")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    notesList.clear()
                    if (snapshots != null) {
                        for (document in snapshots.documents) {
                            val note = document.toObject(Note::class.java)
                            notesList.add(note!!)
                        }
                    }
                    notesAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun editNote(note: Note) {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        notesListener?.remove()
    }
}