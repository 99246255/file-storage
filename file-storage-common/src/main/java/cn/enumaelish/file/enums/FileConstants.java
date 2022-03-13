package cn.enumaelish.file.enums;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/2/24 20:24
 * @Description: 微服务常量
 */
public interface FileConstants {

    String SERVICE_NAME = "file-storage";

    String VERSION = "v1";

    String SEPARATOR = "/";

    String INTERROGATION = "?";

    String PERIOD = ".";

    String AMPERSAND = "&";

    String PATH = SERVICE_NAME + SEPARATOR + "api" + SEPARATOR + VERSION + SEPARATOR;

    String HEADER_DATE = "Date";

    String HEADER_CONTENT_TYPE = "Content-Type";

    int SUCCESS_CODE = 200;

    /**
     * 文件值标识
     */
    String FILE_VALUE = "${file}";

    /**
     * 大部分的上传文件key
     */
    String FILE_KEY = "file";

    /**
     * 这个是为了前端方便与前端约定
     */
    String HTTP_METHOD_PUT = "put";

    /**
     * 这个是为了前端方便，七牛使用从POST改formPost
     */
    String HTTP_METHOD_POST = "formPost";

    /**
     * 永不过期的时间3000-1-1
     */
    Date NO_EXPIRE_TIME = new Date(32503651200000L);

    String FILE_ID_NOT_NULL = "文件标识不可为空";

    String FILE_ID_LENGTH_DESC = "文件标识长度限制1~40";

    /**
     * fileId最大长度
     */
    int FILE_ID_MAX_LENGTH = 40;
    /**
     * 不为空最小长度
     */
    int NOT_EMPTY_MIN_LENGTH = 1;

    /**
     * 名称类最大长度
     */
    int NAME_MAX_LENGTH = 50;

    /**
     * 文件名称最大长度255
     */
    int FILE_NAME_MAX_LENGTH = 255;

    /**
     * 文件类型最大长度255
     */
    int FILE_TYPE_MAX_LENGTH = 100;
    /**
     * 分片最小上传单位1M
     */
    public long ONE_M_SIZE = 1024*1024;

    public long ONE_HUNDRED_M_SIZE = 100 * ONE_M_SIZE;

    public long ONE_G_SIZE = 1024 * ONE_M_SIZE;

}
