package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "test to check if player has origin";
    }

    @Override
    public String getSyntax() {
        return "/origin has <player> <layer> <origintag>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if(args.length > 2){
            Player given = Bukkit.getPlayer(args[1]);
            String origintag = OriginPlayer.getOriginTag(given);
            if(origintag.equalsIgnoreCase(args[2])){
                p.sendMessage("Test passed");
            }else{
                p.sendMessage("Test failed");
            }

        }else{
            p.sendMessage(ChatColor.RED + "Invalid Args!!!");
        }
    }
}