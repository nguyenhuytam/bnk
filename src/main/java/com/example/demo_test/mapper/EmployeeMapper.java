package com.example.demo_test.mapper;

import com.example.demo_test.dto.request.EmployeeRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface EmployeeMapper {
    List<EmployeeRequest> getEmployee(EmployeeRequest request);
    List<EmployeeRequest> create(EmployeeRequest request);
    EmployeeRequest checkName(@Param("name") String name,@Param("email") String email);
    int delete (@Param("id") String id);
    List<EmployeeRequest> getId (EmployeeRequest request);
    EmployeeRequest editedEmployee(EmployeeRequest request);

    int checkCodeUsed(@Param("email") String email, @Param("id") String id);
    List<EmployeeRequest> search(@Param("email") String email,@Param("name") String name);
    EmployeeRequest login (@Param("name") String name,@Param("password") String password);
}
