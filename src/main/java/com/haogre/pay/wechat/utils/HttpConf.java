package com.haogre.pay.wechat.utils;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * @author haoz
 * 
 * @date 2016-8-30
 *
 */
public class HttpConf {
	private static Logger log=Logger.getLogger(HttpConf.class);
	public static final String SEND_SYSTEM_MSG_URL="send_system_msg_url";
	public static final String HOME_CONTEXT_URL_BASE="home_context_url_base";
	/** 商城地址应用基准地址*/
	public static final String MALL_CONTEXT_URL_BASE="mall_context_url_base";
	/** 商品信息接口*/
	public static final String MATERIAL_INFO_API_URI="material_info_api_uri";
	/**微信获取授权token接口*/
	public static final String WECHAT_AUTH_TOKEN_URL="wechat_auth_token_url";
	/**静态应用基准地址*/
	public static final String STATIC_HOME_CONTEXT_URL_BASE="static_home_context_url_base";
	/**工人小熊头像地址*/
	public static final String BEAR_ICON_URI="bear_icon_uri";
	/**资源应用基准地址*/
	public static final String RES_HOME_CONTEXT_URL_BASE="res_home_context_url_base";
	/**商品搜索接口URI*/
	public static final String SEARCH_GOODS_URI="search_goods_uri";
	/**获取学名信息URI*/
	public static final String GET_MATERIA_NAME_INFO_BY_NAME="get_materia_name_info_by_name";
	
	private static ResourceBundle resourceBundle;
	static {
		resourceBundle = ResourceBundle.getBundle("http");		
	}
	
	public static String get(String constProperKey) {
		return resourceBundle.getString(constProperKey);
	}
	
	public static void main(String[] args) {
		log.debug(HttpConf.get(HttpConf.STATIC_HOME_CONTEXT_URL_BASE)+HttpConf.get(HttpConf.BEAR_ICON_URI));
	}

}
