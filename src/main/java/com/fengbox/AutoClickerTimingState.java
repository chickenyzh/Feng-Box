/*
 */
package com.fengbox;
import java.util.SplittableRandom;
public final class AutoClickerTimingState {
    private static final long MIN_DRIFT_INTERVAL_MILLIS = 1200L;
    private static final long MAX_DRIFT_INTERVAL_MILLIS = 3200L;
    private static final long MIN_TARGET_INTERVAL_MILLIS = 30000L;
    private static final long MAX_TARGET_INTERVAL_MILLIS = 90000L;
    private static final long IDLE_RESET_THRESHOLD_MILLIS = 1200L;
    private static final double MAX_FATIGUE = 0.4;
    private final SplittableRandom random;
    private final long timingSalt;
    private double currentCps;
    private double targetCps;
    private double minimumCps;
    private double maximumCps;
    private double targetOffset;
    private double pauseChanceOffset;
    private double repeatDelayChance = 0.06;
    private double fatigue;
    private int clicksSinceVariation;
    private int burstClicksRemaining;
    private long previousDelayMillis;
    private long lastClickTimestamp;
    private long nextDriftTimestamp;
    private long nextTargetTimestamp;
    private boolean initialized;

    AutoClickerTimingState() {
        this(System.nanoTime());
    }

    AutoClickerTimingState(long seed) {
        long randomSeed = AutoClickerTimingState.mixSeed(seed ^ 0x9E3779B97F4A7C15L);
        this.random = new SplittableRandom(randomSeed);
        this.timingSalt = AutoClickerTimingState.mixSeed(randomSeed + -3335678366873096957L);
    }

    private static long mixSeed(long seed) {
        long z = seed;
        z = (z ^ z >>> 30) * -4658895280553007687L;
        z = (z ^ z >>> 27) * -7723592293110705685L;
        return z ^ z >>> 31;
    }

