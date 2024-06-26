package com.example.happystudent.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object FilterPreferencesSerializer: Serializer<FilterPreferences> {
    override val defaultValue: FilterPreferences =  FilterPreferences
        .getDefaultInstance()
        .toBuilder()
        .setFilterType(FilterPreferences.FilterType.NO_FILTER)
        .build()


    override suspend fun readFrom(input: InputStream): FilterPreferences {
        try {
            return FilterPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: FilterPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}