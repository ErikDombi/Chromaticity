package org.simplemc.plugintemplate

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.simplemc.plugintemplate.commands.SetCommand
import org.simplemc.plugintemplate.commands.ToggleCommand
import org.simplemc.plugintemplate.events.PlayerChat
import org.simplemc.plugintemplate.events.PlayerJoin

/**
 * KotlinPluginTemplate plugin
 */
class Chromaticity : JavaPlugin() {

    override fun onEnable() {
        // ensure config file exists
        saveDefaultConfig()
        Bukkit.getPluginManager().registerEvents(PlayerChat(this), this)
        Bukkit.getPluginManager().registerEvents(PlayerJoin(this), this)
        this.getCommand("togglechroma")?.setExecutor(ToggleCommand(this))
        this.getCommand("setchroma")?.setExecutor(SetCommand(this))
        logger.info("${description.name} version ${description.version} enabled")
    }

    override fun onDisable() {
        //saveConfig()
        logger.info("${description.name} disabled.")
    }
}
