package com.gmail.lynx7478.kits.base;
import com.gmail.lynx7478.anni.anniGame.AnniPlayer;
import com.gmail.lynx7478.anni.main.AnnihilationMain;
import com.gmail.lynx7478.anni.voting.ConfigManager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;


public abstract class AnniKit extends KitBase
{
	private int runnableID;
	private ItemStack specialItem;
	private String specialItemName;
	protected Delays delays;
	
	@Override
	protected void setUp()
	{
		delays = Delays.getInstance();
		try {
			specialItem = specialItem();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(getDelayLength() > 0 && useDefaultChecking())
		{
			delays.createNewDelay(getInternalName(), new StandardItemUpdater(getSpecialItemName(),specialItem.getType(),new Function<ItemStack,Boolean>(){
				@Override
				public Boolean apply(ItemStack stack)
				{
					return isSpecialItem(stack);
				}}));
		}
		runnableID = Bukkit.getScheduler().scheduleSyncRepeatingTask(AnnihilationMain.getInstance(), new Runnable()
				{
			public void run()
			{
				AnniKit.this.passive();
			}
				}, 0L, 5L);
		onInitialize();
	}
	
	protected abstract void onInitialize();
	
	//getSpecialItem() has a guarantee that the special item name 
	//has a value
	protected abstract ItemStack specialItem() throws ClassNotFoundException;
	protected abstract String defaultSpecialItemName();
	protected abstract boolean isSpecialItem(ItemStack stack);
	protected abstract boolean performPrimaryAction(Player player, AnniPlayer p);
	protected abstract boolean performSecondaryAction(Player player, AnniPlayer p);
	protected abstract long getDelayLength();
	protected abstract boolean useDefaultChecking();
	protected abstract boolean passive();
	
	public ItemStack getSpecialItem()
	{
		return specialItem;
	}
	
	//This will be called before setUp
	@Override
	protected void loadKitStuff(ConfigurationSection section)
	{
		super.loadKitStuff(section);
		specialItemName = section.getString("SpecialItemName");
	}
	
	@Override
	protected int setDefaults(ConfigurationSection section)
	{
		//section.set("SpecialItemName", defaultSpecialItemName());
		return ConfigManager.setDefaultIfNotSet(section, "SpecialItemName", defaultSpecialItemName());
	}
	
	public String getSpecialItemName()
	{
		return specialItemName;
	}
	
//	public void giveSpecialItem(Player player)
//	{
//		if(player != null)
//			player.getInventory().addItem(specialItem.clone());
//	}
	
//	public ItemStack getSpecialItem()
//	{
//		return this.specialItem.clone();
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void specialItemActionCheck(final PlayerInteractEvent event)
	{
		if(useDefaultChecking())
		{
			if(event.getItem() != null && event.getItem().getType() == specialItem.getType())
			{
				AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
				if(p != null && p.getKit().equals(this) && isSpecialItem(event.getItem()))
				{
					event.setCancelled(true);
					if(!delays.hasActiveDelay(event.getPlayer(), getInternalName()))
					{
						if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
						{
							if(performPrimaryAction(event.getPlayer(),p) && getDelayLength() > 0)
								delays.addDelay(event.getPlayer(), System.currentTimeMillis()+getDelayLength(), getInternalName());
							return;
						}
						
						if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
						{
							if(performSecondaryAction(event.getPlayer(),p) && getDelayLength() > 0)
								delays.addDelay(event.getPlayer(), System.currentTimeMillis()+getDelayLength(), getInternalName());
							return;
						}
					}
				}
			}
		}
	}
	
}
