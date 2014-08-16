package com.ngb.projectzulu.common.blocks.itemblockdeclarations;

import net.minecraft.block.Block;
import com.ngb.projectzulu.common.api.BlockList;
import com.ngb.projectzulu.common.api.ItemList;
import com.ngb.projectzulu.common.blocks.ItemFoodProjectZulu;
import com.ngb.projectzulu.common.core.itemblockdeclaration.ItemDeclaration;

import com.google.common.base.Optional;

import cpw.mods.fml.common.registry.GameRegistry;

public class WaterDropletDeclaration extends ItemDeclaration {

    public WaterDropletDeclaration() {
        super("WaterDroplet");
    }

    @Override
    protected boolean createItem() {
        ItemList.waterDroplets = Optional.of(new ItemFoodProjectZulu(1, 0.6f, false, name.toLowerCase()));
        return true;
    }

    @Override
    protected void registerItem() {
        GameRegistry.registerItem(ItemList.waterDroplets.get(), name);
    }
}
