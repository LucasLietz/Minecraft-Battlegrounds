package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    		String playerId = "Players." + event.getPlayer().getUniqueId().toString() + "("+event.getPlayer().getDisplayName()+")";
    	  	boolean joinPlayer = plugin.getConfig().getBoolean(playerId + ".Dead");

    	  	plugin.getConfig().set(playerId + ".JPTaskId",0);
    	  	plugin.getConfig().set(playerId + ".JPInvincible",20);
    	  	plugin.getConfig().set(playerId + ".JPCanceled",false);
    	  	plugin.saveConfig();
	    	
	    	if(!joinPlayer)
	    	{
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
	    		event.getPlayer().kickPlayer(ChatColor.RED + "Du kannst nicht mehr beitreten, da du bereits gestorben bist.");
	    	}
    }
    
    public void startScheduler(PlayerJoinEvent pje)
    {
		String playerId = "Players." + pje.getPlayer().getUniqueId().toString() + "("+pje.getPlayer().getDisplayName()+")";
		
		plugin.getConfig().set(playerId + ".JPTaskId", s.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				
				int invincible = plugin.getConfig().getInt(playerId + ".JPInvincible");
				
    			if(invincible <= 0)
    			{
    				s.broadcastMessage(ChatColor.GOLD + pje.getPlayer().getDisplayName() + ChatColor.DARK_AQUA + " ist jetzt angreifbar!");
    				plugin.getConfig().set(playerId + ".JPCanceled",true);
    				plugin.saveConfig();
    				cancelScheduler(playerId);
    			}
    			else
    			{
    				s.broadcastMessage(ChatColor.GOLD + pje.getPlayer().getDisplayName() + ChatColor.DARK_AQUA + " ist in " + invincible + " Sekunden angreifbar!");
    			}
    				
    			plugin.getConfig().set(playerId + ".JPInvincible",invincible-5);
    			plugin.saveConfig();

    		}
    	}, 0, 5*20));
		plugin.saveConfig();
    }
    
    public void cancelScheduler(String pid){
    	
    	int taskId = plugin.getConfig().getInt(pid + ".JPTaskId");
    	s.getScheduler().cancelTask(taskId);	
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent bbe)
    {	
    	String playerId = "Players." + bbe.getPlayer().getUniqueId().toString() + "("+bbe.getPlayer().getDisplayName()+")";
    	boolean canceled = plugin.getConfig().getBoolean(playerId + ".JPCanceled");
    	if(bgStarted && ! canceled){
    		bbe.setCancelled(true);
    	}
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void move(PlayerMoveEvent move)
    {
    	String playerId = "Players." + move.getPlayer().getUniqueId().toString() + "("+move.getPlayer().getDisplayName()+")";
    	boolean canceled = plugin.getConfig().getBoolean(playerId + ".JPCanceled");
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
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void move(EntityDamageEvent damage)
    {
    	String playerId = "Players." + damage.getEntity().getUniqueId().toString() + "("+ damage.getEntity().getName()+")";
    	boolean canceled = plugin.getConfig().getBoolean(playerId + ".JPCanceled");
    	if(bgStarted &&  ! canceled){
        	Entity player = damage.getEntity();
        	damage.setCancelled(true);
        	player.sendMessage(ChatColor.RED + "Die Schutzzeit läuft noch! Du kannst nicht angreifen oder angegriffen werden!");
    	}
    }
}
