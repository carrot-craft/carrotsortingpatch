package io.github.countyclaaw4.carrotcraftsortingpatch.interceptor;

import io.github.countyclaaw4.carrotcraftsortingpatch.condition.SlotLayout;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * 将来の各インベントリソートMod（IPN、ClientSort、mc-inventory-management など）
 * へ介入するための共通インターフェースです。
 */
public interface SortInterceptor {

    /**
     * 対象Modの識別子（例: "inventoryprofilesnext", "clientsort", "inventorymanagement"）
     */
    String getModId();

    /**
     * 当該Modが導入・有効化されているか
     */
    boolean isAvailable();

    /**
     * エンダーチェストGUIが検出された際の通知
     */
    default void onVaultGuiDetected(AbstractContainerMenu menu) {}

    /**
     * コンテナ内のスロットインデックス (0始まり) が自動操作から除外されるべきか判定します。
     * 除外範囲: 45 <= containerSlotIndex <= 53
     */
    default boolean shouldProtectSlot(int containerSlotIndex) {
        return SlotLayout.isProtectedContainerSlot(containerSlotIndex);
    }
}
