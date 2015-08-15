package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnPlayerQuit implements Listener
{
	
    private Battlegrounds plugin;
    public OnPlayerQuit(Battlegrounds instance) 
    {
        plugin = instance;
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) 
    {
    	String playerId = "Players." + event.getPlayer().getUniqueId().toString();
    	boolean joinPlayer = plugin.getConfig().getBoolean(playerId + ".Dead");
    	if(joinPlayer)
    	{
    		event.setQuitMessage("");
    	}
    	else
    	{
    		String playerNick = event.getPlayer().getDisplayName();
    		event.setQuitMessage(ChatColor.GOLD + playerNick + ChatColor.DARK_AQUA + " hat den Server verlassen!");
    	}
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) 
    {
		event.setLeaveMessage("");
    }

}
