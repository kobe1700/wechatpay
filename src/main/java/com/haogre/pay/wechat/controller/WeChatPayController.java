package com.haogre.pay.wechat.controller;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.glxn.qrgen.javase.QRCode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.haogre.pay.wechat.utils.WeChatUtils;
import com.haogre.pay.wechat.utils.WxHttpClientUtils;
import com.haogre.pay.wechat.utils.WxUrlUtils;


/**
 * 微信支付相关功能 公众号支付  扫码支付  APP支付暂不放出
 * @author haoz
 *
 * @date 2016-08-30
 */					   
@RequestMapping(value="/wechatpay")
@Controller
public class WeChatPayController{
	Logger logger=Logger.getLogger(this.getClass());
	
	
	@RequestMapping(value="/index")
	public String index(HttpServletRequest request,  HttpServletResponse response){
		
		return "/wechat/index";
	}
	
	/**
	 * 微信支付完成回调
	 * @param model
	 * @return
	 * 微信支付完成回调,返回的参数：
	 * -appid:wxdb6a4f2cf35d02fd                     
		-bank_type:CFT                                
		-cash_fee:1                                   
		-fee_type:CNY                                 
		-is_subscribe:Y                               
		-mch_id:1298512701                            
		-nonce_str:72A31F50E5774430818779BE4A01005A   
		-openid:oYwg6wHnHheojy4HtKZjDzZofem8          
		-out_trade_no:2016010506000551                
		-result_code:SUCCESS                          
		-return_code:SUCCESS                          
		-time_end:20160105162554                      
		-total_fee:1                                  
		-trade_type:NATIVE                            
		-transaction_id:1004090056201601052521982851  
	 */
	@RequestMapping(value="/wxNotifyUrl")
	public ModelAndView wxNotify(@RequestBody String wxData,HttpServletRequest request,  HttpServletResponse response){
		logger.info("微信支付完成回调,返回的参数：");
		try {
			String return_code = "FAIL";
			String return_msg = "";
			Map<String, String> map = new HashMap<String, String>();
			Document document = DocumentHelper.parseText(wxData);
			Element root = document.getRootElement();
			for(Iterator i = root.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				map.put(element.getName(), element.getText());
			}
			if(WxUrlUtils.checkSign(map)){
				logger.info("签名验证通过!");
				logger.info(JSONObject.toJSON(map));
				
				
				String trade_no = map.get("transaction_id");//交易号
				String out_trade_no = map.get("out_trade_no");//订单号
				
				logger.info("解析返回状态");
				if(map.get("return_code").equals("SUCCESS") && map.get("result_code").equals("SUCCESS")){
					logger.info("=============支付成功-更新业务状态start=============");
					return_code = "SUCCESS";
					return_msg = "ok";
					logger.info("=============支付成功-更新业务状态end=============");
				}
			} else {
				logger.error("支付成功返回数据，签名不合法");
			}
			String xml = "<xml><return_code>"+return_code+"</return_code><return_msg>"+return_msg+"</return_msg></xml>";
			response.getWriter().write(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
  	
	/**
	 * 跳转到微信支付
	 * @param orderId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/toPay/{orderId}")
	public String toPay(@PathVariable Long orderId,HttpServletRequest request,  HttpServletResponse response){
		String orginUrl = "http://www.xxgj365.com/home/wechatpay/"+orderId;
		String encodeUrl = URLEncoder.encode(orginUrl);
		String resultUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxdb6a4f2cf35d02fd&redirect_uri="+encodeUrl+"&response_type=code&scope=snsapi_base&state="+orderId+"#wechat_redirect";
		logger.info(resultUrl);
		return "redirect:"+resultUrl;
	}
	
	@RequestMapping(value="/{orderId}")
	public String payByOrderId(@PathVariable Long orderId,Model model ,HttpServletRequest request,  HttpServletResponse response) throws Exception{
		String openId = "";
		if (request.getParameter("code")!=null) {
			String code = request.getParameter("code").toString();
			logger.info("code====="+code);
			openId = WeChatUtils.getOpenId(code);
			logger.info("openId======================"+openId);
		}

		//orderId 为paymainId 
		logger.info(orderId+"<<<<<<<<<<<<<<<<<<<orderId");
		String attach = "测试订单attach";
		String body = "测试数据";
		
		
		model.addAttribute("payMainId", "20160830110001");//FIXME 测试订单id
	
		model.addAttribute("openId", "wxdb6a4f2cf35d02fd");//FIXME 商户id 替换为自己的订单id
		
		
		//调用微信支付统一下单接口
        Map<String, String> orderParam = new HashMap<String, String>();  
        orderParam.put("attach", attach);
        orderParam.put("body", body);
        orderParam.put("openid", openId);
        logger.info(orderId.toString());
        orderParam.put("out_trade_no", orderId.toString());
        orderParam.put("ip", request.getRemoteAddr());
        
        orderParam.put("total_fee", "1");//FIXME 测试数据 一分 

		String prePayId = WxHttpClientUtils.getPrePayIdH5(orderParam);
		
		logger.info("prePayId======"+prePayId);
	       //封装h5页面调用参数
		Map<String, String> paySign = WxUrlUtils.generatePaySign(prePayId);

        model.addAttribute("paytimestamp", paySign.get("timeStamp"));
        model.addAttribute("paypackage", "prepay_id="+prePayId);
        model.addAttribute("prePayId", prePayId);
        model.addAttribute("paynonceStr", paySign.get("nonceStr"));
        model.addAttribute("paysignType", "MD5");
        model.addAttribute("paySign",paySign.get("sign") );
		
        
		
		return "/wechat/payView";
	}
	
	
	@RequestMapping(value="/createWeChatOrder/{money}", method=RequestMethod.GET)
	public void createWeChatOrder(Model model, @PathVariable Float money, HttpServletResponse reponse){
		try {
			logger.info("提交微信订单");
				//保存订单
			String generateWxQRCode = WxUrlUtils.generateWxQRCode("2016083000001");
			ByteArrayOutputStream stream = QRCode.from(generateWxQRCode).withSize(160, 160).stream();
			ServletOutputStream outputStream = reponse.getOutputStream();
			stream.writeTo(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="wxPayBack")
	public ModelAndView wxPayBack(@RequestBody String wxData, HttpServletResponse response){
		logger.info("微信支付过程回调,返回的参数：");
		try {
			String return_code = "FAIL";
			String return_msg = "无效";
			String err_code_des = "此商品无效";
			String result_code = "FAIL";
			String prePayId = null;
			Map<String, String> map = new HashMap<String, String>();
			Document document = DocumentHelper.parseText(wxData);
			Element root = document.getRootElement();
			for(Iterator i = root.elementIterator(); i.hasNext();) {
			    Element element = (Element) i.next();
			    map.put(element.getName(), element.getText());
			}
            logger.info("payBack params:" + map);
			if(WxUrlUtils.checkSign(map)){
				logger.info("签名验证通过! 调用【统一下单API】提交支付交易");
				map.put("body", "测试body");
				map.put("total_fee", "1");//测试 1分
				prePayId = WxHttpClientUtils.getPrePayId(map);
				if(StringUtils.isNotBlank(prePayId)){
					return_code = "SUCCESS";
					result_code = "SUCCESS";
					return_msg = "";
					err_code_des = "";
				}
			} else {
				return_msg = "签名失败";
				err_code_des = "签名验证没有通过";
			}
			String xml = WxHttpClientUtils.getReturnXml(prePayId, return_code, return_msg, err_code_des, result_code);
			response.getWriter().write(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}