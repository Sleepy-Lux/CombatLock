package uk.sleepylux.combatlog.events;

import net.minecraft.server.network.ServerPlayerEntity;
import uk.sleepylux.combatlog.common.Registry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class onRespawn {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static void run(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (alive) scheduler.schedule(() -> {
            Registry.inCombat.remove(newPlayer.getUuidAsString());
        }, 3, TimeUnit.SECONDS);
    }
}
