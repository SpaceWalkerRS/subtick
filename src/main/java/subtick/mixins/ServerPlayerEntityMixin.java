package subtick.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import subtick.TickHandlers;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin
{
  // Needed to account for end -> overworld portal
  @Inject(method = "moveToWorld", at = @At("RETURN"))
  private void changeDimension(ServerWorld destination, CallbackInfoReturnable<Entity> cir)
  {
    TickHandlers.getHandler(destination.getRegistryKey()).updateFrozenStateToConnectedPlayer((ServerPlayerEntity)(Object)this);
  }

  // Needed to account for teleports
  @Inject(method = "worldChanged", at = @At("RETURN"))
  private void changeDimension2(ServerWorld origin, CallbackInfo ci)
  {
    TickHandlers.getHandler(((ServerPlayerEntity)(Object)this).world.getRegistryKey()).updateFrozenStateToConnectedPlayer((ServerPlayerEntity)(Object)this);
  }
}
