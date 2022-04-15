import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayConstants;
import com.nossiac.jx.easypay.domain.AlipayConfig;
import com.nossiac.jx.easypay.domain.EasyPayRequest;
import com.nossiac.jx.easypay.domain.WxpayConfig;
import com.nossiac.jx.easypay.enums.EasyPayTypeEnum;
import com.nossiac.jx.easypay.service.EasyPayService;
import com.nossiac.jx.easypay.service.impl.EasyPayServiceImpl;

public class TestMain {

    public static void main(String args[]) {

        /**
         * *************************************************************************************************************
         * 1、配置参数
         * *************************************************************************************************************
         */
        //配置支付宝支付
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId("支付宝APPID");
        alipayConfig.setAppPrivateKey("商户私钥");
        alipayConfig.setAlipayPublicKey("支付宝公钥");
        alipayConfig.setNotifyUrl("异步通知URL");

        //配置微信支付
        WxpayConfig wxpayConfig = new WxpayConfig();
        wxpayConfig.setAppID("微信APPID");
        wxpayConfig.setMchID("微信MchId");
        wxpayConfig.setCertPath("微信支付证书路径（退款等需要用到）");
        wxpayConfig.setKey("微信支付Key");
        wxpayConfig.setSignType(WXPayConstants.SignType.HMACSHA256);//签名方式
        wxpayConfig.setNotifyUrl("异步通知URL");


        /**
         * *************************************************************************************************************
         * 2、创建easyPayService对象
         * *************************************************************************************************************
         */
        EasyPayService easyPayService = new EasyPayServiceImpl();
        easyPayService.setAlipayConfig(alipayConfig);//设置支付宝配置参数
        easyPayService.setWxpayConfig(wxpayConfig);//设置微信配置参数


        /**
         * *************************************************************************************************************
         * 3、填充支付参数
         * *************************************************************************************************************
         */

        EasyPayRequest easyPayRequest = new EasyPayRequest();
        easyPayRequest.setTradeNo("3130075961234343235");//订单号
        easyPayRequest.setAmount(0.01F);//支付金额
        easyPayRequest.setSubject("测试支付");//支付标题
        easyPayRequest.setBody("附加内容");//附加内容
        easyPayRequest.setOpenid("xxx");//微信JsApi等支付时必传
        easyPayRequest.setProductId("xxx");//微信Native支付时必传
        easyPayRequest.setTimeout(600);//设置超时时间
        easyPayRequest.setReturnUrl("http://xxxxx.html");//支付宝网页支付付款完后要跳转到的的页面URL
        easyPayService.setEasyPayRequest(easyPayRequest);//设置请求数据对象

        /**
         * *************************************************************************************************************
         * 4、发起支付
         * *************************************************************************************************************
         */
        easyPayService.setEasyPayType(EasyPayTypeEnum.WXPAY_JSAPI);//设置支付方式
        Object obj = easyPayService.pay();//发起支付
        System.out.println(JSON.toJSONString(obj));
    }
}
