package com.github.fate0608.Battlegrounds;

import java.io.File;
import java.io.IOException;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Battlegrounds extends JavaPlugin {
	
	private Server srv = this.getServer();
	private boolean isStarted;

	@Override
	public void onEnable(){
			
		
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerDeath(this,getServer()),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerJoin(this,getServer()),this);
		initConfig();
		this.getLogger().info("Battlegrounds wurde aktiviert.");
		
		/* Maybe later: add a seperate file players to the config folder, instead of using the plugin.yml
		if(!playersFile.exists()){
			try 
			{
				playersFile.createNewFile();
			} 
			catch (IOException e) 
			{
				getLogger().info(e.getMessage());
			}
		}
		*/
	}

	private void initConfig() {
		this.reloadConfig();
		this.getConfig()
				.options()
				.header("#Willkommen bei Battlegrounds! Danke, dass du mein Plugin benutzt. Für Feedback, nutze bitte den Diskussionsthread auf der Spigot-Downloadpage. "
						+ "\n#Dieses Plugin basiert auf dem Spielmodus VARO, dessen geistlicher Inhaber ich nicht bin. Von mir stammt lediglich die Umsetzung in Java.");
		this.getConfig().addDefault("Battlegrounds.commands.STATUS", false);
		isStarted = this.getConfig().getBoolean("Battlegrounds.commands.STATUS");
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.getLogger().info("Battlegrounds wurde erfolgreich (re)loaded!");
	}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
    	if(sender instanceof Player)
        {
                    if (cmd.getName().equalsIgnoreCase("bg"))
                    {
                        if(args != null && args.length==1)
                        {
                            if(args[0].equalsIgnoreCase("status"))
                            {
				            					                    
			                    if(isStarted)
			                    {
			                    	sender.sendMessage(ChatColor.DARK_AQUA + "Battlegrounds ist gestartet. Es leben noch " + ChatColor.RED + " XX " + ChatColor.DARK_AQUA + "Spieler!");
			                    }
			                    else
			                    {
			                    	sender.sendMessage(ChatColor.DARK_AQUA + "Battlegrounds ist derzeit nicht gestartet.");
			                    }
			                    return true;
			                }
                            else if((args[0].equalsIgnoreCase("start")))
                            {
                            	if(!getConfig().getBoolean("STATUS"))
                            	{
                            		getConfig().set("Battlegrounds.commands.STATUS", true);
                            		saveConfig();
                            		StartGame();
                            	}
                            }
                           
                            if(sender.isOp())
                            {
                                if(args[0].equalsIgnoreCase("addplayers"))
                                {
                                	sender.sendMessage(ChatColor.DARK_AQUA + "Füge alle Spieler des Servers hinzu!");
                                	List<Player> Players = new ArrayList<Player>();
                                	
                                	for(Player p : srv.getOnlinePlayers())
                                	{
                                		Players.add(p);
                                		
                                		if(!getConfig().contains("Players." + p.getUniqueId().toString()))
                            			{
                                			getConfig().set("Players." + p.getUniqueId().toString() + "("+p.getDisplayName()+")" + ".Dead",false);
                                			getConfig().set("Players." + p.getUniqueId().toString() + "("+p.getDisplayName()+")" + ".Kills",0);
                                			getConfig().set("Players." + p.getUniqueId().toString() + "("+p.getDisplayName()+")" + ".KilledBy","");

                                			saveConfig();
                            			}
                                		
                                	}
								
                                }
                            }
                            else
                            {
                            	sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigungen dafür!");
                            }
                        }

                        return false;  
                    } 
                    return false;
        }
    	return false;
    }
    

    
	private void StartGame() 
	{
		srv.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				int sec = 30;
				
				if(sec > 0)
				{
					for(Player p : srv.getOnlinePlayers())
					{
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20,10));
						srv.broadcastMessage(ChatColor.RED + "BATTLEGROUNDS startet in " + ChatColor.GOLD +  sec + " Sekunden" + ChatColor.RED +" ! Bereitet euch vor!");
					}
				}
				else
				{
					srv.broadcastMessage(ChatColor.RED + "BATTLEGROUNDS startet! Viel Erfolg!!");
				}
				
				sec--;
    		}
    	},0, 30*20);

		srv.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				for(Player p : srv.getOnlinePlayers())
				{
					p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				}
    		}
    	}, 30*20);
		
	}

	@Override 
	public void onDisable(){

        this.getLogger().info("Battlegrounds wurde deaktiviert.");
	}
	
}
