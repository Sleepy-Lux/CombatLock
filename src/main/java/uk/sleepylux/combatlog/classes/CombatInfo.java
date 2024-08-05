package uk.sleepylux.combatlog.classes;

public class CombatInfo {
    public String id;
    public String enemy_uuid;
    public long entry_time_ms;
    public long exit_time_ms;

    public CombatInfo(String id, String attacker_uuid, long entry_time_ms, long expiry_date_ms) {
        this.id = id;
        this.enemy_uuid = attacker_uuid;
        this.entry_time_ms = entry_time_ms;
        this.exit_time_ms = expiry_date_ms;
    }
}
