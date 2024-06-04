package com.bluedragonmc.quickstart.mygame

import com.bluedragonmc.quickstart.mygame.module.EggModule
import com.bluedragonmc.server.Game
import com.bluedragonmc.server.module.combat.CustomDeathMessageModule
import com.bluedragonmc.server.module.combat.OldCombatModule
import com.bluedragonmc.server.module.combat.ProjectileModule
import com.bluedragonmc.server.module.database.AwardsModule
import com.bluedragonmc.server.module.gameplay.InstantRespawnModule
import com.bluedragonmc.server.module.gameplay.WorldPermissionsModule
import com.bluedragonmc.server.module.instance.CustomGeneratorInstanceModule
import com.bluedragonmc.server.module.minigame.*
import com.bluedragonmc.server.utils.GameState
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.sound.SoundEvent
import net.minestom.server.world.DimensionType

class MyGame(mapName: String) : Game(name = "MyGame", mapName) {
    override fun initialize() {
        // Built-in game modules
        use(InstantRespawnModule())
        use(SpectatorModule(spectateOnDeath = true))
        use(VoidDeathModule(threshold = 40.0))
        use(CustomDeathMessageModule())
        use(
            WinModule(
                winCondition = WinModule.WinCondition.LAST_PLAYER_ALIVE,
                coinAwardsFunction = { player, winningTeam ->
                    // When the game ends, give 100 coins to players on the winning team and 10 coins to everyone else.
                    if (winningTeam.players.contains(player)) {
                        return@WinModule 100
                    } else {
                        return@WinModule 10
                    }
                })
        )
        use(
            MOTDModule(
                motd = Component.text(
                    "Use the eggs in your inventory to knock\n" +
                            "other players off the platform.\n" +
                            "The last player standing wins!"
                )
            )
        )
        use(OldCombatModule(allowDamage = false, allowKnockback = false), { _ ->
            // This predicate is added to each event that the module receives.
            // If we return false, the module never sees the event. This is useful
            // if we want to only add a module after the countdown has ended and the game has started.
            state == GameState.INGAME
        })
        use(ProjectileModule())
        use(
            SpawnpointModule(
                spawnpointProvider = SpawnpointModule.TestSpawnpointProvider(
                    spawns = arrayOf(Pos(8.0, 40.0, 8.0))
                )
            )
        )
        use(WorldPermissionsModule(allowBlockBreak = false, allowBlockPlace = false, allowBlockInteract = true))
        use(CountdownModule(threshold = 2, allowMoveDuringCountdown = true, countdownSeconds = 5))
        use(AwardsModule())
        use(PlayerResetModule(defaultGameMode = GameMode.SURVIVAL))

        val generator = generator@{ generationUnit: GenerationUnit ->
            // Edited from the Minestom wiki's world generation example:
            // https://wiki.minestom.net/world/generation#your-first-flat-world
            val start = generationUnit.absoluteStart()
            if (start.x() != 0.0 || start.z() != 0.0) return@generator

            val size = generationUnit.size()
            val groundHeight = 40

            for (x in 4 until 12) {
                for (z in 4 until 12) {
                    for (y in 0 until (groundHeight - start.blockY()).coerceAtMost(size.blockY())) {
                        generationUnit.modifier()
                            .setBlock(start.add(x.toDouble(), y.toDouble(), z.toDouble()), Block.IRON_BLOCK)
                    }
                }
            }
        }

        use(CustomGeneratorInstanceModule(DimensionType.OVERWORLD, generator)) { module ->
            // After the module is initialized, you can use it in this callback
            // Here, we use it to enable Minestom's lighting system in our custom world
            module.getInstance().setChunkSupplier(::LightingChunk)
        }

        // Custom modules created for this game
        use(EggModule())

        // Shorthand methods that create a module internally
        onGameStart {
            // The `Game` class inherits the `Audience` interface from Adventure, so you can use Adventure
            // methods directly. These messages and sounds will be sent to every player in the game.
            playSound(Sound.sound(SoundEvent.ENTITY_ENDER_DRAGON_GROWL, Sound.Source.MASTER, 1f, 1f))
        }

        handleEvent<WinModule.WinnerDeclaredEvent> { event ->
            // Events are scoped to this game only, so you don't have
            // to worry about other games triggering this event handler.
            event.winningTeam.players.forEach { player ->
                // Because `Game` is a `ForwardingAudience`, all players will receive this message
                sendMessage(
                    Component.text("Congratulations ", NamedTextColor.YELLOW)
                        .append(player.name)
                        .append(Component.text("!", NamedTextColor.YELLOW))
                )
            }
        }
    }
}