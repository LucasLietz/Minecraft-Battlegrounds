package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnPlayerDeath implements Listener{
	
    private Battlegrounds plugin;
    private Server s;
    private boolean bgStarted;
    private int lastPlayerCount=0;

    public OnPlayerDeath(Battlegrounds instance, Server server) {
        plugin = instance;
        s=server;
        bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) 
    {
    	bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
    	if(bgStarted)
    	{
    		String victim = event.getEntity().getDisplayName();
        	Player victimObj = event.getEntity();
        	Player killerObj = event.getEntity().getKiller();

        	String killedPlayerUUID = victimObj.getUniqueId().toString();
        	if(killerObj != null)
        	{
	        	String killerUUID = killerObj.getUniqueId().toString();
	        	String killerDisplayName = killerObj.getDisplayName() != null ? killerObj.getDisplayName() : victimObj.getLastDamageCause().toString();
	        	int killerTotalKills = plugin.getConfig().getInt("Players." + killerUUID  + ".Kills");
				plugin.getConfig().set("Players." + killedPlayerUUID + ".KilledBy",killerDisplayName);
	        	plugin.getConfig().set("Players." + killerUUID + ".Kills",killerTotalKills+1);
        	}
        	else
        	{				
        		plugin.getConfig().set("Players." + killedPlayerUUID  + ".KilledBy","");
        	}

			plugin.getConfig().set("Players." + killedPlayerUUID  + ".Dead",true);
			

			plugin.saveConfig();
        	
        	String killer = "";
        	
        	if(killerObj != null)
        	{
        		killer = killerObj.getDisplayName();
        	}

        	if(!killer.isEmpty())
        	{
        		event.setDeathMessage(ChatColor.GOLD + victim + ChatColor.DARK_AQUA + " wurde von " + ChatColor.GOLD + killer + ChatColor.DARK_AQUA + " getötet!");
        		s.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        		{
        			public void run(){
        				victimObj.kickPlayer(ChatColor.RED + "Du bist gestorben." + ChatColor.RED + " Damit bist du aus Battlegrounds ausgeschieden!");
        			}
        		},5*20);
        		
        	}
        	else
        	{
        		event.setDeathMessage(ChatColor.GOLD + victim + ChatColor.DARK_AQUA + " wurde getötet!");
        		s.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
        			public void run(){
        				victimObj.kickPlayer(ChatColor.RED + "Du bist gestorben." + ChatColor.RED + " Damit bist du aus Battlegrounds ausgeschieden!");
        			}
        		},5*20);
        	}
        	
        	lastPlayerCount=0;
        	
        	for(OfflinePlayer op : s.getOfflinePlayers())
        	{
        		
        		String uuidPlayer = op.getUniqueId().toString();
        		boolean isDead = plugin.getConfig().getBoolean("Player." + uuidPlayer + ".Dead");
        		if(isDead) lastPlayerCount++;
        	}
        	int players = s.getOfflinePlayers().length;
        	if(lastPlayerCount==players-1)
        	{
        		if(event.getEntity().getKiller() != null)
        		{
            		event.getEntity().getKiller().sendMessage(ChatColor.GOLD + "Du hast Battlegrounds gewonnen! Herzlichen Gllückwunsch!");
            		Location playerLoc = event.getEntity().getKiller().getLocation();
            		event.getEntity().getKiller().playSound(playerLoc, Sound.FIREWORK_LAUNCH,10,1);
        		}
        		else
        		{
        			for(Player p : s.getOnlinePlayers())
        			{
        				p.sendMessage(ChatColor.GOLD + "Du hast Battlegrounds gewonnen! Herzlichen Gllückwunsch!");
                		Location playerLoc = event.getEntity().getKiller().getLocation();
                		p.playSound(playerLoc, Sound.FIREWORK_LAUNCH,10,1);
        			}
        		}

        	}

			
    	}
    	
    }

}
