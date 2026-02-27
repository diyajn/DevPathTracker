package com.org.example.service;


import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SM2AlgorithmService {

    /**
     * Calculate next review date using SM-2 algorithm
     *
     * @param rating User's rating (1-5)
     * @param currentEF Current easiness factor
     * @param currentRepetition Current repetition count
     * @param currentInterval Current interval in days
     * @return SM2Result containing new EF, repetition, and interval
     */
    public SM2Result calculateNext(int rating, BigDecimal currentEF, int currentRepetition, int currentInterval) {

        // Step 1: Calculate new Easiness Factor (EF)
        BigDecimal newEF = calculateEasinessFactor(rating, currentEF);

        // Step 2: Determine repetition count and interval
        int newRepetition;
        int newInterval;

        if (rating < 3) {
            // Failed recall - reset progress
            newRepetition = 0;
            newInterval = 1;  // Review tomorrow
        } else {
            // Successful recall
            newRepetition = currentRepetition + 1;
            newInterval = calculateInterval(newRepetition, currentInterval, newEF);
        }

        return new SM2Result(newEF, newRepetition, newInterval);
    }

    /**
     * Calculate new Easiness Factor based on rating
     * Formula: EF' = EF + (0.1 - (5 - rating) * (0.08 + (5 - rating) * 0.02))
     */
    private BigDecimal calculateEasinessFactor(int rating, BigDecimal currentEF) {
        // Break down the formula step by step using BigDecimal

        // (5 - rating)
        BigDecimal fiveMinusRating = BigDecimal.valueOf(5 - rating);

        // (5 - rating) * 0.02
        BigDecimal term1 = fiveMinusRating.multiply(new BigDecimal("0.02"));

        // 0.08 + (5 - rating) * 0.02
        BigDecimal term2 = new BigDecimal("0.08").add(term1);

        // (5 - rating) * (0.08 + (5 - rating) * 0.02)
        BigDecimal term3 = fiveMinusRating.multiply(term2);

        // 0.1 - (5 - rating) * (0.08 + (5 - rating) * 0.02)
        BigDecimal adjustment = new BigDecimal("0.1").subtract(term3);

        // EF' = EF + adjustment
        BigDecimal newEF = currentEF.add(adjustment);

        // EF must be at least 1.3
        BigDecimal minEF = new BigDecimal("1.3");
        if (newEF.compareTo(minEF) < 0) {
            newEF = minEF;
        }

        // Round to 2 decimal places
        return newEF.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate interval based on repetition number
     *
     * Repetition 1: 1 day
     * Repetition 2: 6 days
     * Repetition 3+: previous interval * EF
     */
    private int calculateInterval(int repetition, int previousInterval, BigDecimal easinessFactor) {
        if (repetition == 1) {
            return 1;
        } else if (repetition == 2) {
            return 6;
        } else {
            // For repetition >= 3, multiply previous interval by EF
            BigDecimal interval = easinessFactor.multiply(BigDecimal.valueOf(previousInterval));
            return interval.setScale(0, RoundingMode.HALF_UP).intValue();
        }
    }

    /**
     * Inner class to hold SM-2 calculation results
     */
    public static class SM2Result {
        private final BigDecimal easinessFactor;
        private final int repetitionCount;
        private final int intervalDays;

        public SM2Result(BigDecimal easinessFactor, int repetitionCount, int intervalDays) {
            this.easinessFactor = easinessFactor;
            this.repetitionCount = repetitionCount;
            this.intervalDays = intervalDays;
        }

        public BigDecimal getEasinessFactor() {
            return easinessFactor;
        }

        public int getRepetitionCount() {
            return repetitionCount;
        }

        public int getIntervalDays() {
            return intervalDays;
        }
    }
}