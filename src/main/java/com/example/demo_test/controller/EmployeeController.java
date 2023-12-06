package com.example.demo_test.controller;

import com.example.demo_test.dto.request.EmployeeRequest;
import com.example.demo_test.dto.response.BaseResponse;
import com.example.demo_test.mapper.EmployeeMapper;
import com.example.demo_test.service.impl.EmployeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping(path = "/employee")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl service;

    @GetMapping(value = "/getEmployee")
    public ResponseEntity<BaseResponse> getEmployee(){
        EmployeeRequest request = new EmployeeRequest();
        return new ResponseEntity<>(service.getEmployee(request), HttpStatus.OK);
    }

    @PostMapping(value = "/create", consumes = { "multipart/form-data" })
    public ResponseEntity<BaseResponse> create(@RequestParam(value = "file",required = false) MultipartFile file, @RequestParam("employee") String employeeJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeRequest employeeRequest = objectMapper.readValue(employeeJson, EmployeeRequest.class);
        if(file != null){
            employeeRequest.setImgFile(file.getBytes());
        }
        if (employeeRequest.getIdPosition() == null || employeeRequest.getIdPosition().trim().isEmpty()) {
            employeeRequest.setIdPosition("3");
        }
        return new ResponseEntity<>((service.create(employeeRequest)), HttpStatus.OK);
    }

    @PostMapping(value = "/delete{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id){
        return new ResponseEntity<>(service.delete(id),HttpStatus.OK);
    }
    @PostMapping(value = "/edit{id}" , consumes = { "multipart/form-data"} )
    public ResponseEntity<BaseResponse> editBank(@RequestParam(value = "file",required = false) MultipartFile file, @RequestParam("employee") String request,@PathVariable String id) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeRequest employeeRequest = objectMapper.readValue(request, EmployeeRequest.class);
        employeeRequest.setId(id);
        if (file != null){
            employeeRequest.setImgFile(file.getBytes());
        }
        return new ResponseEntity<>(service.edit(employeeRequest,employeeRequest.getId()), HttpStatus.OK);
    }

    @PostMapping(value = "getid{id}")
    public ResponseEntity<BaseResponse> getId(@PathVariable String id){
        EmployeeRequest request = new EmployeeRequest();
        request.setId(id);
        return new ResponseEntity<>(service.getId(request), HttpStatus.OK);
    }

    @GetMapping(value = "search")
    public  ResponseEntity<BaseResponse> search(@RequestParam(required = false) String name, @RequestParam(required = false) String email){
        EmployeeRequest request = new EmployeeRequest();
        if (name !=  null){
            request.setName(name);
        }
        if(email != null){
            request.setEmail(email);
        }
        return new ResponseEntity<>(service.search(request),HttpStatus.OK);
    }

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> login(@RequestBody EmployeeRequest user){
        return new ResponseEntity<>(service.login(user),HttpStatus.OK);
    }


    @PostMapping(value = "/exportPDF")
    public ResponseEntity<InputStreamResource> exportPDF() {
        EmployeeRequest request = new EmployeeRequest();
        try {
            File file = service.exportPDF(request);
            if (file == null) {
                throw new ServiceException("Nothing to export");
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/pdf"))  // Set the content type explicitly for PDF
                    .body(resource);
        } catch (FileNotFoundException e) {
            throw new ServiceException("Failed to export PDF: " + e.getMessage(), e);
        }
    }


    @PostMapping(value = "/export/{type}")
    public ResponseEntity<BaseResponse> export(@PathVariable String type) throws JRException, FileNotFoundException, UnsupportedEncodingException {
        if (type != null) {
            service.export(type);
            // Thực hiện xuất dữ liệu
            return new ResponseEntity<>( HttpStatus.OK);
        } else {
            // Trả về lỗi nếu type là null
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }
}
