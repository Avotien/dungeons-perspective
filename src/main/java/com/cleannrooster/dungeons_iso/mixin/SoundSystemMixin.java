package com.cleannrooster.dungeons_iso.mixin;

import com.cleannrooster.dungeons_iso.mod.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundListener;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.sound.SoundExecutor;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Shadow
    private SoundListener listener;
    @Shadow
    private SoundExecutor taskQueue;

    @Inject(method = "updateListenerPosition", at = @At("HEAD"), cancellable = true)
    private void usePlayerPosition(Camera camera, CallbackInfo ci) {
        if (Mod.enabled && MinecraftClient.getInstance().player != null) {
            SoundListenerTransform transform = new SoundListenerTransform(
                MinecraftClient.getInstance().player.getEyePos(),
                new Vec3d(camera.getHorizontalPlane()),
                new Vec3d(camera.getVerticalPlane())
            );
            this.taskQueue.execute(() -> this.listener.setTransform(transform));
            ci.cancel();
        }
    }
}