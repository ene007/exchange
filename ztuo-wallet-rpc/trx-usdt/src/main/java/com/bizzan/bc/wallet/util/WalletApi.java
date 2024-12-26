package com.bizzan.bc.wallet.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author liuj
 * @date 2021/3/17 21:50
 * @description
 */
public class WalletApi {

    private static byte addressPreFixByte = 65;;

    private static byte[] decode58Check(String input) {
        byte[] decodeCheck = Base58.decode(input);
        if (decodeCheck.length <= 4) {
            return null;
        }
        byte[] decodeData = new byte[decodeCheck.length - 4];
        System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
        byte[] hash0 = Sha256Sm3Hash.hash(decodeData);
        byte[] hash1 = Sha256Sm3Hash.hash(hash0);
        if (hash1[0] == decodeCheck[decodeData.length]
                && hash1[1] == decodeCheck[decodeData.length + 1]
                && hash1[2] == decodeCheck[decodeData.length + 2]
                && hash1[3] == decodeCheck[decodeData.length + 3]) {
            return decodeData;
        }
        return null;
    }

    public static byte[] decodeFromBase58Check(String addressBase58) {
        if (StringUtils.isEmpty(addressBase58)) {
            System.out.println("Warning: Address is empty !!");
            return null;
        }
        byte[] address = decode58Check(addressBase58);
        if (!addressValid(address)) {
            return null;
        }
        return address;
    }

    public static boolean addressValid(byte[] address) {
        if (ArrayUtils.isEmpty(address)) {
            System.out.println("Warning: Address is empty !!");
            return false;
        }
        if (address.length != 21) {
            System.out.println(
                    "Warning: Address length need "
                            + 21
                            + " but "
                            + address.length
                            + " !!");
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != WalletApi.getAddressPreFixByte()) {
            System.out.println(
                    "Warning: Address need prefix with "
                            + WalletApi.getAddressPreFixByte()
                            + " but "
                            + preFixbyte
                            + " !!");
            return false;
        }
        // Other rule;
        return true;
    }

    public static byte getAddressPreFixByte() {
        return addressPreFixByte;
    }
}
