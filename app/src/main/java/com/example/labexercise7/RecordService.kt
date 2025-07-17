package com.example.labexercise7

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RecordService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val baseUrl = "http://10.0.2.2/IT140P"

    suspend fun getAllRecords(): List<Record> {
        return client.get("$baseUrl/get_all_records.php").body()
    }

    suspend fun addRecord(request: AddRecordRequest): Boolean {
        return try {
            val response = client.post("$baseUrl/add_record.php") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(request)
            }
            response.status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateRecord(record: Record): Boolean {
        return try {
            val response = client.post("$baseUrl/update.php") {
                contentType(ContentType.Application.Json)
                setBody(record)
            }
            response.status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun searchRecords(name: String): List<Record> {
        return try {
            val response = client.post("$baseUrl/search_record.php") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("name" to name))
            }
            response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    suspend fun deleteRecordByName(name: String): Boolean {
        return try {
            val response = client.post("$baseUrl/delete.php") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("name" to name))
            }
            response.status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
