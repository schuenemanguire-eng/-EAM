package com.company.eam.domain.enums;

public enum LeaveTypeEnum {
    ANNUAL("年假"), SICK("病假"), PERSONAL("事假"), MARITAL("婚假"), MATERNITY("产假"), BEREAVEMENT("丧假");

    private final String desc;

    LeaveTypeEnum(String desc){this.desc=desc;}

    public String getDesc(){return desc;}
}
