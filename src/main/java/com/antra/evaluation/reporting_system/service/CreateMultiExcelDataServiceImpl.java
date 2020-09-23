package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.repo.ExcelRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CreateMultiExcelDataServiceImpl implements CreateMultiExcelDataService {

    @Autowired
    ExcelRepository excelRepository;
    private static int incrementalFileTitle = 1;
    private static int incrementalFileId = 1;


    private static final Logger log = LoggerFactory.getLogger(CreateMultiExcelDataServiceImpl.class);

    @Override
    public ExcelFile createMultiExcelData(MultiSheetExcelRequest request) {
        int incrementalSheetName = 1;
        List<String> headers = request.getHeaders();
        List<List<Object>> data = request.getData();
        String splitBy = request.getSplitBy();
        int indexOfsplitBy = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equals(splitBy)) {
                indexOfsplitBy = i;
            }
        }
        if(indexOfsplitBy==-1) {
            log.info("No splitBy header existed in the headers from the request!");
        }
        HashMap<Object, List<List<Object>>> map = new HashMap<>();
        for (int j = 0; j < data.size(); j++) {
            List<Object> objects = data.get(j);
            Object object = objects.get(indexOfsplitBy);
            if (map.containsKey(object)) {
                map.get(object).add(objects);
            } else {
                List<List<Object>> values = new ArrayList<>();
                values.add(objects);
                map.put(object, values);
            }
        }

        List<ExcelDataHeader> excelDataHeaders = new ArrayList<>();
        for (String header : headers) {
            ExcelDataHeader excelDataHeader = new ExcelDataHeader();
            excelDataHeader.setName(header);
            excelDataHeader.setType(ExcelDataType.STRING);
            excelDataHeaders.add(excelDataHeader);
        }
        log.info("Create Excel Data Header Success");

        ExcelData excelData = new ExcelData();
        List<ExcelDataSheet> excelDataSheets = new ArrayList<>();
        for(Object obj: map.keySet()){
            ExcelDataSheet excelDataSheet = new ExcelDataSheet();
            excelDataSheet.setTitle("sheet"+incrementalSheetName++);
            excelDataSheet.setHeaders(excelDataHeaders);
            excelDataSheet.setDataRows(map.get(obj));
            excelDataSheets.add(excelDataSheet);
        }
        log.info("Create Multiple Excel Data Sheets Success");

        int mapSize = excelRepository.getMapSize()+1;
        excelData.setTitle("File_"+mapSize);
        excelData.setGeneratedTime(LocalDateTime.now());
        excelData.setSheets(excelDataSheets);
        log.info("Create Excel Data Success");

        ExcelFile excelFile = new ExcelFile();
        excelFile.setExcelData(excelData);
        excelFile.setFileId(String.valueOf(mapSize));

        ExcelRepositoryImpl excelRepositoryImpl = new ExcelRepositoryImpl();
        excelRepositoryImpl.saveFile(excelFile);
        log.info("Save File into Repository Successfully");

        return excelFile;
    }
}
