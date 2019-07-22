package me.pikamug.DungeonsXLQuests;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.erethon.dungeonsxl.event.dmob.DMobDeathEvent;
import de.erethon.dungeonsxl.mob.DNPCRegistry;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class DungeonsXLKillObjective extends CustomObjective implements Listener {
	private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
	private static DNPCRegistry registry;
	
	public DungeonsXLKillObjective() {
		setName("DXL Kill Mobs Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("Kill Obj", "Set a name for the objective", "Kill dungeon mob");
		addStringPrompt("Kill Names", "Enter dungeon mob names, separating each one by a comma", "ANY");
		setCountPrompt("Set the amount of dungeon mobs to kill");
		setDisplay("%Kill Obj% %Kill Names%: %count%");
	}
	
	@EventHandler
	public void onDMobDeath(DMobDeathEvent event) {
		Player killer = event.getDMob().getEntity().getKiller();
		if (killer == null) {
			return;
		}
		Quester quester = quests.getQuester(killer.getUniqueId());
		if (quester == null) {
			return;
		}
		Entity entity = event.getDMob().getEntity();
		if (registry != null && entity != null && registry.isNPC(entity)) {
			return;
		}
		String mobName = entity.getName();
		for (Quest q : quester.getCurrentQuests().keySet()) {
			Map<String, Object> datamap = getDataForPlayer(killer, this, q);
			if (datamap != null) {
				String mobNames = (String)datamap.getOrDefault("Kill Names", "ANY");
				if (mobNames == null) {
					return;
				}
				String[] spl = mobNames.split(",");
				for (String str : spl) {
					if (str.equals("ANY") || mobName.equalsIgnoreCase(str)) {
						incrementObjective(killer, this, 1, q);
						return;
					}
				}
			}
		}
	}
}