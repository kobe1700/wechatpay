package com.haogre.pay.wechat.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;

/**
 * @author haoz
 * 
 * @date 2016-8-30
 *
 */
@SuppressWarnings("deprecation")
public class WxHttpClientUtils {
	private static Logger log = Logger.getLogger(WxHttpClientUtils.class);
	//统一下单
	private static final String preOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	//接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
	private static final String notify_url = "http://dev.xxgj365.com/home/wxNotifyUrl";	
	//APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
	private static final String spbill_create_ip = "203.100.85.40";
	private static HttpClient client;
	
	@SuppressWarnings("rawtypes")
	public static String getPrePayId(Map<String, String> data){
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", data.get("appid"));
		params.put("openid", data.get("openid"));
		params.put("mch_id", data.get("mch_id"));
		params.put("is_subscribe", data.get("is_subscribe"));
		params.put("nonce_str", uuid);
		params.put("body", data.get("body"));
		params.put("total_fee", data.get("total_fee"));
		params.put("out_trade_no", data.get("product_id"));
		params.put("spbill_create_ip", spbill_create_ip);
		params.put("notify_url", notify_url);
		params.put("trade_type", "NATIVE");
		try {
			String sign = WxUrlUtils.getSignByParams(params);
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost(preOrderUrl);
			StringBuffer xml = new StringBuffer();
			xml.append("<xml>");
			xml.append("<appid>"+data.get("appid")+"</appid>");
			xml.append("<openid>"+data.get("openid")+"</openid>");
			xml.append("<mch_id>"+data.get("mch_id")+"</mch_id>");
			xml.append("<is_subscribe>"+data.get("is_subscribe")+"</is_subscribe>");
			xml.append("<nonce_str>"+uuid+"</nonce_str>");
			xml.append("<out_trade_no>"+data.get("product_id")+"</out_trade_no>");
			xml.append("<notify_url>"+notify_url+"</notify_url>");
			xml.append("<body>"+data.get("body")+"</body>");
			xml.append("<total_fee>"+data.get("total_fee")+"</total_fee>");
			xml.append("<spbill_create_ip>"+spbill_create_ip+"</spbill_create_ip>");
			xml.append("<trade_type>NATIVE</trade_type>");
			xml.append("<sign>"+sign+"</sign>");
			xml.append("</xml>");
			log.info(xml.toString());
			HttpEntity entity = new ByteArrayEntity(xml.toString().getBytes("UTF-8"));
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			String result = EntityUtils.toString(response.getEntity(),"UTF-8");
			
			//解析返回数据
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Map<String, String> recData = new HashMap<String, String>();
	        for(Iterator i = root.elementIterator(); i.hasNext();) {
	            Element element = (Element) i.next();
	            recData.put(element.getName(), element.getText());
	        }
	        log.info(JSON.toJSONString(recData, true));
	        return recData.get("prepay_id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 生成返回的xml
	 * @param prePayId
	 * @param return_code
	 * @param return_msg
	 * @param err_code_des
	 * @return
	 */
	public static String getReturnXml(String prePayId, String return_code, String return_msg, String err_code_des, String result_code) {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		Map<String, String> params = new HashMap<String, String>();
		params.put("return_code", return_code);
		params.put("return_msg", return_msg);
		params.put("appid", Configure.getAppid());
		params.put("mch_id", Configure.getMch_id());
		params.put("nonce_str", uuid);
		params.put("prepay_id", prePayId);
		params.put("result_code", result_code);
		params.put("err_code_des", err_code_des);
		try {
			String sign = WxUrlUtils.getSignByParams(params);
			client = new DefaultHttpClient();
			StringBuffer xml = new StringBuffer();
			xml.append("<xml>");
			xml.append("<return_code>"+return_code+"</return_code>");
			xml.append("<return_msg>"+return_msg+"</return_msg>");
			xml.append("<appid>"+Configure.getAppid()+"</appid>");
			xml.append("<mch_id>"+Configure.getMch_id()+"</mch_id>");
			xml.append("<nonce_str>"+uuid+"</nonce_str>");
			xml.append("<prepay_id>"+prePayId+"</prepay_id>");
			xml.append("<result_code>"+result_code+"</result_code>");
			xml.append("<err_code_des>"+err_code_des+"</err_code_des>");
			xml.append("<sign>"+sign+"</sign>");
			xml.append("</xml>");
	        return xml.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static String getPrePayIdH5(Map<String, String> orderParam) throws Exception{

		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

		Map<String, String> paraMap = new HashMap<String, String>();  
        paraMap.put("appid", Configure.getAppid());  
        paraMap.put("attach", orderParam.get("attach"));  
        paraMap.put("body", orderParam.get("body"));  
        paraMap.put("mch_id", Configure.getMch_id());  
        paraMap.put("nonce_str", uuid);  
        paraMap.put("openid", orderParam.get("openid"));  
        paraMap.put("out_trade_no", orderParam.get("out_trade_no"));  
        paraMap.put("spbill_create_ip", orderParam.get("ip"));  
        paraMap.put("total_fee", orderParam.get("total_fee"));  
        paraMap.put("trade_type", "JSAPI");  
        paraMap.put("notify_url", Configure.getSuccessBackUrl());// 此路径是微信服务器调用支付结果通知路径  
        log.info(JSON.toJSONString(paraMap));
		String sign = WxUrlUtils.getSignByParams(paraMap);
		log.info("sign11111111===   "+sign);
		paraMap.put("sign", sign); 
		
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost(preOrderUrl);
			String xml = mapToXml(paraMap);
			log.info("xml==========");
			log.info(xml);
			HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			String result = EntityUtils.toString(response.getEntity(),"UTF-8");
			//解析返回数据
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Map<String, String> recData = new HashMap<String, String>();
	        for(Iterator i = root.elementIterator(); i.hasNext();) {
	            Element element = (Element) i.next();
	            recData.put(element.getName(), element.getText());
	        }
	        log.info(JSON.toJSONString(recData, true));
	        return recData.get("prepay_id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("body", "测试订单");
		params.put("out_trade_no", "480");
		params.put("ip", "127.0.0.1");
		params.put("total_fee", "20");
		Map<String, String> map = getPrePayIdAndroid(params);
		System.out.println(map);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getPrePayIdAndroid(Map<String, String> orderParam) throws Exception{

		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

		Map<String, String> paraMap = new HashMap<String, String>();  
        paraMap.put("appid", Configure.getAndroidAppID());  
        paraMap.put("mch_id", Configure.getAndroid_mch_id());  
        paraMap.put("nonce_str", uuid);  
        paraMap.put("body", orderParam.get("body"));  
        paraMap.put("out_trade_no", orderParam.get("out_trade_no"));  
        paraMap.put("spbill_create_ip", orderParam.get("ip"));  
        paraMap.put("total_fee", orderParam.get("total_fee"));  
        paraMap.put("trade_type", "APP");  
        paraMap.put("notify_url", Configure.getSuccessBackUrlApp());// 此路径是微信服务器调用支付结果通知路径  
        log.info(JSON.toJSONString(paraMap));
		String sign = WxUrlUtils.getAndroidSignByParams(paraMap);
		log.info("sign11111111===   "+sign);
		paraMap.put("sign", sign); 
		
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost(preOrderUrl);
			String xml = mapToXml(paraMap);
			log.info("xml==========");
			log.info(xml);
			HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			String result = EntityUtils.toString(response.getEntity(),"UTF-8");
			//解析返回数据
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Map<String, String> recData = new HashMap<String, String>();
	        for(Iterator i = root.elementIterator(); i.hasNext();) {
	            Element element = (Element) i.next();
	            recData.put(element.getName(), element.getText());
	        }
	        log.info(JSON.toJSONString(recData, true));
	        return recData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String mapToXml(Map<String, String> param) {
        String xml = "<xml>";
        Iterator<Entry<String, String>> iter = param.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (IsNumeric(val)) {
                xml += "<" + key + ">" + val + "</" + key + ">";
            } else
            	xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
        }

        xml += "</xml>";
        return xml;
    }
	
	public static boolean IsNumeric(String str) {
        if (str.matches("\\d *")) {
            return true;
        } else {
            return false;
        }
    }
	
    public static Map<String,Object> getMapFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is =  getStringStream(xmlString);
        org.w3c.dom.Document document = builder.parse(is);

        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Node node;
        Map<String, Object> map = new HashMap<String, Object>();
        int i=0;
        while (i < allNodes.getLength()) {
            node = allNodes.item(i);
            if(node instanceof Element){
                map.put(node.getNodeName(),node.getTextContent());
            }
            i++;
        }
        return map;

    }
    
    public static InputStream getStringStream(String sInputString) {
        ByteArrayInputStream tInputStringStream = null;
        if (sInputString != null && !sInputString.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
        }
        return tInputStringStream;
    }
	
}
