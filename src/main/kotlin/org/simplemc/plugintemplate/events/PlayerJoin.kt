package org.simplemc.plugintemplate.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.simplemc.plugintemplate.Chromaticity

public class PlayerJoin(chromaticity: Chromaticity) : Listener {
    var _chromaticity: Chromaticity

    init {
        _chromaticity = chromaticity
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if(!_chromaticity.config.isSet("players.${e.player.name}.enabled"))
            _chromaticity.config.set("players.${e.player.name}.enabled", false)

        if(!_chromaticity.config.isSet("players.${e.player.name}.startColor"))
            _chromaticity.config.set("players.${e.player.name}.startColor", "#FFFFFF")

        if(!_chromaticity.config.isSet("players.${e.player.name}.endColor"))
            _chromaticity.config.set("players.${e.player.name}.endColor", "#FFFFFF")
    }
}
