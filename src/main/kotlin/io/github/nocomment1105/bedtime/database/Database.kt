/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import io.github.nocomment1105.bedtime.MONGO_URI
import io.github.nocomment1105.bedtime.database.migrations.Migrator
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class Database {
    // Connect to the database using the provided connection URI
    private val settings = MongoClientSettings
        .builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .applyConnectionString(ConnectionString(MONGO_URI))
        .build()

    private val client = KMongo.createClient(settings).coroutine

    /** The main database. */
    val database get() = client.getDatabase("BedTime")

    suspend fun migrate() {
        Migrator.migrate()
    }
}