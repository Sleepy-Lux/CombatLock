package uk.sleepylux.combatlog.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import uk.sleepylux.combatlog.classes.CombatInfo;
import uk.sleepylux.combatlog.common.Registry;

public class onLogout {
    public static void run(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if (handler.getPlayer() instanceof ServerPlayerEntity player) {
            if (Registry.inCombat.get(player.getUuidAsString()) != null) {
                player.kill();

                CombatInfo info = Registry.inCombat.get(player.getUuidAsString());
                Registry.inCombat.remove(info.enemy_uuid);
                Registry.inCombat.remove(player.getUuidAsString());
            };
        };
    }
}
