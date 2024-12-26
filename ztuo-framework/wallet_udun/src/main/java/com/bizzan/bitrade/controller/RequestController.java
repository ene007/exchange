package com.bizzan.bitrade.controller;


import com.bizzan.bitrade.service.BiPayService;
import com.spark.bipay.constant.CoinType;
import com.spark.bipay.entity.Address;
import com.spark.bipay.entity.SupportCoin;
import com.spark.bipay.entity.Transaction;
import com.spark.bipay.http.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@RestController
public class RequestController {
    @Autowired
    private BiPayService biPayService;

    /**
     * 创建新地址
     *
     * @param coinType
     * @return
     */
    @RequestMapping("/bipay/create-address")
    public Address createCoinAddress(int coinType) {
        return biPayService.createCoinAddress(CoinType.codeOf(coinType),"","");
    }

    /**
     * 发起转账请求
     *
     * @param coinType
     * @param amount
     * @param address
     * @return
     */
    @RequestMapping("/bipay/transfer")
    public ResponseMessage<String> transfer(int coinType, BigDecimal amount, String address, String memo) {
        String orderId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        CoinType coin = CoinType.codeOf(coinType);
        ResponseMessage<String> resp = biPayService.transfer(orderId, amount, coin, coin.getCode(), address, memo);
        return resp;
    }

    /**
     * 代付
     *
     * @param coinType
     * @param amount
     * @param address
     * @return
     */
    @RequestMapping("/bipay/autotransfer")
    public ResponseMessage<String> autoTransfer(int coinType, BigDecimal amount, String address, String memo) {
        String orderId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        CoinType coin = CoinType.codeOf(coinType);
        ResponseMessage<String> resp = biPayService.autoTransfer(orderId, amount, coin, coin.getCode(), address, memo);
        return resp;
    }

    @RequestMapping("/test")
    public String test() {
        return "Success";
    }

    @RequestMapping("/bipay/transaction")
    public List<Transaction> queryTransaction() throws Exception {
        return biPayService.queryTransaction();
    }

    /**
     * 校验地址合法性
     * @param mainCoinType
     * @param address
     */
    @RequestMapping("/bipay/checkAddress")
    public boolean checkAddress(String mainCoinType, String address) throws Exception {
        return biPayService.checkAddress(mainCoinType, address);
    }

    /**
     *获取支持的币种
     * @param showBalance
     * @return
     * @throws Exception
     */
    @RequestMapping("/bipay/getSupportCoin")
    public List<SupportCoin> getSupportCoin(Boolean showBalance) throws Exception {
        return biPayService.getSupportCoin(showBalance);
    }
}
