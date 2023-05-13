package me.dueris.genesismc.core.factory.powers;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.factory.handlers.CustomOriginExistCheck;
import me.dueris.genesismc.core.factory.powers.armour.GoldArmourBuff;
import me.dueris.genesismc.core.factory.powers.block.Webbing;
import me.dueris.genesismc.core.factory.powers.block.fluid.WaterDamage;
import me.dueris.genesismc.core.factory.powers.block.solid.PumpkinHate;
import me.dueris.genesismc.core.factory.powers.entity.*;
import me.dueris.genesismc.core.factory.powers.food.Carnivore;
import me.dueris.genesismc.core.factory.powers.food.Vegitarian;
import me.dueris.genesismc.core.factory.powers.item.EnderPearlThrow;
import me.dueris.genesismc.core.factory.powers.item.GoldItemBuff;
import me.dueris.genesismc.core.factory.powers.runnables.*;
import me.dueris.genesismc.core.factory.powers.world.WorldSpawnHandler;

import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static org.bukkit.Bukkit.getServer;

public class OriginStartHandler {

    public static void StartRunnables(){

        Climbing climb = new Climbing();
        climb.runTaskTimer(getPlugin(), 0, 5);

        WaterDamage waterdamage = new WaterDamage();
        waterdamage.runTaskTimer(getPlugin(), 0, 20);

        BurningWrath burningWrath = new BurningWrath();
        burningWrath.runTaskTimer(getPlugin(), 0, 5);

        SlowFalling slowFalling = new SlowFalling();
        slowFalling.runTaskTimer(getPlugin(), 0, 5);

        Charged charged = new Charged();
        charged.runTaskTimer(getPlugin(), 0, 5);

        FelinePhobia felinePhobia = new FelinePhobia();
        felinePhobia.runTaskTimer(getPlugin(), 0, 20);

        JumpBetter jumpBetter = new JumpBetter();
        jumpBetter.runTaskTimer(getPlugin(), 0, 10);

        MineSpeed mineSpeed = new MineSpeed();
        mineSpeed.runTaskTimer(getPlugin(), 0, 10);

        Transparency transparency = new Transparency();
        transparency.runTaskTimer(getPlugin(), 0, 10);

        BurnInDaylight burnInDaylight = new BurnInDaylight();
        burnInDaylight.runTaskTimer(getPlugin(), 0, 10);

        Phantomized phantomized = new Phantomized();
        phantomized.runTaskTimer(getPlugin(), 0 , 5);

    }

    public static void StartListeners(){

        getServer().getPluginManager().registerEvents(new CustomOriginExistCheck(), getPlugin());
        getServer().getPluginManager().registerEvents(new Powers(), getPlugin());
        getServer().getPluginManager().registerEvents(new Webbing(), getPlugin());
        getServer().getPluginManager().registerEvents(new Arthropod(), getPlugin());
        getServer().getPluginManager().registerEvents(new Carnivore(), getPlugin());
        getServer().getPluginManager().registerEvents(new LayEggs(), getPlugin());
        getServer().getPluginManager().registerEvents(new Vegitarian(), getPlugin());
        getServer().getPluginManager().registerEvents(new FreshAir(), getPlugin());
        getServer().getPluginManager().registerEvents(new Reach(), getPlugin());
        //getServer().getPluginManager().registerEvents(new NoCobwebSlowdown(), GenesisMC.getPlugin()); -- removed do to anti-cheat triggers
        getServer().getPluginManager().registerEvents(new HotHands(), getPlugin());
        getServer().getPluginManager().registerEvents(new FallImmunity(), getPlugin());
        getServer().getPluginManager().registerEvents(new HotBlooded(), getPlugin());
        getServer().getPluginManager().registerEvents(new WorldSpawnHandler(), getPlugin());
        getServer().getPluginManager().registerEvents(new ExtraFireTick(), getPlugin());
        getServer().getPluginManager().registerEvents(new SilkTouch(), getPlugin());
        getServer().getPluginManager().registerEvents(new BowInability(), getPlugin());
        getServer().getPluginManager().registerEvents(new PumpkinHate(), getPlugin());
        getServer().getPluginManager().registerEvents(new Reach(), getPlugin());
        getServer().getPluginManager().registerEvents(new WaterDamage(), getPlugin());
        getServer().getPluginManager().registerEvents(new ProjectileImmune(), getPlugin());
        getServer().getPluginManager().registerEvents(new EnderPearlThrow(), getPlugin());
        getServer().getPluginManager().registerEvents(new Phantomized(), getPlugin());
        getServer().getPluginManager().registerEvents(new GoldArmourBuff(), getPlugin());
        getServer().getPluginManager().registerEvents(new GoldItemBuff(), getPlugin());
    }
}
