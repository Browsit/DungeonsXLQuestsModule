/*
 * Copyright (c) 2019 PikaMug and contributors. All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.pikamug.dungeonsxlquests;

import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.event.player.GlobalPlayerRewardPayOutEvent;
import de.erethon.dungeonsxl.reward.ItemReward;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class DungeonsXLItemRewardObjective extends CustomObjective implements Listener {

	public DungeonsXLItemRewardObjective() {
		setName("DXL Item Reward Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("DXL Item Obj", "Set a name for the objective", "Get reward item");
		addStringPrompt("DXL Item Name", "Enter item names, separating each one by a comma", "ANY");
		setCountPrompt("Set the quantity of item rewards to get");
		setDisplay("%DXL Item Obj% %DXL Item Name%: %count%");
	}

	@EventHandler
	public void onDGamePlayerFinish(GlobalPlayerRewardPayOutEvent event) {
		final Player recipient = event.getBukkitPlayer();
		final Quester quester = DungeonsXLModule.getQuests().getQuester(recipient.getUniqueId());
		if (quester == null) {
			return;
		}
		final ArrayList<String> dungeonRewardNames = new ArrayList<>();
		for (final Reward r : event.getRewards()) {
			if (r instanceof ItemReward) {
				final ItemReward ir = (ItemReward) r;
				for (final ItemStack is : ir.getItems()) {
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
			final Map<String, Object> datamap = getDataForPlayer(recipient, this, q);
			if (datamap != null) {
				final String rewardNames = (String)datamap.getOrDefault("DXL Item Name", "ANY");
				final String[] spl = rewardNames.split(",");
				for (final String str : spl) {
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
