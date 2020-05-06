package me.pikamug.DungeonsXLQuests;

import de.erethon.dungeonsxl.api.event.player.GamePlayerDeathEvent;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class DungeonXLPlayerKillObjective extends CustomObjective implements Listener {
    private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");

    public DungeonXLPlayerKillObjective() {
        setName("DXL Kill Players Objective");
        setAuthor("DSroD");
        setShowCount(true);
        addStringPrompt("Objective name", "Set a name for the objective", "Kill players in dungeon");
        addStringPrompt("Dungeon names", "Set a name of dungeons where players should be killed, separated by comma", "ANY");
        setCountPrompt("Set the amount of players to kill");
        setDisplay("%Objective name% in %Dungeon names%: %count%");
    }

    @EventHandler
    public void OnPlayerDeath(GamePlayerDeathEvent e) {

        Player killer = e.getGamePlayer().getPlayer().getKiller();
        if(killer == null) {
            return;
        }
        Quester quester = quests.getQuester(killer.getUniqueId());
        if(quester == null)
        {
            return;
        }

        String dungeonName = e.getGamePlayer().getGameWorld().getDungeon().getName();
        for (Quest q : quester.getCurrentQuests().keySet()) {
            Map<String, Object> datamap = getDataForPlayer(killer, this, q);
            String dungs = (String)datamap.getOrDefault("Dungeon names", "ANY");
            if(dungs == null) {
                continue;
            }
            String[] split = dungs.split(",");
            for (String str : split) {
                if(str.equals("ANY") || str.equalsIgnoreCase(dungeonName)) {
                    incrementObjective(killer, this, 1, q);
                    break;
                }
            }
        }
    }

}
