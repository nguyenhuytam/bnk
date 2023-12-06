package com.example.demo_test.dto.request;

import com.example.demo_test.model.Employee;
import lombok.Data;

@Data
public class EmployeeRequest extends Employee {
    private String positionName;
    private String fileType;

    public String getFileType() {
        return fileType;
    }
}
