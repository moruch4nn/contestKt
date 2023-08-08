package dev.mr3n.contestkt

import dev.mr3n.paperallinone.configuration.kotlinx.serialization.serializer.ComponentSerializer
import dev.mr3n.paperallinone.configuration.kotlinx.serialization.serializer.LocationSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

@Serializable
data class ContestInfo(
    val id: String,
    @Serializable(with = ComponentSerializer::class)
    val displayName: Component,
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    val streetWidth: Int,
    val plotSize: Int,
    val plots: MutableMap<String, ContestPlot> = mutableMapOf()
) {


    fun addPlot(player: Player, id: String): ContestPlot {
        val plot =  if(plots.size % 2 == 0) {
            val from = location.clone().add(streetWidth/2.0,0.0,(plots.size / 2) * (plotSize + 5.0))
            val to = from.clone().add(plotSize / 1.0, plotSize / 1.0, plotSize / 1.0)
            val gateTo = from.clone().add(0.0,(plotSize / 1.0),plotSize / 1.0)
            ContestPlot(from, to, player.uniqueId, id, ContestPlot.Gate(from.clone().add(0.0,1.0,0.0 + 1), gateTo.subtract(0.0,0.0,1.0)))
        } else {
            val from = location.clone().add((-streetWidth/2.0)-plotSize / 1.0,0.0,(plots.size / 2) * (plotSize + 5.0))
            val to = from.clone().add(plotSize / 1.0, plotSize / 1.0, plotSize / 1.0)
            val gateTo = to.clone().add(0.0,(plotSize / 1.0),0.0)
            ContestPlot(from, to, player.uniqueId, id, ContestPlot.Gate(from.clone().subtract(plotSize / -1.0,0.0,0.0).add(0.0,1.0,1.0), gateTo.subtract(0.0,0.0,1.0)))
        }
        this.plots[id] = plot
        return plot
    }
}