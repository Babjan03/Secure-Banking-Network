package error;

public class CRCTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 8 TEST: CYCLIC REDUNDANCY CHECK (CRC) MODULO-2         ");
        System.out.println("================================================================");

        int passed = 0;
        int total = 0;

        String data = "1101011011";
        String generator = "10011"; // CRC-4 (x^4 + x + 1)

        // Test 1: CRC Computation
        total++;
        CRC.CRCResult crcRes = CRC.computeCRC(data, generator);
        System.out.println("[TEST 1] Original Data      : " + crcRes.originalData);
        System.out.println("         Generator Poly     : " + crcRes.generatorPolynomial);
        System.out.println("         Augmented Data     : " + crcRes.augmentedData);
        System.out.println("         Calculated CRC/FCS : " + crcRes.remainder);
        System.out.println("         Transmitted Frame  : " + crcRes.transmittedFrame);
        boolean t1Ok = crcRes.transmittedFrame.equals(data + crcRes.remainder);
        System.out.printf("         Computation Result : %s\n", t1Ok ? "PASS" : "FAIL");
        if (t1Ok) passed++;

        // Test 2: Uncorrupted Frame Verification
        total++;
        CRC.VerificationResult cleanVerify = CRC.verifyFrame(crcRes.transmittedFrame, generator);
        System.out.println("[TEST 2] Verifying Clean Frame: " + cleanVerify.explanation);
        System.out.printf("         Verification Result  : %s\n", cleanVerify.isValid ? "PASS" : "FAIL");
        if (cleanVerify.isValid) passed++;

        // Test 3: Error Detection on Injected Bit Flip
        total++;
        int errorIndex = 4; // Flip bit 4
        String corruptedFrame = CRC.injectBitError(crcRes.transmittedFrame, errorIndex);
        CRC.VerificationResult corruptedVerify = CRC.verifyFrame(corruptedFrame, generator);
        System.out.println("[TEST 3] Injected bit flip at index " + errorIndex + ": " + corruptedFrame);
        System.out.println("         Corrupted Remainder : " + corruptedVerify.remainder);
        System.out.println("         Verification Result : " + corruptedVerify.explanation);
        boolean t3Ok = !corruptedVerify.isValid;
        System.out.printf("         Error Detected      : %s\n", t3Ok ? "PASS" : "FAIL");
        if (t3Ok) passed++;

        // Test 4: ATM CRC-8 Standard
        total++;
        String atmHeaderData = "10100101110000111111000010101010"; // 32-bit sample ATM header
        CRC.CRCResult atmCrc = CRC.computeCRC(atmHeaderData, CRC.STANDARD_CRC8_ATM);
        CRC.VerificationResult atmVerify = CRC.verifyFrame(atmCrc.transmittedFrame, CRC.STANDARD_CRC8_ATM);
        System.out.printf("[TEST 4] ATM CRC-8 (HEC) Remainder: %s, Verify: %b -> %s\n",
                atmCrc.remainder, atmVerify.isValid, atmVerify.isValid ? "PASS" : "FAIL");
        if (atmVerify.isValid) passed++;

        System.out.println("================================================================");
        System.out.printf("PHASE 8 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
