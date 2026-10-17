package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlotLayoutTest {

    /** コンテナ側 n + プレイヤーの持ち物 36（エンダーチェスト画面は n=54） */
    private static boolean[] menu(int containerSlots) {
        boolean[] flags = new boolean[containerSlots + 36];
        for (int i = containerSlots; i < flags.length; i++) {
            flags[i] = true;
        }
        return flags;
    }

    @Test
    @DisplayName("保護対象は 45〜53 だけ")
    void protectedRange() {
        for (int i = 0; i < 45; i++) {
            assertFalse(SlotLayout.isProtectedContainerSlot(i), "slot " + i);
        }
        for (int i = 45; i <= 53; i++) {
            assertTrue(SlotLayout.isProtectedContainerSlot(i), "slot " + i);
        }
        assertFalse(SlotLayout.isProtectedContainerSlot(54));
        assertFalse(SlotLayout.isProtectedContainerSlot(-1));
    }

    @Test
    @DisplayName("先頭から連続するコンテナ側スロット数")
    void leadingContainerSlots() {
        assertEquals(54, SlotLayout.leadingContainerSlots(menu(54)));
        assertEquals(27, SlotLayout.leadingContainerSlots(menu(27)));
        assertEquals(0, SlotLayout.leadingContainerSlots(new boolean[0]));
        assertEquals(0, SlotLayout.leadingContainerSlots(new boolean[]{true, false}));
    }

    @Test
    @DisplayName("メニュー位置 → コンテナ内番号（持ち物は -1）")
    void containerIndices() {
        int[] idx = SlotLayout.containerIndices(menu(54));
        assertEquals(90, idx.length);
        for (int i = 0; i < 54; i++) {
            assertEquals(i, idx[i]);
        }
        for (int i = 54; i < 90; i++) {
            assertEquals(-1, idx[i]);
        }
    }

    @Test
    @DisplayName("持ち物スロットが途中に混ざっても、コンテナ側だけを順に数える")
    void containerIndicesInterleaved() {
        int[] idx = SlotLayout.containerIndices(new boolean[]{false, false, true, false, false});
        assertArrayEquals(new int[]{0, 1, -1, 2, 3}, idx);
    }
}
