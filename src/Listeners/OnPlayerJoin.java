package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnPlayerJoin implements Listener{
	
    private Battlegrounds plugin;
    private Server s;
	private int invincibleTime = 20;
	private boolean bgStarted;
	
    public OnPlayerJoin(Battlegrounds instance, Server server) {
        plugin = instance;
        s=server;
        bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerJoinEvent event) 
    {
    		String playerId = "Players." + event.getPlayer().getUniqueId().toString();
    	  	boolean joinPlayer = plugin.getConfig().getBoolean(playerId + ".Dead");
    	  	bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
    	  	
    	  	if(!joinPlayer)
	    	{
				plugin.getConfig().set(playerId + ".Nickname",event.getPlayer().getDisplayName());
				plugin.getConfig().set(playerId + ".Dead",false);
				plugin.getConfig().set(playerId + ".Kills",0);
				plugin.getConfig().set(playerId + ".KilledBy","");
				plugin.getConfig().set(playerId + ".JPTaskId",0);
				plugin.getConfig().set(playerId + ".JPCanceled",false);
				plugin.getConfig().set(playerId + ".JPInvincible",20);
	    	  	plugin.saveConfig();
	    	

		    	if(bgStarted)
		    	{
			    	event.setJoinMessage(ChatColor.GOLD + event.getPlayer().getDisplayName() + ChatColor.DARK_AQUA + " hat den Server betreten. Er ist in " + invincibleTime + " Sekunden angreifbar!");  	
			    	startScheduler(event);
		    	}
		    	else
		    	{
		    		event.setJoinMessage(ChatColor.GOLD + event.getPlayer().getDisplayName() + ChatColor.DARK_AQUA + " hat den Server betreten.");  	
		    	}
	    	}
	    	else
	    	{
	    		event.setJoinMessage("");
	    		event.getPlayer().kickPlayer(ChatColor.RED + "Du kannst nicht mehr beitreten, da du bereits gestorben bist.");
	    	}
    }

    public void startScheduler(PlayerJoinEvent pje)
    {
		String playerNick = "Players." + pje.getPlayer().getUniqueId().toString();
		
		plugin.getConfig().set(playerNick  + ".JPTaskId", s.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				
				int invincible = plugin.getConfig().getInt(playerNick  + ".JPInvincible");
				
    			if(invincible <= 0)
    			{
    				s.broadcastMessage(ChatColor.GOLD + pje.getPlayer().getDisplayName() + ChatColor.DARK_AQUA + " ist jetzt angreifbar!");
    				plugin.getConfig().set(playerNick + ".JPCanceled",true);
    				plugin.saveConfig();
    				cancelScheduler(playerNick);
    			}
    			else
    			{
    				s.broadcastMessage(ChatColor.GOLD + pje.getPlayer().getDisplayName() + ChatColor.DARK_AQUA + " ist in " + invincible + " Sekunden angreifbar!");
    			}
    				
    			plugin.getConfig().set(playerNick + ".JPInvincible",invincible-5);
    			plugin.saveConfig();

    		}
    	}, 0, 5*20));
		plugin.saveConfig();
    }
    
    public void cancelScheduler(String pid){
    	
    	int taskId = plugin.getConfig().getInt(pid + ".JPTaskId");
    	s.getScheduler().cancelTask(taskId);	
    }
}
