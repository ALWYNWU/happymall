package com.happymall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.happymall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000119643033";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCaTzKkaJJdccLJgShDGzhyWGwQkPmaKvNx965gmSKHkz3Aa06OdviM2QWv4T8NzlTDNLtk1dvK3sndyYhVi+JkS2fjT8PCJSveIGCJ7KcOvewa6uJ1NY+G3kiazY3mFhLru4ndu4HXfx4XnnmzpTcVtXMxoRGbkKcJPkL7m2Xc2fvskHYic1Tuk8LbNr2mv1TzGIYEb+j4nUQTPV4h8SaHiENmyvXo3CUNSI0FOKnwoqLY7ir6yhtV5hQNw6eDEHAQh495d9lGX5zyhHqD58B+QTqHsepnZLPCGJBIqJxXDvm0/iJasO3iY+SCag+ktH/wn+E1L+WKDTU8pbbVijqtAgMBAAECggEABGCIvDcnJ7VpZBPg0357LGsrrZCfM+tR+oKfFKhjXVsk3kds9AeV51Dko2eNRVP7/YmKY0LZuDh3m23zCxIrutKUwNLrZ59sTiG2+1KWLonen6jYAaEiUB4QCywXgVZQ9RTdxUdjWHyHmv3oz9q45EnAWcEK4dHJmy6D8eUnj4soAhcOqoKLrIoUrxY8X4fgss6x17K1M2mViY76Rbi+nQoTcIoN64ox9GifNzE3MUOtKH6a2PbbdnOPyYWdWHnQOYWtb/J+DVznIS6NfOGkOL1RNFwsQZI2NlEObKQ96BeDhzzMuUx5UsnHgO2KKRrqRe4NYb8gLrJg4ALVixuzNQKBgQDkv/MDnGexHaLGDqig9pHPxlYuHgYMINs4Ek4iZSnCAB1nT1VXLVUyxPrUfb4XPtwtzdynct3g5bkCYTZpVQFDwcZJZTSSYm5ZnCLw1T115ehUaYS5WBwms/FnHl32aWf0YBShNjPnOEsRIJhCK2SGFblQG8tLROd7XZlN+NwzlwKBgQCssRTfyTfRqVCqA1/HV1JBvbZF8aE8Qhk5JwJCqgxWpnHRIMFvJCtwnZp55b/s086pWVHGysCEEdeoLYd6NVX/F9Khbni1NvygAOhmXoBGr9I6TVvKOgvZ9iecDj660GZ85Zk5y8bjycF6dnhHq5DI2irTxxx0AvhXWynwyce8WwKBgGOzFaP5Q2h61jskoHKnhR3eACnRhCk9GM7zi4KKPQ0FJ8ujVDrO01dxazG6LGPNTgHYu9dvxS0EcwKKhECcgBScHGRY6/TxWc9Msh3tkwAT2JMR+nsd4AcuXoTnQ2TmxBxK1DL/gNfCPVWVs0cDyqh2R3KW6qA/w/SM8wh88ty5AoGAWG512gMYamzhua3wpgu5O6ECbd7GSi0u7W1kYu7hUp35OrYjV96VwB10tWdJWOU9tsrebYX1/6uDW8O01Ifrea2hVrlTZri5tNXCLGnIFmqCpQhoMK540yySK/+/b7q4QMzyIf6P4qW2RhfUkB7zDWdq9vfHQkEjGti8KWmqtnECgYEAr4ztO2trqtscBYPloD9xkNHgaT1Ze3vNhMRyqCEL3Ub3RAWU4poIUitLe/IDuL3MCpO74ijAaLtBV0GM1vQ9ws6csqM8P5U6nyNz7oreCNEmmDKjdii2EpZ9Mp7ATPnfyzRP2SCw/R4UCmQpiW8Lc2TrsfE8w3rUfMMfB0DEwmg=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5W5q0iUNCQUj3sQhOgtrbBdGJ/elNDcOoDInHCQiFLLImlu8sRG0SoPE9PuKz+Dco8ZhO3FLqubwNdd35EWVep4eZ/qGiUvJChiwM/VJ6S/jbZ/GyyZl0QRY0PbL2m2UbwnVIaJbF143oGVlH0b5oudmfouvH3ADVN1IU4KGZsPALtOsjRz8FaY/J5NROPydA/1hQkzea0BTHE3oJoPmIJswfhXGnyGX+yqEhAiRqD/JvWHjorb4lerMUSZzfJIm321+WKQjRXYcSiLiQIG4rWY4RlQ2K76P+YOyhiYlQaRRLHGYyMnimYcpRTy2IIepmAj0V1FQPRUcDyZFgw7EGwIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url ="https://62pm139512.zicp.fun/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url ="http://member.happymall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"30m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
