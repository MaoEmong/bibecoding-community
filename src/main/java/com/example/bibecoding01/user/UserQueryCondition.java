package com.example.bibecoding01.user;

import com.example.bibecoding01.employee.EmployeeStatus;
import lombok.Data;

@Data
public class UserQueryCondition {

    private String employeeNo;
    private String name;
    private Long departmentId;
    private Long positionId;
    private EmployeeStatus status;
    private int page = 0;
    private int size = 10;

    public void setPage(int page) {
        this.page = Math.max(page, 0);
    }

    public void setSize(int size) {
        this.size = size <= 0 ? 10 : Math.min(size, 50);
    }
}
