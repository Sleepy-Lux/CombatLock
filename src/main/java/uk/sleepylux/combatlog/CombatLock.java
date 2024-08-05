/*
    Modlock, A Anti-Combatlog mod originally built for fabric minecraft 1.21
    Copyright (C) 2024 SleepyLux

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package uk.sleepylux.combatlog;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.command.CommandManager;
import org.slf4j.LoggerFactory;
import uk.sleepylux.combatlog.commands.ReloadCommand;
import uk.sleepylux.combatlog.common.ConfigModel;
import uk.sleepylux.combatlog.events.*;

public class CombatLock implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(ConfigModel.class, Toml4jConfigSerializer::new);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("clreload").executes(new ReloadCommand()::run));
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(onDamage::run);
        ServerLivingEntityEvents.AFTER_DEATH.register(onDeath::run);
        ServerPlayerEvents.AFTER_RESPAWN.register(onRespawn::run);
        UseItemCallback.EVENT.register(onItemUse::run);
        ServerPlayConnectionEvents.DISCONNECT.register(onLogout::run);

        UseBlockCallback.EVENT.register(onBlockPlace::run);

        LoggerFactory.getLogger(this.getClass()).info("Loaded Successfully.");
    }
}
