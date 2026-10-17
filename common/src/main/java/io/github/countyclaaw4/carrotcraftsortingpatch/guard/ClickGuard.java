package io.github.countyclaaw4.carrotcraftsortingpatch.guard;

import io.github.countyclaaw4.carrotcraftsortingpatch.condition.SlotLayout;

/**
 * 「人がマウスやキーで押したクリック」と「他の Mod がプログラムから送ったクリック」を区別する。
 *
 * <p>人の操作は必ず画面の slotClicked を通る。ソート Mod の多くは slotClicked を通らず
 * クリック送信を直接呼ぶので、保護スロットへのそれを止めれば、対応 Mod を個別に増やさなくても守れる。
 * クライアントのメインスレッドからだけ呼ばれる前提。
 */
public final class ClickGuard {
    private static int userClickDepth;

    private ClickGuard() {}

    /** 画面の slotClicked に入った（人の操作の始まり）。 */
    public static void enterUserClick() {
        userClickDepth++;
    }

    /** 画面の slotClicked から出た。 */
    public static void exitUserClick() {
        if (userClickDepth > 0) {
            userClickDepth--;
        }
    }

    /** 例外などで出口を通らなかった場合に備えて、毎ティックと画面の開閉で呼んで戻す。 */
    public static void reset() {
        userClickDepth = 0;
    }

    public static boolean isUserClick() {
        return userClickDepth > 0;
    }

    /** 人の操作でないクリックが保護スロットに向かう時だけ止める。 */
    public static boolean shouldBlock(int containerSlotIndex, boolean userInitiated) {
        return !userInitiated && SlotLayout.isProtectedContainerSlot(containerSlotIndex);
    }
}
