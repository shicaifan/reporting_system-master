package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class APITest {
    @Mock
    ExcelService excelService;
    @Mock
    ExcelRepository excelRepository;

    ExcelFile excelFile = new ExcelFile();


    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService, excelRepository));
    }

    @Test
    public void testFileDownload() throws FileNotFoundException {
        excelFile.setFileId("123abcd");
        Optional<ExcelFile> optional = Optional.of(excelFile);
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("File_1.xlsx"));
        Mockito.when(excelRepository.getFileById(anyString())).thenReturn(optional);
        given().accept("application/json").get("/excel/123abcd/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testListFiles(){
        excelFile.setFileId("123abcd");
        List<ExcelFile> excelFiles  = new ArrayList<>();
        excelFiles.add(excelFile);
        Mockito.when(excelRepository.getFiles()).thenReturn(excelFiles);
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void createExcel() {
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }
    @Test
    public void createMultiExcel() {
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\",\"Class\"], \"data\":[[\"Teresa\",\"5\",\"A\"],[\"Daniel\",\"1\",\"B\"]],\"splitBy\":[\"Class\"]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void deleteExcel(){
        excelFile.setFileId("123abcd");
        Mockito.when(excelRepository.deleteFile("123abcd")).thenReturn(excelFile);
        given().accept("application/json").delete("/excel/123abcd").peek().
                then().assertThat()
                .statusCode(200);
    }
}
