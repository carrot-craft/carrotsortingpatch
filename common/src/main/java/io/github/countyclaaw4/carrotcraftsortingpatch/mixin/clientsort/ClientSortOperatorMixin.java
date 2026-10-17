package io.github.countyclaaw4.carrotcraftsortingpatch.mixin.clientsort;

import io.github.countyclaaw4.carrotcraftsortingpatch.CarrotSortingPatch;
import io.github.countyclaaw4.carrotcraftsortingpatch.condition.ServerCondition;
import io.github.countyclaaw4.carrotcraftsortingpatch.condition.VaultGuiDetector;
import io.github.countyclaaw4.carrotcraftsortingpatch.interceptor.InterceptorRegistry;
import io.github.countyclaaw4.carrotcraftsortingpatch.interceptor.clientsort.ClientSortInterceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * ClientSort の操作対象スロット一覧から、エンダーチェスト画面の保護スロットを外す。
 * ClientSort が入っていなければ（@Pseudo）何も起きない。
 */
@Pseudo
@Mixin(targets = "dev.terminalmc.clientsort.client.inventory.operator.SingleUseOperator", remap = false)
public class ClientSortOperatorMixin {

    @Inject(method = "collectSlots", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
    private void carrotSortingPatch$onCollectSlots(CallbackInfoReturnable<Slot[]> cir) {
        try {
            Slot[] originalSlots = cir.getReturnValue();
            if (originalSlots == null || originalSlots.length == 0) {
                return;
            }
            if (!ServerCondition.isTargetServer()) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.player == null) {
                return;
            }
            AbstractContainerMenu menu = mc.player.containerMenu;
            if (!VaultGuiDetector.isVaultGui(menu)) {
                return;
            }

            int[] containerSlotIndices = VaultGuiDetector.getContainerSlotIndices(menu, originalSlots);
            List<Slot> filtered = new ArrayList<>();
            for (int i = 0; i < originalSlots.length; i++) {
                if (InterceptorRegistry.shouldProtectSlot(ClientSortInterceptor.MOD_ID, containerSlotIndices[i])) {
                    continue;
                }
                filtered.add(originalSlots[i]);
            }

            if (filtered.size() != originalSlots.length) {
                CarrotSortingPatch.LOGGER.info("[CarrotSortingPatch] ClientSort の操作対象から保護スロットを除外しました ({} -> {} スロット)",
                        originalSlots.length, filtered.size());
                cir.setReturnValue(filtered.toArray(new Slot[0]));
            }
        } catch (Throwable t) {
            CarrotSortingPatch.LOGGER.error("[CarrotSortingPatch] ClientSort 連携でエラーが発生しました（保護なしで続行します）", t);
        }
    }
}
