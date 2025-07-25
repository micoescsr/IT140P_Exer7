// Mico Efraim D. Escosura
// IT140P - CIS341

package com.example.labexercise7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch

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

    fun isDigitsOnly(input: String): Boolean = input.all { it.isDigit() }

    fun areInputsValid(): Boolean {
        return name.isNotBlank() && birthday.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.information),
                contentDescription = "Information Icon",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
            )

            // Add, Update, Delete Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            Toast.makeText(context, "Phone must be digits only.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            val success = recordService.addRecord(AddRecordRequest(name, birthday, email, phone))
                            if (success) {
                                Toast.makeText(context, "Record added successfully", Toast.LENGTH_SHORT).show()
                                name = ""; birthday = ""; email = ""; phone = ""
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
                            Toast.makeText(context, "Phone must be digits only.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            selectedRecord?.let { record ->
                                val updated = record.copy(name = name, birthday = birthday, email = email, phone = phone)
                                if (recordService.updateRecord(updated)) {
                                    Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                                    selectedRecord = null
                                    name = ""; birthday = ""; email = ""; phone = ""
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
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                selectedRecord = null
                                name = ""; birthday = ""; email = ""; phone = ""
                            }
                        }
                    },
                    enabled = selectedRecord != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }

            // Search and Clear Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            if (name.isNotBlank()) {
                                val results = recordService.searchRecordByName(name)
                                selectedRecord = results.firstOrNull()
                                selectedRecord?.let {
                                    name = it.name; birthday = it.birthday; email = it.email; phone = it.phone
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
                        name = ""; birthday = ""; email = ""; phone = ""
                        selectedRecord = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }

            // Search Result
            selectedRecord?.let {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Column {
                    Text("Search Result", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Name: ${it.name}")
                    Text("Birthday: ${it.birthday}")
                    Text("Email: ${it.email}")
                    Text("Phone: ${it.phone}")
                }
            }
        }
    }
}

