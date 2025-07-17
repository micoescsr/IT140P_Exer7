package com.example.labexercise7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val recordService = RecordService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RecordApp(recordService)
                }
            }
        }
    }
}

@Composable
fun RecordApp(recordService: RecordService) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var records by remember { mutableStateOf(listOf<Record>()) }
    var selectedRecord by remember { mutableStateOf<Record?>(null) }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch {
                    val success = recordService.addRecord(AddRecordRequest(name, email))
                    if (success) {
                        name = ""
                        email = ""
                        selectedRecord = null
                        records = recordService.getAllRecords()
                    }
                }
            }, enabled = selectedRecord == null) {
                Text("Add")
            }

            Button(onClick = {
                scope.launch {
                    records = recordService.getAllRecords()
                }
            }) {
                Text("Fetch All")
            }

            Button(onClick = {
                scope.launch {
                    selectedRecord?.let {
                        recordService.updateRecord(Record(id = it.id, name = name, email = email))
                        name = ""
                        email = ""
                        selectedRecord = null
                        records = recordService.getAllRecords()
                    }
                }
            }, enabled = selectedRecord != null) {
                Text("Update")
            }

            Button(onClick = {
                scope.launch {
                    selectedRecord?.let {
                        recordService.deleteRecordByName(it.name)
                        name = ""
                        email = ""
                        selectedRecord = null
                        records = recordService.getAllRecords()
                    }
                }
            }, enabled = selectedRecord != null) {
                Text("Delete")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(records) { record ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("• ${record.name} | ${record.email}")
                    Button(onClick = {
                        name = record.name
                        email = record.email
                        selectedRecord = record
                    }) {
                        Text("Select")
                    }
                }
            }
        }
    }
}
