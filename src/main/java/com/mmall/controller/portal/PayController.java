package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IPayService;
import com.mmall.service.impl.PayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class PayController {

    private static Logger logger = LoggerFactory.getLogger(PayController.class);
    @Autowired
    IPayService iPayService;
    @RequestMapping(value = "pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMsg("用户未登录,无法获取当前用户信息");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");

        return iPayService.pay(orderNo,user.getId(), path);

    }

    @RequestMapping(value = "alipay_callback.do", method = RequestMethod.POST)
    @ResponseBody
    public Object callback(HttpServletRequest request) {
        logger.info("进入回调函数");
        Map<String, String> map = Maps.newHashMap();
        Map<String, String[]> requestParams = request.getParameterMap();
        for(Map.Entry<String, String[]> e : requestParams.entrySet()) {
            String name = e.getKey();
            String[] values = e.getValue();
            String valueStr = "";
            for(int i = 0; i < values.length; i++) {
                valueStr = (i==values.length-1) ? valueStr+values[i]:valueStr + values[i] +",";
            }
            map.put(name, valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",map.get("sign"),map.get("trade_status"),map.toString());
        map.remove("sign_type");

        try {
            boolean alipayCheckCallback = AlipaySignature.rsaCheckV2(map, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if(alipayCheckCallback == false) {
                logger.error("非法请求");
                return ServerResponse.createByErrorMsg("非法请求");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        ServerResponse serverResponse = iPayService.aliCallback(map);
        if(serverResponse.isSuccess()) return Const.AlipayCallback.RESPONSE_SUCCESS;
        return Const.AlipayCallback.RESPONSE_FAIL;
    }

    @RequestMapping(value = "query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryPayStatus(HttpSession session, Long orderNo, HttpServletRequest request) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMsg("用户未登录,无法获取当前用户信息");
        }
        ServerResponse serverResponse = iPayService.queryPayStatus(orderNo);
        if(serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    @RequestMapping(value = "create.do")
    @ResponseBody
    public ServerResponse createOrder(HttpSession session, Integer shippingId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMsg("用户未登录,无法获取当前用户信息");
        }

        return null;
    }
}
