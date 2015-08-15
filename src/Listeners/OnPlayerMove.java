package Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnPlayerMove implements Listener
{
    private Battlegrounds plugin;
    private boolean bgStarted;
	
    public OnPlayerMove(Battlegrounds instance) {
        plugin = instance;

    }
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void OnMove(PlayerMoveEvent move)
	    {
	    	bgStarted = plugin.getConfig().getBoolean("Battlegrounds.commands.STATUS");
	    	String playerNick = "Players." + move.getPlayer().getUniqueId().toString();
	    	boolean canceled = plugin.getConfig().getBoolean(playerNick + ".JPCanceled");
	    	if(bgStarted && ! canceled){
		        Location from=move.getFrom();
		        Location to=move.getTo();
		        double x=Math.floor(from.getX());
		        double z=Math.floor(from.getZ());
		        if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z)
		        {
		            x+=.5;
		            z+=.5;
		            move.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
		        }
			 }
	    }

}
