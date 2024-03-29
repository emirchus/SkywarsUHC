package me.emi.swh.commands;

import me.emi.swh.Main;
import me.emi.swh.game.Arena;
import me.emi.swh.utils.ConfigMessages;
import me.emi.swh.utils.LoadSchematics;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SwhCommand implements CommandExecutor{

    private final Main main = Main.getInstance();

    private final ConfigMessages msg = ConfigMessages.getConfig();
    private boolean reload;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("skywarshardcore")) {
            if (args.length == 0) {

                p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", "Need args"));
                p.sendMessage(getHelpMessage().toString());
            } else if (args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")){
                    if(p.hasPermission("swh.reload")){
                        reload = true;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(reload == true){
                                    p.sendMessage("§aReload configuration of §6Skywars§bUHC");

                                    p.sendMessage("§aReloaded configuration of §6Skywars§bUHC");

                                    Main.getInstance().reloadConfig();
                                    ConfigMessages.getConfig().reload();
                                    reload = false;
                                }
                            }
                        }.runTaskTimer(Main.getPlugin(Main.class), 60, 20);

                    }
                }
                else if(args[0].equalsIgnoreCase("leave")){
                    Arena arena = Main.getInstance().getArenaManager().getArenaByPlayer(p.getName());

                    if (arena != null) {
                        if(arena.getIngame().contains(p.getName())){
                            arena.leavePlayer(p);
                        }else {
                            p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", "You dont are in match"));
                        }
                    }
                    else if(arena == null) {
                        p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", "You dont are in a arena"));
                    }
                }else {
                  p.sendMessage(getHelpMessage().toString());
                }
            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("removearena")) {
                    String arenaname = args[1];
                   if(p.hasPermission("swh.removearena")){
                       if (main.getArenaManager().getArenaByName(arenaname) == null) {
                           p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", arenaname + " does exist"));
                       } else {
                           main.getConfig().set("arenas." + arenaname, null);
                           main.saveConfig();

                           p.sendMessage(msg.getString("messages.arenadelete").replace("&", "§").replace("{arenaname}", arenaname));
                       }
                   }else{
                       p.sendMessage(getHelpMessage().toString());
                   }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                   if(p.hasPermission("swh.addspawns")){
                       String arenaname = args[1];
                       Arena arena = Main.getInstance().getArenaManager().getArenaByName(arenaname);
                       if (arena == null) {
                           p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", arenaname + " does exist"));
                       } else {
                           if (arena.getSpawns().size() < arena.getMaxPlayers()) {

                               arena.addSpawn(p.getLocation());
                               p.sendMessage(msg.getString("messages.addspawn").replace("&", "§").replace("{arenaname}", arenaname).replace("{spawns}", arena.getSpawns().size() + "|" + arena.getMaxPlayers()));

                               List<String> currentSpawns = Main.getInstance().getConfig().getStringList("arenas." + arenaname + ".spawns");
                               currentSpawns.add(Main.getInstance().serializeLocation(p.getLocation()));
                               main.getConfig().set("arenas." + arenaname + ".spawns", currentSpawns);
                               main.saveConfig();
                           }else{

                               p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", "You do not have space for more spawns"));
                           }
                       }
                   }else{
                       p.sendMessage(getHelpMessage().toString());
                   }
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                   if(p.hasPermission("swh.setlobby")){
                       String arenaname = args[1];
                       Arena arena = Main.getInstance().getArenaManager().getArenaByName(arenaname);

                       if (arena == null) {
                           p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", arenaname + " does exist"));
                       } else {
                           arena.setLobbySpawn(p.getLocation());
                           main.getConfig().set("arenas." + arenaname + ".lobbyspawn", main.serializeLocation(p.getLocation()));
                           p.sendMessage(msg.getString("messages.addlobby").replace("&", "§").replace("{arenaname}", arenaname));
                           Main.getInstance().saveConfig();
                       }
                   }else{
                       p.sendMessage(getHelpMessage().toString());
                   }
                }
                else if(args[0].equalsIgnoreCase("setmainlobby")){
                    if(p.hasPermission("swh.setmainlobby")){
                        String arenaname = args[1];
                        Arena arena = Main.getInstance().getArenaManager().getArenaByName(arenaname);
                        if (arena == null) {
                            p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", arenaname + " does exist"));
                        } else {
                            arena.setMainLobbySpawn(p.getLocation());
                            main.getConfig().set("arenas." + arenaname + ".mainlobby", main.serializeLocation(p.getLocation()));
                            p.sendMessage("§aMain lobby has been set");
                            Main.getInstance().saveConfig();
                        }
                    }else{
                        p.sendMessage(getHelpMessage().toString());
                    }
                }
                else if(args[0].equalsIgnoreCase("setspecspawn")){
                    if(p.hasPermission("swh.setspecspawn")){
                        String arenaname = args[1];
                        Arena arena = Main.getInstance().getArenaManager().getArenaByName(arenaname);
                        if (arena == null) {
                            p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", arenaname + " does exist"));
                        } else {
                            arena.setMainLobbySpawn(p.getLocation());
                            main.getConfig().set("arenas." + arenaname + ".specspawn", main.serializeLocation(p.getLocation()));
                            p.sendMessage("§aThe spawn for specs has been set");
                            Main.getInstance().saveConfig();
                        }
                    }else{
                        p.sendMessage(getHelpMessage().toString());
                    }
                }

            }
            else if(args.length == 3){

            }
            else if (args.length == 4) {
                if(args[0].equalsIgnoreCase("load")) {
                    if (p.hasPermission("swh.load.schem")) {
                        if (args[1].equalsIgnoreCase("schematic") || args[1].equalsIgnoreCase("schem")) {
                            boolean trueorfalse = Boolean.valueOf(args[3]);
                            String schem = args[2];
                            LoadSchematics.onLoadSchematic(p, schem, trueorfalse);
                            p.sendMessage("§6[§6lSWH§6] §aSchematic successfully loaded §e" + schem);
                        }
                    } else {
                        p.sendMessage(getHelpMessage().toString());
                    }
                }
                else if(args[1].equalsIgnoreCase("world") || args[1].equalsIgnoreCase("w")){
                    if(p.hasPermission("swh.load.world")){
                        String worlname = args[2];
                        World world = Bukkit.createWorld(WorldCreator.name(worlname));

                        p.teleport(world.getSpawnLocation());
                        p.sendMessage("§6[§6lSWH§6] §aWorld successfully loaded §e"+worlname);
                    }else{
                        p.sendMessage(getHelpMessage().toString());
                    }
                }
               else  if (args[0].equalsIgnoreCase("createarena")) {
                   if(p.hasPermission("swh.createarena")){
                       String arenaname = args[1];
                       int maxplayers = Integer.valueOf(args[2]);
                       int minplayers = Integer.valueOf(args[3]);
                       if (main.getArenaManager().getArenaByName(arenaname) != null) {
                           p.sendMessage(msg.getString("messages.errormessage").replace("&", "§").replace("{error}", arenaname + " does exist"));
                       } else {
                           main.getConfig().set("arenas." + arenaname, "");
                           main.getConfig().set("arenas." + arenaname + ".lobbyspawn", "NOEXIST");
                           main.getConfig().set("arenas." + arenaname + ".mainlobby", "NOEXIST");
                           main.getConfig().set("arenas." + arenaname + ".specspawn", "NOEXIST");
                           main.getConfig().set("arenas." + arenaname + ".spawns", new ArrayList<>());
                           main.getConfig().set("arenas." + arenaname + ".maxplayers", maxplayers);
                           main.getConfig().set("arenas." + arenaname + ".minplayers", minplayers);
                           main.getConfig().set("arenas." + arenaname + ".sign", "NOEXIST");
                           main.saveConfig();

                           new Arena(arenaname, null, null, null,new ArrayList<>(), main.getConfig().getInt("arenas." + arenaname + ".maxplayers"), null);

                           p.sendMessage(msg.getString("messages.arenacreate").replace("&", "§").replace("{arenaname}", arenaname)
                                   .replace("{maxplayers}", String.valueOf(maxplayers)));
                       }
                   }else{
                       p.sendMessage(getHelpMessage().toString());
                   }
                }else{
                    p.sendMessage(getHelpMessage().toString());
                }
            }else {
                p.sendMessage(getHelpMessage().toString());
            }
        }
            return true;
    }


    public StringBuilder getHelpMessage(){

        StringBuilder msg = new StringBuilder("§6SkywarsUHC §aCommands\n");
        msg.append("§a§m________________________________________________\n");
        msg.append("§a[]-Optionals <>-Required\n");
        msg.append("\n");
        msg.append("§b/swh createarena <arenaname> <maxplayers> <minplayers>- §7Create with name and max players\n");
        msg.append("§b/swh removearena <arenaname> - §7Delete arena forever\n");
        msg.append("§b/swh addspawn <arena> - §7Add spawn for the arena\n");
        msg.append("§b/swh setlobby <arena> - §7Set pre lobby of the arena\n");
        msg.append("§b/swh setmainlobby <arena> - §7Set main lobby\n");
        msg.append("§b/swh setscenarios <arena> - §7Open scenarios gui\n");
        msg.append("§b/swh leave - §7Return to lobby\n");
        msg.append("§b/swh load <world-schem> <worlname-schemname> <true-false>- §7Load world or load schematic. True = ignore air. False = dont ignore air\n");
        msg.append("§a§m________________________________________________\n");

        return msg;
    }

}

