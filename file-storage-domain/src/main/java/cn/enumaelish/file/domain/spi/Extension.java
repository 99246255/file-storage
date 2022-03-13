package cn.enumaelish.file.domain.spi;

import java.lang.annotation.*;

/**
 * 扩展接口实现类的标识
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Extension {
    /**
     * 在 core-spi 中不起作用，仅用作 alias 标识
     *
     * @return alias
     */
    String value();
}