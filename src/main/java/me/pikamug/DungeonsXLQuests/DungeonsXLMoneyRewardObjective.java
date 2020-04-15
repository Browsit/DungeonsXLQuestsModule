package me.pikamug.DungeonsXLQuests;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerRewardEvent;
import de.erethon.dungeonsxl.reward.MoneyReward;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class DungeonsXLMoneyRewardObjective extends CustomObjective implements Listener {
	private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
	
	public DungeonsXLMoneyRewardObjective() {
		setName("DXL Money Reward Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("Money Obj", "Set a name for the objective", "Get reward money");
		addStringPrompt("Money Amounts", "Enter specific amounts of money, separating each one by a comma", "ANY");
		setCountPrompt("Set the quantity of money rewards to get");
		setDisplay("%Money Obj% %Money Amounts%: %count%");
	}
	
	@EventHandler
	public void onDGamePlayerFinish(DGamePlayerRewardEvent event) {
		Player recipient = event.getDPlayer().getPlayer();
		Quester quester = quests.getQuester(recipient.getUniqueId());
		if (quester == null) {
			return;
		}
		ArrayList<Double> dungeonRewardNames = new ArrayList<Double>();
		for (Reward r : event.getRewards()) {
			if (r instanceof MoneyReward) {
				MoneyReward ir = (MoneyReward) r;
				dungeonRewardNames.add(ir.getMoney());
			}
		}
		for (Quest q : quester.getCurrentQuests().keySet()) {
			Map<String, Object> datamap = getDataForPlayer(recipient, this, q);
			if (datamap != null) {
				String rewardNames = (String)datamap.getOrDefault("Money Amounts", "ANY");
				String[] spl = rewardNames.split(",");
				for (String str : spl) {
					if (str != null) {
						if (str.equals("ANY") || dungeonRewardNames.contains(Double.valueOf(str))) {
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