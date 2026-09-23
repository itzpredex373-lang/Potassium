package com.predex.potassium.optimization.world;

import com.predex.potassium.optimization.system.CpuOptimizer;

public final class WorldUpdateScheduler {
    private int budget = 2;

    public void setBudget(int budget) { this.budget = Math.max(1, budget); }

    public int consumeBudget() {
        if (!CpuOptimizer.shouldRunOptionalWork()) return 0;
        return budget;
    }
}