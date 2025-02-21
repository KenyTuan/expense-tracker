package com.test.service;

import com.test.entity.Expense;
import com.test.enums.Category;

import java.io.IOException;
import java.util.List;

public interface ExpenseService {
    void save() throws IOException;

    Expense addExpense(String description, int amount, Category category);

    Expense updateExpense(long id, String description, int amount, Category category);

    void deleteExpense(long id);

    List<Expense> getAll();

    List<Expense> getExpensesByCategory(Category category);

    String getTotalExpenses();

    String getTotalExpensesByMonth(int month);

    void exportToCSV();

    void setBudget(int month,int amount) throws IOException;
}
