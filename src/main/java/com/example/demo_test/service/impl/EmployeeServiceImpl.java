package com.example.demo_test.service.impl;

import com.example.demo_test.dto.request.EmployeeRequest;
import com.example.demo_test.dto.response.BaseResponse;
import com.example.demo_test.mapper.EmployeeMapper;
import com.example.demo_test.service.IEmployee;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo_test.utils.ExportUtil;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements IEmployee {

    @Autowired
    private EmployeeMapper mapper;
    @Override
    public BaseResponse getEmployee(EmployeeRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            List<EmployeeRequest> list =mapper.getEmployee(request);
            baseResponse.setData(list);
            baseResponse.setErrorCode(HttpStatus.OK.name());
            baseResponse.setErrorDesc("Danh sách Employye");
        }
        catch (Exception ex){
            baseResponse.setErrorDesc(HttpStatus.BAD_REQUEST.name());
            baseResponse.setErrorDesc("Error");
        }
        return baseResponse;
    }

    @Override
    public BaseResponse create(EmployeeRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        if(request.getName()!=null && request.getEmail() != null){
            EmployeeRequest check = mapper.checkName(request.getName(),request.getEmail());
            if (check != null ){
                baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
                baseResponse.setErrorDesc("UserName hoặc Email đã tồn tại :");
                return baseResponse;
            }
            List<EmployeeRequest> rs =mapper.create(request);
            if (rs != null ){
                baseResponse.setData(rs);
                baseResponse.setErrorCode(HttpStatus.OK.name());
                baseResponse.setErrorDesc("Thêm mới thành công");
            }
            else{
                baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
                baseResponse.setErrorDesc("Lỗi");
            }
            return baseResponse;
        }
        else {
            baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
            baseResponse.setErrorDesc("error");
            return baseResponse;
        }
    }

    @Override
    public BaseResponse delete(String id) {
        BaseResponse baseResponse = new BaseResponse();
        if (id != null && !id.isEmpty()) {
            int rs = mapper.delete(id);
            if (rs > 0) {
                baseResponse.setData(rs);
                baseResponse.setErrorCode(HttpStatus.OK.name());
                baseResponse.setErrorDesc(
                        "Delete Bank success");
            } else {
                baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
                baseResponse.setErrorDesc(
                        "Delete Bank failed");
                return baseResponse;
            }
        } else {
            baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
            baseResponse.setErrorDesc("Error");
            return baseResponse;
        }
        return baseResponse;
    }

    @Override
    public BaseResponse edit(EmployeeRequest request,String id) {
        BaseResponse baseResponse = new BaseResponse();
        if (request.getEmail() != null && request.getName() != null && request.getId() != null) {
            EmployeeRequest checkExist = mapper.checkName(request.getName(),request.getEmail());
            if (checkExist != null) {
                EmployeeRequest e = mapper.editedEmployee(request);
                    baseResponse.setData(e);
                    baseResponse.setErrorCode(HttpStatus.OK.name());
                    baseResponse.setErrorDesc("Cập nhập thành công");
                    return baseResponse;

            }
            else {
                List<EmployeeRequest> e = mapper.create(request);
                baseResponse.setData(e);
                baseResponse.setErrorCode(HttpStatus.OK.name());
                baseResponse.setErrorDesc("Thêm mới thành công");
                return baseResponse;
            }
        } else {
            baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
            baseResponse.setErrorDesc("Error");
            return baseResponse;
        }
    }

    @Override
    public BaseResponse getId(EmployeeRequest request) {
            BaseResponse baseResponse = new BaseResponse();
       List<EmployeeRequest> e = mapper.getId(request);
       if(e.size() > 0){
           EmployeeRequest employee = e.get(0);
           byte[] imgData = employee.getImgFile();
           request.setImgFile(imgData);
           baseResponse.setData(e);
           baseResponse.setErrorCode(HttpStatus.OK.name());
           baseResponse.setErrorDesc("Chi tiết");
       }
       else {
           baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
           baseResponse.setErrorDesc("Id không tồn tại");
       }
       return baseResponse;
    }

    @Override
    public BaseResponse search(EmployeeRequest request) {
        List<EmployeeRequest> check = mapper.search(request.getEmail(), request.getName());
        BaseResponse baseResponse = new BaseResponse();
        if (check !=null) {
            baseResponse.setData(check);
            baseResponse.setErrorCode(HttpStatus.OK.name());
            baseResponse.setErrorDesc("Kết quả");
        } else {
            baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
            baseResponse.setErrorDesc("Không có kết quả nào");
        }
        return baseResponse;
    }

    @Override
    public BaseResponse login(EmployeeRequest request) {
        BaseResponse baseResponse = new BaseResponse();
        if(request.getName() != null && request.getPassword() != null){
            EmployeeRequest check = mapper.login(request.getName(),request.getPassword());
            if (check != null){
                baseResponse.setErrorCode(HttpStatus.OK.name());
                baseResponse.setErrorDesc("Đăng nhập thành công");
                baseResponse.setData(check);
            }
            else {
                baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
                baseResponse.setErrorDesc("Sai tài khoản hoặc mật khẩu");
            }
        }else {
            baseResponse.setErrorCode(HttpStatus.BAD_REQUEST.name());
            baseResponse.setErrorDesc("Vui lòng nhập đầy đủ thông tin");
        }
        return baseResponse;
    }

    @Override
    public String export(String type) throws FileNotFoundException, JRException {
        String past = "C:\\Users\\tamhu\\Downloads";
        EmployeeRequest request = new EmployeeRequest();
        List<EmployeeRequest> export= mapper.getEmployee(request);
        File file = ResourceUtils.getFile("classpath:templates/employye.jrxml");
        JasperDesign jasperDesign;
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource =new JRBeanCollectionDataSource(export);
        Map<String,Object> map = new HashMap<>();
        map.put("CreateBy","JavaTechie");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,map,dataSource);
        if (type.equalsIgnoreCase("pdf")){
            JasperExportManager.exportReportToPdfFile(jasperPrint,past+"\\emloyee.pdf");
        }
        return "Export : " +past;
    }

    @Override
    public File exportPDF(EmployeeRequest request) {
        File file = null;
        try {
            file = File.createTempFile("out", ".tmp");
            file.deleteOnExit();
            Resource resource = new ClassPathResource("templates/Employee.jasper");

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 InputStream inputStream = resource.getInputStream()) {

                List<EmployeeRequest> list = mapper.getEmployee(request);
                if (!list.isEmpty()) {
                    list.add(0, new EmployeeRequest());
                }
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("language", "");
                ExportUtil.exportReport(inputStream, outputStream, parameters, list, "pdf");

                // Convert ByteArrayOutputStream to InputStream
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());

                // Use byteArrayInputStream as needed
                // For example, you can save it to a file
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = byteArrayInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                    }
                }

            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return file;
    }
}
