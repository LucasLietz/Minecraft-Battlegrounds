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

    public OnPlayerDeath(Battlegrounds instance, Server server, boolean isStarted) {
        plugin = instance;
        s=server;
        bgStarted = isStarted;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerDeathEvent event) {
    	if(bgStarted)
    	{
    		String victim = event.getEntity().getDisplayName();
        	Player victimObj = event.getEntity();
        	Player killerObj = event.getEntity().getKiller();
        	
        	String killer = "";
        	
        	if(killerObj != null){
        		killer = killerObj.getDisplayName();
        	}else{
        		killer = "";
        	}
        	if(!killer.isEmpty()){
        		event.setDeathMessage(ChatColor.GOLD + victim + ChatColor.DARK_AQUA + " wurde von " +ChatColor.GOLD + killer + ChatColor.DARK_AQUA + " getötet!");
        		s.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
        			public void run(){
        				victimObj.kickPlayer(ChatColor.RED + "Du bist gestorben." + ChatColor.RED + " Damit bist du aus Battlegrounds ausgeschieden!");
        			}
        		},5*20);
        		
        	}else{
        		event.setDeathMessage(ChatColor.GOLD + victim + ChatColor.DARK_AQUA + " wurde getötet!");
        		s.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
        			public void run(){
        				victimObj.kickPlayer(ChatColor.RED + "Du bist gestorben." + ChatColor.RED + " Damit bist du aus Battlegrounds ausgeschieden!");
        			}
        		},5*20);
        	}
    	}
    	
    }

}
