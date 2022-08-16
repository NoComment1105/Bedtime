/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime

import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull

val TOKEN = env("TOKEN")

val MONGO_URI = envOrNull("MONGO_URI") ?: "mongodb://localhost:27017"
