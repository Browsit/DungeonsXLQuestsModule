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
import de.erethon.dungeonsxl.reward.LevelReward;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Map;

public class DungeonsXLLevelRewardObjective extends CustomObjective implements Listener {

	public DungeonsXLLevelRewardObjective() {
		setName("DXL Level Reward Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("DXL Level Obj", "Set a name for the objective", "Get reward level");
		addStringPrompt("DXL Level Amount", "Enter money amounts, separating each one by a comma", "ANY");
		setCountPrompt("Set the quantity of level rewards to get");
		setDisplay("%DXL Level Obj% %DXL Level Amount%: %count%");
	}

	@EventHandler
	public void onDGamePlayerFinish(GlobalPlayerRewardPayOutEvent event) {
		final Player recipient = event.getBukkitPlayer();
		final Quester quester = DungeonsXLModule.getQuests().getQuester(recipient.getUniqueId());
		if (quester == null) {
			return;
		}
		final ArrayList<Integer> dungeonRewardNames = new ArrayList<>();
		for (final Reward r : event.getRewards()) {
			if (r instanceof LevelReward) {
				final LevelReward ir = (LevelReward) r;
				dungeonRewardNames.add(ir.getLevels());
			}
		}
		for (final Quest q : quester.getCurrentQuests().keySet()) {
			final Map<String, Object> datamap = getDataForPlayer(recipient, this, q);
			if (datamap != null) {
				final String rewardNames = (String)datamap.getOrDefault("DXL Level Amount", "ANY");
				if (rewardNames == null) {
					return;
				}
				final String[] spl = rewardNames.split(",");
				for (final String str : spl) {
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