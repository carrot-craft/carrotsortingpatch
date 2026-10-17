package io.github.countyclaaw4.carrotcraftsortingpatch.condition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public final class ServerCondition {
    private ServerCondition() {}

    /**
     * 現在接続中のサーバーが CarrotCraft（carrotcraft.net 配下）かどうか判定します。
     * シングルプレイ、他サーバー、接続先を取得できない場合は false を返します。
     */
    public static boolean isTargetServer() {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client == null || client.isLocalServer() || client.hasSingleplayerServer()) {
                return false;
            }
            ServerData serverData = client.getCurrentServer();
            if (serverData == null || serverData.ip == null) {
                return false;
            }
            return HostMatcher.isTargetHost(serverData.ip);
        } catch (Throwable t) {
            return false;
        }
    }
}
