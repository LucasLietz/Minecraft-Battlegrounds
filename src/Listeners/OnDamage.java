package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnDamage implements Listener
{
	
    private Battlegrounds plugin;
    private boolean bgStarted;
	
    public OnDamage(Battlegrounds instance) 
    {
        plugin = instance;
    }
	
	@EventHandler(priority=EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent damage)
    {    	
    	
    	String playerNick = "Players." + damage.getEntity().getUniqueId().toString();
    	boolean canceled = plugin.getConfig().getBoolean(playerNick + ".JPCanceled");
    	Entity player = damage.getEntity();
    	bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
    	
    	if(!bgStarted)
    	{
	    	if(player.getType() == EntityType.PLAYER)
	    	{
	        	damage.setCancelled(true);
	        	player.sendMessage(ChatColor.RED + "Die Schutzzeit läuft noch! Du kannst nicht angreifen oder angegriffen werden!");
	    	}
    	}
    	else if(bgStarted &&  !canceled)
        	{
            	if(player.getType() == EntityType.PLAYER)
            	{
                	damage.setCancelled(true);
                	player.sendMessage(ChatColor.RED + "Die Schutzzeit läuft noch! Du kannst nicht angreifen oder angegriffen werden!");
            	}
        	}
        else if(bgStarted && canceled)
        	{
        		damage.setCancelled(false);
        	}
    }

}
