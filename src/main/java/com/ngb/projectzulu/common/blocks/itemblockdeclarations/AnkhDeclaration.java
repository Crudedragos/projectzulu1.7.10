package com.ngb.projectzulu.common.blocks.itemblockdeclarations;

import com.ngb.projectzulu.common.api.ItemList;
import com.ngb.projectzulu.common.blocks.ItemAnkh;
import com.ngb.projectzulu.common.core.itemblockdeclaration.ItemDeclaration;

import com.google.common.base.Optional;

import cpw.mods.fml.common.registry.GameRegistry;

public class AnkhDeclaration extends ItemDeclaration {

    public AnkhDeclaration() {
        super("Ankh");
    }

    @Override
    protected boolean createItem() {
        ItemList.ankh = Optional.of(new ItemAnkh(name));
        return true;
    }

    @Override
    protected void registerItem() {
        GameRegistry.registerItem(ItemList.ankh.get(), name);
    }
}
