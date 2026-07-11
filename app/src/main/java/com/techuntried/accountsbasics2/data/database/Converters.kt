package com.techuntried.accountsbasics2.data.database

import androidx.room.TypeConverter
import com.techuntried.accountsbasics2.domain.model.content.Option
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromOptionsList(options: List<Option>): String {
        return json.encodeToString(options)
    }

    @TypeConverter
    fun toOptionsList(data: String): List<Option> {
        return json.decodeFromString(data)
    }
}
