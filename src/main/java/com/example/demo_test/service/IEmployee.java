package com.example.demo_test.service;


import com.example.demo_test.dto.request.EmployeeRequest;
import com.example.demo_test.dto.response.BaseResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileNotFoundException;

public interface IEmployee {
    BaseResponse getEmployee(EmployeeRequest request);
    BaseResponse create(EmployeeRequest request);
    BaseResponse delete(String id);
    BaseResponse edit(EmployeeRequest request , String id);
    BaseResponse getId(EmployeeRequest request);
    BaseResponse search(EmployeeRequest request);
    BaseResponse login(EmployeeRequest request);
    String export(String type) throws FileNotFoundException, JRException;
    File exportPDF(EmployeeRequest request);
}
