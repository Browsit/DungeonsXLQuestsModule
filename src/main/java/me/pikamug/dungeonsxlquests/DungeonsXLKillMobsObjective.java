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

import de.erethon.dungeonsxl.api.event.mob.DungeonMobDeathEvent;
import de.erethon.dungeonsxl.mob.DNPCRegistry;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class DungeonsXLKillMobsObjective extends BukkitCustomObjective implements Listener {

	public DungeonsXLKillMobsObjective() {
		setName("DungeonsXL Kill Mobs Objective");
		setAuthor("PikaMug");
		setShowCount(true);
		addStringPrompt("DXL Mob Obj", "Set a name for the objective", "Kill dungeon mob");
		addStringPrompt("DXL Mob Name", "Enter dungeon mob names, separating each one by a comma", "ANY");
		setCountPrompt("Set the amount of dungeon mobs to kill");
		setDisplay("%DXL Mob Obj% %DXL Mob Name%: %count%");
	}

	@EventHandler
	public void onDMobDeath(DungeonMobDeathEvent event) {
		final Player killer = event.getDungeonMob().getEntity().getKiller();
		if (killer == null) {
			return;
		}
		final Quester quester = DungeonsXLModule.getQuests().getQuester(killer.getUniqueId());
		if (quester == null) {
			return;
		}
		final Entity entity = event.getDungeonMob().getEntity();
		if (entity == null || new DNPCRegistry().isNPC(entity)) {
			return;
		}
		final String mobName = entity.getName();
		for (final Quest q : quester.getCurrentQuests().keySet()) {
			final Map<String, Object> datamap = getDataForPlayer(killer.getUniqueId(), this, q);
			if (datamap != null) {
				final String mobNames = (String)datamap.getOrDefault("DXL Mob Name", "ANY");
				if (mobNames == null) {
					return;
				}
				final String[] spl = mobNames.split(",");
				for (final String str : spl) {
					if (str.equals("ANY") || mobName.equalsIgnoreCase(str)) {
						incrementObjective(killer.getUniqueId(), this, q, 1);
						return;
					}
				}
			}
		}
	}
}
