package me.pikamug.DungeonsXLQuests;

import java.util.ArrayList;
import java.util.Map;

import de.erethon.dungeonsxl.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerRewardEvent;
import de.erethon.dungeonsxl.reward.ItemReward;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class DungeonsXLItemRewardObjective extends CustomObjective implements Listener {
	private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
	
	public DungeonsXLItemRewardObjective() {
		setName("DXL Item Reward Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("Item Obj", "Set a name for the objective", "Get reward item");
		addStringPrompt("Item Names", "Enter item names, separating each one by a comma", "ANY");
		setCountPrompt("Set the quantity of item rewards to get");
		setDisplay("%Item Obj% %Item Names%: %count%");
	}
	
	@EventHandler
	public void onDGamePlayerFinish(DGamePlayerRewardEvent event) {
		Player recipient = event.getDPlayer().getPlayer();
		Quester quester = quests.getQuester(recipient.getUniqueId());
		if (quester == null) {
			return;
		}
		ArrayList<String> dungeonRewardNames = new ArrayList<String>();
		for (Reward r : event.getRewards()) {
			if (r instanceof ItemReward) {
				ItemReward ir = (ItemReward) r;
				for (ItemStack is : ir.getItems()) {
					if (is != null) {
						if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
							dungeonRewardNames.add(is.getItemMeta().getDisplayName().toLowerCase());
						} else {
							dungeonRewardNames.add(is.getType().name().toLowerCase());
						}
					}
				}
			}
		}
		for (Quest q : quester.getCurrentQuests().keySet()) {
			Map<String, Object> datamap = getDataForPlayer(recipient, this, q);
			if (datamap != null) {
				String rewardNames = (String)datamap.getOrDefault("Item Names", "ANY");
				String[] spl = rewardNames.split(",");
				for (String str : spl) {
					if (str != null) {
						if (str.equals("ANY") || dungeonRewardNames.contains(str.toLowerCase())) {
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