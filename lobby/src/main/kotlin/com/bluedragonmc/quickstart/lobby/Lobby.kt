package com.bluedragonmc.quickstart.lobby

import com.bluedragonmc.server.Game
import com.bluedragonmc.server.module.gameplay.WorldPermissionsModule
import com.bluedragonmc.server.module.instance.SharedInstanceModule
import com.bluedragonmc.server.module.map.AnvilFileMapProviderModule
import com.bluedragonmc.server.module.minigame.MOTDModule
import com.bluedragonmc.server.module.minigame.PlayerResetModule
import com.bluedragonmc.server.module.minigame.SpawnpointModule
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import java.nio.file.Paths

class Lobby : Game("Lobby", "lobby") {
    override fun initialize() {
        use(SharedInstanceModule())
        use(AnvilFileMapProviderModule(Paths.get("worlds/Lobby/lobby")))
        use(
            SpawnpointModule(
                spawnpointProvider = SpawnpointModule.TestSpawnpointProvider(
                    spawns = arrayOf(Pos(8.5, -60.0, 8.5))
                )
            )
        )
        use(WorldPermissionsModule(allowBlockBreak = false, allowBlockPlace = false, allowBlockInteract = true))
        use(PlayerResetModule(defaultGameMode = GameMode.SURVIVAL))

        use(
            MOTDModule(
                Component.text("Welcome!\nTo join the example game, \ntype ")
                    .append(Component.text("/join MyGame", NamedTextColor.BLUE))
                    .append(Component.text(".", NamedTextColor.WHITE))
            )
        )
    }
}