package com.fly.motion.math

class KalmanFilter(
    private var R: Double = 1.0, // Process noise
    private var Q: Double = 1.0, // Measurement noise
    private var A: Double = 1.0, // State vector
    private var B: Double = 0.0, // Control vector
    private var C: Double = 1.0  // Measurement vector
) {
    private var x: Double = 0.0 // State estimate
    private var cov: Double = Double.NaN // Covariance

    fun filter(measurement: Double): Double {
        if (cov.isNaN()) {
            x = (1 / C) * measurement
            cov = (1 / C) * Q * (1 / C)
        } else {
            val predX = (A * x) + (B * 0)
            val predCov = (A * cov * A) + R

            val K = predCov * C * (1 / ((C * predCov * C) + Q))

            x = predX + K * (measurement - (C * predX))
            cov = predCov - (K * C * predCov)
        }
        return x
    }
}
