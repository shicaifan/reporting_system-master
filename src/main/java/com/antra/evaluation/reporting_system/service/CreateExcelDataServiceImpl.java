package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service
public class CreateExcelDataServiceImpl implements CreateExcelDataService {

    @Autowired
    ExcelRepository excelRepository;

    public CreateExcelDataServiceImpl(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }
    public CreateExcelDataServiceImpl(){}

    private static final Logger log = LoggerFactory.getLogger(CreateExcelDataServiceImpl.class);
    @Override
    public ExcelFile createExcelData(ExcelRequest request){
        List<String> headers = request.getHeaders();
        List<List<Object>> data = request.getData();
        List<ExcelDataHeader> excelDataHeaders = new ArrayList<>();
        for (String header : headers) {
            ExcelDataHeader excelDataHeader = new ExcelDataHeader();
            excelDataHeader.setName(header);
            excelDataHeader.setType(ExcelDataType.STRING);
            excelDataHeaders.add(excelDataHeader);
        }
        log.info("Create Excel Headers Success");
        ExcelDataSheet sheet1 = new ExcelDataSheet();
        sheet1.setTitle("Sheet");
        sheet1.setHeaders(excelDataHeaders);
        sheet1.setDataRows(data);
        log.info("Create Excel Data Sheet Success");

        int mapSize = excelRepository.getMapSize()+1;
        ExcelData excelData = new ExcelData();
        excelData.setTitle("File_"+ mapSize);
        excelData.setGeneratedTime(LocalDateTime.now());

        List<ExcelDataSheet> excelDataSheets = new ArrayList<>();
        excelDataSheets.add(sheet1);
        excelData.setSheets(excelDataSheets);
        log.info("Create Excel Data Success");


        ExcelFile excelFile = new ExcelFile();
        excelFile.setExcelData(excelData);
        excelFile.setFileId(String.valueOf(mapSize));
        excelRepository.saveFile(excelFile);
        log.info("Save File to Repository Success");

        return excelFile;
    }

}
