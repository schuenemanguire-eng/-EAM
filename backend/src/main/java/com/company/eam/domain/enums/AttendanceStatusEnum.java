package com.company.eam.domain.enums;

public enum AttendanceStatusEnum {
    NORMAL("正常"), LATE("迟到"), EARLY_LEAVE("早退"), ABSENT("缺卡");

    private final String desc;

    AttendanceStatusEnum(String desc){this.desc=desc;}

    public String getDesc(){return desc;}
}
