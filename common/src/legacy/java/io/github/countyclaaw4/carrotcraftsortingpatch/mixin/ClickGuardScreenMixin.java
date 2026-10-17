package io.github.countyclaaw4.carrotcraftsortingpatch.mixin;

import io.github.countyclaaw4.carrotcraftsortingpatch.guard.ClickGuard;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 人のクリック（マウス・キー）は必ず画面の slotClicked を通る。その間だけ「人の操作」と印を付ける。
 * 印が無いまま保護スロットへ来るクリック（ソート Mod などの自動操作）は ClickGuardGameModeMixin が止める。
 */
// 優先度を下げて（数字を大きくして）他の Mod の後に動かす。IPN や ClientSort が slotClicked の先頭で
// 処理を取り消した時に、こちらの「人の操作」の印だけが残らないようにするため。
@Mixin(value = AbstractContainerScreen.class, priority = 2000)
public abstract class ClickGuardScreenMixin {

    @Inject(method = "slotClicked", at = @At("HEAD"), require = 0)
    private void carrotSortingPatch$enterUserClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        ClickGuard.enterUserClick();
    }

    @Inject(method = "slotClicked", at = @At("RETURN"), require = 0)
    private void carrotSortingPatch$exitUserClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        ClickGuard.exitUserClick();
    }
}
