package uk.sleepylux.combatlog.commands;

import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import uk.sleepylux.combatlog.common.ConfigModel;
import uk.sleepylux.combatlog.common.Registry;

public class ReloadCommand {
    public int run(CommandContext<ServerCommandSource> context) {
        Registry.config = AutoConfig.getConfigHolder(ConfigModel.class).getConfig();

        Text text = Text.of(
                Text.literal("CombatLog's ")).copy().styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))).append(
                Text.literal("config has been succesfully reloaded").copy().styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)))
        );
        context.getSource().sendMessage(text);
        return 1;
    };
}
