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
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class DungeonsXLKillPlayersObjective extends CustomObjective implements Listener {

    public DungeonsXLKillPlayersObjective() {
        setName("DXL Kill Players Objective");
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
        for (final Quest q : quester.getCurrentQuests().keySet()) {
            final Map<String, Object> datamap = getDataForPlayer(killer, this, q);
            if (datamap != null) {
                final String dungeonNames = (String)datamap.getOrDefault("DXL Player Dungeon", "ANY");
                if (dungeonNames == null) {
                    return;
                }
                final String[] split = dungeonNames.split(",");
                for (final String str : split) {
                    if (str.equals("ANY") || str.trim().equalsIgnoreCase(dungeonName)) {
                        incrementObjective(killer, this, 1, q);
                        break;
                    }
                }
            }
        }
    }
}
