package com.bluedragonmc.quickstart.mygame.module

import com.bluedragonmc.server.Game
import com.bluedragonmc.server.event.GameStartEvent
import com.bluedragonmc.server.module.GameModule
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

/**
 * Gives all players a full inventory of eggs when the game starts.
 */
class EggModule : GameModule() {

    // `initialize` is run when the game is created, so you can use it to register
    // event handlers before the game starts.
    override fun initialize(parent: Game, eventNode: EventNode<Event>) {

        val inventoryOfEggs = Array(36) { ItemStack.of(Material.EGG, 16) }.toList()

        // `GameStartEvent` comes from the CountdownModule and is triggered at the same time as the "GO!" title
        eventNode.addListener(GameStartEvent::class.java) { event ->
            parent.players.forEach { player ->
                player.inventory.addItemStacks(inventoryOfEggs, TransactionOption.ALL)
            }
        }
    }
}