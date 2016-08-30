package com.haogre.pay.wechat.utils;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author haoz
 * 
 * @date 2016-8-30
 *
 */
public class WeChatUtils {
	
	//通过code获取openId
	public static String getOpenId(String code){
		if(StringUtils.isNotEmpty(code)){
			String appid = Configure.getAppid();
			String secret = Configure.getAppSecret();
			String result = HttpUtil.doGet(HttpConf.get(HttpConf.WECHAT_AUTH_TOKEN_URL)+"?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code", null, "UTF-8", true);
			if(StringUtils.isNotEmpty(result)){
				JSONObject json = JSONObject.parseObject(result);
				if(json.get("openid")!=null){
					return json.get("openid").toString();
				}
			}
		}
		return "";
	}
	
}
