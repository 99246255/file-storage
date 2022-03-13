package cn.enumaelish.file.util;

import com.alibaba.cola.exception.BizException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;

public class EncryptUtil {

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param key     加密的密钥
     * @return19
     */
    public static byte[] encrypt(byte[] content, String key) {
        try {
            DESKeySpec desKey = new DESKeySpec(key.getBytes(StandardCharsets.US_ASCII));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv2 = new IvParameterSpec(key.getBytes(StandardCharsets.US_ASCII));
            cipher.init(Cipher.ENCRYPT_MODE, securekey, iv2);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Throwable e) {
           throw new BizException("加密失败",e);
        }
    }

    public static byte[] desDecrypt(byte[] encryptText, String key){
        byte[] decryptedData = new byte[0];
        try {
            DESKeySpec desKey = new DESKeySpec(key.getBytes(StandardCharsets.US_ASCII));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv2 = new IvParameterSpec(key.getBytes(StandardCharsets.US_ASCII));
            cipher.init(Cipher.DECRYPT_MODE, securekey, iv2);
            byte[] encryptedData = encryptText;
            decryptedData = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new BizException("解密失败",e);
        }
        return decryptedData;
    }
}
