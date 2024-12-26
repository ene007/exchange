package com.bizzan.bc.wallet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @Auther: liuj
 * @Date: 2021/03/14/16:06
 * @Description:
 */
@Service
public class TRC20Service {

    //节点地址
    @Value("${fullnode.http}")
    private String http="http://52.53.189.99:8090/";



    @Autowired
    private AccountService accountService;

    public static void main(String arg[]) {
        TRC20Service trc20Service=new TRC20Service();

        String ownerAddress="TGkxgxSGjLM6bZWkewtjgdk8teYd5XWwJN";

        //获取trx余额
//        System.out.println(trc20Service.getAccount("TFtBYiVb87YBzHdKRWQv2CCdZWprVvNjUd"));
        //获取trc20 余额
//        System.out.println(trc20Service.getTrc20Account("TRqYRG16yb6zhrHdjkrAMm52m2CitWmcKL","TFX279LExhxpDrjiWxRzh6SrVzHdP93kPT"));


        //随机生成地址和密钥
//        AddressTemp address=trc20Service.generateAddress();
//        String temp=trc20Service.createAccount(ownerAddress,address.getAddress(),address.getPrivateKey());
//        System.out.println(address.getAddress()+"  "+address.getPrivateKey());
//        System.out.println(temp);
        //trx 转账
        String transaction=trc20Service.trxTransaction("TFtBYiVb87YBzHdKRWQv2CCdZWprVvNjUd",BigDecimal.valueOf(0.1),"");
        System.out.println(transaction);
        //trc20 转账
//        trc20Transaction("TW6a53SLU4vM4QJBnK2vWVx4cqTnDChS1m","TT7bh9H6o8hVXXQ4L3q5Cp17LASmW9ud2y",BigDecimal.valueOf(0.1),"",null);

    }

    /**
     * TRX 随机生成地址
     *
     * @return
     **/
    public AddressTemp generateAddress() {
        String url = http + "/wallet/generateaddress";
        RestTemplate restTemplate = new RestTemplate();
        String entity = restTemplate.getForEntity(url, String.class).getBody();
        AddressTemp addressTemp = JSONArray.parseObject(entity, AddressTemp.class);
        return addressTemp;
    }

