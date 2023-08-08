package dev.mr3n.contestkt

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.mr3n.contestkt.commands.ContestCommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ContestKt: JavaPlugin() {

    private val contestsJson = File("contests.json")

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false))
    }

    override fun onEnable() {
        INSTANCE = this
        CommandAPI.onEnable()
        ContestCommand.register()
        this.reloadConfig()
    }

    override fun onDisable() {
        this.saveConfig()
    }

    override fun saveConfig() {
        contestsJson.writeText(Json.encodeToString(Storage.contests.toMap()))
    }

    override fun reloadConfig() {
        if(contestsJson.exists()) { Storage.contests.putAll(Json.decodeFromString(contestsJson.readText())) }
    }

    companion object {
        lateinit var INSTANCE: ContestKt
    }
}