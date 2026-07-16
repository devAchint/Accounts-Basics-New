package com.techuntried.accountsbasics2.data.database

import androidx.room.TypeConverter
import com.techuntried.accountsbasics2.domain.model.content.ContentItem
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

    @TypeConverter
    fun fromContent(items: List<ContentItem>): String =
        json.encodeToString(items)

    @TypeConverter
    fun toContent(data: String): List<ContentItem> =
        json.decodeFromString(data)
}
