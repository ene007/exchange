package com.bizzan.bitrade.client;


import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java_websocket.client.WebSocketClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/***
 * 主要用于与 WebSocket 服务器建立安全连接，尤其是在涉及到 SSL/TLS 加密时。
 * 该代码实现了一个名为 Client 的类，它包含了一个 connect 方法
 * ，用于通过 WebSocket 连接到服务器，并配置了一个忽略 SSL 证书验证的信任管理器
 */
@Service
@Scope(SCOPE_PROTOTYPE)
public class Client {

    public void connect(WebSocketClient ws) {
        try {
            // 请求连接
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }
            } };
            SSLContext sc = SSLContext.getInstance("TLS");
            // 创建WebSocket工厂
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory factory = sc.getSocketFactory();
            ws.setSocketFactory(factory);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        // 进行连接
        ws.connect();
    }
}