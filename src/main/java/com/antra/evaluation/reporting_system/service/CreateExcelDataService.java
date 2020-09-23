package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

public interface CreateExcelDataService {
    ExcelFile createExcelData(ExcelRequest request);
}
