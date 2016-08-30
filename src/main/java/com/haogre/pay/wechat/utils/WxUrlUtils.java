package com.haogre.pay.wechat.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
/**
 * @author haoz
 * 
 * @date 2016-8-30
 *
 */
public class WxUrlUtils {
	
	private static Logger log = Logger.getLogger(WxUrlUtils.class);
	/**
	 * 生成微信二维码链接文字
	 * @param product_id
	 * @return
	 * @throws Exception
	 */
	public static String generateWxQRCode(String product_id) throws Exception {
		String curTime = String.valueOf(new Date().getTime());
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", Configure.getAppid());
		params.put("mch_id", Configure.getMch_id());
		params.put("product_id", product_id);
		params.put("time_stamp", curTime);
		params.put("nonce_str", uuid);
		String sign = getSignByParams(params);
		StringBuffer sb = new StringBuffer();
		sb.append("weixin://wxpay/bizpayurl?");
		sb.append("appid=");
		sb.append(Configure.getAppid());
		sb.append("&mch_id=");
		sb.append(Configure.getMch_id());
		sb.append("&nonce_str=");
		sb.append(uuid);
		sb.append("&product_id=");
		sb.append(product_id);
		sb.append("&time_stamp=");
		sb.append(curTime);
		sb.append("&sign=");
		sb.append(sign);
		log.info(sb.toString());
		return sb.toString();
	}

	public static Map<String, String> generatePaySign(String prePayId) throws Exception {
		
		String curTime = String.valueOf(new Date().getTime());
		Map<String, String> paraMap = new HashMap<String, String>();  
		paraMap.put("appId", Configure.getAppid());  
		paraMap.put("timeStamp", curTime);  
		paraMap.put("nonceStr", Configure.getSuccessBackUrl());  
		paraMap.put("signType", "MD5");  
		paraMap.put("package", "prepay_id=" + prePayId); 
		String sign = getSignByParams(paraMap);
		paraMap.put("sign", sign);  
        
		return paraMap;
	}
	
	public static Map<String, String> generateAndroidPaySign(String prePayId) throws Exception {
		Long timestamp = System.currentTimeMillis() / 1000;
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		Map<String, String> paraMap = new HashMap<String, String>();  
		paraMap.put("appid", Configure.getAndroidAppID());   //appid
		paraMap.put("timestamp", timestamp.toString());  //时间戳  十位  
		paraMap.put("noncestr", uuid); //随机字符串
		paraMap.put("package", "Sign=WXPay");   //固定值
		paraMap.put("partnerid", Configure.getAndroid_mch_id());  //商户id（微信商户平台获取）
		paraMap.put("prepayid", prePayId);  //第一次请求微信，成功后，返回的参数
		String sign = getAndroidSignByParams(paraMap);  //生成签名
		paraMap.put("sign", sign);  
		
		return paraMap;
	}
	
