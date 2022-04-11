package org.simplemc.plugintemplate.events

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.simplemc.plugintemplate.Chromaticity
import java.util.regex.Pattern

public class PlayerChat(chromaticity: Chromaticity) : Listener {
    var _chromaticity: Chromaticity

    init {
        _chromaticity = chromaticity
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        if (!e.player.hasPermission("chromaticity.chat"))
            return

        if (!_chromaticity.config.getBoolean("players.${e.player.name}.enabled"))
            return

        var message = ChatColor.translateAlternateColorCodes('&', e.message)
        var specialCharacters = "klmnorKLMNOR".toCharArray()
        var characters = message.toMutableList()
        var sections: MutableList<String> = mutableListOf<String>()

        while (characters.isNotEmpty()) {
            var nextChar = characters.removeAt(0)

            if (nextChar != '§') {
                sections.add(nextChar.toString())
                continue
            }

            if (characters[0] == 'r') {
                characters.removeAt(0)
                continue
            }

            var newSection = StringBuilder(nextChar.toString())
            if (sections.isNotEmpty())
                if (sections.last().startsWith('§') && sections.last().length == 2)
                    newSection = StringBuilder(sections.removeLast() + nextChar.toString())

            while (characters.isNotEmpty()) {
                if (characters[0] == '§')
                    break

                nextChar = characters.removeAt(0)
                newSection.append(nextChar)
            }
            var newSectionString = newSection.toString().replace("§r", "").replace("§R", "")
            var tmp = newSectionString.elementAtOrElse(1, { '.' })
            if (specialCharacters.contains(newSectionString.lowercase().elementAtOrElse(1, { '.' }))) {
                var code = newSectionString.lowercase().elementAt(1)
                var newSectionStringSubstr = newSectionString.substring(2)
                for (charToAdd in newSectionStringSubstr) {
                    sections.add("§$code$charToAdd")
                }
                continue
            }

            sections.add(newSectionString)
        }

        sections = sections.filter { it.length != 0 }.toMutableList()

        var configColorA = _chromaticity.getConfig().getString("players.${e.player.name}.startColor")?.replace("#", "")?.trim()
        var configColorB = _chromaticity.getConfig().getString("players.${e.player.name}.endColor")?.replace("#", "")?.trim()

        var ColorA = Integer.parseInt(configColorA, 16)
        if (ChatColor.stripColor(e.message)?.length == 1) {
            e.message = hex("§#$configColorA$message")!!
            return
        }
        var ColorB = Integer.parseInt(configColorB, 16)

        var ARed = (ColorA shr 16) and 0xff
        var AGreen = (ColorA shr 8) and 0xff
        var ABlue = ColorA and 0xff

        var BRed = (ColorB shr 16) and 0xff
        var BGreen = (ColorB shr 8) and 0xff
        var BBlue = ColorB and 0xff

        var steps = ChatColor.stripColor(sections.joinToString(""))!!.length - 1 // sections.filter { !it.startsWith('§') && !specialCharacters.contains(it.getOrElse(1, { 'g' })) }

        var messageBuilder = StringBuilder()
        for (index in 0..sections.count() - 1) {
            var section = sections[index]
            if (section.startsWith('§') && !specialCharacters.contains(section.lowercase().elementAtOrElse(1, { '.' }))) {
                messageBuilder.append(section)
                continue
            }

            var red = (((BRed - ARed) * (index / steps.toFloat())) + ARed).toInt()
            var green = (((BGreen - AGreen) * (index / steps.toFloat())) + AGreen).toInt()
            var blue = (((BBlue - ABlue) * (index / steps.toFloat())) + ABlue).toInt()

            // Recombine the colors, convert them to hex string, and append to start of the string
            var newColor = blue or (green shl 8) or (red shl 16) // interpolateColorsCompact(ColorA, ColorB, index.toFloat() / steps.toFloat()) //
            var hexColor = String.format("#%06X", (0xFFFFFF and newColor))
            messageBuilder.append("§#$hexColor$section")
        }
        e.message = hex(messageBuilder.toString())!!
    }

    fun interpolateColorsCompact(a: Int, b: Int, lerp: Float): Int {
        var MASK1: Int = 0xff00ff
        var MASK2: Int = 0x00ff00

        var f2: Int = (256 * lerp).toInt()
        var f1: Int = 256 - f2

        return (((((a and MASK1) * f1) + ((b and MASK1) * f2)) shr 8) and MASK1) or (((((a and MASK2) * f1) + ((b and MASK2) * f2)) shr 8) and MASK2)
    }

    fun hex(message: String): String? {
        var message = message
        val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
        var matcher = pattern.matcher(message)
        while (matcher.find()) {
            val hexCode = message.substring(matcher.start(), matcher.end())
            val replaceSharp = hexCode.replace('#', 'x')
            val ch = replaceSharp.toCharArray()
            val builder = StringBuilder("")
            for (c in ch) {
                builder.append("&$c")
            }
            message = message.replace(hexCode, builder.toString())
            matcher = pattern.matcher(message)
        }
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}
