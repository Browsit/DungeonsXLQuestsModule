package me.pikamug.DungeonsXLQuests;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerFinishEvent;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class DungeonsXLFinishObjective extends CustomObjective implements Listener {
	private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
	
	public DungeonsXLFinishObjective() {
		setName("DXL Finish Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("Finish Obj", "Set a name for the objective", "Finish dungeon");
		addStringPrompt("Dungeon Names", "Enter dungeon names, separating each one by a comma", "ANY");
		setCountPrompt("Set the amount of floors to finish");
		setDisplay("%Finish Obj% %Dungeon Names%: %count%");
	}
	
	@EventHandler
	public void onDGamePlayerFinish(DGamePlayerFinishEvent event) {
		Player finisher = event.getDPlayer().getPlayer();
		Quester quester = quests.getQuester(finisher.getUniqueId());
		if (quester == null) {
			return;
		}
		String dungeonName = event.getDPlayer().getDGroup().getDungeonName();
		for (Quest q : quester.getCurrentQuests().keySet()) {
			Map<String, Object> datamap = getDataForPlayer(finisher, this, q);
			if (datamap != null) {
				String dungeonNames = (String)datamap.getOrDefault("Dungeon Names", "ANY");
				if (dungeonNames == null) {
					incrementObjective(finisher, this, 1, q);
				}
				String[] spl = dungeonNames.split(",");
				for (String str : spl) {
					if (str.equals("ANY") || dungeonName.equalsIgnoreCase(str)) {
						incrementObjective(finisher, this, 1, q);
						return;
					}
				}
				return;
			}
		}
	}
}