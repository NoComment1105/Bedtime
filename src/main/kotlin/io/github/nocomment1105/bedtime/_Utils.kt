/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import com.kotlindiscord.kord.extensions.utils.loadModule
import io.github.nocomment1105.bedtime.database.Database
import io.github.nocomment1105.bedtime.database.collections.BedTimeCollection
import io.github.nocomment1105.bedtime.database.collections.MetaCollection
import kotlinx.coroutines.runBlocking
import org.koin.dsl.bind

suspend inline fun ExtensibleBotBuilder.database(migrate: Boolean = false) {
    val db = Database()

    hooks {
        afterKoinSetup {
            loadModule {
                single { db } bind Database::class
            }

            loadModule {
                single { MetaCollection() } bind MetaCollection::class
                single { BedTimeCollection() } bind BedTimeCollection::class
            }
        }
    }

    if (migrate) {
        runBlocking {
            db.migrate()
        }
    }
}
