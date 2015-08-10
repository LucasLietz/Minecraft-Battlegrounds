package Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.fate0608.Battlegrounds.JoinedPlayer;
import com.github.fate0608.Battlegrounds.Battlegrounds;

public class OnPlayerJoin implements Listener{
	
    private Battlegrounds plugin;
    private Server s;
    private JoinedPlayer _jp;
	private int invincibleTime = 20;
	private boolean bgStarted;

    public OnPlayerJoin(Battlegrounds instance, Server server, boolean isStarted) {
        plugin = instance;
        s=server;
        bgStarted = isStarted;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerJoinEvent event) {
    	
    	if(bgStarted)
    	{
	    	JoinedPlayer jp = new JoinedPlayer();
	    	jp.invincible = 20;
	    	jp.player = event.getPlayer();
	    	jp.taskId = 0;
	    	jp.canceled = false;
	
	    	event.setJoinMessage(ChatColor.GOLD + jp.player.getDisplayName() + ChatColor.DARK_AQUA + " hat den Server betreten. Er ist in " + invincibleTime + " Sekunden angreifbar!");  	
	    	
	    	startScheduler(jp);
    	}
    }
    
    public void startScheduler(JoinedPlayer jp){
    	jp.taskId = s.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				_jp=jp;
    			if(jp.invincible <= 0){
    				s.broadcastMessage(ChatColor.GOLD + jp.player.getDisplayName() + ChatColor.DARK_AQUA + " ist jetzt angreifbar!");
    				jp.canceled = true;
    				//s.broadcastMessage(jp.player.getDisplayName() + " schedulerId: " + jp.taskId + " wird beendet!" + " cancelstatus: " + jp.canceled );
    				cancelScheduler(jp);
    				_jp = jp;
    			}else{
    				s.broadcastMessage(ChatColor.GOLD + jp.player.getDisplayName() + ChatColor.DARK_AQUA + " ist in " + jp.invincible + " Sekunden angreifbar!");
    				//s.broadcastMessage(jp.player.getDisplayName() + " schedulerId: " + jp.taskId  + " cancelstatus: " + jp.canceled );
    			}

    			jp.invincible -=5;
    		}
    	}, 0, 5*20);
    }
    
    public void cancelScheduler(JoinedPlayer jp){
    	s.getScheduler().cancelTask(jp.taskId);	
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent bbe)
    {	
    	if(bgStarted && !_jp.canceled && _jp.player.getUniqueId() == bbe.getPlayer().getUniqueId()){
    		bbe.setCancelled(true);
    	}
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void move(PlayerMoveEvent move)
    {
    	if(bgStarted && !_jp.canceled && _jp.player.getUniqueId() == move.getPlayer().getUniqueId()){
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
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void move(EntityDamageEvent damage)
    {
    	if(bgStarted &&  !_jp.canceled && _jp.player.getUniqueId() == damage.getEntity().getUniqueId()){
        	Player player = (Player)damage.getEntity();
        	damage.setCancelled(true);
        	player.sendMessage(ChatColor.RED + "Die Schutzzeit läuft noch! Du kannst nicht angreifen oder angegriffen werden!");
    	}
    }
}
