package com.company.eam.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("employee")
public class EmployeePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField("employee_no")
    private String employeeNo;

    @TableField("dept_id")
    private Long deptId;

    @TableField("position_id")
    private Long positionId;

    @TableField("entry_date")
    private LocalDate entryDate;

    private Integer status;

    private String phone;

    private String email;

    private String gender;

    private String birthday;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
