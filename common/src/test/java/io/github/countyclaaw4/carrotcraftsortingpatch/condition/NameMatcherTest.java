package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameMatcherTest {

    @Test
    @DisplayName("装飾コードの除去")
    void stripFormatting() {
        assertEquals("使い方", NameMatcher.stripFormatting("§e使い方"));
        assertEquals("使い方", NameMatcher.stripFormatting("§e§l使い方§r"));
        assertEquals("プラン", NameMatcher.stripFormatting("§b§nプラン§r"));
        assertEquals("test", NameMatcher.stripFormatting("test"));
        assertEquals("", NameMatcher.stripFormatting(""));
        assertEquals("", NameMatcher.stripFormatting(null));
    }

    @Test
    @DisplayName("名前の完全一致（装飾と前後の空白は無視）")
    void matches() {
        assertTrue(NameMatcher.matches("プラン", "プラン"));
        assertTrue(NameMatcher.matches("§fプラン", "プラン"));
        assertTrue(NameMatcher.matches(" §f§lプラン§r ", "プラン"));
        assertFalse(NameMatcher.matches("プラン変更", "プラン"));
        assertFalse(NameMatcher.matches("使い方", "プラン"));
        assertFalse(NameMatcher.matches("", "プラン"));
        assertFalse(NameMatcher.matches(null, "プラン"));
    }
}
