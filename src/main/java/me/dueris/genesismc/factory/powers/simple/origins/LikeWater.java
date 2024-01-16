package me.dueris.genesismc.factory.powers.simple.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.Gravity;
import me.dueris.genesismc.factory.powers.simple.PowerProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class LikeWater extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> likeWaterPlayers = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("like_water");
    private static Gravity gravityHook = new Gravity();

    @Override
    public void run(Player p) {
        gravityHook.run(p);
    }

    @Override
    public String getPowerFile() {
        return null;
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return likeWaterPlayers;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
