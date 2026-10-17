package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public final class VaultGuiDetector {
    public static final int REQUIRED_CONTAINER_SLOTS = SlotLayout.REQUIRED_CONTAINER_SLOTS;
    public static final int SLOT_INDEX_PLAN = 45;
    public static final int SLOT_INDEX_HOWTO = 53;

    public static final String NAME_PLAN = "プラン";
    public static final String NAME_HOWTO = "使い方";

    private VaultGuiDetector() {}

    /**
     * 指定されたコンテナメニューが CarrotCraft のエンダーチェスト GUI か判定します。
     * 条件A: コンテナ領域がちょうど 54 スロットであること。
     * 条件B: index 45 が clock、「プラン」、carrotgui:item = 1b であること。
     * 条件C: index 53 が book、「使い方」、carrotgui:item = 1b であること。
     * 判定中に例外が発生した場合は安全に false を返します（保護を諦めるだけで、画面は壊さない）。
     */
    public static boolean isVaultGui(AbstractContainerMenu menu) {
        if (menu == null) {
            return false;
        }

        try {
            if (!checkContainerSlotCount(menu)) {
                return false;
            }
            if (!matchesSlotItem(menu.getSlot(SLOT_INDEX_PLAN), Items.CLOCK, NAME_PLAN)) {
                return false;
            }
            return matchesSlotItem(menu.getSlot(SLOT_INDEX_HOWTO), Items.BOOK, NAME_HOWTO);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * プレイヤーインベントリを除くコンテナ領域のスロット数がちょうど 54 か確認します。
     */
    public static boolean checkContainerSlotCount(AbstractContainerMenu menu) {
        if (menu.slots.size() < REQUIRED_CONTAINER_SLOTS) {
            return false;
        }
        return SlotLayout.leadingContainerSlots(playerSlotFlags(menu.slots)) == REQUIRED_CONTAINER_SLOTS;
    }

    /**
     * スロットのアイテムが、指定アイテム・指定名・carrotgui:item = 1b をすべて満たすか判定します。
     */
    public static boolean matchesSlotItem(Slot slot, Item expectedItem, String expectedName) {
        if (slot == null) {
            return false;
        }
        ItemStack stack = slot.getItem();
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        // 1. アイテムの種類
        if (stack.getItem() != expectedItem) {
            return false;
        }

        // 2. 名前（装飾コードを除いて前後の空白を外して比較）
        Component nameComponent = stack.get(DataComponents.CUSTOM_NAME);
        if (nameComponent == null) {
            nameComponent = stack.getHoverName();
        }
        if (nameComponent == null) {
            return false;
        }
        if (!NameMatcher.matches(nameComponent.getString(), expectedName)) {
            return false;
        }

        // 3. carrotgui:item = 1b
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        CompoundTag publicBukkitValues = customData.copyTag().getCompoundOrEmpty("PublicBukkitValues");
        if (!publicBukkitValues.contains("carrotgui:item")) {
            return false;
        }
        return publicBukkitValues.getByteOr("carrotgui:item", (byte) 0) == (byte) 1;
    }

    /**
     * コンテナ内のスロット番号（0 始まり）が保護対象（45〜53）か判定します。
     */
    public static boolean isProtectedContainerSlot(int containerSlotIndex) {
        return SlotLayout.isProtectedContainerSlot(containerSlotIndex);
    }

    /**
     * メニュー全体のスロット番号から、コンテナ領域内の番号（0 始まり）を計算します。
     * プレイヤーインベントリのスロットや範囲外の場合は -1 を返します。
     */
    public static int getContainerSlotIndex(AbstractContainerMenu menu, int slotId) {
        if (menu == null || slotId < 0 || slotId >= menu.slots.size()) {
            return -1;
        }
        return SlotLayout.containerIndices(playerSlotFlags(menu.slots))[slotId];
    }

    /**
     * 複数スロットのコンテナ内番号を、メニューを一度走査して取得します。
     * メニューに含まれないスロットやプレイヤーインベントリのスロットは -1 になります。
     */
    public static int[] getContainerSlotIndices(AbstractContainerMenu menu, Slot[] slots) {
        if (slots == null) {
            return new int[0];
        }
        int[] result = new int[slots.length];
        java.util.Arrays.fill(result, -1);
        if (menu == null || slots.length == 0) {
            return result;
        }

        int[] byPosition = SlotLayout.containerIndices(playerSlotFlags(menu.slots));
        java.util.Map<Slot, Integer> positionBySlot = new java.util.IdentityHashMap<>();
        for (int i = 0; i < menu.slots.size(); i++) {
            positionBySlot.put(menu.slots.get(i), i);
        }
        for (int i = 0; i < slots.length; i++) {
            Integer position = positionBySlot.get(slots[i]);
            result[i] = position == null ? -1 : byPosition[position];
        }
        return result;
    }

    /** メニュー内の位置ごとに「プレイヤーの持ち物のスロットか」を並べる。 */
    public static boolean[] playerSlotFlags(List<Slot> slots) {
        boolean[] flags = new boolean[slots.size()];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = slots.get(i).container instanceof Inventory;
        }
        return flags;
    }
}
