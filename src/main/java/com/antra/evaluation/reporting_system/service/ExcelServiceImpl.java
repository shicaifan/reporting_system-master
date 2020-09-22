package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;

    @Override
    public InputStream getExcelBodyById(String id) {

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1);

        if (fileInfo.isPresent()) {
            File file = new File(fileLocation + fileInfo.get().getExcelData().getTitle()+ ".xlsx");//open file according to the filename
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
