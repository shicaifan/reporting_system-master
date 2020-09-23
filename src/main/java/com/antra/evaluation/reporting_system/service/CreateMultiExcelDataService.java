package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

public interface CreateMultiExcelDataService {
    ExcelFile createMultiExcelData(MultiSheetExcelRequest request);
}
