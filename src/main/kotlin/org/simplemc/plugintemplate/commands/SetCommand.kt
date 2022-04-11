package org.simplemc.plugintemplate.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.simplemc.plugintemplate.Chromaticity

public class SetCommand(chromaticity: Chromaticity) : CommandExecutor {
    var _chromaticity: Chromaticity

    init {
        this._chromaticity = chromaticity
    }

    @Override
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.count() < 2) {
            sender.sendMessage("" + ChatColor.RED + "Syntax Error. Correct Usage: /setchroma <StartColor> <EndColor> [Player]")
            return true
        }

        var ColorA: String = args[0]
        var ColorB: String = args[1]
        var PlayerName: String = sender.name

        if (args.count() == 3) {
            PlayerName = args[2]
            if (!sender.hasPermission("chromaticity.command.set.others")) {
                sender.sendMessage("" + ChatColor.RED + "Invalid Permission!")
                return true
            }
        }

        if (!sender.hasPermission("chromaticity.command.set")) {
            sender.sendMessage("" + ChatColor.RED + "Invalid Permission!")
            return true
        }

        _chromaticity.config.set("players.$PlayerName.startColor", ColorA)
        _chromaticity.config.set("players.$PlayerName.endColor", ColorB)
        sender.sendMessage("" + ChatColor.GREEN + "Updated chroma for " + ChatColor.YELLOW + PlayerName)

        return true
    }
}
