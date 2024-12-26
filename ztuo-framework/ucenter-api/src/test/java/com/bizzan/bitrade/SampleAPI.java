package com.bizzan.bitrade;

import com.sparkframework.security.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UcenterApplication.class)
public class SampleAPI {
    /**
     * https://dm.aliyuncs.com/?Action=SingleSendMail
     * &AccountName=test@example.com
     * &ReplyToAddress=true
     * &AddressType=1
     * &ToAddress=test1@example.com
     * &Subject=Subject
     * &HtmlBody=body
     * &<公共请求参数>
     * <p>
     */
    @Test
    public void testSingleSendMail() {
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put("AccessKeyId", AccessKeyId);
        params.put("Action", "SingleSendMail");
        params.put("Format", Format);
        params.put("RegionId", RegionId);
        params.put("SignatureMethod", SignatureMethod);
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("SignatureVersion", SignatureVersion);
        params.put("Timestamp", getUTCTimeStr());
        params.put("Version", Version);
        params.put("AccountName", AccountName);
        params.put("AddressType", AddressType);
        params.put("HtmlBody", HtmlBody);
        params.put("ReplyToAddress", ReplyToAddress);
        params.put("Subject", Subject);
        params.put("TagName", TagName);
        params.put("ToAddress", ToAddress);
        Long start = System.currentTimeMillis();
        httpRequestSendEmail(params);
        System.out.println("耗时 ： " + (System.currentTimeMillis() - start));
    }

    public String httpRequestSendEmail(Map<String, Object> params) {
        String result = null;
        try {
            params.put("Signature", getSignature(prepareParamStrURLEncoder(params), method));
            String param = prepareParamStrURLEncoder(params);
            String url = protocol + "://" + host + "/?" + param;
            System.out.println("-----url-----" + url);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = null;
            if (method.equals(HttpMethod.GET)) {
                HttpGet request = new HttpGet(url);
                response = httpClient.execute(request);
            } else {
                HttpPost request = new HttpPost(url);
                response = httpClient.execute(request);
            }
            System.out.println(response);
            if (null != response) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-----httpRequestSendEmail result-----" + result);
        return result;
    }

    private final static String protocol = "https";
    //dm.ap-southeast-1.aliyuncs.com//dm.ap-southeast-2.aliyuncs.com
    private final static String host = "dm.aliyuncs.com";
    private final static String AccessKeyId = "LTAI4GBStjUx1Yv7GLb1iKNt";
    private final static String AccessKeySecret = "0jjTIxgw55027yohSE6il2LrJrTUnj";
    //发信地址
    private final static String AccountName = "service@bizzan.vip";
    //收信地址
    private final static String ToAddress = "422432886@qq.com";
    private final static String Format = "JSON";
    private final static String SignatureMethod = "HMAC-SHA1";
    private final static String SignatureVersion = "1.0";
    //2017-06-22
    private final static String Version = "2015-11-23";
    private final static String AddressType = "1";
    //ap-southeast-1//ap-southeast-2
    private final static String RegionId = "cn-hangzhou";
    private final static Boolean ReplyToAddress = Boolean.TRUE;
    private final static String HtmlBody = "<html><body><img alt=\"\" src=\"https://goss4.vcg.com/creative/vcg/400/new/VCG211173951082.jpg\" ><h3>Test send to email （）！</h3></body></html> <a%b' + * %7E> 测试邮件正文。你此次申请注册的验证码为：123456";
    private final static String Subject = "测试主题";
    private final static String TagName = "测试Tag";
    private final static HttpMethod method = HttpMethod.POST;

    public String prepareParamStrURLEncoder(Map<String, Object> params) {
        try {
            StringBuffer param = new StringBuffer();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (StringUtils.isBlank(entry.getKey()) || null == entry.getValue()) {
                    continue;
                }
                param.append(getUtf8Encoder(entry.getKey()) + "=" + getUtf8Encoder(entry.getValue().toString()) + "&");
            }
            return param.substring(0, param.lastIndexOf("&"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取签名
     *
     * @param param
     * @param method
     * @return
     * @throws Exception
     */
    private String getSignature(String param, HttpMethod method) throws Exception {
        String toSign = method + "&" + URLEncoder.encode("/", "utf8") + "&"
                + getUtf8Encoder(param);
        byte[] bytes = HmacSHA1Encrypt(toSign, AccessKeySecret + "&");
        return Base64.encode(bytes);
    }

    private String getUtf8Encoder(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, "utf8")
                .replaceAll("\\+", "%20")
                .replaceAll("\\*", "%2A")
                .replaceAll("%7E", "~");
    }

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return mac.doFinal(text);
    }

    private static DateFormat daetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 得到UTC时间，类型为字符串，格式为"yyyy-MM-dd HH:mm"<br />
     * 如果获取失败，返回null
     *
     * @return
     */
    public static String getUTCTimeStr() {
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        String date = daetFormat.format(cal.getTime());
        System.out.println("时间------" + date);
        String[] strs = date.split(" ");
        return strs[0] + "T" + strs[1] + "Z";
    }
}
