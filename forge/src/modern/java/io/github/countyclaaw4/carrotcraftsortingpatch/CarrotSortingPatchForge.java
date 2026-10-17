package io.github.countyclaaw4.carrotcraftsortingpatch;

import io.github.countyclaaw4.carrotcraftsortingpatch.platform.ModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * 26.x 向け（ModList.isLoaded が static）。クライアント専用 Mod。Forge には Mod クラス単位のサイド指定が無いが、
 * Mixin はすべて "client" 扱いなので、専用サーバーに入っていても何も適用されない。
 */
@Mod(CarrotSortingPatch.MOD_ID)
public final class CarrotSortingPatchForge {
    public CarrotSortingPatchForge() {
        ModLoader.initialize(modId -> ModList.isLoaded(modId));
        CarrotSortingPatch.initialize();
    }
}
