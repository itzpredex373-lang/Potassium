package com.predex.potassium.proxy;

/**
 * Common proxy. Client-only features are kept out of the server bootstrap path.
 */
public class PotassiumProxy {
    public void registerCommonHooks() {
        // No server-side pet registration or entity spawning.
    }

    public void registerClientHooks() {
        // No-op on dedicated servers.
    }
}
