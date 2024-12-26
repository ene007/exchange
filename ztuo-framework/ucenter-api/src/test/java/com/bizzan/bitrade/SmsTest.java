package com.bizzan.bitrade;

import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcenterApplication.class)
public class SmsTest {

    @Autowired
    private SMSProvider smsProvider;

    @Test
    public void test(){
        try {
            MessageResult messageResult = smsProvider.sendVerifyMessage("18911982963", "223456");
            System.out.println(messageResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInternational(){
        try {
            MessageResult messageResult = smsProvider.sendInternationalMessage("123456","1","9092355380");
            System.out.println(messageResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
