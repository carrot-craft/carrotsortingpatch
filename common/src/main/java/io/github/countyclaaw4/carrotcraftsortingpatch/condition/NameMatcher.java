package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import java.util.regex.Pattern;

/**
 * アイテム名の比較（Minecraft のクラスに依存しない）。
 */
public final class NameMatcher {
    private static final Pattern FORMATTING_PATTERN = Pattern.compile("§[0-9a-fk-orA-FK-OR]");

    private NameMatcher() {}

    /** 書式・装飾コード（§a, §l など）を除去する。null は空文字にする。 */
    public static String stripFormatting(String input) {
        if (input == null) {
            return "";
        }
        return FORMATTING_PATTERN.matcher(input).replaceAll("");
    }

    /** 装飾コードと前後の空白を無視して、表示名が期待する名前と完全一致するか。 */
    public static boolean matches(String displayName, String expectedName) {
        return expectedName.equals(stripFormatting(displayName).trim());
    }
}
