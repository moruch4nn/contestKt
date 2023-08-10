package dev.mr3n.contestkt.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.*
import dev.mr3n.contestkt.ContestInfo
import dev.mr3n.contestkt.ManagementGui
import dev.mr3n.contestkt.Storage
import dev.mr3n.paperallinone.commands.failCommand
import dev.mr3n.paperallinone.commands.successCommand
import dev.mr3n.paperallinone.item.nbt
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.command.CommandSender

object ContestCommand {
    fun register() {
        commandTree("contest") {
            literalArgument("add") {
                locationArgument("location") {
                    stringArgument("id") {
                        textArgument("displayName") {
                            integerArgument("streetWidth") {
                                integerArgument("plotSize") {
                                    anyExecutor { sender, args ->
                                        val id = args["id"] as String
                                        val displayName = args["displayName"] as String
                                        val location = args["location"] as Location
                                        val streetWidth = args["streetWidth"] as Int
                                        val plotSize = args["plotSize"] as Int
                                        val contestInfo = ContestInfo(
                                            id = id,
                                            displayName = Component.text(displayName),
                                            location = location,
                                            streetWidth = streetWidth,
                                            plotSize = plotSize
                                        )
                                        Storage.contests[id] = contestInfo
                                        sender.successCommand(NamedTextColor.GREEN to "コンテスト(${displayName}) を作成しました。")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            stringArgument("contestId") {
                replaceSuggestions { info, builder -> ArgumentSuggestions.stringCollection<CommandSender> { Storage.contests.keys }.suggest(info, builder) }
                literalArgument("plot") {
                    literalArgument("open") {
                        stringArgument("plotId") {
                            replaceSuggestions { info, builder -> ArgumentSuggestions.stringCollection<CommandSender> { Storage.contests[info.previousArgs["contestId"]?:return@stringCollection null]?.plots?.keys }.suggest(info, builder) }
                            playerExecutor { sender, args ->
                                val contestId = args["contestId"] as String
                                val plotId = args["plotId"] as String
                                val contest = Storage.contests[contestId]?:sender.failCommand(NamedTextColor.RED to "${contestId}に一致するコンテストは存在しません。")
                                val plot = contest.plots[plotId]?:sender.failCommand(NamedTextColor.RED to "${plotId}に一致するプロットは存在しません。")
                                if(plot.owner != sender.uniqueId) { sender.failCommand(NamedTextColor.RED to "操作できるのはプロットの所有者のみです。") }
                                plot.open()
                                sender.successCommand(NamedTextColor.BLUE to "プロットを開放しました。")
                            }
                        }
                    }
                    literalArgument("gui") {
                        playerExecutor { player, args ->
                            val contestId = args["contestId"] as String
                            val contest = Storage.contests[contestId]?:player.failCommand(NamedTextColor.RED to "${contestId}に一致するコンテストは存在しません。")
                            ManagementGui.managementGui(player, contest)
                        }
                    }
                    literalArgument("teleport") {
                        stringArgument("plotId") {
                            replaceSuggestions { info, builder -> ArgumentSuggestions.stringCollection<CommandSender> { Storage.contests[info.previousArgs["contestId"]?:return@stringCollection null]?.plots?.keys }.suggest(info, builder) }
                            playerExecutor { player, args ->
                                val contestId = args["contestId"] as String
                                val plotId = args["plotId"] as String
                                val contest = Storage.contests[contestId]?:player.failCommand(NamedTextColor.RED to "${contestId}に一致するコンテストは存在しません。")
                                val plot = contest.plots[plotId]?:player.failCommand(NamedTextColor.RED to "${plotId}に一致するプロットは存在しません。")
                                if(plot.owner != player.uniqueId) { player.failCommand(NamedTextColor.RED to "操作できるのはプロットの所有者のみです。") }
                                plot.join(player)
                            }
                        }
                    }
                    literalArgument("add") {
                        stringArgument("plotId") {
                            playerExecutor { player, args ->
                                if(System.currentTimeMillis()-(player.nbt { long(Keys.LAST_CREATED) }?:0L) < 1000 * 60 * 10) { player.failCommand(NamedTextColor.RED to "コンテストステージを作成するには最大10分間待つ必要があります。") }
                                val plotId = args["plotId"] as String
                                val contestId = args["contestId"] as String
                                val contest = Storage.contests[contestId]?:player.failCommand(NamedTextColor.RED to "${contestId}に一致するコンテストは存在しません。")
                                contest.addPlot(player, plotId).genPlot()
                                player.nbt { long(Keys.LAST_CREATED, System.currentTimeMillis()) }
                                player.successCommand(NamedTextColor.BLUE to "プロットを追加しました。")
                            }
                        }
                    }
                }
            }

        }
    }

    enum class Keys {
        LAST_CREATED
    }
}