/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_18_R2.block.impl;

public final class CraftCoralFanAbstract extends org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData implements org.bukkit.block.data.Waterlogged {

    public CraftCoralFanAbstract() {
        super();
    }

    public CraftCoralFanAbstract(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_18_R2.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.BaseCoralFanBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(CraftCoralFanAbstract.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(CraftCoralFanAbstract.WATERLOGGED, waterlogged);
    }
}
