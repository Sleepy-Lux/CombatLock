package uk.sleepylux.combatlog.events;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import org.slf4j.LoggerFactory;
import uk.sleepylux.combatlog.CombatLock;
import uk.sleepylux.combatlog.classes.CombatInfo;
import uk.sleepylux.combatlog.classes.TooltipLore;
import uk.sleepylux.combatlog.common.Registry;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class onDeath {
    public static void run(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof PlayerEntity player
                && damageSource.getAttacker() instanceof PlayerEntity attacker) {
            if (!Registry.config.heads.enabled
                || attacker.getUuid() == player.getUuid()) return;

            CombatInfo info = Registry.inCombat.get(player.getUuidAsString());
            if (info != null) {


                Registry.inCombat.remove(attacker.getUuidAsString());
            };
        };
    }

    public static void DropHead(PlayerEntity player, PlayerEntity attacker) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);

        if (Registry.config.heads.lore) {
            String final_lore = "This is a real bug (0%?)";

            String[][] lores_info = new String[][]{
                    new String[] { "Unlucky. (30%)", "300" },
                    new String[] { "End of the line. (20%)", "200" },
                    new String[] { "A worthy opponent. (15%)", "150" },
                    new String[] { "Close your eyes. (15%)", "150" },
                    new String[] { "Sleep tight. (12%)", "120" },
                    new String[] { "Farewell. (5%)", "50" },
                    new String[] { "Shut your eyes. (3%)", "30" },
                    new String[] { "Checkmate. (0.4%)", "4" },
                    new String[] { "? (0.1%)", "1" },
            };
            ArrayList<TooltipLore> lores = new java.util.ArrayList<>();
            for (String[] lore_info : lores_info)
                lores.add(new TooltipLore(
                        Registry.config.heads.lore_has_rng ? lore_info[0] : lore_info[0].split(" \\(")[0],
                        Registry.config.heads.lore_has_rng ? Integer.parseInt(lore_info[1]) : 10)
                );

            int total_weight = 1000;
            int random_weight = Random.create().nextInt(total_weight) + 1;
            int cumulative_weight = 0;

            for (TooltipLore lore : lores) {
                cumulative_weight += lore.weight;
                if (cumulative_weight >= random_weight) {
                    final_lore = lore.text;
                    break;
                };
            };

            LocalDateTime date_time = LocalDateTime.ofInstant(Instant.ofEpochSecond(Instant.now().getEpochSecond()), ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
            String time = formatter.format(date_time);

            stack.set(DataComponentTypes.LORE, new LoreComponent(List.of(Text.of(final_lore),
                    Text.of((Registry.config.heads.head_signed_by_killer) ? "Killed by " + attacker.getName() + " on " + time + ".\n" : " "))));
        };

        NbtCompound nbt_compound = new NbtCompound();
        nbt_compound.putBoolean("CustomCombatLog", true);
        stack.set(DataComponentTypes.CUSTOM_DATA,  NbtComponent.of(nbt_compound));

        GameProfile profile = player.getGameProfile();

        String skinURL = FetchSkinFromMojangAPI(profile.getId().toString()); // return the data from "https://sessionserver.mojang.com/session/minecraft/profile/<playerUUID>"
        profile.getProperties().put("textures", new Property("textures",
                Objects.requireNonNullElse(skinURL, FetchSkinFromMojangAPI("956fe27582dc46b882495276a318a1ff"))));

        ProfileComponent component = new ProfileComponent(
                Optional.of(player.getName().getString()),
                Optional.of(UUID.fromString(player.getUuidAsString())),
                profile.getProperties(),
                profile
        );
        stack.set(DataComponentTypes.PROFILE, component);

        System.out.println(Objects.requireNonNull(stack.get(DataComponentTypes.PROFILE)).gameProfile());
        ItemEntity dropped_item = player.dropItem(stack, false, false);
        if (dropped_item != null) {
            dropped_item.velocityModified = true;
            dropped_item.setVelocity(0, 0.1, 0);
        };
    }

    private static String FetchSkinFromMojangAPI(String uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject properties = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject();
            return properties.get("value").getAsString();
        } catch (Exception e) {
            LoggerFactory.getLogger(CombatLock.class).error("Failed to fetch skin from mojang servers. Real error: " + e.getMessage());
            return null;
        }
    }
}
