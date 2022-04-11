package org.simplemc.plugintemplate.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.simplemc.plugintemplate.Chromaticity

public class ToggleCommand(chromaticity: Chromaticity) : CommandExecutor {
    var _chromaticity: Chromaticity

    init {
        this._chromaticity = chromaticity
    }

    @Override
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command is only available for players!")
            return true
        }

        var player: Player = sender as Player
        var name = player.name

        if (!player.hasPermission("chromaticity.command.toggle")) {
            player.sendMessage("" + ChatColor.RED + "Invalid Permission!")
            return true
        }

        if (args.count() == 1) {
            if (!player.hasPermission("chromaticity.command.toggle.others")) {
                player.sendMessage("" + ChatColor.RED + "Invalid Permission!")
                return true
            }
            name = args[0]
        }

        var newValue = !_chromaticity.config.getBoolean("players.${name}.enabled")
        _chromaticity.config.set("players.${name}.enabled", newValue)

        if (newValue)
            player.sendMessage("" + ChatColor.GREEN + "Chromaticity Enabled for $name!")
        else
            player.sendMessage("" + ChatColor.RED + "Chromaticity Disabled for $name!")

        return true
    }
}
