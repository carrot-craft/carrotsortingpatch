package io.github.countyclaaw4.carrotcraftsortingpatch;

import io.github.countyclaaw4.carrotcraftsortingpatch.interceptor.InterceptorRegistry;
import io.github.countyclaaw4.carrotcraftsortingpatch.interceptor.clientsort.ClientSortInterceptor;
import io.github.countyclaaw4.carrotcraftsortingpatch.interceptor.ipn.IpnInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CarrotSortingPatch {
    public static final String MOD_ID = "carrotsortingpatch";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private CarrotSortingPatch() {}

    public static void initialize() {
        LOGGER.info("[CarrotSortingPatch] 初期化開始");

        // 各ソートMod向けインターセプターの登録
        InterceptorRegistry.register(new IpnInterceptor());
        InterceptorRegistry.register(new ClientSortInterceptor());

        LOGGER.info("[CarrotSortingPatch] 初期化完了");
    }
}
