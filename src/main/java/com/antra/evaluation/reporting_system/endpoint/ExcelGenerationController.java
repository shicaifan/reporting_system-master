package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.CreateExcelDataService;
import com.antra.evaluation.reporting_system.service.CreateMultiExcelDataService;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);

    ExcelService excelService;
    ExcelRepository excelRepository;
    CreateExcelDataService createExcelDataService;
    ExcelGenerationService excelGenerationService;
    CreateMultiExcelDataService createMultiExcelDataService;

    @Autowired
    public ExcelGenerationController(
            ExcelService excelService,
            ExcelRepository excelRepository,
            CreateExcelDataService createExcelDataService,
            ExcelGenerationService excelGenerationService,
            CreateMultiExcelDataService createMultiExcelDataService) {
        this.excelService = excelService;
        this.excelRepository = excelRepository;
        this.createExcelDataService = createExcelDataService;
        this.excelGenerationService = excelGenerationService;
        this.createMultiExcelDataService = createMultiExcelDataService;
    }

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody ExcelRequest request) throws IOException {
        ExcelResponse response = new ExcelResponse();
        if(!validationParams(request, null)){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        ExcelFile excelFile = createExcelDataService.createExcelData(request);
        excelGenerationService.generateExcelReport(excelFile.getExcelData());
        response.setFileId(excelFile.getFileId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody MultiSheetExcelRequest request) throws IOException {
        ExcelResponse response = new ExcelResponse();
        if(!validationParams(null, request)){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        ExcelFile excelFile = createMultiExcelDataService.createMultiExcelData(request);
        excelGenerationService.generateExcelReport(excelFile.getExcelData());
        response.setFileId(excelFile.getFileId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<List<ExcelResponse>> listExcels() {
        var response = new ArrayList<ExcelResponse>();
        List<ExcelFile> excelFiles = excelRepository.getFiles();
        for(ExcelFile excelFile: excelFiles) {
            ExcelResponse excelResponse = new ExcelResponse();
            excelResponse.setFileId(excelFile.getFileId());
            response.add(excelResponse);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream fis = excelService.getExcelBodyById(id);
        String fileId = excelRepository.getFileById(id).get().getFileId();//get fileId
        response.setHeader("Content-Type","application/vnd.ms-excel");
        response.setHeader("Content-Disposition","attachment; filename=" + fileId); // TODO: File name cannot be hardcoded here
        FileCopyUtils.copy(fis, response.getOutputStream());
    }

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) {
        var response = new ExcelResponse();
        ExcelFile excelFiles = excelRepository.deleteFile(id);
        if (excelFiles != null) {
            response.setFileId(id);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public boolean validationParams(ExcelRequest request, MultiSheetExcelRequest requestOfMulti){
        List<String> headers = request != null ? request.getHeaders() : requestOfMulti.getHeaders();
        List<List<Object>> data = request != null ? request.getData() : requestOfMulti.getData();
        if(requestOfMulti!=null){
           String splitBy = requestOfMulti.getSplitBy();
           if(splitBy.isEmpty() || splitBy.length()==0){
               log.info("Invalid splitBy");
               return false;
           }
        }
        if(headers==null || headers.size()==0){
            log.info("Empty headers");
            return false;
        }
        if(data==null || data.size()==0){
            log.info("Empty data");
            return false;
        }
        int col = headers.size();
        for(List<Object> row : data){
            if(col != row.size()) {
                log.info("Total Number of header isn't equals to the size of some rows in data");
                return false;
            }
        }
        return true;
    }
}
