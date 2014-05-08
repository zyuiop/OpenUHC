package me.zyuiop.uhplugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class UHEvents implements Listener {

	private UHPlugin pl = null;
	
	public UHEvents(UHPlugin uhPlugin) {
		pl = uhPlugin;
	}
	
	@EventHandler
	public void joinEvent(PlayerLoginEvent e) {
		if (!pl.canJoin() && !e.getPlayer().hasPermission("uhpl.join") && !pl.isIngame(e.getPlayer().getName())) {
			e.disallow(Result.KICK_OTHER, "Le jeu est dèjà lancé.");
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage("Le jeu est dèjà lancé.");
			return;
		}
		
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(pl.getSb());
		if (pl.isIngame(e.getPlayer().getName())) {
			e.getPlayer().sendMessage(ChatColor.GOLD+"Re !");
			Location l = e.getPlayer().getLocation();
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				e.getPlayer().teleport(pl.getRandLoc());
				e.getPlayer().sendMessage(ChatColor.DARK_RED+"Vous êtes HORS LIMITES.");
			}
		}
	}
	
	@EventHandler 
	public void onPing(ServerListPingEvent e) {
		if (pl.canJoin()) {
			e.setMotd(ChatColor.GREEN+"En attente de joueurs.");
		} else if (pl.isFinished()) {
			if (pl.isSolo()) {
				e.setMotd(ChatColor.GREEN+"Partie gagnée par "+ChatColor.AQUA+pl.getWinner());
			} else {
				e.setMotd(ChatColor.GREEN+"Partie gagnée par l'équipe "+pl.getTeamColor(pl.getWinner())+pl.getWinner());
			}
		} else {
			e.setMotd(ChatColor.RED+"Partie en cours");
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Location l = e.getTo();
		if (pl.isIngame(e.getPlayer().getName())) {
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				Location p = e.getFrom();
				if (!pl.isInLimits(p.getBlockX(), p.getBlockZ())) {
					e.getPlayer().teleport(pl.getRandLoc());
					return;
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent death) {
			Player p = (Player) death.getEntity();
			String n = p.getName();
			pl.deletePlayer(n);
			p.kickPlayer("Vous êtes mort !");
		
		
	}
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		e.setCancelled(!pl.getStarted());
	}
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent e) {
		e.setCancelled(!pl.getStarted());
	}
	@EventHandler
	public void takeDamage(EntityDamageEvent e) {
		if (!pl.getStarted())
			e.setCancelled(true);
		else {
			if (e.getEntity() instanceof Player && !pl.canTakeDamage()) {
				e.setCancelled(true);
			}
			if (e instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
				if (ev.getDamager() instanceof Player && ev.getEntity() instanceof Player && !pl.canPvP())
					e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent ev) {
		try {
			if (ev.getRecipe() instanceof ShapedRecipe) {
				ev.setCancelled(true);
			}
			if (ev.isCancelled()) ((Player) ev.getWhoClicked()).sendMessage(ChatColor.RED+"Vous ne pouvez pas crafter le melon comme ceci");
			
		} catch (Exception e) {
			Bukkit.getLogger().warning(ChatColor.RED+"Erreur dans le craft");
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent ev) {
		if (ev.getEntity() instanceof Ghast) {
			List<ItemStack> drops = new ArrayList<ItemStack>(ev.getDrops());
			ev.getDrops().clear();
			for (ItemStack i : drops) {
				if (i.getType() == Material.GHAST_TEAR) {
					ev.getDrops().add(new ItemStack(Material.GOLD_INGOT,i.getAmount()));
				} else {
					ev.getDrops().add(i);
				}
			}
		}
	}
	
	
	/*****************************
	 * Blocks events             *
	 *****************************/
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e)
	{
		if (!pl.getStarted())
			e.setCancelled(true);
		if (!pl.isIngame(e.getPlayer().getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent e)
	{
		e.setCancelled(!pl.getStarted());
		if (!pl.isIngame(e.getPlayer().getName()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void blockSpread(BlockSpreadEvent e)
	{
		e.setCancelled(!pl.getStarted());
	}

	@EventHandler
	public void blockBurn(BlockBurnEvent e)
	{
		e.setCancelled(!pl.getStarted());
	}
	
}
