package nycuro.tasks;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import nycuro.api.API;

import java.util.UUID;

/**
 * author: NycuRO
 * FactionsCore Project
 * API 1.0.0
 */
public class CombatLoggerTask extends Task {

    private Object2IntMap<UUID> k = new Object2IntOpenHashMap<>();

    @Override
    public void onRun(int i) {
        for (Player player : API.getMainAPI().getServer().getOnlinePlayers().values()) {
            if (API.getMechanicAPI().isOnSpawn(player)) {
                Effect effect = Effect.getEffect(Effect.SPEED);
                effect.setAmplifier(1);
                effect.setDuration(20 * 3);
                effect.setVisible(false);
                player.addEffect(effect);
            }
        }
        API.getCombatAPI().inCombat.forEach((uuid, time) -> {
            if (k.getOrDefault(uuid, -1) == -1) k.put(uuid, 13);
            long count = k.getInt(uuid);
            float procent = (float) ((int) (count * 100 / 13));
            API.getMainAPI().bossbar.get(uuid).setText("      §7-§8=§7- §7CombatLogger: §6§l" + k.getInt(uuid) + " §7-§8=§7-");
            if (k.getInt(uuid) <= 1) API.getMainAPI().bossbar.get(uuid).setLength(1F);
            else API.getMainAPI().bossbar.get(uuid).setLength(procent);
            if (k.getInt(uuid) == 0) {
                API.getMainAPI().getServer().getPlayer(uuid).ifPresent( (player) -> {
                    player.sendMessage(API.getMessageAPI().getMessageCombatLogger(player));
                    API.getCombatAPI().removeCombat(player);
                    k.removeInt(player.getUniqueId());
                    API.getMainAPI().bossbar.get(player.getUniqueId()).setLength(100F);
                });
            }
            k.replace(uuid, k.getInt(uuid) - 1);
        });
    }
}
