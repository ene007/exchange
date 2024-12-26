package com.bizzan.bitrade.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.system.CoinExchangeFactory;

import java.math.BigDecimal;
import java.util.List;

/***
 * 功能：CoinExchangeFactoryConfig 配置类在 Spring Boot 启动时，
 * 创建了一个包含所有币种汇率信息的 CoinExchangeFactory 实例。
 * 这个工厂类存储了不同币种的汇率数据，并可以通过币种单位来获取相应的汇率。
 */
@Configuration
public class CoinExchangeFactoryConfig {
    @Autowired
    private CoinService coinService;

    @Bean
    public CoinExchangeFactory createCoinExchangeFactory() {
        List<Coin> coinList = coinService.findAll();
        CoinExchangeFactory factory = new CoinExchangeFactory();
        coinList.forEach(coin ->
                factory.set(coin.getUnit(), new BigDecimal(coin.getUsdRate()), new BigDecimal(coin.getCnyRate()))
        );
        return factory;
    }
}
