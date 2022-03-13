package cn.enumaelish.file.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 20:21
 * @Description: 文件状态
 */
public enum FileStatusEnum{


	/**
	 * 临时文件
	 */
	TEMPORARY(0,"临时文件"),
	/**
	 *  永久文件
	 */
	PERMANENT(1, "永久文件"),
	;


	private Integer value;
	private String desc;

	FileStatusEnum(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	static final Map<Integer, FileStatusEnum> MAP = new HashMap<Integer, FileStatusEnum>();
	static {
		for (FileStatusEnum c : FileStatusEnum.values()) {
			MAP.put(c.getValue(), c);
		}
	}

	public static FileStatusEnum getByValue(Integer value) {
		if(value == null){
			throw new IllegalArgumentException("找不到对应的错误");
		}
		if(MAP.containsKey(value)){
			return MAP.get(value);
		}else{
			throw new IllegalArgumentException("找不到对应的来源");
		}
	}

	public Integer getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	public static void main(String[] args) {
		StringBuilder stringBuilder = new StringBuilder();
		for (FileStatusEnum fileStatusEnum: values()){
			stringBuilder.append(fileStatusEnum.getValue()).append(fileStatusEnum.getDesc()).append(";");
		}
		System.out.println(stringBuilder.toString());
	}
}

