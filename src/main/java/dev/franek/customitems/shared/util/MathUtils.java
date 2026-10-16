package dev.franek.customitems.shared.util;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

    public static boolean chance(double chance) {
        return chance >= 100.0 || chance >= ThreadLocalRandom.current().nextDouble(0.0, 100.0);
    }
}

