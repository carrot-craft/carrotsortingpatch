package io.github.countyclaaw4.carrotcraftsortingpatch;

import io.github.countyclaaw4.carrotcraftsortingpatch.platform.ModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * 1.21.11 向け（ModList はインスタンス経由）。クライアント専用 Mod。Forge には Mod クラス単位のサイド指定が無いが、
 * Mixin はすべて "client" 扱いなので、専用サーバーに入っていても何も適用されない。
 */
@Mod(CarrotSortingPatch.MOD_ID)
public final class CarrotSortingPatchForge {
    public CarrotSortingPatchForge() {
        ModLoader.initialize(modId -> ModList.get().isLoaded(modId));
        CarrotSortingPatch.initialize();
    }
}
