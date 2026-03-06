package com.cleannrooster.dungeons_iso.compat;

import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface SodiumWorldRendererAccessor {
    RenderSectionManager sectionManager();

    @Invoker("sodium$setMatrices")
    void callSetMatrices(ChunkRenderMatrices matrices);
}