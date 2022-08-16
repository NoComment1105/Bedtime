/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * The data for bedtime notifications.
 *
 * @property dm Whether to DM the notification or not
 * @property bedtime The time to do the reminding
 * @property bedtimeMessage The message to send with the bedtime notification
 * @property guildId The guild the notification should be sent in, if any
 * @property channel The channel the notification should be sent in, if any
 */
@Serializable
data class BedTime(
    val userId: Snowflake,
    val bedtime: Instant,
    val bedtimeMessage: String?,
    val dm: Boolean,
    val guildId: Snowflake?,
    val channel: Snowflake?
)
