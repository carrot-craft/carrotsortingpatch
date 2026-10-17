package io.github.countyclaaw4.carrotcraftsortingpatch.guard;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClickGuardTest {

    @AfterEach
    void cleanUp() {
        ClickGuard.reset();
    }

    @Test
    @DisplayName("人の操作でないクリックは、保護スロット(45〜53)の時だけ止める")
    void blocksOnlyAutomatedClicksOnProtectedSlots() {
        for (int i = 0; i < 45; i++) {
            assertFalse(ClickGuard.shouldBlock(i, false), "slot " + i);
        }
        for (int i = 45; i <= 53; i++) {
            assertTrue(ClickGuard.shouldBlock(i, false), "slot " + i);
        }
        assertFalse(ClickGuard.shouldBlock(-1, false));
        assertFalse(ClickGuard.shouldBlock(54, false));
    }

    @Test
    @DisplayName("人の操作によるクリックは、保護スロットでも止めない")
    void neverBlocksUserClicks() {
        for (int i = -1; i < 60; i++) {
            assertFalse(ClickGuard.shouldBlock(i, true), "slot " + i);
        }
    }

    @Test
    @DisplayName("入れ子の slotClicked でも、最後に出るまで人の操作として扱う")
    void nestedScopes() {
        assertFalse(ClickGuard.isUserClick());
        ClickGuard.enterUserClick();
        ClickGuard.enterUserClick();
        assertTrue(ClickGuard.isUserClick());
        ClickGuard.exitUserClick();
        assertTrue(ClickGuard.isUserClick());
        ClickGuard.exitUserClick();
        assertFalse(ClickGuard.isUserClick());
    }

    @Test
    @DisplayName("出口が余分に呼ばれても負数にならない・reset で必ず戻る")
    void exitIsSafeAndResetClears() {
        ClickGuard.exitUserClick();
        assertFalse(ClickGuard.isUserClick());
        ClickGuard.enterUserClick();
        ClickGuard.enterUserClick();
        ClickGuard.reset();
        assertFalse(ClickGuard.isUserClick());
        ClickGuard.enterUserClick();
        assertTrue(ClickGuard.isUserClick());
    }
}
