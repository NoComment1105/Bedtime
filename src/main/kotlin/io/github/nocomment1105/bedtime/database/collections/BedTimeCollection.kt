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
import dev.kord.common.entity.Snowflake
import io.github.nocomment1105.bedtime.database.Database
import io.github.nocomment1105.bedtime.database.entities.BedTime
import org.koin.core.component.inject
import org.litote.kmongo.eq

/**
 * This class contains the functions for interacting with the [BedTime] database table.
 * It contains functions for getting, setting and deleting [BedTime] data
 *
 * @since 0.1.0
 * @see getBedtime(userId: Snowflake)
 * @see getAllBedtimes
 * @see setBedtime(bedtime: BedTime)
 * @see clearBedtime(userId: Snowflake)
 */
class BedTimeCollection : KordExKoinComponent {
    private val db: Database by inject()

    @PublishedApi
    internal val collection = db.database.getCollection<BedTime>()

    /**
     * Gets the bedtime data for the given user
     *
     * @param userId The user to get the bedtime for
     * @return the bedtime data for the user
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun getBedtime(userId: Snowflake) =
        collection.findOne(BedTime::userId eq userId)

    /**
     * Gets all the bedtime data in the datbase
     *
     * @return the list of bedtime data in the database
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun getAllBedtimes() =
        collection.find().toList()

    /**
     * Adds the given [bedtime] to the database
     *
     * @param bedtime The bedtime data
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun setBedtime(bedtime: BedTime) {
        collection.deleteOne(BedTime::userId eq bedtime.userId)
        collection.insertOne(bedtime)
    }

    /**
     * Clears the bedtime data for the given user
     *
     * @param userId The user to clear the bedtime for
     * @author NoComment1105
     * @since 0.1.0
     */
    suspend inline fun clearBedtime(userId: Snowflake) =
        collection.deleteOne(BedTime::userId eq userId)

    /**
     * Updates the bedtime in the database.
     *
     * @param userId The user to update the bedtime for
     * @param newBedtime The new bedtime data to add
     */
    suspend inline fun updateBedtime(userId: Snowflake, newBedtime: BedTime) {
        collection.updateOne(BedTime::userId eq userId, newBedtime)
    }

}