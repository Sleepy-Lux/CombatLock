package uk.sleepylux.combatlog.common;

import me.shedaniel.autoconfig.AutoConfig;
import uk.sleepylux.combatlog.classes.CombatInfo;

import java.util.HashMap;

public class Registry {
    public static ConfigModel config = AutoConfig.getConfigHolder(ConfigModel.class).getConfig();;
    public static final HashMap<String, CombatInfo> inCombat = new HashMap<>();
}
