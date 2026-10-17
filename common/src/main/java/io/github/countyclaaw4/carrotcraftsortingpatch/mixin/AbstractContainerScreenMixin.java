package io.github.countyclaaw4.carrotcraftsortingpatch.mixin;

import io.github.countyclaaw4.carrotcraftsortingpatch.CarrotSortingPatch;
import io.github.countyclaaw4.carrotcraftsortingpatch.condition.ServerCondition;
import io.github.countyclaaw4.carrotcraftsortingpatch.condition.VaultGuiDetector;
import io.github.countyclaaw4.carrotcraftsortingpatch.guard.ClickGuard;
import io.github.countyclaaw4.carrotcraftsortingpatch.interceptor.InterceptorRegistry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {

    @Shadow
    @Final
    protected T menu;

    @Unique
    private boolean carrotSortingPatch$detected = false;

    @Inject(method = "containerTick", at = @At("HEAD"), require = 0)
    private void carrotSortingPatch$onContainerTick(CallbackInfo ci) {
        try {
            // 例外などで「人のクリック」の出口を通らなかった場合に備えて、毎ティック戻す
            ClickGuard.reset();

            if (this.carrotSortingPatch$detected) {
                return;
            }
            if (!ServerCondition.isTargetServer()) {
                return;
            }
            if (VaultGuiDetector.isVaultGui(this.menu)) {
                this.carrotSortingPatch$detected = true;
                CarrotSortingPatch.LOGGER.info("[CarrotSortingPatch] CarrotCraft のエンダーチェスト画面を検出しました（保護スロット: 45〜53）");
                InterceptorRegistry.notifyVaultGuiDetected(this.menu);
            }
        } catch (Throwable t) {
            // 保護が効かなくなるだけで、画面は壊さない
            CarrotSortingPatch.LOGGER.error("[CarrotSortingPatch] 画面の判定でエラーが発生しました", t);
        }
    }

    @Inject(method = "onClose", at = @At("HEAD"), require = 0)
    private void carrotSortingPatch$onScreenClose(CallbackInfo ci) {
        this.carrotSortingPatch$detected = false;
        ClickGuard.reset();
    }
}
