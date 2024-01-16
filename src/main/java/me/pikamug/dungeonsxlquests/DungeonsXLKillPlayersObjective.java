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

import de.erethon.dungeonsxl.api.event.player.GamePlayerDeathEvent;
import me.pikamug.quests.enums.ObjectiveType;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class DungeonsXLKillPlayersObjective extends BukkitCustomObjective implements Listener {

    public DungeonsXLKillPlayersObjective() {
        setName("DungeonsXL Kill Players Objective");
        setAuthor("DSroD");
        setShowCount(true);
        addStringPrompt("DXL Player Obj", "Set a name for the objective", "Kill players in dungeon");
        addStringPrompt("DXL Player Dungeon", "Enter dungeon names, separating each one by a comma", "ANY");
        setCountPrompt("Set the amount of players to kill");
        setDisplay("%DXL Player Obj% in %DXL Player Dungeon%: %count%");
    }

    @EventHandler
    public void onPlayerDeath(GamePlayerDeathEvent e) {
        final Player killer = e.getBukkitPlayer().getKiller();
        if (killer == null) {
            return;
        }
        final Quester quester = DungeonsXLModule.getQuests().getQuester(killer.getUniqueId());
        if (quester == null) {
            return;
        }
        final String dungeonName = e.getGamePlayer().getGameWorld().getDungeon().getName();
        for (final Quest quest : quester.getCurrentQuests().keySet()) {
            final Map<String, Object> datamap = getDataForPlayer(killer.getUniqueId(), this, quest);
            if (datamap != null) {
                final String dungeonNames = (String)datamap.getOrDefault("DXL Player Dungeon", "ANY");
                if (dungeonNames == null) {
                    return;
                }
                final String[] split = dungeonNames.split(",");
                for (final String str : split) {
                    if (str.equals("ANY") || str.trim().equalsIgnoreCase(dungeonName)) {
                        incrementObjective(killer.getUniqueId(), this, quest, 1);

                        quester.dispatchMultiplayerEverything(quest, ObjectiveType.CUSTOM,
                                (final Quester q, final Quest cq) -> {
                                    incrementObjective(q.getUUID(), this, quest, 1);
                                    return null;
                                });
                        break;
                    }
                }
            }
        }
    }
}
