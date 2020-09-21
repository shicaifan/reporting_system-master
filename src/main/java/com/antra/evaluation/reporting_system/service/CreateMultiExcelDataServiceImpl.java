package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.repo.ExcelRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
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
    private static int incrementalSheetName = 1;

    @Override
    public ExcelData createMultiExcelData(@RequestBody MultiSheetExcelRequest request) throws IOException {
        List<String> headers = request.getHeaders();
        List<List<Object>> data = request.getData();
        String splitBy = request.getSplitBy();
        int indexOfsplitBy = 0;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equals(splitBy)) {
                indexOfsplitBy = i;
            }
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
        for(String header: headers){
            ExcelDataHeader excelDataHeader = new ExcelDataHeader();
            excelDataHeader.setName(header);
            excelDataHeader.setType(ExcelDataType.STRING);
            excelDataHeaders.add(excelDataHeader);
        }

        ExcelData excelData = new ExcelData();
        List<ExcelDataSheet> excelDataSheets = new ArrayList<>();
        for(Object obj: map.keySet()){
            ExcelDataSheet excelDataSheet = new ExcelDataSheet();
            excelDataSheet.setTitle("sheet_"+ incrementalSheetName++);
            excelDataSheet.setHeaders(excelDataHeaders);
            excelDataSheet.setDataRows(map.get(obj));
            excelDataSheets.add(excelDataSheet);
        }
        excelData.setTitle("File_"+incrementalFileTitle++);
        excelData.setGeneratedTime(LocalDateTime.now());
        excelData.setSheets(excelDataSheets);

        ExcelFile excelFile = new ExcelFile();
        excelFile.setExcelData(excelData);
        excelFile.setFileId(String.valueOf(incrementalFileId++));

        ExcelRepositoryImpl excelRepositoryImpl = new ExcelRepositoryImpl();
        excelRepositoryImpl.saveFile(excelFile);

        return excelData;
    }
}
