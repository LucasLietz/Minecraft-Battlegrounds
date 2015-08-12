package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Server;
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

    public OnPlayerDeath(Battlegrounds instance, Server server) {
        plugin = instance;
        s=server;
        bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerDeathEvent event) {
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
	        	int killerTotalKills = plugin.getConfig().getInt("Players." + killerUUID + "("+killerDisplayName+")" + ".Kills");
				plugin.getConfig().set("Players." + killedPlayerUUID + "("+victimObj.getDisplayName()+")" + ".KilledBy",killerDisplayName);
	        	plugin.getConfig().set("Players." + killerUUID + "("+killerDisplayName+")" + ".Kills",killerTotalKills+1);
        	}
        	else
        	{				
        		plugin.getConfig().set("Players." + killedPlayerUUID + "("+victimObj.getDisplayName()+")" + ".KilledBy","");
        	}
        	String victimDisplayName = victimObj.getDisplayName();

			plugin.getConfig().set("Players." + killedPlayerUUID + "("+victimDisplayName+")" + ".Dead",true);
			

			plugin.saveConfig();
        	
        	String killer = "";
        	
        	if(killerObj != null)
        	{
        		killer = killerObj.getDisplayName();
        	}

        	if(!killer.isEmpty())
        	{
        		event.setDeathMessage(ChatColor.GOLD + victim + ChatColor.DARK_AQUA + " wurde von " +ChatColor.GOLD + killer + ChatColor.DARK_AQUA + " get�tet!");
        		s.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        		{
        			public void run(){
        				victimObj.kickPlayer(ChatColor.RED + "Du bist gestorben." + ChatColor.RED + " Damit bist du aus Battlegrounds ausgeschieden!");
        			}
        		},5*20);
        		
        	}
        	else
        	{
        		event.setDeathMessage(ChatColor.GOLD + victim + ChatColor.DARK_AQUA + " wurde get�tet!");
        		s.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
        			public void run(){
        				victimObj.kickPlayer(ChatColor.RED + "Du bist gestorben." + ChatColor.RED + " Damit bist du aus Battlegrounds ausgeschieden!");
        			}
        		},5*20);
        	}

			
    	}
    	
    }

}
