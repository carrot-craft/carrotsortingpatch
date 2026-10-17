package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HostMatcherTest {

    @Test
    @DisplayName("ホスト名の抽出（ポート・ブラケット・空白）")
    void extractHost() {
        assertEquals("play.carrotcraft.net", HostMatcher.extractHost("play.carrotcraft.net"));
        assertEquals("play.carrotcraft.net", HostMatcher.extractHost("  play.carrotcraft.net  "));
        assertEquals("play.carrotcraft.net", HostMatcher.extractHost("play.carrotcraft.net:25565"));
        assertEquals("play.carrotcraft.net", HostMatcher.extractHost("  play.carrotcraft.net:19132  "));
        assertEquals("play.carrotcraft.net", HostMatcher.extractHost("[play.carrotcraft.net]:25565"));
        assertEquals("", HostMatcher.extractHost(""));
        assertEquals("", HostMatcher.extractHost("   "));
    }

    @Test
    @DisplayName("carrotcraft.net とそのサブドメインは対象（大文字小文字・ポート・空白・末尾のドットを許す）")
    void acceptsOwnDomain() {
        assertTrue(HostMatcher.isTargetHost("play.carrotcraft.net"));
        assertTrue(HostMatcher.isTargetHost("PLAY.CARROTCRAFT.NET"));
        assertTrue(HostMatcher.isTargetHost("Play.Carrotcraft.Net"));
        assertTrue(HostMatcher.isTargetHost("  play.carrotcraft.net  "));
        assertTrue(HostMatcher.isTargetHost("play.carrotcraft.net:25565"));
        assertTrue(HostMatcher.isTargetHost("  PLAY.CARROTCRAFT.NET:25565  "));
        assertTrue(HostMatcher.isTargetHost("carrotcraft.net"));
        assertTrue(HostMatcher.isTargetHost("sub.play.carrotcraft.net"));
        assertTrue(HostMatcher.isTargetHost("play.carrotcraft.net."));
        assertTrue(HostMatcher.isTargetHost("[play.carrotcraft.net]:25565"));
    }

    @Test
    @DisplayName("紛らわしい別ドメインや空の入力は対象外")
    void rejectsLookalikes() {
        assertFalse(HostMatcher.isTargetHost("evilcarrotcraft.net"));
        assertFalse(HostMatcher.isTargetHost("play.carrotcraft.net.example.com"));
        assertFalse(HostMatcher.isTargetHost("carrotcraft.net.evil.test"));
        assertFalse(HostMatcher.isTargetHost("carrotcraft.network"));
        assertFalse(HostMatcher.isTargetHost("xcarrotcraft.net"));
        assertFalse(HostMatcher.isTargetHost("other.server.net"));
        assertFalse(HostMatcher.isTargetHost("127.0.0.1"));
        assertFalse(HostMatcher.isTargetHost("127.0.0.1:25565"));
        assertFalse(HostMatcher.isTargetHost("::1"));
        assertFalse(HostMatcher.isTargetHost("."));
        assertFalse(HostMatcher.isTargetHost(null));
        assertFalse(HostMatcher.isTargetHost(""));
        assertFalse(HostMatcher.isTargetHost("   "));
    }
}
