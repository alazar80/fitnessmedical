package com.example.sql;

/**
 * In-place radix-2 FFT (and inverse FFT).
 * <ul>
 *   <li>n must be a power of two</li>
 *   <li>Forward FFT: call fft(real, imag)</li>
 *   <li>Inverse FFT: call ifft(real, imag)</li>
 * </ul>
 */
public class FFT {
    private final int n, m;
    private final double[] cos;    // cos[k] = cos(−2πk/n)
    private final double[] sin;    // sin[k] = sin(−2πk/n)
    private final int[] bitRev;    // bitRev[i] = bit-reversed index of i in m bits

    /**
     * @param n size of FFT, must be power of 2
     */
    public FFT(int n) {
        if (Integer.bitCount(n) != 1)
            throw new IllegalArgumentException("n must be power of two");
        this.n = n;
        this.m = Integer.numberOfTrailingZeros(n);

        // precompute twiddles
        cos = new double[n/2];
        sin = new double[n/2];
        for (int k = 0; k < n/2; k++) {
            double angle = -2 * Math.PI * k / n;
            cos[k] = Math.cos(angle);
            sin[k] = Math.sin(angle);
        }

        // precompute bit-reversed indices
        bitRev = new int[n];
        for (int i = 0; i < n; i++) {
            bitRev[i] = Integer.reverse(i) >>> (32 - m);
        }
    }

    /**
     * Forward in-place FFT.
     *
     * @param real real-part array, length n
     * @param imag imaginary-part array, length n
     */
    public void fft(double[] real, double[] imag) {
        transform(real, imag, false);
    }

    /**
     * In-place inverse FFT.
     * After calling, you usually want to divide each element by n to get the
     * true inverse transform.
     *
     * @param real real-part array, length n
     * @param imag imaginary-part array, length n
     */
    public void ifft(double[] real, double[] imag) {
        transform(real, imag, true);
        // normalize
        for (int i = 0; i < n; i++) {
            real[i] /= n;
            imag[i] /= n;
        }
    }

    // core transform, forward if inv==false, inverse if inv==true
    private void transform(double[] real, double[] imag, boolean inv) {
        // bit-reverse reordering
        for (int i = 0; i < n; i++) {
            int j = bitRev[i];
            if (j > i) {
                double tr = real[i];
                real[i] = real[j];
                real[j] = tr;
                double ti = imag[i];
                imag[i] = imag[j];
                imag[j] = ti;
            }
        }

        // Cooley-Tukey
        for (int size = 2; size <= n; size <<= 1) {
            int half = size >>> 1;
            int step = n / size;
            for (int i = 0; i < n; i += size) {
                for (int j = 0; j < half; j++) {
                    int k = j * step;
                    double wr =  cos[k];
                    double wi = (inv ? -sin[k] : sin[k]);
                    double xr = real[i + j + half] * wr
                            - imag[i + j + half] * wi;
                    double xi = real[i + j + half] * wi
                            + imag[i + j + half] * wr;
                    real[i + j + half] = real[i + j] - xr;
                    imag[i + j + half] = imag[i + j] - xi;
                    real[i + j] += xr;
                    imag[i + j] += xi;
                }
            }
        }
    }

    /**
     * Returns the magnitudes sqrt(re^2 + im^2) into a new array.
     */
    public double[] getMagnitudes(double[] real, double[] imag) {
        double[] mag = new double[n];
        for (int i = 0; i < n; i++) {
            mag[i] = Math.hypot(real[i], imag[i]);
        }
        return mag;
    }
}
