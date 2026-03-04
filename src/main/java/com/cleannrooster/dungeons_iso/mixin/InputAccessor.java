package com.cleannrooster.dungeons_iso.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.Vec2f;

@Mixin(Input.class)
public interface InputAccessor {
    @Accessor("movementVector")
    void setMovementVector(Vec2f vec);
}
