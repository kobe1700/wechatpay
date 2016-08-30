package com.haogre.pay.wechat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author haoz
 * 
 * @date 2016-8-30
 *
 */
public class HttpUtil {
	static Logger log =Logger.getLogger(HttpUtil.class);
	static HttpClient client = new HttpClient();
	public static String getString(String url)  {
		try {
			HttpMethod method = new GetMethod(url);
			client.executeMethod(method);
			log.debug(method.getStatusLine());// 打印服务器返回的状态
			InputStream inputStream = method.getResponseBodyAsStream();// 打印返回的信息
			String responseData = inputStream2String(inputStream);
			responseData = UnicodeDecoder.decodeUnicode(responseData);
			method.releaseConnection();// 释放连接		
			return responseData;
		} catch (Exception e) {
			return null;
		}
		
	}
	public static JSONObject getJSONObject(String url)  {
		String text = getString(url);
		return text==null?null:JSON.parseObject(text);		
		
	}
	public static String postString(String url,Map<String,Object> params)  {
		try {
			HttpMethod method = new PostMethod(url);
			//HttpMethodParams params=new HttpMethodParams();		
			//method.setParams(params);
			client.executeMethod(method);
			log.debug(method.getStatusLine());// 打印服务器返回的状态
			InputStream inputStream = method.getResponseBodyAsStream();// 打印返回的信息
			String responseData = inputStream2String(inputStream);
			responseData = UnicodeDecoder.decodeUnicode(responseData);
			method.releaseConnection();// 释放连接		
			return responseData;
		} catch (Exception e) {
			return null;
		}
		
	}
	public static JSONObject postJSONObject(String url,Map<String,Object> params)  {
		String text=postString(url, params);
		return text==null?null:JSON.parseObject(text);
	}
	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	/**
	 * 执行一个http/https get请求，返回请求响应的文本数据
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的文本数据
	 */
	public static String doGet(String url, String queryString, String charset,
			boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https",
					new MySSLProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		HttpMethod method = new GetMethod(url);
		try {
			if (StringUtils.isNotBlank(queryString))
				// 对get请求参数编码，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			log.error("执行Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
		} catch (IOException e) {
			log.error("执行Get请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}
	/**
	 * post请求带json格式参数，返回字符串
	 * @author mengjingji
	 * @date 2016年8月16日
	 * @param url
	 * @param josnText
	 * @param charset
	 * @param pretty
	 * @return
	 */
	public static String doPost(String url, String josnText,
			String charset, boolean pretty){
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https",
					new MySSLProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		PostMethod method = new PostMethod(url);
		// 设置参数的字符集
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				charset);
		// 设置post数据
		if(josnText != null && !josnText.trim().equals("")) {
	        RequestEntity requestEntity;
			try {
				requestEntity = new StringRequestEntity(josnText,"text/xml",charset);
				method.setRequestEntity(requestEntity);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("执行Post请求" + url + "时，发生异常！", e);
			}
	        
	      }
		
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * 执行一个http/https post请求，返回请求响应的文本数据
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的文本数据
	 */
	public static String doPost(String url, Map<String, String> params,
			String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https",
					new MySSLProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		PostMethod method = new PostMethod(url);
		// 设置参数的字符集
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				charset);
		// 设置post数据
		if (params != null) {
			// HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// p.setParameter(entry.getKey(), entry.getValue());
				method.setParameter(entry.getKey(), entry.getValue());
			}
			// method.setParams(p);
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * 执行一个http/https post请求， 直接写数据 json,xml,txt
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的文本数据
	 */
	public static String writePost(String url, String content, String charset,
			boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https",
					new MySSLProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		PostMethod method = new PostMethod(url);
		try {
			// 设置请求头部类型参数
			// method.setRequestHeader("Content-Type","text/plain; charset=utf-8");//application/json,text/xml,text/plain
			// method.setRequestBody(content);
			// //InputStream,NameValuePair[],String
			// RequestEntity是个接口，有很多实现类，发送不同类型的数据
			RequestEntity requestEntity = new StringRequestEntity(content,
					"text/plain", charset);// application/json,text/xml,text/plain
			method.setRequestEntity(requestEntity);
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (Exception e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	public static String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}
	
	
	public static void main(String[] args) {
		try {
			String y = doGet("http://www.baidu.com", null,
					"GBK", true);
			System.out.println(y);
			// Map params = new HashMap();
			// params.put("param1", "value1");
			// params.put("json", "{\"aa\":\"11\"}");
			// String j =
			// doPost("http://localhost/uplat/manage/test.do?reqCode=add",
			// params, "UTF-8", true);
			// System.out.println(j);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
