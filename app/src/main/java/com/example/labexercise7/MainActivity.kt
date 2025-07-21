// Mico Efraim D. Escosura
// IT140P - CIS341

package com.example.labexercise7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity() {
    private val recordService = RecordService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF18415C),
                    onPrimary = Color.White,
                    background = Color.White,
                    onBackground = Color.Black,
                    surface = Color.White,
                    onSurface = Color.Black
                )
            ) {
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
    var birthday by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRecord by remember { mutableStateOf<Record?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun isValidBirthday(input: String): Boolean {
        val regex = Regex("""\d{2}-\d{2}-\d{4}""")
        return input.matches(regex)
    }

    fun isDigitsOnly(input: String): Boolean {
        return input.all { it.isDigit() }
    }

    fun areInputsValid(): Boolean {
        return name.isNotBlank() &&
                birthday.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Input Fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = birthday,
            onValueChange = { birthday = it },
            label = { Text("Birthday (MM-DD-YYYY)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Button Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (!areInputsValid()) {
                        Toast.makeText(context, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isValidBirthday(birthday)) {
                        Toast.makeText(context, "Invalid birthday format. Use MM-DD-YYYY.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isDigitsOnly(phone)) {
                        Toast.makeText(context, "Phone number must contain digits only.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        val success = recordService.addRecord(
                            AddRecordRequest(name, birthday, email, phone)
                        )
                        if (success) {
                            Toast.makeText(context, "Record added successfully", Toast.LENGTH_SHORT).show()
                            name = ""
                            birthday = ""
                            email = ""
                            phone = ""
                            selectedRecord = null
                        }
                    }
                },
                enabled = selectedRecord == null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Add")
            }

            Button(
                onClick = {
                    if (!areInputsValid()) {
                        Toast.makeText(context, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isValidBirthday(birthday)) {
                        Toast.makeText(context, "Invalid birthday format. Use MM-DD-YYYY.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isDigitsOnly(phone)) {
                        Toast.makeText(context, "Phone number must contain digits only.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        selectedRecord?.let { record ->
                            val updatedRecord = Record(
                                id = record.id,
                                name = name,
                                birthday = birthday,
                                email = email,
                                phone = phone
                            )
                            val success = recordService.updateRecord(updatedRecord)
                            if (success) {
                                Toast.makeText(context, "Record updated successfully", Toast.LENGTH_SHORT).show()
                                selectedRecord = updatedRecord
                                name = ""
                                birthday = ""
                                email = ""
                                phone = ""
                            }
                        }
                    }
                },
                enabled = selectedRecord != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Update")
            }

            Button(
                onClick = {
                    scope.launch {
                        selectedRecord?.let {
                            recordService.deleteRecordById(it.id)
                            Toast.makeText(context, "Record deleted successfully", Toast.LENGTH_SHORT).show()
                            name = ""
                            birthday = ""
                            email = ""
                            phone = ""
                            selectedRecord = null
                        }
                    }
                },
                enabled = selectedRecord != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete")
            }
        }

        // Button Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        if (name.isNotBlank()) {
                            val results = recordService.searchRecordByName(name)
                            selectedRecord = results.firstOrNull()
                            selectedRecord?.let {
                                name = it.name
                                birthday = it.birthday
                                email = it.email
                                phone = it.phone
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Search")
            }

            Button(
                onClick = {
                    name = ""
                    birthday = ""
                    email = ""
                    phone = ""
                    selectedRecord = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        // Display search result
        selectedRecord?.let {
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Search Result", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Name: ${it.name}")
                Text("Birthday: ${it.birthday}")
                Text("Email: ${it.email}")
                Text("Phone Number: ${it.phone}")
            }
        }
    }
}