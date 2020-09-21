package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.repo.ExcelRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateExcelDataServiceImpl implements CreateExcelDataService {

    @Autowired
    ExcelRepository excelRepository;
    private static int incrementalFileTitle = 1;
    private static int incrementalFileId = 1;
    private static int incrementalSheetName = 1;
    @Override
    public ExcelData createExcelData(@RequestBody ExcelRequest request) throws IOException {
        List<String> headers = request.getHeaders();
        List<List<Object>> data = request.getData();
        System.out.println(headers+" "+data);
        List<ExcelDataHeader> excelDataHeaders = new ArrayList<>();
        for(String header: headers) {
            ExcelDataHeader excelDataHeader = new ExcelDataHeader();
            excelDataHeader.setName(header);
            excelDataHeader.setType(ExcelDataType.STRING);
            excelDataHeaders.add(excelDataHeader);
        }
        ExcelDataSheet sheet1 = new ExcelDataSheet();
        sheet1.setTitle("Sheet_" + incrementalSheetName++);
        sheet1.setHeaders(excelDataHeaders);
        sheet1.setDataRows(data);

        ExcelData excelData = new ExcelData();
        excelData.setTitle("File_"+incrementalFileTitle++);
        excelData.setGeneratedTime(LocalDateTime.now());

        List<ExcelDataSheet> excelDataSheets = new ArrayList<>();
        excelDataSheets.add(sheet1);
        excelData.setSheets(excelDataSheets);

        ExcelRepositoryImpl excelRepositoryImpl = new ExcelRepositoryImpl();
        ExcelFile excelFile = new ExcelFile();
        excelFile.setExcelData(excelData);
        excelFile.setFileId(String.valueOf(incrementalFileId++));
        excelRepositoryImpl.saveFile(excelFile);

        return excelData;
    }

}
