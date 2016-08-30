package com.haogre.pay.wechat.utils;

import java.io.Serializable;
import java.security.MessageDigest;
/**
 * <H1>加密工具类</H1>
 * 
 * <p>登录密码MD5&SHA1加密</p>
 * 
 * @author haoz
 * @date 2016-8-30
 * @version 1.0
 */
public class EncoderHandler implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final String ALGORITHM = "MD5";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * encode string
	 * @param algorithm
	 * @param str
	 * @return String
	 */
	public static String encode(String algorithm, String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * encode By MD5
	 *
	 * @param str
	 * @return String
	 */
	public static String encodeByMD5(String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 *
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
	/**
	 * 先进行MD5摘要，在进行SHA1加密
	 * @param str
	 * @return
	 */
	public static String encodeByMD5AndSHA1(String str){
		return encode("SHA1",encodeByMD5(str));
	}

	public static void main(String[] args) {
		System.out.println("111111 MD5  :"
				+ EncoderHandler.encodeByMD5("111111"));
		System.out.println("111111 MD5  :"
				+ EncoderHandler.encode("MD5", "111111"));
		System.out.println("111111 SHA1 :"
				+ EncoderHandler.encode("SHA1", "111111"));
		System.out.println("96e79218965eb72c92a549dd5a330112 SHA1 :"
				+ EncoderHandler.encode("SHA1", "96e79218965eb72c92a549dd5a330112"));
	}

}