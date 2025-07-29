package com.example.sql;

    public class Rotation {
        private final boolean enabled;
        private final float speed;
        private final float variance;
        private final float multiplier2D;
        private final float multiplier3D;

        /**
         * Primary constructor—no defaults in Java, so supply all five values.
         */
        public Rotation(
                boolean enabled,
                float speed,
                float variance,
                float multiplier2D,
                float multiplier3D
        ) {
            this.enabled = enabled;
            this.speed = speed;
            this.variance = variance;
            this.multiplier2D = multiplier2D;
            this.multiplier3D = multiplier3D;
        }

        // Getters
        public boolean isEnabled()    { return enabled;    }
        public float   getSpeed()     { return speed;      }
        public float   getVariance()  { return variance;   }
        public float   getMultiplier2D() { return multiplier2D; }
        public float   getMultiplier3D() { return multiplier3D; }

        /**
         * Factory for the “enabled” default.
         */
        public static Rotation enabled() {
            return new Rotation(
                    true,   // enabled
                    1f,     // speed
                    0.5f,   // variance
                    8f,     // multiplier2D
                    1.5f    // multiplier3D
            );
        }

        /**
         * Factory for the “disabled” default.
         */
        public static Rotation disabled() {
            return new Rotation(
                    false,  // enabled
                    1f,
                    0.5f,
                    8f,
                    1.5f
            );
        }

        @Override
        public String toString() {
            return "Rotation(" +
                    "enabled=" + enabled +
                    ", speed=" + speed +
                    ", variance=" + variance +
                    ", multiplier2D=" + multiplier2D +
                    ", multiplier3D=" + multiplier3D +
                    ")";
        }
    }

