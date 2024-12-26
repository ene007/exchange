package com.bizzan.bc.wallet.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.spongycastle.crypto.digests.SM3Digest;

/**
 * @author liuj
 * @date 2021/3/17 21:53
 * @description
 */
public class Sha256Sm3Hash {


    private static boolean isEckey = true;

    public static byte[] hash( byte[] input) {
        return hash( input, 0, input.length);
    }

    /**
     * Calculates the SHA-256 hash of the given byte range.
     *
     * @param input the array containing the bytes to hash
     * @param offset the offset within the array of the bytes to hash
     * @param length the number of bytes to hash
     * @return the hash (in big-endian order)
     */
    public static byte[] hash( byte[] input, int offset, int length) {
        if (isEckey) {
            MessageDigest digest = newDigest();
            digest.update(input, offset, length);
            return digest.digest();
        } else {
            SM3Digest digest = newSM3Digest();
            digest.update(input, offset, length);
            byte[] eHash = new byte[digest.getDigestSize()];
            digest.doFinal(eHash, 0);
            return eHash;
        }

    }

    public static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);  // Can't happen.
        }
    }

    public static SM3Digest newSM3Digest() {
        return new SM3Digest();
    }
}
