package Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnBlockBreak implements Listener
{
    private Battlegrounds plugin;
    private boolean bgStarted;
	
    public OnBlockBreak(Battlegrounds instance) {
        plugin = instance;
    }

	 @EventHandler(priority=EventPriority.HIGHEST)
	    public void onBlockBreak(BlockBreakEvent bbe)
	    {	
	    	bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
	    	if(plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS") == true)
	    	{
	        	String playerNick = "Players." + bbe.getPlayer().getUniqueId().toString();
	        	boolean canceled = plugin.getConfig().getBoolean(playerNick + ".JPCanceled");
	        	if(bgStarted && ! canceled)
	        	{
	        		bbe.setCancelled(true);
	        	}
	    	}
	    	else
	    	{
	    		bbe.setCancelled(true);
	    	}
	    }

}
