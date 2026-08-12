package com.andrija.martinabudilica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrija.martinabudilica.data.Note
import com.andrija.martinabudilica.data.NotesRepository
import com.andrija.martinabudilica.ui.theme.MartinaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotebookActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shouldOpenNewNote = intent.getBooleanExtra("NEW_NOTE", false)
        
        setContent {
            MartinaTheme {
                NotebookScreen(
                    repository = NotesRepository(this),
                    startWithNewNote = shouldOpenNewNote,
                    onClose = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(repository: NotesRepository, startWithNewNote: Boolean, onClose: () -> Unit) {
    var notes by remember { mutableStateOf(repository.getNotes()) }
    var showNewNoteDialog by remember { mutableStateOf(startWithNewNote) }
    var newNoteText by remember { mutableStateOf("") }
    
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault()) }

    fun refreshNotes() {
        notes = repository.getNotes()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewNoteDialog = true }) {
                Icon(Icons.Rounded.Add, contentDescription = "Dodaj novu zabilješku")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Martinin Dnevnik", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Zatvori")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF1EA), Color(0xFFF4EEFF))))
                .padding(padding)
        ) {
            if (notes.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tvoj dnevnik je prazan. 📖", fontSize = 20.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Zapiši svoje prve misli ili snove!", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateFormatter.format(Date(note.timestamp)),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    IconButton(
                                        onClick = { 
                                            repository.deleteNote(note.id)
                                            refreshNotes()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Rounded.Delete, contentDescription = "Obriši", tint = Color.LightGray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(note.text, fontSize = 16.sp, lineHeight = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewNoteDialog) {
        AlertDialog(
            onDismissRequest = { 
                showNewNoteDialog = false 
                newNoteText = ""
            },
            title = { Text("Nova zabilješka") },
            text = {
                OutlinedTextField(
                    value = newNoteText,
                    onValueChange = { newNoteText = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Što ti je na umu?") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newNoteText.isNotBlank()) {
                        repository.saveNote(newNoteText.trim())
                        refreshNotes()
                    }
                    showNewNoteDialog = false
                    newNoteText = ""
                }) {
                    Text("Spremi")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showNewNoteDialog = false
                    newNoteText = ""
                }) {
                    Text("Odustani")
                }
            }
        )
    }
}
