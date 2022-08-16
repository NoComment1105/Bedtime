/*
 * Copyright (c) 2022 NoComment1105 <nocomment1105@outlook.com>
 *
 * This file is part of Bedtime.
 *
 * Licensed under the MIT license. For more information,
 * please see the LICENSE file
 */

package io.github.nocomment1105.bedtime.extensions

import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.linkButton
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.rest.builder.message.create.embed

class HelpExtension : Extension() {
    override val name: String = "help-extension"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "help"
            description = "Get help with Bedtime"

            action {
                respond {
                    embed {
                        title = "Bedtime help"
                        // TODO when kordx.emoji actually works, add an emoji in places
                        // Fun fact, I did write this a 1am :)
                        description = "Bedtime allows you to set custom times at which you will be reminded to go to " +
                                "bed. No more getting carried away and oops, it's, uh, 1am.\n\nThe bed-time set " +
                                "command allows you to set up the time you'd like to go to bed. It takes in a " +
                                "timestamp, you can generate them at [this site]" +
                                "(https://bchaing.github.io/discord-timestamp/).\nIt allows you to choose whether to " +
                                "sent a DM when it's bedtime or sent a message in the channel in a server. Along " +
                                "with this you are able to provide a custom message to your bed time notification"
                        footer {
                            text = "Created by NoComment#6411"
                        }
                    }
                    components {
                        linkButton {
                            label = "Invite"
                            url =
                                "https://discord.com/api/oauth2/authorize?client_id=1008676566785597471&permissions=" +
                                        "19456&scope=bot%20applications.commands"
                        }
                        linkButton {
                            label = "GitHub"
                            url = "https://github.com/NoComment1105/Bedtime"
                        }
                        linkButton {
                            label = "Privacy policy"
                            url = "https://github.com/NoComment1105/blob/main/docs/privacy-policy.md"
                        }
                    }
                }
            }
        }
    }
}
