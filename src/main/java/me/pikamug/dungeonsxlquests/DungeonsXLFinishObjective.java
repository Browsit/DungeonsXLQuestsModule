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

import de.erethon.dungeonsxl.api.event.player.GamePlayerFinishEvent;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class DungeonsXLFinishObjective extends BukkitCustomObjective implements Listener {

	public DungeonsXLFinishObjective() {
		setName("DungeonsXL Finish Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("DXL Finish Obj", "Set a name for the objective", "Finish dungeon");
		addStringPrompt("DXL Finish Dungeon", "Enter dungeon names, separating each one by a comma", "ANY");
		setCountPrompt("Set the amount of floors to finish");
		setDisplay("%DXL Finish Obj% %DXL Finish Dungeon%: %count%");
	}

	@EventHandler
	public void onDGamePlayerFinish(GamePlayerFinishEvent event) {
		final Player finisher = event.getBukkitPlayer();
		final Quester quester = DungeonsXLModule.getQuests().getQuester(finisher.getUniqueId());
		if (quester == null) {
			return;
		}
		final String dungeonName = event.getGamePlayer().getGroup().getDungeon().getName();
		for (final Quest q : quester.getCurrentQuests().keySet()) {
			final Map<String, Object> datamap = getDataForPlayer(finisher.getUniqueId(), this, q);
			if (datamap != null) {
				final String dungeonNames = (String)datamap.getOrDefault("DXL Finish Dungeon", "ANY");
				if (dungeonNames == null) {
					return;
				}
				final String[] spl = dungeonNames.split(",");
				for (final String str : spl) {
					if (str.equals("ANY") || dungeonName.equalsIgnoreCase(str)) {
						incrementObjective(finisher.getUniqueId(), this, q, 1);
						return;
					}
				}
				return;
			}
		}
	}
}
