package com.github.fate0608.Battlegrounds;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Battlegrounds extends JavaPlugin {
	
	private Server srv = this.getServer();
	private boolean isStarted;
	
	@Override
	public void onEnable(){
		
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerJoin(this,getServer(), isStarted),this);
		getServer().getPluginManager().registerEvents(new Listeners.OnPlayerDeath(this,getServer(), isStarted),this);
		
		initConfig();
		this.getLogger().info("Battlegrounds wurde aktiviert.");
	}

	private void initConfig() {
		this.reloadConfig();
		this.getConfig()
				.options()
				.header("#Willkommen bei Battlegrounds! Danke, dass du mein Plugin benutzt. Für Feedback, nutze bitte den Diskussionsthread auf der Spigot-Downloadpage. "
						+ "\n#Dieses Plugin basiert auf dem Spielmodus VARO, dessen geistlicher Inhaber ich nicht bin. Von mir stammt lediglich die Umsetzung in Java.");
		this.getConfig().addDefault("Battlegrounds.commands.STATUS", false);

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
				            		
			                    boolean isStarted = getConfig().getBoolean("Battlegrounds.commands.STATUS");
			                    
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
                            		srv.broadcastMessage(ChatColor.RED + "BATTLEGROUNDS startet in " + ChatColor.GOLD +  "30 Sekunden" + ChatColor.RED +" ! Bereitet euch vor!");
                            		saveConfig();
                            		StartGame();
                            	}
                            }
			            	else
			            	{
			                	if(!args[0].equals("status"))
			                	{
			                		sender.sendMessage(ChatColor.RED + "Du hast einen falschen Parameter übergeben!");
			                		return false;
			                	}else{
			                		sender.sendMessage(ChatColor.RED + "Du hast keine Rechte für diesen Befehl!");
			                		return false;	
			                	}
			            	}
                        }
                        return false;  
                    } 
                    return false;
        }
    	return false;
    }
    

    
	private void StartGame() {
		// TODO Auto-generated method stub
	}

	@Override 
	public void onDisable(){

        this.getLogger().info("Battlegrounds wurde deaktiviert.");
	}
	
}
