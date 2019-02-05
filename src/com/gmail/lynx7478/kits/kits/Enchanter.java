package com.gmail.lynx7478.kits.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gmail.lynx7478.anni.anniEvents.ResourceBreakEvent;
import com.gmail.lynx7478.anni.kits.Loadout;
import com.gmail.lynx7478.anni.utils.VersionUtils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.gmail.lynx7478.kits.base.KitBase;

public class Enchanter extends KitBase
{	
	private Random rand;
	
	@Override
	protected void setUp()
	{
		rand = new Random(System.currentTimeMillis());
	}

	@Override
	protected String getInternalName()
	{
		return "Enchanter";
	}

	@Override
	protected ItemStack getIcon() throws ClassNotFoundException
	{
		if(!VersionUtils.getVersion().contains("13"))
		return new ItemStack(Material.EXP_BOTTLE);
		else
			return new ItemStack((Material) Enum.valueOf((Class<Enum>) Class.forName("org.bukkit.Material"), "EXPERIENCE_BOTTLE"));
	}
	
	@Override
	protected int setDefaults(ConfigurationSection section)
	{
		return 0;
	}

	@Override
	protected List<String> getDefaultDescription()
	{
		List<String> l = new ArrayList<String>();
		addToList(l,
					aqua+"Gain extra exp when gathering",
					aqua+"resources which enables",
					aqua+"quicker level succession.",
					aqua+"",
					aqua+"There is a small chance",
					aqua+"to obtain experience bottles",
					aqua+"when mining ores and chopping",
					aqua+"wood."
				);
		return l;
	}


	@Override
	public void cleanup(Player arg0)
	{
		
	}
	
	//Increase the xp gained from mining blocks and potentially gives you an XP bottle (1% chance)
	@EventHandler
	public void onResourceBreak(ResourceBreakEvent event) throws ClassNotFoundException
	{
		if(event.getPlayer().getKit().equals(this))
		{
			int xp = event.getXP();
			//Bukkit.getLogger().info("Inital XP: "+xp);
			if(xp > 0)
			{
				//I guess this needs to be verified to actuall give you more XP
				xp = (int)Math.ceil(xp*2);
				event.setXP(xp);
				//Bukkit.getLogger().info("New XP: "+xp);
				if(rand.nextInt(100) == 4)
				{
					Player pl = event.getPlayer().getPlayer();
					if(pl != null)
					{
						Material mat = null;
						if(!VersionUtils.getVersion().contains("13"))
						{
							mat = Material.EXP_BOTTLE;
						}else
						{
							mat = (Material) Enum.valueOf((Class<Enum>) Class.forName("org.bukkit.Material"), "EXPERIENCE_BOTTLE");
						}
						
						if(mat != null)
							pl.getInventory().addItem(new ItemStack(mat));
					}
				}
			}
		}
	}

//	@Override
//	public IconPackage getIconPackage()
//	{
//		return new IconPackage(new ItemStack(Material.EXP_BOTTLE), 
//				new String[]{
//								aqua+"Gain extra exp when gathering",
//								aqua+"resources which enables",
//								aqua+"quicker level succession.",
//								aqua+"",
//								aqua+"There is a small chance",
//								aqua+"to obtain experience bottles",
//								aqua+"when mining ores and chopping",
//								aqua+"wood.",
//							});
//	}
//
//	@Override
//	public String getName()
//	{
//		return "Enchanter";
//	}

//	@Override
//	public void onPlayerSpawn(Player player)
//	{
//		KitUtils.giveTeamArmor(player);
//		player.getInventory().addItem(KitUtils.getGoldSword());
//		player.getInventory().addItem(KitUtils.getWoodPick());
//		player.getInventory().addItem(KitUtils.getWoodAxe());
//		player.getInventory().addItem(KitUtils.getNavCompass());
//	}
	
	@Override
	protected Loadout getFinalLoadout()
	{
		return new Loadout().addGoldSword().addWoodPick().addWoodAxe();
	}
}
