package com.ngb.projectzulu.common;

import com.ngb.projectzulu.common.blocks.EntityCreeperBlossomPrimed;
import com.ngb.projectzulu.common.blocks.RenderCreeperBlossomPrimed;
import com.ngb.projectzulu.common.core.CustomEntityManager;
import com.ngb.projectzulu.common.mobs.BossHealthDisplayTicker;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxyProjectZulu extends CommonProxyProjectZulu{ 	
	
	@Override
	public int addArmor(String armor){
		return RenderingRegistry.addNewArmourRendererPrefix(armor);
	}
	
	@Override
	public void bossHealthTicker(){
	    FMLCommonHandler.instance().bus().register(new BossHealthDisplayTicker());
	}
	
	@Override
	public void registerModelsAndRender() {
		CustomEntityManager.INSTANCE.registerModelsAndRender();
	}
}
