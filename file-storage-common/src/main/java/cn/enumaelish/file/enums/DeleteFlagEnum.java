package cn.enumaelish.file.enums;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 20:20
 * @Description: 删除状态
 */
public enum DeleteFlagEnum {

    /**
     * 删除
     */
    DELETE(1),
    /**
     * 未删除
     */
    NOT_DELETE(0);

    private int isDelete;
    DeleteFlagEnum(int i) {
        isDelete = i;
    }

    public int getIsDelete() {
        return isDelete;
    }

}
