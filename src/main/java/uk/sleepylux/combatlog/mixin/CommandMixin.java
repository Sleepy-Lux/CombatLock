package uk.sleepylux.combatlog.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.sleepylux.combatlog.common.Registry;
import uk.sleepylux.combatlog.events.onDeath;

import java.util.List;
import java.util.Objects;

@Mixin(CommandManager.class)
public class CommandMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void onExecute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci) {
        if (parseResults.getContext().getSource().getPlayer() == null) return;

        PlayerEntity player = parseResults.getContext().getSource().getPlayer();
        if (Objects.equals(command, "head")) {
            onDeath.DropHead(player, player);
            return;
        };

        if (Registry.inCombat.get(player.getUuidAsString()) == null) return;

        CommandContextBuilder<ServerCommandSource> context = parseResults.getContext();
        ServerCommandSource source = context.getSource();

        List<String> banned_commands = List.of("tpa", "tpaaccept", "spawn", "home", "warp", "shop", "tp");
        if (banned_commands.contains(command.split(" ")[0])) {

            source.sendError(Text.of("You cannot run this until you are out of combat."));
            ci.cancel();
        }
    }
}
