package dev.mr3n.contestkt

import dev.mr3n.paperallinone.configuration.kotlinx.serialization.serializer.LocationSerializer
import dev.mr3n.paperallinone.configuration.kotlinx.serialization.serializer.UUIDSerializer
import dev.mr3n.paperallinone.location.box
import dev.mr3n.paperallinone.location.fill
import dev.mr3n.paperallinone.location.wall
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.UUID

@Serializable
data class ContestPlot(
    @Serializable(with = LocationSerializer::class)
    val from: Location,
    @Serializable(with = LocationSerializer::class)
    val to: Location,
    @Serializable(with = UUIDSerializer::class)
    val owner: UUID,
    val id: String,
    val gate: Gate,
    var isOpened: Boolean = false,
) {

    fun genPlot() {
        val material = Material.LIGHT_GRAY_GLAZED_TERRACOTTA
        to.add(0.0,1.0,0.0)
        from.wall(to,1.0) { location -> location.block.type = material }
        from.clone().fill(to.clone().apply { y = from.y }, 1.0) {
            it.block.type = material
        }
        from.clone().subtract(0.0,1.0,0.0).fill(to.clone().apply { y = from.y }.subtract(0.0,1.0,0.0), 1.0) { it.block.type = Material.BARRIER }
        from.clone().subtract(0.0,1.0,0.0).apply { y = to.y }.fill(to.clone().subtract(0.0,1.0,0.0), 1.0) { it.block.type = Material.BARRIER }
        from.clone().subtract(1.0,0.0,1.0).wall(to.clone().add(1.0,0.0,1.0),1.0) { it.block.type = Material.BARRIER }
    }

    fun join(player: Player) {
        player.teleport(from.toVector().setY(minOf(to.y,from.y)).add(to.toVector().setY(minOf(to.y,from.y))).multiply(0.5).toLocation(from.world).add(0.0,1.0,0.0))
    }

    fun open() {
        this.isOpened = true
        gate.from.fill(gate.to, 1.0) { it.block.type = Material.AIR }
    }

    @Serializable
    data class Gate(
        @Serializable(with = LocationSerializer::class)
        val from: Location,
        @Serializable(with = LocationSerializer::class)
        val to: Location
    )
}