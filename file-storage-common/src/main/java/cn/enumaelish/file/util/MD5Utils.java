package cn.enumaelish.file.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: EnumaElish
 * @Date: 2022/2/14 19:00
 * @Description:
 */
public class MD5Utils {

	/**
	 * 流转数组
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream input) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		try {
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("无法获取文件内容",e);
		}
		return output.toByteArray();
	}

	/**
	 * 获取md5，无法处理大文件，可能内存不够
	 * @param bytes
	 * @return
	 */
	public static String stringToMd5(byte[] bytes) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(
					bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有这个md5算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

	/**
	 * 获取一个文件的md5值(可处理大文件)
	 * @return md5 value
	 */
	public static String getMd5(InputStream fileInputStream) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				md5.update(buffer, 0, length);
			}
			String md5code = new BigInteger(1, md5.digest()).toString(16);
			for (int i = 0; i < 32 - md5code.length(); i++) {
				md5code = "0" + md5code;
			}
			return md5code;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