	/**
	 * <b>微信签名算法</b>
	 * <p>
	 * 签名生成的通用步骤如下：
	 * </p>
	 * <p>
	 * 第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
	 * </p>
	 * <p>
	 * 特别注意以下重要规则：
	 * </p>
	 * <li>◆ 参数名ASCII码从小到大排序（字典序）；</li> <li>◆ 如果参数的值为空不参与签名；</li> <li>◆ 参数名区分大小写；</li> <li>◆
	 * 验证调用返回或微信主动通知签名时，传送的sign参数不参与签名，将生成的签名与该sign值作校验。</li> <li>◆ 微信接口可能增加字段，验证签名时必须支持增加的扩展字段</li>
	 * <p>
	 * 第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
	 * </p>
	 * 
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	protected static String getSignByParams(Map<String, String> params) throws Exception {
		List<String> emptyValueKey = new ArrayList<String>();
		//判断参数是否为空
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (StringUtils.isBlank(params.get(param.getKey()))) {
					emptyValueKey.add(param.getKey());
				}
			}
			//清除空值的参数
			for (String key : emptyValueKey) {
				params.remove(key);
			}
			//排序
			Map<String, String> treeMap = new TreeMap<String, String>(params);
			//拼接key
			String temp = "";
			for (Map.Entry<String, String> param : treeMap.entrySet()) {
				log.info(param.getKey() + ":" +param.getValue());
				temp += param.getKey() + "=" + param.getValue() + "&";
			}
			temp += "key=" + Configure.getKey();
			String sign = EncoderHandler.encodeByMD5(temp.toString()).toUpperCase();
			return sign;
		} else {
			throw new Exception("生成微信支付二维码链接参数不能为空");
		}
	}
	protected static String getAndroidSignByParams(Map<String, String> params) throws Exception {
		List<String> emptyValueKey = new ArrayList<String>();
		//判断参数是否为空
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (StringUtils.isBlank(params.get(param.getKey()))) {
					emptyValueKey.add(param.getKey());
				}
			}
			//清除空值的参数
			for (String key : emptyValueKey) {
				params.remove(key);
			}
			//排序
			Map<String, String> treeMap = new TreeMap<String, String>(params);
			//拼接key
			String temp = "";
			for (Map.Entry<String, String> param : treeMap.entrySet()) {
				log.info(param.getKey() + ":" +param.getValue());
				temp += param.getKey() + "=" + param.getValue() + "&";
			}
			temp += "key=" + Configure.getAndroidkey();
			String sign = EncoderHandler.encodeByMD5(temp.toString()).toUpperCase();
			return sign;
		} else {
			throw new Exception("生成微信支付二维码链接参数不能为空");
		}
	}
	/**
	 * 验证签名
	 * @param params
	 * @return
	 */
	public static boolean checkSign(Map<String, String> params) {
		List<String> emptyValueKey = new ArrayList<String>();
		String recSign = "";
		//判断参数是否为空
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (StringUtils.isBlank(params.get(param.getKey()))) {
					emptyValueKey.add(param.getKey());
				}
				//sign不参与验证
				if("sign".equals(param.getKey())){
					emptyValueKey.add(param.getKey());
					recSign = params.get(param.getKey());
				}
			}
			//清除空值的参数
			for (String key : emptyValueKey) {
				params.remove(key);
			}
			//排序
			Map<String, String> treeMap = new TreeMap<String, String>(params);
			//拼接key
			String temp = "";
			for (Map.Entry<String, String> param : treeMap.entrySet()) {
				log.info(param.getKey() + ":" +param.getValue());
				temp += param.getKey() + "=" + param.getValue() + "&";
			}
			temp += "key=" + Configure.getKey();
			String sign = EncoderHandler.encodeByMD5(temp.toString()).toUpperCase();
			if(sign.equals(recSign)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 验证签名
	 * @param params
	 * @return
	 */
	public static boolean checkAppSign(Map<String, String> params) {
		List<String> emptyValueKey = new ArrayList<String>();
		String recSign = "";
		//判断参数是否为空
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (StringUtils.isBlank(params.get(param.getKey()))) {
					emptyValueKey.add(param.getKey());
				}
				//sign不参与验证
				if("sign".equals(param.getKey())){
					emptyValueKey.add(param.getKey());
					recSign = params.get(param.getKey());
				}
			}
			//清除空值的参数
			for (String key : emptyValueKey) {
				params.remove(key);
			}
			//排序
			Map<String, String> treeMap = new TreeMap<String, String>(params);
			//拼接key
			String temp = "";
			for (Map.Entry<String, String> param : treeMap.entrySet()) {
				log.info(param.getKey() + ":" +param.getValue());
				temp += param.getKey() + "=" + param.getValue() + "&";
			}
			temp += "key=" + Configure.getAndroidkey();
			String sign = EncoderHandler.encodeByMD5(temp.toString()).toUpperCase();
			if(sign.equals(recSign)){
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String curTime = String.valueOf(new Date().getTime());
		String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", Configure.getAppid());
		params.put("mch_id", Configure.getMch_id());
		params.put("product_id", "000000000");
		params.put("time_stamp", curTime);
		params.put("nonce_str", uuid);
		params.put("FDSAF", "");
		params.put("XCWE", "11");
		try {
			System.out.println(getSignByParams(params));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
