package dev.mr3n.contestkt

import dev.mr3n.paperallinone.customgui.inventory.CustomContentsGui.Companion.createCustomContentsGui
import dev.mr3n.paperallinone.customgui.inventory.CustomGui
import dev.mr3n.paperallinone.item.EasyItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import java.util.UUID

object ManagementGui {
    fun managementGui(player: Player, contestInfo: ContestInfo): CustomGui {
        return ContestKt.INSTANCE.createCustomContentsGui(1, Component.text(""),1,0,6,0) {
            set(0, 0, EasyItem(Material.ARROW, Component.text("前に戻る", NamedTextColor.AQUA))) {
                action(ClickType.LEFT) {
                    back()
                    managementGui(player, contestInfo).open(player)
                }
            }
            set(7, 0, EasyItem(Material.ARROW, Component.text("次へ進む", NamedTextColor.AQUA))) {
                action(ClickType.LEFT) {
                    next()
                    managementGui(player, contestInfo).open(player)
                }
            }
            set(8, 0, EasyItem(Material.LIME_WOOL, Component.text("作成", NamedTextColor.GREEN))) {
                action(ClickType.LEFT) {
                    val id = UUID.randomUUID()
                    player.performCommand("contest ${contestInfo.id} plot add ${id}")
                    player.performCommand("contest ${contestInfo.id} plot teleport ${id}")
                    player.closeInventory()
                }
            }
            contestInfo.plots.values.filter { it.owner == player.uniqueId }.forEachIndexed { index, contestPlot ->
                add(EasyItem(Material.GRASS_BLOCK, Component.text("プロット${index + 1}"))) {
                    action(ClickType.LEFT) {
                        player.performCommand("contest ${contestInfo.id} plot teleport ${contestPlot.id}")
                    }
                    action(ClickType.RIGHT) {
                        player.performCommand("contest ${contestInfo.id} plot open ${contestPlot.id}")
                    }
                }
            }
        }
    }
}