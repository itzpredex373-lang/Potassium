package com.predex.potassium.proxy;

/**
 * Common proxy. Client-only classes are kept out of the common bootstrap path.
 */
public class PotassiumProxy {
    public void registerClientHooks() {
        // No-op on dedicated servers.
    }
}
