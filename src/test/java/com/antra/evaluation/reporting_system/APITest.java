package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.service.CreateExcelDataService;
import com.antra.evaluation.reporting_system.service.CreateMultiExcelDataService;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class APITest {
    @Mock
    ExcelService excelService;
    @Mock
    ExcelRepository excelRepository;
    @Mock
    CreateExcelDataService createExcelDataService;
    @Mock
    ExcelGenerationService excelGenerationService;
    @Mock
    CreateMultiExcelDataService createMultiExcelDataService;
    ExcelFile excelFile = new ExcelFile();



    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService, excelRepository,
                createExcelDataService,excelGenerationService,createMultiExcelDataService));
    }

    @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
    public void setUpFile() {
        ExcelData data = new ExcelData();
        data.setTitle("Test book");
        data.setGeneratedTime(LocalDateTime.now());

        var sheets = new ArrayList<ExcelDataSheet>();
        var sheet = new ExcelDataSheet();
        sheet.setTitle("Sheet1");

        var headersS1 = new ArrayList<ExcelDataHeader>();
        ExcelDataHeader header1 = new ExcelDataHeader();
        header1.setName("Name");
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
        row1.add("Teresa");
        row1.add("5");
        List<Object> row2 = new ArrayList<>();
        row2.add("Daniel");
        row2.add("1");
        dataRows.add(row1);
        dataRows.add(row2);

        sheet.setDataRows(dataRows);
        sheet.setHeaders(headersS1);
        sheets.add(sheet);
        data.setSheets(sheets);

        excelFile.setFileId("123abcd");
        excelFile.setExcelData(data);
    }

    @Test
    public void testFileDownload() throws FileNotFoundException {
        Optional<ExcelFile> optional = Optional.of(excelFile);
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("File_1.xlsx"));
        Mockito.when(excelRepository.getFileById(anyString())).thenReturn(optional);
        given().accept("application/json").get("/excel/123abcd/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testListFiles(){
        List<ExcelFile> excelFiles  = new ArrayList<>();
        excelFiles.add(excelFile);
        Mockito.when(excelRepository.getFiles()).thenReturn(excelFiles);
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void createExcel() throws IOException {
        Mockito.when(createExcelDataService.createExcelData(ArgumentMatchers.any(ExcelRequest.class))).thenReturn(excelFile);
        Mockito.when(excelGenerationService.generateExcelReport(excelFile.getExcelData())).thenReturn(null);
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void createMultiExcel() throws IOException {
        Mockito.when(createMultiExcelDataService.createMultiExcelData(ArgumentMatchers.any(MultiSheetExcelRequest.class))).thenReturn(excelFile);
        Mockito.when(excelGenerationService.generateExcelReport(excelFile.getExcelData())).thenReturn(null);
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\",\"Class\"], \"data\":[[\"Teresa\",\"5\",\"A\"],[\"Daniel\",\"1\",\"B\"]],\"splitBy\":\"Class\"}").post("/excel/auto").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void deleteExcel(){
        Mockito.when(excelRepository.deleteFile("123abcd")).thenReturn(excelFile);
        given().accept("application/json").delete("/excel/123abcd").peek().
                then().assertThat()
                .statusCode(200);
    }
}
