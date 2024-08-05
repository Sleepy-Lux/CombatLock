package uk.sleepylux.combatlog.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import uk.sleepylux.combatlog.classes.CombatInfo;
import uk.sleepylux.combatlog.common.Registry;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class onDamage {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static boolean run(LivingEntity entity, DamageSource source, float _amount) {
        if (entity instanceof PlayerEntity player
                && source.getAttacker() instanceof PlayerEntity attacker) {

            boolean handle = true;
            CombatInfo fetched_info = Registry.inCombat.get(attacker.getUuidAsString());
            if (fetched_info != null)
                if(Objects.equals(fetched_info.enemy_uuid, player.getUuidAsString())) {
                    Registry.inCombat.remove(player.getUuidAsString());
                    handle = false;
                }
            Registry.inCombat.remove(attacker.getUuidAsString());

            Instant now = Instant.now();
            long entry_time_ms = now.toEpochMilli();
            long exit_time_ms = entry_time_ms + (Registry.config.tag_duration * 1000L);

            Registry.inCombat.put(player.getUuidAsString(), new CombatInfo(
                    player.getUuidAsString(),
                    attacker.getUuidAsString(),
                    entry_time_ms,
                    exit_time_ms
            ));
            Registry.inCombat.put(attacker.getUuidAsString(), new CombatInfo(
                    attacker.getUuidAsString(),
                    player.getUuidAsString(),
                    entry_time_ms,
                    exit_time_ms
            ));

            if (handle) StartScheduler(player, attacker);
        };
        return true;
    }

    private static void StartScheduler(PlayerEntity player, PlayerEntity attacker) {
        Text entry_player_text = Text.of(
                Text.literal("You are in combat with ").styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))).append(
                        Text.literal(attacker.getName().getString()).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000)))).append(
                        Text.literal(" (" + Registry.config.tag_duration + "s)").styled(style -> style.withColor(TextColor.fromRgb(0xFFFF00))))
        );
        Text entry_attacker_text = Text.of(
                Text.literal("You are in combat with ").styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))).append(
                        Text.literal(player.getName().getString()).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000)))).append(
                        Text.literal(" (" + Registry.config.tag_duration + "s)").styled(style -> style.withColor(TextColor.fromRgb(0xFFFF00))))
        );
        player.sendMessage(entry_player_text);
        attacker.sendMessage(entry_attacker_text);

        scheduler.scheduleWithFixedDelay(() -> {
            CombatInfo info = Registry.inCombat.get(player.getUuidAsString());

            if (info == null || !Registry.inCombat.containsKey(player.getUuidAsString())) { //removal from inCombat check
                Text player_text = Text.of(
                        Text.literal("You are no longer in combat with ").styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))).append(
                                Text.literal(attacker.getName().getString()).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000)))).append(
                                    Text.literal(".").styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))))
                );
                Text attacker_text = Text.of(
                        Text.literal("You are no longer in combat with ").styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))).append(
                                Text.literal(player.getName().getString()).styled(style -> style.withColor(TextColor.fromRgb(0xFF0000)))).append(
                                Text.literal(".").styled(style -> style.withColor(TextColor.fromRgb(0xFF00FF))))
                );

                player.sendMessage(player_text);
                attacker.sendMessage(attacker_text);
                throw new Error(CloseScheduler(player, attacker));
            }

            long exit_time_ms = info.exit_time_ms;
            Instant current_now = Instant.now();
            long time_left = ((exit_time_ms - current_now.toEpochMilli()) / 1000);

            if (time_left % 5 != 0)
                return;

            if (time_left == 0) {
                CloseScheduler(player, attacker);
            };
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static String CloseScheduler(PlayerEntity player, PlayerEntity attacker)  {
        Registry.inCombat.remove(player.getUuidAsString());
        Registry.inCombat.remove(attacker.getUuidAsString());
        return "Task Closed";
    }
}
