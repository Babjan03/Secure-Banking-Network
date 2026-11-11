package error;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Cyclic Redundancy Check (CRC) using Modulo-2 Polynomial Binary Division.
 * Generates Frame Check Sequence (FCS) remainder bits and verifies received frames for accidental bit errors.
 */
public class CRC {

    public static final String STANDARD_CRC8_ATM = "100000111"; // x^8 + x^2 + x + 1 (ATM HEC)
    public static final String STANDARD_CRC16_CCITT = "10001000000100001"; // x^16 + x^12 + x^5 + 1
    public static final String STANDARD_CRC4_DEMO = "1101"; // Demo polynomial: x^3 + x^2 + 1

    public static class DivisionStep {
        public final int stepIndex;
        public final String currentRemainder;
        public final String operation; // "XOR" or "SHIFT"

        public DivisionStep(int stepIndex, String currentRemainder, String operation) {
            this.stepIndex = stepIndex;
            this.currentRemainder = currentRemainder;
            this.operation = operation;
        }
    }

    public static class CRCResult {
        public final String originalData;
        public final String generatorPolynomial;
        public final String augmentedData;
        public final String remainder;
        public final String transmittedFrame;
        public final List<DivisionStep> divisionSteps;

        public CRCResult(String originalData, String generatorPolynomial, String augmentedData,
                         String remainder, String transmittedFrame, List<DivisionStep> divisionSteps) {
            this.originalData = originalData;
            this.generatorPolynomial = generatorPolynomial;
            this.augmentedData = augmentedData;
            this.remainder = remainder;
            this.transmittedFrame = transmittedFrame;
            this.divisionSteps = divisionSteps;
        }
    }

    public static class VerificationResult {
        public final String receivedFrame;
        public final String remainder;
        public final boolean isValid;
        public final String explanation;

        public VerificationResult(String receivedFrame, String remainder, boolean isValid, String explanation) {
            this.receivedFrame = receivedFrame;
            this.remainder = remainder;
            this.isValid = isValid;
            this.explanation = explanation;
        }
    }

    /**
     * Performs modulo-2 binary division of dividend by divisor.
     */
    public static String modulo2Divide(String dividend, String divisor, List<DivisionStep> steps) {
        int divisorLen = divisor.length();
        StringBuilder remainder = new StringBuilder(dividend.substring(0, divisorLen));

        for (int i = divisorLen; i <= dividend.length(); i++) {
            if (remainder.charAt(0) == '1') {
                // Perform XOR with divisor
                StringBuilder xored = new StringBuilder();
                for (int j = 0; j < divisorLen; j++) {
                    xored.append(remainder.charAt(j) == divisor.charAt(j) ? '0' : '1');
                }
                remainder = new StringBuilder(xored.substring(1));
                if (steps != null) {
                    steps.add(new DivisionStep(i, remainder.toString(), "XOR with " + divisor));
                }
            } else {
                // Leading bit is 0, XOR with zeros (equivalent to shifting)
                remainder = new StringBuilder(remainder.substring(1));
                if (steps != null) {
                    steps.add(new DivisionStep(i, remainder.toString(), "SHIFT (Leading 0)"));
                }
            }

            if (i < dividend.length()) {
                remainder.append(dividend.charAt(i));
            }
        }

        return remainder.toString();
    }

    /**
     * Computes the CRC remainder and generates the complete transmitted frame.
     */
    public static CRCResult computeCRC(String binaryData, String generatorPolynomial) {
        validateBinary(binaryData, "binaryData");
        validateBinary(generatorPolynomial, "generatorPolynomial");

        if (generatorPolynomial.length() < 2 || generatorPolynomial.charAt(0) != '1') {
            throw new IllegalArgumentException("Generator polynomial must start with '1' and be at least 2 bits.");
        }

        int polyDegree = generatorPolynomial.length() - 1;
        String augmentedData = binaryData + "0".repeat(polyDegree);

        List<DivisionStep> steps = new ArrayList<>();
        String remainder = modulo2Divide(augmentedData, generatorPolynomial, steps);

        // Ensure remainder length matches polynomial degree
        while (remainder.length() < polyDegree) {
            remainder = "0" + remainder;
        }

        String transmittedFrame = binaryData + remainder;

        return new CRCResult(binaryData, generatorPolynomial, augmentedData, remainder, transmittedFrame, steps);
    }

    /**
     * Verifies a received frame by dividing it by the generator polynomial.
     * If the remainder is all zeros, no transmission error occurred.
     */
    public static VerificationResult verifyFrame(String receivedFrame, String generatorPolynomial) {
        validateBinary(receivedFrame, "receivedFrame");
        validateBinary(generatorPolynomial, "generatorPolynomial");

        int polyDegree = generatorPolynomial.length() - 1;
        String remainder = modulo2Divide(receivedFrame, generatorPolynomial, null);

        while (remainder.length() < polyDegree) {
            remainder = "0" + remainder;
        }

        boolean isAllZero = remainder.chars().allMatch(c -> c == '0');
        String explanation = isAllZero
                ? "CRC Check PASSED: Remainder is 0. No accidental bit corruption detected."
                : "CRC Check FAILED: Non-zero remainder (" + remainder + "). Accidental bit corruption detected!";

        return new VerificationResult(receivedFrame, remainder, isAllZero, explanation);
    }

    /**
     * Flips a bit at the specified index to simulate transmission noise or electromagnetic interference.
     */
    public static String injectBitError(String frame, int bitIndex) {
        if (bitIndex < 0 || bitIndex >= frame.length()) {
            throw new IndexOutOfBoundsException("Bit index out of bounds: " + bitIndex);
        }
        char[] chars = frame.toCharArray();
        chars[bitIndex] = (chars[bitIndex] == '1') ? '0' : '1';
        return new String(chars);
    }

    private static void validateBinary(String s, String paramName) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty.");
        }
        for (char c : s.toCharArray()) {
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException(paramName + " must contain only '0' and '1', got: " + s);
            }
        }
    }
}
