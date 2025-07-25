package com.example.labexercise7

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RecordService {

    private val baseUrl = "http://10.0.2.2/IT140P" //10.0.2.2
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun addRecord(request: AddRecordRequest): Boolean {
        return try {
            val response: HttpResponse = client.post("$baseUrl/add_record.php") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllRecords(): List<Record> {
        return try {
            client.get("$baseUrl/get_all_records.php").body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
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

    suspend fun deleteRecordById(id: Int): Boolean {
        return try {
            val response = client.post("$baseUrl/delete.php") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("id" to id))
            }
            response.status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun searchRecordByName(name: String): List<Record> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/search_record.php") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("name" to name))
            }
            response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
