package com.askrida.web.service.service;

import com.askrida.web.service.exception.ResourceNotFoundException;
import com.askrida.web.service.model.ExpenseItem;
import com.askrida.web.service.model.ExpenseReport;
import com.askrida.web.service.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Business logic untuk modul Expense.
 * Sebelumnya tersebar di ExpenseController.
 */
@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<ExpenseReport> getAllReports() {
        return expenseRepository.getAllReports();
    }

    public ExpenseReport getReportById(int id) {
        ExpenseReport r = expenseRepository.getReportById(id);
        if (r == null) throw new ResourceNotFoundException("ExpenseReport", id);
        return r;
    }

    @Transactional
    public void addReport(ExpenseReport report) throws Exception {
        expenseRepository.addReport(report);
    }

    @Transactional
    public void updateReport(ExpenseReport report) throws Exception {
        getReportById(report.getIdReport()); // throws 404 if not found
        expenseRepository.updateReport(report);
    }

    @Transactional
    public void deleteReport(int id) throws Exception {
        getReportById(id); // throws 404 if not found
        expenseRepository.deleteReport(id);
    }

    public int countReports()                     { return expenseRepository.countReports(); }
    public int countByStatus(String status)       { return expenseRepository.countByStatus(status); }
    public BigDecimal getTotalAllExpenses()        { return expenseRepository.getTotalAllExpenses(); }

    public List<ExpenseItem> getItemsByReport(int reportId) {
        return expenseRepository.getItemsByReport(reportId);
    }

    @Transactional
    public void addItem(ExpenseItem item) throws Exception    { expenseRepository.addItem(item); }

    @Transactional
    public void updateItem(ExpenseItem item) throws Exception { expenseRepository.updateItem(item); }

    @Transactional
    public void deleteItem(int id) throws Exception           { expenseRepository.deleteItem(id); }

    public List<Map<String, Object>> getSummaryByCategory(int reportId) {
        return expenseRepository.getSummaryByCategory(reportId);
    }
}
