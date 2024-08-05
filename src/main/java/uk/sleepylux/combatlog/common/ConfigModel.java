package uk.sleepylux.combatlog.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

@Config(name = "Combatlog")
public class ConfigModel implements ConfigData {
    public int tag_duration = 15;

    @ConfigEntry.Category("Playerheads")
    public Heads heads = new Heads();

    public static class Heads {
        public boolean enabled = true;
        public boolean lore = true;
        public boolean lore_has_rng = true;
        public boolean head_signed_by_killer = false;
    };

    @ConfigEntry.Category("Items")
    public Items items = new Items();

    public static class Items {
        public boolean enabled = true;
        public List<String> banned_items = List.of("ender_pearl", "firework_rocket");
    };


}
