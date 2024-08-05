package uk.sleepylux.combatlog.events;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class onBlockPlace {
    public static ActionResult run(PlayerEntity player, World _world, Hand hand, BlockHitResult _hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        NbtComponent custom_data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (custom_data != null && custom_data.contains("CustomCombatLog")) {
            Text text = Text.of(
                    Text.literal("You cannot place playerheads from PVP").styled(style -> style.withColor(TextColor.fromRgb(0xFF0000)))
            );
            player.sendMessage(text);

            return ActionResult.FAIL;
        };

        return ActionResult.PASS;
    }
}
