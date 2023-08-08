package dev.mr3n.contestkt.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.*
import dev.mr3n.contestkt.ContestInfo
import dev.mr3n.contestkt.Storage
import dev.mr3n.paperallinone.commands.failCommand
import dev.mr3n.paperallinone.commands.successCommand
import net.kyori.adventure.bossbar.BossBar
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
                                plot.open()
                                sender.successCommand(NamedTextColor.BLUE to "プロットを開放しました。")
                            }
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
                                plot.join(player)
                            }
                        }
                    }
                    literalArgument("add") {
                        stringArgument("plotId") {
                            playerExecutor { player, args ->
                                val plotId = args["plotId"] as String
                                val contestId = args["contestId"] as String
                                val contest = Storage.contests[contestId]?:player.failCommand(NamedTextColor.RED to "${contestId}に一致するコンテストは存在しません。")
                                contest.addPlot(player, plotId).genPlot()
                                player.successCommand(NamedTextColor.BLUE to "プロットを追加しました。")
                            }
                        }
                    }
                }
            }

        }
    }
}