    private static double midpoint(double first, double second) {
        return (first + second) * 0.5;
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private void scheduleDrift(long now) {
        this.nextDriftTimestamp = now + this.randomDuration(now, 1200L, 3200L);
    }

    private void scheduleTargetChange(long now) {
        this.nextTargetTimestamp = now + this.randomDuration(now, 30000L, 90000L);
    }

    private double sampleNormalDistribution() {
        double sampleSum = 0.0;
        for (int i = 0; i < 6; ++i) {
            sampleSum += this.random.nextDouble();
        }
        return (sampleSum - 3.0) * 1.22474487139;
    }

    private void initializeTiming(long now) {
        this.scheduleDrift(now);
        this.scheduleTargetChange(now);
        this.targetOffset = (this.random.nextDouble() * 2.0 - 1.0) * 0.75;
        this.targetCps = AutoClickerTimingState.clamp(AutoClickerTimingState.midpoint(this.minimumCps, this.maximumCps) + this.targetOffset, this.minimumCps, this.maximumCps);
    }

    private long randomDuration(long now, long minimumDuration, long maximumDuration) {
        long timeComponent = (now ^ this.timingSalt) & 0xFFFFL;
        long durationRange = maximumDuration - minimumDuration + 1L;
        return minimumDuration + (long)(Math.abs((int)timeComponent) + this.random.nextInt((int)durationRange)) % durationRange;
    }

    private double sampleClickNoise() {
        double normalSample = this.sampleNormalDistribution();
        double variationRoll = this.random.nextDouble();
        if (variationRoll < 0.12) {
            return (this.random.nextDouble() - 0.5) * 3.0;
        }
        if (variationRoll < 0.17) {
            return normalSample * (1.4 + 0.6 * this.random.nextDouble());
        }
        return normalSample;
    }

    void configureCpsRange(int minCps, int maxCps) {
        double rawMinimumCps = Math.max(0, minCps);
        double rawMaximumCps = Math.max(rawMinimumCps, (double)maxCps);
        double scaledMinimumCps = Math.max(0.5, rawMinimumCps * 1.5);
        double scaledMaximumCps = Math.max(0.5, rawMaximumCps * 1.5);
        this.minimumCps = scaledMinimumCps;
        this.maximumCps = Math.max(scaledMinimumCps + 0.1, scaledMaximumCps);
        if (!this.initialized) {
            this.targetCps = AutoClickerTimingState.midpoint(this.minimumCps, this.maximumCps);
            this.currentCps = AutoClickerTimingState.clamp(this.targetCps + this.sampleNormalDistribution() * 0.25, this.minimumCps, this.maximumCps);
        }
    }

    long getNextDelayMillis() {
        long idleDuration;
        long now = System.currentTimeMillis();
        if (!this.initialized) {
            this.initialized = true;
            this.initializeTiming(now);
            this.lastClickTimestamp = now;
        }
        long l = idleDuration = this.lastClickTimestamp != 0L ? now - this.lastClickTimestamp : 0L;
        if (idleDuration >= 1200L) {
            double recoveryAmount = (double)Math.min(5000L, idleDuration) / 10.0;
            this.fatigue = Math.max(0.0, this.fatigue - 0.004 * recoveryAmount);
            this.burstClicksRemaining = this.random.nextDouble() < 0.7 ? 2 + this.random.nextInt(4) : 0;
            this.repeatDelayChance = 0.03 + 0.06 * this.random.nextDouble();
            this.pauseChanceOffset = 0.04 * this.random.nextDouble();
        }
        ++this.clicksSinceVariation;
        if (this.clicksSinceVariation > 80 + this.random.nextInt(120)) {
            this.repeatDelayChance = 0.03 + 0.06 * this.random.nextDouble();
            this.pauseChanceOffset = 0.04 * this.random.nextDouble();
            this.clicksSinceVariation = 0;
        }
        if (now >= this.nextDriftTimestamp) {
            double drift = this.sampleNormalDistribution();
            this.currentCps = this.currentCps + 0.25 * (this.targetCps - this.currentCps) + 0.45 * drift;
            if (this.random.nextDouble() < 0.03) {
                this.currentCps += (this.random.nextDouble() - 0.5) * 1.2;
            }
            this.currentCps = AutoClickerTimingState.clamp(this.currentCps, this.minimumCps, this.maximumCps);
            this.scheduleDrift(now);
        }
        if (now >= this.nextTargetTimestamp) {
            this.targetOffset = (this.random.nextDouble() * 2.0 - 1.0) * 0.75;
            this.targetCps = AutoClickerTimingState.clamp(AutoClickerTimingState.midpoint(this.minimumCps, this.maximumCps) + this.targetOffset, this.minimumCps, this.maximumCps);
            this.scheduleTargetChange(now);
        }
        boolean isBurstClick = this.burstClicksRemaining > 0;
        double effectiveCps = this.currentCps;
        if (isBurstClick) {
            effectiveCps *= 1.0 + (0.05 + 0.4 * (0.8 + 0.6 * this.random.nextDouble()));
            --this.burstClicksRemaining;
        }
        effectiveCps *= 1.0 - 0.4 * Math.min(0.4, this.fatigue);
        effectiveCps = AutoClickerTimingState.clamp(effectiveCps, this.minimumCps, this.maximumCps);
        double baseDelay = 1000.0 / effectiveCps;
        double timingMultiplier = Math.exp(0.24 * this.sampleClickNoise());
        int maximumMicroJitter = 35 + this.random.nextInt(11) - 5;
        double delayMillis = baseDelay * timingMultiplier + (double)this.random.nextInt(Math.max(1, maximumMicroJitter) + 1);
        if (isBurstClick) {
            delayMillis += (double)this.random.nextInt(15);
        }
        if (this.previousDelayMillis > 0L && this.random.nextDouble() < this.repeatDelayChance) {
            delayMillis = this.previousDelayMillis + (long)this.random.nextInt(7) - 3L;
        }
        if (this.random.nextDouble() < 0.07) {
            delayMillis *= 0.7 + 0.2 * this.random.nextDouble();
        }
        double pauseChance = Math.min(0.18, 0.04 + 0.12 * this.fatigue + this.pauseChanceOffset);
        if (this.random.nextDouble() < pauseChance) {
            delayMillis += (double)(50 + this.random.nextInt(101));
        }
        this.fatigue = Math.min(0.4, this.fatigue + 0.015);
        this.lastClickTimestamp = now;
        this.previousDelayMillis = (long)Math.max(1.0, Math.min(2.147483647E9, delayMillis));
        return this.previousDelayMillis;
    }
}