    /**
     * 在链上创建账号. 一个已经激活的账号创建一个新账号需要花费 0.1 TRX 或等值 Bandwidth 带宽 创建完成后，需要签名广播
     * PS：HTTP API 创建交易的过期时间是1分钟. 需要在1分钟内完成签名和广播.
     *
     * @param accountAddress 待创建的新账户地址
     * @param privateKey     待创建用户的密钥
     */
    public String createAccount(String ownerAddress,String accountAddress, String privateKey) {
        String url = http + "/wallet/createaccount";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(ownerAddress)));
        map.add("account_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(accountAddress)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String entity = restTemplate.postForEntity(url, request, String.class).getBody();
        //签名广播
        signAndBroadcast(entity, privateKey);
        return entity;
    }


    /**
     * 签名广播
     *
     * @param transaction 交易对象
     * @return
     */
    private String signAndBroadcast(String transaction, String privateKey) {
        //签名
        String url = http + "/wallet/gettransactionsign";
        Map<String, Object> map = new HashMap<>();
        map.put("transaction", transaction);
        map.put("privateKey", privateKey);
        String param = JSON.toJSONString(map);
        ResponseEntity<String> stringResponseEntity = postForEntity(url, param);
        //广播
        url = http + "/wallet/broadcasttransaction";
        stringResponseEntity = postForEntity(url, stringResponseEntity.getBody());
        return stringResponseEntity.getBody();
    }


    /**
     * 获取TRX地址余额
     *
     * @param address
     * @return
     */
    public BigDecimal getAccount(String address) {
        String url = http + "/wallet/getaccount";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(address)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
        String entity = restTemplate.postForEntity(url, request, String.class).getBody();
        JSONObject jsonBody = JSONObject.parseObject(entity);
        if (jsonBody.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal balance = new BigDecimal(jsonBody.getBigInteger("balance")).divide(new BigDecimal("1000000"));
            return balance;
        }
    }

    /**
     * 获取合约地址余额
     *
     * @param ownerAddress
     * @return
     */
    public BigDecimal getTrc20Account(String ownerAddress, String contractAddress) {
        String url = http + "/wallet/triggerconstantcontract";
        Map<String, Object> map = new HashMap<>();
        map.put("contract_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(contractAddress)));
        map.put("function_selector", "balanceOf(address)");
        map.put("parameter", "0000000000000000000000" + ByteArray.toHexString(WalletApi.decodeFromBase58Check(ownerAddress)));
        map.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(ownerAddress)));
        String param = JSON.toJSONString(map);
        JSONObject result = JSONObject.parseObject(postForEntity(url, param).getBody());
        String hexValue = result.getJSONArray("constant_result").getString(0);
        return TokenConverter.tokenHexValueToBigDecimal(hexValue, decimals(ownerAddress, contractAddress));
    }

    /**
     * 获取小数位
     *
     * @param ownerAddress
     * @param contractAddress
     * @return
     */
    public Integer decimals(String ownerAddress, String contractAddress) {
        String method = "decimals()";
        String response = triggerSmartContract(
                contractAddress,
                method,
                ownerAddress,
                null);
        JSONObject result = JSONObject.parseObject(response);
        String hexValue = result.getJSONArray("constant_result").getString(0);
        return TokenConverter.tokenHexValueToBigInteger(hexValue).intValue();
    }


    private String triggerSmartContract(String contractAddress, String method, String ownerAddress, String parameter) {
        String url = http + "/wallet/triggersmartcontract";
        Map<String, String> params = new HashMap<>();
        String hexOwnerAddress = ByteArray.toHexString(WalletApi.decodeFromBase58Check(ownerAddress));
        String hexContractAddress = ByteArray.toHexString(WalletApi.decodeFromBase58Check(contractAddress));
        params.put("contract_address", hexContractAddress);
        params.put("function_selector", method);
        params.put("parameter", parameter);
        params.put("owner_address", hexOwnerAddress);
        String param = JSON.toJSONString(params);
        return postForEntity(url, param).getBody();
    }

    /**
     * trx 转账 快捷转账 API. 直接使用私钥从账户地址转账TRX. 集成了签名和广播功能。
     *
     * @param toAddress 地址
     * @param amount    数量
     */
    public String trxTransaction(String toAddress, BigDecimal amount, String privateKey) {
        String url = http + "/wallet/easytransferbyprivate";
        Map<String, Object> map = new HashMap<>();
        map.put("privateKey", privateKey);
        map.put("toAddress", ByteArray.toHexString(WalletApi.decodeFromBase58Check(toAddress)));
        amount = amount.multiply(new BigDecimal(1 + 6));
        map.put("amount", amount.toBigInteger());
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }

    /**
     * trc20 转账
     *
     * @param senderAddress
     * @param amount
     * @param senderPrivateKey
     * @param fee
     * @return
     */
    public String trc20Transaction(String senderAddress, String receiverAddress, BigDecimal amount, String senderPrivateKey, Long fee, String contractAddress) {
        //发起交易
        String url = http + "/wallet/triggersmartcontract";

        Map<String, Object> map = new HashMap<>();

        String to_address = ByteArray.toHexString(WalletApi.decodeFromBase58Check(receiverAddress));
        to_address = TransformUtil.addZeroForNum(to_address, 64);
        // todo trc20 单位换算
        amount = amount.multiply(new BigDecimal(1 + TransformUtil.getSeqNumByLong(0L, 6)));
        String uint256 = TransformUtil.addZeroForNum(amount.toBigInteger().toString(16), 64);

        map.put("owner_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(senderAddress)));
        map.put("contract_address", ByteArray.toHexString(WalletApi.decodeFromBase58Check(contractAddress)));
        map.put("function_selector", "transfer(address,uint256)");
        map.put("parameter", to_address + uint256);
        map.put("call_value", 0);
        map.put("fee_limit", fee);

        String param = JSON.toJSONString(map);

        ResponseEntity<String> stringResponseEntity = postForEntity(url, param);

        return signAndBroadcast(JSON.parseObject(stringResponseEntity.getBody()).getString("transaction"), senderPrivateKey);
    }


    /**
     * 查询交易的 Info 信息, 包括交易的 fee 信息, 所在区块, 虚拟机 log 等.
     *
     * @param txId 交易id
     * @return
     */
    public String getTransactionInfoById(String txId) {
        String url = http + "/wallet/gettransactioninfobyid";
        Map<String, Object> map = new HashMap<>();
        map.put("value", txId);
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }

    /**
     * 获取特定区块的所有交易 Info 信息
     *
     * @param num 区块
     * @return
     */
    public String getTransactionInfoByBlockNum(BigInteger num) {
        String url = http + "/wallet/gettransactioninfobyblocknum";
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        String param = JSON.toJSONString(map);
        return postForEntity(url, param).getBody();
    }


    /**
     * 查询最新区块
     *
     * @return
     */
    public String getNowBlock() {
        String url = http + "/wallet/getnowblock";
        return getForEntity(url);
    }


    /**
     * 执行 post 请求
     *
     * @param url   url
     * @param param 请求参数
     * @return
     */
    private ResponseEntity<String> postForEntity(String url, String param) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<>(param, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
        return result;
    }

    /**
     * 执行 get 请求
     *
     * @param url url
     * @return
     */
    private String getForEntity(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        return result.getBody();
    }


}




