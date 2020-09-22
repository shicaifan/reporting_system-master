package com.antra.evaluation.reporting_system.service;


import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface CreateMultiExcelDataService {
    ExcelData createMultiExcelData(@RequestBody @Validated MultiSheetExcelRequest request) throws IOException;
}
