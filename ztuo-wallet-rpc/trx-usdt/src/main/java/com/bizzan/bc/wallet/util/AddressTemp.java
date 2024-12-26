package com.bizzan.bc.wallet.util;

import lombok.Data;

/**
 * @Auther: liuj
 * @Date: 2021/03/14/16:06
 */

public class AddressTemp {

    private String privateKey;

    private String address;

    private String hexAddress;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHexAddress() {
        return hexAddress;
    }

    public void setHexAddress(String hexAddress) {
        this.hexAddress = hexAddress;
    }
}
