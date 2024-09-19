package uk.sleepylux.combatlog.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import uk.sleepylux.combatlog.common.Registry;

import java.util.Optional;


public class onItemUse {
    public static TypedActionResult<ItemStack> run(PlayerEntity player, World _world, Hand _hand) {
        ItemStack stack = player.getStackInHand(_hand);

        if (!Registry.config.items.enabled) return TypedActionResult.pass(stack);

        Optional<RegistryKey<Item>> key = stack.getItem().getRegistryEntry().getKey();
        if (key.isEmpty()) return TypedActionResult.fail(stack);

        if (Registry.inCombat.containsKey(player.getUuidAsString())
                && Registry.config.items.banned_items.contains(key.get().getValue().toString().split(":")[1])) {
            Text exit_text = Text.of(
                    Text.literal("You cannot use ").styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))).append(
                            Text.literal(stack.getName().getString()).styled(style -> style.withColor(TextColor.fromRgb(0xFFFF00)))).append(
                                Text.literal(" during combat.").styled(style -> style.withColor(TextColor.fromRgb(0xFF0000))))
            );

            player.sendMessage(exit_text);
            player.getInventory().markDirty();
            player.currentScreenHandler.sendContentUpdates();
            return TypedActionResult.fail(stack);
        }

        return TypedActionResult.pass(stack);
    }
}
