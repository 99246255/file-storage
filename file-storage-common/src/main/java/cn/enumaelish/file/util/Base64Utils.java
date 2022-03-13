package cn.enumaelish.file.util;

import java.util.Base64;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 10:04
 * @Description: BASE64编码解码工具包
 */
public class Base64Utils {

	/** *//**
	 * <p>
	 * BASE64字符串解码为二进制数据
	 * </p>
	 *
	 * @param base64
	 * @return
	 * @throws Exception
	 */
	public static byte[] decode(String base64) {
		return Base64.getDecoder().decode(base64);
	}

	/** *//**
	 * <p>
	 * 二进制数据编码为BASE64字符串
	 * </p>
	 *
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encode(byte[] bytes) {
		return new String(Base64.getEncoder().encode(bytes));
	}

}