package cn.enumaelish.file.domain.ability;

/**
 * @author: EnumaElish
 * @Date: 2022/2/25 20:32
 * @Description: 实现需注意别将接口的四个方式也序列化了
 */
public interface StoragePlatformConfig {

	/**
	 * 加密,此方法存储时使用
	 */
	String encrypt();

	/**
	 * 此方法在从数据库读取后使用
	 */
	void desDecrypt();

	/**
	 * 反序列化
	 * @param config
	 */
	void deserialize(String config);
}
