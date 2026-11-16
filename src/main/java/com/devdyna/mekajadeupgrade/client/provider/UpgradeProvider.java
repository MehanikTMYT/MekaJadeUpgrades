package com.devdyna.mekajadeupgrade.client.provider;

import java.util.ArrayList;
import java.util.List;
import com.devdyna.mekajadeupgrade.Main;

import mekanism.common.block.prefab.BlockTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.*;
import mekanism.api.Upgrade;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registries.MekanismItems;

/*
 * 26.1.2 port notes (vs upstream 1.3-21.1):
 * - Jade 26.x replaced IElementHelper/IElement with JadeUI/Element and the
 *   layout-based tooltip pipeline; item/text elements are now built via
 *   JadeUI.item(...)/JadeUI.text(...) with size()/offset().
 * - Mekanism 10.8.0 removed TileComponentUpgrade#isUpgradeInstalled;
 *   installed upgrades are detected via getUpgrades(upgrade) > 0.
 * - Optional mekextras / mekanism_empowered integrations from upstream are
 *   dropped (no 26.1.2 ports in the fleet); see PORT_REPORT.md.
 */
@SuppressWarnings("null")
public enum UpgradeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();
        List<Element> elements = new ArrayList<>();

        for (Upgrade upgrade : Upgrade.values()) {
            if (tag.contains(upgrade.getSerializedName())) {
                int count = tag.getInt(upgrade.getSerializedName()).orElse(0);

                elements.add(item(upgrade));
                elements.add(countText(count));
            }
        }

        tooltip.add(elements);
    }

    public Element item(Upgrade upgrade) {
        try {

            return JadeUI.item(new ItemStack(getUpgrade(upgrade).get()), 0.55f)
                    .size(10, 10)
                    .offset(0, -2)
                    .narration((String) null);

        } catch (NullPointerException e) {
            // catch eventual crashes with a dummy item render
            return JadeUI.item(new ItemStack(Items.BARRIER), 0.55f)
                    .size(10, 10)
                    .offset(0, -2)
                    .narration((String) null);
        }
    }

    public Element countText(int count) {
        return JadeUI.text(Component.literal("x" + count))
                .size(5, 5)
                .offset(0, -1)
                .narration((String) null);
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {

        if (accessor.getBlockEntity() instanceof TileEntityMekanism be) {
            TileComponentUpgrade component = be.getComponent();

            if (component != null) {
                for (Upgrade upgrade : Upgrade.values()) {
                    int count = component.getUpgrades(upgrade);
                    if (count > 0)
                        data.putInt(upgrade.getSerializedName(), count);
                }
            }
        }

    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(Main.MODID, "upgrades");
    }

    public ItemRegistryObject<ItemUpgrade> getUpgrade(Upgrade upgrade) {

        if (Upgrade.SPEED.equals(upgrade))
            return MekanismItems.SPEED_UPGRADE;
        if (Upgrade.ENERGY.equals(upgrade))
            return MekanismItems.ENERGY_UPGRADE;
        if (Upgrade.CHEMICAL.equals(upgrade))
            return MekanismItems.CHEMICAL_UPGRADE;
        if (Upgrade.ANCHOR.equals(upgrade))
            return MekanismItems.ANCHOR_UPGRADE;
        if (Upgrade.FILTER.equals(upgrade))
            return MekanismItems.FILTER_UPGRADE;

        if (Upgrade.MUFFLING.equals(upgrade))
            return MekanismItems.MUFFLING_UPGRADE;

        if (Upgrade.STONE_GENERATOR.equals(upgrade))
            return MekanismItems.STONE_GENERATOR_UPGRADE;

        return null;

    }

}
