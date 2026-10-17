package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

/**
 * エンダーチェスト画面のスロット配置の計算（Minecraft のクラスに依存しない）。
 * メニューのスロットを「プレイヤーの持ち物か」の真偽値だけで表して扱う。
 */
public final class SlotLayout {
    public static final int REQUIRED_CONTAINER_SLOTS = 54;

    public static final int PROTECTED_SLOT_MIN = 45;
    public static final int PROTECTED_SLOT_MAX = 53;

    private SlotLayout() {}

    /** コンテナ内のスロット番号（0 始まり）が保護対象（45〜53）か。 */
    public static boolean isProtectedContainerSlot(int containerSlotIndex) {
        return containerSlotIndex >= PROTECTED_SLOT_MIN && containerSlotIndex <= PROTECTED_SLOT_MAX;
    }

    /** 先頭から連続するコンテナ側スロットの数（プレイヤーの持ち物が最初に現れる所まで）。 */
    public static int leadingContainerSlots(boolean[] isPlayerSlot) {
        int count = 0;
        for (boolean player : isPlayerSlot) {
            if (player) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * メニュー内の位置ごとに、コンテナ内のスロット番号を返す。
     * プレイヤーの持ち物のスロットは -1。
     */
    public static int[] containerIndices(boolean[] isPlayerSlot) {
        int[] indices = new int[isPlayerSlot.length];
        int next = 0;
        for (int i = 0; i < isPlayerSlot.length; i++) {
            indices[i] = isPlayerSlot[i] ? -1 : next++;
        }
        return indices;
    }
}
