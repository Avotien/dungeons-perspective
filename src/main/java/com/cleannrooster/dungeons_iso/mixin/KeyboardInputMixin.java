package com.cleannrooster.dungeons_iso.mixin;

import com.cleannrooster.dungeons_iso.ClientInit;
import com.cleannrooster.dungeons_iso.api.MinecraftClientAccessor;
import com.cleannrooster.dungeons_iso.compat.MidnightControlsCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.cleannrooster.dungeons_iso.config.Config;
import com.cleannrooster.dungeons_iso.mod.Mod;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(
            method = "tick", at = @At("TAIL")
    )
    private void movementXIV(CallbackInfo ci) {
        Vector2f movement = new Vector2f(this.movementVector.y, this.movementVector.x);
        if (Mod.enabled) {
            if (FabricLoader.getInstance().isModLoaded("midnightcontrols") && Mod.noMouse && Mod.useTimer > 40) {
                if (MidnightControlsCompat.isEnabled()) {
                    return;
                }
            }
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;

            float tickDelta = client.gameRenderer.getCamera().getLastTickProgress();
            boolean bool = ((MinecraftClientAccessor)client).getLocation() instanceof EntityHitResult result && result.getEntity() instanceof ItemEntity;
            float yaw = client.gameRenderer.getCamera().getYaw() - client.player.getYaw(tickDelta);

            if ((Config.GSON.instance().clickToMove || bool)
                    && ((MinecraftClientAccessor)client).getOriginalLocation() != null
                    && ((MinecraftClientAccessor)client).getLocation() != null
                    && ((MinecraftClientAccessor)client).getLocation().getPos() instanceof Vec3d vec3d
                    && client.player.squaredDistanceTo(((MinecraftClientAccessor)client).getOriginalLocation()) < ((MinecraftClientAccessor)client).getOriginalLocation().squaredDistanceTo(((MinecraftClientAccessor)client).getLocation().getPos()) - 1) {

                if (((MinecraftClientAccessor)client).getLocation() instanceof EntityHitResult
                        && ((MinecraftClientAccessor)client).getLocation().getPos().subtract(0, ((MinecraftClientAccessor)client).getLocation().getPos().getY() - (client.player.getEntityPos()).getY(), 0)
                        .squaredDistanceTo(client.player.getEntityPos()) < (bool ? client.player.getWidth() / 2 : (client.player.getEntityInteractionRange() * client.player.getEntityInteractionRange() / 4))) {
                    return;
                }

                if (((MinecraftClientAccessor)client).getLocation() instanceof BlockHitResult result && Mod.isInteractable(result)) {
                    Hand[] var1 = Hand.values();
                    for (Hand hand : var1) {
                        var interact = client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, result);
                        if (interact.isAccepted()) {
                            if (interact == ActionResult.SUCCESS) {
                                client.player.swingHand(hand);
                            }
                            Mod.crosshairTarget = null;
                            ((MinecraftClientAccessor)client).setLocation(null);
                            ((MinecraftClientAccessor)client).setOriginalLocation(null);
                            ClientInit.clickToMove.setPressed(false);
                            return;
                        }
                    }
                }

                movement = new Vector2f(1.0F, 0F);
                yaw = getAngle(new Vec3d(0, 0, 0), vec3d.subtract(client.player.getEntityPos()).subtract(0, vec3d.subtract(client.player.getEntityPos()).getY(), 0));
                movement.mul(new Matrix2f().rotate((float) Math.toRadians(yaw)));
                movement.mul(new Matrix2f().rotate((float) Math.toRadians(+client.player.getYaw(tickDelta))));
                Mod.unModMovement = new Vector2f(1.0F, 0F).mul(new Matrix2f().rotate((float) Math.toRadians(+MinecraftClient.getInstance().gameRenderer.getCamera().getYaw())));
                this.movementVector = new Vec2f(movement.y, movement.x);
                return;
            } else {
                this.movementVector = Vec2f.ZERO;
            }

            if (Config.GSON.instance().cameraRelative) {
                Mod.relativeYaw = yaw;
                movement.mul(new Matrix2f().rotate((float) Math.toRadians(-yaw)));
            }

            this.movementVector = new Vec2f(movement.y, movement.x);
        }

        if (this.playerInput.backward() || this.playerInput.forward() || this.playerInput.left() || this.playerInput.right()) {
            Mod.useTimer = 0;
        } else {
            Mod.useTimer++;
        }
    }
    public float getAngle(Vec3d start, Vec3d target) {
        return (float) Math.toDegrees(Math.atan2(target.x - start.x, target.z - start.z));
    }

}
