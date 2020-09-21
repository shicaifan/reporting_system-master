package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExcelRepositoryImpl implements ExcelRepository {

    static Map<String, ExcelFile> excelData = new ConcurrentHashMap<>();

    @Override
    public Optional<ExcelFile> getFileById(String id) {
        return Optional.ofNullable(excelData.get(id));
    }

    @Override
    public ExcelFile saveFile(ExcelFile file) {
        excelData.put(file.getFileId(), file);
        return file;
    }

    @Override
    public ExcelFile deleteFile(String id) {
        if(excelData.containsKey(id)){
            ExcelFile file = excelData.get(id);
            excelData.remove(id);
            return file;
        }
        return null;
    }

    @Override
    public List<ExcelFile> getFiles() {
        List<ExcelFile> res = new ArrayList<>();
        for(String key: excelData.keySet()){
            ExcelFile excelFiles = excelData.get(key);
            res.add(excelFiles);
        }
        return res;
    }
}

