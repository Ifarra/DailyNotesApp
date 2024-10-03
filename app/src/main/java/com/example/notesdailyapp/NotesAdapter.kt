package com.example.notesdailyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private val notes: List<Note>,
    private val clickListener: (Note) -> Unit,
    private val longClickListener: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position], clickListener, longClickListener)
    }

    override fun getItemCount(): Int = notes.size

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note, clickListener: (Note) -> Unit, longClickListener: (Note) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvNoteTitle).text = note.title
            itemView.findViewById<TextView>(R.id.tvNoteContent).text = note.content // Bind content
            itemView.setOnClickListener { clickListener(note) }
            itemView.setOnLongClickListener {
                longClickListener(note)
                true
            }
        }
    }
}