package me.pikamug.DungeonsXLQuests;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.event.player.GlobalPlayerRewardPayOutEvent;
import de.erethon.dungeonsxl.reward.LevelReward;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class DungeonsXLLevelRewardObjective extends CustomObjective implements Listener {
	private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");

	public DungeonsXLLevelRewardObjective() {
		setName("DXL Level Reward Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("Level Obj", "Set a name for the objective", "Get reward level");
		addStringPrompt("Level Amounts", "Enter specific amounts of money, separating each one by a comma", "ANY");
		setCountPrompt("Set the quantity of level rewards to get");
		setDisplay("%Level Obj% %Level Amounts%: %count%");
	}

	@EventHandler
	public void onDGamePlayerFinish(GlobalPlayerRewardPayOutEvent event) {
		Player recipient = event.getBukkitPlayer().getPlayer();
		Quester quester = quests.getQuester(recipient.getUniqueId());
		if (quester == null) {
			return;
		}
		ArrayList<Integer> dungeonRewardNames = new ArrayList<Integer>();
		for (Reward r : event.getRewards()) {
			if (r instanceof LevelReward) {
				LevelReward ir = (LevelReward) r;
				dungeonRewardNames.add(ir.getLevels());
			}
		}
		for (Quest q : quester.getCurrentQuests().keySet()) {
			Map<String, Object> datamap = getDataForPlayer(recipient, this, q);
			if (datamap != null) {
				String rewardNames = (String)datamap.getOrDefault("Level Amounts", "ANY");
				String[] spl = rewardNames.split(",");
				for (String str : spl) {
					if (str != null) {
						if (str.equals("ANY") || dungeonRewardNames.contains(Integer.valueOf(str))) {
							incrementObjective(recipient, this, 1, q);
							return;
						}
					}
				}
				return;
			}
		}
	}
}
