package com.company.eam.domain.enums;

public enum LeaveStatusEnum {
    PENDING("待审批"), APPROVED("已批准"), REJECTED("已拒绝");

    private final String desc;

    LeaveStatusEnum(String desc){this.desc=desc;}

    public String getDesc(){return desc;}
}
