package net.zyuiop.openUHC.events;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockEvents implements Listener {
	private OpenUHC pl;
	public BlockEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e)
	{
		if (!pl.getGame().getStarted()) {
			e.setCancelled(true);
			
		}
		if (!pl.isIngame(e.getPlayer()))
			e.setCancelled(true);
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent e)
	{
		e.setCancelled(!pl.getGame().getStarted());
		if (!pl.isIngame(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void blockSpread(BlockSpreadEvent e)
	{
		e.setCancelled(!pl.getGame().getStarted());
	}

	@EventHandler
	public void blockBurn(BlockBurnEvent e)
	{
		e.setCancelled(!pl.getGame().getStarted());
	}
}
