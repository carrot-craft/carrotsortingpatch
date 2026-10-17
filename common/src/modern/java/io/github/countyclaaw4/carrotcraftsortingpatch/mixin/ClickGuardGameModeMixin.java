package io.github.countyclaaw4.carrotcraftsortingpatch.mixin;

import io.github.countyclaaw4.carrotcraftsortingpatch.CarrotSortingPatch;
import io.github.countyclaaw4.carrotcraftsortingpatch.condition.ServerCondition;
import io.github.countyclaaw4.carrotcraftsortingpatch.condition.VaultGuiDetector;
import io.github.countyclaaw4.carrotcraftsortingpatch.guard.ClickGuard;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * コンテナ画面のクリック送信の手前で、エンダーチェスト画面の保護スロット（45〜53）への
 * 「人の操作ではないクリック」を止める。ソート Mod の種類を問わない最後の守り。
 * 失敗した場合は止めずに通す（画面や通信を壊さない）。
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class ClickGuardGameModeMixin {

    @Inject(method = "handleContainerInput", at = @At("HEAD"), cancellable = true, require = 0)
    private void carrotSortingPatch$blockAutomatedClick(int containerId, int slotId, int buttonNum, ContainerInput input, Player player, CallbackInfo ci) {
        try {
            if (ClickGuard.isUserClick() || slotId < 0 || player == null) {
                return;
            }
            if (!ServerCondition.isTargetServer()) {
                return;
            }
            AbstractContainerMenu menu = player.containerMenu;
            if (menu == null || menu.containerId != containerId) {
                return;
            }
            int containerSlotIndex = VaultGuiDetector.getContainerSlotIndex(menu, slotId);
            if (!ClickGuard.shouldBlock(containerSlotIndex, false) || !VaultGuiDetector.isVaultGui(menu)) {
                return;
            }
            CarrotSortingPatch.LOGGER.warn("[CarrotSortingPatch] 保護スロットへの自動クリックを遮断しました: containerSlot={}, action={}",
                    containerSlotIndex, input);
            ci.cancel();
        } catch (Throwable t) {
            CarrotSortingPatch.LOGGER.error("[CarrotSortingPatch] クリック保護でエラーが発生しました（遮断せず通します）", t);
        }
    }
}
