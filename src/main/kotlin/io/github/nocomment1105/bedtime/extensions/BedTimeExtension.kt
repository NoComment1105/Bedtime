/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime.extensions

import com.kotlindiscord.kord.extensions.checks.guildFor
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.FormattedTimestamp
import com.kotlindiscord.kord.extensions.commands.converters.impl.boolean
import com.kotlindiscord.kord.extensions.commands.converters.impl.timestamp
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.modules.unsafe.annotations.UnsafeAPI
import com.kotlindiscord.kord.extensions.modules.unsafe.extensions.unsafeSlashCommand
import com.kotlindiscord.kord.extensions.modules.unsafe.extensions.unsafeSubCommand
import com.kotlindiscord.kord.extensions.modules.unsafe.types.InitialSlashCommandResponse
import com.kotlindiscord.kord.extensions.modules.unsafe.types.respondEphemeral
import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.dm
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import com.kotlindiscord.kord.extensions.utils.scheduling.Task
import com.kotlindiscord.kord.extensions.utils.waitFor
import dev.kord.common.entity.DiscordActivityTimestamps
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.interaction.GlobalApplicationCommandInteraction
import dev.kord.core.event.interaction.GlobalApplicationCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import io.github.nocomment1105.bedtime.database.collections.BedTimeCollection
import io.github.nocomment1105.bedtime.database.entities.BedTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.sql.Timestamp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@OptIn(UnsafeAPI::class)
class BedTimeExtension : Extension() {
    override val name = "bed-time"

    private val scheduler = Scheduler()

    private lateinit var task: Task

