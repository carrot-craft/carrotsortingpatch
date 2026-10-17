package io.github.countyclaaw4.carrotcraftsortingpatch;

import io.github.countyclaaw4.carrotcraftsortingpatch.platform.ModLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@Mod(value = CarrotSortingPatch.MOD_ID, dist = Dist.CLIENT)
public final class CarrotSortingPatchNeoForge {
    public CarrotSortingPatchNeoForge() {
        ModLoader.initialize(modId -> ModList.get().isLoaded(modId));
        CarrotSortingPatch.initialize();
    }
}
