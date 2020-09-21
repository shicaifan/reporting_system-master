package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataType;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.repo.ExcelRepositoryImpl;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.*;
import com.antra.evaluation.reporting_system.service.CreateExcelDataServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);
    private static int incrementalFileID = 1;

    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Autowired
    ExcelRepository excelRepository;

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) throws IOException {
        ExcelResponse response = new ExcelResponse();
        CreateExcelDataServiceImpl createExcelDataServiceImp = new CreateExcelDataServiceImpl();
        ExcelData excelData = createExcelDataServiceImp.createExcelData(request);
        ExcelGenerationServiceImpl excelGenerationService = new ExcelGenerationServiceImpl();
        excelGenerationService.generateExcelReport(excelData);

        response.setFileId(String.valueOf(incrementalFileID++));
        log.info("createExcel");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) throws IOException {
        ExcelResponse response = new ExcelResponse();
        CreateMultiExcelDataServiceImpl createMultiExcelDataServiceImp = new CreateMultiExcelDataServiceImpl();
        ExcelData excelData = createMultiExcelDataServiceImp.createMultiExcelData(request);
        ExcelGenerationServiceImpl excelGenerationService = new ExcelGenerationServiceImpl();
        excelGenerationService.generateExcelReport(excelData);

        response.setFileId(String.valueOf(incrementalFileID++));
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
        System.out.println(response);
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
}
// Log
// Exception handling
// Validation
