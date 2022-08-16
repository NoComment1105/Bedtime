# Bedtime bot commands list
#### This file contains the list of commands and functions in Bedtime bot

### bed-time set
Sets the time you'd like to be notified that it's bedtime.
Arguments:
* `time` - Bed time. Discord timestamp.
* `dm` - Whether to DM you the notification. Boolean
* `bedtimeMessage` - Whether you'd like to supply a custom bedtime message. Boolean
Result:
* Sets the bedtime at which you'll be reminded each night

### bed-time check
Checks what time you set bedtime as
Result:
* Returns an embed with the time you set as bedtime, whether you'll be DM'd or not and what hte content of the message is

### bed-time remove
Result:
* Removes the time you set as bedtime

### help
Displays some information on how to use these commands
