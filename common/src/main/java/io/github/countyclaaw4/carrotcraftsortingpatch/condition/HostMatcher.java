package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import java.util.Locale;

/**
 * 接続先アドレスが CarrotCraft のものかを判定する（Minecraft のクラスに依存しない）。
 * carrotcraft.net とそのサブドメイン（play.carrotcraft.net など）を対象にする。
 */
public final class HostMatcher {
    public static final String TARGET_DOMAIN = "carrotcraft.net";

    private HostMatcher() {}

    /**
     * 与えられたアドレス文字列（host、host:port、[ipv6]:port）が対象ドメインかどうか。
     * "evilcarrotcraft.net" や "carrotcraft.net.example.com" のような紛らわしいものは対象外。
     */
    public static boolean isTargetHost(String address) {
        if (address == null) {
            return false;
        }
        String host = extractHost(address).toLowerCase(Locale.ROOT);
        // FQDN 表記の末尾のドット（play.carrotcraft.net.）を許す
        while (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        return host.equals(TARGET_DOMAIN) || host.endsWith("." + TARGET_DOMAIN);
    }

    /**
     * アドレス文字列からポート番号や IPv6 のブラケットを取り除き、トリムしたホスト名を返す。
     */
    public static String extractHost(String address) {
        String trimmed = address.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        // IPv6 形式: [host]:port
        if (trimmed.startsWith("[")) {
            int closingBracket = trimmed.indexOf(']');
            if (closingBracket != -1) {
                return trimmed.substring(1, closingBracket).trim();
            }
        }
        // 通常の host:port 形式
        int colonIndex = trimmed.indexOf(':');
        if (colonIndex != -1) {
            return trimmed.substring(0, colonIndex).trim();
        }
        return trimmed;
    }
}
