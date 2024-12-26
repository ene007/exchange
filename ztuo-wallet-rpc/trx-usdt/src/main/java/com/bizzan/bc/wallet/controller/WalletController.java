package com.bizzan.bc.wallet.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.service.TRC20Service;
import com.bizzan.bc.wallet.util.AddressTemp;
import com.bizzan.bc.wallet.util.MessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/rpc")
public class WalletController {
    private Logger logger = LoggerFactory.getLogger(WalletController.class);

    //系统TRX账户地址，用于给用户创建波场账号用，每次创建花费0.1TRX
    @Value("${owner.address}")
    private String ownerAddress;
    //trc20 udst的合约地址
    @Value("${contract.address}")
    private String  contractAddress;

    @Autowired
    private TRC20Service trc20Service;
    @Autowired
    private AccountService accountService;


    /**
     * 创建TRX转账
     * @param account
     * @return
     */
    @GetMapping("address/{account}")
    public MessageResult getNewAddress(@PathVariable String account) {
        logger.info("create new account={}", account);
        try {
            //使用节点随机生成地址
            AddressTemp addressTemp = trc20Service.generateAddress();
            //使用主账号创建trx账号
            trc20Service.createAccount(ownerAddress,addressTemp.getAddress(),addressTemp.getPrivateKey());
            Account temp=new Account();
            temp.setAccount(account);
            temp.setAddress(addressTemp.getAddress());
            temp.setWalletFile(addressTemp.getPrivateKey());
            accountService.save(temp);
            MessageResult result = new MessageResult(0, "success");
            result.setData(addressTemp.getAddress());
            return result;
        } catch (Exception e) {
            logger.error("创建trx账号异常：",e);
            return MessageResult.error(500, "rpc error:" + e.getMessage());
        }
    }

    /**
     * 获取 TRX 单个地址余额
     * @param address
     * @return
     */
    @GetMapping("balance/{address}")
    public MessageResult addressTrxBalance(@PathVariable String address) {
        try {
            BigDecimal balance = trc20Service.getAccount(address);
            MessageResult result = new MessageResult(0, "success");
            result.setData(balance);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "查询失败，error:" + e.getMessage());
        }
    }

    /**
     * 获取 TRC20-USDT 合约地址余额
     * @param address
     * @return
     */
    @GetMapping("balance/trc20/{address}")
    public MessageResult addressTrc20Balance(@PathVariable String address) {
        try {

            BigDecimal balance = trc20Service.getTrc20Account(address,contractAddress);
            MessageResult result = new MessageResult(0, "success");
            result.setData(balance);
            return result;
        } catch (Exception e) {
            logger.error("获取 TRC20-USDT 合约地址余额异常：",e);
            return MessageResult.error(500, "查询失败，error:" + e.getMessage());
        }
    }

    /**
     * TRX转账
     * @param address 接受者地址
     * @param amount  转账数量
     * @param privateKey 转账者私钥
     * @return
     */
    @GetMapping("transfer")
    public MessageResult transfer(String address, BigDecimal amount, String privateKey) {
        logger.info("transfer:address={},amount={},privateKey={}", address, amount, privateKey);
        try {
            Account account=accountService.findByAddress(address);
            if (account == null ) {
                MessageResult messageResult = new MessageResult(500, "没有满足条件的转账账户(大于0.1)!");
                logger.info(messageResult.toString());
                return messageResult;
            }
            String entity = trc20Service.trxTransaction(address, amount, privateKey);
            JSONObject jsonBody = JSONObject.parseObject(entity);
            MessageResult result=new MessageResult();
            if (jsonBody.isEmpty()) {
                result.setCode(500);
                result.setMessage("TRX转账异常");
            } else {
                result=MessageResult.success();
            }
            return result;
        } catch (Exception e) {
            logger.error("TRX转账异常：",e);
            return MessageResult.error(500, "error:" + e.getMessage());
        }
    }

    /**
     * TRC20 转账
     * @param address 转账者地址
     * @param amount 数量
     * @param fee 费率
     * @param receiverAddress 接受者地址
     * @return
     */
    @GetMapping("trc/transfer")
    public MessageResult trcTransfer(String address, BigDecimal amount, Long fee,String receiverAddress) {
        logger.info("transfer:address={},amount={},receiverAddress={}", address, amount, receiverAddress);
        try {
            Account account=accountService.findByAddress(address);
            if (account == null ) {
                MessageResult messageResult = new MessageResult(500, "没有满足条件的转账账户(大于0.1)!");
                logger.info(messageResult.toString());
                return messageResult;
            }
            String entity = trc20Service.trc20Transaction(address,receiverAddress, amount, account.getWalletFile(),fee,contractAddress);
            JSONObject jsonBody = JSONObject.parseObject(entity);
            MessageResult result=new MessageResult();
            if (jsonBody.isEmpty()) {
                result.setCode(500);
                result.setMessage("TRC20转账异常");
            } else {
                result=MessageResult.success();
            }
            return result;
        } catch (Exception e) {
            logger.error("TRC20转账异常：",e);
            return MessageResult.error(500, "error:" + e.getMessage());
        }
    }
}
