/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime.database.collections

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import io.github.nocomment1105.bedtime.database.Database
import io.github.nocomment1105.bedtime.database.entities.Meta
import org.koin.core.component.inject
import org.litote.kmongo.eq

class MetaCollection : KordExKoinComponent {
    private val db: Database by inject()

    @PublishedApi
    internal val collection = db.bedtimeDatabase.getCollection<Meta>()

    /**
     * Gets the metadata from the database.
     *
     * @return the meta data
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun get(): Meta? = collection.findOne()

    /**
     * Set the metadata when the table is first created.
     *
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun set(meta: Meta) = collection.insertOne(meta)

    /**
     * Update the config metadata in the databse with the new [meta][Meta].
     *
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun update(meta: Meta) = collection.findOneAndReplace(Meta::id eq "meta", meta)
}
