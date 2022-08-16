/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime

import com.kotlindiscord.kord.extensions.ExtensibleBot
import io.github.nocomment1105.bedtime.extensions.BedTimeExtension
import io.github.nocomment1105.bedtime.extensions.HelpExtension

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        database()

        extensions {
            add(::BedTimeExtension)
            add(::HelpExtension)
        }
    }

    bot.start()
}