    override suspend fun setup() {
        task =
            scheduler.schedule(seconds = 30, pollingSeconds = 30, repeat = true, callback = ::postBedTimeNotification)

        ephemeralSlashCommand {
            name = "bed-time"
            description = "The parent command for bed-time commands"

            unsafeSubCommand(::BedTimeSetArgs) {
                name = "set"
                description = "Set the time you'd like to be reminded for bedtime"

                initialResponse = InitialSlashCommandResponse.None

                action {
                    if (BedTimeCollection().getBedtime(user.id) != null) {
                        event.interaction.respondEphemeral {
                            content =
                                "You already have a bedtime setup! Please clear your bedtime before settings a new one"
                        }
                        return@action
                    }

                    if (guildFor(event) == null && !arguments.dm) {
                        event.interaction.respondEphemeral {
                            content = "**Error**: It appears you are in our DMs and DM is set to false.\nThis means I" +
                                    " cannot remind you anywhere! Please set DM to true or run the command in a server!"
                        }
                        return@action
                    }

                    if (arguments.bedTimeMessage) {
                        val response = event.interaction.modal("Bed time message", "bedTimeMessageModal") {
                            actionRow {
                                textInput(TextInputStyle.Paragraph, "bedTimeMessage", "Bed Time Message") {
                                    placeholder = "It's time for bed now!"
                                }
                            }
                        }

                        val interaction =
                            response.kord.waitFor<ModalSubmitInteractionCreateEvent>(2.minutes.inWholeMilliseconds) {
                                interaction.modalId == "bedTimeMessageModal"
                            }?.interaction

                        if (interaction == null) {
                            response.createEphemeralFollowup {
                                content = "Message configuration timed out"
                            }

                            return@action
                        }

                        val bedTimeMessage = interaction.textInputs["bedTimeMessage"]!!.value!!
                        val modalResponse = interaction.deferEphemeralResponse()

                        modalResponse.respond {
                            embed {
                                setEmbed(arguments.bedTime, arguments.dm)
                                field {
                                    name = "Message"
                                    value = if (bedTimeMessage.length >= 1024) {
                                        bedTimeMessage.substring(0, 1000)
                                    } else {
                                        bedTimeMessage
                                    }
                                }
                            }
                        }

                        BedTimeCollection().setBedtime(
                            BedTime(
                                user.id,
                                arguments.bedTime.instant,
                                bedTimeMessage,
                                arguments.dm,
                                if (arguments.dm) null else guild!!.id,
                                if (arguments.dm) null else channel.id
                            )
                        )
                    } else {
                        event.interaction.respondEphemeral {
                            embed {
                                setEmbed(arguments.bedTime, arguments.dm)
                                field {
                                    name = "Message"
                                    value = "Message not set."
                                }
                            }
                        }

                        BedTimeCollection().setBedtime(
                            BedTime(
                                user.id,
                                arguments.bedTime.instant,
                                null,
                                arguments.dm,
                                if (arguments.dm) null else guild!!.id,
                                if (arguments.dm) null else channel.id
                            )
                        )
                    }
                }
            }

            ephemeralSubCommand {
                name = "check"
                description = "Check what time you set bed time as!"

                action {
                    val bedTime = BedTimeCollection().getBedtime(user.id)
                    respond {
                        if (bedTime != null) {
                            embed {
                                title = "Bedtime info"
                                description = "You set your bedtime as the following"
                                field {
                                    name = "Time"
                                    value = bedTime.bedtime.toDiscord(TimestampType.ShortTime)
                                }
                                field {
                                    name = "Notify via DM"
                                    value = if (bedTime.dm) "True." else "False. I will remind you in ${
                                        event.kord.getGuild(bedTime.guildId!!)
                                            ?.getChannelOf<GuildMessageChannel>(bedTime.channel!!)?.mention
                                    }"
                                }
                                if (bedTime.bedtimeMessage != null) {
                                    field {
                                        name = "Message"
                                        value = if (bedTime.bedtimeMessage.length >= 1024) {
                                            bedTime.bedtimeMessage.substring(0, 1000)
                                        } else {
                                            bedTime.bedtimeMessage
                                        }
                                    }
                                }

                            }
                        } else {
                            content = "You have not set up a bed time yet! Set one up now with `/bed-time set`."
                        }
                    }
                }
            }

            ephemeralSubCommand {
                name = "remove"
                description = "Remove your bedtime"

                action {
                    BedTimeCollection().clearBedtime(user.id)
                    respond {
                        content = "Bedtime cleared!"
                    }
                }
            }
        }
    }

    private suspend fun postBedTimeNotification() {
        BedTimeCollection().getAllBedtimes().forEach {
            if (it.bedtime.toEpochMilliseconds() - Clock.System.now().toEpochMilliseconds() <= 0) {
                if (!it.dm) {
                    kord.getGuild(it.guildId!!)?.getChannelOf<GuildMessageChannel>(it.channel!!)?.createMessage {
                        content = "It's time for bed <@${it.userId}>!\n${
                            if (it.bedtimeMessage != null) "Bedtime message: ${it.bedtimeMessage}" else ""
                        }"
                    }
                    BedTimeCollection().updateBedtime(
                        it.userId,
                        BedTime(
                            it.userId,
                            it.bedtime.plus(1.days),
                            it.bedtimeMessage,
                            false,
                            it.guildId,
                            it.channel
                        )
                    )

                } else {
                    kord.getUser(it.userId)?.dm {
                        content = "It's time for bed!\n${
                            if (it.bedtimeMessage != null) "Bedtime message: ${it.bedtimeMessage}" else ""
                        }"
                    }

                    BedTimeCollection().updateBedtime(
                        it.userId,
                        BedTime(
                            it.userId,
                            it.bedtime.plus(1.days),
                            it.bedtimeMessage,
                            true,
                            it.guildId,
                            it.channel
                        )
                    )
                }
            }
        }
    }

    private fun EmbedBuilder.setEmbed(bedTime: FormattedTimestamp, dm: Boolean) {
        title = "Bedtime reminder set!"
        description = "I will remind you it is bedtime every night at this time"
        field {
            name = "Time"
            value = bedTime.toDiscord()
        }
        field {
            name = "Remind via DM"
            value =
                if (dm) "True." else "False. I will remind you in this channel"
        }
    }

    inner class BedTimeSetArgs : Arguments() {
        val bedTime by timestamp {
            name = "time"
            description = "The time to be reminded for bed time"
        }

        val dm by boolean {
            name = "dm"
            description = "Whether to dm the bedtime notification or not"
        }

        val bedTimeMessage by boolean {
            name = "bed-time-message"
            description = "Whether to add a bed time message or not"
        }
    }
}