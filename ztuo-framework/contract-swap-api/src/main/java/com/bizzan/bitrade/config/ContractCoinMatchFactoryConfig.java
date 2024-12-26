package com.bizzan.bitrade.config;

import com.bizzan.bitrade.engine.ContractCoinMatchFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 该配置类的作用是提供一个 ContractCoinMatchFactory 的实例作为 Spring Bean。通过使用 @Bean 注解，Spring 将 getContractCoinMatchFactory() 方法的返回值作为一个管理的 Bean，能够在需要时被其他组件注入。

 主要功能：

 工厂类实例化： 通过 ContractCoinMatchFactory 类实例化一个工厂对象。
 Spring Bean 管理： 通过 @Bean 注解将该工厂对象注册为 Spring 管理的 Bean，方便其他地方进行依赖注入和使用。
 日志记录： @Slf4j 注解准备好日志记录功能，尽管代码中没有使用日志，但可以在未来的开发中用来记录信息或调试日志。
 用途：

 ContractCoinMatchFactory 可能与合约、币种匹配的业务逻辑相关，可能是用于创建或管理某种特定的合约币种匹配策略，或者是提供匹配对象的工厂方法。
 */
@Configuration
@Slf4j
public class ContractCoinMatchFactoryConfig {
    @Bean
    public ContractCoinMatchFactory getContractCoinMatchFactory() {

        ContractCoinMatchFactory factory = new ContractCoinMatchFactory();
        return factory;

    }
}
