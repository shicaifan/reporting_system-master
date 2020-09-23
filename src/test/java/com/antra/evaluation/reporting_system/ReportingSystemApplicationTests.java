package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;

import com.antra.evaluation.reporting_system.service.CreateExcelDataService;
import com.antra.evaluation.reporting_system.service.CreateMultiExcelDataService;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportingSystemApplicationTests {

    @Autowired
    ExcelGenerationService reportService;
    @Autowired
    CreateExcelDataService createExcelDataService;
    @Autowired
    CreateMultiExcelDataService createMultiExcelDataService;
    @Autowired
    ExcelService excelService;

    ExcelData data = new ExcelData();
    ExcelRequest request = new ExcelRequest();
    MultiSheetExcelRequest multiSheetExcelRequest = new MultiSheetExcelRequest();


    @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
    public void setUpData() {
        data.setTitle("Test book");
        data.setGeneratedTime(LocalDateTime.now());

        var sheets = new ArrayList<ExcelDataSheet>();
        var sheet1 = new ExcelDataSheet();
        sheet1.setTitle("First Sheet");

        var headersS1 = new ArrayList<ExcelDataHeader>();
        ExcelDataHeader header1 = new ExcelDataHeader();
        header1.setName("NameTest");
        // header1.setWidth(10000);
        header1.setType(ExcelDataType.STRING);
        headersS1.add(header1);

        ExcelDataHeader header2 = new ExcelDataHeader();
        header2.setName("Age");
        // header2.setWidth(10000);
        header2.setType(ExcelDataType.NUMBER);
        headersS1.add(header2);

        List<List<Object>> dataRows = new ArrayList<>();
        List<Object> row1 = new ArrayList<>();
        row1.add("Dawei");
        row1.add(12);
        List<Object> row2 = new ArrayList<>();
        row2.add("Dawei2");
        row2.add(23);
        dataRows.add(row1);
        dataRows.add(row2);

        sheet1.setDataRows(dataRows);
        sheet1.setHeaders(headersS1);
        sheets.add(sheet1);
        data.setSheets(sheets);

        var sheet2 = new ExcelDataSheet();
        sheet2.setTitle("second Sheet");
        sheet2.setDataRows(dataRows);
        sheet2.setHeaders(headersS1);
        sheets.add(sheet2);
    }

    @Test
    public void testExcelGegeration() {
        File file = null;
        try {
            file = reportService.generateExcelReport(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(file != null);
    }

    @BeforeEach
    public void setUpRequest() {
        List<String> headers = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        List<Object> subObj1 = Arrays.asList("Shicai_1","25");
        List<Object> subObj2 = Arrays.asList("Shicai_2","26");
        headers.add("Name");
        headers.add("Age");

        data.add(subObj1);
        data.add(subObj2);
        request.setHeaders(headers);
        request.setData(data);
    }

    @Test
    public void testCreateExcelData(){
        ExcelFile excelFile= null;
        excelFile = createExcelDataService.createExcelData(request);
        assertTrue(excelFile!=null);
    }

   @BeforeEach
    public void setUpMultiExcelRequest() {
        List<String> headers = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        List<Object> subObj1 = Arrays.asList("Shicai_1","25","A");
        List<Object> subObj2 = Arrays.asList("Shicai_2","26","B");
        String splitBy = "Class";
        headers.add("Name");
        headers.add("Age");
        headers.add("Class");
        data.add(subObj1);
        data.add(subObj2);
        multiSheetExcelRequest.setHeaders(headers);
        multiSheetExcelRequest.setData(data);
        multiSheetExcelRequest.setSplitBy(splitBy);
    }


    @Test
    public void createMultiExcelData(){
        ExcelFile excelFile= null;
        excelFile = createMultiExcelDataService.createMultiExcelData(multiSheetExcelRequest);
        assertTrue(excelFile!=null);
    }


}
