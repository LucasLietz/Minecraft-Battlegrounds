package com.github.fate0608.Battlegrounds;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Battlegrounds extends JavaPlugin {
	
	private Server srv = this.getServer();
	private boolean isStarted;
	private int SecGlob;
	private int taskId;
	
	@Override
	public void onEnable(){
			
		
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerDeath(this,getServer()),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerJoin(this,getServer()),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnBlockBreak(this),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerMove(this),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerQuit(this),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnDamage(this),this);
		
		initConfig();
		this.getLogger().info("Battlegrounds wurde aktiviert.");
	}

	private void initConfig() {
		this.reloadConfig();
		this.getConfig()
				.options()
				.header("#Willkommen bei Battlegrounds! Danke, dass du mein Plugin benutzt. Für Feedback, nutze bitte den Diskussionsthread auf der Spigot-Downloadpage. "
						+ "\n#Dieses Plugin basiert auf dem Spielmodus VARO, dessen geistiger Inhaber ich nicht bin. Von mir stammt lediglich die Umsetzung dieser Variante des beliebten Modus."
						+ "Vermeide es, die Configparameter anzupassen.");
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
    		if(args.length==0)
    		{
    			sender.sendMessage(ChatColor.RED + "Syntax: " + ChatColor.DARK_GREEN + "/bg [addplayers, status, start, statistics, setspawn[spawnid], assignspawns[Anzahl Spieler]]");
    			return false;
    		}

                    if (cmd.getName().equalsIgnoreCase("bg"))
                    {

                		
                        if(args != null && args.length==1)
                        {
                            if(args[0].equalsIgnoreCase("status"))
                            {
                            	isStarted = this.getConfig().getBoolean("Battlegrounds.commands.STATUS");
				            					                    
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
                            
                            if((args[0].equalsIgnoreCase("start")))
                            {
                            	if(!getConfig().getBoolean("STATUS"))
                            	{
                            		getConfig().set("Battlegrounds.commands.STATUS", true);
                            		saveConfig();
                            		StartGame(30);
                            		
                            	}
                            }
                            if((args[0].equalsIgnoreCase("statistics")))
                            {
                            	
                            	sender.sendMessage(ChatColor.DARK_AQUA + "~~~~S~~T~~A~~T~~I~~S~~T~~I~~K~~~~\n"
										   + "~Scoreboard:\n");

                            	                            	
                            	for(OfflinePlayer p : srv.getOfflinePlayers())
                            	{
                            		int kills = getConfig().getInt("Players." + p.getUniqueId().toString() + ".Kills");
                            		sender.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.DARK_AQUA + " mit " 
                            		+ ChatColor.GOLD + kills + ChatColor.DARK_AQUA +" Kills.");
                            	}
                            	return true; 
                            }
                            if(sender.isOp())
                            {
                                AddPlayers(sender, args);
                                ReloadPlugin(sender, args);
                                return true; 

                            }
                            else if(!sender.isOp())
                            {
                            	sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigungen dafür!");
                            	sender.sendMessage(ChatColor.RED + "Syntax: " + ChatColor.DARK_GREEN + "/bg [addplayers, status, start, statistics, setspawn[spawnid], assignspawns[Anzahl Spieler]]");
                            	return false; 
                            }

                        }

                        if(args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("assignspawns"))
                        {
                        	if(sender.isOp())
                        	{
                        		if(args.length==2)
                        		{
                                    SetSpawnpoint(sender, args);
                                    AssignPlayersToSpawns(sender, args);
                                    return true;
                        		}
                        		else
                        		{
                        			sender.sendMessage(ChatColor.RED + "Du bist zwar OP, doch hast nicht genug Parameter angegeben!");
                        			return false; 
                        		}

                        	}
                        	else
                        	{
                        		sender.sendMessage(ChatColor.RED + "Du hast nicht die nötigen Rechte! (OP)");
                        		sender.sendMessage(ChatColor.RED + "Syntax: " + ChatColor.DARK_GREEN + "/bg [addplayers, status, start, statistics, setspawn[spawnid], assignspawns[Anzahl Spieler]]");
                        		return false; 
                        	}
                        }
                        else
                        {
                        	sender.sendMessage(ChatColor.RED + "Syntax: " + ChatColor.DARK_GREEN + "/bg [addplayers, status, start, statistics, setspawn[spawnid], assignspawns[Anzahl Spieler]]");
                        	return false; 
                        }

                         
                    }
                    else
                    {
                    	sender.sendMessage(ChatColor.RED + "Syntax: " + ChatColor.DARK_GREEN + "/bg [addplayers, status, start, statistics, setspawn[spawnid], assignspawns[Anzahl Spieler]]");
                    	return false;
                    }
                    
        }
    	return false;
    }

	private void AssignPlayersToSpawns(CommandSender sender, String[] args) 
	{
		//Portet Spieler in die Locations!
		
		if(args[0].equalsIgnoreCase("assignspawns") && args[1] != null)
		{

			int amount = 0 ;
			try
	        {
	        	amount = Integer.parseInt(args[1]);
	        }
	        catch(Exception ex)
	        {
	        	sender.sendMessage(ChatColor.RED + "Das übergebene Argument konnte nicht als Ganzzahl ermittelt werden. Syntax: /bg assignspawns [MENGE DER SPIELER]");
	        }

			List<Location> locationList = new ArrayList<Location>();
			locationList.addAll(GetSpawnpoints(locationList, amount, sender));
			
			int i = Integer.parseInt(args[1].toString()); 
			


					for(Player p : getServer().getOnlinePlayers())
					{
						Location loc = locationList.get(0);
						TeleportPlayerToDistinctLocation(p, loc);
						locationList.remove(0);
						i--;
					}
				
		}
	}

	private void TeleportPlayerToDistinctLocation(Player p, Location loc) 
	{
		p.teleport(new Location(loc.getWorld(),loc.getX(),loc.getY()+1,loc.getZ()));
	}

	private Collection<Location> GetSpawnpoints(List<Location> locationList, int amount, CommandSender sender) 
	{		
		Player senderPlayer = (Player)sender;
		for(int i = 1; i <= amount; i++)
		{
			String root = "Spawns.";
			String xS = getConfig().getString(root+i);
			String xM = xS.substring(xS.indexOf("x",0)+2, xS.indexOf("y",0)-1);

			String yS = getConfig().getString(root+i);
			String yM = yS.substring(yS.indexOf("y",0)+2, yS.indexOf("z",0)-1);
			
			String zS = getConfig().getString(root+i);
			String zM = zS.substring(zS.indexOf("z",0)+2, zS.indexOf("z",0)+8);
			
			float x = Float.parseFloat(xM);
			float y = Float.parseFloat(yM);
			float z = Float.parseFloat(zM);
			
/*
			getServer().getLogger().info("xS" + xS + "yS" + yS + "zS" + zS);
			getServer().getLogger().info("xM" + xM + "yM" + yM + "zM" + zM);
			getServer().getLogger().info("x" + x + "y" + y + "z" + z);
*/
			Location loc = new Location(senderPlayer.getWorld(),x,y,z);
			
			locationList.add(loc);
		}
			
		return locationList;
	}

	private void SetSpawnpoint(CommandSender sender, String[] args) 
	{
		Player p = (Player)sender;
		if(args[0].equalsIgnoreCase("setspawn") && args[1] != null)
		{
			int playerSpawn=0;
			try
			{
				playerSpawn = Integer.parseInt(args[1]);
			}
			catch(Exception ex)
			{
				sender.sendMessage(ChatColor.RED + "Das übergebene Argument konnte nicht als Ganzzahl ermittelt werden. Syntax: /bg setspawn [Spawn ID]");
			}
			
			Location location = p.getLocation();
			String root = "Spawns.";
			String locationXYZ = "x:" + location.getX() + "y:"+ location.getY() + "z:"+location.getZ();
			getConfig().set(root + playerSpawn, locationXYZ);
			saveConfig();
			sender.sendMessage(ChatColor.DARK_AQUA + "Der Spawnpunkt wurde erfolgreich erstellt!");
		}
	}

	private void ReloadPlugin(CommandSender sender, String[] args) 
	{
		if((args[0].equalsIgnoreCase("reload")))
		{
			this.reloadConfig();
			sender.sendMessage(ChatColor.DARK_AQUA + "Plugin wurde reloaded!");
		}
	}

	private void AddPlayers(CommandSender sender, String[] args) 
	{
		if(args[0].equalsIgnoreCase("addplayers"))
		{
			sender.sendMessage(ChatColor.DARK_AQUA + "Füge alle Spieler des Servers hinzu!");
			List<String> Players = new ArrayList<String>();
			for(Player p : srv.getOnlinePlayers())
			{
				Players.add(p.getUniqueId().toString());
				SetPlayerVariables(p);
				srv.broadcastMessage(ChatColor.GOLD + p.getDisplayName() + ChatColor.DARK_AQUA + " wurde hinzugefügt.");
				String pid = "Players.BGPlayers";
				getConfig().set(pid,Players);
				saveConfig();
				
			}
		}
	}

	private void SetPlayerVariables(Player p) {
		String playerId = "Players." + p.getPlayer().getUniqueId().toString();
		getConfig().set(playerId + ".Nickname",p.getDisplayName());
		getConfig().set(playerId + ".Dead",false);
		getConfig().set(playerId + ".Kills",0);
		getConfig().set(playerId + ".KilledBy","");
		getConfig().set(playerId + ".JPTaskId",0);
		getConfig().set(playerId + ".JPCanceled",false);
		getConfig().set(playerId + ".JPInvincible",20);
		getConfig().set(playerId + ".IsBGPlayer", true);
		saveConfig();
	}
    
    public String readConfigFile()
    {
        String content = null;
        File file = new File(getDataFolder() + "config.yml");
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return content;
    }

	private void StartGame(int countdown) 
	{
		SecGlob = countdown;
		getLogger().info(":Entry::" + SecGlob);
		
		taskId = srv.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				getLogger().info(":Pre:Prüfung:" + SecGlob);
				if(SecGlob>5)
				{
					for(Player p : srv.getOnlinePlayers())
					{
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 5*20,10));
						p.sendMessage(ChatColor.DARK_RED + "BATTLEGROUNDS startet in " + ChatColor.GOLD + SecGlob + ChatColor.RED +" Sekunden !");
						
					}
					getLogger().info(":Abzug:" + SecGlob);
					SecGlob-=5;
					
				}
				else
				{
					getLogger().info(":CANCEL:" + SecGlob + "tid:" + taskId);
					srv.getScheduler().cancelTask(taskId);
					getLogger().info(":CANCEL2:" + SecGlob + "tid:" + taskId);
				}
				
    		}
    	},0, 5*20);
		
		getLogger().info(":2ter:Sched:" + SecGlob);
		
		if(SecGlob<=10)
		{
			getLogger().info(":2ter:Sched:IN:" + SecGlob);
			taskId = srv.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				public void run()
				{
					getLogger().info(":2ter:Sched:IN2:" + SecGlob);
					if(SecGlob<=10)
					{
						for(Player p : srv.getOnlinePlayers())
						{
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 5*20,10));
							p.sendMessage(ChatColor.DARK_RED + "BATTLEGROUNDS startet in " + ChatColor.GOLD + SecGlob + ChatColor.RED +" Sekunden!!!");
						}
						SecGlob--;
					}
					else
					{					
						for(Player p : srv.getOnlinePlayers())
						{
							p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH,10,1);
							p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
							String playerId = "Players." + p.getPlayer().getUniqueId().toString();
							getConfig().set(playerId + ".JPCanceled",true);
							saveConfig();
							srv.getScheduler().cancelTask(taskId);
						}
						srv.broadcastMessage(ChatColor.DARK_RED + "BATTLEGROUNDS startet JETZT!");
						srv.broadcastMessage(ChatColor.DARK_AQUA + "Du bist jetzt angreifbar!");
						srv.getScheduler().cancelTask(taskId);
					}
		    	}
		    },0, 20);
	}
}
	
	@Override 
	public void onDisable(){

        this.getLogger().info("Battlegrounds wurde deaktiviert.");
	}
	
}
