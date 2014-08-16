package com.ngb.projectzulu.common.blocks;

import net.minecraft.item.Item;
import com.ngb.projectzulu.common.ProjectZulu_Core;
import com.ngb.projectzulu.common.core.DefaultProps;

public class ItemCoconutItem extends Item {

    public ItemCoconutItem(boolean full3D, String name) {
        super();
        maxStackSize = 1;
        setMaxDamage(5);
        setCreativeTab(ProjectZulu_Core.projectZuluCreativeTab);
        bFull3D = full3D;
        setHasSubtypes(true);
        setUnlocalizedName(name);
        setTextureName(DefaultProps.blockKey + ":" + name);
    }
}
